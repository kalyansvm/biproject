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

package com.jaspersoft.jasperserver.export.modules;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseImporterModule.java 12757 2008-03-31 16:58:16Z lucian $
 */
public abstract class BaseImporterModule implements ImporterModule {

	private static final Log log = LogFactory.getLog(BaseImporterModule.class);
	
	protected static final CommandOut commandOut = CommandOut.getInstance();
	
	private String id;
	
	protected ImporterModuleContext importContext;
	protected Parameters params;
	protected ExecutionContext executionContext;
	protected ImportInput input;
	protected Element indexElement;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void init(ImporterModuleContext moduleContext) {
		this.importContext = moduleContext;
		this.params = moduleContext.getImportTask().getParameters();
		this.executionContext = moduleContext.getImportTask().getExecutionContext();
		this.input = moduleContext.getImportTask().getInput();
		this.indexElement = moduleContext.getModuleIndexElement();
	}
	
	protected String getParameterValue(String name) {
		return params.getParameterValue(name);
	}
	
	protected boolean hasParameter(String name) {
		return params.hasParameter(name);
	}
	
	protected final Object deserialize(String parentPath, String fileName, ObjectSerializer serializer) {
		InputStream in = getFileInput(parentPath, fileName);
		boolean closeIn = true;
		try {
			Object object = serializer.read(in, importContext);
			
			closeIn = false;
			in.close();
			
			return object;
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeIn) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	protected InputStream getFileInput(String parentPath, String fileName) {
		try {
			return input.getFileInputStream(parentPath, fileName);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected Attributes getContextAttributes() {
		return importContext.getAttributes();
	}
}
