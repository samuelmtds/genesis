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
package net.java.dev.genesis.script.jxpath;

import java.util.Map;

import net.java.dev.genesis.commons.jxpath.VariablesImpl;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptExpression;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;

public class JXPathScriptContext implements ScriptContext {
   private JXPathContext ctx;
   private FunctionLibrary functionLib = new FunctionLibrary();

   protected JXPathScriptContext(Object root) {
      ctx = JXPathContext.newContext(root);
      ctx.setFunctions(functionLib);
      ctx.setVariables(getVariables());

      registerFunctions("g", JXPathFunctionsAdapter.class);
   }

   public void registerFunctions(String prefix, Object functions) {
      registerFunctions(prefix, functions.getClass());
   }

   public void registerFunctions(String prefix, Class functionClass) {
      functionLib.addFunctions(new ClassFunctions(functionClass, prefix));
   }

   public void declare(String name, Object value) {
      declare(name, value, null);
   }

   public void declare(String name, Object value, Class type) {
      ctx.getVariables().declareVariable(name, value);
   }

   public Object lookup(String name) {
      return ctx.getVariables().getVariable(name);
   }

   public void undeclare(String name) {
      ctx.getVariables().undeclareVariable(name);
   }

   public Map getContextMap() {
      return ((VariablesImpl)ctx.getVariables()).getVariablesMap();
   }

   public Object eval(ScriptExpression expr) {
      return ctx.getValue(expr.getExpressionString());
   }

   public Object eval(String expr) {
      return eval(new JXPathExpression(expr));
   }

   protected Variables getVariables() {
      return new VariablesImpl();
   }
}