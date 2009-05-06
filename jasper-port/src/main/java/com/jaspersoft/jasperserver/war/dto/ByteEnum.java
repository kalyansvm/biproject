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
package com.jaspersoft.jasperserver.war.dto;

import java.io.Serializable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ByteEnum.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ByteEnum implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private byte code;
	private String labelMessage;
	
	public ByteEnum() {
	}
	
	public ByteEnum(byte code, String labelMessage) {
		this.code = code;
		this.labelMessage = labelMessage;
	}

	public byte getCode() {
		return code;
	}

	public void setCode(byte format) {
		this.code = format;
	}

	public String getLabelMessage() {
		return labelMessage;
	}

	public void setLabelMessage(String labelMessage) {
		this.labelMessage = labelMessage;
	}
	
	

}
