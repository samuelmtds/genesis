/*
 * The Genesis Project
 * Copyright (C) 2005-2008  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.text;

public class BooleanFormatter implements Formatter {
   public static final String TRUE = "true"; // NOI18N
   public static final String FALSE = "false"; // NOI18N

   private final String trueValue;
   private final String falseValue;
   private final String nullValue;

   public BooleanFormatter() {
      this(TRUE, FALSE, FALSE);
   }

   public BooleanFormatter(String trueValue, String falseValue) {
      this(trueValue, falseValue, falseValue);
   }

   public BooleanFormatter(String trueValue, String falseValue, 
         String nullValue) {
      this.trueValue = trueValue;
      this.falseValue = falseValue;
      this.nullValue = nullValue;
   }

   public String format(Object o) {
      return o == null ? nullValue : (((Boolean)o).booleanValue() ? trueValue :
            falseValue);
   }
}