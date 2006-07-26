/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.samples.useradmin.business;

import net.java.dev.genesis.command.hibernate.AbstractHibernateCriteria;
import net.java.dev.genesis.paging.Page;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.MatchMode;

public class UserSearchCommand extends AbstractHibernateCriteria {
   public void setEmail(String email) {
      getCriteria().add(Expression.ilike("email", email, MatchMode.START));
   }

   public void setLogin(String login) {
      getCriteria().add(Expression.ilike("login", login, MatchMode.START));
   }

   public void setName(String name) {
      getCriteria().add(Expression.ilike("name", name, MatchMode.START));
   }
   
   /**
    * @Criteria(value=net.java.dev.genesis.samples.useradmin.databeans.User.class, orderby="name")
    */
   public Page getUsers(final int pageNumber) throws Exception {
      return getPage(pageNumber);
   }
}