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

import java.util.HashMap;
import java.util.Map;

public class FormStateImpl implements FormState {
   private final Map enabledMap = new HashMap();
   private final Map visibleMap = new HashMap();
   private final Map changedMap = new HashMap();
   private final Map valuesMap = new HashMap();
   private final Map dataProvidedMap = new HashMap();
   private final Map dataProvidedIndexesMap = new HashMap();

   public FormStateImpl() {
   }

   public FormStateImpl(FormState state) {
      this(state, state.getValuesMap());
   }

   public FormStateImpl(FormState state, Map values) {
      enabledMap.putAll(state.getEnabledMap());
      visibleMap.putAll(state.getVisibleMap());
      changedMap.putAll(state.getChangedMap());
      valuesMap.putAll(values);
      dataProvidedMap.putAll(state.getDataProvidedMap());
      dataProvidedIndexesMap.putAll(state.getDataProvidedIndexesMap());
   }

   public Map getChangedMap() {
      return changedMap;
   }

   public Map getEnabledMap() {
      return enabledMap;
   }

   public Map getVisibleMap() {
      return visibleMap;
   }

   public Map getValuesMap() {
      return valuesMap;
   }

   public Map getDataProvidedMap() {
      return dataProvidedMap;
   }

   public Map getDataProvidedIndexesMap() {
      return dataProvidedIndexesMap;
   }
}