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
package net.java.dev.genesis.script.bsf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.script.PrimitiveFunctions;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.ObjectRegistry;

public class BSFScriptContext extends ScriptContext {
   public static final String FORM_NS = "form";
   public static final String GENESIS_FUNCTIONS_NS = "genesis";
   public static final String PRIMITIVE_FUNCTIONS_NS = "types";

   private final BSFManager manager = new BSFManager();
   private final Map contextMap = new HashMap();
   private final String lang;

   protected BSFScriptContext(String lang, final Object root) {
      this.lang = lang;
      manager.setObjectRegistry(getObjectRegistry());
      declare(FORM_NS, root);
      registerFunctions(PRIMITIVE_FUNCTIONS_NS, PrimitiveFunctions.class);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         return manager.eval(lang, expr.getExpressionString(), 0, 0, expr
               .getExpressionString());
      } catch (BSFException e) {
         throw new ScriptException(e.getMessage(), e);
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
      return Collections.unmodifiableMap(contextMap);
   }

   protected ObjectRegistry getObjectRegistry() {
      return new BSFObjectRegistry();
   }

   protected Class getFunctions() {
      return ScriptFunctionsAdapter.class;
   }

   protected class BSFObjectRegistry extends ObjectRegistry {
      private final Map registry = new HashMap();

      public Object lookup(String name) throws IllegalArgumentException {
         Object obj = registry.get(name);

         if (obj == null) {
            throw new IllegalArgumentException("object '" + name
                  + "' not in registry");
         }

         return obj;
      }

      public void register(String name, Object obj) {
         registry.put(name, obj);

         if (obj instanceof BSFDeclaredBean) {
            contextMap.put(name, ((BSFDeclaredBean)obj).bean);   
         } else {
            contextMap.put(name, obj);
         }
      }

      public void unregister(String name) {
         registry.remove(name);
         contextMap.remove(name);
      }
   }
}