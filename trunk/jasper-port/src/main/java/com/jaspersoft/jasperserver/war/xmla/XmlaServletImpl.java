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
package com.jaspersoft.jasperserver.war.xmla;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;

import mondrian.xmla.impl.DefaultXmlaServlet;
import mondrian.xmla.XmlaHandler;
import mondrian.xmla.DataSourcesConfig;
import mondrian.xmla.XmlaException;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.impl.OlapConnectionServiceImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author sbirney
 */

public class XmlaServletImpl extends DefaultXmlaServlet {

    private static final Log log = LogFactory.getLog(XmlaServletImpl.class);

    private static ApplicationContext ctx;

    public static ApplicationContext getContext() { return ctx; }

    public void init(ServletConfig config) throws ServletException {
	log.debug("XmlaServletImpl:init");
   
        ServletContext servletContext = config.getServletContext();
        ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	if (ctx == null) {
	    log.error("XmlaServletImpl:init Unable to obtain ApplicationContext from servletContext");
	    ctx = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
	    if (ctx == null) {
		log.error("XmlaServletImpl:init Unable to obtain ApplicationContext");
	    }
	}
	
        super.init(config);
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
	    throws ServletException, IOException {
	try {
		// js:i18n set servlet character encoding
		if (getConnectionService() != null) {
			this.charEncoding = 
				((OlapConnectionServiceImpl) getConnectionService()).getEncodingProvider().getCharacterEncoding();
			log.debug("doPost:charEncoding set to " + this.charEncoding);
		}
	    super.doPost( request, response ) ;
	} catch (Throwable t) {
	    log.error("XMLA Servlet Error, ROOT CAUSE:");
	    log.error(XmlaException.getRootCause(t).getStackTrace());
	    throw new ServletException(t);
	}
    }

    protected void handleFault(
                    HttpServletResponse response,
                    byte[][] responseSoapParts,
                    Phase phase,
                    Throwable t) {
	log.error("XMLA FAULT!!!");
	log.error(XmlaException.getRootCause(t).getStackTrace());
	super.handleFault(response, responseSoapParts, phase, t);
    }

    protected DataSourcesConfig.DataSources makeDataSources(ServletConfig servletConfig) {
		log.debug("makeDataSources");
		RepositoryService rep = getRepository();
		
		// TODO it is meaningless to generate URLs here, but the XMLServlet requires it
		// Looks like XML/A clients ignore the URL
		
		String servletURL = null;
		try {
			ServletContext servletContext = servletConfig.getServletContext();
			InetAddress local = InetAddress.getLocalHost();
			
			// We can override the default protocol and port with servlet init parameters
			
			String defaultProtocol = servletContext.getInitParameter("defaultProtocol");
			if (defaultProtocol == null || defaultProtocol.trim().length() == 0) {
				defaultProtocol = "http";
			}
			
			String defaultPort = servletContext.getInitParameter("defaultPort");
			if (defaultPort == null || defaultPort.trim().length() == 0) {
				defaultPort = "-1";
			}
			int port = Integer.parseInt(defaultPort);
			
			
			URL root = servletContext.getResource("/");
			// Looks like the path will be /localhost/webapp
			
			int pastHost = root.getPath().indexOf("/", 1); 
			String path = root.getPath().substring(pastHost, root.getPath().length());
			
			servletURL = (new URL(defaultProtocol, local.getCanonicalHostName(), port, path)).toString() + "xmla";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		    
		DataSourcesConfig.DataSources datasources = new DataSourcesConfig.DataSources();
		
		// Use findResource to avoid filtering with security
		FilterCriteria f = FilterCriteria.createFilter(MondrianXMLADefinition.class);
		ResourceLookup[] lookups = rep.findResource(JasperServerUtil.getExecutionContext(), f);
	
		if (lookups == null || lookups.length == 0) {
		    log.debug("No XML/A Connections available");
		    return datasources;
		}
	
		datasources.dataSources =  new DataSourcesConfig.DataSource[lookups.length];
		DataSourcesConfig.DataSource[] ds = datasources.dataSources;
		
		for (int i = 0; i < lookups.length; i++) {
		    DataSourcesConfig.DataSource d = new DataSourcesConfig.DataSource();
		    
                    // to avoid security checkings, we need to use the interface RepositoryUnsecure
		    MondrianXMLADefinition def = (MondrianXMLADefinition) ((RepositoryUnsecure)rep).getResourceUnsecure(null, lookups[i].getURIString());
	
		    d.description = def.getDescription();
	
		    d.url = servletURL;
	
		    d.providerName = "Mondrian";
		    d.providerType = DataSourcesConfig.DataSource.PROVIDER_TYPE_MDP;
		    
		    // This is really about requiring roles
		    
		    d.authenticationMode = DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED;
		    
		    d.name = "Provider=" + d.providerName + ";DataSource=" + def.getCatalog() + ";";
		    d.dataSourceInfo = d.name;
		    
		    DataSourcesConfig.Catalogs cs = new DataSourcesConfig.Catalogs();
		    DataSourcesConfig.Catalog c = new DataSourcesConfig.Catalog();
		    c.name = def.getCatalog();
		    //c.setDataSource(d); //XmlaHandler.java constructor does this for us
		    cs.catalogs = new DataSourcesConfig.Catalog[1];
		    cs.catalogs[0] = c;
		    d.catalogs = cs;
		    c.definition = "JASPERSERVER";

		    if (log.isDebugEnabled()) {
			log.debug("loading DataSource name=" + d.name + 
				 ", info=" + d.dataSourceInfo + ", url=" + d.url);
		    }
	
		    ds[i] = d;	    
		}
		return datasources;
    }

    /**
     * property: xmlaHandler
     */
    protected XmlaHandler getXmlaHandler() {
        if (this.xmlaHandler == null) {
	    log.debug("getXmlaHandler");
            this.xmlaHandler = 
                new XmlaHandlerImpl(this.dataSources, this.catalogLocator,
				    getRepository(), getConnectionService());
        }
        return this.xmlaHandler;
    }

    /**
     * property: repository
     */
    private RepositoryService mRepository;
    public RepositoryService getRepository() {
	if (mRepository == null) {
	    log.debug("getRepository");
	    mRepository = (RepositoryService)ctx.getBean("repositoryService");
	    if (mRepository == null) {
		log.error("repositoryService not available in context: " + ctx);
	    }
	}
        return mRepository;
    }
    public void setRepository(RepositoryService repository) {
        mRepository = repository;
    }

    /**
     * property: olapConnectionService
     */
    private OlapConnectionService mConnectionService;
    public OlapConnectionService getConnectionService() {
	if (mConnectionService == null) {
	    log.debug("getConnectionService");
	    mConnectionService = (OlapConnectionService)ctx.getBean("olapConnectionService");
	    if (mConnectionService == null) {
		log.error("repositoryService not available in context: " + ctx);
	    }
	}
        return mConnectionService;
    }
    public void setConnectionService( OlapConnectionService cs ) {
        mConnectionService = cs;
    }
}
 