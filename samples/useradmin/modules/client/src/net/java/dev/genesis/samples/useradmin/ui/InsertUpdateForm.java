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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.java.dev.genesis.samples.useradmin.business.RoleSearchCommand;
import net.java.dev.genesis.samples.useradmin.business.UserCreateCommand;
import net.java.dev.genesis.samples.useradmin.business.UserUpdateCommand;
import net.java.dev.genesis.samples.useradmin.databeans.Country;
import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.samples.useradmin.databeans.State;
import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.genesis.ui.UIException;
import net.java.dev.reusablecomponents.lang.Enum;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @Form
 * @Condition findRoleCondition=genesis.isNotEmpty('form:roleCode') &&
 *       genesis.hasChanged('form:roleCode')
 * 
 * @genesis.form
 */
public class InsertUpdateForm {
   private Long id;
   private String name;
   private String login;
   private String password;
   private String email;
   private Date birthday;
   private String address;
   private String roleCode;
   
   private Role role;
   private Country country;
   private State state;

   public Date getBirthday() {
      return birthday;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.birthday"
    * 
    * @genesis.validator type="date"
    * @genesis.validator-var name="pattern" value="MM/dd/yyyy"
    */
   public void setBirthday(Date birthday) {
      this.birthday = birthday;
   }

   public Country getCountry() {
      return country;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.country"
    */
   public void setCountry(Country country) {
      this.country = country;
   }

   public String getEmail() {
      return email;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.email"
    * 
    * @genesis.validator type="email"
    * @genesis.validator-args arg0resource="User.email"
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @ClearOn genesis.isEmpty('form:roleCode')
    */
   public Role getRole() {
      return role;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.role"
    */
   public void setRole(Role role) {
      this.role = role;
      setRoleCode(role == null ? null : role.getCode());
   }

   public String getRoleCode(){
      return this.roleCode;
   }

   public void setRoleCode(String roleCode) {
      this.roleCode = roleCode;
   }

   /**
    * @ClearOn genesis.isEmpty('form:roleCode')
    */
   public String getRoleLabel(){
      return role == null ? null : role.getLabel();
   }

   /**
    * @NotBound
    */
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getLogin() {
      return login;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.login"
    * 
    * @genesis.validator type="minlength" arg1value="${var:minlength}"
    * @genesis.validator-var name="minlength" value="4"
    */
   public void setLogin(String login) {
      this.login = login;
   }

   public String getName() {
      return name;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.name"
    */
   public void setName(String name) {
      this.name = name;
   }

   public String getPassword() {
      return password;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.password"
    * 
    * @genesis.validator type="minlength" arg1value="${var:minlength}"
    * @genesis.validator-var name="minlength" value="6"
    * 
    * @genesis.validator type="maxlength" arg1value="${var:maxlength}"
    * @genesis.validator-var name="maxlength" value="8"
    */
   public void setPassword(String password) {
      this.password = password;
   }

   public String getAddress() {
      return address;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.address"
    */
   public void setAddress(String address) {
      this.address = address;
   }

   public State getState() {
      return state;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="User.state"
    */
   public void setState(State state) {
      this.state = state;
   }

   /**
    * @DataProvider objectField=country
    */
   public List provideCountries() throws Exception {
      return new ArrayList(Enum.getInstances(Country.class));
   }

   /**
    * @DataProvider objectField=state
    * @CallWhen genesis.hasChanged('form:country')
    */
   public List provideState() throws Exception {
      return new ArrayList(State.getStates(getCountry()));
   }

   /**
    * @Action
    * @ValidateBefore
    */
   public void save() throws Exception {
      final User user = new User();
      PropertyUtils.copyProperties(user, this);

      if (getId() == null) {
         new UserCreateCommand().createUser(user);
      } else {
         new UserUpdateCommand().updateUser(user);
      }
   }

   /**
    * @Action
    * @CallWhen findRoleCondition
    */
   public void findRole() throws Exception {
      final Role role = new RoleSearchCommand().getRole(roleCode);

      if (role == null) {
         throw new UIException("Role not found", "No role with code " + 
               roleCode + " could be found.");
      }

      setRole(role);
   }
}
