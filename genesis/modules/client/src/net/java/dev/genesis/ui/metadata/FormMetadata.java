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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jxpath.CompiledExpression;

public class FormMetadata {
   private final Class formClass;

   private Map namedConditions;
   private Map fieldMetadatas;

   public FormMetadata(final Class formClass) {
      this(formClass, new HashMap(), new HashMap());
   }

   public FormMetadata(final Class formClass, final Map namedConditions,
         final Map fieldMetadatas) {
      this.formClass = formClass;
      this.namedConditions = namedConditions;
      this.fieldMetadatas = fieldMetadatas;
   }

   public Class getFormClass() {
      return formClass;
   }

   public Map getFieldMetadatas() {
      return fieldMetadatas;
   }

   public void setFieldMetadatas(Map fieldMetadatas) {
      this.fieldMetadatas = fieldMetadatas;
   }

   public Map getNamedConditions() {
      return namedConditions;
   }

   public void setNamedConditions(Map namedConditions) {
      this.namedConditions = namedConditions;
   }

   public CompiledExpression getNamedCondition(String namedConditionName) {
      return (CompiledExpression) namedConditions.get(namedConditionName);
   }

   public FieldMetadata getFieldMetadata(String fieldName) {
      return (FieldMetadata) fieldMetadatas.get(fieldName);
   }

   public void addNamedCondition(final Object key, final Object value) {
      namedConditions.put(key, value);
   }

   public void addFieldMetadata(String fieldName,
         final FieldMetadata fieldMetadata) {
      fieldMetadatas.put(fieldName, fieldMetadata);
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(" = {\n");

      for (Iterator iter = namedConditions.entrySet().iterator(); iter
            .hasNext();) {
         Map.Entry element = (Map.Entry) iter.next();
         buffer.append("\t");
         buffer.append(element.getKey());
         buffer.append("=");
         buffer.append(element.getValue());
         buffer.append("\n");
      }

      for (Iterator iter = fieldMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t");
         buffer.append(iter.next());
         buffer.append("\n");
      }

      buffer.append("}");

      return buffer.toString();
   }
}