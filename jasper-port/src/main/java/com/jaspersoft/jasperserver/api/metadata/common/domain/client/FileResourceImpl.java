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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileResourceImpl.java 12757 2008-03-31 16:58:16Z lucian $
 */
public class FileResourceImpl extends ResourceImpl implements FileResource
{
	private String fileType;
	private byte[] data;
	private String referenceURI;

	public FileResourceImpl()
	{
		super();
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}

	public void readData(InputStream is)
	{
		setData(StreamUtils.readData(is));
	}

	public InputStream getDataStream()
	{
		return data == null ? null : new ByteArrayInputStream(data);
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public boolean isReference() {
		return referenceURI != null && referenceURI.length() > 0;
	}

	public String getReferenceURI() {
		return referenceURI;
	}

	public void setReferenceURI(String referenceURI) {
		this.referenceURI = referenceURI;
	}

	public boolean hasData() {
		//empty array is considered no data
		return !isReference() && data != null && data.length > 0;
	}

	protected Class getImplementingItf() {
		return FileResource.class;
	}
	
	public boolean isSameType(Resource resource) {
		boolean same = super.isSameType(resource);
		if (same) {
			FileResource fileRes = (FileResource) resource;
			String resType = fileRes.getFileType();
			String type = getFileType();
			if (type != null && resType != null  && !type.equals(resType)) {
				same = false;
			}
		}
		return same;
	}
}
