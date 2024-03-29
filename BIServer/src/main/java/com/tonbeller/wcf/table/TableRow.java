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
 * 
 */
package com.tonbeller.wcf.table;

/**
 * a row of the table. To make selection work, this class must implement equals() and hashCode() properly.
 */
public interface TableRow {
  /** returns the cell value */
  public Object getValue(int columnIndex);
}