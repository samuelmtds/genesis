/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.java.dev.genesis.GenesisTestCase;

public class DefaultValueRegistryTest extends GenesisTestCase {
   public DefaultValueRegistryTest() {
      super("Default Registry Unit Test");
   }
   
   public void test() {
      Map defaultValues = new HashMap() {
         {
            put(Boolean.TYPE, Boolean.FALSE);
            put(Byte.TYPE, new Byte((byte)0));
            put(Short.TYPE, new Short((short)0));
            put(Integer.TYPE, new Integer(0));
            put(Long.TYPE, new Long(0));
            put(Float.TYPE, new Float(0));
            put(Double.TYPE, new Double(0));
         }
      };

      for (final Iterator i = defaultValues.entrySet().iterator(); i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();

         assertEquals(entry.getValue(), DefaultValueRegistry.getInstance().
               get((Class)entry.getKey()));
      }
   }
}