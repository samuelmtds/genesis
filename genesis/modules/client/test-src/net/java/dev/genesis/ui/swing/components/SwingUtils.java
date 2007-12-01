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
package net.java.dev.genesis.ui.swing.components;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;

import net.java.dev.genesis.mockobjects.MockBean;
import net.java.dev.genesis.ui.swing.factory.SwingFactory;

public class SwingUtils {

   public static AbstractButton newButton() {
      AbstractButton component = new AbstractButton() {
      };

      component.setModel(new DefaultButtonModel());

      return component;
   }

   public static JComboBox newStringCombo() {
      JComboBox combo = new JComboBox();
      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      model.addElement("one");
      model.addElement("two");
      model.addElement("three");
      model.addElement("four");
      model.addElement("five");

      return combo;
   }
   
   public static JComboBox newCombo() {
      JComboBox combo = new JComboBox();
      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      model.addElement(new MockBean("one", "One"));
      model.addElement(new MockBean("two", "Two"));
      model.addElement(new MockBean("three", "Three"));
      model.addElement(new MockBean("four", "Four"));
      model.addElement(new MockBean("five", "Five"));

      return combo;
   }

   public static JList newList() {
      JList list = new JList(new DefaultListModel());
      DefaultListModel model = (DefaultListModel) list.getModel();

      model.addElement(new MockBean("one", "One"));
      model.addElement(new MockBean("two", "Two"));
      model.addElement(new MockBean("three", "Three"));
      model.addElement(new MockBean("four", "Four"));
      model.addElement(new MockBean("five", "Five"));

      return list;
   }

   public static JTable newTable() {
      return SwingFactory.createTable(new String[] { "key", "value" },
            new String[] { "key", "value" });
   }
}
