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
package net.java.dev.genesis.samples.useradmin.ui;

import java.awt.Frame;

import net.java.dev.genesis.samples.useradmin.databeans.User;

import org.apache.commons.beanutils.PropertyUtils;

public class InsertUpdateView extends BaseDialogView {
   private final InsertUpdateForm form;
   private boolean hasChanged;

   public InsertUpdateView(final Frame frame) throws Exception {
      this(frame, (User)null);
   }

   public InsertUpdateView(final Frame frame, final User user) throws Exception {
      super(frame, user == null ? "InsertView.title" : "UpdateView.title",
            "insert-update.xml", false, true);
      form = new InsertUpdateForm();
      if (user != null) {
         PropertyUtils.copyProperties(form, user);
      }
      bind(form);
   }

   public boolean showView() throws Exception {
      display();
      return hasChanged;
   }

   /**
    * @PreAction
    */
   public void chooseRole() throws Exception {
      final RoleListView view = new RoleListView(getFrame());
      if(view.showView()) {
         form.setRole(view.getRole());
      }
   }

   /**
    * @PosAction
    */
   public void save() {
      getDialog().dispose();
      this.hasChanged = true;
   }

   public void cancel() {
      getDialog().dispose();
   }

}

