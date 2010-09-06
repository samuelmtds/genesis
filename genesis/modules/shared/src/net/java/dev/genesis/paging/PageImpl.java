/*
 * The Genesis Project
 * Copyright (C) 2004-2010  Summa Technologies do Brasil Ltda.
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

import java.util.Collections;
import java.util.List;

public class PageImpl implements Page {
   private static final Page EMPTY_PAGE = new PageImpl(Collections.emptyList(),
         0, 0, true);

   private final List results;
   private final int resultsPerPage;
   private final int pageNumber;
   private final boolean last;

   public PageImpl(List results, int pageNumber, int resultsPerPage,
         boolean last) {
      this.results = results;
      this.pageNumber = pageNumber;
      this.resultsPerPage = resultsPerPage;
      this.last = last;
   }

   public List getResults() {
      return results;
   }

   public int getResultsPerPage() {
      return resultsPerPage;
   }

   public int getPageNumber() {
      return pageNumber;
   }

   public boolean isFirst() {
      return pageNumber == 0;
   }

   public boolean isLast() {
      return last;
   }

   public static Page empty() {
      return EMPTY_PAGE;
   }
}