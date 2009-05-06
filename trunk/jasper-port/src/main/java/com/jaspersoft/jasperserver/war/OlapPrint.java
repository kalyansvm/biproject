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
package com.jaspersoft.jasperserver.war;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.logger.NullLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Options;
import org.apache.fop.configuration.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.tonbeller.jpivot.chart.ChartComponent;
import com.tonbeller.jpivot.print.PrintComponent;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.component.RendererParameters;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestContextFactoryFinder;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * 
 * Based on the PrintServlet in JPivot. Download Excel and PDF files
 * based on the current OLAP view
 * 
 * @author swood
 *
 */
public class OlapPrint extends HttpServlet {
	
    private static final Log logger = LogFactory.getLog(OlapPrint.class);
	
	private static int XLS = 0;
	private static int PDF = 1;
	String filename;
	Resources resources = Resources.instance(OlapPrint.class);
    Driver driver;

	/**
	 * 
	 */
	public OlapPrint() {
		super();
	}

	  /** Initializes the servlet.
	   */
	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    /*
	     * TODO Add init parameters for:
	     * 		ChartServlet path,
	     * 		Excel XSL and content type,
	     *		PDF XSL and content type
	     */
	    try {
	        // set base FOP FONT directory.  The font config  stuff will be looked for here
	        Configuration.put("fontBaseDir", config.getServletContext().getRealPath("/WEB-INF/jpivot/print/"));
	        logger.debug("fontBaseDir=" + Configuration.getStringValue("fontBaseDir"));
	        // get the physical path for the config file
	        String fopConfigPath = config.getServletContext().getRealPath("/WEB-INF/jpivot/print/userconfig.xml");
	        // load the user proerties, contining the CustomFont font.
	        logger.debug("fopConfigPath=" + fopConfigPath);
	        new Options(new File(fopConfigPath));
		    logger.debug("Construct driver");
		    driver = new Driver();
		    driver.setLogger(new NullLogger());
		    logger.debug("Setup Renderer (output format)");
		    driver.setRenderer(Driver.RENDER_PDF);
	        logger.debug("FOP renderer set to PDF");
	      } catch (FOPException e) {
	        e.printStackTrace();
	        logger.info("FOP user config file not loaded");
	      } catch (Exception e) {
	        e.printStackTrace();
	        logger.info("FOP user config file not loaded");
	      }
	  }

	  /** Destroys the servlet.
	   */
	  public void destroy() {

	  }

	  /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	   * 
	   * @param request servlet request
	   * @param response servlet response
	   */

	  protected void processRequest(RequestContext context) throws ServletException, IOException {
	    HttpServletRequest request = context.getRequest();
	    HttpServletResponse response = context.getResponse();
        HttpSession session = request.getSession();

        String type = request.getParameter("type");
        int identifiedType = -1;
        boolean xslCache = true;
        
        String view = request.getParameter("view");
        
	    if (view == null || type == null) {
	    	throw new ServletException("view and type parameters not supplied");
	    }
	    //context.getFileParameters();
	    try {
	        String xslUri = null;
	        if (type.equalsIgnoreCase("XLS")) {
	          xslUri = "/WEB-INF/jpivot/table/xls_mdxtable.xsl";
	          RendererParameters.setParameter(context.getRequest(), "mode", "excel", "request");
	          response.setContentType("application/vnd.ms-excel");
	          filename = "xls_export.xls";
	          identifiedType = XLS;
	        } else if (type.equalsIgnoreCase("PDF")) {
	          xslUri = "/WEB-INF/jpivot/table/fo_mdxtable.xsl";
	          RendererParameters.setParameter(context.getRequest(), "mode", "print", "request");
	          response.setContentType("application/pdf");
	          filename = "xls_export.pdf";
	          identifiedType = PDF;
	        } else {
	        	throw new ServletException("Unknown file type: " + type);
	        }

	          
	        // Get references to needed elements. We expect them to be in the current
	        // session
	          
	        String tableRef = view + "/table";
	        String chartRef = view + "/chart";
	        String printRef = view + "/print";

	        // get TableComponent
	        TableComponent table = (TableComponent) context.getModelReference(tableRef);
	        // only proceed if table component exists
	        if (table == null) {
	        	return;
	        }
	          
	        Map parameters = getPrintParameters(printRef, context);
	        parameters.putAll(getChartParameters(chartRef, request));

	        //parameters.put("message",table.getReportTitle());
	        // add "context" and "renderId" to parameter map

	        //parameters.put("renderId", renderId);
	        parameters.put("context", context.getRequest().getContextPath());

	        // Some FOP-PDF versions require a complete URL, not a path
	        //parameters.put("contextUrl", createContextURLValue(context));

	        // Get table rendered as XML
	            
	        StringWriter sw = getRenderedTableDOM(table, parameters, session, context, xslUri, xslCache);

		    OutputStream outStream = response.getOutputStream();
		    PrintWriter out = new PrintWriter(outStream);
		    
		    // set up filename for download.
		    response.setHeader("Content-Disposition", "attachment; filename=" + filename);

	        // if this is XLS, then we are done, so output xml file.
	        if (identifiedType == XLS) {
	            logger.debug("Creating XLS");
	            response.setContentLength(sw.toString().length());
	            out.write(sw.toString());
	            RendererParameters.removeParameter(context.getRequest(), "mode", "excel", "request");
	        } else {
	            // if this is PDF, then need to generate PDF from the FO xml
	          	logger.debug("Creating PDF");
	            try {
	            	logger.debug(
        				resources.getString("jpivot.PrintServlet.message.encoding", new Object[]{resources.getCharacterEncoding()}));
		            ByteArrayInputStream bain = new ByteArrayInputStream(sw.toString()
		                .getBytes(resources.getEncodingProvider().getCharacterEncoding()));
		            ByteArrayOutputStream baout = new ByteArrayOutputStream(16384);
		            // process FO to PDF
		            convertFO2PDF(bain, baout);
		            final byte[] content = baout.toByteArray();
		            response.setContentLength(content.length);
		            outStream.write(content);
		            RendererParameters
	                    	.removeParameter(context.getRequest(), "mode", "print", "request");
	            } catch (Exception e) {
	              	logger.error(e);
	            }
	        }
	        
	        //close output streams
	        out.flush();
	        out.close();
	        outStream.flush();
	        
	    } catch (Exception e) {
	        logger.error(e);
        	throw new ServletException(e);
	    }
	  }

	/**
	 * Set parameters for printing based on defaults + settings through the
	 * print parameters form
	 * 
	 * @param printRef
	 * @param context
	 * @return
	 */
	private Map getPrintParameters(String printRef, RequestContext context) {
        Map parameters = new HashMap();
          
        // add parameters from printConfig
        PrintComponent printConfig = (PrintComponent) context.getModelReference(printRef);
        if (printConfig == null) {
        	return parameters;
        }
        if (printConfig.isSetTableWidth()) {
        	parameters.put(printConfig.PRINT_TABLE_WIDTH, new Double(printConfig
                  .getTableWidth()));
        }
        if (printConfig.getReportTitle().trim().length() != 0) {
        	parameters.put(printConfig.PRINT_TITLE, printConfig.getReportTitle().trim());
        }
        parameters.put(printConfig.PRINT_PAGE_ORIENTATION, printConfig.getPageOrientation());
        parameters.put(printConfig.PRINT_PAPER_TYPE, printConfig.getPaperType());
        if (printConfig.getPaperType().equals(
      		  resources.getString("jsp.wcf.print.custom"))) {
            parameters
                  .put(printConfig.PRINT_PAGE_WIDTH, new Double(printConfig.getPageWidth()));
            parameters.put(printConfig.PRINT_PAGE_HEIGHT, new Double(printConfig
                  .getPageHeight()));
        }
        parameters.put(printConfig.PRINT_CHART_PAGEBREAK, new Boolean(printConfig
                  .isChartPageBreak()));

        return parameters;
	  }

	/**
	 * Set parameters to include the chart in the printed Excel or PDF
	 * 
	 * @param chartRef
	 * @param request
	 * @return
	 */
	private Map getChartParameters(String chartRef, HttpServletRequest request) {
		  Map parameters = new HashMap();
		  
          // add parameters and image from chart if visible
          ChartComponent chart = (ChartComponent) request.getSession().getAttribute(chartRef);
          if (chart == null || !chart.isVisible()) {
        	  return parameters;
          }

          String host = request.getServerName();
          int port = request.getServerPort();
          String location = request.getContextPath();
          String scheme = request.getScheme();

          String chartServlet = scheme + "://" + host + ":" + port + location + "/GetChart";
          parameters.put("chartimage", chartServlet + "?filename=" + chart.getFilename());
          parameters.put("chartheight", new Integer(chart.getChartHeight()));
          parameters.put("chartwidth", new Integer(chart.getChartWidth()));
	  	  return parameters;
	}
	  
	/**
	 * Get the XML rendition of the table component based on the given parameters
	 * 
	 * @param table
	 * @param parameters
	 * @param session
	 * @param context
	 * @param xslUri
	 * @param xslCache
	 * @return
	 * @throws Exception
	 */
	private StringWriter getRenderedTableDOM(TableComponent table,
			  									Map parameters, 
			  									HttpSession session, 
			  									RequestContext context, 
			  									String xslUri, 
			  									boolean xslCache) throws Exception {
          
          table.setDirty(true);
          Document document = table.render(context);
          table.setDirty(true);

          DOMSource source = new DOMSource(document);
          // set up xml transformation
          Transformer transformer = XmlUtils.getTransformer(session, xslUri, xslCache);
          for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            Object value = parameters.get(name);
            transformer.setParameter(name, value);
          }
          StringWriter sw = new StringWriter();
          StreamResult result = new StreamResult(sw);
          //do transform
          transformer.transform(source, result);
          sw.flush();
          
          return sw;

	  }
	
	  /**
	   * converts FO xml into PDF using the FOP processor
	   */
	  public void convertFO2PDF(ByteArrayInputStream bain, ByteArrayOutputStream baout)
	      throws IOException, FOPException {
			
	    driver.setOutputStream(baout);
	    logger.debug("Setup input");
	    driver.setInputSource(new InputSource(bain));

	    logger.debug("Process FO");
	    driver.run();
	    logger.debug("PDF file generation completed");
	  }

	  /** Handles the HTTP <code>GET</code> method.
	   * @param request servlet request
	   * @param response servlet response
	   */
	  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
	    doPost(request, response);
	  }

	  /** Handles the HTTP <code>POST</code> method.
	   * @param request servlet request
	   * @param response servlet response
	   */
	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
	    RequestContext context = RequestContextFactoryFinder.createContext(request, response, true);
	    try {
	      processRequest(context);
	    } finally {
	      context.invalidate();
	    }
	  }

	  /** Returns a short description of the servlet.
	   */
	  public String getServletInfo() {
	    return "Export OLAP table to Excel or PDF";
	  }

}
