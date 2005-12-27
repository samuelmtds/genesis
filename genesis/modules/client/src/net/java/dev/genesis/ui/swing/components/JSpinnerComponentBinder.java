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
package net.java.dev.genesis.ui.swing.components;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;

import javax.swing.JSpinner;

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
      private final FocusListener listener;

      public JSpinnerBoundField(SwingBinder binder, JSpinner component,
         FieldMetadata fieldMetadata, boolean useFormatter) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.useFormatter = useFormatter;
         component.addFocusListener(listener = createFocusListener());
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

      protected FocusListener getListener() {
         return listener;
      }

      protected FocusListener createFocusListener() {
         return new FocusAdapter() {
               public void focusLost(FocusEvent event) {
                  try {
                     getBinder().getFormController()
                           .populate(Collections.singletonMap(
                              fieldMetadata.getName(),
                              getBinder().getConverter(fieldMetadata)
                                    .convert(String.class, component.getValue())),
                           getBinder().getConverters());
                  } catch (Exception e) {
                     getBinder().handleException(e);
                  }
               }
            };
      }

      public void setValue(Object value) throws Exception {
         component.setValue(useFormatter
            ? getBinder().getFormatter(fieldMetadata).format(value) : value);
      }

      public void unbind() {
         if (listener != null) {
            component.removeFocusListener(listener);
         }
      }
   }
}
