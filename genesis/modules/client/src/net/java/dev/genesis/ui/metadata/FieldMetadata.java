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


import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.resolvers.EmptyResolver;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.jxpath.CompiledExpression;

public class FieldMetadata {
   private final String fieldName;
   private final Class fieldClass;
   
   private Converter converter;

   private CompiledExpression enabledCondition;
   private CompiledExpression clearOnCondition;
   private CompiledExpression visibleCondition;
   private boolean displayOnly;
   private EqualityComparator equalityComparator;
   private EmptyResolver emptyResolver;
   private Object emptyValue;
   
   public FieldMetadata(String fieldName, Class fieldClass){
      this.fieldName = fieldName;
      this.fieldClass = fieldClass;
   }

   public String getFieldName() {
      return fieldName;
   }

   public CompiledExpression getClearOnCondition() {
      return clearOnCondition;
   }

   public void setClearOnCondition(CompiledExpression clearOnCondition) {
      this.clearOnCondition = clearOnCondition;
   }

   public boolean isDisplayOnly() {
      return displayOnly;
   }

   public void setDisplayOnly(boolean displayOnly) {
      this.displayOnly = displayOnly;
   }

   public EmptyResolver getEmptyResolver() {
      return emptyResolver;
   }

   public void setEmptyResolver(EmptyResolver emptyResolver) {
      this.emptyResolver = emptyResolver;
   }

   public CompiledExpression getEnabledCondition() {
      return enabledCondition;
   }

   public void setEnabledCondition(CompiledExpression enabledCondition) {
      this.enabledCondition = enabledCondition;
   }

   public CompiledExpression getVisibleCondition() {
      return visibleCondition;
   }

   public void setVisibleCondition(CompiledExpression visibleCondition) {
      this.visibleCondition = visibleCondition;
   }
   public EqualityComparator getEqualityComparator() {
      return equalityComparator;
   }

   public void setEqualityComparator(EqualityComparator equalityComparator) {
      this.equalityComparator = equalityComparator;
   }

   public Converter getConverter() {
      return converter;
   }

   public void setConverter(Converter converter) {
      this.converter = converter;
   }

   public Class getFieldClass() {
      return fieldClass;
   }
   
   public Object getEmptyValue() {
      return emptyValue;
   }

   public void setEmptyValue(Object emptyValue) {
      this.emptyValue = emptyValue;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(".");
      buffer.append(fieldName);
      buffer.append(" = {\n\t\tenabledCondition = ");
      buffer.append(enabledCondition);
      buffer.append("\n\t\tclearOnCondition = ");
      buffer.append(clearOnCondition);
      buffer.append("\n\t\tdisplayOnly = ");
      buffer.append(displayOnly);
      buffer.append("\n\t\tequalityComparator = ");
      buffer.append(equalityComparator);
      buffer.append("\n\t\temptyResolver = ");
      buffer.append(emptyResolver);
      buffer.append("\n\t\tconverter = ");
      buffer.append(converter);
      buffer.append("\n\t\tfieldClass = ");
      buffer.append(fieldClass);
      buffer.append("\n\t\temptyValue = ");
      buffer.append(emptyValue);
      buffer.append("\n\t}");
      return buffer.toString();
   }
}