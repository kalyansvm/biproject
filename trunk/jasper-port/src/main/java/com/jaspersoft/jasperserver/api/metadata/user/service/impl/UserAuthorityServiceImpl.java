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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.ui.switchuser.SwitchUserGrantedAuthority;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;

/**
 * @author swood
 * @version $Id: UserAuthorityServiceImpl.java 13657 2008-05-20 01:23:00Z swood $
 */
public class UserAuthorityServiceImpl extends HibernateDaoImpl implements UserDetailsService, ExternalUserService, PersistentObjectResolver {

	protected static final Log log = LogFactory.getLog(UserAuthorityServiceImpl.class);
	private ResourceFactory objectFactory;
	private ResourceFactory persistentClassFactory;
        private ProfileAttributeService profileAttributeService;

	private List defaultInternalRoles;

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

        public ProfileAttributeService getProfileAttributeService() {
	    return profileAttributeService;
	}

        public void setProfileAttributeService(ProfileAttributeService p) {
	    this.profileAttributeService = p;
	    p.setUserAuthorityService(this);
	}

	protected RepoUser getRepoUser(ExecutionContext context, String username) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClassFactory().getImplementationClass(User.class));
		criteria.add(Restrictions.eq("username", username));
		List userList = getHibernateTemplate().findByCriteria(criteria);
		RepoUser user = null;
		if (userList.isEmpty()) {
			log.debug("User not found with username \"" + username + "\"");
		} else {
			user = (RepoUser) userList.get(0);
		}
		return user;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
	 */
	public User getUser(ExecutionContext context, String username) {
		RepoUser user = getRepoUser(context, username);
		User userDTO = null;
		if (user != null) {
			userDTO = (User) user.toClient(getObjectMappingFactory());
			List attrs = getProfileAttributeService().
			    getProfileAttributesForPrincipal(null, user);
			userDTO.setAttributes(attrs);
		} else {
			log.debug("No such user as: " + username);
		}
		return userDTO;
	}

	protected RepoUser getRepoUser(ExecutionContext context, Long id) {
		RepoUser user = (RepoUser) getHibernateTemplate().load(getPersistentClassFactory().getImplementationClass(User.class), id);
		return user;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
	 */
	protected User getUser(ExecutionContext context, Long id) {
		RepoUser user = getRepoUser(context, id);
		User userDTO = null;
		if (user != null) {
			userDTO = (User) user.toClient(getObjectMappingFactory());
		}
		return userDTO;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		User u = getUser(null, username);

		if (u == null) {
			throw new UsernameNotFoundException("User not found with username \"" + username + "\"");
		} else {
			return new MetadataUserDetails(u);
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#putUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.User)
	 */
	public void putUser(ExecutionContext context, User aUser) {
		RepoUser existingUser = getRepoUser(context, aUser.getUsername());
		if (existingUser == null) {
			existingUser = (RepoUser) getPersistentClassFactory().newObject(User.class);
		}
		existingUser.copyFromClient(aUser, this);
		getHibernateTemplate().saveOrUpdate(existingUser);
	}

	/**
	 * return everything for now
	 *
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getUsers(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria)
	 */
	public List getUsers(ExecutionContext context, FilterCriteria filterCriteria) {
		// make User DTOs
		List results = getHibernateTemplate().loadAll(getPersistentClassFactory().getImplementationClass(User.class));
		List userDTOs = null;

		if (results != null) {
			userDTOs = new ArrayList(results.size());
			Iterator it = results.iterator();
			while (it.hasNext()) {
				RepoUser u = (RepoUser) it.next();
				User newUser = (User) u.toClient(getObjectMappingFactory());
				userDTOs.add(newUser);
			}
		}
		return userDTOs;
	}

	/**
	 * DTO for the User interface
	 *
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#newUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
	 */
	public User newUser(ExecutionContext context) {
		return (User) getObjectMappingFactory().newObject(User.class);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#disableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
	 */
	protected boolean disableUser(ExecutionContext context, Long id) {
		RepoUser user = getRepoUser(context, id);
		if (user != null && user.isEnabled()) {
			user.setEnabled(false);
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#disableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
	 */
	public boolean disableUser(ExecutionContext context, String username) {
		RepoUser user = getRepoUser(context, username);
		if (user != null && user.isEnabled()) {
			user.setEnabled(false);
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#enableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
	 */
	protected boolean enableUser(ExecutionContext context, Long id) {
		RepoUser user = getRepoUser(context, id);
		if (user != null && !user.isEnabled()) {
			user.setEnabled(true);
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#enableUser(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
	 */
	public boolean enableUser(ExecutionContext context, String username) {
		RepoUser user = getRepoUser(context, username);
		if (user != null && !user.isEnabled()) {
			user.setEnabled(true);
			return true;
		} else {
			return false;
		}
	}

	public void deleteUser (ExecutionContext context, String username) {
		RepoUser user = getRepoUser(context, username);
		if (user == null) {
			return;
		}
		removeAllRoles(context, (User) user);

		getHibernateTemplate().delete(user);
	}

	public void addRole(ExecutionContext context, User user, Role role) {
		if (user == null) {
			return;
		}

		RepoUser existingUser = getRepoUser(context, user.getUsername());
		if (existingUser != null) {
            RepoRole existingRole = getRepoRole(context, role.getRoleName());
			existingUser.addRole(existingRole);
			putUser(null, existingUser);
		}
		user.addRole(role);
	}


	public void removeRole(ExecutionContext context, User user, Role role) {
		if (user == null || role == null) {
			return;
		}

		RepoUser existingUser = getRepoUser(context, user.getUsername());
		
		if (existingUser != null) {
			RepoRole r = getRepoRole(context, role.getRoleName());
			if (r != null) {
				existingUser.removeRole(r);
				putUser(null, existingUser);
			} else {
				log.debug("removeRole: No role such as " + role.getRoleName());
			}
		} else {
			log.debug("removeRole: No user such as " + user.getUsername());
		}
		user.removeRole(role);
	}

	public void removeAllRoles(ExecutionContext context, User user) {
		if (user == null) {
			return;
		}

		RepoUser existingUser = getRepoUser(context, user.getUsername());
		if (existingUser == null) {
			return;
		}

		/*
			for (Iterator it = existingUser.getRoles().iterator(); it.hasNext(); ) {
				Role role = (Role) it.next();
				existingUser.removeRole(role);
				user.removeRole(role);
			}
		*/

		existingUser.getRoles().clear(); //to avoid ConcurrentModificationException
		putUser(null, existingUser);

	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.String)
	 */
	public Role getRole(ExecutionContext context, String roleName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClassFactory().getImplementationClass(Role.class));
		criteria.add(Restrictions.eq("roleName", roleName));
		List roleList = getHibernateTemplate().findByCriteria(criteria);
		Role role = null;
		if (roleList.isEmpty()) {
			log.debug("Role not found with role name \"" + roleName + "\"");
		} else {
			RepoRole repoRole = (RepoRole) roleList.get(0);
			role = (Role) repoRole.toClient((ResourceFactory) getObjectMappingFactory());
		}
		return role;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Long)
	 */
	protected RepoRole getRepoRole(ExecutionContext context, String roleName) {
		//return (RepoRole) getHibernateTemplate().load(getPersistentClassFactory().getImplementationClass(Role.class), roleName);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClassFactory().getImplementationClass(Role.class));
		criteria.add(Restrictions.eq("roleName", roleName));
		List userList = getHibernateTemplate().findByCriteria(criteria);
		RepoRole role = null;
		if (userList.isEmpty()) {
			log.debug("Role not found with role name \"" + roleName + "\"");
		} else {
			role = (RepoRole) userList.get(0);
		}
		return role;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#putRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
	 */
	public void putRole(ExecutionContext context, Role aRole) {
		RepoRole existingRole = getRepoRole(context, aRole.getRoleName());
		log.debug("putRole: " + aRole.getRoleName() + ", " + existingRole);
		if (existingRole == null) {
			existingRole = (RepoRole) getPersistentClassFactory().newObject(Role.class);
			log.debug("New Object");
		}
		existingRole.copyFromClient(aRole, this);
		getHibernateTemplate().saveOrUpdate(existingRole);

		Set repoUsers = existingRole.getUsers();
		for (Iterator it = repoUsers.iterator(); it.hasNext();) {
			RepoUser repoUser = (RepoUser) it.next();
			repoUser.getRoles().remove(getPersistentObject(aRole));
		}

		Set users = aRole.getUsers();
		for (Iterator it = users.iterator(); it.hasNext();) {
			User user = (User) it.next();
			addRole(context, user, aRole);
		}

	}

	/**
	 * Return everything for now
	 *
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#getRoles(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria)
	 */
	public List getRoles(ExecutionContext context, FilterCriteria filterCriteria) {
		List results = getHibernateTemplate().loadAll(getPersistentClassFactory().getImplementationClass(Role.class));
		List roleDTOs = null;

		if (results != null) {
			roleDTOs = new ArrayList(results.size());
			Iterator it = results.iterator();
			while (it.hasNext()) {
				RepoRole r = (RepoRole) it.next();
				Role newRole = (Role) r.toClient((ResourceFactory) getObjectMappingFactory());
				roleDTOs.add(newRole);
			}
		}
		return roleDTOs;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService#newRole(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
	 */
	public Role newRole(ExecutionContext context) {
		// return a Role DTO
		return (Role) getObjectMappingFactory().newObject(Role.class);
	}

	public void deleteRole(ExecutionContext context, String roleName) {
		RepoRole role = getRepoRole(context, roleName);
		if (role == null) {
			return;
		}

		// Get all users that have this role and remove the role from them

		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClassFactory().getImplementationClass(User.class));
		criteria.createAlias("roles", "r")
			.add( Restrictions.eq("r.roleName", roleName) );
		List userList = getHibernateTemplate().findByCriteria(criteria);

		for (Iterator it = userList.iterator(); it.hasNext(); ) {
			RepoUser u = (RepoUser) it.next();
			u.removeRole(role);
		}

//		role.getUsers().clear();

		// then delete the role
		getHibernateTemplate().delete(role);
	}

	public List getUsersNotInRole(ExecutionContext context, String roleName)
	{
		List allUsers = getUsers(context, null);
		List usersInRole = getUsersInRole(context, roleName);
		allUsers.removeAll(usersInRole);

		return allUsers;
	}

	public List getUsersInRole(ExecutionContext context, String roleName)
	{
		RepoRole repoRole = getRepoRole(context, roleName);
		Set repoUsers = repoRole.getUsers();
		List users = new ArrayList();

		for (Iterator it = repoUsers.iterator(); it.hasNext();)
		{
			RepoUser repoUser = (RepoUser) it.next();
			User user = (User) repoUser.toClient(getObjectMappingFactory());
			users.add(user);
		}

		return users;
	}


	public List getAssignedRoles(ExecutionContext context, String userName)
	{
		RepoUser repoUser = getRepoUser(context, userName);
		Set repoRoles = repoUser.getRoles();

		List roles = new ArrayList();

		for (Iterator it = repoRoles.iterator(); it.hasNext();) {
			RepoRole repoRole = (RepoRole) it.next();
			Role role = (Role) repoRole.toClient(getObjectMappingFactory());
			roles.add(role);
		}

		return roles;
	}


	public List getAvailableRoles(ExecutionContext context, String userName)
	{
		List allRoles = getRoles(context, null);
		List assignedRoles = getAssignedRoles(null, userName);
		allRoles.removeAll(assignedRoles);
		return allRoles;
	}

	public boolean roleExists(ExecutionContext context, String roleName)
	{
		return (getRole(context, roleName) != null);
	}

	/*
	 * TODO this should be generalized. Maybe get the Repo* objects to return a
	 * DetachedCriteria filled with the key from the client object?
	 *
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver#getPersistentObject(java.lang.Object)
	 */
	public Object getPersistentObject(Object clientObject) {
		if (clientObject instanceof Role) {
			Role r = (Role) clientObject;
			return getRepoRole(null, r.getRoleName());
		} else if (clientObject instanceof User) {
			User u = (User) clientObject;
			return getRepoUser(null, u.getUsername());
		}
		return null;
	}

	/**
	 * From an external UserDetails + GrantedAuthority[], maintain the shadow internal user
	 *
	 * @param externalUserDetails
	 */
	public void maintainInternalUser(UserDetails externalUserDetails, GrantedAuthority[] authorities) {

    	log.debug("External user: " + externalUserDetails.getUsername());

        User user = getUser(new ExecutionContextImpl(), externalUserDetails.getUsername());

		if (user == null) {
			user = createNewExternalUser(externalUserDetails.getUsername());
		}

		alignInternalAndExternalUser(getRolesFromGrantedAuthorities(externalUserDetails.getAuthorities()), user);

	}

	/**
	 * From an external user (string user name) + GrantedAuthority[], maintain the shadow internal user
	 *
	 * @param externalUserDetails
	 */
	public void maintainInternalUser(String userName, GrantedAuthority[] authorities) {

    	log.debug("External user(String): " + userName);

        User user = getUser(new ExecutionContextImpl(), userName);

		if (user == null) {
			user = createNewExternalUser(userName);
		}

		alignInternalAndExternalUser(getRolesFromGrantedAuthorities(authorities), user);

	}
	/**
	 * New user created from given authentication details. No password is set or needed.
	 * Roles are set elsewhere.
	 *
	 * @param userDetails
	 * @return created User
	 */
	private User createNewExternalUser(String userName) {
    	User user = newUser(new ExecutionContextImpl());
    	user.setUsername(userName);
    	// If it is externally authenticated, no save of password
    	//user.setPassword(userDetails.getPassword());
    	user.setFullName(userName); // We don't know the real name
    	user.setExternallyDefined(true);
    	user.setEnabled(true);
		log.warn("Created new external user: " + user.getUsername());
		return user;
	}

	/**
	 * Ensure the external user has the right roles. Roles attached to the userDetails are the definitive list
	 * of externally defined roles.
	 *
	 * @param auth
	 * @param userDetails
	 * @param user
	 */
	private void alignInternalAndExternalUser(Set externalRoles, User user) {

    	final Predicate externallyDefinedRoles = new Predicate() {
    		public boolean evaluate(Object input) {
    			if (!(input instanceof Role)) {
    				return false;
    			}
    			return ((Role) input).isExternallyDefined();
    		}
    	};

    	Set currentRoles = user.getRoles();

    	// we may have a new user, so always persist them
    	boolean persistUserNeeded = (currentRoles.size() == 0);
/*
    	// If it is externally authenticated, no save of password
    	if (!user.getPassword().equals(userDetails.getPassword())) {
    		user.setPassword(userDetails.getPassword());
    		persistUserNeeded = true;
    	}

*/    	Collection currentExternalRoles = CollectionUtils.select(user.getRoles(), externallyDefinedRoles);
		if (log.isDebugEnabled()) {
			log.debug("Login of external User: " + user.getUsername() );
			log.debug("Roles from authentication:\n" + roleCollectionToString(externalRoles));
			log.debug("Current roles from metadata:\n" + roleCollectionToString(user.getRoles()));
			log.debug("Current external roles for user from metadata: " + user.getUsername() + "\n" + roleCollectionToString(currentExternalRoles));
		}

		/*
		 * If we have new external roles, we want to add them
		 */
    	Collection newExternalRoles = CollectionUtils.subtract(externalRoles, currentExternalRoles);

    	if (newExternalRoles.size() > 0) {
    		currentRoles.addAll(newExternalRoles);
    		if (log.isWarnEnabled()) {
    			log.warn("Added following external roles to: " + user.getUsername() + "\n" + roleCollectionToString(newExternalRoles));
    		}
    		persistUserNeeded = true;
    	}

		/*
		 * If external roles have been removed, we need to remove them
		 */
    	Collection rolesNeedingRemoval = CollectionUtils.subtract(currentExternalRoles, externalRoles);

    	if (rolesNeedingRemoval.size() > 0) {
    		currentRoles.removeAll(rolesNeedingRemoval);
    		if (log.isWarnEnabled()) {
    			log.warn("Removed following external roles from: " + user.getUsername() + "\n" + roleCollectionToString(rolesNeedingRemoval));
    		}
    		persistUserNeeded = true;
    	}

		/*
		 * If we have new default internal roles, we want to add them
		 */
    	Collection defaultInternalRolesToAdd = CollectionUtils.subtract(getNewDefaultInternalRoles(), currentRoles);

    	if (defaultInternalRolesToAdd.size() > 0) {
    		if (log.isDebugEnabled()) {
    			log.debug("Default internal roles: " + roleCollectionToString(getNewDefaultInternalRoles()));
    		}
    		currentRoles.addAll(defaultInternalRolesToAdd);
    		if (log.isWarnEnabled()) {
    			log.warn("Added following new default internal roles to: " + user.getUsername() + "\n" + roleCollectionToString(defaultInternalRolesToAdd));
    		}
    		persistUserNeeded = true;
    	}

    	if (persistUserNeeded) {
    		if (log.isWarnEnabled()) {
    			log.warn("Updated user: " + user.getUsername() + ". Roles are now:\n" + roleCollectionToString(currentRoles));
    		}
        	user.setRoles(currentRoles);
        	// persist user and roles
        	putUser(new ExecutionContextImpl(), user);
    		if (log.isWarnEnabled()) {
    			log.warn("Updated user: " + user.getUsername() + ". Roles are now:\n" + roleCollectionToString(currentRoles));
    		}
    	}

	}

	private String roleCollectionToString(Collection coll) {
		Iterator it = coll.iterator();
		StringBuffer rolesPrint = new StringBuffer();
		while (it.hasNext()) {
			String s = ((Role) it.next()).getRoleName();
			rolesPrint.append(s).append("\n");
		}
		return rolesPrint.toString();
	}

	/**
	 * Get a set of roles based on the given GrantedAuthority[]. Roles are created
	 * in the metadata if they do not exist.
	 *
	 * @param GrantedAuthority[] authorities from authenticated user
	 * @return Set of externally defined Roles
	 */
	private Set getRolesFromGrantedAuthorities(GrantedAuthority[] authorities) {
		Set set = new HashSet();

		if (authorities == null || authorities.length == 0)
			return set;

		for (int i = 0; i < authorities.length; i++) {
			GrantedAuthority auth = authorities[i];

			String authorityName = auth.getAuthority();

			// Make spaces in the authority name be underscores

			authorityName = authorityName.replace(' ', '_');
                        
                        if (!authorityName.startsWith("ROLE_")) {
                            authorityName = "ROLE_" + authorityName;
                        }

			set.add(getOrCreateRole(authorityName, true));
		}
		return set;
	}

    /**
     * @return the Authentication corresponding to the principal who used
     * the "Switch User" feature to login as the current principal if any, 
     * or null, if the current principal is not a switched user.
     */ 
    public static Authentication getSourceAuthentication() {
	Authentication current = SecurityContextHolder.getContext().getAuthentication();
	Authentication original = null;
 
	// iterate over granted authorities and find the 'switch user' authority
	GrantedAuthority[] authorities = current.getAuthorities();
	
	for (int i = 0; i < authorities.length; i++) {
	    // check for switch user type of authority
	    if (authorities[i] instanceof SwitchUserGrantedAuthority) {
		original = ((SwitchUserGrantedAuthority) authorities[i]).getSource();
		log.debug("Found original switch user granted authority [" + original + "]");
	    }
         }
	
	return original;
    }

    public static boolean isUserSwitched() {
	return (getSourceAuthentication() != null);
    }

	/**
	 * Get a set of roles that are the defaults for a new external user. Roles are created
	 * in the metadata if they do not exist.
	 *
	 * @return Set of internally defined Roles
	 */
	private Set getNewDefaultInternalRoles() {
		Set set = new HashSet();

		if (getDefaultInternalRoles() == null || getDefaultInternalRoles().size() == 0)
			return set;

		for (int i = 0; i < getDefaultInternalRoles().size(); i++) {
			String roleName = (String) getDefaultInternalRoles().get(i);

			set.add(getOrCreateRole(roleName, false));
		}
		return set;
	}

	private Role getOrCreateRole(String roleName, boolean externallyDefined) {
		Role r = getRole(new ExecutionContextImpl(), roleName);
		if (r == null) {
			r = newRole(new ExecutionContextImpl());
			r.setRoleName(roleName);
			r.setExternallyDefined(externallyDefined);
			putRole(new ExecutionContextImpl(), r);
			log.warn("Created new " + (externallyDefined ? "external" : "internal") + " role: " + roleName);
		}

		return r;
	}

	public void makeUserLoggedIn(String username) {

		try {
			// Make our user the Authentication!

			UserDetails ourUserDetails = loadUserByUsername(username);

			// Don't set the authentication if we have no roles

			if (ourUserDetails.getAuthorities().length != 0) {
				UsernamePasswordAuthenticationToken ourAuthentication = new UsernamePasswordAuthenticationToken(ourUserDetails,
						ourUserDetails.getPassword(), ourUserDetails.getAuthorities());

				if (log.isDebugEnabled()) {
					log.debug("Setting Authentication to: " + ourAuthentication);
				}
				SecurityContextHolder.getContext().setAuthentication(ourAuthentication);
			} else {

	            // There was some error - maybe no roles?
	            // Remove authentication to allow anonymous access to catch things
	            // later in the filter chain
				SecurityContextHolder.getContext().setAuthentication(null);
			}
		} catch (UsernameNotFoundException e) {
			log.warn("User: " + username + " was not found to make them logged in");
		}
	}

	/**
	 * @return Returns the defaultInternalRoles.
	 */
	public List getDefaultInternalRoles() {
		return defaultInternalRoles;
	}

	/**
	 * @param defaultInternalRoles The defaultInternalRoles to set.
	 */
	public void setDefaultInternalRoles(List defaultInternalRoles) {
		this.defaultInternalRoles = defaultInternalRoles;
	}

	public boolean userExists(ExecutionContext context, String username)
	{
		return (getUser(context, username) != null);
	}
	
	public boolean isPasswordExpired(ExecutionContext context, String username, int nDate) {		
		Date previousExpirationDate = getUser(context, username).getPreviousPasswordChangeTime();
		// TO-DO what if previousExpirationDate is empty
		if ((previousExpirationDate == null) || ("".equals(previousExpirationDate))) {
		   resetPasswordExpiration(context, username);	
		   return false;
		}		

        long during = nDate*3600*24;
		if (((previousExpirationDate.getTime()/1000)+during) <= ((new Date()).getTime()/1000)) {
		   return true;	
		}
		return false;
	}
	public void resetPasswordExpiration(ExecutionContext context, String username) {	
		User user = getUser(context, username);
		if (user != null) {			
			user.setPreviousPasswordChangeTime(new Date());
			putUser(context, user);
		}
	}
}
