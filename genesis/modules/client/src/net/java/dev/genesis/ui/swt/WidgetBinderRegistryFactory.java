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

import net.java.dev.genesis.registry.Registry;
import net.java.dev.genesis.ui.swt.widgets.ButtonGroupBinder;
import net.java.dev.genesis.ui.swt.widgets.ButtonWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.ComboWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.LabelWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.ListWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.ProgressBarWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.SliderWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.SpinnerWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.TableWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.TextWidgetBinder;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class WidgetBinderRegistryFactory {
   private static final WidgetBinderRegistryFactory instance =
      new WidgetBinderRegistryFactory();
   private Registry registry = new Registry();
   private GroupBinder groupBinder = new ButtonGroupBinder();

   private WidgetBinderRegistryFactory() {
      register(Text.class, new TextWidgetBinder());
      register(Label.class, new LabelWidgetBinder());
      register(Button.class, new ButtonWidgetBinder());
      register(Slider.class, new SliderWidgetBinder());
      register(ProgressBar.class, new ProgressBarWidgetBinder());
      register(Spinner.class, new SpinnerWidgetBinder());
      register(List.class, new ListWidgetBinder());
      register(Combo.class, new ComboWidgetBinder());
      register(Table.class, new TableWidgetBinder());
   }

   public static WidgetBinderRegistryFactory getInstance() {
      return instance;
   }

   public void deregister() {
      deregisterButtonGroupBinder();
      registry.deregister();
   }

   public void deregister(Class clazz) {
      registry.deregister(clazz);
   }

   public WidgetBinder register(Class clazz, WidgetBinder binder) {
      return (WidgetBinder) registry.register(clazz, binder);
   }

   public WidgetBinder get(Class clazz) {
      return (WidgetBinder) registry.get(clazz);
   }

   public WidgetBinder get(Class clazz, boolean superClass) {
      return (WidgetBinder) registry.get(clazz, superClass);
   }

   public WidgetBinder get(Object o) {
      return (WidgetBinder) registry.get(o);
   }
   
   public void deregisterButtonGroupBinder() {
      this.groupBinder = null;
   }

   public GroupBinder registerButtonGroupBinder(GroupBinder groupBinder) {
      return this.groupBinder = groupBinder;
   }

   public GroupBinder getButtonGroupBinder() {
      return this.groupBinder;
   }
}
