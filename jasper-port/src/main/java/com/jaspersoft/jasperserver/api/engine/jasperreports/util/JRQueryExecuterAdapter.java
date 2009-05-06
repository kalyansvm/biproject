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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRQueryChunk;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseDataset;
import net.sf.jasperreports.engine.base.JRBaseField;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.base.JRBaseParameter;
import net.sf.jasperreports.engine.base.JRBaseQuery;
import net.sf.jasperreports.engine.base.JRBaseQueryChunk;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRQueryExecuterUtils;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRQueryExecuterAdapter.java 11039 2007-12-04 16:16:58Z lucian $
 */
public class JRQueryExecuterAdapter {
	
	private static final Log log = LogFactory.getLog(JRQueryExecuterAdapter.class);
	
	public static OrderedMap executeQuery(final Query query, final String keyColumn, final String[] resultColumns, Map parameterValues) {
		try {
			JRQueryExecuterFactory queryExecuterFactory = JRQueryExecuterUtils.getQueryExecuterFactory(query.getLanguage());
			
			JRParameter[] dsParameters = getDatasetParameters(queryExecuterFactory, parameterValues);			
			JRField[] fields = getDatasetFields(keyColumn, resultColumns);			
			JRQuery dsQuery = new JSQuery(query);
			JSDataset dataset = new JSDataset(query.getName(), dsParameters, fields, dsQuery);
			
			Map parametersMap = new HashMap();
			for (int i = 0; i < dsParameters.length; i++) {
				JRParameter parameter = dsParameters[i];
				parametersMap.put(parameter.getName(), parameter);
			}
			
			JRQueryExecuter executer = queryExecuterFactory.createQueryExecuter(dataset, parametersMap);
			try {
				JRDataSource ds = executer.createDatasource();
				OrderedMap values = new LinkedMap();
				while (ds.next()) {
					Object valueColumn = ds.getFieldValue(dataset.getField(keyColumn));
					
					String[] visibleColumnValues = new String[resultColumns.length];
					for (int idx = 0; idx < resultColumns.length; ++idx) {
						visibleColumnValues[idx] = (String) ds.getFieldValue(dataset.getField(resultColumns[idx]));
					}

					values.put(valueColumn, visibleColumnValues);
				}
				
				return values;
			} finally {
				executer.close();
			}
		} catch (JRException e) {
			log.error("Error while executing query", e);
			throw new JSExceptionWrapper(e);
		}

	}


	protected static JRField[] getDatasetFields(final String keyColumn, final String[] resultColumns) {
		JRField[] fields = new JRField[resultColumns.length + 1];
		fields[0] = new ColumnField(keyColumn, Object.class);
		for (int idx = 0; idx < resultColumns.length; ++idx) {
			fields[idx + 1] = new ColumnField(resultColumns[idx], String.class);
		}
		return fields;
	}

	
	protected static JRParameter[] getDatasetParameters(JRQueryExecuterFactory queryExecuterFactory, Map parameterValues) {
		boolean jdbcConnectionParam = parameterValues.containsKey(JRParameter.REPORT_CONNECTION);
		
		List dsParameters = new ArrayList();
		
		dsParameters.add(new ValueParameter(JRParameter.REPORT_PARAMETERS_MAP, Map.class, parameterValues));
		dsParameters.add(new ValueParameter(JRParameter.REPORT_MAX_COUNT, Connection.class, null));

		if (jdbcConnectionParam) {
			Object value = parameterValues.get(JRParameter.REPORT_CONNECTION);
			dsParameters.add(new ValueParameter(JRParameter.REPORT_CONNECTION, Connection.class, value));
		}
		
		Object[] builtinParameters = queryExecuterFactory.getBuiltinParameters();
		if (builtinParameters != null) {
			for (int i = 0; i < builtinParameters.length - 1; i += 2) {
				String name = (String) builtinParameters[i];
				Class type = (Class) builtinParameters[i + 1];
				Object value = parameterValues.get(name);
				dsParameters.add(new ValueParameter(name, type, value));
			}
		}
		
		JRParameter[] params = new JRParameter[dsParameters.size()];
		return (JRParameter[]) dsParameters.toArray(params);
	}

	
	public static JRQueryExecuter createQueryExecuter(JasperReport report, Map parameterValues, Query query) {
		try {
			JRQueryExecuterFactory queryExecuterFactory = JRQueryExecuterUtils.getQueryExecuterFactory(query.getLanguage());

			ReportQueryDataset dataset = new ReportQueryDataset(report, query, queryExecuterFactory);

			JRBaseObjectFactory jrObjectFactory = new ShallowJRObjectFactory(report);			
			Map parametersMap = new HashMap();
			JRParameter[] parameters = dataset.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				JRParameter parameter = parameters[i];
				Object value = parameterValues.get(parameter.getName());
				ParameterValueDecorator parameterValue = new ParameterValueDecorator(parameter, value, jrObjectFactory);
				parametersMap.put(parameter.getName(), parameterValue);
			}

			JRQueryExecuter executer = queryExecuterFactory.createQueryExecuter(dataset, parametersMap);
			return executer;
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	
	protected static class ColumnField extends JRBaseField {
		
		public ColumnField(final String column, final Class type) {
			this.name = column;
			this.valueClass = type;
			this.valueClassName = type.getName();
		}
		
	}
	
	
	protected static class JSQuery extends JRBaseQuery {
		
		private final JRQueryChunk[] chunks;
		
		public JSQuery(final String queryLanguage, final String queryString) {
			this.language = queryLanguage;
			
			this.chunks = new JRQueryChunk[]{new JSQueryChunk(queryString)};
		}
			
		public JSQuery(Query query) {
			this(query.getLanguage(), query.getSql());
		}

		public JRQueryChunk[] getChunks() {
			return chunks;
		}
		
	}
	
	protected static class JSQueryChunk extends JRBaseQueryChunk {
		
		public JSQueryChunk(String text) {
			this.text = text;
			this.type = JRQueryChunk.TYPE_TEXT;
		}
		
	}
	
	
	protected static class JSDataset extends JRBaseDataset {
		
		private final Map fieldsMap;
		
		public JSDataset(final String name, final JRParameter[] parameters, final JRField[] fields, final JRQuery query) {
			super(false);
			
			this.name = name;
			this.parameters = parameters;
			this.fields = fields;
			this.query = query;
			
			fieldsMap = new HashMap();
			for (int i = 0; i < fields.length; i++) {
				JRField field = fields[i];
				fieldsMap.put(field.getName(), field);
			}
		}
		
		public JRField getField(String column) {
			return (JRField) fieldsMap.get(column);
		}
		
	}
	
	protected static class ValueParameter extends JRBaseParameter implements JRValueParameter {
		
		private final Object value;
		
		public ValueParameter(String name, Class type, Object value) {
			this.name = name;
			this.valueClass = type;
			this.valueClassName = type.getName();
			
			this.isSystemDefined = true;
			this.isForPrompting = false;
			
			this.value = value;
		}
		
		public Object getValue() {
			return value;
		}
		
	}
	
	
	protected static class ReportQueryDataset implements JRDataset {
		
		private final JRDataset reportDataset;
		private final JRParameter[] parameters;
		private final JRQuery query;
		
		public ReportQueryDataset(JasperReport report, Query query, JRQueryExecuterFactory queryExecuterFactory) {
			this.reportDataset = report.getMainDataset();
			this.query = getQuery(query);
			this.parameters = getParams(queryExecuterFactory);
		}
		
		private JRQuery getQuery(Query query) {
			JRDesignQuery designQuery = new JRDesignQuery();
			designQuery.setLanguage(query.getLanguage());
			designQuery.setText(query.getSql());
			return designQuery;
		}

		private JRParameter[] getParams(JRQueryExecuterFactory queryExecuterFactory) {
			JRParameter[] paramArray;
			Object[] builtinParameters = queryExecuterFactory.getBuiltinParameters();
			if (builtinParameters == null || builtinParameters.length == 0) {
				paramArray = this.reportDataset.getParameters();
			} else {
				JRParameter[] reportParams = this.reportDataset.getParameters();
				List params = new ArrayList(reportParams.length + builtinParameters.length / 2);
				
				Set paramNames = new HashSet();
				for (int i = 0; i < reportParams.length; i++) {
					JRParameter parameter = reportParams[i];
					params.add(parameter);
					paramNames.add(parameter.getName());
				}
				
				for (int i = 0; i < builtinParameters.length - 1; i += 2) {
					String name = (String) builtinParameters[i];
					if (!paramNames.contains(name)) {
						Class type = (Class) builtinParameters[i + 1];
						params.add(new ValueParameter(name, type, null));
					}
				}

				paramArray = new JRParameter[params.size()];
				paramArray = (JRParameter[]) params.toArray(paramArray);
			}
			return paramArray;
		}

		public String getName() {
			return reportDataset.getName();
		}

		public String getScriptletClass() {
			return reportDataset.getScriptletClass();
		}

		public JRParameter[] getParameters() {
			return parameters;
		}

		public JRQuery getQuery() {
			return query;
		}

		public JRField[] getFields() {
			return reportDataset.getFields();
		}

		public JRVariable[] getVariables() {
			return reportDataset.getVariables();
		}

		public JRGroup[] getGroups() {
			return reportDataset.getGroups();
		}

		public boolean isMainDataset() {
			return true;
		}

		public String getResourceBundle() {
			return reportDataset.getResourceBundle();
		}

		public byte getWhenResourceMissingType() {
			return reportDataset.getWhenResourceMissingType();
		}

		public void setWhenResourceMissingType(byte type) {
			//nothing
		}

		public JRPropertiesMap getPropertiesMap() {
			return reportDataset.getPropertiesMap();
		}

		public JRPropertiesHolder getParentProperties() {
			return reportDataset.getParentProperties();
		}

		public boolean hasProperties() {
			return reportDataset.hasProperties();
		}

		public JRExpression getFilterExpression() {
			return reportDataset.getFilterExpression();
		}
		
		public JRSortField[] getSortFields() {
			return reportDataset.getSortFields();
		}
		
		public Object clone() {
			throw new JSException("Clone not supported");
		}
		
	}
	
	
	protected static class ParameterValueDecorator extends JRBaseParameter implements JRValueParameter {
		
		private final Object value;
		
		public ParameterValueDecorator(final JRParameter parameter, final Object value, JRBaseObjectFactory jrObjectFactory) {
			super(parameter, jrObjectFactory);
			this.value = value;
		}

		public Object getValue() {
			return value;
		}
		
	}
	
	protected static class ShallowJRObjectFactory extends JRBaseObjectFactory {

		protected ShallowJRObjectFactory(JRDefaultStyleProvider defaultStyleProvider) {
			super(defaultStyleProvider);
		}

		public JRExpression getExpression(JRExpression expression) {
			return expression;
		}
		
	}

}
