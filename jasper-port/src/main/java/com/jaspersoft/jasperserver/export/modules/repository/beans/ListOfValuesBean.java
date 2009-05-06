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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;


/**
 * @author tkavanagh
 * @version $Id: ListOfValuesBean.java 8408 2007-05-29 23:29:12Z melih $
 */


public class ListOfValuesBean extends ResourceBean {

	
	private ListOfValuesItemBean[] items;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsFrom(lov);
	}

	protected void copyItemsFrom(ListOfValues lov) {
		ListOfValuesItem[] lovItems = lov.getValues();
		if (lovItems == null || lovItems.length == 0) {
			items = null;
		} else {
			items = new ListOfValuesItemBean[lovItems.length];
			for (int i = 0; i < lovItems.length; i++) {
				ListOfValuesItem lovItem = lovItems[i];
				ListOfValuesItemBean itemBean = new ListOfValuesItemBean(lovItem);
				items[i] = itemBean;
			}
		}
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		ListOfValues lov = (ListOfValues) res;
		copyItemsTo(lov);
	}

	protected void copyItemsTo(ListOfValues lov) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				ListOfValuesItemBean item = items[i];
				ListOfValuesItem lovItem = new ListOfValuesItemImpl();
				item.copyTo(lovItem);
				lov.addValue(lovItem);
			}
		}
	}

	public ListOfValuesItemBean[] getItems() {
		return items;
	}

	public void setItems(ListOfValuesItemBean[] values) {
		this.items = values;
	}

}
