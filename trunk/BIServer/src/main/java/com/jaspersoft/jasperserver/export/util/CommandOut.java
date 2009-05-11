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

package com.jaspersoft.jasperserver.export.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CommandOut.java 8408 2007-05-29 23:29:12Z melih $
 */
public class CommandOut {

	public static final String COMMAND_OUT_LOGGER = "com.jaspersoft.jasperserver.export.command";
	
	private static final CommandOut instance = new CommandOut(COMMAND_OUT_LOGGER);
	
	public static CommandOut getInstance() {
		return instance;
	}
	
	private final Log logger;

	private final boolean trace;
	private final boolean debug;
	private final boolean info;
	private final boolean warn;
	private final boolean error;
	private final boolean fatal;
	
	protected CommandOut(String loggerName) {
		logger = LogFactory.getLog(loggerName);
		
		trace = logger.isTraceEnabled();
		debug = logger.isDebugEnabled();
		info = logger.isInfoEnabled();
		warn = logger.isWarnEnabled();
		error = logger.isErrorEnabled();
		fatal = logger.isFatalEnabled();
	}
	
	public void trace(String msg) {
		if (trace) {
			logger.trace(msg);
		}
	}
	
	public void debug(String msg) {
		if (debug) {
			logger.debug(msg);
		}
	}
	
	public void info(String msg) {
		if (info) {
			logger.info(msg);
		}
	}
	
	public void warn(String msg) {
		if (warn) {
			logger.warn(msg);
		}
	}
	
	public void warn(String msg, Throwable e) {
		if (warn) {
			logger.warn(msg, e);
		}
	}
	
	public void error(String msg) {
		if (error) {
			logger.error(msg);
		}
	}
	
	public void error(String msg, Throwable e) {
		if (error) {
			logger.error(msg, e);
		}
	}
	
	public void fatal(String msg) {
		if (fatal) {
			logger.fatal(msg);
		}
	}
	
	public void fatal(String msg, Throwable e) {
		if (fatal) {
			logger.fatal(msg, e);
		}
	}
	
}
