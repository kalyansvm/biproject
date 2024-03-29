/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.Filter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.metadata.view.domain.ParentFolderFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.PropertyFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.ReferenceFilter;
import com.jaspersoft.jasperserver.api.metadata.view.domain.URIFilter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateFilter.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateFilter implements Filter {

	public final Junction junction;
	private final ReferenceResolver referenceResolver;
	
	private Criterion criterion;
	
	public HibernateFilter(Junction junction, ReferenceResolver referenceResolver) {
		this.junction = junction;
		this.referenceResolver = referenceResolver;
	}

	protected final Criterion last() {
		return criterion;
	}
	
	protected void add(Criterion newCriterion) {
		this.criterion = newCriterion;
		if (junction != null) {
			junction.add(newCriterion);
		}
	}
	
	public void applyParentFolderFilter(ParentFolderFilter filter) {
		add(parentFolderRestriction(filter.getFolderURI()));
	}

	protected SimpleExpression parentFolderRestriction(String folderURI) {
		return Restrictions.eq("parent.URI", folderURI);
	}

	public void applyPropertyFilter(PropertyFilter filter) {
		Criterion propCriterion;
		switch (filter.getOp()) {
		case PropertyFilter.EQ:
			propCriterion = Restrictions.eq(filter.getProperty(), filter.getValue());
			break;
		case PropertyFilter.LIKE:
			propCriterion = Restrictions.like(filter.getProperty(), filter.getValue());
			break;
		case PropertyFilter.GT:
			propCriterion = Restrictions.gt(filter.getProperty(), filter.getValue());
			break;
		case PropertyFilter.LT:
			propCriterion = Restrictions.lt(filter.getProperty(), filter.getValue());
			break;
		case PropertyFilter.BETWEEN:
			propCriterion = Restrictions.between(filter.getProperty(), filter.getLowValue(), filter.getHighValue());
			break;
		default:
			throw new JSException("jsexception.hibernate.unknown.property.filter.operation", new Object[] {new Byte(filter.getOp())});
		}
		add(propCriterion);
	}

	public void applyNegatedFilter(FilterElement element) {
		HibernateFilter notFilter = new HibernateFilter(null, referenceResolver);
		element.apply(notFilter);
		add(Restrictions.not(notFilter.last()));
	}

	public void applyConjunction(List filterElements) {
		Conjunction conjunction = Restrictions.conjunction();
		applyJunction(filterElements, conjunction);
	}

	public void applyDisjunction(List filterElements) {
		Disjunction disjunction = Restrictions.disjunction();
		applyJunction(filterElements, disjunction);
	}

	public void applyOr(FilterElement lhs, FilterElement rhs) {
		HibernateFilter lhsFilter = new HibernateFilter(null, referenceResolver);
		lhs.apply(lhsFilter);
		HibernateFilter rhsFilter = new HibernateFilter(null, referenceResolver);
		rhs.apply(rhsFilter);
		LogicalExpression or = Restrictions.or(lhsFilter.last(), rhsFilter.last());
		add(or);
	}

	protected void applyJunction(List filterElements, Junction subJunction) {
		HibernateFilter junctionFilter = new HibernateFilter(subJunction, referenceResolver);
		for (Iterator it = filterElements.iterator(); it.hasNext();) {
			FilterElement filterElement = (FilterElement) it.next();
			filterElement.apply(junctionFilter);
		}
		add(subJunction);
	}

	public void applyReferenceFilter(ReferenceFilter filter) {
		RepoResource persistentRef = referenceResolver.getPersistentReference(filter.getReferredURI(), filter.getReferenceClass());
		Criterion propCriterion = Restrictions.eq(filter.getProperty(), persistentRef);
		add(propCriterion);
	}

	public void applyURIFilter(URIFilter filter) {
		String uri = filter.getURI();
		int lastSep = uri.lastIndexOf(Folder.SEPARATOR);
		if (lastSep <= 0) {
			throw new JSException("Invalid resource URI " + uri);
		}
		
		String folder = uri.substring(0, lastSep);
		String name = uri.substring(lastSep + Folder.SEPARATOR_LENGTH);
		
		add(Restrictions.conjunction()
				.add(parentFolderRestriction(folder))
				.add(Restrictions.eq("name", name)));
	}

}
