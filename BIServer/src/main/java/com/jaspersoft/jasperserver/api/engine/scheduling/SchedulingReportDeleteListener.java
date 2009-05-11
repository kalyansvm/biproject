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
package com.jaspersoft.jasperserver.api.engine.scheduling;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerSupport;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.FolderMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositoryListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.ResourceMoveEvent;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SchedulingReportDeleteListener.java 11000 2007-12-03 17:37:40Z lucian $
 */
public class SchedulingReportDeleteListener extends RepositoryEventListenerSupport implements RepositoryListener {

	private ReportSchedulingInternalService schedulingService;
	
	public void onResourceDelete(Class resourceItf, String resourceURI) {
		if (ReportUnit.class.isAssignableFrom(resourceItf)) {
			schedulingService.removeReportUnitJobs(resourceURI);
		}
	}

	public ReportSchedulingInternalService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(
			ReportSchedulingInternalService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public void folderMoved(FolderMoveEvent folderMove) {
		// NOOP
	}

	public void resourceMoved(ResourceMoveEvent resourceMove) {
		if (ReportUnit.class.isAssignableFrom(resourceMove.getResourceType())) {
			schedulingService.updateReportUnitURI(
					resourceMove.getOldResourceURI(), resourceMove.getNewResourceURI());
		}
	}

}
