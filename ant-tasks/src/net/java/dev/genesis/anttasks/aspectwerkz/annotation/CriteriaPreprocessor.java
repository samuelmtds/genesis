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

public class CriteriaPreprocessor implements AnnotationPreprocessor {
   public String convert(String untyped) {
      if (untyped == null) {
         return "";
      }

      String trimmed = untyped.trim();

      if (trimmed.length() == 0) {
         return "";
      }

      String[] values = trimmed.split("(order-by\\s*=)");

      if (values.length == 0) {
         return "";
      }

      String persisterClassName = values[0].trim();

      if (values.length == 1) {
         return persisterClassName + ".class";
      }

      StringBuffer buffer = new StringBuffer();
      buffer.append("value=");
      buffer.append(persisterClassName);
      buffer.append(".class,orderby={");

      String[] props = values[1].trim().split("(\\s*,\\s*)");
      for (int i = 0; i < props.length; i++) {
         if (i > 0) {
            buffer.append(',');
         }
         buffer.append('"');

         buffer.append(props[i]);
         buffer.append('"');
      }

      buffer.append('}');

      return buffer.toString();
   }

}
