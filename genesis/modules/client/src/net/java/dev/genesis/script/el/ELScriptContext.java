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
package net.java.dev.genesis.script.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

import org.apache.taglibs.standard.functions.Functions;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;

public class ELScriptContext implements ScriptContext, VariableResolver,
      FunctionMapper {
   private final Map variables = new HashMap();
   private final Map functions = new HashMap();
   private final ExpressionEvaluator evaluator;

   protected ELScriptContext(Object root, ExpressionEvaluator evaluator) {
      this.evaluator = evaluator;
      variables.put("form", root);
      registerFunctions("", Functions.class);
      registerFunctions("genesis", getFunctions());
   }

   public void registerFunctions(String prefix, Object obj) {
      registerFunctions(prefix, obj.getClass());
   }

   public void registerFunctions(String prefix, Class functionClass) {
      Method methods[] = functionClass.getMethods();
      for (int i = 0; i < methods.length; i++) {
         Method m = methods[i];
         if (Modifier.isStatic(m.getModifiers())) {
            functions.put(prefix + ":" + m.getName(), m);
         }
      }
   }

   public Method resolveFunction(String prefix, String localName) {
      return (Method)functions.get(prefix + ":" + localName);
   }

   public Object resolveVariable(String var) throws ELException {
      return variables.get(var);
   }

   public void declare(String name, Object value, Class type) {
      variables.put(name, value);
   }

   public void declare(String name, Object value) {
      declare(name, value, null);
   }

   public Object lookup(String name) {
      return variables.get(name);
   }

   public void undeclare(String name) {
      variables.remove(name);
   }

   public Map getContextMap() {
      return variables;
   }

   public Object eval(ScriptExpression expr) {
      try {
         return evaluator.evaluate(expr.getExpressionString(), Object.class,
               getVariableResolver(), getFunctionMapper());
      } catch (ELException e) {
         throw new RuntimeException(e);
      }
   }

   public Object eval(String expr) {
      return new ELExpression(expr, evaluator).eval(this);
   }

   protected VariableResolver getVariableResolver() {
      return this;
   }

   protected FunctionMapper getFunctionMapper() {
      return this;
   }

   protected Class getFunctions() {
      return ScriptFunctionsAdapter.class;
   }
}