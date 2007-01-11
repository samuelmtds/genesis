/*
 * The Genesis Project
 * Copyright (C) 2005-2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JSpinnerComponentBinder extends AbstractComponentBinder {
   private final boolean useFormatter;

   public JSpinnerComponentBinder() {
      this(false);
   }

   public JSpinnerComponentBinder(boolean useFormatter) {
      this.useFormatter = useFormatter;
   }

   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JSpinnerBoundField(binder, (JSpinner) component,
         fieldMetadata, useFormatter);
   }

   public class JSpinnerBoundField extends AbstractBoundMember
         implements BoundField {
      private final JSpinner component;
      private final FieldMetadata fieldMetadata;
      private final boolean useFormatter;
      private final ChangeListener listener;

      public JSpinnerBoundField(SwingBinder binder, JSpinner component,
         FieldMetadata fieldMetadata, boolean useFormatter) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.useFormatter = useFormatter;
         component.addChangeListener(listener = createChangeListener());
      }

      protected JSpinner getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      public boolean isUseFormatter() {
         return useFormatter;
      }

      protected ChangeListener getListener() {
         return listener;
      }

      protected ChangeListener createChangeListener() {
         return new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
               getBinder().populateForm(getFieldMetadata(), getValue());
            }
         };
      }

      public Object getValue() {
         return component.getValue();
      }

      public void setValue(Object value) throws Exception {
         deactivateListeners();

         try {
            component.setValue(useFormatter ? getBinder().getFormatter(
                  fieldMetadata).format(value) : value);
         } finally {
            reactivateListeners();
         }
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeChangeListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addChangeListener(listener);
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeChangeListener(listener);
         }
      }
   }
}
