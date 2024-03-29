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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepoFileResource.java 11251 2007-12-14 17:50:43Z lucian $
 * 
 * @hibernate.joined-subclass table="file_resource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoFileResource extends RepoResource {
	private static final Log log = LogFactory.getLog(RepoFileResource.class);
	
	private String fileType;
	private Blob data;
	private RepoFileResource reference;

	public RepoFileResource() {
		super();
	}

	/**
	 * @hibernate.property column="data" type="blob"
	 */
	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	/**
	 * @hibernate.property column="file_type" type="string" length="20"
	 */
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String type) {
		this.fileType = type;
	}
	
	public String getFinalFileType() {
		RepoFileResource res = this;
		while (res.isFileReference()) {
			res = res.getReference();
		}
		return res.getFileType();
	}

	public boolean isFileReference() {
		return getReference() != null;
	}
	
	/**
	 * @hibernate.many-to-one column="reference"
	 */
	public RepoFileResource getReference() {
		return reference;
	}

	public void setReference(RepoFileResource reference) {
		this.reference = reference;
	}

	protected Class getClientItf() {
		return FileResource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		FileResource fileRes = (FileResource) clientRes;

		if (isFileReference()) {
			RepoFileResource ref = getReference();
			fileRes.setFileType(null);
			fileRes.setData(null);
			fileRes.setReferenceURI(ref.getResourceURI());
		} else {
			fileRes.setFileType(getFileType());
			if (hasClientOption(CLIENT_OPTION_FULL_DATA)) {
				copyDataTo(fileRes);
			} else {
				fileRes.setData(null);
			}
			fileRes.setReferenceURI(null);
		}
	}

	protected void copyDataTo(FileResource fileRes) {
		Blob blob = getData();
		if (blob == null) {
			fileRes.setData(null);
		} else {
			try {
				fileRes.readData(blob.getBinaryStream());
			} catch (SQLException e) {
				log.error("Error while reading data blob of \"" + getResourceURI() + "\"", e);
				throw new JSExceptionWrapper(e);
			}
		}
	}

	public FileResourceData copyData() {
		try {
			if (isFileReference()) {
				String quotedResourceURI = "\"" + getResourceURI() + "\"";
				throw new JSException("jsexception.file.resource.is.reference", new Object[] {quotedResourceURI});
			}
			
			FileResourceData resData;

			Blob blob = getData();
			if (blob == null) {
				resData = new FileResourceData((byte[]) null);
			} else {
				resData = new FileResourceData(blob.getBinaryStream());
			}

			return resData;
		} catch (SQLException e) {
			log.error("Error while reading data blob of \"" + getResourceURI() + "\"", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);

		FileResource dataRes = (FileResource) clientRes;
		if (dataRes.isReference()) {
			setFileType(null);
			setData(null);

			RepoFileResource externalReference = (RepoFileResource) referenceResolver
					.getExternalReference(dataRes.getReferenceURI(), RepoFileResource.class);
			
			if (dataRes.getFileType() != null
					&& !dataRes.getFileType().equals(externalReference.getFinalFileType())) {
				throw new JSException("jsexception.file.resource.no.match.type",
						new Object[] {dataRes.getFileType(), externalReference.getFileType()}
						);
			}
			setReference(externalReference);
		} else {
			setFileType(dataRes.getFileType());
			copyDataFrom(dataRes);
			setReference(null);
		}
	}

	protected void copyDataFrom(FileResource dataRes) {
		if (dataRes.isReference()) {
			setData(null);
		} else {
			//only update when the client has set some data
			if (dataRes.hasData()) {
				byte[] clientData = dataRes.getData();
				Blob blob = Hibernate.createBlob(clientData);
				setData(blob);
			}
		}
	}
}
