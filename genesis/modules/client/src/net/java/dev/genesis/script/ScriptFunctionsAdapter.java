/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.script;

public class ScriptFunctionsAdapter {
   public static boolean hasChanged(Object obj) throws Exception {
      return ScriptFunctions.hasChanged(ScriptUtils.getCurrentContext(),
            ScriptUtils.getScriptValue(obj));
   }

   public static boolean hasNotChanged(Object obj) throws Exception {
      return !hasChanged(obj);
   }

   public static boolean isEmpty(final Object obj) throws Exception {
      return ScriptFunctions.isEmpty(ScriptUtils.getCurrentContext(),
            ScriptUtils.getScriptValue(obj));
   }

   public static boolean isNotEmpty(final Object obj) throws Exception {
      return !isEmpty(obj);
   }

   public static boolean equals(final Object arg1, final Object arg2)
         throws Exception {
      final ScriptValue[] values = ScriptUtils.getScriptValues(arg1, arg2);
      return ScriptFunctions.equals(ScriptUtils.getCurrentContext(), values[0],
            values[1]);
   }

   public static boolean notEquals(final Object arg1, final Object arg2)
         throws Exception {
      return !equals(arg1, arg2);
   }
}