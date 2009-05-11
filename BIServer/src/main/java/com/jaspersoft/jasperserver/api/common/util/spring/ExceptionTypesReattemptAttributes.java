/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ExceptionTypesReattemptAttributes.java 13093 2008-04-21 13:56:46Z lucian $
 */
public class ExceptionTypesReattemptAttributes implements ReattemptMethodAttributes {

	protected static final int DEFAULT_REATTEMPT_COUNT = 2;
	
	private List exceptionTypes = new ArrayList();
	private int attemptCount = DEFAULT_REATTEMPT_COUNT;

	public boolean toReattempt(Exception exc, int attemptNumber) {
		return attemptNumber < getAttemptCount() 
				&& matchTypes(exc);
	}

	protected boolean matchTypes(Exception exc) {
		boolean match = false;
		for (Iterator it = exceptionTypes.iterator(); it.hasNext();) {
			String type = (String) it.next();
			if (matchesType(exc, type)) {
				match = true;
				break;
			}
		}
		return match;
	}

	protected boolean matchesType(Exception exc, String type) {
		return type.equals(exc.getClass().getName());
	}
	
	public int getAttemptCount() {
		return attemptCount;
	}
	
	public void setAttemptCount(int reattemptCount) {
		this.attemptCount = reattemptCount;
	}

	public List getExceptionTypes() {
		return exceptionTypes;
	}

	public void setExceptionTypes(List exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
	}

}
