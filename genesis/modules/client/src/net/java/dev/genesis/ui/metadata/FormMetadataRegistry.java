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
package net.java.dev.genesis.ui.metadata;

import java.util.HashMap;
import java.util.Map;

public class FormMetadataRegistry {
   private static final FormMetadataRegistry instance = new FormMetadataRegistry();

   private final Map registry = new HashMap();

   private FormMetadataRegistry() {
   }

   public static FormMetadataRegistry getInstance() {
      return instance;
   }

   public FormMetadata get(Class clazz) {
      return (FormMetadata) registry.get(clazz);
   }

   public FormMetadata register(Class clazz, FormMetadata meta) {
      return (FormMetadata) registry.put(clazz, meta);
   }
}
