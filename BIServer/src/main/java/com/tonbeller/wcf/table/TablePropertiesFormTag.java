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
package com.tonbeller.wcf.table;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentTag;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.form.FormDocument;
import com.tonbeller.wcf.utils.I18nReplacer;
import com.tonbeller.wcf.utils.ResourceLocator;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * creates a FormComponent
 *
 * @author av
 */
public class TablePropertiesFormTag extends ComponentTag {

  private String xmlUri;
  private String table;
  private boolean bookmarkable = false;
  private boolean closable = true;

  private static Logger logger = Logger.getLogger(TablePropertiesFormTag.class);

  /**
   * loads a form from an xml file and registeres it with the controller.
   */
  public Component createComponent(RequestContext context) throws JspException {
    try {

      Locale locale = Resources.instance().getLocaleContextHolderLocale();
      if (locale == null) {
    	  locale = context.getLocale();
      }
      URL url = ResourceLocator.getResource(context.getServletContext(), locale, xmlUri);
      Document doc = XmlUtils.parse(url);

      //In replaceI18n(...) it is examined whether "bundle" - attribute available
	  I18nReplacer replacer = I18nReplacer.instance(Resources.instance(getClass()));
	  if (replacer != null) {
		  replacer.replaceAll(doc);	  
	  }
	  else {
		  FormDocument.replaceI18n(context, doc, null);
	  }

      // find the bean model
      TableComponent tc = (TableComponent) context.getModelReference(table);

      // create the component
      TablePropertiesFormComponent fc= new TablePropertiesFormComponent(id, null, doc, tc);
      fc.setCloseable(closable);
      fc.setBookmarkable(bookmarkable);
      return fc;

    } catch (MalformedURLException e) {
      logger.error("exception caught", e);
      throw new JspException(e);
    }
  }

  public void setBookmarkable(boolean b) {
    bookmarkable = b;
  }

  public void setTable(String string) {
    table = string;
  }

  public void setXmlUri(String string) {
    xmlUri = string;
  }

  public void setClosable(boolean b) {
    closable = b;
  }

}
