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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsScheduler.java 8408 2007-05-29 23:29:12Z melih $
 */
public interface ReportJobsScheduler {
	
	void scheduleJob(ExecutionContext context, ReportJob job);

	void rescheduleJob(ExecutionContext context, ReportJob job);

	void removeScheduledJob(ExecutionContext context, long jobId);
	
	void addReportSchedulerListener(ReportSchedulerListener listener);
	
	void removeReportSchedulerListener(ReportSchedulerListener listener);
	
	ReportJobRuntimeInformation[] getJobsRuntimeInformation(ExecutionContext context, long[] jobIds);
	
	void validate(ReportJob job, ValidationErrors errors);

}
