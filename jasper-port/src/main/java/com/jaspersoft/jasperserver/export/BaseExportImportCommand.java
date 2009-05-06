/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
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

package com.jaspersoft.jasperserver.export;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import com.jaspersoft.jasperserver.export.util.CommandUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseExportImportCommand.java 13438 2008-05-07 11:44:11Z lucian $
 */
public abstract class BaseExportImportCommand {
	
	protected static final Log log = LogFactory.getLog(BaseExportImportCommand.class);
	
	protected static final CommandOut commandOut = CommandOut.getInstance();

	public static final String ARG_CONFIG_FILES = "configFiles";
	public static final String ARG_CONFIG_RESOURCES = "configResources";
	public static final String ARG_COMMAND_BEAN = "commandBean";
	public static final String ARG_HELP = "help";
	
	public static final String HELP_BEAN_NAME = "helpPrintBean";
	
	private final String defaultBeanName;
	private final String commandMetaBeanName;
	
	protected BaseExportImportCommand(String defaultBeanName, String commandMetaBeanName) {
		this.defaultBeanName = defaultBeanName;
		this.commandMetaBeanName = commandMetaBeanName;
	}

	protected static void debugArgs(String[] args) {
		if (log.isDebugEnabled()) {
			for (int i = 0; i < args.length; ++i) {
				log.debug("arg #" + i + " = \"" + args[i] + "\"");
			}
		}
	}

	protected boolean process(String[] args) {
		Parameters exportParameters = parseArgs(args);
		ConfigurableApplicationContext ctx = createSpringContext(exportParameters);
		try {
			boolean success = true;
			CommandMetadata metadataBean = getCommandMetadataBean(ctx);
			if (exportParameters.hasParameter(ARG_HELP)) {
				CommandHelp helpBean = getHelpBean(ctx);
				helpBean.printHelp(args[0], metadataBean, System.out);
			} else {
				CommandBean commandBean = getCommandBean(exportParameters, ctx);
				try {
					metadataBean.validateParameters(exportParameters);
					commandBean.process(exportParameters);
				} catch (JSExceptionWrapper e) {
					throw e;
				} catch (JSException e) {
					success = false;
					if (log.isInfoEnabled()) {
						log.info(e.getMessage(), e);
					}
					
					String message = ctx.getMessage(e.getMessage(), e.getArgs(), Locale.getDefault());
					System.err.println(message);
				}
			}
			return success;
		} finally {
			ctx.close();
		}
	}

	protected Parameters parseArgs(String[] args) {
		Parameters exportParameters = CommandUtils.parse(args);
		return exportParameters;
	}

	protected ConfigurableApplicationContext createSpringContext(Parameters exportParameters) {
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader configReader = new XmlBeanDefinitionReader(ctx);
		registerConfig(exportParameters.getParameterValues(ARG_CONFIG_FILES), configReader, fileSystemResourceFactory);
		registerConfig(exportParameters.getParameterValues(ARG_CONFIG_RESOURCES), configReader, classPathResourceFactory);
		ctx.refresh();
		return ctx;
	}

	protected static interface SpringResourceFactory {
		Resource create(String location);
	}
	
	protected static final SpringResourceFactory fileSystemResourceFactory = new SpringResourceFactory() {
		public Resource create(String location) {
			commandOut.debug("Loading Spring configuration file " + location);
			return new FileSystemResource(location);
		}
	};
	
	protected static final SpringResourceFactory classPathResourceFactory = new SpringResourceFactory() {
		public Resource create(String location) {
			commandOut.debug("Loading Spring configuration classpath resource " + location);
			return new ClassPathResource(location);
		}
	};
	
	protected void registerConfig(String[] locations, XmlBeanDefinitionReader reader, SpringResourceFactory resourceFactory) {
		if (locations != null && locations.length > 0) {
			for (int i = 0; i < locations.length; i++) {
				String location = locations[i];
				Resource resource = resourceFactory.create(location);
				reader.loadBeanDefinitions(resource);
			}
		}
	}

	protected String getConfigSeparator() {
		return System.getProperty("path.separator");
	}

	protected CommandBean getCommandBean(Parameters exportParameters, ApplicationContext ctx) {
		String beanName = getCommandBeanName(exportParameters);
		return (CommandBean) ctx.getBean(beanName, CommandBean.class);
	}

	protected CommandHelp getHelpBean(ApplicationContext ctx) {
		return (CommandHelp) ctx.getBean(HELP_BEAN_NAME, CommandHelp.class);
	}

	protected CommandMetadata getCommandMetadataBean(ApplicationContext ctx) {
		return (CommandMetadata) ctx.getBean(commandMetaBeanName, CommandMetadata.class);
	}
	
	protected String getCommandBeanName(Parameters exportParameters) {
		String beanName = exportParameters.getParameterValue(ARG_COMMAND_BEAN);

		if (beanName == null) {
			commandOut.debug("Using default " + defaultBeanName + " command bean");
			
			beanName = defaultBeanName;
		} else {
			commandOut.debug("Using " + beanName + " command bean");
		}

		return beanName;
	}

}
