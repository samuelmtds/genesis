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
import net.java.dev.genesis.command.hibernate.HibernateCriteria;

import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.samples.useradmin.business.UserRemoveCommand;
import net.java.dev.genesis.samples.useradmin.business.UserSearchCommand;
import net.java.dev.genesis.samples.useradmin.databeans.User;
import net.java.dev.genesis.ui.paging.BaseCriteriaSearchForm;

/**
 * @Form
 */
public class UserListForm extends BaseCriteriaSearchForm {
   private String name;
   private String login;
   private String email;

   private List users;
   private final UserSearchCommand command = new UserSearchCommand();

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
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

   /**
    * @Condition usersSelected=genesis.isNotEmpty('form:users')
    */
   public List getUsers() {
      return users;
   }

   public void setUsers(List users) {
      this.users = users;
   }

   public User getUser() {
      return users == null || users.isEmpty() ? null : (User)users.get(0);
   }

   protected HibernateCriteria getHibernateCriteria() {
      return command;
   }
   
   protected Page performSearch(int pageNumber) throws Exception {
      return command.getUsers(pageNumber);
   }

   /**
    * @Action
    * @CallWhen form.isRunSearch()
    * @DataProvider objectField=users callOnInit=false
    */
   public List doSearch() throws Exception {
      search();

      return getPage().getResults();
   }

   /**
    * @Action
    * @EnabledWhen form.getUsers().size() == 1
    */
   public void update() throws Exception {
   }

   /**
    * @Action
    * @EnabledWhen  usersSelected
    */
   public void remove() throws Exception {
      new UserRemoveCommand().removeUser(users);
   }

   /**
    * @Action
    */
   public void reset() {
      setName(null);
      setEmail(null);
      setLogin(null);
   }
   
   public void setRunSearch(boolean runSearch) {
      super.setRunSearch(runSearch);
   }
   
   public void setResetSearch(boolean resetSearch) {
      super.setResetSearch(resetSearch);
   }
   
}
