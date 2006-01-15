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

import java.beans.PropertyDescriptor;

import net.java.dev.genesis.script.ScriptFactory;
import net.java.dev.genesis.script.bsf.BSFScriptFactory;

import org.apache.commons.beanutils.PropertyUtils;

public class JavascriptFunctionsTest extends ScriptFunctionsTest {
   protected ScriptFactory newScriptFactory() {
      BSFScriptFactory factory = new BSFScriptFactory();
      factory.setLang("javascript");
      return factory;
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "genesis." + methodName + '(' + expression + ')';
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b);
   }

   protected String toScriptByte(byte b) {
      return "Byte(" + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return "Character('" + c + "')";
   }

   protected String toScriptFloat(float f) {
      return "Float(" + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return "Integer(" + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return "Long(" + String.valueOf(l) + ')';
   }

   protected String toScriptShort(short s) {
      return "Short(" + String.valueOf(s) + ')';
   }

   protected String toScriptField(String fieldName) {
      String[] fields = fieldName.split("[.]");
      StringBuffer buffer = new StringBuffer("form");
      PropertyDescriptor lastDesc = null;
      for (int j = 0; j < fields.length; j++) {
         buffer.append('.');
         lastDesc = getFieldDesc(lastDesc == null ? root.getClass() : lastDesc
               .getPropertyType(), fields[j]);
         buffer.append(lastDesc.getReadMethod().getName());
         buffer.append("()");
      }

      return buffer.toString();
   }

   protected PropertyDescriptor getFieldDesc(Class beanClass, String field) {
      PropertyDescriptor[] descs = PropertyUtils
            .getPropertyDescriptors(beanClass);
      for (int i = 0; i < descs.length; i++) {
         if (descs[i].getName().equals(field)) {
            return descs[i];
         }
      }
      throw new IllegalArgumentException("Field not found: " + field);
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
