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

import java.util.Map;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;
import net.java.dev.genesis.script.mustang.bridge.JavaxScriptBridge;
import net.java.dev.genesis.script.mustang.bridge.ScriptEngine;

public class MustangScriptContext extends ScriptContext {
   public static final String FORM_NS = "form";
   public static final String GENESIS_FUNCTIONS_NS = "genesis";

   private final ScriptEngine scriptEngine;

   protected MustangScriptContext(String engine, Object root) {
      this.scriptEngine = JavaxScriptBridge.getInstance().createScriptEngineManager()
            .getEngineByName(engine);

      declare(FORM_NS, root);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         return scriptEngine.eval(expr.getExpressionString());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new MustangExpression(expression);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      try {
         scriptEngine.getContext().setAttribute(prefix,
               functionClass.newInstance(),
               JavaxScriptBridge.getInstance().getGlobalScope());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public void declare(String name, Object value) {
      scriptEngine.getContext().setAttribute(name, value,
            JavaxScriptBridge.getInstance().getEngineScope());
   }

   public Map getContextMap() {
      return scriptEngine.getContext().getBindings(
            JavaxScriptBridge.getInstance().getEngineScope());
   }

   public Object lookup(String name) {
      return scriptEngine.getContext().getAttribute(name);
   }

   public void undeclare(String name) {
      scriptEngine.getContext().removeAttribute(name,
            JavaxScriptBridge.getInstance().getEngineScope());
   }

   protected Class getFunctions() {
      return ScriptFunctionsAdapter.class;
   }
}