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
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.modules.Attributes;
import com.jaspersoft.jasperserver.export.modules.ImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.MapAttributes;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ImporterImpl.java 12757 2008-03-31 16:58:16Z lucian $
 */
public class ImporterImpl extends BaseExporterImporter implements Importer {
	
	private static final Log log = LogFactory.getLog(ImporterImpl.class);

	protected class ModuleContextImpl implements ImporterModuleContext {
		
		private final Element moduleElement;
		private final Attributes attributes;

		public ModuleContextImpl(final Element moduleElement, 
				final Attributes attributes) {
			this.moduleElement = moduleElement;
			this.attributes = attributes;
		}

		public String getCharacterEncoding() {
			return ImporterImpl.this.getCharacterEncoding();
		}

		public ImportTask getImportTask() {
			return task;
		}

		public Element getModuleIndexElement() {
			return moduleElement;
		}

		public Attributes getAttributes() {
			return attributes;
		}
		
	}
	
	protected ImportTask task;
	protected ImportInput input;

	public void setTask(ImportTask task) {
		this.task = task;
		this.input = task == null ? null : task.getInput();
	}

	public void performImport() {
		try {
			input.open();
			boolean close = true;
			try {
				process();
				
				close = false;
				input.close();
			} finally {
				if (close) {
					input.close();
				}
			}			
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void process() {
		Document indexDocument = readIndexDocument();
		Element indexRoot = indexDocument.getRootElement();
		
		Properties properties = new Properties();
		for (Iterator it = indexRoot.elementIterator(getPropertyElementName()); it.hasNext(); ) {
			Element propElement = (Element) it.next();
			String propKey = propElement.attribute(getPropertyNameAttribute()).getValue();
			Attribute valueAttr = propElement.attribute(getPropertyValueAttribute());
			String value = valueAttr == null ? null : valueAttr.getValue();
			properties.setProperty(propKey, value);
		}
		input.propertiesRead(properties);
		
		Attributes contextAttributes = createContextAttributes();
		
		for (Iterator it = indexRoot.elementIterator(getIndexModuleElementName()); it.hasNext(); ) {
			Element moduleElement = (Element) it.next();
			String moduleId = moduleElement.attribute(getIndexModuleIdAttributeName()).getValue();
			ImporterModule module = getModuleRegister().getImporterModule(moduleId);
			if (module == null) {
				throw new JSException("jsexception.import.module.not.found", new Object[] {moduleId});
			}

			commandOut.debug("Invoking module " + module);
			
			ModuleContextImpl moduleContext = new ModuleContextImpl(moduleElement, 
					contextAttributes);
			module.init(moduleContext);
			module.process();
		}
	}

	protected Attributes createContextAttributes() {
		Attributes contextAttributes = new MapAttributes();
		return contextAttributes;
	}

	protected Document readIndexDocument() {
		InputStream indexInput = getIndexInput();
		boolean close = true;
		try {
			SAXReader reader = new SAXReader();
			reader.setEncoding(getCharacterEncoding());
			Document document = reader.read(indexInput);
			
			close = false;
			indexInput.close();
			
			return document;
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} catch (DocumentException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					indexInput.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}		
	}

	protected InputStream getIndexInput() {
		try {
			return input.getFileInputStream(getIndexFilename());
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

}
