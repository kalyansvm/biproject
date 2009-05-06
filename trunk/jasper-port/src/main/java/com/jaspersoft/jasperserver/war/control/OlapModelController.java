/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved. 
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published 
 * by the Free Software Foundation.
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

package com.jaspersoft.jasperserver.war.control;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mondrian.olap.Util.PropertyList;
import mondrian.rolap.RolapConnectionProperties;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.hibernate.lob.SerializableBlob;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.impl.OlapConnectionServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.mondrian.JPivotPackageAccess;
import com.tonbeller.jpivot.mondrian.MondrianDrillThroughTableModel;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.OlapModelDecorator;
import com.tonbeller.jpivot.tags.OlapModelProxy;
import com.tonbeller.jpivot.xmla.XMLA_Model;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.bookmarks.BookmarkManager;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.table.EditableTableComponent;


/**
 * The OlapModelController handles the request to load a specified OLAP model
 * A session 'sess' contains can have multiple models stored in
 * the 'olapModels' map. An entry in the 'olapModels' map contains the 
 * 'olapSessionState' of a given olap model.
 * 
 * @author jshih
 * @revision $Id$
 *
 */
public class OlapModelController extends JRBaseMultiActionController {

    private final Logger logger = Logger.getLogger(OlapModelController.class);
    private Resources resources = Resources.instance(OlapModelController.class);

    private OlapConnectionService olapConnectionService;
	
    /**
     * @return Returns the olapConnectionService.
     */
    public OlapConnectionService getOlapConnectionService() {
	return olapConnectionService;
    }

    /**
     * @param olapConnectionService The olapConnectionService to set.
     */
    public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
	this.olapConnectionService = olapConnectionService;
    }

    public ModelAndView viewOlap(HttpServletRequest req, 
				 HttpServletResponse res)
	throws ServletException {
    	
    if(req.getParameter("_eventId_backFromErrorPage")!= null){
		String redirect = "redirect:/flow.html?_flowId="+req.getParameter("_flowId")+"&folder="+req.getParameter("folderPath");
		return new ModelAndView(redirect);
    }
   	HttpSession sess = req.getSession();
   	
   	// set model attributes
	String viewUri = req.getParameter("name");	
	
	if ((viewUri == null) || ("".equals(viewUri))) {
		viewUri = (String)sess.getAttribute("currentView");
	} else if ("true".equals(req.getParameter("new"))) {
	    clearOlapSession(req);
	}
	String currentView = (String)sess.getAttribute("currentView");
	String currentUserName = (String)sess.getAttribute("currentUserName"); 
	Locale browserLocale = req.getLocale();
	Locale defaultLocale = RequestContext.instance().getLocale();
	Locale locale = LocaleContextHolder.getLocale(); // login locale

	// set locale
	if (locale == null) {
		// default
		locale = defaultLocale;
	}
	logger.debug("Request locale set to: " + locale.toString());	
	req.setAttribute("locale", locale); // for fmt:
	sess.setAttribute("locale", locale); // for drill through
	sess.getServletContext().setAttribute("locale", browserLocale); // for mondrian query, see MondrianModel

	// hook for ja-pro drill-through
	String drillThrough = getDrillThrough(req, sess);
	sess.setAttribute("drillthrough", drillThrough);		
	
	if (viewUri == null || viewUri.length() == 0) {
		logger.warn(resources.getString("jsexception.session.timeout.occurred"));
		// try to recover viewUri from servlet context
		if ((viewUri = (String) sess.getServletContext().getAttribute("name")) == null) {
			logger.warn(resources.getString("jsexception.no.olap.model.name"));
			// display analysis views
			String redirect = "redirect:/" + resources.getString("jasperserver.OlapModelController.olapViewList"); // i.e., flow.html?_flowId=olapViewListFlow&curlnk=2;
			return new ModelAndView(redirect);
		}
	}
	logger.debug("Viewing OLAP Model: " + viewUri);
		
	req.setAttribute("name", viewUri);
	sess.getServletContext().setAttribute("name", viewUri); // initialize and be used in case of session timeout 
		
	// set flag to reload session state when switching view FIXME synchronize?
	boolean isSameView = false;
	if (currentView != null && 
	    viewUri.equals(currentView)) {
	    isSameView = true;
	}
	else {
	    sess.setAttribute("currentView", viewUri); 
	}
	
	boolean isSameUser = setCurrentUser(sess, currentUserName);
	
	if (!isSameUser) {
		// force the re-creation of the saveas form component
		removeAttribute(sess, "saveas");
	}
	
	Map olapModels = (Map) sess.getAttribute("olapModels");
	
	if (olapModels == null) {
	    olapModels = new HashMap();
	    sess.setAttribute("olapModels", olapModels);
	}
	
		
	sess.setAttribute("drillThroughTableModel", new MondrianDrillThroughTableModel(null));
	OlapSessionState sessionState = (OlapSessionState) olapModels.get(viewUri);	
	if (sessionState == null || req.getParameter("mdx") != null || !isSameUser) { // reload sessionState if user is switched
	    sessionState = getOlapSession(viewUri, sess); // load olap model
	    olapModels.put(viewUri, sessionState);
        // need to set the attribute for the first time, so we can leverage it for retrieving the
		// "save view as" functionality
		sess.setAttribute("drillthrough", drillThrough);
	    sess.setAttribute("olapUnit", sessionState.getOlapUnit()); // put current OLAP Unit to session for save purpose
		// restore previously bookmark to the current session  
	    Object state = null;
	    if ((sessionState.getOlapUnit() != null) && (((OlapUnitImpl)sessionState.getOlapUnit()).getOlapViewOptions() != null)) {  
	    	try {
	    	    XMLDecoder d = new XMLDecoder(new BufferedInputStream(((SerializableBlob)((OlapUnitImpl)sessionState.getOlapUnit()).getOlapViewOptions()).getBinaryStream()));
        	    state = d.readObject();
         	    d.close();
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	        BookmarkManager.instance(sess).restoreSessionState(state);
	    } 
	    // get drillthroughSQL
	    String curView = (String)sess.getAttribute("currentView");   
	    EditableTableComponent drillthru = (EditableTableComponent)sess.getAttribute(curView + ".drillthroughtable");    
	    if (drillthru == null) {		
           Model mdl = ((OlapModelProxy)sessionState.getOlapModel()).getRootModel();
           if (mdl instanceof MondrianModel) {     	   
	  	      PropertyList connectInfo = ((MondrianModel)mdl).getConnectProperties();
	  	      if (connectInfo != null) { 	  
	  	    	  MondrianDrillThroughTableModel dtm = new MondrianDrillThroughTableModel(null);
	  	          String jdbcUrl = connectInfo.get(RolapConnectionProperties.Jdbc.name());
	  	          String jdbcUser = connectInfo.get(RolapConnectionProperties.JdbcUser.name());
	  	          String jdbcPassword = connectInfo.get(RolapConnectionProperties.JdbcPassword.name());
	  	          String dataSourceName = connectInfo.get(RolapConnectionProperties.DataSource.name());
	  	          dtm.setJdbcUrl(jdbcUrl);
	  	          dtm.setJdbcUser(jdbcUser);
	  	          dtm.setJdbcPassword(jdbcPassword);
	  	          dtm.setDataSourceName(dataSourceName); 
	  	          // restore sql
	  	          HashMap hMap = (HashMap)state;
	  	          if (hMap != null) {
	  	             String curSql = (String)hMap.get("drillThruSQL");
	  	             dtm.setSql(curSql);	  
	  	             sess.setAttribute("drillThroughTableModel", dtm);
	  	             // set drill-throug switches
	  	             sess.removeAttribute("belowCube");
	  	             if (curSql != null && curSql.length() > 0) {
				 logger.debug("Drillthrough SQL: " + curSql);
	  	            	 // if curSql contains a sql statement, activate drill-through table (for belowCube option only)
	  	            	 logger.debug("Display drill-through table.");
	  	            	 if (state != null && viewUri != null) {
	  	            		 // set showTableBelowCube in olapModel
	  	            		 HashMap stateMap = (HashMap) ((HashMap) state).get(viewUri + "/displayform");
	  	            		 if (stateMap != null) {
	  	            			 Boolean showTableBelowCube = (Boolean) stateMap.get("extensions(drillThrough).showTableBelowCube");
	  	            			 sess.setAttribute("belowCube", showTableBelowCube.booleanValue() ? "true" : "false");
			  	           	  }
	  	            		 else {
	  	            			 logger.error("Invalid displayform state.");
	  	            		 }
	  	            	 }
	  	            	 else {
	  	            		 logger.error("Invalid view URI, or view state.");
	  	            	 }
	  	            	 sess.setAttribute("drillthrough", "y"); // FIXME: check (((DrillThroughUI) listener).isShowTableBelowCube())
	  	            	 sess.setAttribute("inDrillThrough", "true");
	  	             }
	  	             else {
	  	            	 logger.debug("Drill-through table not displayed");
	  	             }
	  	          } else {
	  	        	 dtm.setSql(""); 
	  	          }
	  	      }
	       } 
	    }
	}
	// ...blank-view-the-second-time-load-fix: load the view if switched
	else if (!isSameView) {	
	    sessionState = getOlapSession(viewUri, sess); // load olap model
	}

    setJPivotParams( req, sessionState.getOlapModel() );
	
	req.setAttribute("olapModel", sessionState.getOlapModel());
	req.setAttribute("olapSession", sessionState);
	req.setAttribute("drillthrough", drillThrough); // hook for pro (deprecated)
	
	
	// Because WCF is so behind the times, we have to also set the 
	// session attribute by name
	
	if (sessionState.getOlapModel() instanceof MondrianModel) {
	    MondrianModel tmpModel = (MondrianModel)sessionState.getOlapModel();
	    //logger.warn("#TMP OlapModel.connection is " + tmpModel.getConnection());
	    // the connect string may not even be relevant since we use connect props instead
	    //log.warn("#TMP OlapModel.connectString is " + tmpModel.getConnectString());
	}

	// put olapModel into session initially and subsequently when view is changed
	if ((sess != null && sess.getAttribute("olapModel") == null) ||      // initial session
		(sessionState != null && sessionState.getOlapModel() != null &&  // subsequent session
		 sess.getAttribute("olapModel") != sessionState.getOlapModel())) {// may be refactor to replace isSameView)
		sess.setAttribute("olapModel", sessionState.getOlapModel());
	}
	
	return new ModelAndView("/olap/viewOlap");
    }

    private void clearOlapSession(HttpServletRequest req) {
	HttpSession sess = req.getSession();
	Map olapModels = (Map) sess.getAttribute("olapModels");
	if (olapModels == null) return;
	String viewUri = req.getParameter("name");	
	olapModels.remove(viewUri);
	sess.setAttribute("olapUnit", null);
    }

    private void removeAttribute(HttpSession sess, String component) {
    	Enumeration attributeNames = sess.getAttributeNames();
    	while (attributeNames.hasMoreElements()) {
    		String attribute = (String) attributeNames.nextElement();
    		if (attribute.endsWith(component)) {
    			sess.removeAttribute((String) attribute);
    			break;
    		}
    	}
    }
    
    /**
     * set drill-through status:
     * x = drill-through in new browser window
     * y = drill-through below cube (navigation table)
     * z = other hyperlinks while in 'y'
     * @param req
     * @param sess
     * @return
     */
    private String getDrillThrough(HttpServletRequest req, HttpSession sess) {
    	String drillthrough = null;
    	Map parametersMap = req.getParameterMap();
    	Iterator itrMap = parametersMap.entrySet().iterator();
		sess.setAttribute("inDrillThrough", "false");
    	while (itrMap.hasNext()) {
    		Map.Entry mapEntry = (Map.Entry) itrMap.next();
    		String requestParameter = (String) mapEntry.getKey();
    		if (requestParameter.equals("drillthrough")) {
    			// initialize
        		drillthrough = ((String[]) mapEntry.getValue())[0];
				sess.setAttribute("inDrillThrough", "true");
    			if (sess.getAttribute("changemode") != null) {
    				// clear it
        			sess.removeAttribute("changemode"); 
    			}
        		break;
    		}
    		else if (requestParameter.contains("drillthrough")) {
    			// subsequent interactions for drill-through pop-up
    			if (sess.getAttribute("changemode") != null) {
    				// remove it, don't set drillthrough as side effect.
        			sess.removeAttribute("changemode"); 
    			}
    			else {
        			drillthrough = "x"; //(String) sess.getAttribute("drillthrough"); 
        			sess.setAttribute("inDrillThrough", "true");
    			}
    			break;
    		}
    	}
    	return drillthrough;
    }
    
    /** may set state in the OlapModel to reflect the supported http params */
    protected void setJPivotParams( HttpServletRequest req, 
				    OlapModel model ) {
	String mdx = req.getParameter("mdx");
	if (mdx != null) {
		logger.debug("mdx request param: |" + mdx + "|");
		logger.debug("OlapModel class: " + model.getClass());
	    // how many layers are there to this onion?
	    while (model instanceof OlapModelDecorator) {
		model = ((OlapModelDecorator)model).getDelegate();
		logger.debug("OlapModel class: " + model.getClass());
	    }
	    // JPivot is missing out on polymorphism here
	    if (model.getClass() == MondrianModel.class) {
		JPivotPackageAccess.setMdx( (MondrianModel)model, mdx );
	    }
	    if (model.getClass() == XMLA_Model.class) {
		try {
		    ((XMLA_Model)model).setUserMdx(mdx);
		} catch (com.tonbeller.jpivot.olap.model.OlapException oe) {
		    logger.error(oe);
		}
	    }
	}
    }

    protected OlapSessionState getOlapSession(String viewUri, HttpSession sess) {
		
	logger.debug("Setting OlapModel for " + viewUri);
		
	RequestContext context = RequestContext.instance();
	ExecutionContextImpl executionContext = new ExecutionContextImpl();

	OlapUnit olapUnit = (OlapUnit) getRepository().getResource(executionContext,
								   viewUri);

	if (olapUnit == null) {
	    throw new JSException("jsexception.no.olap.model.retrieved");
	}

	OlapModel model = getOlapConnectionService().createOlapModel(executionContext, olapUnit);

	if (model == null) {
	    throw new JSException("jsexception.no.olap.model.created.for", new Object[] {viewUri});
	}
			
	model = (OlapModel) model.getTopDecorator();
	model.setLocale(context.getLocale());
	//model.setServletContext(context.getSession().getServletContext());
	model.setServletContext(sess.getServletContext());
	model.setID(viewUri);

	/*
	  ClickableExtension ext = (ClickableExtension) model.getExtension(ClickableExtension.ID);
	  if (ext == null) {
	  ext = new ClickableExtensionImpl();
	  model.addExtension(ext);
	  }
	  ext.setClickables(clickables);
	*/
	// stackMode
	    
	OlapModelProxy omp = OlapModelProxy.instance(viewUri, sess, false);
	/*	    if (queryName != null)
	  omp.initializeAndShow(queryName, model);
	  else
	*/	    
	try {
	    // add to interface to remove type cast?
	    ((OlapConnectionServiceImpl)getOlapConnectionService()).
		initializeAndShow(omp, viewUri, model, olapUnit);
	} catch (Exception e) {
	    throw new JSException(e);
	}

	return new OlapSessionState(omp, olapUnit);
    }


    /**
     * The handle() method looks up a given OLAP model given by the command parameter.
     * 
     * @param request
     * @param response
     * @param command Specifies the OLAP model to load.
     * @param errors TODO
     * @return
     */
    protected ModelAndView handle(HttpServletRequest request,
				  HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		
	ModelAndView modelAndView = null;

	ResourceLookupImpl olapUnitCommand = (ResourceLookupImpl) command;

	String olapUnitName = olapUnitCommand.getName();
		
	if (olapUnitName != null) 
	    {
		modelAndView = new ModelAndView("olap/viewOlap", "olapUnitName", olapUnitName);
	    }
	else {
	    // TODO resolve the double clicking problem
	    ExecutionContext executionContext = JasperServerUtil.getExecutionContext(request);
	    ResourceLookup[] olapUnits = repository.findResource(executionContext, FilterCriteria.createFilter(OlapUnit.class));
	    modelAndView = new ModelAndView("listOlapViews", "olapUnits", olapUnits);			
	}
		
	return modelAndView;
    }
	
    public class OlapSessionState {
	private OlapModel olapModel;
	private OlapUnit olapUnit;
		
	public OlapSessionState(OlapModel olapModel, OlapUnit olapUnit) {
	    this.olapModel = olapModel;
	    this.olapUnit = olapUnit;
	}
		
	/**
	 * @return Returns the olapModel.
	 */
	public OlapModel getOlapModel() {
	    return olapModel;
	}
	/**
	 * @param olapModel The olapModel to set.
	 */
	public void setOlapModel(OlapModel olapModel) {
	    this.olapModel = olapModel;
	}
	/**
	 * @return Returns the olapUnit.
	 */
	public OlapUnit getOlapUnit() {
	    return olapUnit;
	}
	/**
	 * @param olapUnit The olapUnit to set.
	 */
	public void setOlapUnit(OlapUnit olapUnit) {
	    this.olapUnit = olapUnit;
	    
	}
		
		
    }
    
    private boolean setCurrentUser(HttpSession sess, String currentUserName) {
    	boolean isSameUser = false;
    	
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == null) {
        	logger.error("authenticationError");
        }
        
        User user = (User) authentication.getPrincipal();
        if (user == null) {
        	logger.error("userError");
        }
        
        String userName = user.getFullName();
        
    	if (currentUserName != null &&
    		userName.equals(currentUserName)) {
    		isSameUser = true;
    	}
    	else {
    	    sess.setAttribute("currentUserName", userName); 
    	}
    	
    	return isSameUser;
    }
}
