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

import java.sql.ResultSet;
import java.util.Date;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRDataSource;
import org.quartz.TriggerUtils;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRTimezoneResultSetDataSource implements JRDataSource
{
	private TimeZone timezone;
	private JRDataSource dataSource;


	public JRTimezoneResultSetDataSource(JRDataSource dataSource, TimeZone timezone)
	{
		this.dataSource = dataSource;
		this.timezone = timezone;
	}

	public boolean next() throws JRException
	{
		boolean hasNext = false;
		if (dataSource != null) {
			try {
				hasNext = dataSource.next();
			} catch (JRException e) {
				throw new JRException("Unable to get next record.", e);
			}
		}

		return hasNext;
	}

	public Object getFieldValue(JRField field) throws JRException
	{
		Object value = null;
		if (field != null && dataSource != null) {
			value = dataSource.getFieldValue(field);

			if (value instanceof Date && timezone != null) {
				Date initialDate = (Date) value;
				Date date = TriggerUtils.translateTime(initialDate, TimeZone.getTimeZone("GMT"), timezone);
				initialDate.setTime(date.getTime());
				return initialDate;
			}

			return value;
		}

		return value;
	}
}
