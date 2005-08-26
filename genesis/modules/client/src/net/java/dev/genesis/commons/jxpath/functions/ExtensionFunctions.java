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
package net.java.dev.genesis.commons.jxpath.functions;

import java.util.Map;

import net.java.dev.genesis.commons.jxpath.GenesisNodeSet;
import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormState;
import net.java.dev.genesis.ui.metadata.FormMetadata;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExtensionFunctions {
   private static Log log = LogFactory.getLog(ExtensionFunctions.class);

   private static Object getValue(final ExpressionContext ctx,
         final String propertyName) throws Exception {

      return PropertyUtils.getProperty(ctx.getJXPathContext().getContextBean(),
            propertyName);
   }

   private static Object getValue(final ExpressionContext ctx, final Object obj)
         throws Exception {
      return obj instanceof GenesisNodeSet ? getValue(ctx,
            getSimplePropertyName((GenesisNodeSet) obj)) : obj;
   }

   private static String getSimplePropertyName(final GenesisNodeSet nodeSet) {
      final String path = nodeSet.getPath();
      int index;

      return (index = path.indexOf('/')) > 0 ? path.substring(0, index) : path;
   }

   private static EqualityComparator getEqualityComparator(
         final ExpressionContext ctx, final Object obj) {

      if (obj instanceof GenesisNodeSet) {
         final FormMetadata meta = (FormMetadata) ctx.getJXPathContext()
               .getVariables().getVariable(FormController.FORM_METADATA_KEY);
         return meta.getFieldMetadata(
               getSimplePropertyName((GenesisNodeSet) obj))
               .getEqualityComparator();
      }

      return EqualityComparatorRegistry.getInstance()
            .getDefaultEqualityComparatorFor(obj.getClass());
   }

   public static boolean hasChanged(final ExpressionContext context,
         final GenesisNodeSet nodeSet) {

      final Map changedMap = ((FormState)context.getJXPathContext()
            .getVariables().getVariable(FormController.CURRENT_STATE_KEY))
            .getChangedMap();
      final String propertyName = getSimplePropertyName(nodeSet);
      final boolean result = changedMap.containsKey(propertyName);

      if (log.isDebugEnabled()) {
         log.debug("Property '" + propertyName
               + (result ? "' changed" : "' didn´t change"));
      }

      return result;
   }

   public static boolean hasNotChanged(final ExpressionContext context,
         final GenesisNodeSet nodeSet) {
      return !hasChanged(context, nodeSet);
   }

   public static boolean isEmpty(final ExpressionContext ctx, final Object obj)
         throws Exception {

      if (!(obj instanceof GenesisNodeSet)) {
         return EmptyResolverRegistry.getInstance().getDefaultEmptyResolverFor(
               obj.getClass()).isEmpty(obj);
      }

      final GenesisNodeSet nodeSet = (GenesisNodeSet) obj;
      final FormMetadata formMeta = (FormMetadata) ctx.getJXPathContext()
            .getVariables().getVariable(FormController.FORM_METADATA_KEY);
      final String propertyName = getSimplePropertyName(nodeSet);

      return formMeta.getFieldMetadata(getSimplePropertyName(nodeSet))
            .getEmptyResolver().isEmpty(getValue(ctx, propertyName));
   }

   public static boolean isNotEmpty(final ExpressionContext ctx, final Object obj)
   		throws Exception {
      return !isEmpty(ctx, obj);
   }

   public static boolean equals(final ExpressionContext ctx, final Object arg1,
         final Object arg2) throws Exception {

      final EqualityComparator comp1 = getEqualityComparator(ctx, arg1);
      final EqualityComparator comp2 = getEqualityComparator(ctx, arg2);

      final Object value1 = getValue(ctx, arg1);
      final Object value2 = getValue(ctx, arg2);

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
   
   public static boolean notEquals(final ExpressionContext ctx, final Object arg1,
         final Object arg2) throws Exception {
      return !equals(ctx, arg1, arg2);
   }
}