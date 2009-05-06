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

import java.util.TimeZone;
import java.util.Map;

import net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuter;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRTimezoneJdbcQueryExecuterFactory extends JRJdbcQueryExecuterFactory
{

	public static final String PARAMETER_TIMEZONE = "DATABASE_TIMEZONE";

	private final static Object[] BUILTIN_PARAMETERS = {PARAMETER_TIMEZONE,  TimeZone.class};

	public Object[] getBuiltinParameters()
	{
		return BUILTIN_PARAMETERS;
	}

	public JRQueryExecuter createQueryExecuter(JRDataset dataset, Map parameters) throws JRException
	{
		return new JRTimezoneJdbcQueryExecuter(dataset, parameters);
	}

}
