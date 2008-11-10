/*
 * The Genesis Project
 * Copyright (C) 2004-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.commons.beanutils.converters;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.Converter;

public class BigDecimalConverter implements Converter {
   private org.apache.commons.beanutils.converters.BigDecimalConverter converter = 
               new org.apache.commons.beanutils.converters.BigDecimalConverter(null);
   
   public Object convert(Class clazz, Object obj) {
      if (obj instanceof BigDecimal) {
         return obj;
      }

      if (obj == null || !(obj instanceof String)) {
         return converter.convert(clazz, obj);
      }

      final String s = obj.toString();

      if (s.trim().length() == 0) {
         return converter.convert(clazz, obj);
      }

      final StringTokenizer t = new StringTokenizer(s, "."); // NOI18N
      final StringBuffer ret = new StringBuffer(s.length());

      while (t.hasMoreTokens()) {
         ret.append(t.nextToken());
      }

      int i;
      if ((i = ret.indexOf(",")) != -1) { // NOI18N
         ret.setCharAt(i,  '.');
      }

      try {
         return new BigDecimal(ret.toString().trim());
      } catch (NumberFormatException nfe) {
         return null;
      }
   }   
}