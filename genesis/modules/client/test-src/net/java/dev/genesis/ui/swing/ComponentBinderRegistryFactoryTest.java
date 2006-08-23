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

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import net.java.dev.genesis.GenesisTestCase;
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

public class ComponentBinderRegistryFactoryTest extends GenesisTestCase {
   public ComponentBinderRegistryFactoryTest() {
      super("ComponentBinder Registry Factory Unit Test");
   }

   private ComponentBinderRegistryFactory registry;

   protected void setUp() {
      registry = ComponentBinderRegistryFactory.getInstance();
   }

   protected void tearDown() throws Exception {
      registry.deregister();
      registry.register(JTextComponent.class, new JTextComponentBinder());
      registry.register(JLabel.class, new JLabelComponentBinder());
      registry.register(AbstractButton.class,
            new AbstractButtonComponentBinder());
      registry.register(JSlider.class, new JSliderComponentBinder());
      registry.register(JProgressBar.class, new JProgressBarComponentBinder());
      registry.register(JSpinner.class, new JSpinnerComponentBinder());
      registry
            .register(JToggleButton.class, new JToggleButtonComponentBinder());
      registry.register(JList.class, new JListComponentBinder());
      registry.register(JComboBox.class, new JComboBoxComponentBinder());
      registry.register(JTable.class, new JTableComponentBinder());
      registry.registerButtonGroupBinder(new ButtonGroupBinder());
   }

   public void testLookup() {
      assertComponentBinder(JTextComponent.class, JTextComponentBinder.class);
      assertComponentBinder(JLabel.class, JLabelComponentBinder.class);
      assertComponentBinder(AbstractButton.class,
            AbstractButtonComponentBinder.class);
      assertComponentBinder(JSlider.class, JSliderComponentBinder.class);
      assertComponentBinder(JProgressBar.class,
            JProgressBarComponentBinder.class);
      assertComponentBinder(JSpinner.class, JSpinnerComponentBinder.class);
      assertComponentBinder(JToggleButton.class,
            JToggleButtonComponentBinder.class);
      assertComponentBinder(JList.class, JListComponentBinder.class);
      assertComponentBinder(JComboBox.class, JComboBoxComponentBinder.class);
      assertComponentBinder(JTable.class, JTableComponentBinder.class);

      Object binder = registry.getButtonGroupBinder();
      assertNotNull(binder);
      assertEquals(ButtonGroupBinder.class, binder.getClass());
   }

   private void assertComponentBinder(Class lookupClass, Class binderClass) {
      Object binder = registry.get(lookupClass, true);
      assertNotNull(binder);
      assertEquals(binderClass, binder.getClass());
   }

   private void assertNullComponentBinder(Class lookupClass) {
      assertNull(registry.get(lookupClass, true));
   }

   public void testRegister() {
      final WidgetBinder binderInstance = new AbstractButtonComponentBinder();

      // Test for non-registered type
      registry.register(Comparable.class, binderInstance);
      assertSame(binderInstance, registry.get(Comparable.class, true));

      // Test for already registered type
      registry.register(JButton.class, binderInstance);
      assertSame(binderInstance, registry.get(JButton.class, true));

      final ButtonGroupBinder groupBinder = new ButtonGroupBinder();
      Object binder = registry.registerButtonGroupBinder(groupBinder);
      assertSame(groupBinder, binder);
   }

   public void testDeregister() {
      final WidgetBinder binderInstance = new AbstractButtonComponentBinder();

      assertNotNull(registry.get(JButton.class, true));
      registry.deregister(AbstractButton.class);
      assertNull(registry.get(JButton.class, true));

      // Test for already registered type
      registry.register(AbstractButton.class, binderInstance);
      assertSame(binderInstance, registry.get(JButton.class, true));

      registry.deregister(AbstractButton.class);
      assertNull(registry.get(JButton.class, true));

      registry.deregisterButtonGroupBinder();
      assertNull(registry.getButtonGroupBinder());
   }

   public void testDeregisterAll() {
      registry.deregister();

      assertNullComponentBinder(JTextComponent.class);
      assertNullComponentBinder(JLabel.class);
      assertNullComponentBinder(AbstractButton.class);
      assertNullComponentBinder(JSlider.class);
      assertNullComponentBinder(JProgressBar.class);
      assertNullComponentBinder(JSpinner.class);
      assertNullComponentBinder(JList.class);
      assertNullComponentBinder(JComboBox.class);
      assertNullComponentBinder(JTable.class);
      assertNullComponentBinder(JToggleButton.class);

      assertNull(registry.getButtonGroupBinder());
   }
}