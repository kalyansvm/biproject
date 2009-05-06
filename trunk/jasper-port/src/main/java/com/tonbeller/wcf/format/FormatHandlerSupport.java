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
package com.tonbeller.wcf.format;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.tonbeller.tbutils.res.Resources;

/**
 * base class for FormatHandlers
 */
public abstract class FormatHandlerSupport implements FormatHandler {
  protected String name;
  protected String pattern;
  protected String errorMessage;
  protected ArrayList patterns = new ArrayList();
  protected Resources resources = Resources.instance();
  protected Locale locale;

  /**
   * adds a pattern for a specific locale
   */
  public void addPattern(Pattern p) {
    patterns.add(p);
  }

  /**
   * returns the pattern for a given locale. First it checks if a Pattern child
   * exists if a child exists with the same language as locale. If not, the pattern
   * property of this is returned.
   * @param userPattern a pattern that the user may have provided. If not null 
   * and not empty, the userPattern will be returned.
   */
  protected String findPattern(String userPattern) {
    
    if (userPattern != null && userPattern.length() > 0)
      return userPattern;
      
    Iterator it = patterns.iterator();

    while (it.hasNext()) {
      Pattern p = (Pattern) it.next();

      if (locale.getLanguage().equals(p.getLanguage())) {
        return p.getPattern();
      }
    }

    return this.getPattern();
  }

  protected String getErrorMessage(String userInput) {
	if (resources != null) {
		String errorMessage = null;
		// ja-os: resource bundle approach
		if (this instanceof RequiredStringHandler || userInput.length() == 0) {
			errorMessage = getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.noValue");
		}
		else {
			if (this instanceof IntegerHandler) {
				if (getName().equals("int")) {
					errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.invalidInteger");
				}
				else if (getName().equals("posint")) {
					errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.notPositiveInteger");
				}
			}
			else if (this instanceof DateHandler) {
				if (getName().equals("date")) {
					errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.invalidDate");
				}
				else if (getName().equals("dateTime")) {
					errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.invalidDateTime");
				}
			}
			else if (this instanceof DoubleHandler || this instanceof DoubleNaNHandler) {
				errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.invalidDecimal");
			}
			else if (this instanceof RegexHandler) {
				errorMessage = userInput + getMessageFromResourceBundle("wcf.FormatHandlerSupport.error.invalidEmailAddr");
			}
		}
		return errorMessage;
	}
	else {
		// old jpivot approach
    String errorMessage = null;

    Iterator it = patterns.iterator();

    while (it.hasNext()) {
      Pattern p = (Pattern) it.next();

      if (locale.getLanguage().equals(p.getLanguage())) {
        errorMessage = p.getErrorMessage();
      }
    }

    // no locale specific error message found?
    if (errorMessage == null) {
      errorMessage = getErrorMessage();
    }

    if (errorMessage == null) {
      return userInput;
    }

    return MessageFormat.format(errorMessage, new Object[] { userInput });
	}
	
  }
  
  private String getMessageFromResourceBundle(String key) {
	  return resources.getResourceBundle().getMessage(key, null, getLocale());
  }

  /**
   * Returns the name.
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the default pattern
   * @return String
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Sets the name.
   * @param name The name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the default pattern.
   * @param pattern The pattern to set
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Returns the errorMessage.
   * @return String
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the errorMessage.
   * @param errorMessage The errorMessage to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Returns the locale.
   * @return Locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Sets the locale.
   * @param locale The locale to set
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}