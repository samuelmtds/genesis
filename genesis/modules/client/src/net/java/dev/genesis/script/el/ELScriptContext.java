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

import net.java.dev.genesis.script.PrimitiveFunctions;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptException;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFunctionsAdapter;

public class ELScriptContext extends ScriptContext implements VariableResolver,
      FunctionMapper {
   public static final String FORM_NS = "form";
   public static final String GENESIS_FUNCTIONS_NS = "g";

   private final Map variables = new HashMap();
   private final Map functions = new HashMap();
   private final ExpressionEvaluator evaluator;

   protected ELScriptContext(Object root, ExpressionEvaluator evaluator) {
      variables.put(FORM_NS, root);
      this.evaluator = evaluator;
      registerFunctions("", Functions.class);
      registerFunctions("", PrimitiveFunctions.class);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         return evaluator.evaluate(expr.getExpressionString(), Object.class,
               getVariableResolver(), getFunctionMapper());
      } catch (ELException e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new ELExpression(expression, evaluator);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      Method methods[] = functionClass.getMethods();
      for (int i = 0; i < methods.length; i++) {
         Method m = methods[i];
         if (Modifier.isStatic(m.getModifiers())) {
            functions.put(prefix + ':' + m.getName(), m);
         }
      }
   }

   public Method resolveFunction(String prefix, String localName) {
      return (Method) functions.get(prefix + ':' + localName);
   }

   public Object resolveVariable(String var) throws ELException {
      return variables.get(var);
   }

   public void declare(String name, Object value) {
      variables.put(name, value);
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