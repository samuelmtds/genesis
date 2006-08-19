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
package net.java.dev.genesis.ui.swing.lookup;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BreadthFirstComponentLookupStrategy
      extends RecursiveComponentLookupStrategy {
   protected Component lookupImpl(Component component, String name) {
      return breadthFirstLookup(component, name, new ArrayList());
   }

   private Component breadthFirstLookup(Component component, String name,
      List queue) {
      if (component instanceof Container) {
         Container container = (Container) component;

         queue.addAll(Arrays.asList(container.getComponents()));

         while (!queue.isEmpty()) {
            Component first = (Component) queue.remove(0);

            if (name.equals(first.getName())) {
               return first;
            }

            Component c = breadthFirstLookup(first, name, queue);

            if (c != null) {
               return c;
            }
         }
      }

      return null;
   }
}
