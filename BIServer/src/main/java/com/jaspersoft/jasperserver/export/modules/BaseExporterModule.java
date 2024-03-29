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
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.io.ExportOutput;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseExporterModule.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class BaseExporterModule implements ExporterModule {

	private static final Log log = LogFactory.getLog(BaseExporterModule.class);
	
	protected static final CommandOut commandOut = CommandOut.getInstance();
	
	private String id;
	private String everythingArg;
	
	protected ExporterModuleContext exportContext;
	protected Parameters exportParams;
	protected String characterEncoding;
	protected ExportOutput output;
	protected ExecutionContext executionContext;
	protected boolean exportEverything;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void init(ExporterModuleContext moduleContext) {
		this.exportContext = moduleContext;
		this.exportParams = moduleContext.getExportTask().getParameters();
		this.characterEncoding = moduleContext.getCharacterEncoding();
		this.output = moduleContext.getExportTask().getOutput();
		this.executionContext = moduleContext.getExportTask().getExecutionContext();
		this.exportEverything = isExportEverything();
	}

	protected boolean isExportEverything() {
		return exportParams.hasParameter(everythingArg);
	}

	public boolean toProcess() {
		return exportEverything || isToProcess();
	}
	
	protected abstract boolean isToProcess();

	protected boolean hasParameter(String name) {
		return exportParams.hasParameter(name);
	}
	
	protected String getParameterValue(String name) {
		return exportParams.getParameterValue(name);
	}
	
	protected String[] getParameterValues(String name) {
		return exportParams.getParameterValues(name);
	}

	protected Element getIndexElement() {
		return exportContext.getModuleIndexElement();
	}
	
	protected final void serialize(Object object, String parentPath, String fileName, ObjectSerializer serializer) {
		OutputStream out = getFileOutput(parentPath, fileName);
		boolean closeOut = true;
		try {
			serializer.write(object, out, exportContext);
			
			closeOut = false;
			out.close();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeOut) {
				try {
					out.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	protected final OutputStream getFileOutput(String parentPath, String fileName) {
		try {
			return output.getFileOutputStream(parentPath, fileName);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}
	
	protected final void writeData(InputStream input, String parentPath, String fileName) {
		OutputStream out = getFileOutput(parentPath, fileName);
		boolean closeOut = true;
		try {
			StreamUtils.pipeData(input, out);
			
			closeOut = false;
			out.close();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeOut) {
				try {
					out.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
	
	protected final void mkdir(String path) {
		try {
			output.mkdir(path);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}
	
	protected final String mkdir(String parentPath, String path) {
		try {
			return output.mkdir(parentPath, path);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	public String getEverythingArg() {
		return everythingArg;
	}

	public void setEverythingArg(String everythingArg) {
		this.everythingArg = everythingArg;
	}
}
