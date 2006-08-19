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
package net.java.dev.genesis.script.bsf;

import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;

public class BSFScript implements Script {
   private final String lang;

   protected BSFScript(String lang) {
      this.lang = lang;
   }

   public ScriptContext newContext(Object root) {
      return new BSFScriptContext(lang, root);
   }

   public ScriptExpression compile(String expression) {
      return new BSFExpression(expression);
   }
}