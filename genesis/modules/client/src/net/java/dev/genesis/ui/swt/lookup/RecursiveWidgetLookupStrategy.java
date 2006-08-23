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

import net.java.dev.genesis.ui.binding.RecursiveLookupStrategy;
import net.java.dev.genesis.ui.swt.SWTBinder;

import org.eclipse.swt.widgets.Widget;

public abstract class RecursiveWidgetLookupStrategy
      extends RecursiveLookupStrategy {
   protected String getRealName(Object object) {
      if (object instanceof Widget) {
         return (String) ((Widget)object).getData(SWTBinder.NAME_PROPERTY);
      }

      return null;
   }

   protected boolean isAlreadyBound(Object object) {
      return object instanceof Widget
            && ((Widget) object).getData(SWTBinder.GENESIS_BOUND) != null;
   }

   protected Object lookupImpl(Object object, String name) {
      if (object instanceof Widget) {
         return lookupImpl((Widget) object, name);
      }

      return null;
   }

   protected abstract Widget lookupImpl(Widget widget, String name);
}
