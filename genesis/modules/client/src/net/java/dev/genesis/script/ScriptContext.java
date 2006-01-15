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

import java.util.Map;

public abstract class ScriptContext {
   public abstract Object getContextBean();
   public abstract Object lookup(String name);
   public abstract void declare(String name, Object value);
   public abstract void undeclare(String name);
   public abstract void registerFunctions(String prefix, Class functionClass);
   public abstract Map getContextMap();

   protected abstract Object doEval(ScriptExpression expr);
   protected abstract ScriptExpression newScriptExpression(String expression);

   public Object eval(ScriptExpression expr) {
      ScriptUtils.enter(this);

      try {
         return doEval(expr);
      } finally {
         ScriptUtils.exit();
      }
   }

   public Object eval(String expression) {
      return eval(newScriptExpression(expression));
   }

   protected ScriptableObject proxy(final Object object) {
      if (object instanceof ScriptableObject) {
         return (ScriptableObject) object;
      }

      return DynamicProxy.proxy(object);
   }
}
