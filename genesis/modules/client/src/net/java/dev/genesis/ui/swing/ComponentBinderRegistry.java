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
package net.java.dev.genesis.ui.swing;

import net.java.dev.genesis.registry.Registry;
import net.java.dev.genesis.ui.binding.GroupBinder;
import net.java.dev.genesis.ui.binding.WidgetBinder;
import net.java.dev.genesis.ui.swing.components.AbstractButtonComponentBinder;
import net.java.dev.genesis.ui.swing.components.ButtonGroupBinder;
import net.java.dev.genesis.ui.swing.components.JComboBoxComponentBinder;
import net.java.dev.genesis.ui.swing.components.JLabelComponentBinder;
import net.java.dev.genesis.ui.swing.components.JListComponentBinder;
import net.java.dev.genesis.ui.swing.components.JProgressBarComponentBinder;
import net.java.dev.genesis.ui.swing.components.JSliderComponentBinder;
import net.java.dev.genesis.ui.swing.components.JSpinnerComponentBinder;
import net.java.dev.genesis.ui.swing.components.JTableComponentBinder;
import net.java.dev.genesis.ui.swing.components.JTextComponentBinder;
import net.java.dev.genesis.ui.swing.components.JToggleButtonComponentBinder;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

public class ComponentBinderRegistry {
   private static final ComponentBinderRegistry instance =
      new ComponentBinderRegistry();
   private final Registry registry = new Registry();
   private GroupBinder groupBinder = new ButtonGroupBinder();

   private ComponentBinderRegistry() {
      register(JTextComponent.class, new JTextComponentBinder());
      register(JLabel.class, new JLabelComponentBinder());
      register(AbstractButton.class, new AbstractButtonComponentBinder());
      register(JSlider.class, new JSliderComponentBinder());
      register(JProgressBar.class, new JProgressBarComponentBinder());
      register(JSpinner.class, new JSpinnerComponentBinder());
      register(JToggleButton.class, new JToggleButtonComponentBinder());
      register(JList.class, new JListComponentBinder());
      register(JComboBox.class, new JComboBoxComponentBinder());
      register(JTable.class, new JTableComponentBinder());
   }

   public static ComponentBinderRegistry getInstance() {
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

   public GroupBinder registerButtonGroupBinder(GroupBinder groupBinder) {
      return this.groupBinder = groupBinder;
   }

   public GroupBinder getButtonGroupBinder() {
      return this.groupBinder;
   }

   public void deregisterButtonGroupBinder() {
      this.groupBinder = null;
   }
}
