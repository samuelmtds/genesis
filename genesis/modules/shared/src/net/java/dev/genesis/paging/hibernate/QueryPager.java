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
package net.java.dev.genesis.paging.hibernate;

import java.util.List;

import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.paging.PageImpl;
import net.java.dev.genesis.paging.Pager;
import net.java.dev.genesis.paging.PagingException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;


public class QueryPager implements Pager {

   private final Query query;

   public QueryPager(final Query query) {
      this.query = query;
   }

   public Page getPage(final int pageNumber, final int resultsPerPage)
         throws PagingException {
      return getPage(pageNumber, resultsPerPage, false);
   }

   private Page getPage(final int pageNumber, final int resultsPerPage,
         boolean last) throws PagingException {
      try {
         query.setFirstResult((pageNumber - 1) * resultsPerPage);
         query.setMaxResults(resultsPerPage);
         List results = query.list();
         if (pageNumber > 1 && results.isEmpty()) {
            return getPage(pageNumber - 1, resultsPerPage, true);
         }
         return new PageImpl(results, pageNumber, resultsPerPage, last ? true
               : results.size() < resultsPerPage);
      } catch (HibernateException e) {
         throw new PagingException(e);
      }
   }

}