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

package com.jaspersoft.jasperserver.war.security;

import java.beans.PropertyEditorSupport;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.ConfigAttributeEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FlowDefinitionSourceEditor.java 8408 2007-05-29 23:29:12Z melih $
 */
public class FlowDefinitionSourceEditor extends PropertyEditorSupport {

    public void setAsText(String s) throws IllegalArgumentException {
        FlowDefinitionSource source = new FlowDefinitionSource();

        if (s != null && s.length() > 0) {
            PropertiesEditor propertiesEditor = new PropertiesEditor();
            propertiesEditor.setAsText(s);
            Properties props = (Properties) propertiesEditor.getValue();

            ConfigAttributeEditor configAttribEd = new ConfigAttributeEditor();
            for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Entry) it.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();

                configAttribEd.setAsText(value);
                ConfigAttributeDefinition attr = (ConfigAttributeDefinition) configAttribEd.getValue();

                source.addFlow(name, attr);
            }
        }

        setValue(source);
    }

}
