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

import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.Component;

import javax.swing.JComponent;

public abstract class RecursiveComponentLookupStrategy
      extends MapComponentLookupStrategy {
   private boolean skipLookupInBoundComponent = true;

   public boolean isSkipLookupInBoundComponent() {
      return skipLookupInBoundComponent;
   }

   public void setSkipLookupInBoundComponent(boolean skipGenesisBoundComponents) {
      this.skipLookupInBoundComponent = skipGenesisBoundComponents;
   }

   public Component lookup(Component component, String name) {
      Component result = super.lookup(component, name);

      if (component == null) {
         return null;
      }

      if (result != null) {
         return result;
      }

      if (name.equals(component.getName())) {
         return component;
      }

      if (doSkip(component)) {
         return null;
      }

      return lookupImpl(component, name);
   }

   protected boolean doSkip(Component component) {
      return skipLookupInBoundComponent && component instanceof JComponent &&
         (((JComponent) component).getClientProperty(SwingBinder.GENESIS_BOUND) != null);
   }

   protected abstract Component lookupImpl(Component component, String name);
}
