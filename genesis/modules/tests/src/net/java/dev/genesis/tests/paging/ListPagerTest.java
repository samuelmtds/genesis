/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.paging;

import java.util.ArrayList;
import java.util.List;

import net.java.dev.genesis.paging.ListPager;
import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.paging.PagingException;
import net.java.dev.genesis.tests.TestCase;

public class ListPagerTest extends TestCase {
   public void testIllegalConstructor() {
      try {
         new ListPager(null);
         fail("IllegalArgumentException should be thrown for null list");
      } catch (IllegalArgumentException iae) {
         // ok
      }
   }

   public void testCloneList() throws PagingException {
      testCloneList(true);
      testCloneList(false);
   }

   private void testCloneList(boolean cloneList) throws PagingException {
      int elements = 10;
      List list = createList(elements);

      ListPager pager = new ListPager(list, cloneList);
      pager.getPage(0, 1).getResults().clear();

      assertEquals(cloneList, elements == list.size());
   }

   private List createList(int elements) {
      List list = new ArrayList(elements);

      for (int i = 0; i < elements; i++) {
         list.add(new Integer(i));
      }

      return list;
   }

   public void testGetPage() throws PagingException {
      int elements = 10;
      List list = createList(elements);
      ListPager pager = new ListPager(list);

      testGetPage(pager, 0, 9, 0, 9, true, false, new Integer(0));
      testGetPage(pager, 0, 10, 0, 10, true, true, new Integer(0));
      testGetPage(pager, 1, 10, 0, 10, true, true, new Integer(0));

      testGetPage(pager, 1, 5, 1, 5, false, true, new Integer(5));
      testGetPage(pager, 2, 5, 1, 5, false, true, new Integer(5));

      testGetPage(pager, 3, 3, 3, 1, false, true, new Integer(9));
   }

   private void testGetPage(ListPager pager, int pageNumber, int resultsPerPage, 
         int expectedPageNumber, int expectedResultCount, boolean first, 
         boolean last, Integer firstValue) throws PagingException {
      Page page = pager.getPage(pageNumber, resultsPerPage);
      assertEquals(expectedPageNumber, page.getPageNumber());
      assertEquals(expectedResultCount, page.getResults().size());
      assertEquals(first, page.isFirst());
      assertEquals(last, page.isLast());
      assertEquals(firstValue, page.getResults().get(0));
   }
}