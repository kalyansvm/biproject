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

package com.jaspersoft.jasperserver.export.io;

import java.util.Map;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author lucian
 *
 */
public class SimplePathProcessorFactory implements PathProcessorFactory {

	private String defaultInputProcessor;
	private String defaultOutputProcessor;
	private Map processors;
	
	public String getDefaultInputProcessor() {
		return defaultInputProcessor;
	}

	public String getDefaultOutputProcessor() {
		return defaultOutputProcessor;
	}

	public PathProcessor getProcessor(String id) {
		if (!processors.containsKey(id)) {
			throw new JSException("No path processor found for id \"" + id + "\"");
		}
		
		return (PathProcessor) processors.get(id);
 	}

	public Map getProcessors() {
		return processors;
	}

	public void setProcessors(Map processors) {
		this.processors = processors;
	}

	public void setDefaultInputProcessor(String defaultInputProcessor) {
		this.defaultInputProcessor = defaultInputProcessor;
	}

	public void setDefaultOutputProcessor(String defaultOutputProcessor) {
		this.defaultOutputProcessor = defaultOutputProcessor;
	}

}
