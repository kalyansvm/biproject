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
package com.tonbeller.jpivot.core;

import java.util.EventObject;

/**
 * informs a listener that the model has been initialized
 * 
 * @author sbirney
 */
public class ModelInitializedEvent extends EventObject {

  /**
   * Constructor for ModelChangeEvent.
   * @param arg0
   */
  public ModelInitializedEvent(Object arg0) {
    super(arg0);
  }

}
