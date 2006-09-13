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
package net.java.dev.genesis.command.hibernate;

import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.paging.PagingException;
import net.java.dev.genesis.paging.hibernate.CriteriaPager;
import net.sf.hibernate.Criteria;

public class AbstractHibernateCriteria extends AbstractHibernateCommand
      implements HibernateCriteria {
   private Criteria criteria;
   private int resultsPerPage;

   public AbstractHibernateCriteria() {
      this(10);
   }

   public AbstractHibernateCriteria(final int resultsPerPage) {
      this.resultsPerPage = resultsPerPage;
   }

   public void setCriteria(final Criteria criteria) {
      this.criteria = criteria;
   }

   protected Criteria getCriteria() {
      return criteria;
   }

   protected int getResultsPerPage() {
      return resultsPerPage;
   }

   protected void setResultsPerPage(int resultsPerPage) {
      this.resultsPerPage = resultsPerPage;
   }

   protected Page getPage(final int pageNumber) throws PagingException {
      return getPage(pageNumber, resultsPerPage);
   }

   protected Page getPage(final int pageNumber, final int resultsPerPage)
         throws PagingException {
      return new CriteriaPager(criteria).getPage(pageNumber, resultsPerPage);
   }
}