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
package net.java.dev.genesis.ui.metadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.java.dev.genesis.reflection.FieldEntry;

import org.apache.commons.beanutils.PropertyUtils;

public class DataProviderMetadata extends MethodMetadata {
   private FieldEntry objectField;
   private FieldEntry indexField;
   private boolean callOnInit;

   public DataProviderMetadata(final Method method) {
      super(method);
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

   public void setIndexField(FieldEntry indexField) {
      this.indexField = indexField;
   }

   public boolean isCallOnInit() {
      return callOnInit;
   }

   public void setCallOnInit(boolean callOnInit) {
      this.callOnInit = callOnInit;
   }

   public boolean isProvider(){
      return getObjectField() != null || getIndexField() != null;
   }
   
   public void resetSelectedFields(final Object target) throws Exception {
      if (objectField != null) {
         PropertyUtils.setProperty(target, objectField.getFieldName(), null);
      }
      if (indexField != null) {
         PropertyUtils.setProperty(target, indexField.getFieldName(), null);
      }
   }

   public void populateSelectedFields(final Object target, final List objectList, final int[] selectedIndexes) throws Exception {
      if (objectField != null) {
         populateObjectField(target, objectList, selectedIndexes);
      }
      if (indexField != null) {
         populateIndexField(target, selectedIndexes);
      }
   }

   private void populateObjectField(final Object target, final List objectList, final int[] selectedIndexes) throws Exception {
      final List selectedObjects = new ArrayList(selectedIndexes.length);
      for (int i = 0; i < selectedIndexes.length; i++) {
         selectedObjects.add(objectList.get(selectedIndexes[i]));
      }
      setProperty(target, objectField, selectedObjects);
   }

   private void populateIndexField(final Object target, final int[] selectedIndexes) throws Exception {
      final List list = new ArrayList(selectedIndexes.length);
      // Wraps the int array
      for (int i = 0; i < selectedIndexes.length; i++) {
         list.add(new Integer(selectedIndexes[i]));
      }
      setProperty(target, objectField, list);
   }

   private void setProperty(final Object target, final FieldEntry field, final List values) throws Exception {
      if (field.isMultiple()) {
         if(field.isCollection()){
            PropertyUtils.setProperty(target, field.getFieldName(), values.isEmpty() ? null : values);
         } else {
            PropertyUtils.setProperty(target, field.getFieldName(), values.isEmpty() ? null : values.toArray());
         }
      } else {
         PropertyUtils.setProperty(target, field.getFieldName(), values.isEmpty() ? null : values.get(0));
      }
   }
}