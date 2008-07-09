/*
 * The Genesis Project
 * Copyright (C) 2005-2008 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.paging;

import java.util.ArrayList;
import java.util.List;
import net.java.dev.genesis.util.Bundle;

public class ListPager implements Pager {
   private final List list;
   private final boolean cloneList;

   public ListPager(List list) {
      this(list, true);
   }

   public ListPager(List list, boolean cloneList) {
      if (list == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ListPager.class,
               "LIST_CANNOT_BE_NULL")); // NOI18N
      }

      this.list = list;
      this.cloneList = cloneList;
   }

   public Page getPage(int pageNumber, int resultsPerPage) 
         throws PagingException {
      final int listSize = list.size();

      int initialPosition = pageNumber * resultsPerPage;

      if (initialPosition >= listSize) {
         pageNumber = listSize / resultsPerPage;

         if (listSize % resultsPerPage == 0) {
            pageNumber = Math.max(0, pageNumber - 1);
         }

         initialPosition = pageNumber * resultsPerPage;
      }

      int finalPosition = Math.min(listSize, initialPosition + resultsPerPage);

      return new PageImpl(getListSection(initialPosition, finalPosition), 
            pageNumber, resultsPerPage, finalPosition == listSize);
   }

   protected List getListSection(int initialPosition, int finalPosition) {
      final List subList = list.subList(initialPosition, finalPosition);

      return (cloneList) ? new ArrayList(subList) : subList;
   }
}
