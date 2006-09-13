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
package net.java.dev.genesis.ui.paging;

import java.util.Map;
import net.java.dev.genesis.helpers.CriteriaPropertyHelper;
import net.java.dev.genesis.paging.Page;
import net.java.dev.genesis.command.hibernate.CriteriaResolver;
import net.java.dev.genesis.command.hibernate.HibernateCriteria;

public abstract class BaseCriteriaSearchForm extends BaseSearchForm {
   private Map values;

   protected abstract HibernateCriteria getHibernateCriteria();

   protected Page performSearch() throws Exception {
      final HibernateCriteria criteria = getHibernateCriteria();

      if (isResetSearch() || values == null) {
         CriteriaPropertyHelper.fillCriteria(criteria, this);
         values = ((CriteriaResolver)criteria).getPropertiesMap();
      } else {
         ((CriteriaResolver)criteria).setPropertiesMap(values);
      }

      return performSearch(getPageNumber());
   }

   protected abstract Page performSearch(int pageNumber) throws Exception;
}