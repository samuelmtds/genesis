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

import net.java.dev.genesis.registry.Registry;

public class WidgetFactoryRegistry {
   private static final WidgetFactoryRegistry instance = new WidgetFactoryRegistry();
	private final Registry registry = new Registry();

   private WidgetFactoryRegistry() {
      registry.register(Object.class, new DefaultWidgetFactory());
   }

   public static WidgetFactoryRegistry getInstance() {
      return instance;
   }

	public WidgetFactory get(Class clazz) {
		return (WidgetFactory) registry.get(clazz);
	}

	public WidgetFactory get(Class clazz, boolean superClass) {
		return (WidgetFactory) registry.get(clazz, superClass);
	}

	public WidgetFactory get(Object o) {
		return (WidgetFactory) registry.get(o);
	}
	
	public WidgetFactory register(Class clazz, WidgetFactory wf) {
      return (WidgetFactory) registry.register(clazz, wf);
   }

   public void deregister(Class clazz) {
      registry.deregister(clazz);
   }
}