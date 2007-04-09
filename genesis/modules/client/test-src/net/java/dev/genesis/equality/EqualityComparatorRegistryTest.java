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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import net.java.dev.genesis.GenesisTestCase;

public class EqualityComparatorRegistryTest extends GenesisTestCase {
   public void testGetDefaultEqualityComparatorForClass() {
      assertEquals(DefaultEqualityComparator.class, EqualityComparatorRegistry
            .getInstance().getDefaultEqualityComparatorFor(Object.class)
            .getClass());
      assertEquals(StringEqualityComparator.class, EqualityComparatorRegistry
            .getInstance().getDefaultEqualityComparatorFor(String.class)
            .getClass());
      assertEquals(BigDecimalEqualityComparator.class, EqualityComparatorRegistry
            .getInstance().getDefaultEqualityComparatorFor(BigDecimal.class)
            .getClass());
   }

   public void testGetDefaultEqualityComparatorForClassMap() {
      assertEquals(StringEqualityComparator.class, EqualityComparatorRegistry
            .getInstance().getDefaultEqualityComparatorFor(String.class, null)
            .getClass());

      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEqualityComparator)EqualityComparatorRegistry
            .getInstance().getDefaultEqualityComparatorFor(String.class, 
            attributes)).isTrim());
   }

   public void testGetEqualityComparator() {
      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEqualityComparator)EqualityComparatorRegistry
            .getInstance().getEqualityComparator(StringEqualityComparator.class
            .getName(), attributes)).isTrim());
   }
}
