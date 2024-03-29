/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.api.engine.jasperreports.common;

import java.io.Serializable;
import java.util.Map;

/**
 * @author sanda zaharia
 * @version $Id: XlsExportParametersBean.java 10174 2007-09-20 08:01:37Z lucian $
 */
public class XlsExportParametersBean extends AbstractExportParameters {
	
	private Boolean detectCellType;
	private Boolean onePagePerSheet;
	private Boolean removeEmptySpaceBetweenRows;
	private Boolean removeEmptySpaceBetweenColumns;
	private Boolean whitePageBackground;
	private Boolean ignoreGraphics;
	private Boolean collapseRowSpan;
	private Boolean ignoreCellBorder;
	private Boolean fontSizeFixEnabled;
	private Integer maximumRowsPerSheet;
	private Map xlsFormatPatternsMap;
	
	/**
	 * @return Returns the detectCellType.
	 */
	public Boolean getDetectCellType() {
		return detectCellType;
	}
	/**
	 * @param detectCellType The detectCellType to set.
	 */
	public void setDetectCellType(Boolean detectCellType) {
		this.detectCellType = detectCellType;
	}
	/**
	 * @return Returns the onePagePerSheet.
	 */
	public Boolean getOnePagePerSheet() {
		return onePagePerSheet;
	}
	/**
	 * @param onePagePerSheet The onePagePerSheet to set.
	 */
	public void setOnePagePerSheet(Boolean onePagePerSheet) {
		this.onePagePerSheet = onePagePerSheet;
	}
	/**
	 * @return Returns the removeEmptySpaceBetweenRows.
	 */
	public Boolean getRemoveEmptySpaceBetweenRows() {
		return removeEmptySpaceBetweenRows;
	}
	/**
	 * @param removeEmptySpaceBetweenRows The removeEmptySpaceBetweenRows to set.
	 */
	public void setRemoveEmptySpaceBetweenRows(Boolean removeEmptySpaceBetweenRows) {
		this.removeEmptySpaceBetweenRows = removeEmptySpaceBetweenRows;
	}
	/**
	 * @return Returns the removeEmptySpaceBetweenColumns.
	 */
	public Boolean getRemoveEmptySpaceBetweenColumns() {
		return removeEmptySpaceBetweenColumns;
	}
	/**
	 * @param removeEmptySpaceBetweenColumns The removeEmptySpaceBetweenColumns to set.
	 */
	public void setRemoveEmptySpaceBetweenColumns(Boolean removeEmptySpaceBetweenColumns) {
		this.removeEmptySpaceBetweenColumns = removeEmptySpaceBetweenColumns;
	}
	
	
	/**
	 * @return Returns the whitePageBackground.
	 */
	public Boolean getWhitePageBackground() {
		return whitePageBackground;
	}
	/**
	 * @param whitePageBackground The whitePageBackground to set.
	 */
	public void setWhitePageBackground(Boolean whitePageBackground) {
		this.whitePageBackground = whitePageBackground;
	}
	
	public Object getObject(){
		return this;
	}
	
	public void setPropertyValues(Object object){
		if(object instanceof XlsExportParametersBean){
			XlsExportParametersBean bean =(XlsExportParametersBean)object;
			this.setDetectCellType(bean.getDetectCellType());
			this.setOnePagePerSheet(bean.getOnePagePerSheet());
			this.setRemoveEmptySpaceBetweenRows(bean.getRemoveEmptySpaceBetweenRows());
			this.setRemoveEmptySpaceBetweenColumns(bean.getRemoveEmptySpaceBetweenColumns());
			this.setWhitePageBackground(bean.getWhitePageBackground());
			this.setIgnoreGraphics(bean.getIgnoreGraphics());
			this.setCollapseRowSpan(bean.getCollapseRowSpan());
			this.setIgnoreCellBorder(bean.getIgnoreCellBorder());
			this.setFontSizeFixEnabled(bean.getFontSizeFixEnabled());
			this.setMaximumRowsPerSheet(bean.getMaximumRowsPerSheet());
			this.setXlsFormatPatternsMap(bean.getXlsFormatPatternsMap());
		}
	}
	/**
	 * @return Returns the ignoreGraphics.
	 */
	public Boolean getIgnoreGraphics() {
		return ignoreGraphics;
	}
	/**
	 * @param ignoreGraphics The ignoreGraphics to set.
	 */
	public void setIgnoreGraphics(Boolean ignoreGraphics) {
		this.ignoreGraphics = ignoreGraphics;
	}
	/**
	 * @return Returns the collapseRowSpan.
	 */
	public Boolean getCollapseRowSpan() {
		return collapseRowSpan;
	}
	/**
	 * @param collapseRowSpan The collapseRowSpan to set.
	 */
	public void setCollapseRowSpan(Boolean collapseRowSpan) {
		this.collapseRowSpan = collapseRowSpan;
	}
	/**
	 * @return Returns the ignoreCellBorder.
	 */
	public Boolean getIgnoreCellBorder() {
		return ignoreCellBorder;
	}
	/**
	 * @param ignoreCellBorder The ignoreCellBorder to set.
	 */
	public void setIgnoreCellBorder(Boolean ignoreCellBorder) {
		this.ignoreCellBorder = ignoreCellBorder;
	}
	/**
	 * @return Returns the maximumRowsPerSheet.
	 */
	public Integer getMaximumRowsPerSheet() {
		return maximumRowsPerSheet;
	}
	/**
	 * @param maximumRowsPerSheet The maximumRowsPerSheet to set.
	 */
	public void setMaximumRowsPerSheet(Integer maximumRowsPerSheet) {
		this.maximumRowsPerSheet = maximumRowsPerSheet;
	}
	/**
	 * @return Returns the fontSizeFixEnabled.
	 */
	public Boolean getFontSizeFixEnabled() {
		return fontSizeFixEnabled;
	}
	/**
	 * @param fontSizeFixEnabled The fontSizeFixEnabled to set.
	 */
	public void setFontSizeFixEnabled(Boolean fontSizeFixEnabled) {
		this.fontSizeFixEnabled = fontSizeFixEnabled;
	}
	/**
	 * @return Returns the xlsFormatPatternsMap.
	 */
	public Map getXlsFormatPatternsMap() {
		return xlsFormatPatternsMap;
	}
	/**
	 * @param xlsFormatPatternsMap The xlsFormatPatternsMap to set.
	 */
	public void setXlsFormatPatternsMap(Map xlsFormatPatternsMap) {
		this.xlsFormatPatternsMap = xlsFormatPatternsMap;
	}
	
	
}
