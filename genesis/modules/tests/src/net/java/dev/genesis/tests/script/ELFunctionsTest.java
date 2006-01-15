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

import net.java.dev.genesis.script.ScriptFactory;
import net.java.dev.genesis.script.el.ELScriptFactory;

public class ELFunctionsTest extends ScriptFunctionsTest {

   protected ScriptFactory newScriptFactory() {
      return new ELScriptFactory();
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "${g:" + methodName + '(' + expression + ")}";
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b);
   }
   
   
   protected String toScriptByte(byte b) {
      return "toByte(" + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return "toChar('" + String.valueOf(c) + "')";
   }

   protected String toScriptFloat(float f) {
      return "toFloat(" + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return "toInt(" + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return String.valueOf(l);
   }

   protected String toScriptShort(short s) {
      return "toShort(" + String.valueOf(s) + ')';
   }

   protected String toScriptField(String fieldName) {
      return "form." + fieldName;
   }

   protected String toScriptNot(String expression) {
      return "!(" + expression + ")";
   }

   protected String toScriptDouble(double d) {
      return String.valueOf(d);
   }

   protected String toScriptString(String string) {
      return '"' + string + '"';
   }

}