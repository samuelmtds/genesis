/*
 * The Genesis Project
 * Copyright (C) 2004  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.Method;

import org.apache.commons.jxpath.CompiledExpression;

import net.java.dev.genesis.reflection.MethodEntry;

public class ActionMetadata extends MemberMetadata {

   private final MethodEntry methodEntry;
   private boolean validateBefore;
   private CompiledExpression callCondition;

   public ActionMetadata(Method method) {
      this.methodEntry = new MethodEntry(method);
   }

   public MethodEntry getMethodEntry() {
      return methodEntry;
   }

   public boolean isValidateBefore() {
      return validateBefore;
   }

   public void setValidateBefore(boolean validateBefore) {
      this.validateBefore = validateBefore;
   }

   public CompiledExpression getCallCondition() {
      return callCondition;
   }

   public void setCallCondition(CompiledExpression callWhen) {
      this.callCondition = callWhen;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(".");
      buffer.append(methodEntry.toString());
      buffer.append(" = {\n\t\tenabledCondition = ");
      buffer.append(getEnabledCondition());
      buffer.append(" = {\n\t\tvisibleCondition = ");
      buffer.append(getVisibleCondition());
      buffer.append(" = {\n\t\tvalidateBefore = ");
      buffer.append(isValidateBefore());
      buffer.append("\n\t}");
      return buffer.toString();
   }
}