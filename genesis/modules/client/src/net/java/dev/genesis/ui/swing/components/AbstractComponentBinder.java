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

import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swing.ComponentBinder;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.thinlet.PropertyMisconfigurationException;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

public abstract class AbstractComponentBinder implements ComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return null;
   }

   public BoundAction bind(SwingBinder binder, Component component,
      ActionMetadata actionMetatada) {
      return null;
   }

   public BoundDataProvider bind(SwingBinder binder, Component component,
      DataProviderMetadata dataProviderMetadata) {
      return null;
   }

   public abstract class AbstractBoundMember implements BoundMember {
      private final JComponent component;
      private final SwingBinder binder;
      private final Set enabledWidgetGroupSet = new HashSet();
      private final Set visibleWidgetGroupSet = new HashSet();

      public AbstractBoundMember(SwingBinder binder, JComponent component) {
         this.component = component;
         this.binder = binder;

         createGroups();

         storeBinder();
      }

      protected void storeBinder() {
         component.putClientProperty(SwingBinder.BINDER_KEY, binder);
      }

      protected SwingBinder getBinder() {
         return binder;
      }

      protected Set getEnabledWidgetGroupSet() {
         return enabledWidgetGroupSet;
      }

      protected Set getVisibleWidgetGroupSet() {
         return visibleWidgetGroupSet;
      }

      protected void createGroups() {
         createWidgetGroup();
         createEnabledGroup();
         createVisibleGroup();
      }

      protected void createWidgetGroup() {
         createGroup(component
               .getClientProperty(SwingBinder.WIDGET_GROUP_PROPERTY), true, true);
      }

      protected void createEnabledGroup() {
         createGroup(component
               .getClientProperty(SwingBinder.ENABLED_GROUP_PROPERTY), true,
               false);
      }

      protected void createVisibleGroup() {
         createGroup(component
               .getClientProperty(SwingBinder.VISIBLE_GROUP_PROPERTY), false,
               true);
      }

      protected void createGroup(Object group, boolean enabled, boolean visible) {
         if (group == null) {
            return;
         }

         boolean instanceofString = group instanceof String;
         if (instanceofString || group instanceof String[]) {
        	
            String[] componentNames = instanceofString ?
            		((String) group).split("\\s*,\\s*") : (String[]) group;

            for (int i = 0; i < componentNames.length; i++) {
               Component c =
                  binder.getLookupStrategy()
                           .lookup(binder.getRoot(), componentNames[i]);

               if (c != null) {
                  if (enabled) {
                     enabledWidgetGroupSet.add(c);
                  }

                  if (visible) {
                     visibleWidgetGroupSet.add(c);
                  }
               }
            }
         } else if (group instanceof Collection) {
            Collection groupCollection = (Collection) group;

            if (enabled) {
               enabledWidgetGroupSet.addAll(groupCollection);
            }

            if (visible) {
               visibleWidgetGroupSet.addAll(groupCollection);
            }
         } else if (group instanceof Object[]) {
            Object[] groupArray = (Object[]) group;

            if (enabled) {
               enabledWidgetGroupSet.addAll(Arrays.asList(groupArray));
            }

            if (visible) {
               visibleWidgetGroupSet.addAll(Arrays.asList(groupArray));
            }
         } else if (group instanceof JComponent) {
            JComponent jComponent = (JComponent) group;

            if (enabled) {
               enabledWidgetGroupSet.add(jComponent);
            }

            if (visible) {
               visibleWidgetGroupSet.add(jComponent);
            }
         } else {
            throw new IllegalArgumentException("Group property must be a comma-separated string, array of strings, a collection of components, an array of components or a JComponent");
         }
      }

      protected boolean isBlank(JComponent component) {
         return isBoolean(component, SwingBinder.BLANK_PROPERTY);
      }

      protected boolean isBoolean(JComponent component, String propertyName) {
         final Object booleanObject = component.getClientProperty(propertyName);

         boolean ret;

         if (booleanObject == null) {
            ret = false;
         } else if (booleanObject instanceof String) {
            ret = Boolean.valueOf(booleanObject.toString()).booleanValue();
         } else if (booleanObject instanceof Boolean) {
            ret = ((Boolean) booleanObject).booleanValue();
         } else {
            throw new PropertyMisconfigurationException("Property '" +
               propertyName + "' " + "for the component named " +
               getName() + " must " +
               "either be left empty or contain a boolean value");
         }

         return ret;
      }

      public void setEnabled(boolean enabled) {
         component.setEnabled(enabled);

         for (Iterator iter = enabledWidgetGroupSet.iterator(); iter.hasNext();) {
            ((JComponent) iter.next()).setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         component.setVisible(visible);

         for (Iterator iter = visibleWidgetGroupSet.iterator(); iter.hasNext();) {
            ((JComponent) iter.next()).setVisible(visible);
         }
      }

      public String getName() {
         return binder.getName(component);
      }

      public void unbind() {
      }
   }
}
