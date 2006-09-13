/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.anttasks.aspectwerkz.annotation;

import java.util.StringTokenizer;

public class ClassnamePropertiesPreProcessor implements AnnotationPreprocessor {
   private final String propertiesFieldName;

   public ClassnamePropertiesPreProcessor() {
      this("properties");
   }

   public ClassnamePropertiesPreProcessor(String propertiesFieldName) {
      this.propertiesFieldName = propertiesFieldName;
   }

   public String convert(String untyped) {
      if (untyped == null) {
         return "";
      }

      String trimmed = untyped.trim();

      if (trimmed.length() == 0) {
         return "";
      }

      StringTokenizer tokenizer = new StringTokenizer(trimmed);
      if (!tokenizer.hasMoreTokens()) {
         return "";
      }

      String firstToken = tokenizer.nextToken();
      boolean hasClassName = firstToken.indexOf('=') < 0;

      if (hasClassName && !tokenizer.hasMoreTokens()) {
         return firstToken + ".class";
      }

      String attributes = hasClassName ? trimmed.substring(firstToken.length())
            : trimmed;

      String[] properties = attributes.trim().split("\\s+");

      StringBuffer buffer = new StringBuffer();
      if (hasClassName) {
         buffer.append("value=");
         buffer.append(firstToken);
         buffer.append(".class,");
      }

      String value = null;
      buffer.append(propertiesFieldName);
      buffer.append("={");
      for (int i = 0; i < properties.length; i++) {
         if (properties[i].startsWith("class=")) {
            value = properties[i].substring(6);
            continue;
         }
         if ((value == null && i > 0) || value != null && i > 1) {
            buffer.append(',');
         }
         buffer.append('"');
         buffer.append(properties[i]);
         buffer.append('"');
      }
      buffer.append('}');

      if (value != null) {
         buffer.append(",value=");
         buffer.append(value);
         buffer.append(".class");
      }
      return buffer.toString();
   }
}
