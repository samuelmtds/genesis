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

import net.java.dev.genesis.GenesisTestCase;

public class DefaultEmptyResolverTest extends GenesisTestCase {
   public void testIsEmpty() {
      DefaultEmptyResolver resolver = new DefaultEmptyResolver();

      // Test for null
      assertTrue(resolver.isEmpty(null));

      // Test for ordinary objects
      assertFalse(resolver.isEmpty(new Object()));
      assertFalse(resolver.isEmpty(new String()));
      assertFalse(resolver.isEmpty(this));

      // Test for arrays
      assertFalse(resolver.isEmpty(new Object[1]));
      assertTrue(resolver.isEmpty(new Object[0]));
   }
}