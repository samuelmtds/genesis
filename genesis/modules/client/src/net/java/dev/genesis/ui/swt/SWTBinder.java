/*
 * The Genesis Project
 * Copyright (C) 2006-2007  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.ui.binding.AbstractBinder;
import net.java.dev.genesis.ui.binding.ExceptionHandler;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.binding.LookupStrategy;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.swt.lookup.BreadthFirstWidgetLookupStrategy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


public class SWTBinder extends AbstractBinder {
   public static final String TABLE_COLUMN_IDENTIFIER = "genesis:tableColumnIdentifier";

   private final WidgetBinderRegistry factory =
      WidgetBinderRegistry.getInstance();

   public SWTBinder(Composite composite, Object form, Object handler) {
      this(composite, form, handler, (LookupStrategy) null);
   }

   public SWTBinder(Composite composite, Object form, Object handler,
         LookupStrategy lookupStrategy) {
      super(composite, form, handler, lookupStrategy);
   }

   protected ExceptionHandler createExceptionHandler() {
      return new SWTExceptionHandler((Composite) getRoot());
   }

   public Composite registerButtonGroup(Composite buttonGroup) {
      if (buttonGroup.getData() == null) {
         throw new IllegalArgumentException("Composite " + buttonGroup + " " +
               "must have a name to be registered as a button group");
      }

      return registerButtonGroup((String) buttonGroup.getData(), buttonGroup);
   }

   public Composite registerButtonGroup(String name, Composite buttonGroup) {
      return (Composite) registerGroup(name, buttonGroup);
   }

   public Composite registerButtonGroup(String name, Composite buttonGroup, GroupBinder groupBinder) {
      return (Composite) registerGroup(name, buttonGroup, groupBinder);
   }

   public boolean isVirtual(Object widget) {
      return isVirtual((Widget) widget);
   }

   public boolean isVirtual(Widget widget) {
      return Boolean.TRUE.equals(widget.getData(VIRTUAL));
   }

   protected LookupStrategy createLookupStrategy() {
      return new BreadthFirstWidgetLookupStrategy();
   }

   protected void markBound() {
      if (getRoot() instanceof Widget) {
         ((Widget) getRoot()).setData(GENESIS_BOUND, Boolean.TRUE);
      }
   }

   protected void markUnbound() {
      if (getRoot() instanceof Widget) {
         ((Widget) getRoot()).setData(GENESIS_BOUND, Boolean.FALSE);
      }
   }

   public Widget register(String name, Widget widget) {
      return (Widget) getLookupStrategy().register(name, widget);
   }

   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return factory.getButtonGroupBinder();
   }

   protected WidgetBinder getDefaultWidgetBinderFor(Object widget) {
      return factory.get(widget.getClass(), true);
   }
}
