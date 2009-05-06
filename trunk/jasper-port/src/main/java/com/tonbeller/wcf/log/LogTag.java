/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 *
 */
package com.tonbeller.wcf.log;

import java.net.URL;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.w3c.dom.Document;

import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentTag;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.form.FormDocument;
import com.tonbeller.wcf.utils.I18nReplacer;
import com.tonbeller.wcf.utils.ResourceLocator;
import com.tonbeller.wcf.utils.XmlUtils;

public class LogTag extends ComponentTag {

  String xmlUri;
  String logDir;

  /**
   * loads a form from an xml file and registeres it with the controller.
   */
  public Component createComponent(RequestContext context) throws JspException {
    try {

      // parse the form xml
      Locale locale = Resources.instance().getLocaleContextHolderLocale();
      if (locale == null) {
    	  locale = context.getLocale();
      }
      URL url =
        ResourceLocator.getResource(context.getServletContext(), locale, xmlUri);
      Document doc = XmlUtils.parse(url);

      //In replaceI18n(...) it is examined whether "bundle" - attribute available
      I18nReplacer replacer = I18nReplacer.instance(Resources.instance(getClass()));
      if (replacer != null) {
          replacer.replaceAll(doc);
      }
      else {
          FormDocument.replaceI18n(context, doc, null);
      }

      // create the component
      return new LogForm(id, null, doc, logDir);
    }
    catch (Exception e) {
      throw new JspException(e);
    }
  }

  public void setXmlUri(String xmlUri) {
    this.xmlUri = xmlUri;
  }

  /**
   * Sets the logDir.
   * @param logDir The logDir to set
   */
  public void setLogDir(String logDir) {
    this.logDir = logDir;
  }

}
