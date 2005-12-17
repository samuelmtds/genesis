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
package net.java.dev.genesis.tests.validator;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.BasicValidator;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

public class ValidatorTest extends TestCase {
   private ValidatorResources resources;
   private Form form;
   private Converter oldConverter;

   protected void setUp() {
      oldConverter = ConvertUtils.lookup(BigDecimal.class);
      ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
   }

   protected void tearDown() {
      ConvertUtils.register(oldConverter, BigDecimal.class);
   }


   public ValidatorTest() throws Exception {
      resources = new ValidatorResources(new InputStream[] {
            Thread.currentThread().getContextClassLoader().getResourceAsStream(
                  "validation.xml"),
            Thread.currentThread().getContextClassLoader().getResourceAsStream(
                  "validator-rules.xml") });
      form = resources.getForm(Locale.getDefault(), ValueBean.class.getName());
   }

   private void valueTest(Object bean, String methodName, Field field,
         String action, boolean passed) throws Exception {
      // Calls the BasicValidator static method
      Method method = BasicValidator.class.getDeclaredMethod(methodName,
            new Class[] { Object.class, Field.class });
      boolean success = ((Boolean) method.invoke(null, new Object[] { bean,
            field })).booleanValue();
      assertTrue(methodName + " for field " + field.getProperty() + 
            " with value " + 
            PropertyUtils.getProperty(bean, field.getProperty()) + 
            " and field [" + field +  "] has "
            + (passed ? "" : "not ") + "passed", passed ? success : !success);

      // Performs regular validation
      Validator validator = new Validator(resources, ValueBean.class.getName());
      validator.setParameter(Validator.BEAN_PARAM, bean);
      ValidatorResults results = validator.validate();
      assertNotNull(results);
      ValidatorResult result = results.getValidatorResult(field.getProperty());

      assertNotNull(action + " value ValidatorResult should not be null.",
            result);
      assertTrue("Bean " + bean + " ValidatorResult should contain the '"
            + action + "' action.", result.containsAction(action));
      assertTrue("Bean  " + bean + "ValidatorResult for the '" + action
            + "' action should have " + (passed ? "passed" : "failed") + ".",
            (passed ? result.isValid(action) : !result.isValid(action)));

   }
   
   private void valueTestRequiredIf(Object bean, String methodName, Field field,
         String action, boolean passed) throws Exception {
      // Calls the BasicValidator static method
      Validator validator = new Validator(resources, ValueBean.class.getName());
      validator.setParameter(Validator.BEAN_PARAM, bean);
      Method method = BasicValidator.class.getDeclaredMethod(methodName,
            new Class[] { Object.class, Field.class, Validator.class });
      boolean sucess = ((Boolean) method.invoke(null, new Object[] { bean,
            field, validator })).booleanValue();
      assertTrue(methodName + " for field " + field.getProperty() + " has "
            + (passed ? "" : "not ") + "passed", passed ? sucess : !sucess);

      // Performs regular validation
      ValidatorResults results = validator.validate();
      assertNotNull(results);
      ValidatorResult result = results.getValidatorResult(field.getProperty());
      assertNotNull(action + " value ValidatorResult should not be null.",
            result);
      assertTrue("Bean " + bean + " ValidatorResult should contain the '"
            + action + "' action.", result.containsAction(action));
      assertTrue("Bean  " + bean + "ValidatorResult for the '" + action
            + "' action should have " + (passed ? "passed" : "failed") + ".",
            (passed ? result.isValid(action) : !result.isValid(action)));

   }

   public void testRequired() throws Exception {
      final Field field = form.getField("required");
      final String methodName = "validateRequired";
      final String action = "required";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "           ");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "some value");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, true);
   }

   public void testByte() throws Exception {
      final Field field = form.getField("someByte");
      final String methodName = "validateByte";
      final String action = "byte";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Byte.MAX_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Byte.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Byte.MAX_VALUE + 1));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), String.valueOf(Byte.MIN_VALUE - 1));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testShort() throws Exception {
      final Field field = form.getField("someShort");
      final String methodName = "validateShort";
      final String action = "short";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Short.MAX_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Short.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Short.MAX_VALUE + 1));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), String.valueOf(Short.MIN_VALUE - 1));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testInteger() throws Exception {
      final Field field = form.getField("integer");
      final String methodName = "validateInt";
      final String action = "int";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Integer.MAX_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Integer.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Integer.MAX_VALUE) + "1");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), String.valueOf(Integer.MIN_VALUE) + "1");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testLong() throws Exception {
      final Field field = form.getField("someLong");
      final String methodName = "validateLong";
      final String action = "long";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Long.MAX_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Long.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Long.MAX_VALUE) + "1");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), String.valueOf(Long.MIN_VALUE) + "1");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testFloat() throws Exception {
      final Field field = form.getField("someFloat");
      final String methodName = "validateFloat";
      final String action = "float";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Float.MAX_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Float.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testDouble() throws Exception {
      final Field field = form.getField("someDouble");
      final String methodName = "validateDouble";
      final String action = "double";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Double(Double.MAX_VALUE).toString());
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), String.valueOf(Double.MIN_VALUE));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testBigDecimal() throws Exception {
      final Field field = form.getField("someBigDecimal");
      final String methodName = "validateBigDecimal";
      final String action = "bigDecimal";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "1328217321.2312318732131");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "-1328217321.2312318732131");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
      
      map.put(field.getProperty(), "-1328217321.231sfdsdf2318732131");
      valueTest(map, methodName, field, action, false);
   }

   public void testEmail() throws Exception {
      final Field field = form.getField("email");
      final String methodName = "validateEmail";
      final String action = "email";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "          ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "jsmith@apache.org");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "jsmith@apache.com");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "jsmith@apache.net");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "jsmith@apache.info");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "jsmith@apache.infoo");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "jsmith@apache.");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "jsmith@apache.c");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testDate() throws Exception {
      final Field field = form.getField("date");
      final String methodName = "validateDate";
      final String action = "date";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "        ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "13/13/2003");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "20/12/2004");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Date());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), new SimpleDateFormat("dd/MM/yyyy")
            .format(new Date()));
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), new Object());
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "invalid");
      valueTest(map, methodName, field, action, false);
   }

   public void testRequiredIf() throws Exception {
      final Field field = form.getField("requiredIf");
      final String methodName = "validateRequiredIf";
      final String action = "requiredif";
      final String field2 = "requiredIfDependency";
      final Map map = new HashMap();
      
      map.put(field.getProperty(), "");
      map.put(field2, "");
      valueTestRequiredIf(map, methodName, field, action, true);
      
      map.put(field2, "                ");
      valueTestRequiredIf(map, methodName, field, action, true);
      
      map.put(field2, "some value");
      valueTestRequiredIf(map, methodName, field, action, false);
      
      map.put(field.getProperty(), "some value");
      map.put(field2, "some value");
      valueTestRequiredIf(map, methodName, field, action, true);
   }

   public void testMask() throws Exception {
      final Field field = form.getField("mask");
      final String methodName = "validateMask";
      final String action = "mask";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "                    ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "AA");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "99");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "A9");
      valueTest(map, methodName, field, action, true);
   }

   public void testMaxLength() throws Exception {
      final Field field = form.getField("maxLength");
      final String methodName = "validateMaxLength";
      final String action = "maxlength";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "                    ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "     1              ");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "12345678901");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "1234567890");
      valueTest(map, methodName, field, action, true);
   }

   public void testMinLength() throws Exception {
      final Field field = form.getField("minLength");
      final String methodName = "validateMinLength";
      final String action = "minlength";
      final Map map = new HashMap();

      map.put(field.getProperty(), "");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "                    ");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), " 1");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "1");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "12");
      valueTest(map, methodName, field, action, true);
   }

   public void testLongRange() throws Exception {
      final Field field = form.getField("longRange");
      final String methodName = "validateLongRange";
      final String action = "longRange";
      final Map map = new HashMap();

      map.put(field.getProperty(), String.valueOf(Long.MAX_VALUE));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "4");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "11");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "5");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "10");
      valueTest(map, methodName, field, action, true);
   }

   public void testDoubleRange() throws Exception {
      final Field field = form.getField("doubleRange");
      final String methodName = "validateDoubleRange";
      final String action = "doubleRange";
      final Map map = new HashMap();

      map.put(field.getProperty(), String.valueOf(Double.MAX_VALUE));
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "4");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "11");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "5");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "10");
      valueTest(map, methodName, field, action, true);
   }

   public void testBigDecimalRange() throws Exception {
      final Field field = form.getField("bigDecimalRange");
      final String methodName = "validateBigDecimalRange";
      final String action = "bigDecimalRange";
      final Map map = new HashMap();

      map.put(field.getProperty(), "213123213213.21312321");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "5.123399999999");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "9.123400000001");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "5.1234");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "9.1234");
      valueTest(map, methodName, field, action, true);
   }

   public void testMin() throws Exception {
      final Field field = form.getField("min");
      final String methodName = "validateMin";
      final String action = "min";
      final Map map = new HashMap();

      map.put(field.getProperty(), "5.1234");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "6");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "2.21312321");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "5.123399999999");
      valueTest(map, methodName, field, action, false);
   }

   public void testMax() throws Exception {
      final Field field = form.getField("max");
      final String methodName = "validateMax";
      final String action = "max";
      final Map map = new HashMap();

      map.put(field.getProperty(), "9.123400000001");
      valueTest(map, methodName, field, action, false);

      map.put(field.getProperty(), "9.1234");
      valueTest(map, methodName, field, action, true);

      map.put(field.getProperty(), "9");
      valueTest(map, methodName, field, action, true);
   }

   public void testJXPath() throws Exception {
      final Field field = form.getField("jxpath");
      final String methodName = "validateJXPath";
      final String action = "jxpath";
      final String field2 = "jxpathDependency";
      final Map map = new HashMap();
      
      map.put(field2, "9");
      valueTest(map, methodName, field, action, false);
      
      map.put(field2, "11");
      valueTest(map, methodName, field, action, false);
      
      map.put(field2, "10");
      valueTest(map, methodName, field, action, true);
   }
}