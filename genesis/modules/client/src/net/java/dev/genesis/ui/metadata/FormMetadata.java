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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.reflection.MethodEntry;

import org.apache.commons.jxpath.CompiledExpression;

public class FormMetadata {
   private final Class formClass;

   private final Map namedConditions;
   private final Map fieldMetadatas;
   private final Map actionMetadatas;
   private final Map dataProviderMetadatas;

   public FormMetadata(final Class formClass) {
      this.formClass = formClass;
      this.namedConditions = new HashMap();
      this.fieldMetadatas = new HashMap();
      this.actionMetadatas = new HashMap();
      this.dataProviderMetadatas = new HashMap();
   }

   public Class getFormClass() {
      return formClass;
   }

   public Map getFieldMetadatas() {
      return new HashMap(fieldMetadatas);
   }

   public Map getNamedConditions() {
      return new HashMap(namedConditions);
   }

   public Map getActionMetadatas(){
      return new HashMap(actionMetadatas);
   }
   
   public Map getDataProviderMetadatas(){
      return new HashMap(dataProviderMetadatas);
   }

   public CompiledExpression getNamedCondition(final String namedConditionName) {
      return (CompiledExpression) namedConditions.get(namedConditionName);
   }

   public FieldMetadata getFieldMetadata(final String fieldName) {
      return (FieldMetadata) fieldMetadatas.get(fieldName);
   }
   
   public ActionMetadata getActionMetadata(final Method method) {
      return (ActionMetadata) actionMetadatas.get(new MethodEntry(method));
   }
   
   public ActionMetadata getActionMetadata(final String methodName) {
      return (ActionMetadata) actionMetadatas.get(new MethodEntry(methodName, new String[0]));
   }
   
   public DataProviderMetadata getDataProviderMetadata(final Method method) {
      return (DataProviderMetadata) dataProviderMetadatas.get(new MethodEntry(method));
   }

   public void addNamedCondition(final String key, final CompiledExpression value) {
      namedConditions.put(key, value);
   }

   public void addFieldMetadata(final String fieldName,
         final FieldMetadata fieldMetadata) {
      fieldMetadatas.put(fieldName, fieldMetadata);
   }
   
   public void addActionMetadata(final Method method, final ActionMetadata actionMetadata) {
      actionMetadatas.put(new MethodEntry(method), actionMetadata);
   }
   
   public void addDataProviderMetadata(final Method method, final DataProviderMetadata dataProviderMetadata) {
      dataProviderMetadatas.put(new MethodEntry(method), dataProviderMetadata);
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
      
      for (Iterator iter = actionMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t");
         buffer.append(iter.next());
         buffer.append("\n");
      }
      
      for (Iterator iter = dataProviderMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t");
         buffer.append(iter.next());
         buffer.append("\n");
      }

      buffer.append("}");

      return buffer.toString();
   }
}