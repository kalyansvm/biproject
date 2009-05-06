/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.war.tags;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.war.dto.RuntimeInputControlWrapper;

/**
 * @author Lucian Chirita
 *
 */
public class InputControlLabelTag extends BaseTagSupport {
	
	protected static final Log log = LogFactory.getLog(InputControlLabelTag.class);

	public static final String DEFAULT_LABEL_JSP = "/WEB-INF/jsp/InputControlLabel.jsp";
	public static final String INPUT_CONTROL_ATTRIBUTE_NAME = "control";
	
	private RuntimeInputControlWrapper control;
	private String labelJsp;
	
	protected int doStartTagInternal() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		Map attributes = new HashMap();
		attributes.put(INPUT_CONTROL_ATTRIBUTE_NAME, getControl());
		includeNested(getLabelJsp(), attributes);
		return EVAL_PAGE;
	}

	public RuntimeInputControlWrapper getControl() {
		return control;
	}

	public void setControl(RuntimeInputControlWrapper control) {
		this.control = control;
	}

	public String getLabelJsp() {
		String jsp = labelJsp;
		if (jsp == null) {
			jsp = DEFAULT_LABEL_JSP;
		}
		return jsp;
	}

	public void setLabelJsp(String labelJsp) {
		this.labelJsp = labelJsp;
	}
}
