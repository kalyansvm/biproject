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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BeanPropertyTextAppender.java 8408 2007-05-29 23:29:12Z melih $
 */
public class BeanPropertyTextAppender extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyTextAppender.class);

	private String appended;
	
	protected Object getProcessedPropertyValue(Object originalValue) {
		String appendedValue;
		if (originalValue == null) {
			appendedValue = getAppended();
		} else {
			if (!(originalValue instanceof String)) {
				throw new JSException("jsexception.property.not.a.text", new Object[] {getPropertyName(), getBeanName()});
			}
			appendedValue = originalValue + getAppended();
		}
		
		if (log.isInfoEnabled()) {
			log.info("Appending " + getBeanName() + "." + getPropertyName() + " with " + getAppended());
		}
		
		return appendedValue;
	}

	public String getAppended() {
		return appended;
	}

	public void setAppended(String appendedText) {
		this.appended = appendedText;
	}

}
