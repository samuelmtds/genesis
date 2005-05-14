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
package net.java.dev.genesis.resolvers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import net.java.dev.genesis.GenesisTestCase;

public class CollectionEmptyResolverTest extends GenesisTestCase {
   public void testIsEmpty() {
      CollectionEmptyResolver resolver = new CollectionEmptyResolver();

      // Test for null
      assertTrue(resolver.isEmpty(null));

      // Test for non-empty collections
      assertFalse(resolver.isEmpty(fill(new ArrayList())));
      assertFalse(resolver.isEmpty(fill(new HashSet())));

      // Test for empty collections
      assertTrue(resolver.isEmpty(new ArrayList()));
      assertTrue(resolver.isEmpty(new HashSet()));
   }

   private Collection fill(Collection c) {
      c.add(new Object());

      return c;
   }
}