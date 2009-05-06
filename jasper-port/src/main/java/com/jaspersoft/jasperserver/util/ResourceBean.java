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
package com.jaspersoft.jasperserver.util;

/**
 * @author tkavanagh
 * @version $Id: ResourceBean.java 8408 2007-05-29 23:29:12Z melih $
 */

import java.util.Date;

/**
 * The following information applies to all "bean" classes used to marshal and 
 * unmarshal between objects and xml.
 * 
 * Castor is used to handle the marshal/unmarshal between java object and xml.
 * To work nicely with castor, which will handle moving between an object bean
 * and it's XML representation, we make sure that all methods conform to the
 * get/set pattern. Particularly, we make sure that boolean methods do this. 
 * So, instead of hasData(), we will use getHasData() and setHasData(). This 
 * way castor's output XML is cleaner and easier to read.
 * 
 * As an example, for a FileResource image the output took the following form:
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <file-resource-bean is-reference="false" is-new="false" version="0" has-data="false">
 *   <name>JRLogo</name>
 *   <resource-type>com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource</resource-type>
 *   <creation-date>2006-05-16T18:20:30.000-07:00</creation-date>
 *   <uri-string>/images/JRLogo</uri-string>
 *   <parent-folder>/images</parent-folder>
 *   <label>JR logo</label>
 *   <description>JR logo</description>
 *   <file-type>img</file-type>
 * </file-resource-bean> 
 * 
 * Primitive types (boolean, int) get written as attributes (ie. is-reference). 
 * Primitive type are always written out with a value. If they are un-set they
 * receive a default value, in this example, of false or zero.
 * 
 * Object types (String, Date) get written out as their "toString" values, and
 * are written out as node elements (ie. <name>JRLogo</name>). 
 * If an object type is null, then there is no element written out. For,
 * example, in the xml generated above referenceUri was null so you 
 * do not see it included in the output xml.
 * 
 * // todo: move comments to ResourceBean
 */

/**
 * 
 * The classes that inherit from ResourceBean are used to go back and forth
 * between the java objects they represent and XML. We are currently using
 * the Castor library in order to marshal and unmarshal between java and 
 * XML. 
 * 
 * One thing to note is that Castor handles primitive fields such as int and
 * boolean as XML attributes of the top level XML tag for the particular 
 * object. Castor automatically gives default values for these primitives.
 * So, if something like boolean hasData() has not been explicitly set, Castor
 * will give it a default value of "false". For int the default value is 
 * zero.
 * 
 * Furthermore, if a String value such as String setDescrption() does not
 * have a value (is null) it will not be included in the output (unmarshal)
 * XML. So, this is different than how primitives are dealt with.
 *
 *
 */

public abstract class ResourceBean {

	/*
	 * The following come from the Resource interface 
	 */
	private String name;
	private String label;
	private String description;
	private String uriString;
	private String parentFolder;
	private String resourceType;	// this is package + classname
	private boolean isNew;
	private Date creationDate;
	private int version;
	
	/*
	 * Special export-import processing variables.
	 * 
	 * The following variables are added to simplify export-import processing
	 * 
	 * Regarding isUriReference:
	 *   // todo: ### redo these comments
	 *   FileResource has similar field. This is to 
	 *   help with other resources such as data sources
	 *   which can be local to a ReportUnit or a reference
	 *   (ie essentially the same as a link)
	 */
	
	private String productReleaseVersion;	// JasperServer version compatability
	
	private boolean isUriReference;			// see comments above 
	
	private String resourceBeanType;		// hold the class name
	
	/*
	 * getters and setters
	 */
	
	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUriString() {
		return uriString;
	}
	
	public void setUriString(String uriString) {
		this.uriString = uriString;
	}

	public String getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(String parentFolder) {
		this.parentFolder = parentFolder;
	}

	public String getResourceType() {
		return resourceType;
	}
	
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public boolean getIsNew() {
		return isNew;
	}
	
	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getProductReleaseVersion() {
		return productReleaseVersion;
	}

	public void setProductReleaseVersion(String productReleaseVersion) {
		this.productReleaseVersion = productReleaseVersion;
	}

	public boolean getIsUriReference() {
		return isUriReference;
	}

	public void setIsUriReference(boolean isUriReference) {
		this.isUriReference = isUriReference;
	}

	public String getResourceBeanType() {
		return resourceBeanType;
	}

	public void setResourceBeanType(String resourceBeanType) {
		this.resourceBeanType = resourceBeanType;
	}
}
