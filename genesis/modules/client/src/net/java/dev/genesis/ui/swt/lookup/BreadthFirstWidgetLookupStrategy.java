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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.java.dev.genesis.ui.swt.SwtBinder;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class BreadthFirstWidgetLookupStrategy
      extends RecursiveWidgetLookupStrategy {
   protected Widget lookupImpl(Widget widget, String name) {
      return breadthFirstLookup(widget, name, new ArrayList());
   }

   private Widget breadthFirstLookup(Widget widget, String name,
      List queue) {
      if (widget instanceof Composite) {
         if (widget instanceof Shell) {
            queue.addAll(Arrays.asList(((Shell)widget).getShells()));
         }

         Composite composite = (Composite) widget;

         queue.addAll(Arrays.asList(composite.getChildren()));

         while (!queue.isEmpty()) {
            Widget first = (Widget) queue.remove(0);

            if (name.equals(first.getData(SwtBinder.NAME_PROPERTY))) {
               return first;
            }

            Widget w = breadthFirstLookup(first, name, queue);

            if (w != null) {
               return w;
            }
         }
      }

      return null;
   }
}
