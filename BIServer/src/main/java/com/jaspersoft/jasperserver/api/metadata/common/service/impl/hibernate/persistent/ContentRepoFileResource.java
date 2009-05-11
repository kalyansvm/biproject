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

import com.jaspersoft.jasperserver.api.JSException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileBufferedDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import org.hibernate.lob.BlobImpl;
import org.hibernate.lob.SerializableBlob;


/**
* @hibernate.joined-subclass table="content_file"
* @hibernate.joined-subclass-key column="id"
*/
public class ContentRepoFileResource extends RepoResource
{
	private static final Log log = LogFactory.getLog(ContentRepoFileResource.class);

		private String fileType;

		private Blob data;


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


	protected void copyDataFrom(ContentResource dataRes) {
			if (dataRes.isReference()) {
				setData(null);
			} else {
				//only update when the client has set some data
				if (dataRes.hasData()) {
                                        Blob blob;
                                        /* ContentResourceImpl uses a dataContainer that can either have content
                                         * completely in memory or have part of the content stored on disk.
                                         */ 
                                        try {
                                            blob = Hibernate.createBlob(dataRes.getDataStream());
                                        } catch (IOException ex) {
                                            throw new JSException(ex);
                                        }
                                        setData(blob);
				}
			}
		}

//	public void set(ContentResource file)
//	{
//		copyDataFrom(file);
//		setDescription(file.getDescription());
//		setFileType(file.getFileType());
//		setLabel(file.getLabel());
//		setName(file.getName());
//	}

	public FileResourceData copyData() {
		try {
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

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		ContentResource resource = (ContentResource) clientRes;

		resource.setFileType(getFileType());
		if (hasClientOption(CLIENT_OPTION_FULL_DATA)) {
			copyDataTo(resource);
		} else {
			resource.setDataContainer(null);
		}
		resource.setReferenceURI(null);

		RepoFolder childrenFolder = getChildrenFolder();

		if (childrenFolder != null) {
			Set resList = childrenFolder.getChildren();
			for (Iterator it = resList.iterator(); it.hasNext();) {
				ContentRepoFileResource fileRes = (ContentRepoFileResource) it.next();
				resource.addChildResource((ContentResource) fileRes.toClient(resourceFactory));
			}
		}

	}

	protected void copyDataTo(ContentResource resource) {
		Blob blob = getData();
		if (blob == null) {
			resource.setDataContainer(null);
		} else {
			try {
				FileBufferedDataContainer dataContainer = new FileBufferedDataContainer();
				StreamUtils.pipeData(blob.getBinaryStream(), dataContainer);
				resource.setDataContainer(dataContainer);
			} catch (SQLException e) {
				log.error("Error while reading data blob of \"" + getResourceURI() + "\"", e);
				throw new JSExceptionWrapper(e);
			}
		}
	}

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);

		ContentResource dataRes = (ContentResource) clientRes;
		setFileType(dataRes.getFileType());
		copyDataFrom(dataRes);
		copyResources(referenceResolver, (ContentResource) clientRes);
	}


	private void copyResources(ReferenceResolver referenceResolver, ContentResource resource) {
		List clientResources = resource.getResources();
		if (clientResources != null && !clientResources.isEmpty()) {
			for (Iterator it = clientResources.iterator(); it.hasNext();) {
				ContentResource childResource = (ContentResource) it.next();
				getReference(childResource, RepoFileResource.class, referenceResolver);
			}
		}
	}


	protected Class getClientItf()
	{
		return ContentResource.class;
	}
}
