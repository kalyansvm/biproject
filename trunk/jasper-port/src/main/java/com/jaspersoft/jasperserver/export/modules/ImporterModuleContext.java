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

import org.dom4j.Element;

import com.jaspersoft.jasperserver.export.ImportTask;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ImporterModuleContext.java 12757 2008-03-31 16:58:16Z lucian $
 */
public interface ImporterModuleContext {

	ImportTask getImportTask();
	
	String getCharacterEncoding();

	Element getModuleIndexElement();
	
	Attributes getAttributes();

}
