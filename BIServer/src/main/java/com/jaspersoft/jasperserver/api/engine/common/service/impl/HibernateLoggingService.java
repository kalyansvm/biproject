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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import com.jaspersoft.jasperserver.api.common.domain.impl.LogEventImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.LoggingService;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HibernateLoggingService extends HibernateDaoSupport implements LoggingService {

	private static final Log log = LogFactory.getLog(HibernateLoggingService.class);
	
	private static final long EVENT_ID_NEW = 0l;

	private SecurityContextProvider securityContextProvider;
	private int maximumAge;

	public SecurityContextProvider getSecurityContextProvider() {
		return securityContextProvider;
	}

	public void setSecurityContextProvider(
			SecurityContextProvider securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}

	public int getMaximumAge() {
		return maximumAge;
	}

	public void setMaximumAge(int days) {
		this.maximumAge = days;
	}
	
	public LogEvent instantiateLogEvent() {
		return createLogEvent();
	}

	protected LogEvent createLogEvent() {
		return new LogEventImpl();
	}

	public void log(LogEvent event) {
		prepareForSave(event);
		getHibernateTemplate().save(event);
	}

	public void update(LogEvent event)
	{
		getHibernateTemplate().saveOrUpdate(event);
	}

	protected void prepareForSave(LogEvent event) {
		event.setId(EVENT_ID_NEW);
		if (event.getOccurrenceDate() == null) {
			event.setOccurrenceDate(new Date());
		}
		event.setUsername(securityContextProvider.getContextUsername());
	}

	public void purge() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -getMaximumAge());
		Date last = cal.getTime();
		
		if (log.isDebugEnabled()) {
			log.debug("Purging log events older than " + last);
		}

		getHibernateTemplate().bulkUpdate("delete LogEventImpl e where e.occurrenceDate < ?", last);
	}


	public void delete(ExecutionContext context, long[] eventIds)
	{
		for (int i = 0; i < eventIds.length; i++) {
			LogEvent event = getLogEvent(context, eventIds[i]);
			getHibernateTemplate().delete(event);
		}
	}


	public List getUserEvents(ExecutionContext context) {
		String username = securityContextProvider.getContextUsername();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LogEventImpl.class);
		if (username != null) {
			criteria.add(Restrictions.eq("username", username));
		}
		criteria.addOrder(Order.desc("occurrenceDate"));

		List events = getHibernateTemplate().findByCriteria(criteria);

		return events;
	}

	public List getUnreadEvents(ExecutionContext context)
	{
		String username = securityContextProvider.getContextUsername();

		DetachedCriteria criteria = DetachedCriteria.forClass(LogEventImpl.class);
		criteria.add(Restrictions.eq("state", new Byte(LogEvent.STATE_UNREAD)));
		if (username != null) {
			criteria.add(Restrictions.eq("username", username));
		}
		criteria.addOrder(Order.desc("occurrenceDate"));

		List events = getHibernateTemplate().findByCriteria(criteria);

		return events;
	}

	public LogEvent getLogEvent(ExecutionContext context, long id) {
		// TODO check username
		LogEvent event = (LogEvent) getHibernateTemplate().get(LogEventImpl.class, new Long(id));
		if (event == null) {
			log.debug("Log event with id " + id + " not found");
		}
		return event;
	}

	public int getUserEventsCount(ExecutionContext context)
	{
		//FIXME ?
		String username = securityContextProvider.getContextUsername();

		List result = getHibernateTemplate().find("select count(*) from LogEventImpl where state=?", new Byte(LogEvent.STATE_UNREAD));

		if (result != null) {
			Integer size = (Integer) result.get(0);
			return size.intValue();
		}
		return 0;
	}
}
