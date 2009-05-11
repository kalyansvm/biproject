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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.engine.AnnotatedObject;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;
import com.jaspersoft.jasperserver.api.engine.common.service.VirtualizerFactory;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequest;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.war.action.hyperlinks.HyperlinkProducerFactoryFlowFactory;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.RuntimeInputControlWrapper;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id: ViewReportAction.java 12830 2008-04-04 16:32:24Z angus $
 */
public class ViewReportAction extends ReportParametersAction
{
	protected final static Log log = LogFactory.getLog(ViewReportAction.class);

	public static final String REPORTUNIT_URI = "reportUnit";
    public final static String VIEW_AS_DASHBOARD_FRAME = "viewAsDashboardFrame"; 	

	private String transactionAttributeRequestParsed;
	private String flowAttributeInhibitRequestParsing;
	private String requestParameterPageIndex;
	private String flowAttributePageIndex;
	private String requestAttributeHtmlLinkHandlerFactory;
	private String flowAttributeDepth;//TODO remove?
	private String flowAttributeJasperPrintName;
	private HyperlinkProducerFactoryFlowFactory hyperlinkProducerFactory;
	private String flowAttributeIsSubflow;
	private String requestParameterReportOutput;
	private String flowAttributeReportOutput;
	private String flowAttributeUseClientTimezone;
	private SessionObjectSerieAccessor jasperPrintAccessor;
	private VirtualizerFactory virtualizerFactory;
	private Map configuredExporters;
	private String attributeReportControlsLayout;
	private String attributeReportForceControls;
	private String attributeSavedInputsState;
	private String attributeControlsHidden;
//	private boolean reportLevelConfigurable;
	private String attributeReportLocale;
	private String parameterReportLocale;
	

//	/**
//	 * @return Returns the reportLevelConfigurable.
//	 */
//	public boolean isReportLevelConfigurable() {
//		return reportLevelConfigurable;
//	}
//
//	/**
//	 * @param reportLevelConfigurable The reportLevelConfigurable to set.
//	 */
//	public void setReportLevelConfigurable(boolean reportLevelConfigurable) {
//		this.reportLevelConfigurable = reportLevelConfigurable;
//	}

	public Event checkForParams(RequestContext context) throws Exception
	{
		MutableAttributeMap flowScope = context.getFlowScope();

		Integer depth = flowScope.getInteger(getFlowAttributeDepth());
		if (depth == null) {
			depth = new Integer(0);
			flowScope.put(getFlowAttributeDepth(), depth);
		}

		boolean isSubflow = !context.getFlowExecutionContext().getActiveSession().isRoot();
		flowScope.put(getFlowAttributeIsSubflow(), Boolean.valueOf(isSubflow));
		
		// remember return Parent folder
		
		String folderURI = (String)context.getRequestParameters().get("ParentFolderUri");
		if (folderURI != null) {
		   context.getFlowScope().put("ParentFolderUri", folderURI);
		}
		
		String standAlone = (String)context.getRequestParameters().get("standAlone");
	    if (standAlone != null) {
		   context.getFlowScope().put("standAlone", standAlone);	    	
	    }

		String reportOutput = context.getRequestParameters().get(getRequestParameterReportOutput());
		if (reportOutput != null) {
			flowScope.put(getFlowAttributeReportOutput(), reportOutput);
		}

		setReportLocale(context);
		
		boolean parseRequest = toParseRequest(context);
		flowScope.put(getFlowAttributeUseClientTimezone(), Boolean.valueOf(!parseRequest));
//		flowScope.put("reportLevelConfigurable", Boolean.valueOf(isReportLevelConfigurable()));
		if (context.getRequestParameters().get("reportUnit") != null) {
		   String displayLabel = (getRepository().getResource(null, (String)context.getRequestParameters().get("reportUnit"))).getLabel();		
		   context.getRequestScope().put("reportUnitDisplayName", displayLabel);
		} else {
		   context.getRequestScope().put("reportUnitDisplayName", "");
		}
		return createWrappers(context);
	}

	protected void setReportLocale(RequestContext context) {
		Locale locale = (Locale) context.getFlowScope().get(getAttributeReportLocale(), Locale.class);
		if (locale == null) {
			String localeCode = context.getRequestParameters().get(getParameterReportLocale());
			if (localeCode == null) {
				locale = LocaleContextHolder.getLocale();
			} else {
				locale = LocaleHelper.getInstance().getLocale(localeCode);
			}
			context.getFlowScope().put(getAttributeReportLocale(), locale);
		}
	}

	protected void setReportUnitAttributes(RequestContext context, ReportUnit reportUnit) {
		super.setReportUnitAttributes(context, reportUnit);
		
		MutableAttributeMap flowScope = context.getFlowScope();
		flowScope.put(getAttributeReportControlsLayout(), new Byte(reportUnit.getControlsLayout()));
		flowScope.put(getAttributeReportForceControls(), Boolean.valueOf(reportUnit.isAlwaysPromptControls()));
	}

	protected boolean isForceControls(RequestContext context) {
		Boolean force = context.getFlowScope().getBoolean(getAttributeReportForceControls());
		return force != null && force.booleanValue();
	}

	protected boolean isControlsDialog(RequestContext context) {
		Byte layoutType = (Byte) context.getFlowScope().get(getAttributeReportControlsLayout(), Byte.class);
		return layoutType != null && layoutType.byteValue() == ReportUnit.LAYOUT_POPUP_SCREEN;
	}
	
	protected boolean toParseRequest(RequestContext context) {
		Boolean inhibitRequestParsingAttr = context.getFlowScope().getBoolean(getFlowAttributeInhibitRequestParsing());
		boolean parseRequest = inhibitRequestParsingAttr == null || !inhibitRequestParsingAttr.booleanValue();
		return parseRequest;
	}
	
	protected boolean needsInput(RequestContext context, List wrappers) {
		boolean needsInput = super.needsInput(context, wrappers);
		if (needsInput) {
			if (toParseRequest(context)) {
				boolean ok = parseRequest(context, wrappers, false);
				if (ok && !isForceControls(context)) {
					needsInput = false;
				}
			} else {
				if (validateValues(wrappers, false) && !isForceControls(context)) {
					needsInput = false;
				}
			}
		}
		return needsInput;
	}

	protected void addReportExecutionParameters(RequestContext context, Map parameterValues) {
		if (virtualizerFactory != null) {
			parameterValues.put(JRParameter.REPORT_VIRTUALIZER, virtualizerFactory.getVirtualizer());
		}
		
		setReportLocaleParameter(context, parameterValues);
	}

	protected void setReportLocaleParameter(RequestContext context, Map parameterValues) {
		Locale locale = (Locale) context.getFlowScope().get(getAttributeReportLocale(), Locale.class);
		if (locale != null) {
			parameterValues.put(JRParameter.REPORT_LOCALE, locale);
		}
	}

	public Event verifyData(RequestContext context)
	{
		ReportUnitResult result = executeReport(context);
		if (result == null) { //error
			return error();
		}
		
		setJasperPrint(context, result);

		context.getFlowScope().remove(getFlowAttributePageIndex());
		
		saveInputsState(context);
		
		return success();
	}

	
	protected static class InputsState implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private final Map values;
		
		public InputsState(Map values)
		{
			this.values = values;
		}
		
		public Map getValues()
		{
			return values;
		}
	}
	
	protected void saveInputsState(RequestContext context) {
		if (hasInputs(context)) {
			InputsState state = createInputsState(context);
			context.getFlowScope().put(getAttributeSavedInputsState(), state);
		}
	}

	protected InputsState createInputsState(RequestContext context) {
		return new InputsState(collectInputValues(context));
	}

	protected Map collectInputValues(RequestContext context) {
		Map values = new HashMap();
		List wrappers = getInputControlWrappers(context);
		for (Iterator it = wrappers.iterator(); it.hasNext();) {
			RuntimeInputControlWrapper wrapper = (RuntimeInputControlWrapper) it.next();
			values.put(wrapper.getInputControl().getName(), wrapper.getValue());
		}
		return values;
	}
	
	public Event revertToSavedInputs(RequestContext context) {
		revertInputsState(context);
		return success();
	}

	protected void revertInputsState(RequestContext context) {
		if (hasInputs(context)) {
			InputsState state = retrieveSavedInputsState(context);
			if (state != null) {
				applyInputsState(context, state);
			}
		}
	}
	
	public Event toggleTopControls(RequestContext context) {
		Boolean controlsHidden = context.getFlowScope().getBoolean(getAttributeControlsHidden(), Boolean.FALSE);
		if (!controlsHidden.booleanValue()) {
			revertInputsState(context);
		}
		context.getFlowScope().put(getAttributeControlsHidden(), Boolean.valueOf(!controlsHidden.booleanValue()));
		return success();
	}

	protected InputsState retrieveSavedInputsState(RequestContext context) {
		return (InputsState) context.getFlowScope().get(getAttributeSavedInputsState(), InputsState.class);
	}
	
	protected void applyInputsState(RequestContext context, InputsState state) {
		Map values = state.getValues();
		List wrappers = getInputControlWrappers(context);
		for (Iterator it = wrappers.iterator(); it.hasNext();) {
			RuntimeInputControlWrapper wrapper = (RuntimeInputControlWrapper) it.next();
			String name = wrapper.getInputControl().getName();
			if (values.containsKey(name)) {
				wrapper.setErrorMessage(null);
				wrapper.setValue(values.get(name));
			}
		}
	}

	protected ReportUnitResult executeReport(RequestContext context) {
		ReportUnitResult result;
		
		AttributeMap transactionAttributes = ((AnnotatedObject) context.getLastTransition()).getAttributeMap();//FIXME cast
		boolean requestParsed = transactionAttributes.getRequiredBoolean(getTransactionAttributeRequestParsed()).booleanValue();
		Map parameterValues = getParameterValues(context, requestParsed);
		if (parameterValues == null) {//error
			result = null;
		} else {
			addReportExecutionParameters(context, parameterValues);
			String reportUnitUri = (String) context.getFlowScope().get(REPORTUNIT_URI);
			if (reportUnitUri == null) {
				result = null;
			} else {
				result =
					(ReportUnitResult)getEngine().execute(
						JasperServerUtil.getExecutionContext(context),
						new ReportUnitRequest(reportUnitUri, parameterValues)
						);
			}
		}
		
		return result;
	}

	protected void setJasperPrint(RequestContext context, ReportUnitResult result) {
		ServletExternalContext externalContext = (ServletExternalContext) context.getExternalContext();
		String name = getJasperPrintAccessor().putObject(externalContext.getRequest(), result);

        if (context.getRequestParameters().get(VIEW_AS_DASHBOARD_FRAME) == null ||
                !context.getRequestParameters().get(VIEW_AS_DASHBOARD_FRAME).equals("true")) {
            removeCurrentJasperPrint(context);
        }    

        context.getFlowScope().put(getFlowAttributeJasperPrintName(), name);
	}

	public Event navigate(RequestContext context) {
		Integer pageIndex = (Integer) context.getRequestParameters().getNumber(getRequestParameterPageIndex(), Integer.class);
		if (pageIndex == null) {
			context.getFlowScope().remove(getFlowAttributePageIndex());
		} else {
			context.getFlowScope().put(getFlowAttributePageIndex(), pageIndex);
		}
		return success();
	}


	public Event prepareReportView(RequestContext context) {
		if (getHyperlinkProducerFactory() != null) {
			context.getRequestScope().put(getRequestAttributeHtmlLinkHandlerFactory(), getHyperlinkProducerFactory());
		}
		context.getRequestScope().put("configuredExporters", getConfiguredExporters());
		return success();
	}


	public Event cleanSession(RequestContext context) {
        removeCurrentJasperPrint(context);

		return success();
	}

	protected void removeCurrentJasperPrint(RequestContext context) {
		String jasperPrintName = context.getFlowScope().getString(getFlowAttributeJasperPrintName());
		if (jasperPrintName != null) {
			ServletExternalContext externalContext = (ServletExternalContext) context.getExternalContext();
			
			ReportUnitResult result = (ReportUnitResult)getJasperPrintAccessor().getObject(externalContext.getRequest(), jasperPrintName);
			if (result != null && virtualizerFactory != null)
			{
				virtualizerFactory.disposeVirtualizer(result.getVirtualizer());
			}
			getJasperPrintAccessor().removeObject(externalContext.getRequest(), jasperPrintName);
		}
	}

	protected void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	public Event resetInputsToDefaults(RequestContext context) {
		resetValuesToDefaults(context);
		return success();
	}
	
	public String getTransactionAttributeRequestParsed() {
		return transactionAttributeRequestParsed;
	}

	public void setTransactionAttributeRequestParsed(String transactionAttributeRequestParsed) {
		this.transactionAttributeRequestParsed = transactionAttributeRequestParsed;
	}

	public String getFlowAttributeInhibitRequestParsing() {
		return flowAttributeInhibitRequestParsing;
	}

	public void setFlowAttributeInhibitRequestParsing(
			String flowAttributeInhibitRequestParsing) {
		this.flowAttributeInhibitRequestParsing = flowAttributeInhibitRequestParsing;
	}

	public String getFlowAttributePageIndex() {
		return flowAttributePageIndex;
	}

	public void setFlowAttributePageIndex(String flowAttributePageIndex) {
		this.flowAttributePageIndex = flowAttributePageIndex;
	}

	public String getRequestParameterPageIndex() {
		return requestParameterPageIndex;
	}

	public void setRequestParameterPageIndex(String requestParameterPageIndex) {
		this.requestParameterPageIndex = requestParameterPageIndex;
	}

	public String getRequestAttributeHtmlLinkHandlerFactory() {
		return requestAttributeHtmlLinkHandlerFactory;
	}

	public void setRequestAttributeHtmlLinkHandlerFactory(
			String requestAttributeHtmlLinkHandlerFactory) {
		this.requestAttributeHtmlLinkHandlerFactory = requestAttributeHtmlLinkHandlerFactory;
	}

	public String getFlowAttributeDepth() {
		return flowAttributeDepth;
	}

	public void setFlowAttributeDepth(String flowAttributeDepth) {
		this.flowAttributeDepth = flowAttributeDepth;
	}

	public String getFlowAttributeJasperPrintName() {
		return flowAttributeJasperPrintName;
	}

	public void setFlowAttributeJasperPrintName(
			String flowAttributeJasperPrintName) {
		this.flowAttributeJasperPrintName = flowAttributeJasperPrintName;
	}

	public HyperlinkProducerFactoryFlowFactory getHyperlinkProducerFactory() {
		return hyperlinkProducerFactory;
	}

	public void setHyperlinkProducerFactory(
			HyperlinkProducerFactoryFlowFactory hyperlinkProducerFactory) {
		this.hyperlinkProducerFactory = hyperlinkProducerFactory;
	}

	public String getFlowAttributeIsSubflow() {
		return flowAttributeIsSubflow;
	}

	public void setFlowAttributeIsSubflow(String requestAttributeIsSubflow) {
		this.flowAttributeIsSubflow = requestAttributeIsSubflow;
	}

	public String getFlowAttributeReportOutput() {
		return flowAttributeReportOutput;
	}

	public void setFlowAttributeReportOutput(String flowAttributeReportOutput) {
		this.flowAttributeReportOutput = flowAttributeReportOutput;
	}

	public String getRequestParameterReportOutput() {
		return requestParameterReportOutput;
	}

	public void setRequestParameterReportOutput(String requestParameterReportOutput) {
		this.requestParameterReportOutput = requestParameterReportOutput;
	}

	public String getFlowAttributeUseClientTimezone() {
		return flowAttributeUseClientTimezone;
	}

	public void setFlowAttributeUseClientTimezone(
			String flowAttributeUseClientTimezone) {
		this.flowAttributeUseClientTimezone = flowAttributeUseClientTimezone;
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	/**
	 *
	 */
	public VirtualizerFactory getVirtualizerFactory() {
		return virtualizerFactory;
	}

	/**
	 *
	 */
	public void setVirtualizerFactory(VirtualizerFactory virtualizerFactory) {
		this.virtualizerFactory = virtualizerFactory;
	}

	/**
	 * @return Returns the configuredExporters.
	 */
	public Map getConfiguredExporters() {
		return configuredExporters;
	}

	/**
	 * @param configuredExporters The configuredExporters to set.
	 */
	public void setConfiguredExporters(Map configuredExporters) {
		this.configuredExporters = configuredExporters;
	}

	public String getAttributeReportControlsLayout() {
		return attributeReportControlsLayout;
	}

	public void setAttributeReportControlsLayout(
			String attributeReportControlsLayout) {
		this.attributeReportControlsLayout = attributeReportControlsLayout;
	}

	public String getAttributeReportForceControls() {
		return attributeReportForceControls;
	}

	public void setAttributeReportForceControls(String attributeReportForceControls) {
		this.attributeReportForceControls = attributeReportForceControls;
	}

	public String getAttributeSavedInputsState() {
		return attributeSavedInputsState;
	}

	public void setAttributeSavedInputsState(String attributeLastInputsState) {
		this.attributeSavedInputsState = attributeLastInputsState;
	}

	public String getAttributeControlsHidden() {
		return attributeControlsHidden;
	}

	public void setAttributeControlsHidden(String attributeControlsHidden) {
		this.attributeControlsHidden = attributeControlsHidden;
	}

	public String getAttributeReportLocale() {
		return attributeReportLocale;
	}

	public void setAttributeReportLocale(String attributeReportLocale) {
		this.attributeReportLocale = attributeReportLocale;
	}

	public String getParameterReportLocale() {
		return parameterReportLocale;
	}

	public void setParameterReportLocale(String parameterReportLocale) {
		this.parameterReportLocale = parameterReportLocale;
	}
}
