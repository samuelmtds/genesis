/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.GenesisTestCase;

public class BooleanFormatterTest extends GenesisTestCase {
   private static final String TRUE_VALUE = "a";
   private static final String FALSE_VALUE = "b";
   private static final String NULL_VALUE = "c";

   public void testDefaultConstructor() {
      testFormat(new BooleanFormatter(), BooleanFormatter.TRUE, 
            BooleanFormatter.FALSE, BooleanFormatter.FALSE);
   }

   public void testTwoStringsConstructor() {
      testFormat(new BooleanFormatter(TRUE_VALUE, FALSE_VALUE), TRUE_VALUE, 
            FALSE_VALUE, FALSE_VALUE);
   }

   public void testThreeStringsConstructor() {
      testFormat(new BooleanFormatter(TRUE_VALUE, FALSE_VALUE, NULL_VALUE), 
            TRUE_VALUE, FALSE_VALUE, NULL_VALUE);
   }

   private void testFormat(BooleanFormatter formatter, String trueValue, 
         String falseValue, String nullValue) {
      assertEquals(formatter.format(Boolean.TRUE), trueValue);
      assertEquals(formatter.format(Boolean.FALSE), falseValue);
      assertEquals(formatter.format(null), nullValue);
   }
}