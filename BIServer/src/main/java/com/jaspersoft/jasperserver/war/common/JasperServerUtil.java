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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;


import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author aztec
 * @version $Id: JasperServerUtil.java 13147 2008-04-23 21:21:04Z achan $
 */
public class JasperServerUtil {

	private static final Log log = LogFactory.getLog(JasperServerUtil.class);

	/*
     * method to return a DB-Connection to the
     * JasperServer Database
     */
    public static Connection getJSDatabaseConnection () throws ClassNotFoundException, SQLException, NamingException {
		boolean sqlExcpn = false;
		Connection con = null;
		try {
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource)cxt.lookup(JasperServerConstImpl.getJSDataSrc());
			con = ds.getConnection();
			return con;
		} catch(Exception _ex) {
			if (log.isErrorEnabled())
				log.error(_ex, _ex);
			sqlExcpn = true;
		} finally {
			if(sqlExcpn) {
				Class.forName(JasperServerConstImpl.getJSConnector());
				con = DriverManager.getConnection(JasperServerConstImpl.getJSUrl(),
					JasperServerConstImpl.getJSDbUser(),
					JasperServerConstImpl.getJSDbPasswd());
				return con;
			}
		}
		return con;
    }

    /*
     * method to export a Jasper Report to a Html type
     * arguments: JasperPrint, StringBuffer
     * retunrs: JRHtmlExporter
     */
    public static JRHtmlExporter exportJRToHtml(JasperPrint jasperPrint, StringBuffer reportContent) throws JRException {
        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, reportContent);
        //exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "../servlets/image?image=");
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRExporterParameter.PAGE_INDEX, new Integer(0));
        exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
        exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
        exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
        exporter.exportReport();
        return exporter;
    }

	/*
	 * method to parse-out the file name alone
	 * from the full path. this is an added method
	 * if the filename is returned with PATH info
	 */
	public String parseFileName(String fullName) {
		int lastIndex = fullName.lastIndexOf(System.getProperty("file.separator"));
		fullName = fullName.substring(lastIndex+1);
		return fullName;
    }

	/*
	 * method to update report unit fields
	 * argument: reportunitbean
	 * returns: boolean
	 * this method has 2b modfd as RU can itself be sent (to avoid URI being NULL)
	 */
	public static boolean createNewReportUnit(RepositoryService repository,
			ReportUnit unit) throws Exception {
		repository.saveResource(null, unit);
		return true;
	}

	/** Creates a new folder in the repository
	 * @param repository Instance of the Repository service
	 * @param folderName The Folder name to be created
	 * @param parentUri The parent uri fo the folder to be created
	 * @throws Exception Lets any thrown exceptions bubble
	 */
	 public static Folder createNewFolder(RepositoryService repository,
		String folderName,String parentUri) throws Exception {
		Folder folder=new FolderImpl();
		folder.setName(folderName);
		folder.setLabel(folderName);
		folder.setParentFolder(parentUri);
		repository.saveFolder(null,folder);
		return folder;
	}

	/*
	 * function to upload the files into repository
	 * arguments string
	 * returns boolean
	 */
	public static boolean uploadToRepository(ReportUnit rpunit, String fileName) throws Exception {

/*		OutputStream os = new FileOutputStream(JasperServerConstImpl.getJSReposPath() + fileName + JasperServerConst.FILE_JASPER_EXTN);
		JasperCompileManager.compileReportToStream(new ByteArrayInputStream(rpunit.getJrxml()), os);
		os.flush(); os.close();

		os = new FileOutputStream(JasperServerConstImpl.getJSReposPath() + fileName + JasperServerConst.FILE_JRXML_EXTN);
		os.write(rpunit.getJrxml());
		os.flush(); os.close();
*/
		return true;
	}

	private static final Pattern PATTERN_NAME = Pattern.compile("(\\p{L}|\\p{N}|(\\_)|(\\.)|(\\-)|[;@])+");

	/*
	 * function to validate name
	 * allows only valid word characters and doesn't allow
	 * any space or any special characters for this field
	 * arguments string
	 * returns boolean
	 */
	 public static boolean regExValidateName(String inp) throws PatternSyntaxException {
		 Matcher mat = PATTERN_NAME.matcher(inp.trim());
		 return mat.matches();
	 }

	/*
	 * function to validate label
	 * allows only valid word characters and doesn't allow
	 * any special characters for this field (allows space)
	 * arguments string
	 * returns boolean
	 */
	 public static boolean regExValidateLabel(String inp) throws PatternSyntaxException {

/*		 Pattern pat = Pattern.compile("([a-zA-Z]+([a-zA-Z0-9])*(((\\s)|(\\_)){1}[a-zA-Z0-9]+)*)");
		 Matcher mat = pat.matcher(inp.trim());
		 if(mat.matches())
		 	return true;
		 else
		 	return false;
*/	 
		 //TODO validate?
		 return true;
	 }

	/*
	 * function to validate emailaddress
	 * @param string
	 * @return boolean
	 */
	 public static boolean regExValidateEmail(String email) {
		try {
			new InternetAddress(email.trim(), true);
			if (email.trim().length() > 0) {
				Pattern p = Pattern.compile("(\\w+)(\\.\\w+)*@(\\w+\\.)(\\w+)(\\.\\w+)*");
				Matcher m = p.matcher(email);
				return m.matches();
			}
			return true;
		} catch (AddressException e) {
			if (log.isDebugEnabled()) {
				log.debug("Email address \"" + email + "\" not valid");
			}
			
			return false;
		}
	 }

	/*
	 * function to validate folderNames
	 * @param
	 * @return
	 * assumption: new folders are created under / - root directory
	 */
	public static boolean regExValidateFolder(String folderName) throws PatternSyntaxException {
		return regExValidateName(folderName.trim());
	}


	/*
	 * function to validate jndi Service Name
	 * @param
	 * @return
	 * assumption: pattern is 'jndiName' or 'jdbc:jndiName' or 'jdbc/jndiName'
	 */
	public static boolean regExValidateJndiServiceName(String jndiName) throws PatternSyntaxException {
		 Pattern pat = Pattern.compile("([a-zA-Z]+(((\\:)|(\\/)){1}[a-zA-Z0-9]+)*)");
		 Matcher mat = pat.matcher(jndiName.trim());
		 if(mat.matches())
		 	return true;
		 else
		 	return false;
	}

	/*
	 * function to validate jndi Service Name
	 * @param
	 * @return
	 * assumption: pattern is 'jndiName' or 'jdbc:jndiName' or 'jdbc/jndiName'
	 */
	public static boolean regExValidateDbDriver(String driverName) throws PatternSyntaxException {
		 Pattern pat = Pattern.compile("([a-zA-Z]+((\\.){1}[a-zA-Z0-9]+)*)");
		 Matcher mat = pat.matcher(driverName.trim());
		 if(mat.matches())
		 	return true;
		 else
		 	return false;
	}

	/*
	 * function to validate JDBC URL
	 * @param
	 * @return
	 * assumption: pattern is strictly jdbc:dbname://(com.jasper.jsoft)(192.168.192.29(9))(:)(port#)/dbname - MYSQL/HSQLDB
	 * assumption: pattern is strictly jdbc:oracle:thin:@(com.jasper.jsoft)(192.168.192.29(9))(:)(port#)/dbname - ORACLE
	 */
	public static boolean regExValidateJdbcURL(String jdbcUrl) throws PatternSyntaxException {

		//allow user to enter any free-text
		return true;

	/*
		Pattern pat = Pattern.compile("(jdbc:{1}([a-zA-Z]{3,}:){1,}(((\\/)(\\/))|(\\@)){1}(([0-9]{2,3}((\\.){1}[0-9]{2,3}){3}){1}|([a-zA-Z]+[a-zA-Z0-9]*((\\.){1}[a-zA-Z]+[a-zA-Z0-9]*)*){1}){1}((\\:){1}[0-9]{2,4})*((\\/)|(\\:)){1}[a-zA-Z]+[a-zA-Z0-9]*)");
		Matcher mat = pat.matcher(jdbcUrl.trim());
		if(mat.matches())
			return true;
		else
		 	return false;
	*/

	}

	/*
	 * function to validate REPORT NAME
	 * @param
	 * @return
	 */
	public static boolean regExValidateReportName(String reportName) throws PatternSyntaxException {

		 Pattern pat = Pattern.compile("[a-zA-Z0-9]+((\\_){1}[a-zA-Z0-9]+)*");
		 Matcher mat = pat.matcher(reportName.trim());
		 if(mat.matches())
		 	return true;
		 else
		 	return false;
	}

	public static ExecutionContext getExecutionContext(HttpServletRequest request) {
		 return getExecutionContext(RequestContextUtils.getLocale(request));
	 }

	 public static ExecutionContext getExecutionContext() {
		 return getExecutionContext(LocaleContextHolder.getLocale());
	 }

	 public static ExecutionContext getExecutionContext(Locale locale) {
		 ExecutionContextImpl context = new ExecutionContextImpl();
		 context.setLocale(locale);
		 return context;
	 }

	public static ExecutionContext getExecutionContext(Locale locale, TimeZone timeZone) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.setLocale(locale);
		context.setTimeZone(timeZone);
		return context;
	}

		public static ExecutionContext getExecutionContext(RequestContext context) {
			TimeZone timeZone = getTimezone(context);
			return getExecutionContext(LocaleContextHolder.getLocale(), timeZone);
		}

		public static TimeZone getTimezone(RequestContext context) {
			String timeZoneId = (String) context.getExternalContext().getSessionMap().get(JasperServerConstImpl.getUserTimezoneSessionAttr());
			return getTimezone(timeZoneId);
		}

        public static TimeZone getTimezone(ExecutionContext exContext) {
            return exContext.getTimeZone();
        }        
        
		protected static TimeZone getTimezone(String timeZoneId) {
			TimeZone timeZone;
			if (timeZoneId == null)
			{
				timeZone = TimeZone.getDefault();
			}
			else
			{
				timeZone = TimeZone.getTimeZone(timeZoneId);
			}
			return timeZone;
		}

		public static TimeZone getTimezone(HttpServletRequest request) {
			String timeZoneId = (String) request.getSession().getAttribute(JasperServerConstImpl.getUserTimezoneSessionAttr());
			return getTimezone(timeZoneId);
		}

	 /*
	  * function to trim the fields of objects
	  * before final save into the Repository
	  * @param
	  * @return
	  */
	 public static void trimDTOFieldSpaces(Object object) {

		 if(object instanceof User) {
			 User user = (User)object;
			 user.setUsername(user.getUsername().trim());
			 user.setFullName(user.getFullName().trim());
			 user.setEmailAddress(user.getEmailAddress().trim());
		 } else if(object instanceof Role) {
			 Role role = (Role)object;
			 role.setRoleName(role.getRoleName().trim());
		 } else {
			 //code can handle similar DTO objects
		 }

	 }

	public static DateFormat createCalendarDateFormat(MessageSource messages, Locale locale) {
		String pattern = messages.getMessage("date.format", null, locale);
		return new SimpleDateFormat(pattern);
	}

	public static DateFormat createCalendarDateFormat(MessageSource messages) {
		return createCalendarDateFormat(messages, LocaleContextHolder.getLocale());
	}

	public static DateFormat createCalendarDateTimeFormat(MessageSource messages, Locale locale) {
		String pattern = messages.getMessage("datetime.format", null, locale);
		return new SimpleDateFormat(pattern);
	}

	public static DateFormat createCalendarDateTimeFormat(MessageSource messages) {
		return createCalendarDateTimeFormat(messages, LocaleContextHolder.getLocale());
	}

	public static String formatDate(MessageSource messages, Date date, TimeZone timeZone)
	{
		DateFormat format = createCalendarDateTimeFormat(messages);
		format.setTimeZone(timeZone);
		return format.format(date);
	}

    public static HttpServletRequest getServletRequestFromRequestContext(RequestContext context) {
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext instanceof ServletExternalContext) {
            return ((ServletExternalContext)externalContext).getRequest();
        } else {
            //wrong kind of context
            return null;
        }
    }     
    
}
