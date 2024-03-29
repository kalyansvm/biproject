/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * $Id$
 */
package com.tonbeller.jpivot.olap.navi;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.olap.model.Member;

/**
 * allows to expand / collapse members on an axis. If a member is expanded, 
 * the member itself plus its children are displayed.
 * <p>
 * Example: if you expand "Europe" you may see "Germany", "France" etc.
 * If you collapse "Europe" the countries will not be shown.
 * <p>
 * If multiple Hierarchies are shown on a single axis, all positions
 * are expanded. For example
 * 
 * <pre>
 * -----+-------
 * 2001 | Europe
 * -----+-------
 * 2002 | Europe
 * -----+-------
 * 2003 | Europe
 * -----+-------
 * </pre>
 * 
 * clicking on Europe in 2002 will give
 * 
 * <pre>
 * -----+-------
 * 2001 | Europe
 *      |   Germany
 *      |   France
 * -----+-------
 * 2002 | Europe
 *      |   Germany
 *      |   France
 * -----+-------
 * 2003 | Europe
 *      |   Germany
 *      |   France
 * -----+-------
 * </pre>
 * 
 * @author av
 */

public interface DrillExpandMember extends Extension {
  /**
   * name of the Extension for lookup
   */
  public static final String ID = "drillExpandMember";
  
  /**
   * true if member has children and is not currently expanded
   */
  boolean canExpand(Member member);

  /**
   * true if member has children that are currently displayed. 
   * I.e. member is expanded.
   */
  boolean canCollapse(Member member);

  /**
   * expands member
   */
  void expand(Member member);
  
  /**
   * collapses member
   */
  void collapse(Member member);

}
