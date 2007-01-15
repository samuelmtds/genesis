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

import java.util.Comparator;
import net.java.dev.genesis.GenesisTestCase;

public class EqualityComparatorAdapterTest extends GenesisTestCase {
   public void testComparator() {
      Comparator adaptee = new Comparator() {
         public int compare(Object o1, Object o2) {
            Comparable c1 = (Comparable)o1;
            Comparable c2 = (Comparable)o2;

            return c1 != null ? (c2 == null ? -1 : -c1.compareTo(c2)) : 
               (c2 == null ? 0 : 1);
         }
      };
      
      EqualityComparatorAdapter comparator = new EqualityComparatorAdapter(
            adaptee);

      String s1 = "a";
      String s2 = "b";
      Integer i1 = new Integer(1);
      Integer i2 = new Integer(2);

      assertTrue(comparator.equals(null, null));
      assertFalse(comparator.equals(null, s1));
      assertFalse(comparator.equals(s1, null));

      assertTrue(comparator.equals(s1, s1));
      assertTrue(comparator.equals(s1, new String("a")));
      assertFalse(comparator.equals(s1, s2));

      assertTrue(comparator.equals(i1, i1));
      assertTrue(comparator.equals(i1, new Integer(1)));
      assertFalse(comparator.equals(i1, i2));
      
      try {
         assertFalse(comparator.equals(s1, i1));
         fail("A ClassCastException should be thrown");
      } catch (ClassCastException cce) {
         // expected
      }

      assertEquals(-i2.compareTo(i1), adaptee.compare(i2, i1));
      assertEquals(comparator.compare(i2, i1), adaptee.compare(i2, i1));
   }
}
