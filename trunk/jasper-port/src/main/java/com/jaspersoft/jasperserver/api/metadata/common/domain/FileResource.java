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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.InputStream;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileResource.java 12128 2008-02-15 23:22:08Z asokolnikov $
 */
public interface FileResource extends Resource
{
	String TYPE_IMAGE = "img";
	String TYPE_FONT = "font";
	String TYPE_JRXML = "jrxml";
	String TYPE_JAR = "jar";
	String TYPE_RESOURCE_BUNDLE = "prop";
	String TYPE_STYLE_TEMPLATE = "jrtx";
	String TYPE_XML = "xml";

	boolean hasData();

	InputStream getDataStream();
	
	void readData(InputStream is);
	
	byte[] getData();
	
	void setData(byte[] data);
	
	String getFileType();
	
	void setFileType(String fileType);
	
	boolean isReference();
	
	String getReferenceURI();
	
	void setReferenceURI(String referenceURI);
}
