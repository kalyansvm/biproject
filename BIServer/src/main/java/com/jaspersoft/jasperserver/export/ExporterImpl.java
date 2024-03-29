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

package com.jaspersoft.jasperserver.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.io.ExportOutput;
import com.jaspersoft.jasperserver.export.modules.ExporterModule;
import com.jaspersoft.jasperserver.export.modules.ExporterModuleContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ExporterImpl.java 12510 2008-03-14 15:20:13Z lucian $
 */

public class ExporterImpl extends BaseExporterImporter implements Exporter {
	
	private static final Log log = LogFactory.getLog(ExporterImpl.class);
	
	protected class ModuleContextImpl implements ExporterModuleContext {
		
		private final String moduleId;
		
		public ModuleContextImpl(final String moduleId) {
			this.moduleId = moduleId;
		}

		public String getCharacterEncoding() {
			return ExporterImpl.this.getCharacterEncoding();
		}

		public ExportTask getExportTask() {
			return task;
		}

		public Element getModuleIndexElement() {
			return getIndexModuleElement(moduleId);
		}
		
	}
	
	private String xmlVersion;
	
	protected ExportTask task;
	protected ExportOutput output;
	
	private Element indexRootElement;
	private Map moduleIndexElements;

	public String getXmlVersion() {
		return xmlVersion;
	}

	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	public Element getIndexRootElement() {
		return indexRootElement;
	}

	protected Element getIndexModuleElement(String moduleId) {
		return (Element) moduleIndexElements.get(moduleId);
	}
	
	public ExportTask getExportTask() {
		return task;
	}
	
	public void setTask(ExportTask task) {
		this.task = task;
		this.output = task == null ? null :  task.getOutput();
	}
	
	public ExportOutput getOutput() {
		return output;
	}

	public void performExport() {
		try {
			output.open();
			boolean close = true;
			try {
				process();
				
				close = false;
				output.close();
			} finally {
				if (close) {
					output.close();
				}
			}		
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void process() {
		Document indexDocument = DocumentHelper.createDocument();
		indexRootElement = indexDocument.addElement(getIndexRootElementName());
		
		setOutputProperties();
		invokeModules();
		
		writeIndexDocument(indexDocument);		
	}

	protected void setOutputProperties() {
		Properties properties = output.getOutputProperties();
		for (Enumeration it = properties.propertyNames(); it.hasMoreElements();) {
			String property = (String) it.nextElement();
			String value = properties.getProperty(property);
			setOutputProperty(property, value);
		}
	}

	protected void setOutputProperty(String property, String value) {
		Element propElement = indexRootElement.addElement(getPropertyElementName());
		propElement.addAttribute(getPropertyNameAttribute(), property);
		if (value != null) {
			propElement.addAttribute(getPropertyValueAttribute(), value);
		}
	}

	protected void invokeModules() {
		List modules = getModuleRegister().getExporterModules();
		
		moduleIndexElements = new HashMap();
		for (Iterator it = modules.iterator(); it.hasNext();) {
			ExporterModule module = (ExporterModule) it.next();
			ModuleContextImpl moduleContext = new ModuleContextImpl(module.getId());

			module.init(moduleContext);
			if (module.toProcess()) {
				commandOut.debug("Module " + module.getId() + " processing");
				
				createModuleElement(module);
				module.process();
			}			
		}
	}

	protected void createModuleElement(ExporterModule module) {		
		Element moduleElement = indexRootElement.addElement(getIndexModuleElementName());
		moduleElement.addAttribute(getIndexModuleIdAttributeName(), module.getId());
		moduleIndexElements.put(module.getId(), moduleElement);
	}

	protected void writeIndexDocument(Document indexDocument) {
		OutputStream indexOut = getIndexOutput();
		boolean closeIndexOut = true;
		try {
			OutputFormat format = new OutputFormat();
			format.setEncoding(getCharacterEncoding());
			XMLWriter writer = new XMLWriter(indexOut, format);
			writer.write(indexDocument);
			
			closeIndexOut = false;
			indexOut.close();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeIndexOut) {
				try {
					indexOut.close();
				} catch (IOException e) {
					log.error("Error while closing index output", e);
				}
			}
		}
	}

	protected OutputStream getIndexOutput() {
		try {
			return output.getFileOutputStream(getIndexFilename());
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}
	
}
