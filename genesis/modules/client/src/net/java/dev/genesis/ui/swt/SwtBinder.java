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
package net.java.dev.genesis.ui.swt;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.BoundAction;
import net.java.dev.genesis.ui.binding.BoundDataProvider;
import net.java.dev.genesis.ui.binding.BoundField;
import net.java.dev.genesis.ui.binding.BoundMember;
import net.java.dev.genesis.ui.binding.ExceptionHandler;
import net.java.dev.genesis.ui.controller.FormControllerListener;
import net.java.dev.genesis.ui.metadata.ActionMetadata;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.swing.SwingBinder;
import net.java.dev.genesis.ui.swt.lookup.BreadthFirstWidgetLookupStrategy;
import net.java.dev.genesis.ui.swt.lookup.WidgetLookupStrategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


public class SwtBinder extends AbstractBinder {
   private static final Log log = LogFactory.getLog(SwingBinder.class);
   
   public static final String GENESIS_BOUND = "genesis:boundField";
   public static final String NAME_PROPERTY = "genesis:widgetName";
   public static final String WIDGET_GROUP_PROPERTY = "genesis:widgetGroup";
   public static final String ENABLED_GROUP_PROPERTY = "genesis:enabledGroup";
   public static final String VISIBLE_GROUP_PROPERTY = "genesis:visibleGroup";
   public static final String BLANK_PROPERTY = "genesis:blank";
   public static final String BLANK_LABEL_PROPERTY = "genesis:blankLabel";
   public static final String KEY_PROPERTY = "genesis:key";
   public static final String VALUE_PROPERTY = "genesis:value";
   public static final String VIRTUAL = "genesis:virtual";
   public static final String COLUMN_NAMES = "genesis:columnNames";
   public static final String TABLE_COLUMN_IDENTIFIER = "genesis:tableColumnIdentifier";
   public static final String BUTTON_GROUP_SELECTION_VALUE = "genesis:buttonGroupSelectionValue";

   private final WidgetBinderRegistryFactory factory =
      WidgetBinderRegistryFactory.getInstance();
   private final Composite root;
   private final WidgetLookupStrategy lookupStrategy;
   private final Map buttonGroupsMap = new HashMap();
   private final Map widgetBinders = new HashMap();
   private final Map groupBinders = new IdentityHashMap();

   public SwtBinder(Composite composite, Object form, Object handler) {
      this(composite, (WidgetLookupStrategy) null, form, handler);
   }

   public SwtBinder(Composite composite, 
         WidgetLookupStrategy lookupStrategy, Object form, Object handler) {
      this(composite, lookupStrategy, form, handler, true);
   }

   public SwtBinder(Composite composite, 
         WidgetLookupStrategy lookupStrategy, Object form, Object handler, boolean bindDefaultButton) {
      super(form, handler);
      this.root = composite;
      this.lookupStrategy = (lookupStrategy == null)
         ? createWidgetLookupStrategy() : lookupStrategy;
   }

   public WidgetLookupStrategy getLookupStrategy() {
      return lookupStrategy;
   }

   public Composite getRoot() {
      return root;
   }

   protected ExceptionHandler createExceptionHandler() {
      return new SwtExceptionHandler(getRoot());
   }

   public WidgetBinder registerWidgetBinder(String widgetName,
      WidgetBinder binder) {
      return (WidgetBinder) widgetBinders.put(widgetName, binder);
   }

   public Composite registerButtonGroup(String name, Composite buttonGroup) {
      buttonGroupsMap.put(name, buttonGroup);

      return buttonGroup;
   }

   public boolean isVirtual(Object widget) {
      return isVirtual((Widget) widget);
   }

   public boolean isVirtual(Widget widget) {
      return Boolean.TRUE.equals(widget.getData(VIRTUAL));
   }

   public Composite registerButtonGroup(String name, Composite buttonGroup, GroupBinder groupBinder) {
      registerButtonGroup(name, buttonGroup);
      groupBinders.put(buttonGroup, groupBinder);

      return buttonGroup;
   }

   public String getName(Object component) {
      return getLookupStrategy().getName((Widget) component);
   }

   protected WidgetLookupStrategy createWidgetLookupStrategy() {
      return new BreadthFirstWidgetLookupStrategy();
   }

   protected WidgetBinder getWidgetBinder(Widget widget) {
      WidgetBinder binder =
         (WidgetBinder) widgetBinders.get(getLookupStrategy().getName(widget));

      if (binder != null) {
         return binder;
      }

      return factory.get(widget.getClass(), true);
   }

   protected GroupBinder getGroupBinder(Composite group) {
      GroupBinder binder = (GroupBinder) groupBinders.get(group);

      if (binder != null) {
         return binder;
      }

      return factory.getButtonGroupBinder();
   }

   public void bind() {
      super.bind();

      markBound();
   }

   protected void markBound() {
      root.setData(GENESIS_BOUND, Boolean.TRUE);
   }

   protected BoundField bindFieldMetadata(String name,
      FormMetadata formMetadata, FieldMetadata fieldMetadata) {
      final Widget widget = lookupStrategy.lookup(root, name);

      if (widget == null) {
         final Composite group = (Composite) buttonGroupsMap.get(name);

         if (group == null) {
            log.warn(name + " could not be found while binding " +
               getForm().getClass());

            return null;
         }

         GroupBinder binder = getGroupBinder(group);

         if (binder == null) {
            log.warn("No GroupBinder registered for " + group.getClass());

            return null;
         }

         return binder.bind(this, group, fieldMetadata);
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No WidgetBinder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, fieldMetadata);
   }

   protected BoundAction bindActionMetadata(String name,
      FormMetadata formMetadata, ActionMetadata actionMetadata) {
      final Widget widget = lookupStrategy.lookup(root, name);

      if (widget == null) {
         log.warn(name + " could not be found while binding " +
            getForm().getClass());

         return null;
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No WidgetBinder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, actionMetadata);
   }

   protected BoundDataProvider bindDataProvider(String name,
      FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata) {
      final Widget widget = lookupStrategy.lookup(root, name);

      if (widget == null) {
         log.warn(name + " could not be found while binding " +
            getForm().getClass());

         return null;
      }

      WidgetBinder binder = getWidgetBinder(widget);

      if (binder == null) {
         log.warn("No WidgetBinder registered for " + widget.getClass());

         return null;
      }

      return binder.bind(this, widget, dataProviderMetadata);
   }

   public BoundMember getBoundMember(String name) {
      BoundMember member = super.getBoundMember(name);

      if (member == null) {
         member = (BoundMember) buttonGroupsMap.get(name);
      }

      return member;
   }

   protected FormControllerListener getFormControllerListener() {
      return (FormControllerListener) this;
   }

   public Widget register(String name, Widget widget) {
      return getLookupStrategy().register(name, widget);
   }
}
