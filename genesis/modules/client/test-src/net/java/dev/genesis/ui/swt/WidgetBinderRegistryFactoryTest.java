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

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.binding.WidgetBinder;
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

public class WidgetBinderRegistryFactoryTest extends GenesisTestCase {
   public WidgetBinderRegistryFactoryTest() {
      super("WidgetBinder Registry Factory Unit Test");
   }

   private WidgetBinderRegistryFactory registry;

   protected void setUp() {
      registry = WidgetBinderRegistryFactory.getInstance();
   }

   protected void tearDown() throws Exception {
      registry.deregister();
      registry.register(Text.class, new TextWidgetBinder());
      registry.register(Label.class, new LabelWidgetBinder());
      registry.register(Button.class, new ButtonWidgetBinder());
      registry.register(Slider.class, new SliderWidgetBinder());
      registry.register(ProgressBar.class, new ProgressBarWidgetBinder());
      registry.register(Spinner.class, new SpinnerWidgetBinder());
      registry.register(List.class, new ListWidgetBinder());
      registry.register(Combo.class, new ComboWidgetBinder());
      registry.register(Table.class, new TableWidgetBinder());
      registry.registerButtonGroupBinder(new ButtonGroupBinder());
   }

   public void testLookup() {
      assertComponentBinder(Text.class, TextWidgetBinder.class);
      assertComponentBinder(Label.class, LabelWidgetBinder.class);
      assertComponentBinder(Button.class, ButtonWidgetBinder.class);
      assertComponentBinder(Slider.class, SliderWidgetBinder.class);
      assertComponentBinder(ProgressBar.class, ProgressBarWidgetBinder.class);
      assertComponentBinder(Spinner.class, SpinnerWidgetBinder.class);
      assertComponentBinder(List.class, ListWidgetBinder.class);
      assertComponentBinder(Combo.class, ComboWidgetBinder.class);
      assertComponentBinder(Table.class, TableWidgetBinder.class);

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
      final WidgetBinder binderInstance = new ButtonWidgetBinder();

      // Test for non-registered type
      registry.register(Comparable.class, binderInstance);
      assertSame(binderInstance, registry.get(Comparable.class, true));

      // Test for already registered type
      registry.register(Button.class, binderInstance);
      assertSame(binderInstance, registry.get(Button.class, true));

      final ButtonGroupBinder groupBinder = new ButtonGroupBinder();
      Object binder = registry.registerButtonGroupBinder(groupBinder);
      assertSame(groupBinder, binder);
   }

   public void testDeregister() {
      final WidgetBinder binderInstance = new ButtonWidgetBinder();

      assertNotNull(registry.get(Button.class, true));
      registry.deregister(Button.class);
      assertNull(registry.get(Button.class, true));

      // Test for already registered type
      registry.register(Button.class, binderInstance);
      assertSame(binderInstance, registry.get(Button.class, true));

      registry.deregister(Button.class);
      assertNull(registry.get(Button.class, true));

      registry.deregisterButtonGroupBinder();
      assertNull(registry.getButtonGroupBinder());
   }

   public void testDeregisterAll() {
      registry.deregister();

      assertNullComponentBinder(Text.class);
      assertNullComponentBinder(Label.class);
      assertNullComponentBinder(Button.class);
      assertNullComponentBinder(Slider.class);
      assertNullComponentBinder(ProgressBar.class);
      assertNullComponentBinder(Spinner.class);
      assertNullComponentBinder(List.class);
      assertNullComponentBinder(Combo.class);
      assertNullComponentBinder(Table.class);

      assertNull(registry.getButtonGroupBinder());
   }
}