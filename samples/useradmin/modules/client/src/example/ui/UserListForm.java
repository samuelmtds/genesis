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
import net.java.dev.genesis.paging.Page;
import example.business.UserRemoveCommand;
import example.business.UserSearchCommand;
import example.databeans.User;

/**
 * @Form
 */
public class UserListForm implements Serializable {
   private static final int RESULTS_PER_PAGE = 10;
   private int pageNumber = 1;
   private boolean lastPage = true;

   private String name;
   private String login;
   private String email;

   private List users;

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

   public boolean isLastPage() {
      return lastPage;
   }

   public void setLastPage(boolean lastPage) {
      this.lastPage = lastPage;
   }

   public int getPageNumber() {
      return pageNumber;
   }

   public void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }

   /**
    * @Condition usersSelected=g:isNotEmpty(users)
    */
   public List getUsers() {
      return users;
   }

   public User getUser() {
      return users == null || users.isEmpty() ? null : (User)users.get(0);
   }

   public void setUsers(List users) {
      this.users = users;
   }

   /**
    * @Action
    * @DataProvider objectField=users
    */
   public List search() throws Exception {
      final UserSearchCommand command = new UserSearchCommand();
      CriteriaPropertyHelper.fillCriteria(command, this);
      Page page = command.getUsers(pageNumber, RESULTS_PER_PAGE);
      setLastPage(page.isLast());
      setPageNumber(page.getPageNumber());
      return page.getResults();
   }

   /**
    * @Action
    */
   public void create() throws Exception {
   }

   /**
    * @Action
    * @EnabledWhen $usersSelected
    */
   public void update() throws Exception {
   }

   /**
    * @Action
    * @EnabledWhen $usersSelected
    */
   public void remove() throws Exception {
      new UserRemoveCommand().removeUser(users);
   }

   /**
    * @Action
    * @VisibleWhen g:isNotEmpty(email)
    * 					or g:isNotEmpty(login)
    * 					or g:isNotEmpty(name)
    */
   public void reset() {
      setName(null);
      setEmail(null);
      setLogin(null);
   }

   /**
    * @Action
    * @VisibleWhen pageNumber > 1
    */
   public void previousPage() {
      pageNumber--;
   }
   
   /**
    * @Action
    * @VisibleWhen lastPage=false()
    */
   public void nextPage() {
      pageNumber++;
   }

}