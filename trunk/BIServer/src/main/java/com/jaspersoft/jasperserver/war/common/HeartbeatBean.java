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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatBean implements ServletContextAware 
{
	private static final Log log = LogFactory.getLog(HeartbeatBean.class);
	
	private ServletContext servletContext = null;
	private DataSource dataSource = null;

	private boolean enabled = false;
	private boolean askForPermission = false;
	private String url = null;
	
	private String localId = null;
	private String heartbeatId = null;
	private int callCount = 0;
	private boolean askForPermissionReplied = false;

	private String osName = null;
	private String osVersion = null;
	private String javaVendor = null;
	private String javaVersion = null;
	private String serverInfo = null;
	private String productName = null;
	private String productVersion = null;
	private String location = null;
	private String dbName = null;
	private String dbVersion = null;
	

	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public boolean getEnabled() 
	{
		return enabled;
	}

	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}

	public boolean getAskForPermission() 
	{
		return askForPermission;
	}

	public void setAskForPermission(boolean askForPermission) 
	{
		this.askForPermission = askForPermission;
	}

	public void setUrl(String url) 
	{
		this.url = url;
	}

	public void setProductName(String productName) 
	{
		this.productName = productName;
	}

	public void setProductVersion(String productVersion) 
	{
		this.productVersion = productVersion;
	}

	public void call() 
	{
		if (enabled)
		{
			boolean toCall = false;
			Properties localIdProperties = loadLocalIdProperties();
			if (localIdProperties == null)
			{
				if (askForPermission)
				{
					toCall = false;
					if (log.isDebugEnabled())
						log.debug("Heartbeat is ENABLED, but permission was NOT YET GRANTED on first login.");
				}
				else
				{
					toCall = true;
					if (log.isDebugEnabled())
						log.debug("Heartbeat is ENABLED and does not ask for permission on first login.");
				}
			}
			else
			{
				boolean permissionGranted = Boolean.valueOf(localIdProperties.getProperty("permission.granted")).booleanValue();
				if (permissionGranted)
				{
					toCall = true;
					if (log.isDebugEnabled())
						log.debug("Heartbeat is ENABLED and permission to call was GRANTED.");
				}
				else
				{
					toCall = false;
					if (log.isDebugEnabled())
						log.debug("Heartbeat is ENABLED, but permission to call was NOT GRANTED.");
				}
			}

			if (toCall)
			{
				if (localIdProperties == null)
				{
					localIdProperties = createLocalIdProperties(true);
				}
				
				httpCall(localIdProperties, url);
			}
			callCount++;
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("Heartbeat is DISABLED.");
		}
	}
	
	public void permitCall(boolean isCallPermitted) 
	{
		askForPermissionReplied = true;
		
		Properties localIdProperties = loadLocalIdProperties();

		if (localIdProperties == null)
		{
			localIdProperties = createLocalIdProperties(isCallPermitted);
		}
		else
		{
			localIdProperties.setProperty("permission.granted", String.valueOf(isCallPermitted));
		}
		
		if (heartbeatId != null)
		{
			localIdProperties.setProperty("heartbeat.id", heartbeatId);
		}
		
		saveLocalIdProperties(localIdProperties);
		
		//initial call after first login
		if (isCallPermitted)
		{
			call();
		}
	}
	
	public boolean haveToAskForPermissionNow() 
	{
		return !askForPermissionReplied && getAskForPermission() && (loadLocalIdProperties() == null);
	}
	
	private void httpCall(Properties localIdProperties, String url) 
	{
		if (log.isDebugEnabled())
			log.debug("Heartbeat calling: " + url);
		
		HttpClient httpClient = new HttpClient();

		PostMethod post = new PostMethod(url);

		try 
		{
			if (heartbeatId == null)
			{
				heartbeatId = localIdProperties.getProperty("heartbeat.id");//FIXME use constant
			}
			
			if (heartbeatId != null)
			{
				post.addParameter("id", heartbeatId);
			}
			post.addParameter("callCount", String.valueOf(callCount));
			post.addParameter("osName", osName);
			post.addParameter("osVersion", osVersion);
			post.addParameter("javaVendor", javaVendor);
			post.addParameter("javaVersion", javaVersion);
			post.addParameter("serverInfo", serverInfo);
			post.addParameter("productName", productName);
			post.addParameter("productVersion", productVersion);
			post.addParameter("dbName", dbName);
			post.addParameter("dbVersion", dbVersion);
			
			int statusCode = httpClient.executeMethod(post);
			if (statusCode == HttpStatus.SC_OK)
			{
				if (heartbeatId == null)
				{
					heartbeatId = post.getResponseBodyAsString();
					heartbeatId = heartbeatId == null ? null : heartbeatId.trim();
					
					localIdProperties.setProperty("heartbeat.id", heartbeatId);

					saveLocalIdProperties(localIdProperties);
				}
			}
			else if ( 
				//supported types of redirect
				statusCode == HttpStatus.SC_MOVED_PERMANENTLY
				|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY
				|| statusCode == HttpStatus.SC_SEE_OTHER
				|| statusCode == HttpStatus.SC_TEMPORARY_REDIRECT
				)
			{
				Header header = post.getResponseHeader("location");
				if (header != null)
				{
					if (log.isDebugEnabled())
						log.debug("Heartbeat listener redirected.");

					httpCall(localIdProperties, header.getValue());
				}
				else
				{
					if (log.isDebugEnabled())
						log.debug("Heartbeat listener redirected to unknown destination.");
				}
			}
			else
			{
				if (log.isDebugEnabled())
					log.debug("Connecting to heartbeat listener URL failed. Status code: " + statusCode);
			}
		}
		catch (IOException e)
		{
			if (log.isDebugEnabled())
				log.debug("Connecting to heartbeat listener URL failed.", e);
		}
		finally
		{
			// Release current connection to the connection pool once you are done
			post.releaseConnection();
		}
	}
	
	private Properties createLocalIdProperties(boolean isCallPermitted) 
	{
		Properties localIdProperties = new Properties();
		
		localIdProperties.setProperty("permission.granted", String.valueOf(isCallPermitted));

		saveLocalIdProperties(localIdProperties);
		
		return localIdProperties;
	}
	
	private synchronized void saveLocalIdProperties(Properties localIdProperties) 
	{
		File localIdFile = getLocalIdFile();
		if (!localIdFile.exists())
		{
			localIdFile.getParentFile().mkdirs();
		}
		
		FileOutputStream fos = null;
		
		try
		{
			fos = new FileOutputStream(localIdFile);
			localIdProperties.store(fos, "heartbeat local ID file");
			fos.flush();
		}
		catch (IOException e)
		{
			if (log.isDebugEnabled())
				log.debug("Creating heartbeat local ID properties file failed.", e);
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
	private String getLocalId()
	{
		if (localId == null)
		{
			osName = System.getProperty("os.name");
			osVersion = System.getProperty("os.version");
			javaVendor = System.getProperty("java.vendor");
			javaVersion = System.getProperty("java.version");
			serverInfo = servletContext.getServerInfo();
			location = servletContext.getRealPath("/");
			dbName = null;
			dbVersion = null;
			
			Connection connection = null;

			try
			{
				connection = dataSource.getConnection();
				DatabaseMetaData metaData = connection.getMetaData();
				
				dbName = metaData.getDatabaseProductName();
				dbVersion = metaData.getDatabaseProductVersion();
			}
			catch (SQLException e)
			{
			}
			finally
			{
				if (connection != null)
				{
					try
					{
						connection.close();
					}
					catch (SQLException e)
					{
					}
				}
			}
			
			String idSource = 
				//javaVendor + "|" 
				//+ javaVersion + "|" 
				serverInfo + "|" 
				+ productName + "|" 
				+ productVersion + "|" 
				+ (location == null ? "" : location);
			
			MessageDigest messageDigest = null;
			try
			{
				messageDigest = MessageDigest.getInstance("MD5");
			}
			catch(NoSuchAlgorithmException e)
			{
			}
			
			if (messageDigest == null)
			{
				localId = String.valueOf(idSource.hashCode());
			}
			else
			{
				byte[] idBytes = messageDigest.digest(idSource.getBytes());
				StringBuffer idBuffer = new StringBuffer(2 * idBytes.length);
				for(int i = 0; i < idBytes.length; i++)
				{
					String hexa = Integer.toHexString(128 + idBytes[i]).toUpperCase();
					hexa = ("00" + hexa).substring(hexa.length());
					idBuffer.append(hexa);
				}
				localId = idBuffer.toString();
			}
		}
		
		return localId;
	}
	
	private File getLocalIdFile()
	{
		File jsHomeDir = new File(new File(System.getProperty("user.home")), ".jasperserver");
		
		return new File(jsHomeDir, getLocalId());
	}
	
	private synchronized Properties loadLocalIdProperties()
	{
		Properties properties = null;

		File localIdFile = getLocalIdFile();
		if (localIdFile.exists() && localIdFile.isFile())
		{
			properties = new Properties();

			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(localIdFile);
				properties.load(fis);
			}
			catch (IOException e)
			{
				if (log.isDebugEnabled())
					log.debug("Loading heartbeat local ID properties file failed.", e);
			}
			finally
			{
				if (fis != null)
				{
					try
					{
						fis.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		
		return properties;
	}

}
