/*
 * The Genesis Project
 * Copyright (C) 2004-2007  Summa Technologies do Brasil Ltda.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.java.dev.genesis.ui.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

public interface FormController {
   public static final String FORM_METADATA_KEY = "genesis:formMetadata";
   public static final String CURRENT_STATE_KEY = "genesis:currentState";

   public void setForm(Object form);
   public void setFormMetadata(FormMetadata form);
   public Object getForm();
   public FormMetadata getFormMetadata();

   public void setMaximumEvaluationTimes(int times);
   public int getMaximumEvaluationTimes();

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange);
   public boolean isResetOnDataProviderChange();

   public void addFormControllerListener(FormControllerListener listener);
   public boolean removeFormControllerListener(FormControllerListener listener);
   public Collection getFormControllerListeners();
   public void fireAllEvents(FormControllerListener listener) throws Exception;

   public void setup() throws Exception;
   public boolean isSetup();

   public void populate(Map properties, Map converters) throws Exception;
   public void invokeAction(String actionName, Map stringProperties) 
         throws Exception;
   public void updateSelection(DataProviderMetadata dataProviderMetadata, 
         int[] selected) throws Exception;
   public void resetSelection(DataProviderMetadata dataProviderMetadata,
         List dataProvided) throws Exception;
   public void update() throws Exception;
   public FormState getFormState() throws Exception;
   public void reset(FormState state) throws Exception;
   public ScriptContext getScriptContext();
}