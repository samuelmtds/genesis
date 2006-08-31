/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.script.mustang;

import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.mustang.bridge.JavaxScriptBridge;
import net.java.dev.genesis.script.mustang.bridge.ScriptEngine;

public class MustangExpression extends ScriptExpression {
   private Object realCompiledScript;

   public MustangExpression(String expr, ScriptEngine engine) {
      super(expr);
      compile(engine);
   }

   protected void compile(ScriptEngine engine) {
      try {
         realCompiledScript = engine.compile(getExpressionString());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public Object evalCompiled(ScriptEngine engine, Object realCtx) throws Exception {
      if (realCompiledScript == null) {
         return engine.eval(getExpressionString());
      }

      return JavaxScriptBridge.getInstance().eval(realCompiledScript, realCtx);
   }
}