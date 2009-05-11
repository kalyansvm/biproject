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

package com.jaspersoft.jasperserver.export;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CommandMetadataImpl.java 13438 2008-05-07 11:44:11Z lucian $
 */
public class CommandMetadataImpl implements CommandMetadata, InitializingBean {

	private List argumentNames;
	private Set argumentNameSet;

	public void afterPropertiesSet() {
		argumentNameSet = new HashSet();
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_FILES);
		argumentNameSet.add(BaseExportImportCommand.ARG_CONFIG_RESOURCES);
		argumentNameSet.add(BaseExportImportCommand.ARG_COMMAND_BEAN);
		if (argumentNames != null) {
			argumentNameSet.addAll(argumentNames);
		}
	}
	
	public List getArgumentNames() {
		return argumentNames;
	}

	public void validateParameters(Parameters parameters) {
		for (Iterator it = parameters.getParameterNames(); it.hasNext();) {
			String argument = (String) it.next();
			if (!argumentNameSet.contains(argument)) {
				throw new JSException("jsexception.export.unknown.argument", 
						new Object[]{argument});
			}
		}
	}

	public void setArgumentNames(List argumentNames) {
		this.argumentNames = argumentNames;
	}

}
