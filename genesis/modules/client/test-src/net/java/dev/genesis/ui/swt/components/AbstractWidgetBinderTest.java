/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swt.components;

import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.mockobjects.MockForm;
import net.java.dev.genesis.ui.swt.MockSWTBinder;
import net.java.dev.genesis.ui.swt.SWTBinder;
import net.java.dev.genesis.ui.swt.widgets.AbstractWidgetBinder;
import net.java.dev.genesis.ui.swt.widgets.AbstractWidgetBinder.AbstractBoundMember;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AbstractWidgetBinderTest extends GenesisTestCase {
   private Shell root;
   private Text comp1;
   private Text comp2;
   private MockSWTBinder binder;
   private AbstractBoundMember boundMember;

   public AbstractWidgetBinderTest() {
      super("Abstract Widget Binder Unit Test");
   }

   protected void setUp() throws Exception {
      comp1 = new Text(root = new Shell(), SWT.NONE) {
         private boolean visible = true;

         public boolean isVisible() {
            return visible;
         }

         public void setVisible(boolean visible) {
            this.visible = visible;
         }

         protected void checkSubclass() {
         }
      };
      comp2 = new Text(root, SWT.NONE) {
         private boolean visible = true;

         public boolean isVisible() {
            return visible;
         }

         public void setVisible(boolean visible) {
            this.visible = visible;
         }

         protected void checkSubclass() {
         }
      };
      binder = new MockSWTBinder(root, new MockForm(), null);
      comp1.setData(SWTBinder.ENABLED_GROUP_PROPERTY, comp2);
      comp1.setData(SWTBinder.VISIBLE_GROUP_PROPERTY, comp2);
      boundMember = new AbstractWidgetBinder() {
      }.new AbstractBoundMember(binder, comp1) {
      };
   }

   public void testEnabled() {
      assertTrue(comp1.isEnabled());
      assertTrue(comp2.isEnabled());

      boundMember.setEnabled(false);

      assertFalse(comp1.isEnabled());
      assertFalse(comp2.isEnabled());
   }

   public void testVisible() {
      assertTrue(comp1.isVisible());
      assertTrue(comp2.isVisible());

      boundMember.setVisible(false);

      assertFalse(comp1.isVisible());
      assertFalse(comp2.isVisible());
   }
}
