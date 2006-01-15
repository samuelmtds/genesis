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
package net.java.dev.genesis.script;

import java.util.ArrayList;
import java.util.List;

public class ScriptableObjectImpl implements ScriptableObject {
   private static final ScriptValue[] EMPTY_ARRAY =
      new ScriptValue[] { new ScriptValue(null, null), new ScriptValue(null,
            null) };
   private List info = new ArrayList(2);

   public void visitField(String fieldName, Object value, boolean isPrimitive) {
      // Max info size is 2 (needed in ScriptFunctions.equals(...) method)
      if (info.size() == 2) {
         info.set(0, info.get(1));
         info.set(1, new FieldInfo(fieldName, value, isPrimitive));
      } else {
         info.add(new FieldInfo(fieldName, value, isPrimitive));
      }
   }

   public ScriptValue getScriptValueFor(Object value) {
      if (info.isEmpty()) {
         return new ScriptValue(null, value);
      }

      FieldInfo lastInfo = (FieldInfo) info.get(info.size() - 1);

      if (match(lastInfo, value)) {
         clear();

         return lastInfo;
      }

      // nested properties. No field name.
      clear();

      return new ScriptValue(null, value);
   }

   public ScriptValue[] getScriptValuesFor(Object value1, Object value2) {
      if (info.isEmpty()) {
         return EMPTY_ARRAY;
      }

      if (info.size() == 1) {
         FieldInfo lastInfo = (FieldInfo) info.get(0);

         if (match(lastInfo, value1)) {
            clear();

            return new ScriptValue[] { lastInfo, new ScriptValue(null, value2) };
         }

         if (match(lastInfo, value2)) {
            clear();

            return new ScriptValue[] { new ScriptValue(null, value1), lastInfo };
         }

         // nested properties. No fields name.
         clear();

         return EMPTY_ARRAY;
      }

      FieldInfo lastInfo1 = (FieldInfo) info.get(info.size() - 2);
      FieldInfo lastInfo2 = (FieldInfo) info.get(info.size() - 1);

      if (match(lastInfo1, value1)) {
         if (match(lastInfo2, value2)) {
            clear();

            return new ScriptValue[] { lastInfo1, lastInfo2 };
         }

         // nested properties. No second field name.
         clear();

         return new ScriptValue[] { lastInfo1, new ScriptValue(null, value2) };
      }

      if (match(lastInfo2, value2)) {
         // nested properties. No first field name.
         clear();

         return new ScriptValue[] { new ScriptValue(null, value1), lastInfo2 };
      }

      // nested properties. No fields name.
      clear();

      return EMPTY_ARRAY;
   }

   private void clear() {
      info.clear();
   }

   public void afterEval() {
      info.clear();
   }

   public void beforeEval() {
      info.clear();
   }

   private boolean match(FieldInfo fieldInfo, Object value) {
      if (fieldInfo.isPrimitive) {
         // If primitive, the primitive value was wrapped, so,
         // we should use equals method
         return fieldInfo.getValue().equals(value);
      }

      return fieldInfo.getValue() == value;
   }

   private class FieldInfo extends ScriptValue {
      private boolean isPrimitive;

      public FieldInfo(String name, Object value, boolean isPrimitive) {
         super(name, value);
         this.isPrimitive = isPrimitive;
      }
   }
}
