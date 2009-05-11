/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BeanPropertyListAppender.java 9038 2007-07-10 00:22:55Z bob $
 */
public class BeanPropertyListAppender extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyListAppender.class);

	private Object appended;
	
	protected Object getProcessedPropertyValue(Object originalValue) {
		List newValue;
		if (originalValue == null) {
			newValue = new ArrayList();
		} else {
			if (!(originalValue instanceof List)) {
				throw new JSException("jsexception.property.not.a.list", new Object[] {getPropertyName(), getBeanName()});
			}
			newValue = (List) originalValue;
		}
		if (appended instanceof List) {
			newValue.addAll((List) appended);
			if (log.isInfoEnabled()) {
				log.info("Adding " + ((List) appended).size() + " entries to " + getBeanName() + "." + getPropertyName());
			}
		} else {
			newValue.add(appended);
			if (log.isInfoEnabled()) {
				log.info("Adding 1 entry to " + getBeanName() + "." + getPropertyName());
			}
		}
		return newValue;
	}

	public Object getAppended() {
		return appended;
	}

	public void setAppended(Object appended) {
		this.appended = appended;
	}

}
