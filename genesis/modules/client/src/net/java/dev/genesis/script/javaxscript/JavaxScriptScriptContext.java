/*
 * The Genesis Project
 * Copyright (C) 2006-2008  Summa Technologies do Brasil Ltda.
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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.javaxscript.bridge.JavaxScriptBridge;
import net.java.dev.genesis.script.javaxscript.bridge.ScriptEngine;

public class JavaxScriptScriptContext extends ScriptContext {
   public static final String FORM_NS = "form"; // NOI18N
   public static final String GENESIS_FUNCTIONS_NS = "genesis"; // NOI18N

   private final ScriptEngine scriptEngine;
   private final Map unmodifiableMap;
   private Object realContext;

   protected JavaxScriptScriptContext(ScriptEngine engine, Object root) {
      this.scriptEngine = engine;
      this.realContext = engine.getContext().getRealContext();
      this.unmodifiableMap = Collections.unmodifiableMap(scriptEngine.getContext().getBindings(
            JavaxScriptBridge.getInstance().getEngineScope()));
      declare(FORM_NS, root);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }
   
   protected ScriptEngine getScriptEngine() {
      return scriptEngine;
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         return ((JavaxScriptExpression) expr).evalCompiled(scriptEngine,
               realContext);
      } catch (InvocationTargetException ite) {
         throw new ScriptException(ite.getTargetException().getMessage(), ite.getTargetException());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new JavaxScriptExpression(expression, scriptEngine);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      try {
         scriptEngine.getContext().setAttribute(prefix,
               functionClass.newInstance(),
               JavaxScriptBridge.getInstance().getEngineScope());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public void declare(String name, Object value) {
      scriptEngine.getContext().setAttribute(name, value,
            JavaxScriptBridge.getInstance().getEngineScope());
   }

   public Map getContextMap() {
      return unmodifiableMap;
   }

   public Object lookup(String name) {
      return scriptEngine.getContext().getAttribute(name);
   }

   public void undeclare(String name) {
      scriptEngine.getContext().removeAttribute(name,
            JavaxScriptBridge.getInstance().getEngineScope());
   }

   protected Class getFunctions() {
      return JavaxScriptFunctions.class;
   }
}
