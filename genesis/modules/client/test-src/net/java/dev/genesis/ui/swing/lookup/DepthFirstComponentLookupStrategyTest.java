/*
 * The Genesis Project
 * Copyright (C) 2006-2010  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.lookup;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.swing.SwingBinder;

public class DepthFirstComponentLookupStrategyTest extends GenesisTestCase {
   private DepthFirstComponentLookupStrategy strategy;
   private JPanel root;
   private JPanel rootLevel1Position0;
   private JPanel rootLevel1Position1;
   private JPanel rootLevel2Position0;
   private JPanel panelLevel1Position1;
   private JPanel panelLevel2Position0;
   private JMenuBar menuBar;
   private JMenu menu;
   private JMenuItem menuItem;

   public DepthFirstComponentLookupStrategyTest() {
      super("Depth First Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new DepthFirstComponentLookupStrategy();
      panelLevel1Position1 = new JPanel();
      panelLevel2Position0 = new JPanel();
      root = new JPanel();
      rootLevel1Position0 = new JPanel();
      rootLevel1Position1 = new JPanel();
      rootLevel2Position0 = new JPanel();

      root.add(rootLevel1Position0);
      root.add(rootLevel1Position1);
      rootLevel1Position0.add(rootLevel2Position0);

      rootLevel1Position1.add(panelLevel1Position1);
      rootLevel2Position0.add(panelLevel2Position0);

      menuBar = new JMenuBar();

      menu = new JMenu();
      menu.setName("aMenu");
      menuBar.add(menu);

      menuItem = new JMenuItem();
      menuItem.setName("aMenuItem");
      menu.add(menuItem);
   }

   public void testSimpleLookup() {
      panelLevel1Position1.setName("someName");

      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));

      panelLevel1Position1.setName("anotherName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "anotherName"));

      panelLevel2Position0.setName("theName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "theName"));
   }
   
   public void testDepthFirstLookup() {
      panelLevel1Position1.setName("someName");
      panelLevel2Position0.setName("someName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));

      panelLevel2Position0.setName("changeName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
   }

   public void testLookupForMenus() {
      assertSame(menu, strategy.lookup(menuBar, menu.getName()));
      assertSame(menuItem, strategy.lookup(menuBar, menuItem.getName()));
   }
   
   public void testRegisteredAlias() {
      strategy.register("menuAlias", menu);
      assertSame(menuItem, strategy.lookup(menuBar, menuItem.getName()));
   }

   public void testSkipLookupInBoundComponent() {
      panelLevel1Position1.setName("someName");
      panelLevel2Position0.setName("someName");
      panelLevel2Position0.putClientProperty(SwingBinder.GENESIS_BOUND, Boolean.
            TRUE);
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));
      
      rootLevel2Position0.putClientProperty(SwingBinder.GENESIS_BOUND, Boolean.
            TRUE);
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
      
      strategy.setSkipLookupInBoundComponent(false);
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));
      
      strategy.setSkipLookupInBoundComponent(true);
      panelLevel1Position1.putClientProperty(SwingBinder.GENESIS_BOUND, Boolean.
            TRUE);
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));

      rootLevel1Position1.putClientProperty(SwingBinder.GENESIS_BOUND, Boolean.
            TRUE);
      assertNull(strategy.lookup(root, "someName"));

      rootLevel2Position0.putClientProperty(SwingBinder.GENESIS_BOUND, null);
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));

      rootLevel2Position0.putClientProperty(SwingBinder.GENESIS_BOUND, Boolean.
            FALSE);
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));
   }
}
