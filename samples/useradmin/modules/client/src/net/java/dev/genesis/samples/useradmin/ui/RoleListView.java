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

import java.awt.Dimension;
import java.awt.Frame;

import net.java.dev.genesis.samples.useradmin.databeans.Role;

public class RoleListView extends BaseDialogView {
   private final RoleListForm form;
   private boolean hasChanged;

   public RoleListView(final Frame frame) throws Exception {
      super(frame,"RoleListView.title","role-list.xml", false, true);
      bind(form = new RoleListForm());
   }

   public boolean showView() throws Exception {
      display();
      return hasChanged;
   }

   public Dimension getPreferredSize() {
      return new Dimension(400, 300);
   }

   public Role getRole() {
      return form.getRole();
   }

   public void cancel() throws Exception {
      getDialog().dispose();
   }

   /**
    * @PosAction
    */
   public void select() throws Exception {
      getDialog().dispose();
      hasChanged = true;
   }

   public void create() throws Exception {
      final InsertRoleView thinlet = new InsertRoleView(getFrame());
      if (thinlet.showView()) {
         invokeFormAction("provideRoles");
      }
   }
   
   /**
    * @PosAction
    */
   public void remove() throws Exception {
      invokeFormAction("provideRoles");
   }

}