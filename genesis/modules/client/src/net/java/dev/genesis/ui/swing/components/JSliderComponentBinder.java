/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.beanutils.BeanUtilsBean;

import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class JSliderComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JSliderBoundField(binder, (JSlider) component, fieldMetadata);
   }

   public class JSliderBoundField extends AbstractBoundMember
         implements BoundField {
      private final JSlider component;
      private final FieldMetadata fieldMetadata;
      private final ChangeListener listener;

      public JSliderBoundField(SwingBinder binder, JSlider component,
         FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;

         component.addChangeListener(listener = createChangeListener());
      }

      protected JSlider getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
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

      protected Object getValue() {
         return new Integer(component.getValue());
      }

      protected int toInt(Object value) throws Exception {
         Integer integer = (Integer) BeanUtilsBean.getInstance()
               .getConvertUtils().lookup(Integer.TYPE).convert(Integer.TYPE,
                     value);

         return integer.intValue();
      }

      public void setValue(Object value) throws Exception {
         deactivateListeners();
         try {
            component.setValue(toInt(value));
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
