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
package com.jaspersoft.jasperserver.war.action.hyperlinks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerMapFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MapHyperlinkProducerFactoryFlowFactory.java 8408 2007-05-29 23:29:12Z melih $
 */
public class MapHyperlinkProducerFactoryFlowFactory implements HyperlinkProducerFactoryFlowFactory, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map flowHyperlinkProducers;
	
	public JRHyperlinkProducerFactory getHyperlinkProducerFactory(HttpServletRequest request, HttpServletResponse response) {
		if (flowHyperlinkProducers == null) {
			return null;
		}
		
		JRHyperlinkProducerMapFactory hyperlinkProducerMapFactory = new JRHyperlinkProducerMapFactory();
		for (Iterator it = flowHyperlinkProducers.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String type = (String) entry.getKey();
			HyperlinkProducerFlowFactory flowProducer = (HyperlinkProducerFlowFactory) entry.getValue();
			
			JRHyperlinkProducer producer = flowProducer.getHyperlinkProducer(request, response);
			hyperlinkProducerMapFactory.addProducer(type, producer);
		}

		return hyperlinkProducerMapFactory;
	}

	public Map getFlowHyperlinkProducers() {
		return flowHyperlinkProducers;
	}

	public void setFlowHyperlinkProducers(Map producers) {
		this.flowHyperlinkProducers = producers;
	}

}
