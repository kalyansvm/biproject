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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoQuery.java 8408 2007-05-29 23:29:12Z melih $
 * 
 * @hibernate.joined-subclass table="JSQuery"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoQuery extends RepoResource
{
	
	private RepoResource dataSource = null;
	private String language;
	private String sql;


	/**
	 * @hibernate.many-to-one
	 * 		column="reportDataSource"
	 */
	public RepoResource getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * 
	 */
	public void setDataSource(RepoResource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	/**
	 * @hibernate.property
	 * 		column="sql_query" type="string" length="2000" not-null="true"
	 */
	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}

	protected Class getClientItf() {
		return Query.class;
	}


	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		
		Query query = (Query) clientRes;
		copyDataSource(referenceResolver, query);
		setLanguage(query.getLanguage());
		setSql(query.getSql());
	}


	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		Query query = (Query) clientRes;
		query.setDataSource(getClientReference(getDataSource(), resourceFactory));
		query.setLanguage(getLanguage());
		query.setSql(getSql());
	}

	private void copyDataSource(ReferenceResolver referenceResolver, Query query) {
		ResourceReference ds = query.getDataSource();
		if (ds != null) {
			RepoResource repoDS = getReference(ds, RepoReportDataSource.class, referenceResolver);
			if (repoDS != null && !(repoDS instanceof RepoReportDataSource)) {
				throw new JSException("jsexception.query.datasource.has.an.invalid.type", new Object[] {repoDS.getClass().getName()});
			}
			setDataSource(repoDS);
		} else {
			setDataSource(null);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
