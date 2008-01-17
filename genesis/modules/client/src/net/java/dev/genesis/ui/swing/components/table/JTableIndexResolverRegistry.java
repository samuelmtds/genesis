/*
 * The Genesis Project
 * Copyright (C) 2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components.table;

import net.java.dev.genesis.registry.Registry;

import javax.swing.JTable;

public class JTableIndexResolverRegistry {
   private static final JTableIndexResolverRegistry instance =
         new JTableIndexResolverRegistry();
   private final Registry registry = new Registry();

   private JTableIndexResolverRegistry() {
      register(JTable.class, new DefaultJTableIndexResolver());
   }

   public static JTableIndexResolverRegistry getInstance() {
      return instance;
   }

   public void deregister() {
      registry.deregister();
   }

   public void deregister(Class clazz) {
      registry.deregister(clazz);
   }

   public DefaultJTableIndexResolver register(Class clazz,
         DefaultJTableIndexResolver binder) {
      return (DefaultJTableIndexResolver)registry.register(clazz, binder);
   }

   public DefaultJTableIndexResolver get(Class clazz) {
      return (DefaultJTableIndexResolver)registry.get(clazz);
   }

   public DefaultJTableIndexResolver get(Class clazz, boolean superClass) {
      return (DefaultJTableIndexResolver)registry.get(clazz, superClass);
   }

   public DefaultJTableIndexResolver get(Object o) {
      return (DefaultJTableIndexResolver)registry.get(o);
   }
}
