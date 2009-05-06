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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRRenderable;
import net.sf.jasperreports.engine.JRWrappingSvgRenderer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRTypeSniffer;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.AbstractView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

/**
 * Controller for JasperServer report images.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportImageController.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportImageController  implements Controller
{

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String jasperPrintNameParameter;
	private String imageNameParameter;

	protected static class ImageView extends AbstractView {

		private final String imageMimeType;
		private final byte[] imageData;

		public ImageView(final String imageMimeType, final byte[] imageData) {
			this.imageMimeType = imageMimeType;
			this.imageData = imageData;
		}

		protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (imageData != null && imageData.length > 0) {
				if (imageMimeType != null) {
					response.setHeader("Content-Type", imageMimeType);
				}
				response.setContentLength(imageData.length);
				ServletOutputStream ouputStream = response.getOutputStream();
				ouputStream.write(imageData, 0, imageData.length);
				ouputStream.flush();
				ouputStream.close();
			} else {
				response.getOutputStream().close();
			}
		}
		
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws JRException
	{
		String jasperPrintName = request.getParameter(getJasperPrintNameParameter());
		ReportUnitResult result = (ReportUnitResult) getJasperPrintAccessor().getObject(request, jasperPrintName);
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
		}
		
		byte[] imageData = null;
		String imageMimeType = null;

		String imageName = request.getParameter(getImageNameParameter());
		if ("px".equals(imageName))
		{
			JRRenderable pxRenderer = 
				JRImageRenderer.getInstance(
					"net/sf/jasperreports/engine/images/pixel.GIF",
					JRImage.ON_ERROR_TYPE_ERROR
					);
			imageMimeType = JRRenderable.MIME_TYPE_GIF;
			imageData = pxRenderer.getImageData();
		}
		else
		{
			JRPrintImage image = JRHtmlExporter.getImage(Arrays.asList(new JasperPrint[]{jasperPrint}), imageName);
			
			JRRenderable renderer = image.getRenderer();
			if (renderer.getType() == JRRenderable.TYPE_SVG)
			{
				renderer = 
					new JRWrappingSvgRenderer(
						renderer, 
						new Dimension(image.getWidth(), image.getHeight()),
						JRElement.MODE_OPAQUE == image.getMode() ? image.getBackcolor() : null
						);
			}

			imageMimeType = JRTypeSniffer.getImageMimeType(renderer.getImageType());
			imageData = renderer.getImageData();
		}

		View view = new ImageView(imageMimeType, imageData);
		return new ModelAndView(view);
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	public String getJasperPrintNameParameter() {
		return jasperPrintNameParameter;
	}

	public void setJasperPrintNameParameter(String jasperPrintNameAttribute) {
		this.jasperPrintNameParameter = jasperPrintNameAttribute;
	}

	public String getImageNameParameter() {
		return imageNameParameter;
	}

	public void setImageNameParameter(String imageNameParameter) {
		this.imageNameParameter = imageNameParameter;
	}

}
