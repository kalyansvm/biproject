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
package com.tonbeller.jpivot.mondrian;

import org.apache.log4j.Logger;

import mondrian.olap.Util.PropertyList;
import mondrian.rolap.RolapConnectionProperties;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Cell;
import com.tonbeller.jpivot.olap.navi.DrillThrough;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.table.TableModel;
/**
 * @author Robin Bagot
 *
 * Implementation of the DrillExpand Extension for Mondrian Data Source.
*/
public class MondrianDrillThrough extends ExtensionSupport implements DrillThrough {
  private static Logger logger = Logger.getLogger(MondrianDrillThrough.class);

  private boolean extendedContext = true;

  /**
   * Constructor sets ID
   */
  public MondrianDrillThrough() {
    super.setId(DrillThrough.ID);
  }

  /**
   * drill through is possible if <code>member</code> is not calculated
   */
  public boolean canDrillThrough(Cell cell) {
    return ((MondrianCell) cell).getMonCell().canDrillThrough();
    //String sql = ((MondrianCell) cell).getMonCell().getDrillThroughSQL(extendedContext);
    //return sql != null;
  }

  /**
   * does a drill through, retrieves data that makes up the selected Cell
   */
  public TableModel drillThrough(Cell cell) {
    logger.debug(Resources.instance().getString("jpivot.MondrianDrillThrough.debug.generateSql"));
    String sql = ((MondrianCell) cell).getMonCell().getDrillThroughSQL(extendedContext);
    if (sql == null) {
      String msg = Resources.instance().getString("jpivot.MondrianDrillThrough.error.drillThroughSQLReturnedNull");
      logger.error(msg);
      throw new NullPointerException(msg);
    }
    logger.debug(Resources.instance().getString("jpivot.MondrianDrillThrough.debug.sqlEqual") + sql);
    MondrianDrillThroughTableModel dtm = new MondrianDrillThroughTableModel(cell); // use cell to pass locale
    dtm.setSql(sql);
    PropertyList connectInfo = ((MondrianModel) getModel()).getConnectProperties();
    String jdbcUrl = connectInfo.get(RolapConnectionProperties.Jdbc.name());
    String jdbcUser = connectInfo.get(RolapConnectionProperties.JdbcUser.name());
    String jdbcPassword = connectInfo.get(RolapConnectionProperties.JdbcPassword.name());
    String dataSourceName = connectInfo.get(RolapConnectionProperties.DataSource.name());
    dtm.setJdbcUrl(jdbcUrl);
    dtm.setJdbcUser(jdbcUser);
    dtm.setJdbcPassword(jdbcPassword);
    dtm.setDataSourceName(dataSourceName);
    return dtm;
  }

  public boolean isExtendedContext() {
    return extendedContext;
  }

  public void setExtendedContext(boolean extendedContext) {
    this.extendedContext = extendedContext;
  }

}
