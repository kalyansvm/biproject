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

package com.jaspersoft.jasperserver.export.modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.UnmodifiableList;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ModuleRegisterImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ModuleRegisterImpl implements ModuleRegister {

	private List exporterModules;
	private List importerModules;
	private Map importerModulesMap;
	
	public void setExporterModules(List modules) {
		this.exporterModules = modules;
	}

	public List getExporterModules() {
		return UnmodifiableList.decorate(exporterModules);
	}

	public List getImporterModules() {
		return UnmodifiableList.decorate(importerModules);
	}

	public void setImporterModules(List importerModules) {
		this.importerModules = importerModules;
		refreshImporterMap();
	}

	protected void refreshImporterMap() {
		importerModulesMap = new HashMap();
		for (Iterator it = importerModules.iterator(); it.hasNext();) {
			ImporterModule module = (ImporterModule) it.next();
			importerModulesMap.put(module.getId(), module);
		}
	}

	public ImporterModule getImporterModule(String id) {
		return (ImporterModule) importerModulesMap.get(id);
	}

}
