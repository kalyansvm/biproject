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

package com.jaspersoft.jasperserver.war.action.hyperlinks;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocalPageHyperlinkProducerFactory.java 8408 2007-05-29 23:29:12Z melih $
 */
public class LocalPageHyperlinkProducerFactory implements HyperlinkProducerFlowFactory, JRHyperlinkProducer, Serializable {

	private static final long serialVersionUID = 1L;

	private String navigateToPageFunction;
	
	public JRHyperlinkProducer getHyperlinkProducer(HttpServletRequest request, HttpServletResponse response) {
		return this;
	}
	
	public String getHyperlink(JRPrintHyperlink hyperlink) {
		Integer page = hyperlink.getHyperlinkPage();
		String ref;
		if (page == null) {
			ref = null;
		} else {
			int pageIdx = page.intValue() - 1;
			ref = "javascript:" + getNavigateToPageFunction() + "(" + pageIdx + ");";
		}
		return ref;
	}

	public String getNavigateToPageFunction() {
		return navigateToPageFunction;
	}

	public void setNavigateToPageFunction(String navigateToPageFunction) {
		this.navigateToPageFunction = navigateToPageFunction;
	}

}
