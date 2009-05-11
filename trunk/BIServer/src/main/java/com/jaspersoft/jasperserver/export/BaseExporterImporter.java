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

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.export.modules.ModuleRegister;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseExporterImporter.java 12510 2008-03-14 15:20:13Z lucian $
 */

public class BaseExporterImporter {
	
	protected static final CommandOut commandOut = CommandOut.getInstance();
	
	private String indexFilename;
	private String indexRootElementName;
	private String indexModuleElementName;
	private String indexModuleIdAttributeName;
	private ModuleRegister moduleRegister;
	private CharacterEncodingProvider encodingProvider;
	private String propertyElementName;
	private String propertyNameAttribute;
	private String propertyValueAttribute;

	public String getIndexRootElementName() {
		return indexRootElementName;
	}

	public void setIndexRootElementName(String indexRootElement) {
		this.indexRootElementName = indexRootElement;
	}

	public String getIndexModuleElementName() {
		return indexModuleElementName;
	}

	public void setIndexModuleElementName(String indexModuleElementName) {
		this.indexModuleElementName = indexModuleElementName;
	}

	public String getIndexModuleIdAttributeName() {
		return indexModuleIdAttributeName;
	}

	public void setIndexModuleIdAttributeName(String indexModuleIdAttributeName) {
		this.indexModuleIdAttributeName = indexModuleIdAttributeName;
	}

	public ModuleRegister getModuleRegister() {
		return moduleRegister;
	}

	public void setModuleRegister(ModuleRegister moduleRegister) {
		this.moduleRegister = moduleRegister;
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}
	
	public String getCharacterEncoding() {
		return encodingProvider.getCharacterEncoding();
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	public String getIndexFilename() {
		return indexFilename;
	}

	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}

	public String getPropertyElementName() {
		return propertyElementName;
	}

	public void setPropertyElementName(String propertyElementName) {
		this.propertyElementName = propertyElementName;
	}

	public String getPropertyNameAttribute() {
		return propertyNameAttribute;
	}

	public void setPropertyNameAttribute(String propertyNameAttribute) {
		this.propertyNameAttribute = propertyNameAttribute;
	}

	public String getPropertyValueAttribute() {
		return propertyValueAttribute;
	}

	public void setPropertyValueAttribute(String propertyValueAttribute) {
		this.propertyValueAttribute = propertyValueAttribute;
	}
	
}
