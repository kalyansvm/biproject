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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: LocalesListImpl.java 5713 2006-11-21 11:44:34Z lucian $
 */
public class FileVirtualizerFactory implements VirtualizerFactory
{
	
	/**
	 * 
	 */
	private int maxSize = 100;
	private String directory = null;
	
	/**
	 * 
	 */
	public JRVirtualizer getVirtualizer()
	{
		if (directory == null)
		{
			return new JRFileVirtualizer(maxSize);
		}
		
		return new JRFileVirtualizer(maxSize, directory);
	}
	

	/**
	 * 
	 */
	public void disposeVirtualizer(JRVirtualizer virtualizer)
	{
		if (virtualizer != null)
		{
			virtualizer.cleanup();
		}
	}


	/**
	 * 
	 */
	public int getMaxSize() 
	{
		return maxSize;
	}

	
	/**
	 * 
	 */
	public void setMaxSize(int maxSize) 
	{
		this.maxSize = maxSize;
	}


	/**
	 * 
	 */
	public String getDirectory() 
	{
		return directory;
	}

	
	/**
	 * 
	 */
	public void setDirectory(String directory) 
	{
		this.directory = directory;
	}


}
