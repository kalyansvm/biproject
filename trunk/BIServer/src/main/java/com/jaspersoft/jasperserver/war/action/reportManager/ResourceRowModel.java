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

package com.jaspersoft.jasperserver.war.action.reportManager;

import java.util.Date;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author achan
 *
 */
public class ResourceRowModel implements Serializable {

	private String name;
	private String hiddenName;
	private String id;
	private String resourceUrl;
	private String description;
	private String type;
	private String resourceType;
	private Date creationDate;
	private boolean isScheduled;
	private boolean selected;
	private boolean hasSavedOptions = false;
	private boolean isContentType = false;
	private boolean isWritable = false;
	private boolean isDeletable = false;
	private List listOfOptions = new ArrayList();
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isHasSavedOptions() {
		return hasSavedOptions;
	}
	public void setHasSavedOptions(boolean hasSavedOptions) {
		this.hasSavedOptions = hasSavedOptions;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isScheduled() {
		return isScheduled;
	}
	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	/**
	 * @return Returns the hiddenName.
	 */
	public String getHiddenName() {
		return hiddenName;
	}
	/**
	 * @param hiddenName The hiddenName to set.
	 */
	public void setHiddenName(String hiddenName) {
		this.hiddenName = hiddenName;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public boolean isContentType() {
		return isContentType;
	}
	public void setContentType(boolean isContentType) {
		this.isContentType = isContentType;
	}
	public boolean isDeletable() {
		return isDeletable;
	}
	public void setDeletable(boolean isDeletable) {
		this.isDeletable = isDeletable;
	}
	public boolean isWritable() {
		return isWritable;
	}
	public void setWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}
	public List getListOfOptions() {
		return listOfOptions;
	}
	public void setListOfOptions(List listOfOptions) {
		this.listOfOptions = listOfOptions;
	}
	public void addOption(ResourceRowModel res) {
		this.listOfOptions.add(res);
	}
	
	
	
	
	
}
