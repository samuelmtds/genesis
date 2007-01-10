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
package net.java.dev.genesis.script.javaxscript;

import net.java.dev.genesis.script.ScriptFunctionsAdapter;

public class JavaxScriptFunctions {
   public boolean hasChanged(Object o) throws Exception {
      return ScriptFunctionsAdapter.hasChanged(o);
   }

   public boolean hasNotChanged(Object o) throws Exception {
      return ScriptFunctionsAdapter.hasNotChanged(o);
   }

   public boolean isEmpty(final Object o) throws Exception {
      return ScriptFunctionsAdapter.isEmpty(o);
   }

   public boolean isNotEmpty(final Object o) throws Exception {
      return ScriptFunctionsAdapter.isNotEmpty(o);
   }

   public boolean equals(final Object arg1, final Object arg2)
         throws Exception {
      return ScriptFunctionsAdapter.equals(arg1, arg2);
   }

   public boolean notEquals(final Object arg1, final Object arg2)
         throws Exception {
      return ScriptFunctionsAdapter.notEquals(arg1, arg2);
   }
}
