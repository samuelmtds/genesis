/*
 * The Genesis Project
 * Copyright (C) 2007  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.equality;

import net.java.dev.genesis.GenesisTestCase;

public class StringEqualityComparatorTest extends GenesisTestCase {
   public void testComparator() {
      StringEqualityComparator comparator = new StringEqualityComparator();

      String s = "a";

      assertTrue(comparator.equals(null, null));
      assertFalse(comparator.equals(null, s));
      assertFalse(comparator.equals(s, null));
      assertTrue(comparator.equals(s, s));
      assertTrue(comparator.equals(s, new String("a")));
      assertTrue(comparator.equals(s, "a "));

      comparator.setTrim(false);
      assertFalse(comparator.isTrim());
      assertFalse(comparator.equals(s, "a "));
   }
}
