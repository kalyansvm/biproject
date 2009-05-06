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
import java.util.Date;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobRuntimeInformation.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportJobRuntimeInformation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final byte STATE_UNKNOWN = 0;
	public static final byte STATE_NORMAL = 1;
	public static final byte STATE_EXECUTING = 2;
	public static final byte STATE_PAUSED = 3;
	public static final byte STATE_COMPLETE = 4;
	public static final byte STATE_ERROR = 5;
	
	private byte state;
	private Date previousFireTime;
	private Date nextFireTime;
	
	public ReportJobRuntimeInformation() {
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
	
}
