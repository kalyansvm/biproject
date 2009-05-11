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
package com.jaspersoft.jasperserver.war.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CharacterEncodingFilter.java 8408 2007-05-29 23:29:12Z melih $
 */
public class CharacterEncodingFilter implements Filter {

	private String encodingRequestAttrName;
	private CharacterEncodingProvider encodingProvider;

	public void init(FilterConfig arg0) throws ServletException {
		// nothing
	}

	public void destroy() {
		// nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (hasEncoding(request)) {
			chain.doFilter(request, response);
		} else {
			String encoding = getEncoding();
			setEncoding(request, encoding);

			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding(encoding);
			}

			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			CharsetEncodingResponseWrapper responseWrapper = new CharsetEncodingResponseWrapper(httpServletResponse, encoding);
	        chain.doFilter(request, responseWrapper);
		}	
	}

	protected void setEncoding(ServletRequest request, String encoding) {
		request.setAttribute(getEncodingRequestAttrName(), encoding);
	}

	protected boolean hasEncoding(ServletRequest request) {
		return request.getAttribute(getEncodingRequestAttrName()) != null;
	}
	
	protected String getEncoding() {
		return getEncodingProvider().getCharacterEncoding();
	}

	protected static class CharsetEncodingResponseWrapper extends HttpServletResponseWrapper {

		private boolean encodingSpecified = false;
		private final String encoding;

		public CharsetEncodingResponseWrapper(HttpServletResponse response, String encoding) {
			super(response);
			this.encoding = encoding;
		}

		public void setContentType(String type) {
			String encType = type;

			if (!encodingSpecified) {
				String lowerType = type.toLowerCase();
				
				if (lowerType.indexOf("charset") < 0) {
					if (lowerType.startsWith("text/html")) {
						encType = type + "; charset=" + encoding;
					}
				} else {
					encodingSpecified = true;
				}
			}

			super.setContentType(encType);
		}
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	public String getEncodingRequestAttrName() {
		return encodingRequestAttrName;
	}

	public void setEncodingRequestAttrName(String filteredReqAttrName) {
		this.encodingRequestAttrName = filteredReqAttrName;
	}
}
