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

import java.util.List;
import java.util.Vector;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.acl.AclEntry;
import org.acegisecurity.acl.basic.BasicAclEntry;
import org.acegisecurity.acl.basic.EffectiveAclsResolver;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author swood
 *
 */
public class ObjectPermissionEffectiveAclsResolver
	implements EffectiveAclsResolver {
	
	private static final Log logger = LogFactory.getLog(ObjectPermissionEffectiveAclsResolver.class);

	/* (non-Javadoc)
	 * @see org.acegisecurity.acl.basic.EffectiveAclsResolver#resolveEffectiveAcls(org.acegisecurity.acl.AclEntry[], org.acegisecurity.Authentication)
	 */
    public AclEntry[] resolveEffectiveAcls(AclEntry[] allAcls,
            Authentication filteredBy) {
            if ((allAcls == null) || (allAcls.length == 0)) {
                return null;
            }

            List list = new Vector();

            if (logger.isDebugEnabled()) {
                logger.debug("Locating AclEntry[]s (from set of "
                    + ((allAcls == null) ? 0 : allAcls.length)
                    + ") that apply to Authentication: " + filteredBy);
            }

            for (int i = 0; i < allAcls.length; i++) {
                if (!(allAcls[i] instanceof BasicAclEntry)) {
                    continue;
                }

                Object recipient = ((BasicAclEntry) allAcls[i])
                    .getRecipient();

                if (recipient instanceof Role) {
                	recipient = ((Role) recipient).getRoleName();
                } else if (recipient instanceof User) {
                	recipient = ((User) recipient).getUsername();
                }
                // Allow the Authentication's getPrincipal to decide whether
                // the presented recipient is "equal" (allows BasicAclDaos to
                // return Strings rather than proper objects in simple cases)
                if (filteredBy.getPrincipal().equals(recipient)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Principal matches AclEntry recipient: "
                            + recipient);
                    }

                    list.add(allAcls[i]);
                } else if (filteredBy.getPrincipal() instanceof UserDetails
                    && ((UserDetails) filteredBy.getPrincipal()).getUsername()
                        .equals(recipient)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                            "Principal (from UserDetails) matches AclEntry recipient: "
                            + recipient);
                    }

                    list.add(allAcls[i]);
                } else {
                    // No direct match against principal; try each authority.
                    // As with the principal, allow each of the Authentication's
                    // granted authorities to decide whether the presented
                    // recipient is "equal"
                    GrantedAuthority[] authorities = filteredBy.getAuthorities();

                    if ((authorities == null) || (authorities.length == 0)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                "Did not match principal and there are no granted authorities, so cannot compare with recipient: "
                                + recipient);
                        }

                        continue;
                    }

                    for (int k = 0; k < authorities.length; k++) {
                        if (authorities[k].equals(recipient)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("GrantedAuthority: " + authorities[k]
                                    + " matches recipient: " + recipient);
                            }

                            list.add(allAcls[i]);
                        }
                    }
                }
            }

            // return null if appropriate (as per interface contract)
            if (list.size() > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Returning effective AclEntry array with "
                        + list.size() + " elements");
                }

                return (BasicAclEntry[]) list.toArray(new BasicAclEntry[] {});
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "Returning null AclEntry array as zero effective AclEntrys found");
                }

                return null;
            }
        }

}
