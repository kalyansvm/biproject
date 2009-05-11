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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * The base interface for tree data provider.
 * Implementing classes make a bridge to any datasource (Repository, 
 * File System, etc.)
 * 
 * @author asokolnikov
 */
public interface TreeDataProvider {

    /**
     * Returns an instance of TreeNode for a given URI.
     * <b>depth</b> parameter controls how many levels of children
     * are going to be preloaded within the requested node 
     * (0 - no children preloaded, 1 - only immeadiate children preloaded, etc.)
     * @param executionContext ExecutionContext instance 
     * @param uri a unique string indentifying the node (uses "/node1/node11" convention)
     * @param depth children tree depth to be preloaded
     * @return TreeNode instance OR null of not found
     */
    public TreeNode getNode(ExecutionContext executionContext, String uri, int depth);
    
    /**
     * Returns a list of TreeNode instances which are immediate children of 
     * a node identified by a given URI. Each node in the list may have its
     * children preloaded if depth is greater that 0
     * Returns an empty list if no children found. 
     * @param executionContext ExecutionContext instance 
     * @param parentUri a unique string indentifying the node
     * @param depth children tree depth to be preloaded
     * @return List of TreeNode instances OR null if parent not found
     */
    public List/*<TreeNode>*/ getChildren(ExecutionContext executionContext, String parentUri, int depth);

}
