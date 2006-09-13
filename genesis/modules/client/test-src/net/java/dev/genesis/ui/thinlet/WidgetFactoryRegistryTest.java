/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.thinlet;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.thinlet.BaseThinlet.ItemType;

public class WidgetFactoryRegistryTest extends GenesisTestCase {
   private static final Object WIDGET = new Object();

   private WidgetFactory factory = new WidgetFactory() {
      public Object create(BaseThinlet thinlet, String name, String value, Object bean,
            ItemType type) {
         return WIDGET;
      }
   };

   private WidgetFactoryRegistry registry = WidgetFactoryRegistry.getInstance();

   public WidgetFactoryRegistryTest() {
      super("Widget Factory Registry Unit Test");
   }

   public void testGet() {
      assertEquals(DefaultWidgetFactory.class.getName(), registry.get(
            BaseThinlet.ItemType.CELL).getClass().getName());
      assertEquals(DefaultWidgetFactory.class.getName(), registry.get(
            BaseThinlet.ItemType.CHOICE).getClass().getName());
      assertEquals(DefaultWidgetFactory.class.getName(), registry.get(
            BaseThinlet.ItemType.ITEM).getClass().getName());
   }

   public void testRegister() {
      registry.register(Number.class, factory);

      Integer number = new Integer(0);

      assertSame(factory, registry.get(number));
      assertSame(WIDGET, (registry.get(number)).create(null,
            null, null, null, null));

      registry.deregister(Number.class);
   }

   public void testDeregister() {
      registry.register(Number.class, factory);

      Integer number = new Integer(0);
      assertSame(factory, registry.get(number));

      registry.deregister(Number.class);

      // DefaultWidgetFactory registered for Object.class
      assertTrue(registry.get(number) instanceof DefaultWidgetFactory);
   }
}
