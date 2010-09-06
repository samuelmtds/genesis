/*
 * The Genesis Project
 * Copyright (C) 2005-2010  Summa Technologies do Brasil Ltda.
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

import javax.swing.JMenu;

public class DepthFirstComponentLookupStrategy
      extends RecursiveComponentLookupStrategy {
   protected Component lookupImpl(Component component, String name) {
      return depthFirstLookup(component, name);
   }

   private Component depthFirstLookup(Component component, String name) {
      if (component instanceof Container) {
         Container container = (Container) component;

         if (component instanceof JMenu) {
            container = ((JMenu) container).getPopupMenu();
         }

         Component[] components = container.getComponents();

         for (int i = 0; i < components.length; i++) {
            if (name.equals(components[i].getName())) {
               return components[i];
            }

            registerMap(components[i].getName(), components[i]);

            if (doSkip(components[i])) {
               continue;
            }

            Component c = depthFirstLookup(components[i], name);

            if (c != null) {
               return c;
            }
         }
      }

      return null;
   }
}
