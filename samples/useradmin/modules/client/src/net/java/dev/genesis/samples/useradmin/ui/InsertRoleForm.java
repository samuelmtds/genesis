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

import net.java.dev.genesis.samples.useradmin.business.RoleCreateCommand;
import net.java.dev.genesis.samples.useradmin.databeans.Role;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @Form
 * @genesis.form
 */
public class InsertRoleForm {
   private String code;
   private String label;

   public String getCode() {
      return code;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="Role.code"
    */
   public void setCode(String code) {
      this.code = code;
   }

   public String getLabel() {
      return label;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="Role.label"
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * @Action
    * @ValidateBefore
    */
   public void save() throws Exception {
      final Role role = new Role();
      PropertyUtils.copyProperties(role, this);
      new RoleCreateCommand().addRole(role);
   }
}