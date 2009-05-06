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
package com.jaspersoft.jasperserver.api.engine.common.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.LogEvent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public interface LoggingService {

	LogEvent instantiateLogEvent();

	void log(LogEvent event);

	void update(LogEvent event);

	List getUserEvents(ExecutionContext context);

	List getUnreadEvents(ExecutionContext context);

	public int getUserEventsCount(ExecutionContext context);

	LogEvent getLogEvent(ExecutionContext context, long id);
	
	/**
	 * Get the maximum event age in days.
	 * 
	 * @return the maximum event age in days
	 * @see #setMaximumAge(int)
	 */
	int getMaximumAge();
	
	
	/**
	 * Set the maximum event age in days.
	 * <p>
	 * The event is guaranteed to be kept in the log for at least this number of days.
	 * After this period the event will be eligible for {@link #purge() purge}.
	 * 
	 * @param days the maximum event age in days
	 */
	void setMaximumAge(int days);
	
	
	/**
	 * Purges events that surpassed the {@link #getMaximumAge() maximum age}.
	 * 
	 * @see #setMaximumAge(int)
	 */
	void purge();

	void delete(ExecutionContext context, long[] events);
	
}
