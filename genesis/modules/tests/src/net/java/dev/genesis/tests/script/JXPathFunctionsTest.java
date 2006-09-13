/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.script;

import net.java.dev.genesis.commons.jxpath.JXPathContextFactory;
import net.java.dev.genesis.script.ScriptFactory;
import net.java.dev.genesis.script.jxpath.JXPathScriptContext;
import net.java.dev.genesis.script.jxpath.JXPathScriptFactory;

public class JXPathFunctionsTest extends ScriptFunctionsTest {
   static {
      System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
            JXPathContextFactory.class.getName());
   }

   protected ScriptFactory newScriptFactory() {
      return new JXPathScriptFactory();
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "g:" + methodName + '(' + expression + ')';
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b) + "()";
   }

   protected String toScriptByte(byte b) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toByte("
            + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toChar('"
            + String.valueOf(c) + "')";
   }

   protected String toScriptFloat(float f) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toFloat("
            + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toInt("
            + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toLong("
            + String.valueOf(l) + ')';
   }

   protected String toScriptShort(short s) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toShort("
            + String.valueOf(s) + ')';
   }

   protected String toScriptField(String fieldName) {
      return fieldName.replace('.', '/');
   }

   protected String toScriptNot(String expression) {
      return "not(" + expression + ")";
   }

   protected String toScriptDouble(double d) {
      return String.valueOf(d);
   }

   protected String toScriptString(String string) {
      return '\'' + string + '\'';
   }
}
