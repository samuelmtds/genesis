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

import java.io.Serializable;
import java.util.List;

import net.java.dev.genesis.helpers.CriteriaPropertyHelper;
import example.business.UserRemoveCommand;
import example.business.UserSearchCommand;
import example.databeans.User;

/**
 * @Form
 */
public class UserListForm implements Serializable {
   private String name;
   private String login;
   private String email;

   private List users;

   /**
    * @Condition emailFilled=not(g:isEmpty(email))
    */
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @Condition loginFilled=not(g:isEmpty(login))
    */
   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      this.login = login;
   }

   /**
    * @Condition nameFilled=not(g:isEmpty(name))
    */
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List getUsers() {
      return users;
   }

   public User getUser() {
      return users == null || users.isEmpty() ? null : (User) users.get(0);
   }

   public void setUsers(List users) {
      this.users = users;
   }

   /**
    * @DataProvider objectField=users
    */
   public List search() throws Exception {
      final UserSearchCommand command = new UserSearchCommand();
      CriteriaPropertyHelper.fillCriteria(command, this);
      return command.getUsers();
   }

   /**
    * @Action
    * @EnabledWhen not(g:isEmpty(users))
    */
   public void remove() throws Exception {
      if (users != null) {
         new UserRemoveCommand().removeUser(users);
      }
   }
   
   /**
    * @Action
    */
   public void reset(){
      setName(null);
      setEmail(null);
      setLogin(null);
   }

}