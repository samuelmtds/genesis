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
package net.java.dev.genesis.ui.swt.lookup;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import net.java.dev.genesis.ui.swt.SwtBinder;

import org.eclipse.swt.widgets.Widget;

public class MapWidgetLookupStrategy implements WidgetLookupStrategy {
   private Map widgets = new HashMap();
   private Map identityMap = new IdentityHashMap();

   public Widget lookup(Widget widget, String name) {
      return (Widget) widgets.get(name);
   }

   public Widget register(String name, Widget widget) {
      if (identityMap.containsKey(widget)) {
         throw new IllegalArgumentException("Widget " + widget +
            " is already bound");
      }

      identityMap.put(widget, name);
      final Object oldValue = widgets.put(name, widget);
      if (oldValue != null) {
         identityMap.remove(oldValue);
      }

      return widget;
   }

   public String getName(Widget widget) {
      String name = (String) identityMap.get(widget);

      if (name == null) {
         return (String) widget.getData(SwtBinder.NAME_PROPERTY);
      }

      return name;
   }
}
