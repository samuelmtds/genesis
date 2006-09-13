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
package net.java.dev.genesis.resolvers;

import java.util.Collection;
import java.util.Map;

import net.java.dev.genesis.registry.RegistryFactory;


public class EmptyResolverRegistry extends RegistryFactory {
	private static final EmptyResolverRegistry instance = new EmptyResolverRegistry();

	private EmptyResolverRegistry() {
      register(Object.class, new DefaultEmptyResolver());
      register(String.class, new StringEmptyResolver());
      register(Collection.class, new CollectionEmptyResolver());
      register(Map.class, new MapEmptyResolver());
   }

   public static EmptyResolverRegistry getInstance() {
      return instance;
   }

   public EmptyResolver getEmptyResolver(final String resolverClassName,
         final Map attributesMap) {
      return (EmptyResolver)getNewInstance(resolverClassName, attributesMap);
   }

   public EmptyResolver getDefaultEmptyResolverFor(final Class clazz) {
      return getDefaultEmptyResolverFor(clazz, (Map)null);
   }

   public EmptyResolver getDefaultEmptyResolverFor(final Class clazz,
         final Map attributesMap) {
      return (EmptyResolver)getExistingInstance(clazz, attributesMap);
   }

}