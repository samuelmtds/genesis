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
package net.java.dev.genesis.samples.useradmin.databeans;

import java.io.Serializable;
import java.util.Date;


/**
 * @hibernate.class
 * 
 * @hibernate.query name="User.findByRole"
 * 					query="from User u where u.role.code = :roleCode"
 */
public class User implements Serializable {
   private Long id;
   private String name;
   private String login;
   private String password;
   private String email;
   private Date birthday;
   private String address;
   private Role role;
   private Country country;
   private State state;

   /**
    * @hibernate.property
    */
   public Date getBirthday() {
      return birthday;
   }

   public void setBirthday(Date birthday) {
      this.birthday = birthday;
   }
   
   /**
    * @hibernate.property type="net.java.dev.genesis.samples.useradmin.databeans.CountryType"
    */
   public Country getCountry() {
      return country;
   }

   public void setCountry(Country country) {
      this.country = country;
   }

   /**
    * @hibernate.property
    */
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @hibernate.property
    */
   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * @hibernate.many-to-one
    */
   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   /**
    * @hibernate.id generator-class="increment"
    */
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   /**
    * @hibernate.property
    */
   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      this.login = login;
   }

   /**
    * @hibernate.property
    */
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * @hibernate.property
    */
   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * @hibernate.property type="net.java.dev.genesis.samples.useradmin.databeans.StateType"
    */
   public State getState() {
      return state;
   }

   public void setState(State state) {
      this.state = state;
   }
}