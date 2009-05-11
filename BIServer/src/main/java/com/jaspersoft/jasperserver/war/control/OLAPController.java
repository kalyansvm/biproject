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
package com.jaspersoft.jasperserver.war.control;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * @author swood
 *
 */
public class OLAPController extends JRBaseMultiActionController {

	public ModelAndView viewOlap(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/olap/viewOlap");
	}

	public ModelAndView busy(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/olap/busy");
	}

	public ModelAndView flush(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/olap/flush");
	}

	public ModelAndView properties(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/olap/properties");
	}

	public ModelAndView error(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/olap/error");
	}

}
