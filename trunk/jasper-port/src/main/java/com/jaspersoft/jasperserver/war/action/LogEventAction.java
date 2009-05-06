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
package com.jaspersoft.jasperserver.war.action;

import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.engine.common.service.LoggingService;

public class LogEventAction extends FormAction
{

	private LoggingService loggingService;
	private static final String SHOW_ALL_EVENTS = "showAll";
	private static final String SHOW_UNREAD_EVENTS = "showUnread";
	private static final String SHOW_EVENTS = "showEvents";
	private MessageSource messages;//FIXME not used

	public LoggingService getLoggingService()
	{
		return loggingService;
	}

	public void setLoggingService(LoggingService loggingService)
	{
		this.loggingService = loggingService;
	}

	public MessageSource getMessages()
	{
		return messages;
	}

	public void setMessages(MessageSource messages)
	{
		this.messages = messages;
	}

	public Event getEvents(RequestContext context) throws Exception
	{
		List events = null;

		String show = (String) context.getFlowScope().get(SHOW_EVENTS);
		if (SHOW_ALL_EVENTS.equals(show))
			events = loggingService.getUserEvents(null);
		else if (SHOW_UNREAD_EVENTS.equals(show))
			events = loggingService.getUnreadEvents(null);
		else
			events = loggingService.getUserEvents(null);

		context.getFlowScope().put("events", events);

		return success();
	}
	/**
	 *
	 */
	public Event changeEventsType(RequestContext context) throws Exception
	{
		String show = context.getRequestParameters().get(SHOW_EVENTS);
		context.getFlowScope().put(SHOW_EVENTS, show);
		return success();
	}


	private long[] getIds(RequestContext context)
	{
		if (context.getRequestParameters().contains("selectedIds"))
		{
			Long[] idArray = (Long[]) context.getRequestParameters().getArray("selectedIds", Long.class);

			long[] ids = new long[idArray.length];
			for (int i = 0; i < idArray.length; i++)
				ids[i] = idArray[i].longValue();
			return ids;
		}
		return null;
	}

	public Event delete(RequestContext context) throws Exception
	{
		long[] ids = getIds(context);
		if (ids != null)
			loggingService.delete(null, ids);
		return success();
	}

	public Event markAsRead(RequestContext context) throws Exception
	{
		long[] ids = getIds(context);
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				LogEvent event = loggingService.getLogEvent(null, ids[i]);
				if (event.getState() != LogEvent.STATE_READ) {
					event.setState(LogEvent.STATE_READ);
					loggingService.update(event);
				}
			}
		}
		return success();
	}


	public Event markAsUnread(RequestContext context) throws Exception
	{
		long[] ids = getIds(context);
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				LogEvent event = loggingService.getLogEvent(null, ids[i]);
				if (event.getState() != LogEvent.STATE_UNREAD) {
					event.setState(LogEvent.STATE_UNREAD);
					loggingService.update(event);
				}
			}
		}
		return success();
	}


	/**
	 *
	 */
	public Event setupViewForm(RequestContext context) throws Exception
	{
		String id = context.getRequestParameters().get("eventId");
		try {
			LogEvent event = loggingService.getLogEvent(null, Long.parseLong(id));
			if (event.getState() == LogEvent.STATE_UNREAD) {
				event.setState(LogEvent.STATE_READ);
				loggingService.update(event);
			}
			context.getRequestScope().put("event", event);
			return success();
		} catch(NumberFormatException e) {
			return error();
		}
	}

}
