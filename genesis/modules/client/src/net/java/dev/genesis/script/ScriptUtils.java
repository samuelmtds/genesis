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
package net.java.dev.genesis.script;

import java.util.Map;

import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormState;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

import org.apache.commons.beanutils.PropertyUtils;

public class ScriptUtils {
   private static final ThreadLocal current = new ThreadLocal();

   public static ScriptContext getCurrentContext() {
      return (ScriptContext)current.get();
   }

   public static void enter(ScriptContext ctx) {
      current.set(ctx);
   }

   public static void exit() {
      current.set(null);
   }

   public static Object getPropertyValue(String objKey, String propertyName) {
      final Object bean = getCurrentContext().lookup(objKey);
      if (bean == null) {
         throw new IllegalArgumentException("Bean called '" + objKey
               + "' not found");
      }
      try {
         return PropertyUtils.getProperty(bean, propertyName);
      } catch (Exception e) {
         throw new IllegalArgumentException("Bean doesn't have the property '"
               + propertyName + "'");
      }
   }

   public static ScriptValue getScriptValue(Object obj) {
      if (obj instanceof String) {
         String[] values = obj.toString().split(":");
         if (values.length != 2) {
            throw new IllegalArgumentException("Malformed expression '" + obj
                  + "'");
         }
         return new ScriptValue(values[1], getPropertyValue(values[0],
               values[1]));
      }
      return new ScriptValue(null, obj);
   }

   public static FormMetadata getFormMetadata(ScriptContext ctx) {
      return (FormMetadata)ctx.lookup(FormController.FORM_METADATA_KEY);
   }

   public static Map getChangedMap(ScriptContext ctx) {
      return ((FormState)ctx.lookup(FormController.CURRENT_STATE_KEY))
            .getChangedMap();
   }

   public static EmptyResolver getEmptyResolver(ScriptContext ctx,
         ScriptValue scriptValue) {
      if (scriptValue.getFieldName() == null) {
         return EmptyResolverRegistry.getInstance().getDefaultEmptyResolverFor(
               scriptValue.getValue() == null ? Object.class : scriptValue
                     .getValue().getClass());
      }

      final FormMetadata meta = getFormMetadata(ctx);
      final FieldMetadata fieldMetadata = meta.getFieldMetadata(scriptValue.getFieldName());

      if (fieldMetadata != null) {
         return fieldMetadata.getEmptyResolver();
      }

      return (EmptyResolver) EmptyResolverRegistry.getInstance().get(
            scriptValue.getValue());
   }

   public static EqualityComparator getEqualityComparator(ScriptContext ctx,
         ScriptValue scriptValue) {
      if (scriptValue.getFieldName() == null) {
         return EqualityComparatorRegistry.getInstance()
               .getDefaultEqualityComparatorFor(
                     scriptValue.getValue() == null ? Object.class
                           : scriptValue.getValue().getClass());
      }

      final FormMetadata meta = getFormMetadata(ctx);
      final FieldMetadata fieldMetadata = meta.getFieldMetadata(scriptValue.getFieldName());

      if (fieldMetadata != null) {
         return fieldMetadata.getEqualityComparator();
      }

      return EqualityComparatorRegistry.getInstance()
            .getDefaultEqualityComparatorFor(scriptValue.getValue().getClass());
   }
}