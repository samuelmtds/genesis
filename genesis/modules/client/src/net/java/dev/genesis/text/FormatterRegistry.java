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
package net.java.dev.genesis.text;

import java.util.HashMap;
import java.util.Map;
import net.java.dev.reusablecomponents.lang.Enum;

public final class FormatterRegistry {
   private static final FormatterRegistry instance = new FormatterRegistry();
   private final Map registry = new HashMap();

   private FormatterRegistry() {
      register(Object.class, new DefaultFormatter());
      register(Enum.class, new EnumFormatter());
   }

   public static FormatterRegistry getInstance() {
      return instance;
   }

   public Formatter register(Class clazz, Formatter f) {
      return (Formatter)registry.put(clazz, f);
   }

   public Formatter get(Class clazz) {
      return (Formatter)registry.get(clazz);
   }

   public Formatter get(Class clazz, boolean superClass) {
      if (!superClass) {
         return get(clazz);
      }

      Formatter formatter;

      while ((formatter = get(clazz)) == null) {
         clazz = clazz.getSuperclass();
      }

      return formatter;
   }

   public Formatter get(Object o) {
      final Class clazz = o == null ? Object.class : o.getClass();

      return get(clazz, true);
   }

   public String format(Object o) {
      return get(o).format(o);
   }
}