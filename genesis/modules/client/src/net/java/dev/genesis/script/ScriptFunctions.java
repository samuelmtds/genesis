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
package net.java.dev.genesis.script;

import net.java.dev.genesis.equality.EqualityComparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScriptFunctions {
   private static Log log = LogFactory.getLog(ScriptFunctions.class);

   public static boolean hasChanged(ScriptContext ctx, ScriptValue scriptValue) {
      final boolean result = ScriptUtils.getChangedMap(ctx).containsKey(
            scriptValue.getFieldName());

      if (log.isDebugEnabled()) {
         log.debug("Property '" + scriptValue.getFieldName()
               + (result ? "' changed" : "' didn´t change"));
      }

      return result;
   }

   public static boolean isEmpty(ScriptContext ctx, ScriptValue scriptValue)
         throws Exception {
      return ScriptUtils.getEmptyResolver(ctx, scriptValue).isEmpty(
            scriptValue.getValue());
   }

   public static boolean equals(ScriptContext ctx, ScriptValue scriptValue1,
         ScriptValue scriptValue2) throws Exception {

      final EqualityComparator comp1 = ScriptUtils.getEqualityComparator(ctx,
            scriptValue1);
      final EqualityComparator comp2 = ScriptUtils.getEqualityComparator(ctx,
            scriptValue2);

      final Object value1 = scriptValue1.getValue();
      final Object value2 = scriptValue2.getValue();

      if (comp1 == comp2) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2
                  + "' with same comparator " + comp1);
         }
         return comp1.equals(value1, value2);
      } else if (value1 == null) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2
                  + "'");
         }
         return value2 == null;
      } else if (value1.getClass().equals(value2.getClass())
            || value1.getClass().isAssignableFrom(value2.getClass())) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2
                  + "' with first comparator " + comp1);
         }
         return comp1.equals(value1, value2);
      } else if (value2.getClass().isAssignableFrom(value1.getClass())) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2
                  + "' with second comparator " + comp2);
         }
         return comp2.equals(value2, value1);
      }
      if (log.isDebugEnabled()) {
         log.debug("'" + value1 + "' and '" + value2
               + "' have different classes");
      }
      return false;
   }
}