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

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;

public class ContentResourceImpl extends ResourceImpl implements ContentResource
{
	private String fileType;
	private DataContainer dataContainer;
	private String referenceURI;
	private List resources;

	public ContentResourceImpl()
	{
		super();
		resources = new ArrayList();
	}

	public byte[] getData()
	{
		return dataContainer == null ? null : dataContainer.getData();
	}

	public void setData(byte[] data)
	{
		setDataContainer(new MemoryDataContainer(data));
	}

        public int getSize()
        {
            return dataContainer == null || !hasData() ? 0 : dataContainer.dataSize(); 
        }
        
	public void setDataContainer(DataContainer dataContainer)
	{
		this.dataContainer = dataContainer;
	}

	public void readData(InputStream is)
	{
		setData(StreamUtils.readData(is));
	}

	public InputStream getDataStream()
	{
		return dataContainer == null ? null : dataContainer.getInputStream();
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
		return !isReference() && dataContainer != null && dataContainer.hasData();
	}

	protected Class getImplementingItf() {
		return ContentResource.class;
	}

	public List getResources()
	{
		return resources;
	}

	public void setResources(List resources)
	{
		this.resources = resources;
	}

	public void addChildResource(ContentResource child)
	{
		resources.add(child);
	}
}
