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

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.ObjectRegistry;

public class BSFScriptContext implements ScriptContext {
   private final BSFManager manager = new BSFManager();
   private final Hashtable contextMap = new Hashtable();
   private final String lang;

   protected BSFScriptContext(String lang, Object root) {
      this.lang = lang;
      manager.setObjectRegistry(new BSFObjectRegistry());
      try {
         manager.declareBean("form", root, null);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      registerFunctions("genesis", new ScriptFunctionsAdapter());
   }

   public void registerFunctions(String prefix, Object functions) {
      try {
         manager.declareBean(prefix, functions, null);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void registerFunctions(String prefix, Class functionClass) {
      try {
         registerFunctions(prefix, functionClass.newInstance());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void declare(String name, Object value, Class type) {
      try {
         manager.declareBean(name, value, type);
      } catch (BSFException e) {
         throw new RuntimeException(e);
      }
   }

   public void declare(String name, Object value) {
      declare(name, value, value == null ? null : value.getClass());
   }

   public Object lookup(String name) {
      return manager.lookupBean(name);
   }

   public void undeclare(String name) {
      try {
         manager.undeclareBean(name);
      } catch (BSFException e) {
         throw new RuntimeException(e);
      }
   }

   public Map getContextMap() {
      return contextMap;
   }

   public Object eval(ScriptExpression expr) {
      try {
         return manager.eval(lang, expr.getExpressionString(), 0, 0, expr
               .getExpressionString());
      } catch (BSFException e) {
         throw new RuntimeException(e);
      }
   }

   public Object eval(String expr) {
      return eval(new BSFExpression(expr));
   }

   public class BSFObjectRegistry extends ObjectRegistry {
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