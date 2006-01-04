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
package net.java.dev.genesis.script.jxpath;

import net.java.dev.genesis.commons.jxpath.GenesisNodeSet;
import net.java.dev.genesis.script.ScriptFunctions;
import net.java.dev.genesis.script.ScriptUtils;
import net.java.dev.genesis.script.ScriptValue;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;

public class JXPathFunctionsAdapter {
   private static Object getValue(final ExpressionContext ctx,
         final String propertyName) throws Exception {
      int indexOfDot = -1;

      while ((indexOfDot = propertyName.indexOf('.', indexOfDot + 1)) != -1) {
         if (PropertyUtils.getProperty(ctx.getJXPathContext().getContextBean(),
            propertyName.substring(0, indexOfDot)) == null) {
            return null;
         }
      }

      return PropertyUtils.getProperty(ctx.getJXPathContext().getContextBean(),
            propertyName);
   }

   private static String getSimplePropertyName(final GenesisNodeSet nodeSet) {
      return nodeSet.getPath().replace('/', '.');
   }

   private static ScriptValue getScriptValue(final ExpressionContext ctx,
         final Object obj) throws Exception {
      if (obj instanceof GenesisNodeSet) {
         return getScriptValue(ctx, (GenesisNodeSet)obj);
      }

      return new ScriptValue(null, obj);
   }

   private static ScriptValue getScriptValue(final ExpressionContext ctx,
         final GenesisNodeSet nodeSet) throws Exception {
      return new ScriptValue(getSimplePropertyName(nodeSet), getValue(ctx,
            getSimplePropertyName(nodeSet)));
   }

   public static boolean hasChanged(final ExpressionContext ctx,
         final GenesisNodeSet nodeSet) throws Exception {
      return ScriptFunctions.hasChanged(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, nodeSet));
   }

   public static boolean hasNotChanged(final ExpressionContext context,
         final GenesisNodeSet nodeSet) throws Exception {
      return !hasChanged(context, nodeSet);
   }

   public static boolean isEmpty(final ExpressionContext ctx, final Object obj)
         throws Exception {
      return ScriptFunctions.isEmpty(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, obj));
   }

   public static boolean isNotEmpty(final ExpressionContext ctx,
         final Object obj) throws Exception {
      return !isEmpty(ctx, obj);
   }

   public static boolean equals(final ExpressionContext ctx, final Object arg1,
         final Object arg2) throws Exception {
      return ScriptFunctions.equals(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, arg1), getScriptValue(ctx, arg2));
   }

   public static boolean notEquals(final ExpressionContext ctx,
         final Object arg1, final Object arg2) throws Exception {
      return !equals(ctx, arg1, arg2);
   }
}