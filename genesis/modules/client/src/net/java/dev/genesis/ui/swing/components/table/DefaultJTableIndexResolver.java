/*
 * The Genesis Project
 * Copyright (C) 2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.swing.components.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import net.java.dev.genesis.registry.Registry;
import net.java.dev.genesis.util.Bundle;

public class DefaultJTableIndexResolver implements JTableIndexResolver {
   protected static class JTableIndexResolverInfo {
      private Method convertRowIndexToModel;
      private Method convertRowIndexToView;

      JTableIndexResolverInfo(final Class clazz) {
         try {
            convertRowIndexToModel = clazz.getMethod(
                  "convertRowIndexToModel", new Class[] {int.class}); // NOI18N
            convertRowIndexToView = clazz.getMethod(
                  "convertRowIndexToView", new Class[] {int.class}); // NOI18N
         } catch (SecurityException se) {
            IllegalStateException ise = new IllegalStateException(
                  Bundle.getMessage(getClass(),
                  "COULD_NOT_LOAD_CONVERTROWINDEXTO_METHODS")); // NOI18N
            ise.initCause(se);
            throw ise;
         } catch (NoSuchMethodException nsme) {
            // Expected
         }
      }

      public boolean needsConversion() {
         return convertRowIndexToModel != null;
      }

      public int convertRowIndexToModel(final JComponent component,
            final int index) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
         return ((Integer)convertRowIndexToModel.invoke(component,
               new Object[] {new Integer(index)})).intValue();
      }

      public int convertRowIndexToView(final JComponent component,
            final int index) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
         return ((Integer)convertRowIndexToView.invoke(component,
               new Object[] {new Integer(index)})).intValue();
      }
   }

   private static class JTableIndexResolverInfoRegistry {
      private final Registry registry = new Registry();

      public void deregister() {
         registry.deregister();
      }

      public void deregister(Class clazz) {
         registry.deregister(clazz);
      }

      public JTableIndexResolverInfo register(Class clazz,
            JTableIndexResolverInfo info) {
         return (JTableIndexResolverInfo)registry.register(clazz, info);
      }

      public JTableIndexResolverInfo get(Class clazz) {
         return (JTableIndexResolverInfo)registry.get(clazz);
      }

      public JTableIndexResolverInfo get(Class clazz, boolean superClass) {
         return (JTableIndexResolverInfo)registry.get(clazz, superClass);
      }

      public JTableIndexResolverInfo get(Object o) {
         return (JTableIndexResolverInfo)registry.get(o);
      }
   }
   private final Map resolverInfoMap = new WeakHashMap();
   private final JTableIndexResolverInfoRegistry resolverInfoRegistry =
         new JTableIndexResolverInfoRegistry();

   protected JTableIndexResolverInfo initialize(final Class clazz) {
      JTableIndexResolverInfo info = resolverInfoMap.get(clazz) != null ? (JTableIndexResolverInfo)resolverInfoMap.get(clazz)
            : resolverInfoRegistry.get(clazz, true);
      if (info == null) {
         info = new JTableIndexResolverInfo(clazz);
         if (info.needsConversion()) {
            resolverInfoRegistry.register(clazz, info);
         } else {
            resolverInfoMap.put(clazz, info);
         }
      }

      return info;
   }

   public boolean needsConversion(JComponent component) {
      return initialize(component.getClass()).needsConversion();
   }

   public int convertRowIndexToModel(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return initialize(component.getClass()).convertRowIndexToModel(component,
            index);
   }

   public int convertRowIndexToView(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return initialize(component.getClass()).convertRowIndexToView(component,
            index);
   }
}
