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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import net.java.dev.genesis.GenesisTestCase;

public class FormatAdapterTest extends GenesisTestCase {
   private static final class MockFormat extends Format {
      private String formattedString;

      void setFormattedString(String formattedString) {
         this.formattedString = formattedString;
      }

      public StringBuffer format(Object obj, StringBuffer toAppendTo, 
            FieldPosition pos) {
         return toAppendTo.append(formattedString);
      }
      
      public Object parseObject(String source, ParsePosition pos) {
         throw new UnsupportedOperationException();
      }
   }

   public void testFormatConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock);
      testFormat(adapter, mock, false);
   }

   public void testFormatTrueConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock, true);
      testFormat(adapter, mock, true);
   }

   public void testFormatFalseConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock, false);
      testFormat(adapter, mock, false);
   }

   private void testFormat(FormatAdapter formatter, MockFormat mock, 
         boolean nullAsBlank) {
      String text = "text";
      mock.setFormattedString(text);

      assertEquals(formatter.format(null), nullAsBlank ? "" : text);
      assertEquals(formatter.format(new Object()), text);
   }
}