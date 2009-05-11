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

import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Implementation of TreeDataFilter.
 * Includes a TreeNode instance into a data set if its type is one of the
 * configured types
 * @author asokolnikov
 *
 */public class TypeInclusiveTreeDataFilterImpl implements TreeDataFilter {
    
    private List includeTypesList;

    /**
     * Returns true if excludeTypesList contains a given node type.
     * Returns false otherwise
     */
    public boolean filter(TreeNode node) {
        if (includeTypesList.contains(node.getType())) {
            return true;
        }
        return false;
    }

    public List getIncludeTypesList() {
        return includeTypesList;
    }

    public void setIncludeTypesList(List includeTypesList) {
        this.includeTypesList = includeTypesList;
    }

}
