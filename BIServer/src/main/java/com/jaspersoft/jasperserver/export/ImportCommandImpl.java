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

package com.jaspersoft.jasperserver.export;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.export.io.ExportImportIOFactory;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ImportCommandImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ImportCommandImpl implements CommandBean, ApplicationContextAware {
	
	private static final CommandOut commandOut = CommandOut.getInstance();

	private ApplicationContext ctx;
	
	private ExportImportIOFactory exportImportIOFactory;
	private String importerPrototypeBeanName;

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	public void process(Parameters parameters) {
		ImportTask task = createTask(parameters);
		Importer importer = createPrototypeImporter(parameters);
		importer.setTask(task);
		importer.performImport();
	}

	protected ImportTask createTask(Parameters parameters) {
		ImportTaskImpl task = new ImportTaskImpl();
		task.setParameters(parameters);
		task.setExecutionContext(getExecutionContext(parameters));
		task.setInput(getImportInput(parameters));
		return task;
	}

	protected ImportInput getImportInput(Parameters parameters) {
		return getExportImportIOFactory().createInput(parameters);
	}

	protected ExecutionContext getExecutionContext(Parameters parameters) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.setLocale(getLocale(parameters));
		return context;
	}

	protected Locale getLocale(Parameters parameters) {
		return Locale.getDefault();
	}

	protected Importer createPrototypeImporter(Parameters parameters) {
		String importerBeanName = getImporterPrototypeBeanName(parameters);
		
		commandOut.debug("Using " + importerBeanName + " importer prototype bean.");
		
		return (Importer) ctx.getBean(importerBeanName, Importer.class);
	}

	protected String getImporterPrototypeBeanName(Parameters parameters) {
		return getImporterPrototypeBeanName();
	}

	public String getImporterPrototypeBeanName() {
		return importerPrototypeBeanName;
	}

	public void setImporterPrototypeBeanName(String importerPrototypeBeanName) {
		this.importerPrototypeBeanName = importerPrototypeBeanName;
	}

	public ExportImportIOFactory getExportImportIOFactory() {
		return exportImportIOFactory;
	}

	public void setExportImportIOFactory(ExportImportIOFactory exportImportIOFactory) {
		this.exportImportIOFactory = exportImportIOFactory;
	}

}
