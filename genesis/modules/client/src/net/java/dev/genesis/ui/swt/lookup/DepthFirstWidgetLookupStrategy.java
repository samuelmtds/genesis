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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class DepthFirstWidgetLookupStrategy extends
      RecursiveWidgetLookupStrategy {
   protected Widget lookupImpl(Widget widget, String name) {
      return depthFirstLookup(widget, name);
   }

   private Widget depthFirstLookup(Widget widget, String name) {
      if (widget instanceof Composite) {
         Control[] children = ((Composite) widget).getChildren();

         if (widget instanceof Shell) {
            Control[] shells = ((Shell) widget).getShells();

            for (int i = 0; i < shells.length; i++) {
               Widget w = find(shells[i], name);

               if (w != null) {
                  return w;
               }
            }
         }

         for (int i = 0; i < children.length; i++) {
            Widget w = find(children[i], name);

            if (w != null) {
               return w;
            }
         }
      }

      return null;
   }

   protected Widget find(Control control, String name) {
      if (name.equals(control.getData())) {
         return control;
      }

      registerMap((String) control.getData(), control);

      Widget w = depthFirstLookup(control, name);

      if (w != null) {
         return w;
      }

      return null;
   }
}
