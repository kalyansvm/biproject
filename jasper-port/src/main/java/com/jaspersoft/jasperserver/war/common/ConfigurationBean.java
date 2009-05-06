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
package com.jaspersoft.jasperserver.war.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class ConfigurationBean
{
	private MessageSource messages;

	private int paginatorItemsPerPage;
	private int paginatorPagesRange;
	private boolean reportLevelConfigurable;
	
	private boolean paginationForSinglePageReport;
	
	private String calendarInputJsp;

	/**
	 * @return Returns the reportLevelConfigurable.
	 */
	public boolean isReportLevelConfigurable() {
		return reportLevelConfigurable;
	}

	/**
	 * @param reportLevelConfigurable The reportLevelConfigurable to set.
	 */
	public void setReportLevelConfigurable(boolean reportLevelConfigurable) {
		this.reportLevelConfigurable = reportLevelConfigurable;
	}

	public Map getAllFileResourceTypes() {
		Map allTypes = new LinkedHashMap();
		allTypes.put(FileResource.TYPE_IMAGE,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_IMAGE, null, "Image", LocaleContextHolder.getLocale()));
		allTypes.put(FileResource.TYPE_FONT,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_FONT, null, "Font", LocaleContextHolder.getLocale()));
		allTypes.put(FileResource.TYPE_JAR,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_CLASS_JAR, null, "Jar", LocaleContextHolder.getLocale()));
		allTypes.put(FileResource.TYPE_JRXML,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_SUB_REPORT, null, "Jrxml", LocaleContextHolder.getLocale()));
		allTypes.put(FileResource.TYPE_RESOURCE_BUNDLE,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_RESOURCE_BUNDLE, null, "Properties", LocaleContextHolder.getLocale()));
		allTypes.put(FileResource.TYPE_STYLE_TEMPLATE,
				 messages.getMessage(JasperServerConst.TYPE_RSRC_STYLE_TEMPLATE, null, "Style Template", LocaleContextHolder.getLocale()));
		allTypes.put(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_OLAP_SCHEMA, null, "OLAP Schema", LocaleContextHolder.getLocale()));
		allTypes.put(ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA,
					 messages.getMessage(JasperServerConst.TYPE_RSRC_ACCESS_GRANT_SCHEMA, null, "Access Grant Schema", LocaleContextHolder.getLocale())); // pro-only
		return allTypes;
	}

	public static class DataSourceType {
		private final Class type;
		private final String typeValue;
		private final String labelMessage;
		
		public DataSourceType(final Class type, final String typeValue, final String labelMessage) {
			this.type = type;
			this.typeValue = typeValue;
			this.labelMessage = labelMessage;
		}

		public String getLabelMessage() {
			return labelMessage;
		}

		public Class getType() {
			return type;
		}

		public String getTypeValue() {
			return typeValue;
		}
	}
	
	private final static DataSourceType[] DATA_SOURCE_TYPES = new DataSourceType[] {
		new DataSourceType(JdbcReportDataSource.class, JasperServerConstImpl.getJDBCDatasourceType(), "dataSource.jdbc"),
		new DataSourceType(JndiJdbcReportDataSource.class, JasperServerConstImpl.getJNDIDatasourceType(), "dataSource.jndi"),
		new DataSourceType(BeanReportDataSource.class, JasperServerConstImpl.getBeanDatasourceType(), "dataSource.bean"),
	};
	
	public List getDataSourceTypes() {
		ArrayList types = new ArrayList(DATA_SOURCE_TYPES.length);
		for (int i = 0; i < DATA_SOURCE_TYPES.length; i++) {
			types.add(DATA_SOURCE_TYPES[i]);
		}
		return types;
	}

	public int getPaginatorItemsPerPage()
	{
		return paginatorItemsPerPage;
	}

	public void setPaginatorItemsPerPage(int paginatorItemsPerPage)
	{
		this.paginatorItemsPerPage = paginatorItemsPerPage;
	}

	public int getPaginatorPagesRange()
	{
		return paginatorPagesRange;
	}

	public void setPaginatorPagesRange(int paginatorPagesRange)
	{
		this.paginatorPagesRange = paginatorPagesRange;
	}


	public MessageSource getMessages()
	{
		return messages;
	}

	public void setMessages(MessageSource messages)
	{
		this.messages = messages;
	}

	public boolean isPaginationForSinglePageReport() {
		return paginationForSinglePageReport;
	}

	public void setPaginationForSinglePageReport(
			boolean paginationForSinglePageReport) {
		this.paginationForSinglePageReport = paginationForSinglePageReport;
	}

	public String getCalendarInputJsp() {
		return calendarInputJsp;
	}

	public void setCalendarInputJsp(String calendarInputJsp) {
		this.calendarInputJsp = calendarInputJsp;
	}
}
