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

import java.util.Iterator;
import java.util.Map;


import net.java.dev.genesis.hibernate.type.HibernateTypeValue;
import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.paging.PagingException;
import net.java.dev.genesis.paging.hibernate.CriteriaPager;
import net.java.dev.genesis.paging.hibernate.QueryPager;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

public abstract class AbstractHibernateCommand implements HibernateCommand {

   private Session session;

   public void setSession(Session session) {
      this.session = session;
   }

   protected Session getSession() {
      return session;
   }

   protected void appendClause(StringBuffer where, String condition) {
      if (where.length() != 0) {
         where.append(" and ");
      }

      where.append(condition);
   }

   protected Query generateQuery(String select, Map tables, String where,
         String orderBy, Map parameters, int maxResults)
         throws HibernateException {

      StringBuffer hql = new StringBuffer(select);

      hql.append(" from ");

      Map.Entry e;

      for (Iterator i = tables.entrySet().iterator(); i.hasNext();) {
         e = (Map.Entry) i.next();
         hql.append(e.getValue()).append(' ').append(e.getKey()).append(',');
      }

      hql.setLength(hql.length() - 1);

      if (where != null && where.length() != 0) {
         hql.append(" where ").append(where);
      }

      if (orderBy != null && orderBy.trim().length() > 0) {
         hql.append(" order by ").append(orderBy);
      }

      Query q = getSession().createQuery(hql.toString());

      for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
         e = (Map.Entry) i.next();

         if (e.getValue() instanceof HibernateTypeValue) {
            final HibernateTypeValue htv = (HibernateTypeValue) e.getValue();
            q.setParameter(e.getKey().toString(), htv.getValue(), htv
                        .getType());
         } else {
            q.setParameter(e.getKey().toString(), e.getValue());
         }
      }

      if (maxResults > 0) {
         q.setMaxResults(maxResults);
      }

      return q;
   }

   protected Query generateQuery(String select, Map tables, String where,
         String orderBy, Map parameters) throws HibernateException {

      return generateQuery(select, tables, where, orderBy, parameters, 0);
   }

   protected Page getPage(final Criteria crit, final int pageNumber,
         final int resultsPerPage) throws PagingException {
      return new CriteriaPager(crit).getPage(pageNumber, resultsPerPage);
   }

   protected Page getPage(final Query query, final int pageNumber,
         final int resultsPerPage) throws PagingException {
      return new QueryPager(query).getPage(pageNumber, resultsPerPage);
   }

}