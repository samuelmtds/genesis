/*
 * The Genesis Project
 * Copyright (C) 2008 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;

public class MockSortableTable extends JComponent {
   private List model;
   private List view;

   public MockSortableTable() {
      this(Arrays.asList(new Integer[] {
               new Integer(4), new Integer(5), new Integer(2), new Integer(1),
               new Integer(3)
            }));
   }

   public MockSortableTable(List model) {
      this.model = model;
      view = new ArrayList(model);
   }

   public int convertRowIndexToModel(int viewRowIndex) {
      return model.indexOf(view.get(viewRowIndex));
   }

   public int convertRowIndexToView(int modelRowIndex) {
      return view.indexOf(model.get(modelRowIndex));
   }

   public void sort() {
      Collections.sort(view);
   }
}
