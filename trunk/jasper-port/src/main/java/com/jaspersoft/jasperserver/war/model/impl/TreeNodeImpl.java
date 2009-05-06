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
package com.jaspersoft.jasperserver.war.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Implementation of TreeNode
 * @author asokolnikov
 *
 */
public class TreeNodeImpl implements TreeNode {
    
    protected String id;
    protected String label;
    protected String type;
    protected String uri;
    protected String fontStyle;
    protected String fontWeight;
    protected String fontColor;
    protected int order = Integer.MIN_VALUE;
    protected String tooltip;
    protected JSONObject extraProperty;
    
    protected List children = new ArrayList();
    
    protected TreeDataProvider dataProvider;
    
    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri) {
        
        this.dataProvider = dataProvider;
        this.id = id;
        this.label = label;
        this.type = type;
        this.uri = uri;
    }

    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, int order) {

    	this(dataProvider, id, label, type, uri);
    	this.order = order;
    }

    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, JSONObject extraProperty) {
        this(dataProvider, id, label, type, uri);
        this.extraProperty = extraProperty;
    }
    
    public TreeNodeImpl(TreeDataProvider dataProvider, 
            String id, String label, String type, String uri, int order, JSONObject extraProperty) {
        this(dataProvider, id, label, type, uri, order);
        this.extraProperty = extraProperty;
    }
    
    public List getChildren() {
        return children;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getUriString() {
        return uri;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public JSONObject getExtraProperty() {
        return extraProperty;
    }

    public void setExtraProperty(JSONObject extraProperty) {
        this.extraProperty = extraProperty;
    }

    public String toJSONString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("{\"id\":\"").append(id).append("\",")
            .append("\"label\":\"").append(escape(label)).append("\",")
            .append("\"type\":\"").append(type).append("\",")
            .append("\"uri\":\"").append(uri).append("\"");

        if(fontStyle != null) {
            sb.append(",\"fontStyle\":\"").append(fontStyle).append("\"");
        }
        if (fontWeight != null) {
            sb.append(",\"fontWeight\":\"").append(fontWeight).append("\"");
        }
        if (fontColor != null) {
            sb.append(",\"fontColor\":\"").append(fontColor).append("\"");
        }

        if (order > Integer.MIN_VALUE) {
        	sb.append(",\"order\":").append(order);
        }
        
        if (tooltip != null) {
        	sb.append(",\"tooltip\":\"").append(tooltip).append("\"");
        }
        
        if (extraProperty != null) {
            sb.append(",\"extra\":").append(extraProperty.toJSONString());
        }
        
        if (!children.isEmpty()) {
            sb.append(",\"children\":[");
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                TreeNode child = (TreeNode) iter.next();
                sb.append(child.toJSONString());
                if (iter.hasNext()) {
                    sb.append(',');
                }
            }
            sb.append(']');
        }
        
        sb.append('}');
        
        return sb.toString();
    }
    
    protected String escape(String str) {
    	return (str == null) ? null : str.replace("\"", "\\\"").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
    }

}
