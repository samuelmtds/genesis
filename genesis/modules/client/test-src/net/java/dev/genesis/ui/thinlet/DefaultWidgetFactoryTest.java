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

import thinlet.Thinlet;
import net.java.dev.genesis.GenesisTestCase;

public class DefaultWidgetFactoryTest extends GenesisTestCase {
   public DefaultWidgetFactoryTest() {
      super("Default Widget Factory Unit Test");
   }

   public void testCreate() {
      DefaultWidgetFactory factory = new DefaultWidgetFactory();
      BaseThinlet thinlet = new BaseThinlet() {
      };

      Object cell = factory.create(thinlet, "cellname1", "cellvalue1",
            BaseThinlet.ItemType.CELL);

      assertEquals("cellname1", thinlet.getName(cell));
      assertEquals("cellvalue1", thinlet.getText(cell));
      assertEquals("cellvalue1", thinlet.getTooltip(cell));
      assertEquals(BaseThinlet.ItemType.CELL.getName(), Thinlet.getClass(cell));

      Object choice = factory.create(thinlet, "choicename1", "choicevalue1",
            BaseThinlet.ItemType.CHOICE);

      assertEquals("choicename1", thinlet.getName(choice));
      assertEquals("choicevalue1", thinlet.getText(choice));
      assertEquals("choicevalue1", thinlet.getTooltip(choice));
      assertEquals(BaseThinlet.ItemType.CHOICE.getName(), Thinlet
            .getClass(choice));

      Object item = factory.create(thinlet, "itemname1", "itemvalue1",
            BaseThinlet.ItemType.ITEM);

      assertEquals("itemname1", thinlet.getName(item));
      assertEquals("itemvalue1", thinlet.getText(item));
      assertEquals("itemvalue1", thinlet.getTooltip(item));
      assertEquals(BaseThinlet.ItemType.ITEM.getName(), Thinlet.getClass(item));
   }
}
