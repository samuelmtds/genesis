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
package net.java.dev.genesis.tests.converters;

import java.math.BigDecimal;

import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.genesis.tests.TestCase;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;



public class BigDecimalConverterTest extends TestCase {

   public void testBigDecimalConverter() {
      ConvertUtils.register(new BigDecimalConverter(), BigDecimal.class);
      BigDecimal bigDecimal = new BigDecimal("5.2");

      final Converter converter = ConvertUtils.lookup(bigDecimal.getClass());

      assertTrue(new BigDecimal("0.0").compareTo((BigDecimal) converter
            .convert(BigDecimal.class, "0,0")) == 0);
      assertEquals(new BigDecimal("2.23"), converter.convert(BigDecimal.class,
            "2,23"));
      assertNull(converter.convert(BigDecimal.class, "2,22A"));
      assertEquals(bigDecimal.toString(), "5.2");
   }
}