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
package com.jaspersoft.jasperserver.war.common;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.jaspersoft.jasperserver.api.common.util.LocaleHelper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocalesListImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class LocalesListImpl implements LocalesList {
	
	private final LocaleHelper localeHelper;
	private List locales;
	
	public LocalesListImpl() {
		localeHelper = LocaleHelper.getInstance();
	}

	public List getLocales() {
		return locales;
	}

	public void setLocales(List locales) {
		this.locales = locales;
	}

	public UserLocale[] getUserLocales(Locale displayLocale) {
		int localesCount = locales == null ? 0 : locales.size();
		boolean addUserLocale = locales == null || !locales.contains(displayLocale);

		UserLocale[] userLocales = new UserLocale[localesCount + (addUserLocale ? 1 : 0)];

		int c = 0;

		if (addUserLocale) {
			userLocales[0] = getUserLocale(displayLocale, displayLocale);
			++c;
		}

		for (Iterator it = locales.iterator(); it.hasNext(); ++c) {
			Locale locale = (Locale) it.next();
			userLocales[c] = getUserLocale(locale, displayLocale);
		}
		
		return userLocales;
	}

	protected UserLocale getUserLocale(Locale locale, Locale displayLocale) {
		String code = localeHelper.getCode(locale);
		String name = locale.getDisplayName(displayLocale);
		return new UserLocale(code, name);
	}

}
