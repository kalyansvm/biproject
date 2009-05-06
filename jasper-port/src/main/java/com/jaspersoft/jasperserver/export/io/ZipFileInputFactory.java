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

package com.jaspersoft.jasperserver.export.io;

import java.util.Properties;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.export.Parameters;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ZipFileInputFactory.java 12510 2008-03-14 15:20:13Z lucian $
 */
public class ZipFileInputFactory implements ImportInputFactory, ZipFileInputManager {

	private String inputZipParameter;
	private PathProcessorFactory pathProcessorFactory;
	private String propertyPathProcessorId;

	public boolean matches(Parameters parameters) {
		return parameters.hasParameter(inputZipParameter);
	}
	
	public ImportInput createInput(Parameters parameters) {
		String zipFile = parameters.getParameterValue(inputZipParameter);
		if (zipFile == null) {
			throw new JSException("jsexception.no.import.zip.file.specified");
		}
		
		String processorId = pathProcessorFactory.getDefaultInputProcessor();
		PathProcessor processor = pathProcessorFactory.getProcessor(processorId);
		return new ZipFileInput(zipFile, processor, this);
	}

	public void updateInputProperties(ZipFileInput input, Properties properties) {
		String pathProcessorId = properties.getProperty(propertyPathProcessorId);
		if (pathProcessorId != null && !pathProcessorId.equals(pathProcessorFactory.getDefaultInputProcessor())) {
			PathProcessor processor = pathProcessorFactory.getProcessor(pathProcessorId);
			input.setPathProcessor(processor);
		}
	}

	public String getInputZipParameter() {
		return inputZipParameter;
	}

	public void setInputZipParameter(String inputDirParameter) {
		this.inputZipParameter = inputDirParameter;
	}

	public PathProcessorFactory getPathProcessorFactory() {
		return pathProcessorFactory;
	}

	public void setPathProcessorFactory(PathProcessorFactory pathProcessorFactory) {
		this.pathProcessorFactory = pathProcessorFactory;
	}

	public String getPropertyPathProcessorId() {
		return propertyPathProcessorId;
	}

	public void setPropertyPathProcessorId(String propertyPathProcessorId) {
		this.propertyPathProcessorId = propertyPathProcessorId;
	}

}
