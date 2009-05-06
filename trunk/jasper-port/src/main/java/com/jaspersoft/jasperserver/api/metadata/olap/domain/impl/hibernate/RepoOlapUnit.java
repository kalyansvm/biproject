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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.impl.hibernate;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import org.hibernate.Hibernate;
import org.hibernate.lob.SerializableBlob;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;

/**
 * @author sbirney
 *
 * @hibernate.joined-subclass table="OlapUnit"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoOlapUnit extends RepoResource {

    private RepoOlapClientConnection olapClientConn = null;
    private String mdxQuery;
    private SerializableBlob olapViewOptions;

    /**
     * @hibernate.many-to-one
     *              column="olapClientConnection"
     *
     */
    public RepoOlapClientConnection getOlapClientConnection() {
	return olapClientConn;
    }

    /**
     *
     */
    public void setOlapClientConnection(RepoOlapClientConnection r) {
	olapClientConn = r;
    }

    /**
     * @hibernate.property column="mdx_query" type="string" length="2000" not-null="true"
     */

    public String getMdxQuery() {
	return mdxQuery;
    }

    public void setMdxQuery(String s) {
	mdxQuery = s;
    }

    public SerializableBlob getOlapViewOptions() {
        return olapViewOptions;
    }

    public void setOlapViewOptions(SerializableBlob sb) {
    	olapViewOptions = sb;
    }

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

        OlapUnit view = (OlapUnit) clientRes;
        view.setOlapClientConnection(getClientReference(getOlapClientConnection(), resourceFactory));
        view.setMdxQuery(getMdxQuery());
        Object viewOptions = null;
        // do not do de-serialization here due to spring network would try to passing thing around.  The de-serialization
        // is done when it's needed (OlapModelController)
        if (getOlapViewOptions() != null) {
            ((OlapUnitImpl)view).setOlapViewOptions(getOlapViewOptions());
        }
    }

    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
        OlapUnit view = (OlapUnit) clientRes;
        copyOlapClientConnection(referenceResolver, view);
        setMdxQuery( view.getMdxQuery() );
        copyOlapViewOptions(view);

    }


    private void copyOlapClientConnection(ReferenceResolver referenceResolver, OlapUnit view) {
	ResourceReference conn = view.getOlapClientConnection();
	RepoOlapClientConnection repoMC
	    = (RepoOlapClientConnection) getReference(conn, RepoOlapClientConnection.class, referenceResolver);
	setOlapClientConnection(repoMC);
    }

    private void copyOlapViewOptions(OlapUnit view) {
        if (((OlapUnitImpl)view).getOlapViewOptions() != null) {
        	// if it's instance of SerializableBlob, then no need to perform the serialization, this is for the repo admin flow
        	if (!(((OlapUnitImpl)view).getOlapViewOptions() instanceof SerializableBlob)) {
    	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(baos));
    	        e.writeObject(((OlapUnitImpl)view).getOlapViewOptions());
    	        e.flush();
    	        e.close();
    	        setOlapViewOptions((SerializableBlob)Hibernate.createBlob(baos.toByteArray()));
        	}
        }
    }

    protected Class getClientItf() {
	return OlapUnit.class;
    }



}
