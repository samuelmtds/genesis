/*
 * The Genesis Project
 * Copyright (C) 2006-2007 Summa Technologies do Brasil Ltda.
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
import java.util.Map;

import net.java.dev.genesis.ui.binding.BoundMember;

public abstract class MockBoundMember implements BoundMember {
   private String name;
   private Map map = new HashMap();

   public MockBoundMember(String name) {
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

   public void setValue(Object value) throws Exception {
      map.put("setValue(Object)", value);
   }

   public String getValue() throws Exception {
      map.put("getValue()", Boolean.TRUE);

      return null;
   }

   public String getName() {
      return name;
   }

   public void unbind() {
      map.put("unbind()", Boolean.TRUE);
   }

   public void setEnabled(boolean enabled) {
      map.put("setEnabled(boolean)", Boolean.valueOf(enabled));
   }

   public void setVisible(boolean visible) {
      map.put("setVisible(boolean)", Boolean.valueOf(visible));
   }
}
