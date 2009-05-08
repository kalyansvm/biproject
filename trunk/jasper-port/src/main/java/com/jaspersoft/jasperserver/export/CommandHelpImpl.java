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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.context.MessageSource;

import com.jaspersoft.jasperserver.export.util.CommandUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: CommandHelpImpl.java 13438 2008-05-07 11:44:11Z lucian $
 */
public class CommandHelpImpl implements CommandHelp {

	private static final Pattern LINE_SPLITTER_PATTERN = Pattern.compile("\\n", Pattern.MULTILINE);
	private static final char SPACE = ' ';
	
	private MessageSource messageSource;
	private String startMessage;
	private String argDescriptionMessagePrefix;
	private String argLinePrefix;
	private String argLineSeparator;

	protected int computeMaxArgNameLength(CommandMetadata commandMeta) {
		int maxArgNameLength = 0;
		for (Iterator i = commandMeta.getArgumentNames().iterator(); i.hasNext();) {
			String argName = (String) i.next();
			if (argName.length() > maxArgNameLength) {
				maxArgNameLength = argName.length();
			}
		}
		return maxArgNameLength;
	}
	
	protected String computeDescContPrefix(int maxArgNameLength) {
		int length = 
			argLinePrefix.length() 
			+ CommandUtils.ARG_PREFIX.length() 
			+ maxArgNameLength 
			+ argLineSeparator.length();
		
		char[] c = new char[length];
		Arrays.fill(c, SPACE);
		
		String descContPrefix = new String(c);
		return descContPrefix;
	}

	public void printHelp(String command, CommandMetadata commandMeta, PrintStream out) {
		String header = messageSource.getMessage(startMessage, new String[]{command}, getLocale());
		out.println(header);
		
		int maxArgNameLength = computeMaxArgNameLength(commandMeta);
		String descContPrefix = computeDescContPrefix(maxArgNameLength);
		
		for (Iterator iter = commandMeta.getArgumentNames().iterator(); iter.hasNext();) {
			String argName = (String) iter.next();
			String argDescription = messageSource.getMessage(getArgDescriptionMessagePrefix() + argName, null, getLocale());
			printArgumentHelp(out, argName, argDescription, maxArgNameLength, descContPrefix);
		}
		
		out.println();
	}

	protected Locale getLocale() {
		return Locale.getDefault();
	}

	protected void printArgumentHelp(PrintStream out, String argName, String argDescription,
			int maxArgNameLength, String descContPrefix) {
		out.print(argLinePrefix);
		out.print(CommandUtils.ARG_PREFIX);
		out.print(argName);
		for (int i = argName.length(); i < maxArgNameLength; ++i) {
			out.print(SPACE);
		}
		out.print(argLineSeparator);
		
		String[] descLines = LINE_SPLITTER_PATTERN.split(argDescription, -1);
		if (descLines.length > 0) {
			out.print(descLines[0]);
			for (int i = 1; i < descLines.length; ++i) {
				out.println();
				out.print(descContPrefix);
				out.print(descLines[i]);
			}
		}
		
		out.println();
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getArgDescriptionMessagePrefix() {
		return argDescriptionMessagePrefix;
	}

	public void setArgDescriptionMessagePrefix(String argDescriptionMessagePrefix) {
		this.argDescriptionMessagePrefix = argDescriptionMessagePrefix;
	}

	public String getArgLinePrefix() {
		return argLinePrefix;
	}

	public void setArgLinePrefix(String argLinePrefix) {
		this.argLinePrefix = argLinePrefix;
	}

	public String getArgLineSeparator() {
		return argLineSeparator;
	}

	public void setArgLineSeparator(String argLineSeparator) {
		this.argLineSeparator = argLineSeparator;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public void setStartMessage(String startMessage) {
		this.startMessage = startMessage;
	}

}