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

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;

import net.java.dev.genesis.script.ScriptExpression;

import org.apache.commons.el.ExpressionEvaluatorImpl;

public class ELExpression extends ScriptExpression {
   public ELExpression(String expr, ExpressionEvaluator evaluator) {
      super(expr.startsWith("${") ? expr : "${" + expr + "}");
     
      try {
         // Compile and cache the expression
         ((ExpressionEvaluatorImpl)evaluator).parseExpressionString(getExpressionString());
      } catch (ELException e) {
         throw new RuntimeException(e);
      }
   }
}