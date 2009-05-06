/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.war.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SortedServletResourcePatternResolver.java 10096 2007-09-17 13:58:16Z lucian $
 */
public class SortedServletResourcePatternResolver extends ServletContextResourcePatternResolver {

	private final static String NAME_SUFFIX = ".xml";
	private final static int NAME_SUFFIX_LENGTH = 4;

	private final static Comparator RESOURCE_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			String name1 = ((Resource) o1).getFilename();
			if (name1.endsWith(NAME_SUFFIX)) {
				name1 = name1.substring(0, name1.length() - NAME_SUFFIX_LENGTH);
			}
			
			String name2 = ((Resource) o2).getFilename();
			if (name2.endsWith(NAME_SUFFIX)) {
				name2 = name2.substring(0, name2.length() - NAME_SUFFIX_LENGTH);
			}

			return name1.compareTo(name2);
		}
	};
	
	public SortedServletResourcePatternResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	protected Set doFindPathMatchingFileResources(Resource rootDirResource, String subPattern)
			throws IOException {
		Set resources = super.doFindPathMatchingFileResources(rootDirResource, subPattern);
		if (resources != null && resources.size() > 1) {
			resources = sortResources(resources);
		}
		return resources;
	}

	protected Set sortResources(Set resourceSet) {
		ArrayList resourceList = new ArrayList(resourceSet);
		Collections.sort(resourceList, RESOURCE_COMPARATOR);
		LinkedHashSet sortedSet = new LinkedHashSet(resourceList);
		return sortedSet;
	}

}
