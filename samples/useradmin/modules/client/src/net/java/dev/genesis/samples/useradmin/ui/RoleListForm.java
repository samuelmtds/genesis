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

import java.util.List;

import net.java.dev.genesis.samples.useradmin.business.RoleRemoveCommand;
import net.java.dev.genesis.samples.useradmin.business.RoleSearchCommand;
import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.ui.ActionInvoker;

/**
 * @Form
 */
public class RoleListForm {
   private Role role;

   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   /**
    * @Action
    * @DataProvider objectField=role
    */
   public List provideRoles() throws Exception {
      return new RoleSearchCommand().getRoles();
   }

   /**
    * @Action
    * @EnabledWhen g:isNotEmpty(role)
    */
   public void remove() throws Exception {
      new RoleRemoveCommand().removeRole(role);
      ActionInvoker.invoke(this, "provideRoles");
   }
   
   /**
    * @Action
    * @EnabledWhen g:isNotEmpty(role)
    */
   public void select() throws Exception {
   }
}