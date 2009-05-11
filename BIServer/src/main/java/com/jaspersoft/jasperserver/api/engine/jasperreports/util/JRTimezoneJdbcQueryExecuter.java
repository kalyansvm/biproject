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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;

import org.quartz.TriggerUtils;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRTimezoneJdbcQueryExecuter extends JRJdbcQueryExecuter {
	
	protected static class InheritableValue {
		private final Object value;
		private final boolean inherited;
		
		public InheritableValue(Object value, boolean inherited) {
			this.value = value;
			this.inherited = inherited;
		}

		public Object getValue() {
			return value;
		}

		public boolean isInherited() {
			return inherited;
		}
	}
	
	protected static class InheritableFlaggedThreadLocal extends InheritableThreadLocal {
		protected Object childValue(Object parentValue) {
			InheritableValue child;
			if (parentValue == null) {
				child = null;
			} else {
				InheritableValue parent = (InheritableValue) parentValue;
				child = new InheritableValue(parent.getValue(), true);
			}
			return child;
		}

		public Object get() {
			InheritableValue value = (InheritableValue) super.get();
			return value == null ? null : value.getValue();
		}

		public void set(Object value) {
			InheritableValue inheritableValue = new InheritableValue(value, false);
			super.set(inheritableValue);
		}
		
		public boolean isInherited() {
			InheritableValue value = (InheritableValue) super.get();
			return value == null ? false : value.isInherited();
		}
	}
	
	private static InheritableFlaggedThreadLocal parentTimezone = new InheritableFlaggedThreadLocal();
	
	private final TimeZone timezone;
	private final boolean timezoneSet;
	private final Set adjustedParameters = new HashSet();
	
	public JRTimezoneJdbcQueryExecuter(JRDataset dataset, Map map) {
		super(dataset, map);

		TimeZone timezoneParam = (TimeZone) getValueParameter(JRTimezoneJdbcQueryExecuterFactory.PARAMETER_TIMEZONE).getValue();
		if (timezoneParam == null && parentTimezone.isInherited()) {
			timezoneSet = false;
			timezoneParam = (TimeZone) parentTimezone.get();
		} else {
			timezoneSet = true;
			parentTimezone.set(timezoneParam);
		}
		timezone = timezoneParam;
	}


	protected JRValueParameter getValueParameter(String parameterName) {
		JRValueParameter param = super.getValueParameter(parameterName);

		if (param.getValue() instanceof Date
				&& timezone != null
				&& adjustedParameters.add(param.getName())) {
			Date initialDate = (Date) param.getValue();
			Date date = TriggerUtils.translateTime(initialDate, timezone, TimeZone.getTimeZone("GMT"));
			initialDate.setTime(date.getTime());
		}
		return param;
	}

	public JRDataSource createDatasource() throws JRException {
		JRDataSource dataSource = super.createDatasource();
		return new JRTimezoneResultSetDataSource(dataSource, timezone);
	}
	
	public synchronized void close() {
		if (timezoneSet) {
			parentTimezone.remove();
		}

		super.close();
	}

}
