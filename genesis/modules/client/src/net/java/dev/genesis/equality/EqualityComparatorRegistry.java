/*
 * The Genesis Project
 * Copyright (C) 2004-2007  Summa Technologies do Brasil Ltda.
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
import java.util.Map;

import net.java.dev.genesis.registry.RegistryFactory;

public final class EqualityComparatorRegistry {
   private static final EqualityComparatorRegistry instance = new EqualityComparatorRegistry();
   private final RegistryFactory factory = new RegistryFactory();

   private EqualityComparatorRegistry() {
      factory.register(Object.class, new DefaultEqualityComparator());
      factory.register(String.class, new StringEqualityComparator());
      factory.register(BigDecimal.class, new BigDecimalEqualityComparator());
   }

   public static EqualityComparatorRegistry getInstance() {
      return instance;
   }

   public EqualityComparator getEqualityComparator(final String compClassName, final Map attributesMap) {
      return (EqualityComparator) factory.getNewInstance(compClassName, attributesMap);
   }

   public EqualityComparator getDefaultEqualityComparatorFor(final Class clazz) {
      return getDefaultEqualityComparatorFor(clazz, null);
   }

   public EqualityComparator getDefaultEqualityComparatorFor(final Class clazz, final Map attributesMap) {
      return (EqualityComparator) factory.getExistingInstance(clazz, attributesMap);
   }
}