/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import net.java.dev.genesis.GenesisTestCase;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Var;

public class BasicValidatorTest extends GenesisTestCase {
   private static final String BIG_DECIMAL_PROPERTY = "bigDecimal";

   private Field field;
   private Var min = new Var("min", "0.1", null);
   private Var max = new Var("max", "10.1", null);
   private Converter oldConverter;

   protected void setUp() {
      field = new Field();
      field.setProperty(BIG_DECIMAL_PROPERTY);

      oldConverter = ConvertUtils.lookup(BigDecimal.class);
      ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
   }

   protected void tearDown() {
      field = null;
      ConvertUtils.register(oldConverter, BigDecimal.class);
   }

   public void testValidateBigDecimalRange() {
      field.addVar(min);
      field.addVar(max);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "-1");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0.1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1000000000000001");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));
   }

   public void testValidateMin() {
      field.addVar(min);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "-1");
      assertFalse(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0");
      assertFalse(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0.1");
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateMin(map, field));
   }

   public void testValidateMax() {
      field.addVar(max);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1000000000000001");
      assertFalse(BasicValidator.validateMax(map, field));
   }
}
