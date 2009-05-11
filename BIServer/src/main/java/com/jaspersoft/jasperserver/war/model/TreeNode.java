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
package com.jaspersoft.jasperserver.war.model;

import java.util.List;

/**
 * The base interface for each node in the UI Tree
 * 
 * @author asokolnikov
 */
public interface TreeNode extends JSONObject {
    
    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String TYPE = "type";
    public static final String URI = "uri";
    public static final String ORDER = "order";
    public static final String TOOLTIP = "tooltip";

    public String getId();
    
    public String getLabel();
    
    public String getType();
    
    public String getUriString();
    
    public int getOrder();
    
    public void setOrder(int order);

    public String getFontStyle();

    public void setFontStyle(String fontStyle);

    public String getFontColor();

    public void setFontColor(String fontColor);

    public String getFontWeight();

    public void setFontWeight(String fontWeight);
    
    public String getTooltip();
    
    public void setTooltip(String tooltip);


    /**
     * Extra property is a way for TreeDataProvider to attach its specific
     * property or set of objects and properties to be available on client side.
     * Each client side tree node will have it in its node.param.extra property
     * @return
     */
    public JSONObject getExtraProperty();
    
    public List getChildren();
   
}
