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
 * @Condition findRoleCondition=g:isNotEmpty(roleCode) and g:hasChanged(roleCode)
 * 
 * @struts.form
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
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.birthday"
    * 
    * @struts.validator type="date"
    * @struts.validator-var name="pattern" value="MM/dd/yyyy"
    */
   public void setBirthday(Date birthday) {
      this.birthday = birthday;
   }

   public Country getCountry() {
      return country;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.country"
    */
   public void setCountry(Country country) {
      this.country = country;
   }

   public String getEmail() {
      return email;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.email"
    * 
    * @struts.validator type="email"
    * @struts.validator-args arg0resource="User.email"
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @ClearOn g:isEmpty(roleCode)
    */
   public Role getRole() {
      return role;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.role"
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
    * @ClearOn g:isEmpty(roleCode)
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
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.login"
    * 
    * @struts.validator type="minlength" arg1value="${var:minlength}"
    * @struts.validator-var name="minlength" value="4"
    */
   public void setLogin(String login) {
      this.login = login;
   }

   public String getName() {
      return name;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.name"
    */
   public void setName(String name) {
      this.name = name;
   }

   public String getPassword() {
      return password;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.password"
    * 
    * @struts.validator type="minlength" arg1value="${var:minlength}"
    * @struts.validator-var name="minlength" value="6"
    * 
    * @struts.validator type="maxlength" arg1value="${var:maxlength}"
    * @struts.validator-var name="maxlength" value="8"
    */
   public void setPassword(String password) {
      this.password = password;
   }

   public String getAddress() {
      return address;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.address"
    */
   public void setAddress(String address) {
      this.address = address;
   }

   public State getState() {
      return state;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="User.state"
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
    * @CallWhen g:hasChanged(country)
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
    * @CallWhen $findRoleCondition
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