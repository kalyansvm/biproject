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
package com.jaspersoft.jasperserver.api.common.util;

import java.util.Locale;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocaleHelper.java 8408 2007-05-29 23:29:12Z melih $
 */
public class LocaleHelper {
	
	private static final LocaleHelper instance = new LocaleHelper();
	
	protected LocaleHelper() {
	}
	
	public static LocaleHelper getInstance() {
		return instance;
	}
	
	public String getCode(Locale locale) {
		return locale.toString();
	}
	
	public Locale getLocale(String code) {
		String language;
		String country;
		String variant;
		
		int firstSep = code.indexOf('_');
		if (firstSep < 0) {
			language = code;
			country = variant = "";
		} else {
			language = code.substring(0, firstSep);
			
			int secondSep = code.indexOf('_', firstSep + 1);
			if (secondSep < 0) {
				country = code.substring(firstSep + 1);
				variant = "";
			} else {
				country = code.substring(firstSep + 1, secondSep);
				variant = code.substring(secondSep + 1);
			}
		}
		
		return new Locale(language, country, variant);
	}
}
