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
 * Excludes a TreeNode instance out of a data set if its type is one of the
 * configured types
 * @author asokolnikov
 *
 */
public class TypeExclusiveTreeDataFilterImpl implements TreeDataFilter {

    private List excludeTypesList;

    /**
     * Returns false if excludeTypesList contains a given node type.
     * Returns true otherwise
     */
    public boolean filter(TreeNode node) {
        if (excludeTypesList.contains(node.getType())) {
            return false;
        }
        return true;
    }

    public List getExcludeTypesList() {
        return excludeTypesList;
    }

    public void setExcludeTypesList(List excludeTypesList) {
        this.excludeTypesList = excludeTypesList;
    }

}
