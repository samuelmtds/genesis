/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.java.dev.genesis.reflection.FieldEntry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.CompiledExpression;

public class DataProviderMetadata {
   private final String name;
   private String widgetName;
   private FieldEntry objectField;
   private FieldEntry indexField;
   private boolean callOnInit;
   private CompiledExpression clearOnCondition;

   protected DataProviderMetadata(Method method) {
      this.name = method.getName();

      if ((!method.getReturnType().isArray() || method.getReturnType()
            .getComponentType().isPrimitive())
            && !List.class.isAssignableFrom(method.getReturnType())) {
         throw new IllegalArgumentException("Method '" + name + "' is a " +
               "DataProvider and does not have an Object array or java.util.List as " +
               "its return type");
      }
   }

   public String getName() {
      return this.name;
   }

   public FieldEntry getObjectField() {
      return objectField;
   }

   public void setObjectField(FieldEntry objectField) {
      this.objectField = objectField;
   }

   public FieldEntry getIndexField() {
      return indexField;
   }

   public String getWidgetName() {
      return widgetName;
   }

   public void setWidgetName(String widgetName) {
      this.widgetName = widgetName;
   }

   public void setIndexField(FieldEntry indexField) {
      this.indexField = indexField;
   }

   public boolean isCallOnInit() {
      return callOnInit;
   }

   public void setCallOnInit(boolean callOnInit) {
      this.callOnInit = callOnInit;
   }

   public CompiledExpression getClearOnCondition() {
      return clearOnCondition;
   }

   public void setClearOnCondition(CompiledExpression clearOnCondition) {
      this.clearOnCondition = clearOnCondition;
   }

   public int[] getSelectedIndexes(final Object indexes) {
      if (indexes == null) {
         return new int[0];
      }

      if (!indexField.isMultiple()) {
         return new int[] {((Integer)indexes).intValue()};
      }

      if (indexField.isPrimitiveArray()) {
         final int[] intIndexes = (int[]) indexes;
         Arrays.sort(intIndexes);

         return intIndexes;
      }

      if (indexField.isArray()) {
         final Integer[] integerIndexes = (Integer[])indexes;
         final int[] intIndexes = new int[integerIndexes.length];

         for (int i = 0; i < intIndexes.length; i++) {
            intIndexes[i] = integerIndexes[i].intValue();
         }

         Arrays.sort(intIndexes);

         return intIndexes;
      } 

      final Collection colIndexes = (Collection)indexes;
      final int[] intIndexes = new int[colIndexes.size()];
      int i = 0;

      for (Iterator iter = colIndexes.iterator(); iter.hasNext(); i++) {
         intIndexes[i] = ((Integer)iter.next()).intValue();
      }

      Arrays.sort(intIndexes);

      return intIndexes;
   }

   public void resetSelectedFields(final Object target) throws Exception {
      if (objectField != null) {
         PropertyUtils.setProperty(target, objectField.getFieldName(), null);
      }

      if (indexField != null) {
         final Object value = indexField.isPrimitive() ? (Object)new Integer(0)
               : (indexField.isPrimitiveArray() ? new int[0] : null);
         PropertyUtils.setProperty(target, indexField.getFieldName(), value);
      }
   }

   public Object populateSelectedFields(final Object target, final List objectList, final int[] selectedIndexes) throws Exception {
      Object value = null;

      if (objectField != null) {
         value = populateObjectField(target, objectList, selectedIndexes);
      }

      if (indexField != null) {
         populateIndexField(target, selectedIndexes);
      }
      
      return value;
   }

   private Object populateObjectField(final Object target, final List objectList, final int[] selectedIndexes) throws Exception {
      Object value = null;

      if (objectField.isMultiple()) {
         final Object[] values = new Object[selectedIndexes.length];

         for (int i = 0; i < selectedIndexes.length; i++) {
            values[i] = objectList.get(selectedIndexes[i]);
         }

         value = objectField.isArray() ? (Object)values : Arrays.asList(values);
      } else {
         value = selectedIndexes.length == 0 ? null : objectList
               .get(selectedIndexes[0]);      
      }

      PropertyUtils.setProperty(target, objectField.getFieldName(), value);

      return value;
   }

   private void populateIndexField(final Object target, final int[] selectedIndexes) throws Exception {
      if(indexField.isPrimitiveArray()) {
         PropertyUtils.setProperty(target, indexField.getFieldName(), selectedIndexes);

      } else if (indexField.isMultiple()) {
         final Integer[] integerArray = new Integer[selectedIndexes.length];

         for (int i = 0; i < integerArray.length; i++) {
            integerArray[i] = new Integer(selectedIndexes[i]);
         }

         final Object value = indexField.isArray() ? (Object)integerArray : Arrays.asList(integerArray);
         PropertyUtils.setProperty(target, indexField.getFieldName(), value);

      } else {
         PropertyUtils.setProperty(target, indexField.getFieldName(),
               selectedIndexes.length == 0 ?
                     (indexField.isPrimitive() ? new Integer(0) : null) :
                     new Integer(selectedIndexes[0]));
      }
   }
}