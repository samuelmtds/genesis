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
import javax.swing.JComponent;
import javax.swing.JTable;

public class DefaultJTableIndexResolver implements JTableIndexResolver {
   private volatile boolean initialized = false;
   private Method convertRowIndexToModel;
   private Method convertRowIndexToView;
   private Class clazz;

   public DefaultJTableIndexResolver() {
      this(JTable.class);
   }

   public DefaultJTableIndexResolver(Class clazz) {
      this.clazz = clazz;
   }

   public boolean needsConversion() {
      if (initialized) {
         return convertRowIndexToModel != null;
      }

      try {
         convertRowIndexToModel = clazz.getMethod(
               "convertRowIndexToModel", new Class[] {int.class});
         convertRowIndexToView = clazz.getMethod(
               "convertRowIndexToView", new Class[] {int.class});
      } catch (SecurityException se) {
         IllegalStateException ise = new IllegalStateException(
               "Could not load convertRowIndexTo* methods");
         ise.initCause(se);
         throw ise;
      } catch (NoSuchMethodException nsme) {
      // Expected
      } finally {
         initialized = true;
      }

      return convertRowIndexToModel != null;
   }

   public int convertRowIndexToModel(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return ((Integer)convertRowIndexToModel.invoke(component,
            new Object[] {new Integer(index)})).intValue();
   }

   public int convertRowIndexToView(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return ((Integer)convertRowIndexToView.invoke(component,
            new Object[] {new Integer(index)})).intValue();
   }
}
