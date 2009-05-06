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

import org.springframework.beans.factory.FactoryBean;

import net.sf.jasperreports.engine.JRPrintHyperlink;

import com.jaspersoft.jasperserver.api.engine.common.service.impl.WebDeploymentInformation;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.BaseReportExecutionHyperlinkProducerFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportExecutionHyperlinkProducerFactory.java 13061 2008-04-18 14:59:55Z lucian $
 */
public class ReportExecutionHyperlinkProducerFactory extends BaseReportExecutionHyperlinkProducerFactory implements FactoryBean {
	
	private WebDeploymentInformation deploymentInformation;
	
	public Object getObject() {
		return new HyperlinkProducer();
	}

	public Class getObjectType() {
		return HyperlinkProducer.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	protected String getServerURLPrefix() {
		return getDeploymentInformation().getDeploymentURI();
	}
	
	public class HyperlinkProducer extends BaseHyperlinkProducer {
		protected void appendHyperlinkStart(JRPrintHyperlink hyperlink, StringBuffer sb) {
			sb.append(getServerURLPrefix());
		}
		
		protected void appendAdditionalParameters(JRPrintHyperlink hyperlink, StringBuffer sb) {
			// nothing to add
		}
	}

	public WebDeploymentInformation getDeploymentInformation() {
		return deploymentInformation;
	}

	public void setDeploymentInformation(
			WebDeploymentInformation deploymentInformation) {
		this.deploymentInformation = deploymentInformation;
	}
	
}
