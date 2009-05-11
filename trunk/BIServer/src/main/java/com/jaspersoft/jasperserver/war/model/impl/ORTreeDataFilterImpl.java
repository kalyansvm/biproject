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

import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * This is a container for logical OR operation over all children filters assigned 
 * @author asokolnikov
 *
 */
public class ORTreeDataFilterImpl implements TreeDataFilter {
    
    public List filterList;

    /**
     * Returns true if at least one of children filters returns true.
     * Returns true if no children filters assigned.
     * Returns false otherwise
     */
    public boolean filter(TreeNode node) {
        if (filterList != null) {
            for (Iterator iter = filterList.iterator(); iter.hasNext(); ) {
                TreeDataFilter filter = (TreeDataFilter) iter.next();
                if (filter.filter(node)) {
                    return true;
                }
            }
            return false; // all returned false
        }
        return true;
    }

    public List getFilterList() {
        return filterList;
    }

    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

}
