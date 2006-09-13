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
package net.java.dev.genesis.registry;

public class DefaultValueRegistry extends Registry {
   public static final DefaultValueRegistry instance = new DefaultValueRegistry();

   private DefaultValueRegistry() {
      register(Boolean.TYPE, Boolean.FALSE);
      register(Byte.TYPE, new Byte((byte)0));
      register(Short.TYPE, new Short((short)0));
      register(Integer.TYPE, new Integer(0));
      register(Long.TYPE, new Long(0));
      register(Float.TYPE, new Float(0));
      register(Double.TYPE, new Double(0));
   }

   public static final DefaultValueRegistry getInstance() {
      return instance;
   }
}