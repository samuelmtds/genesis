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

import java.util.List;
import java.util.Map;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;

public interface FormControllerListener {
   public void enabledConditionsChanged(Map updatedEnabledConditions);
   public void visibleConditionsChanged(Map updatedVisibleConditions);
   public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception;
   public void afterInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception;
   public void dataProvidedListChanged(DataProviderMetadata metadata, 
         List items) throws Exception;
   public void valuesChanged(Map updatedValues) throws Exception;
}