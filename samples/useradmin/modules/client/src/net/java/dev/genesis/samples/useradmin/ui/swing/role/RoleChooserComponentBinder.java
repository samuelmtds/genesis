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
package net.java.dev.genesis.samples.useradmin.ui.swing.role;

import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swing.components.AbstractComponentBinder;

import java.awt.Component;
import java.util.Collections;

public class RoleChooserComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new RoleChooserBoundField(binder, (RoleChooser) component,
         fieldMetadata);
   }

   public class RoleChooserBoundField extends AbstractBoundMember
         implements BoundField {
      private final RoleChooser component;
      private final FieldMetadata fieldMetadata;
      private final RoleChooserListener listener;

      public RoleChooserBoundField(SwingBinder binder, RoleChooser component,
         FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.component.addRoleChooserListener(listener = createRoleChooserListener());
      }

      protected RoleChooserListener createRoleChooserListener() {
         return new RoleChooserListener() {
               public void roleChanged(RoleChooser chooser) {
                  try {
                     getBinder().getFormController()
                           .populate(Collections.singletonMap(
                              fieldMetadata.getName(), chooser.getRole()),
                           getBinder().getConverters());
                  } catch (Exception e) {
                     getBinder().handleException(e);
                  }
               }
            };
      }

      public void setValue(Object value) throws Exception {
         component.setRole((Role) value);
      }

      public void unbind() {
         if (listener != null) {
            component.removeRoleChooserListener(listener);
         }
      }
   }
}
