/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package example.ui;

import java.awt.Dimension;
import java.awt.Frame;

import example.databeans.Role;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.BaseDialogThinlet;

public class RoleListView extends BaseDialogThinlet {
   private final RoleListForm form;

   public RoleListView(final Frame frame) throws Exception {
      super(frame);
      getDialog().setModal(true);
      getDialog().setTitle("Role List");
      setAllI18n(true);
      setResourceBundle(UIUtils.getInstance().getBundle());
      add(parse("role-list.xml"));
      bind(form = new RoleListForm());
   }

   public Dimension getPreferredSize() {
      return new Dimension(400, 300);
   }

   public void close() throws Exception {
      getDialog().dispose();
   }
   
   public void ok() throws Exception {
      getDialog().dispose();
   }
   
   public Role getRole(){
      return form.getRole();
   }

}