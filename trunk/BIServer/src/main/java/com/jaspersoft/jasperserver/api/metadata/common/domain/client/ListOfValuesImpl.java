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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ListOfValuesImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ListOfValuesImpl extends ResourceImpl implements ListOfValues
{
	private List values = new ArrayList();

	public ListOfValuesItem[] getValues()
	{
		return (ListOfValuesItem[]) values.toArray(new ListOfValuesItem[values.size()]);
	}

	public void addValue(ListOfValuesItem item)
	{
		values.add(item);
	}

	public void removeValue(ListOfValuesItem item)
	{
		values.remove(item);
	}

	protected Class getImplementingItf() {
		return ListOfValues.class;
	}
}
