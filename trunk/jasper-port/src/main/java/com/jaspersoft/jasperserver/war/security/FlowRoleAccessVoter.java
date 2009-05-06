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

package com.jaspersoft.jasperserver.war.security;

import java.util.Iterator;

import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.vote.AbstractAclVoter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FlowRoleAccessVoter.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FlowRoleAccessVoter extends AbstractAclVoter {
	
    private FlowDefinitionSource flowDefinitionSource;
    private String flowAccessAttribute;
    
	public FlowRoleAccessVoter() {
		super();
		
		setProcessDomainObjectClass(String.class);
	}

	public boolean supports(ConfigAttribute attribute) {
		String attr = attribute.getAttribute();
		return attr != null && attr.equals(getFlowAccessAttribute());
	}

	public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config) {
		int result = ACCESS_ABSTAIN;
		
		for (Iterator it = config.getConfigAttributes(); it.hasNext(); ) {
			ConfigAttribute attribute = (ConfigAttribute) it.next();
			if (supports(attribute)) {
				result = voteFlowAccess(authentication, object);
			}
		}
		
		return result;
	}

	protected int voteFlowAccess(Authentication authentication, Object object) {
		int result;
		
		String flowId = (String) getDomainObjectInstance(object);		
		if (flowId == null) {
			result = ACCESS_ABSTAIN;
		} else {
	        result = ACCESS_DENIED;
			
			ConfigAttributeDefinition flowAttributes = getFlowDefinitionSource().getAttributes(flowId);
	        for (Iterator iter = flowAttributes.getConfigAttributes(); iter.hasNext(); ) {
	            ConfigAttribute attribute = (ConfigAttribute) iter.next();

				if (matchesAuthority(authentication, attribute.getAttribute())) {
					result = ACCESS_GRANTED;
					break;
				}
	        }
		}
        
		return result;
	}

	protected boolean matchesAuthority(Authentication authentication, String attribute) {
		GrantedAuthority[] authorities = authentication.getAuthorities();
		boolean matches = false;
		for (int i = 0; i < authorities.length; i++) {
		    if (attribute.equals(authorities[i].getAuthority())) {
		    	matches = true;
		    	break;
		    }
		}
		return matches;
	}

	public FlowDefinitionSource getFlowDefinitionSource() {
		return flowDefinitionSource;
	}

	public void setFlowDefinitionSource(FlowDefinitionSource flowDefinitionSource) {
		this.flowDefinitionSource = flowDefinitionSource;
	}

	public String getFlowAccessAttribute() {
		return flowAccessAttribute;
	}

	public void setFlowAccessAttribute(String flowAccessAttribute) {
		this.flowAccessAttribute = flowAccessAttribute;
	}

}
