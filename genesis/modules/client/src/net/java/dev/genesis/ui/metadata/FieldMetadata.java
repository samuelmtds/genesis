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

import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.registry.DefaultValueRegistry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.jxpath.CompiledExpression;

public class FieldMetadata extends MemberMetadata {
   private final String fieldName;
   private final Class fieldClass;
   private final boolean writeable;

   private Converter converter;
   private CompiledExpression clearOnCondition;
   private EqualityComparator equalityComparator;
   private EmptyResolver emptyResolver;
   private Object emptyValue;

   private boolean processed = false;

   public FieldMetadata(String fieldName, Class fieldClass, boolean writeable) {
      this.fieldName = fieldName;
      this.fieldClass = fieldClass;
      this.writeable = writeable;
   }

   public String getFieldName() {
      return fieldName;
   }

   public String getName() {
      return getFieldName();
   }

   public boolean isWriteable() {
      return writeable;
   }

   /**
    * @deprecated JXPath will be replaced with generic scripting support in the next major release
    */
   public CompiledExpression getClearOnCondition() {
      return clearOnCondition;
   }

   /**
    * @deprecated JXPath will be replaced with generic scripting support in the next major release
    */
   public void setClearOnCondition(CompiledExpression clearOnCondition) {
      this.clearOnCondition = clearOnCondition;
   }

   public EmptyResolver getEmptyResolver() {
      if (!processed) {
         internalProcess();
      }
      return emptyResolver;
   }

   public void setEmptyResolver(EmptyResolver emptyResolver) {
      this.emptyResolver = emptyResolver;
   }

   public EqualityComparator getEqualityComparator() {
      if (!processed) {
         internalProcess();
      }
      return equalityComparator;
   }

   public void setEqualityComparator(EqualityComparator equalityComparator) {
      this.equalityComparator = equalityComparator;
   }

   public Converter getConverter() {
      if (!processed) {
         internalProcess();
      }
      return converter;
   }

   public void setConverter(Converter converter) {
      this.converter = converter;
   }

   public Class getFieldClass() {
      return fieldClass;
   }

   public Object getEmptyValue() {
      if (!processed) {
         internalProcess();
      }
      return emptyValue;
   }

   public void setEmptyValue(Object emptyValue) {
      this.emptyValue = emptyValue;
   }

   private void internalProcess() {
      if (equalityComparator == null) {
         equalityComparator = EqualityComparatorRegistry.getInstance()
               .getDefaultEqualityComparatorFor(getFieldClass());
      }
      if (emptyResolver == null) {
         emptyResolver = EmptyResolverRegistry.getInstance()
               .getDefaultEmptyResolverFor(getFieldClass());
      }
      if (converter == null) {
         converter = ConvertUtils.lookup(getFieldClass());
         emptyValue = DefaultValueRegistry.getInstance().get(getFieldClass(),
               true);
      }
      processed = true;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(".");
      buffer.append(fieldName);
      buffer.append(" = {\n\t\tenabledCondition = ");
      buffer.append(getEnabledCondition());
      buffer.append(" = {\n\t\tvisibleCondition = ");
      buffer.append(getVisibleCondition());
      buffer.append("\n\t\tclearOnCondition = ");
      buffer.append(clearOnCondition);
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