/*
 * The Genesis Project
 * Copyright (C) 2007 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.mockobjects;

import java.util.Map;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFactory;

public class MockScriptFactory implements ScriptFactory {
   public Script newScript() {
      return new Script() {
         public ScriptContext newContext(Object root) {
            return new MockScriptContext();
         }

         public ScriptExpression compile(String expression) {
            return new MockScriptExpression();
         }
      };
   }
}

class MockScriptContext extends ScriptContext {
   public Object lookup(String name) {
      return null;
   }

   public void declare(String name, Object value) {
   }

   public void undeclare(String name) {
   }

   public void registerFunctions(String prefix, Class functionClass) {
   }

   public Map getContextMap() {
      return null;
   }

   protected Object doEval(ScriptExpression expr) {
      return null;
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new MockScriptExpression();
   }
}

class MockScriptExpression extends ScriptExpression {
   public MockScriptExpression() {
      super(null);
   }
}