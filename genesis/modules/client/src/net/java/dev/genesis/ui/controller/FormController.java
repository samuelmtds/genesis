/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
import java.util.Map;

import net.java.dev.genesis.ui.metadata.FormMetadata;

public interface FormController {
   public void setForm(Object form);
   public void setFormMetadata(FormMetadata form);
   public Object getForm();
   public FormMetadata getFormMetadata();
   public void setup() throws Exception;
   public void populate(Map properties) throws Exception;
   public Map getEnabledMap();
   public Map getVisibleMap();
   public Map getChangedMap();
   public Collection getCallActions();
   public Collection getDataProviderActions();
   public void reset() throws Exception;
   public void save() throws Exception;
   public void update() throws Exception;
}