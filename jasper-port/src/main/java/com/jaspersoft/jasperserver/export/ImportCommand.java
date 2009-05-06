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


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ImportCommand.java 13438 2008-05-07 11:44:11Z lucian $
 */
public class ImportCommand extends BaseExportImportCommand {
	
	public static final String DEFAULT_COMMAND_BEAN_NAME = "importCommandBean";
	public static final String METADATA_BEAN_NAME = "importCommandMetadata";
	
	protected ImportCommand() {
		super(DEFAULT_COMMAND_BEAN_NAME, METADATA_BEAN_NAME);
	}
	
	public static void main(String[] args) {
		debugArgs(args);
	
		boolean success = false;
		try {
			success = new ImportCommand().process(args);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace(System.err);
		}
		System.exit(success ? 0 : -1);
	}

}
