/*
 * The Genesis Project
 * Copyright (C) 2006-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt.widgets;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.binding.PropertyMisconfigurationException;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.swt.SWTBinder;

import net.java.dev.genesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class AbstractWidgetBinder implements WidgetBinder {
   public BoundField bind(AbstractBinder binder, Object widget,
         FieldMetadata fieldMetadata) {
      return bind((SWTBinder) binder, (Widget) widget, fieldMetadata);
   }

   public BoundAction bind(AbstractBinder binder, Object widget,
         ActionMetadata actionMetatada) {
      return bind((SWTBinder) binder, (Widget) widget, actionMetatada);
   }

   public BoundDataProvider bind(AbstractBinder binder, Object widget,
         DataProviderMetadata dataProviderMetadata) {
      return bind((SWTBinder) binder, (Widget) widget, dataProviderMetadata);
   }

   public BoundField bind(SWTBinder binder, Widget widget,
      FieldMetadata fieldMetadata) {
      return null;
   }

   public BoundAction bind(SWTBinder binder, Widget widget,
      ActionMetadata actionMetatada) {
      return null;
   }

   public BoundDataProvider bind(SWTBinder binder, Widget widget,
      DataProviderMetadata dataProviderMetadata) {
      return null;
   }

   public abstract class AbstractBoundMember implements BoundMember {
      private final Widget widget;
      private final SWTBinder binder;
      private final Set enabledWidgetGroupSet = new HashSet();
      private final Set visibleWidgetGroupSet = new HashSet();

      public AbstractBoundMember(SWTBinder binder, Widget widget) {
         this.widget = widget;
         this.binder = binder;

         createGroups();
      }

      protected SWTBinder getBinder() {
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
         createGroup(widget
               .getData(SWTBinder.WIDGET_GROUP_PROPERTY), true, true);
      }

      protected void createEnabledGroup() {
         createGroup(widget
               .getData(SWTBinder.ENABLED_GROUP_PROPERTY), true,
               false);
      }

      protected void createVisibleGroup() {
         createGroup(widget
               .getData(SWTBinder.VISIBLE_GROUP_PROPERTY), false,
               true);
      }

      protected void createGroup(Object group, boolean enabled, boolean visible) {
         if (group == null) {
            return;
         }

         boolean instanceofString = group instanceof String;
         if (instanceofString || group instanceof String[]) {
        	
            String[] widgetNames = instanceofString ?
            		((String) group).split("\\s*,\\s*") : (String[]) group; // NOI18N

            for (int i = 0; i < widgetNames.length; i++) {
               Widget w = (Widget) binder.lookup(widgetNames[i]);

               if (w != null) {
                  if (enabled) {
                     enabledWidgetGroupSet.add(w);
                  }

                  if (visible) {
                     visibleWidgetGroupSet.add(w);
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
         } else if (group instanceof Widget) {
            Widget widget = (Widget) group;

            if (enabled) {
               enabledWidgetGroupSet.add(widget);
            }

            if (visible) {
               visibleWidgetGroupSet.add(widget);
            }
         } else {
            throw new IllegalArgumentException(
                  Bundle.getMessage(getClass(),
                  "GROUP_PROPERTY_MUST_BE_A_COMMA_SEPARATED_STRING_ARRAY_OF_STRINGS_A_COLLECTION_OF_WIDGETS_AN_ARRAY_OF_WIDGETS_OR_A_WIDGET")); // NOI18N
         }
      }

      protected boolean isBlank(Widget widget) {
         return isBoolean(widget, SWTBinder.BLANK_PROPERTY);
      }

      protected boolean isBoolean(Widget widget, String propertyName) {
         final Object booleanObject = widget.getData(propertyName);

         boolean ret;

         if (booleanObject == null) {
            ret = false;
         } else if (booleanObject instanceof String) {
            ret = Boolean.valueOf(booleanObject.toString()).booleanValue();
         } else if (booleanObject instanceof Boolean) {
            ret = ((Boolean) booleanObject).booleanValue();
         } else {
            throw new PropertyMisconfigurationException(
                  Bundle.getMessage(getClass(),
                  "PROPERTY_X_FOR_THE_WIDGET_NAMED_Y_MUST_EITHER_BE_LEFT_EMPTY_OR_CONTAIN_A_BOOLEAN_VALUE", // NOI18N
                  propertyName, getName()));
         }

         return ret;
      }

      public void setEnabled(boolean enabled) {
         ((Control)widget).setEnabled(enabled);

         for (Iterator iter = enabledWidgetGroupSet.iterator(); iter.hasNext();) {
            ((Control) iter.next()).setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         ((Control)widget).setVisible(visible);

         for (Iterator iter = visibleWidgetGroupSet.iterator(); iter.hasNext();) {
            ((Control) iter.next()).setVisible(visible);
         }
      }

      protected Object getProperty(Object bean, String propertyName)
            throws IllegalAccessException, InvocationTargetException {
         try {
            return PropertyUtils.getProperty(bean, propertyName);
         } catch (NoSuchMethodException e) {
            IllegalArgumentException iae = new IllegalArgumentException(
                  Bundle.getMessage(getClass(),
                  "THE_WIDGET_NAMED_X_EXPECTED_Y_TO_HAVE_A_PROPERTY_NAMED_Z_AT_BEAN_W", // NOI18N
                  new Object[] {getName(), bean.getClass().getName(),
                     propertyName, bean}));
            iae.initCause(e);
            throw iae;
         }
      }

      protected String format(FieldMetadata fieldMetadata, Object value) {
         return binder.format(getName(), fieldMetadata.getFieldName(), value,
               getBinder().isVirtual(widget, fieldMetadata.getFieldName()));
      }

      public String getName() {
         return binder.getLookupStrategy().getName(widget);
      }

      public void unbind() {
      }
   }
}
