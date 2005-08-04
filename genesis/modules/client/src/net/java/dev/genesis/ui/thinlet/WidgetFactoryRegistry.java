/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.thinlet;

import java.util.IdentityHashMap;
import java.util.Map;

public class WidgetFactoryRegistry {
   private static final WidgetFactoryRegistry instance = new WidgetFactoryRegistry();

   private final Map registry = new IdentityHashMap();

   private WidgetFactoryRegistry() {
      register(BaseThinlet.ItemType.CELL, new DefaultWidgetFactory());
      register(BaseThinlet.ItemType.CHOICE, new DefaultWidgetFactory());
      register(BaseThinlet.ItemType.ITEM, new DefaultWidgetFactory());
   }

   public static WidgetFactoryRegistry getInstance() {
      return instance;
   }

   public Object register(BaseThinlet.ItemType type, WidgetFactory factory) {
      return registry.put(type, factory);
   }

   public void deregister() {
      registry.clear();
   }

   public void deregister(BaseThinlet.ItemType type) {
      registry.remove(type);
   }

   public WidgetFactory get(BaseThinlet.ItemType type) {
      return (WidgetFactory)registry.get(type);
   }
}
