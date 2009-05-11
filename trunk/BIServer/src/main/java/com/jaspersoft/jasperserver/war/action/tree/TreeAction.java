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
package com.jaspersoft.jasperserver.war.action.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeDataProviderFactory;
import com.jaspersoft.jasperserver.war.model.TreeHelper;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Spring Action class for Tree flow.
 * @author asokolnikov
 */
public class TreeAction extends MultiAction {
	
	public static final String AJAX_REPORT_MODEL = "ajaxResponseModel";
	public static final String PROVIDER = "provider";
	public static final String URI = "uri";
	public static final String DEPTH = "depth";
    public static final String PREFETCH = "prefetch";
    public static final String MESSAGE_ID = "messageId";
	
	protected final Log log = LogFactory.getLog(this.getClass());

	private TreeDataProviderFactory treeDataProviderFactory;
	private MessageSource messageSource;
	
	// Actions

	/**
	 * Returns internationalized message for tree client
	 */
    public Event getMessage(RequestContext context) {
    	
    	String messageId = (String) context.getRequestParameters().get(MESSAGE_ID);
    	String message = messageSource.getMessage(messageId, null, LocaleContextHolder.getLocale());
    	
    	context.getRequestScope().put(AJAX_REPORT_MODEL, message);

    	return success();
    }
    
	/**
	 * Gets the data, builds tree model and returns serialized tree data 
	 */
    public Event getNode(RequestContext context) {
    	
    	String providerId = (String) context.getRequestParameters().get(PROVIDER);
    	String uri = (String) context.getRequestParameters().get(URI);
    	String depth = (String) context.getRequestParameters().get(DEPTH);
        String prefetchNodesList = context.getRequestParameters().get(PREFETCH);
    	
    	int d = 0;
    	if (depth != null && depth.length() > 0) {
	    	try {
	    		d = Integer.parseInt(depth);
	    		if (d < 0) {
	    			d = 0;
	    		}
	    	} catch (Exception e) {
	            log.error("Invalid parameter : depth : " + depth, e);
	        }
    	}
        
        List prefetchList = null;
        if (prefetchNodesList != null && prefetchNodesList.length() > 0) {
            String[] ss = prefetchNodesList.split(",");
            prefetchList = new ArrayList();
            for (int i = 0; i < ss.length; i++) {
                prefetchList.add(ss[i]);
            }
        }
    	
    	TreeDataProvider treeDataProvider = findProvider(context, providerId);
    	
        TreeNode treeNode;
        if (prefetchList == null) {
            treeNode = treeDataProvider.getNode(exContext(context), uri, d);
        } else {
            treeNode = TreeHelper.getSubtree(exContext(context), treeDataProvider, uri, prefetchList);
        }
    	
    	String model = "";
    	if (treeNode != null) {
    	    StringBuffer sb = new StringBuffer();
    	    sb.append("<div id='treeNodeText'>");
    	    sb.append(treeNode.toJSONString());
    	    sb.append("</div>");
    	    model = sb.toString();
    	}
    	
    	context.getRequestScope().put(AJAX_REPORT_MODEL, model);
    	
    	return success();
    }
    
    /**
     * Gets childern for specified tree node
     * @param context
     * @return
     */
    public Event getChildren(RequestContext context) {
    	
    	String providerId = (String) context.getRequestParameters().get(PROVIDER);
    	String uri = (String) context.getRequestParameters().get(URI);
    	
    	TreeDataProvider treeDataProvider = findProvider(context, providerId);
    	
    	TreeNode treeNode = treeDataProvider.getNode(exContext(context), uri, 1);
    	
    	String model = "";
    	if (treeNode != null) {
    	    StringBuffer sb = new StringBuffer();
    	    sb.append("<div id='treeNodeText'>");
    	    //sb.append(treeNode.toJSONString());
    	    sb.append('[');
    	    for (Iterator i = treeNode.getChildren().iterator(); i.hasNext(); ) {
    	        TreeNode n = (TreeNode) i.next();
    	        sb.append(n.toJSONString());
    	        if (i.hasNext()) {
    	            sb.append(',');
    	        }
    	    }
    	    sb.append(']');
    	    sb.append("</div>");
    	    model = sb.toString();
    	}
    	
    	context.getRequestScope().put(AJAX_REPORT_MODEL, model);
    	
    	return success();
    }
    
    private TreeDataProvider findProvider(RequestContext context, String providerId) {
    	TreeDataProvider treeDataProvider = null;
    	// First, try to find data provider in session scope
    	treeDataProvider = (TreeDataProvider) context.getExternalContext().getSessionMap().get(providerId);
    	// Then, try to find it in the factory
		if (treeDataProvider == null) {
    		treeDataProvider =  treeDataProviderFactory.getDataProvider(providerId);
		}
		// Fail if not found
		if (treeDataProvider == null) {
			log.error("Cannot find tree data provider with id : " + providerId);
			throw new IllegalArgumentException("Cannot find tree data provider with id : " + providerId);
		}
		
    	return treeDataProvider;
    }
    
	// Getters and Setters
	
    public TreeDataProviderFactory getTreeDataProviderFactory() {
        return treeDataProviderFactory;
    }
    
    public void setTreeDataProviderFactory(
            TreeDataProviderFactory treeDataProviderFactory) {
        this.treeDataProviderFactory = treeDataProviderFactory;
    }

	private ExecutionContext exContext(RequestContext rContext) {
		return JasperServerUtil.getExecutionContext(rContext);
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

