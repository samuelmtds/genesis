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
package net.java.dev.genesis.ui.swt.lookup;

import net.java.dev.genesis.GenesisTestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class BreadthFirstWidgetLookupStrategyTest extends GenesisTestCase {
   private BreadthFirstWidgetLookupStrategy strategy;
   private Shell root;
   private Shell rootLevel1Position0;
   private Shell rootLevel1Position1;
   private Shell rootLevel2Position0;
   private Label panelLevel1Position1;
   private Label panelLevel2Position0;

   public BreadthFirstWidgetLookupStrategyTest() {
      super("Breadth First Widget LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new BreadthFirstWidgetLookupStrategy();
      root = new Shell();
      rootLevel1Position0 = new Shell(root);
      rootLevel1Position1 = new Shell(root);
      
      rootLevel2Position0 = new Shell(rootLevel1Position0);

      panelLevel1Position1 = new Label(rootLevel1Position1, SWT.NONE);
      panelLevel2Position0 = new Label(rootLevel2Position0, SWT.NONE);
   }

   public void testSimpleLookup() {
      panelLevel1Position1.setData("someName");

      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));

      panelLevel1Position1.setData("anotherName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "anotherName"));

      panelLevel2Position0.setData("theName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "theName"));
   }

   public void testBreadthFirstLookup() {
      panelLevel1Position1.setData("someName");
      panelLevel2Position0.setData("someName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));

      panelLevel1Position1.setData("changeName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));
   }
}
