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

import java.awt.Frame;

import example.databeans.User;

import net.java.dev.genesis.ui.UIUtils;
import net.java.dev.genesis.ui.thinlet.BaseDialogThinlet;

public class InsertUpdateView extends BaseDialogThinlet {
   private final InsertUpdateForm form;
   
   public InsertUpdateView(final Frame frame) throws Exception {
      this(frame, (User) null);
   }

   public InsertUpdateView(final Frame frame, final User user) throws Exception {
      super(frame);
      getDialog().setModal(true);
      if (user == null) {
         getDialog().setTitle("Insert User");
      } else {
         getDialog().setTitle("Update User");
      }
      setAllI18n(true);
      setResourceBundle(UIUtils.getInstance().getBundle());
      add(parse("insert-update.xml"));
      bind(form = new InsertUpdateForm());
   }

   public void chooseRole() throws Exception {
      final RoleListView view = new RoleListView(getFrame());
      view.display();
      form.setRole(view.getRole());
   }
   
   public void cancel(){
      getDialog().dispose();
   }

}
