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
package com.jaspersoft.jasperserver.api.engine.scheduling.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportSchedulingService.java 9819 2007-08-29 17:42:42Z lucian $
 */
public interface ReportSchedulingService {

	ReportJob scheduleJob(ExecutionContext context, ReportJob job);
	
	void updateScheduledJob(ExecutionContext context, ReportJob job);
	
	List getScheduledJobs(ExecutionContext context, String reportUnitURI);
	
	List getScheduledJobs(ExecutionContext context);

	void removeScheduledJob(ExecutionContext context, long jobId);

	void removeScheduledJobs(ExecutionContext context, long[] jobIds);
	
	ReportJob getScheduledJob(ExecutionContext context, long jobId);

	ValidationErrors validateJob(ExecutionContext context, ReportJob job);

}
