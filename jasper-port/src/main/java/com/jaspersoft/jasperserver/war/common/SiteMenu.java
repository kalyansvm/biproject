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

import java.io.InputStream;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sbirney
 */

public class SiteMenu {

    private static final Log log = LogFactory
	.getLog(SiteMenu.class);

    private final String URL_ATTR = "url";
    private final String SERVLETPARAMS_ATTR = "servletParams";
    private final String NAME_ATTR = "name";
    private final String MENU_ITEM_NODE_NAME = "menu-item";
    private final String SUB_ITEMS_NODE_NAME = "sub-items";
    private final String ROLES_NODE_NAME = "roles";
    private final String ROLE_NODE_NAME = "role";
    private final String MENU_XML_URI = "/JI-menu.xml";
    private static SiteMenu theInstance;

    public static SiteMenu instance() {
	if (theInstance == null) {
	    theInstance = new SiteMenu();
	}
	return theInstance;
    }

    private MenuItem mMenu = null;
    public MenuItem getMenu() throws Exception {
	if (mMenu == null) {
	    mMenu = parse(MENU_XML_URI);
	}
	return mMenu;
    }

    protected MenuItem parse(String menuXmlUri) throws Exception {
	MenuItem result = null;
	try {
	    InputStream siteXml = this.getClass().getResourceAsStream(menuXmlUri);
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
	    Document menuDoc = domBuilder.parse(siteXml);
	    result = parseMenuItem(menuDoc.getDocumentElement());
	} catch (Exception e) {
	    log.error(e);
	    throw(e);
	}
	return result;
    }

    // just for debugging
    public void printNode( org.w3c.dom.Node n, int indent ) {
	for (int i=0; i<indent; i++) {
	    System.out.print(" ");
	}
	System.out.println(n);
	if (n.hasChildNodes()) {
	    for (int j=0; j<n.getChildNodes().getLength(); j++) {
		printNode( n.getChildNodes().item(j), indent+2 );
	    }
	}
    }

    protected MenuItem parseMenuItem( Element itemNode ) {
	MenuItem menuItem = new MenuItem();
	menuItem.name = itemNode.getAttribute(NAME_ATTR);
	if (itemNode.hasAttribute(URL_ATTR)) {
	    menuItem.url = itemNode.getAttribute(URL_ATTR);
	}
	if (itemNode.hasAttribute(SERVLETPARAMS_ATTR)) {
	    menuItem.servletParams = itemNode.getAttribute(SERVLETPARAMS_ATTR);
	}
	List subItems = new ArrayList();
	List roles    = new ArrayList();

	if (itemNode.hasChildNodes()) {
	    for (int i=0; i<itemNode.getChildNodes().getLength(); i++) {
		Node child = itemNode.getChildNodes().item(i);
		if (SUB_ITEMS_NODE_NAME.equals(child.getNodeName())) {
		    for (int j=0; j<child.getChildNodes().getLength(); j++) {
			Node menuChild = child.getChildNodes().item(j);
			if (MENU_ITEM_NODE_NAME.equals(menuChild.getNodeName())) {
			    subItems.add(parseMenuItem( (Element)menuChild ));
			}
		    }
		}
		if (ROLES_NODE_NAME.equals(child.getNodeName())) {
		    for (int k=0; k<child.getChildNodes().getLength(); k++) {
			Node roleChild = child.getChildNodes().item(k);
			if (ROLE_NODE_NAME.equals(roleChild.getNodeName())) {
			    roles.add(roleChild.getFirstChild().getNodeValue());
			}
		    }
		}
	    }
	}
	menuItem.subItems = (MenuItem[])subItems.toArray(new MenuItem[subItems.size()]);
	menuItem.roles    = (String[])roles.toArray(new String[roles.size()]);
	return menuItem;
    }

    public static class MenuItem {
	String name;
	public String getName() { return name; }

	String url;
	public String getUrl() { return url; }

	String servletParams;
	public String getServletParams() { return servletParams; }

	MenuItem[] subItems;
	public MenuItem[] getSubItems() { return subItems; }
	public boolean getHasSubItems() { 
	    return (subItems != null && subItems.length > 0);
	}
		

	String[] roles;
	public String[] getRoles() { return roles; }
	// acegi tag seems to want a comma separated String,
	// rather than a String[] of roles, so....
	public String getRolesStr() {
	    StringBuffer b = new StringBuffer();
	    for (int i=0; i<roles.length; i++) {
		b.append(roles[i]);
		if (i<roles.length-1) {
		    b.append(",");
		}
	    }
	    return b.toString();
	}

	public String toString() {
	    StringBuffer buff = new StringBuffer();
	    buff.append("MenuItem( name=").append(name);
	    buff.append(" url=").append(url);
	    buff.append(" servletParams=").append(servletParams);
	    buff.append(" roles=");
	    for (int i=0; i<roles.length; i++) {
		buff.append(roles[i]);
		if (i<(roles.length-1)) {
		    buff.append(",");
		}
	    }
	    buff.append(" sub-items=");
	    for (int j=0; j<subItems.length; j++) {
		buff.append(subItems[j].toString());
		if (j<(subItems.length-1)) {
		    buff.append(",");
		}
	    }
	    buff.append(" )");
	    return buff.toString();
	}

    }

}
 