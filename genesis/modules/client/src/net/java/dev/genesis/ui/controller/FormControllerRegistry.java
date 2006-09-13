/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class FormControllerRegistry {
   private static final FormControllerRegistry instance = new FormControllerRegistry();

   private final Map registry = new WeakHashMap();

   private FormControllerRegistry() {
   }

   public static FormControllerRegistry getInstance() {
      return instance;
   }

   public FormController get(Object form) {
      WeakReference ref = (WeakReference) registry.get(form);
      if (ref == null) {
         return null;
      }

      return (FormController) ref.get();
   }

   public void register(Object form, FormController controller) {
      // controller is stored as a weak reference because controller
      // references the form itself. When the controller is garbage 
      // collected, the form itself can be removed from the map.
      registry.put(form, new WeakReference(controller));
   }
}
