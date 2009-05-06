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
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsPersistenceService.java 9819 2007-08-29 17:42:42Z lucian $
 */
public interface ReportJobsPersistenceService {

	ReportJob saveJob(ExecutionContext context, ReportJob job);

	ReportJob updateJob(ExecutionContext context, ReportJob job);
	
	ReportJob loadJob(ExecutionContext context, ReportJobIdHolder jobId);
	
	void deleteJob(ExecutionContext context, ReportJobIdHolder jobId);

	List listJobs(ExecutionContext context, String reportUnitURI);

	List listJobs(ExecutionContext context);

}
