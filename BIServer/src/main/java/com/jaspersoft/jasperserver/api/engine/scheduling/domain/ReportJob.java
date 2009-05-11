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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJob.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportJob implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int VERSION_NEW = -1;
	
	public static final byte OUTPUT_FORMAT_PDF = 1;
	public static final byte OUTPUT_FORMAT_HTML = 2;
	public static final byte OUTPUT_FORMAT_XLS = 3;
	public static final byte OUTPUT_FORMAT_RTF = 4;
	public static final byte OUTPUT_FORMAT_CSV = 5;

	private long id;
	private int version = VERSION_NEW;
	private String username;
	private String label;
	private String description;
	private ReportJobTrigger trigger;
	private ReportJobSource source;
	private String baseOutputFilename;
	private Set outputFormats;
	private String outputLocale;
	private ReportJobRepositoryDestination contentRepositoryDestination;
	private ReportJobMailNotification mailNotification;

	public ReportJob() {
		outputFormats = new HashSet();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ReportJobSource getSource() {
		return source;
	}

	public void setSource(ReportJobSource source) {
		this.source = source;
	}

	public ReportJobTrigger getTrigger() {
		return trigger;
	}

	public void setTrigger(ReportJobTrigger trigger) {
		this.trigger = trigger;
	}

	public ReportJobMailNotification getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(ReportJobMailNotification mailNotification) {
		this.mailNotification = mailNotification;
	}

	public ReportJobRepositoryDestination getContentRepositoryDestination() {
		return contentRepositoryDestination;
	}

	public void setContentRepositoryDestination(
			ReportJobRepositoryDestination contentRepositoryDestination) {
		this.contentRepositoryDestination = contentRepositoryDestination;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}

	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}

	public Set getOutputFormats() {
		return outputFormats;
	}

	public void setOutputFormats(Set outputFormats) {
		this.outputFormats = outputFormats;
	}
	
	public boolean addOutputFormat(byte outputFormat) {
		return outputFormats.add(new Byte(outputFormat));
	}
	
	public boolean removeOutputFormat(byte outputFormat) {
		return outputFormats.remove(new Byte(outputFormat));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOutputLocale() {
		return outputLocale;
	}

	public void setOutputLocale(String outputLocale) {
		this.outputLocale = outputLocale;
	}
}
