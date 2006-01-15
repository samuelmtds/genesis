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

import java.util.Hashtable;
import java.util.Map;

import net.java.dev.genesis.script.PrimitiveFunctions;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;
import net.java.dev.genesis.script.ScriptableObject;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.ObjectRegistry;

public class BSFScriptContext extends ScriptContext {
   public static final String FORM_NS = "form";
   public static final String GENESIS_FUNCTIONS_NS = "genesis";
   public static final String PRIMITIVE_FUNCTIONS_NS = "types";

   private final BSFManager manager = new BSFManager();
   private final Hashtable contextMap = new Hashtable();
   private final String lang;
   private final ScriptableObject contextBean;

   protected BSFScriptContext(String lang, final Object root) {
      this.lang = lang;
      manager.setObjectRegistry(getObjectRegistry());
      declare(FORM_NS, contextBean = proxy(root));
      registerFunctions(PRIMITIVE_FUNCTIONS_NS, PrimitiveFunctions.class);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   public Object getContextBean() {
      return contextBean;
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         contextBean.beforeEval();
         return manager.eval(lang, expr.getExpressionString(), 0, 0, expr
               .getExpressionString());
      } catch (BSFException e) {
         throw new ScriptException(e.getMessage(), e);
      } finally {
         contextBean.afterEval();
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new BSFExpression(expression);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      try {
         manager.declareBean(prefix, functionClass.newInstance(), null);
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public void declare(String name, Object value) {
      try {
         manager.declareBean(name, value, null);
      } catch (BSFException e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public Object lookup(String name) {
      return manager.lookupBean(name);
   }

   public void undeclare(String name) {
      try {
         manager.undeclareBean(name);
      } catch (BSFException e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public Map getContextMap() {
      return contextMap;
   }

   protected ObjectRegistry getObjectRegistry() {
      return new BSFObjectRegistry();
   }

   protected Class getFunctions() {
      return ScriptFunctionsAdapter.class;
   }

   protected class BSFObjectRegistry extends ObjectRegistry {
      public Object lookup(String name) throws IllegalArgumentException {
         Object obj = contextMap.get(name);

         if (obj == null) {
            throw new IllegalArgumentException("object '" + name
                  + "' not in registry");
         }

         return obj;
      }

      public void register(String name, Object obj) {
         contextMap.put(name, obj);
      }

      public void unregister(String name) {
         contextMap.remove(name);
      }
   }
}