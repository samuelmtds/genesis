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
package net.java.dev.genesis.reflection;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;

public class FieldEntry implements Serializable {
   private final String fieldName;
   private final String fieldTypeName;
   private final boolean isArray;
   private final boolean isCollection;

   public FieldEntry(final Field field) {
      this.fieldName = field.getName();
      this.fieldTypeName = field.getType().getName();
      this.isArray = field.getType().isArray();
      this.isCollection = Collection.class.isAssignableFrom(field
            .getType());
   }

   public String getFieldTypeName() {
      return fieldTypeName;
   }

   public String getFieldName() {
      return fieldName;
   }

   public boolean isArray() {
      return isArray;
   }

   public boolean isCollection() {
      return isCollection;
   }
   
   public boolean isMultiple(){
      return isArray() || isCollection();
   }

   public boolean equals(Object o) {
      final FieldEntry that = (FieldEntry) o;
      return that.fieldName.equals(this.fieldName)
            && that.fieldTypeName.equals(this.fieldTypeName)
            && that.isArray == this.isArray
            && that.isCollection == this.isCollection;
   }

   public int hashCode() {
      return fieldName.hashCode();
   }

   public String toString() {
      final StringBuffer buffer = new StringBuffer();
      buffer.append(fieldName);
      buffer.append("[");
      buffer.append(fieldTypeName);
      buffer.append("]");
      return buffer.toString();
   }
}