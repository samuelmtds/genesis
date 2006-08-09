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
package net.java.dev.genesis.ui.swing.lookup;

import javax.swing.JPanel;

import net.java.dev.genesis.GenesisTestCase;

public class BreadthFirstComponentLookupStrategyTest extends GenesisTestCase {
   private BreadthFirstComponentLookupStrategy strategy;
   private JPanel root;
   private JPanel rootLevel1Position0;
   private JPanel rootLevel1Position1;
   private JPanel rootLevel2Position0;
   private JPanel panelLevel1Position1;
   private JPanel panelLevel2Position0;

   public BreadthFirstComponentLookupStrategyTest() {
      super("Breadth First Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new BreadthFirstComponentLookupStrategy();
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
   }

   public void testSimpleLookup() {
      panelLevel1Position1.setName("someName");

      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
      
      panelLevel1Position1.setName("anotherName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "anotherName"));
      
      panelLevel2Position0.setName("theName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "theName"));
   }
   
   public void testBreadthFirstLookup() {
      panelLevel1Position1.setName("someName");
      panelLevel2Position0.setName("someName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
      
      panelLevel1Position1.setName("changeName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));
   }
}