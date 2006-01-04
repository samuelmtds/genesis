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

public class MultiStringPreprocessor implements AnnotationPreprocessor {
   public String convert(String untyped) {
      if (untyped == null) {
         return "";
      }

      String trimmed = untyped.trim();

      if (trimmed.length() == 0) {
         return "";
      }
      
      String[] values = trimmed.split("\\s*,\\s*");
      StringBuffer buffer = new StringBuffer();
      buffer.append('{');
      for (int i = 0; i < values.length; i++) {
         if (i > 0) {
            buffer.append(',');
         }
         buffer.append('"');
         buffer.append(values[i]);
         buffer.append('"');
      }
      buffer.append('}');

      return buffer.toString();
   }
}
