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

import java.util.Collection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import net.java.dev.genesis.samples.useradmin.business.RoleSearchCommand;
import net.java.dev.genesis.samples.useradmin.business.UserCreateCommand;
import net.java.dev.genesis.samples.useradmin.business.UserUpdateCommand;
import net.java.dev.genesis.samples.useradmin.databeans.Country;
import net.java.dev.genesis.samples.useradmin.databeans.Role;
import net.java.dev.genesis.samples.useradmin.databeans.State;
import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.reusablecomponents.lang.Enum;


/**
 * @Form
 * @Condition findRoleCondition=g:isNotEmpty(roleCode) and g:hasChanged(roleCode)
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

   public void setBirthday(Date birthday) {
      this.birthday = birthday;
   }

   public Country getCountry() {
      return country;
   }

   public void setCountry(Country country) {
      this.country = country;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @ClearOn g:isEmpty(roleCode)
    */
   public Role getRole() {
      return role;
   }

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

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      this.login = login;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public State getState() {
      return state;
   }

   public void setState(State state) {
      this.state = state;
   }

   /**
    * @DataProvider objectField=country
    */
   public Collection provideCountries() throws Exception {
      return Enum.getInstances(Country.class);
   }

   /**
    * @DataProvider objectField=state
    * @CallWhen g:hasChanged(country)
    */
   public Collection provideState() throws Exception {
      return State.getStates(getCountry());
   }

   /**
    * @Action
    * @EnabledWhen g:isNotEmpty(name) and
    * 					g:isNotEmpty(login) and
    * 					g:isNotEmpty(password) and
    * 					g:isNotEmpty(email) and
    * 					g:isNotEmpty(birthday) and
    * 					g:isNotEmpty(address) and
    * 					g:isNotEmpty(role) and
    * 					g:isNotEmpty(country) and
    * 					g:isNotEmpty(state)
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
      setRole(new RoleSearchCommand().getRole(roleCode));
   }

   /**
    * @Action
    */
   public void chooseRole() throws Exception {
   }
}