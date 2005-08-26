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
package net.java.dev.genesis.cloning;

import java.util.Map;

import net.java.dev.genesis.registry.RegistryFactory;

public final class ClonerRegistry {
   private static final ClonerRegistry instance = new ClonerRegistry();
	private final RegistryFactory factory = new RegistryFactory();

   private ClonerRegistry() {
      register(Object.class, new DefaultCloner());
   }

   public static ClonerRegistry getInstance() {
      return instance;
   }

   public Cloner getCloner(final String className,
         final Map attributesMap) {
      return (Cloner)factory.getNewInstance(className, attributesMap);
   }

   public Cloner getDefaultClonerFor(final Class clazz) {
      return getDefaultClonerFor(clazz, null);
   }

   public Cloner getDefaultClonerFor(final Class clazz, final Map attributesMap) {
      return (Cloner)factory.getExistingInstance(clazz, attributesMap);
   }

   public Cloner register(final Class clazz, final Cloner cloner) {
      return (Cloner)factory.register(clazz, cloner);
   }
}
