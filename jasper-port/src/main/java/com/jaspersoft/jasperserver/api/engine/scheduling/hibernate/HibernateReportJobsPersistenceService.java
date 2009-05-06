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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportJobsInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateReportJobsPersistenceService.java 11000 2007-12-03 17:37:40Z lucian $
 */
public class HibernateReportJobsPersistenceService extends HibernateDaoImpl 
	implements ReportJobsPersistenceService, ReportJobsInternalService {
	
	protected static final Log log = LogFactory.getLog(HibernateReportJobsPersistenceService.class);
	
	private SecurityContextProvider securityContextProvider;

	public HibernateReportJobsPersistenceService() {
		super();
	}

	public SecurityContextProvider getSecurityContextProvider() {
		return securityContextProvider;
	}

	public void setSecurityContextProvider(SecurityContextProvider securityContextProvider) {
		this.securityContextProvider = securityContextProvider;
	}

	public ReportJob saveJob(ExecutionContext context, final ReportJob job) {
		return saveJob(context, job, true);
	}

	public ReportJob saveJob(ExecutionContext context, final ReportJob job, final boolean setContextUsername) {
		return (ReportJob) executeWriteCallback(new DaoCallback() {
			public Object execute() {
				HibernateTemplate hibernateTemplate = getHibernateTemplate();
				
				PersistentReportJob persistentJob = new PersistentReportJob();
				persistentJob.copyFrom(job, hibernateTemplate);
				
				String username;
				if (setContextUsername) {
					username = getContextUsername();
				} else {
					username = job.getUsername();
				}
				persistentJob.setUsername(username);
				
				persistentJob.cascadeSave(hibernateTemplate);
				hibernateTemplate.save(persistentJob);
				
				hibernateTemplate.flush();//force job id generation
				ReportJob clientJob = persistentJob.toClient();
				
				if (log.isDebugEnabled()) {
					log.debug("Saved report job " + clientJob.getId() + " for report " + clientJob.getSource().getReportUnitURI());
				}
				
				return clientJob;
			}
		});
	}

	protected String getContextUsername() {
		return getSecurityContextProvider().getContextUsername();
	}

	public ReportJob updateJob(ExecutionContext context, final ReportJob job) {
		return (ReportJob) executeWriteCallback(new DaoCallback() {
			public Object execute() {
				HibernateTemplate hibernateTemplate = getHibernateTemplate();
				PersistentReportJob persistentJob = findJob(job.getId(), true);
				persistentJob.copyFrom(job, hibernateTemplate);
				
				persistentJob.cascadeSave(hibernateTemplate);
				hibernateTemplate.update(persistentJob);
				hibernateTemplate.flush();//force version updates
				
				ReportJob clientJob = persistentJob.toClient();
				
				if (log.isDebugEnabled()) {
					log.debug("Updated report job " + clientJob.getId());
				}
				
				return clientJob;
			}
		});
	}

	public ReportJob loadJob(ExecutionContext context, ReportJobIdHolder jobIdHolder) {
		final long jobId = jobIdHolder.getId();
		return (ReportJob) executeCallback(new DaoCallback() {
			public Object execute() {
				PersistentReportJob persistentJob = findJob(jobId, false);
				ReportJob job;
				if (persistentJob == null) {
					job = null;
				} else {
					job = persistentJob.toClient();
				}
				return job;
			}
		});
	}

	public void deleteJob(ExecutionContext context, ReportJobIdHolder jobIdHolder) {
		deleteJob(jobIdHolder.getId());
	}

	public void deleteJob(final long jobId) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				PersistentReportJob job = findJob(jobId, false);
				if (job != null) {
					deleteJob(job);					
				} else {
					if (log.isInfoEnabled()) {
						log.info("Report job with id " + jobId + " not found for deletion");
					}
				}
				return null;
			}
		}, false);
	}

	public List listJobs(ExecutionContext context, final String reportUnitURI) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				List persistentJobs = getReportUnitJobs(reportUnitURI);
				List jobs = toClientSummary(persistentJobs);
				return jobs;
			}
		});
	}

	public List listJobs(ExecutionContext context) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				List persistentJobs = getAllJobs();
				List jobs = toClientSummary(persistentJobs);
				return jobs;
			}
		});
	}

	protected PersistentReportJob findJob(long jobId, boolean required) {
		PersistentReportJob job = (PersistentReportJob) getHibernateTemplate().get(PersistentReportJob.class, new Long(jobId));
		if (job == null && required) {
			throw new ReportJobNotFoundException(jobId);
		}
		return job;
	}

	public String getJobOwner(final long jobId) {
		return (String) executeCallback(new DaoCallback() {
			public Object execute() {
				PersistentReportJob persistentJob = findJob(jobId, true);
				return persistentJob.getUsername();
			}
		});
	}

	public long[] deleteReportUnitJobs(final String reportUnitURI) {
		return (long[]) executeWriteCallback(new DaoCallback() {
			public Object execute() {
				List jobs = getReportUnitJobs(reportUnitURI);
				return deletePersistentJobs(jobs);
			}
		}, false);
	}

	protected void deleteJob(PersistentReportJob job) {
		job.delete(getHibernateTemplate());
		
		if (log.isDebugEnabled()) {
			log.debug("Deleted job " + job.getId());
		}
	}

	protected List getReportUnitJobs(final String reportUnitURI) {
		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		DetachedCriteria crit = DetachedCriteria.forClass(PersistentReportJob.class);
		crit.add(Restrictions.eq("source.reportUnitURI", reportUnitURI));
		List persistentJobs = hibernateTemplate.findByCriteria(crit);
		return persistentJobs;
	}

	protected List getAllJobs() {
		return getHibernateTemplate().loadAll(PersistentReportJob.class);
	}

	public List toClientSummary(List persistentJobs) {
		List jobs = new ArrayList(persistentJobs.size());
		for (Iterator it = persistentJobs.iterator(); it.hasNext();) {
			PersistentReportJob persistentJob = (PersistentReportJob) it.next();
			jobs.add(persistentJob.toSummary());
		}
		return jobs;
	}

	public long[] deletePersistentJobs(List jobs) {
		long[] jobIds;
		if (jobs == null || jobs.isEmpty()) {
			jobIds = null;
		} else {
			jobIds = new long[jobs.size()];
			int c = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++c) {
				PersistentReportJob job = (PersistentReportJob) it.next();
				jobIds[c] = job.getId();
				deleteJob(job);
			}
		}
		return jobIds;
	}

	public long[] updateReportUnitURI(final String oldURI, final String newURI) {
		return (long[]) executeWriteCallback(new DaoCallback() {
			public Object execute() {
				List jobs = getReportUnitJobs(oldURI);
				return updateReportURI(jobs, newURI);
			}
		}, false);
	}

	protected Object updateReportURI(List jobs, final String newURI) {
		long[] jobIds;
		if (jobs == null || jobs.isEmpty()) {
			jobIds = null;
		} else {
			jobIds = new long[jobs.size()];
			int c = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++c) {
				PersistentReportJob job = (PersistentReportJob) it.next();
				jobIds[c] = job.getId();
				
				job.getSource().setReportUnitURI(newURI);
				getHibernateTemplate().update(job);
				
				if (log.isDebugEnabled()) {
					log.debug("Updated report URI of job " + job.getId() + " to " + newURI);
				}
			}
		}
		return jobIds;
	}

}
