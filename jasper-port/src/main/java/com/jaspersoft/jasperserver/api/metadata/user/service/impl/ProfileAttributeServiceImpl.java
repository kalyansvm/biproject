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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;

import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
        
/**
 *
 * @author sbirney
 *
 */
public class ProfileAttributeServiceImpl extends HibernateDaoImpl
    implements ProfileAttributeService, PersistentObjectResolver {

    protected static final Log log = 
	LogFactory.getLog(ProfileAttributeServiceImpl.class);

    private HibernateRepositoryService repoService;
    private UserAuthorityService userService;

    private ResourceFactory objectFactory;
    private ResourceFactory persistentClassFactory;

    private ApplicationContext appContext;
    
    /**
     * @return Returns the repoService.
     */
    public HibernateRepositoryService getRepositoryService() {
	return repoService;
    }

    /**
     * @param repoService The repoService to set.
     */
    public void setRepositoryService(HibernateRepositoryService repoService) {
	this.repoService = repoService;
    }

    /**
     * @return Returns the userService.
     */
    public UserAuthorityService getUserAuthorityService() {
    	return userService;
    }

    /**
     * @param userService The userService to set.
     */
    public void setUserAuthorityService(UserAuthorityService userService) {
      this.userService = userService;
    }

    public ResourceFactory getObjectMappingFactory() {
	return objectFactory;
    }

    public void setObjectMappingFactory(ResourceFactory objectFactory) {
	this.objectFactory = objectFactory;
    }

    public ResourceFactory getPersistentClassFactory() {
	return persistentClassFactory;
    }

    public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
	this.persistentClassFactory = persistentClassFactory;
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
	// Not needed really?
	appContext = arg0;
    }
   
    public ProfileAttribute newProfileAttribute(ExecutionContext context) {
	return (ProfileAttribute) getObjectMappingFactory().newObject(ProfileAttribute.class);
    }

    public void putProfileAttribute(ExecutionContext context, 
				    ProfileAttribute attr) {
	RepoProfileAttribute existingAttr = getRepoProfileAttribute(context, attr);
	if (existingAttr == null) {
	    existingAttr = (RepoProfileAttribute) 
		getPersistentClassFactory().newObject(ProfileAttribute.class);
	}
	existingAttr.copyFromClient(attr, this);
	getHibernateTemplate().saveOrUpdate(existingAttr);
    }

    public ProfileAttribute getProfileAttribute(ExecutionContext context, 
						ProfileAttribute attr) {
	// Given the object and the recipient, find the permission
	RepoProfileAttribute existingPerm = getRepoProfileAttribute(context, attr);
	if (existingPerm == null) {
	    return null;
	} else {
	    return (ProfileAttribute) existingPerm.toClient(getObjectMappingFactory());
	}
    }

    private RepoProfileAttribute getRepoProfileAttribute(ExecutionContext context, 
							 ProfileAttribute attr) {
	// Given the principal find the attributes
	final IdedObject principalObject = 
	    (IdedObject) getPersistentObject(attr.getPrincipal());

	final String attrClassName = 
	    getPersistentClassFactory().getImplementationClassName(ProfileAttribute.class);

	List objList = null;

	if (principalObject == null) {
	    throw new JSException("jsexception.no.principal");
	}

	final String queryString = 
	    "from " + attrClassName + " as profileAttr " +
	    "where profileAttr.principal.id = ? " +
	    "and profileAttr.principal.class = ? " +
	    "and profileAttr.attrName = ?";
	
	final String attrName = attr.getAttrName();
	
	objList = getHibernateTemplate().executeFind(new HibernateCallback() {
		public Object doInHibernate(Session session) throws HibernateException {
		    Query query = session.createQuery(queryString);
		    query.setParameter(0, new Long(principalObject.getId()), Hibernate.LONG);
		    query.setParameter(1, principalObject.getClass(), Hibernate.CLASS);
		    query.setParameter(2, attrName, Hibernate.STRING); 
		    return query.list();
		}
	    });

	if (objList.size() == 0) return null;
	return (RepoProfileAttribute) objList.get(0);
    }

    private List getRepoProfileAttributes(ExecutionContext context, 
					  Object principal) {
	// Given the principal find the attributes
	final IdedObject principalObject = 
	    (IdedObject) getPersistentObject(principal);

	final String attrClassName = 
	    getPersistentClassFactory().getImplementationClassName(ProfileAttribute.class);

	List objList = null;

	if (principalObject == null) {
	    throw new JSException("jsexception.no.principal");
	}

	final String queryString = 
	    "from " + attrClassName + " as profileAttr " +
	    "where profileAttr.principal.id = ? " +
	    "and profileAttr.principal.class = ?";
	
	objList = getHibernateTemplate().executeFind(new HibernateCallback() {
		public Object doInHibernate(Session session) throws HibernateException {
		    Query query = session.createQuery(queryString);
		    query.setParameter(0, new Long(principalObject.getId()), Hibernate.LONG);
		    query.setParameter(1, principalObject.getClass(), Hibernate.CLASS);
		    return query.list();
		}
	    });
	
	return objList;
    }

    public List getProfileAttributesForPrincipal(ExecutionContext context, 
						 Object principal) {
	List objList = getRepoProfileAttributes(context, principal);
	return makeProfileAttributeClientList(null, objList);
    }

    private List makeProfileAttributeClientList(String uri, List objList) {
	List resultList = new ArrayList(objList.size());
	
	for (Iterator it = objList.iterator(); !objList.isEmpty() && it.hasNext();) {
	    RepoProfileAttribute repoPerm = (RepoProfileAttribute) it.next();
	    ProfileAttribute clientPermission = 
		(ProfileAttribute) repoPerm.toClient(getObjectMappingFactory());
	    resultList.add(clientPermission);
	}
	return resultList;
    }

    public Object getPersistentObject(Object clientObject) {
	// If already persisted, just return it
	if (clientObject == null) {
	    return null;
	} else	if (clientObject instanceof IdedObject) {
	    return clientObject;
	} else if (clientObject instanceof Role || clientObject instanceof User) {
	    return ((PersistentObjectResolver) userService).getPersistentObject(clientObject);
	} else if (clientObject instanceof Resource) {
	    // TODO Hack! Make it an interface!
	    String uri = ((Resource) clientObject).getPath();
	    return repoService.findByURI(RepoResource.class, uri, false);
	} else if (clientObject instanceof ProfileAttribute) {
	    return getRepoProfileAttribute(null, (ProfileAttribute) clientObject);
	}
	return null;
    }

}
