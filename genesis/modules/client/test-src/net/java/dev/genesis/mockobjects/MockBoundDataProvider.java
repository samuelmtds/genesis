/*
 * The Genesis Project
 * Copyright (C) 2006 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.mockobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.dev.genesis.ui.binding.BoundDataProvider;

public class MockBoundDataProvider implements BoundDataProvider {
   private String name;
   private Map map = new HashMap();

   public MockBoundDataProvider(String name) {
      this.name = name;
   }
   
   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }
   
   public void clear() {
      map.clear();
   }

   public void updateIndexes(int[] indexes) {
      map.put("updateIndexes(int[])", indexes);
   }

   public void updateList(List data) throws Exception {
      map.put("updateList(List)", data);
   }

   public String getName() {
      return name;
   }

   public void unbind() {
      map.put("unbind()", Boolean.TRUE);
   }
}
