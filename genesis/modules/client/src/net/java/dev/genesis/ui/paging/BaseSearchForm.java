/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.paging;

import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.ui.BaseForm;

public abstract class BaseSearchForm extends BaseForm {
   private int pageNumber;
   private boolean lastPage = true;
   private boolean runSearch;
   private boolean resetSearch = true;
   private Page page;

   protected abstract Page performSearch() throws Exception;

   public void search() throws Exception {
      if (isResetSearch()) {
         setPageNumber(0);
      }

      setPage(performSearch());

      setPageNumber(getPage().getPageNumber());
      setLastPage(getPage().isLast());
      setRunSearch(false);
      setResetSearch(true);
   }

   /**
    * @Action
    * @VisibleWhen({
    *                "beanshell", "form.getPageNumber() > 0",
    *                "javascript", "form.pageNumber > 0",
    *                "jxpath", "pageNumber > 0",
    *                "el", "${form.pageNumber > 0}"
    *              })
    */
   public void previousPage() {
      setPageNumber(getPageNumber() - 1);
      setRunSearch(true);
      setResetSearch(false);
   }

   /**
    * @Action
    * @VisibleWhen({
    *             "beanshell", "!form.isLastPage()",
    *             "javascript", "!form.lastPage",
    *             "jxpath", "lastPage=false()",
    *             "el", "${form.lastPage}"
    *             })
    */
   public void nextPage() {
      pageNumber++;
      setRunSearch(true);
      setResetSearch(false);
   }

   public int getPageNumber() {
      return pageNumber;
   }

   protected void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }

   public boolean isLastPage() {
      return lastPage;
   }

   protected void setLastPage(boolean lastPage) {
      this.lastPage = lastPage;
   }

   public boolean isRunSearch() {
      return runSearch;
   }

   protected void setRunSearch(boolean runSearch) {
      this.runSearch = runSearch;
   }

   protected boolean isResetSearch() {
      return resetSearch;
   }

   protected void setResetSearch(boolean resetSearch) {
      this.resetSearch = resetSearch;
   }

   protected Page getPage() {
      return page;
   }

   protected void setPage(Page page) {
      this.page = page;
   }
}