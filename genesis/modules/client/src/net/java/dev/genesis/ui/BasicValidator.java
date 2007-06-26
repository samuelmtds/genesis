/*
 * The Genesis Project
 * Copyright (C) 2004-2007  Summa Technologies do Brasil Ltda.
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

import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptRegistry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.util.ValidatorUtils;

public class BasicValidator {
   public final static String FIELD_TEST_NULL = "NULL";
   public final static String FIELD_TEST_NOTNULL = "NOTNULL";
   public final static String FIELD_TEST_EQUAL = "EQUAL";
   
   public static boolean validateRequired(Object bean, Field field) {
      return !GenericValidator.isBlankOrNull(ValidatorUtils.getValueAsString(
            bean, field.getProperty()));
   }
   
   public static boolean validateByte(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) ||
            GenericValidator.isByte(value);
   }

   public static boolean validateBigDecimal(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) ||
            ConvertUtils.convert(value, BigDecimal.class) != null;
   }
   
   public static boolean validateBigDecimalRange(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, true, true);
   }

   public static boolean validateMin(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, true, false);
   }

   public static boolean validateMax(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, false, true);
   }

   private static boolean validateBigDecimalRange(Object bean, Field field, 
         boolean validateMin, boolean validateMax) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      if (GenericValidator.isBlankOrNull(value)) {
         return true;
      }

      if (!validateBigDecimal(bean, field)) {
         return false;
      }
      
      BigDecimal inputValue = normalize(ConvertUtils.convert(value,
            BigDecimal.class));
      BigDecimal min = validateMin ? new BigDecimal(field.getVarValue("min")) : 
            null;
      BigDecimal max = validateMax ? new BigDecimal(field.getVarValue("max")) : 
            null;

      return (!validateMin || min.compareTo(inputValue) <= 0) && 
            (!validateMax || max.compareTo(inputValue) >= 0);
   }
   
   private static BigDecimal normalize(Object value) {
      return value == null ? new BigDecimal(0) : (BigDecimal)value;
   }

   public static boolean validateShort(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isShort(
            value);
   }
   
   public static boolean validateInt(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isInt(
            value);
   }
   
   public static boolean validateLong(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isLong(
            value);
   }
   
   public static boolean validateFloat(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isFloat(
            value);
   }
   
   public static boolean validateDouble(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isDouble(
            value);
   }
   
   public static boolean validateEmail(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isEmail(
            value);
   }
   
   /**
    * @deprecated use validateScript(Object,Field) instead
    */
   public static boolean validateRequiredIf(Object bean, Field field,
         Validator validator) {
      final Object form = validator.getParameterValue(Validator.BEAN_PARAM);
      String value = null;
      boolean required = false;
      
      if (isString(bean)) {
         value = (String) bean;
      } else {
         value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      }
      
      String fieldJoin = "AND";
      
      if (!GenericValidator.isBlankOrNull(field.getVarValue("fieldJoin"))) {
         fieldJoin = field.getVarValue("fieldJoin");
      }
      
      if (fieldJoin.equalsIgnoreCase("AND")) {
         required = true;
      }
      
      for (int i = 0 ; !GenericValidator.isBlankOrNull(field.getVarValue(
            "field[" + i + "]")); i++) {
         String dependProp = field.getVarValue("field[" + i + "]");
         String dependTest = field.getVarValue("fieldTest[" + i + "]");
         String dependTestValue = field.getVarValue("fieldValue[" + i + "]");
         String dependIndexed = field.getVarValue("fieldIndexed[" + i + "]");
         
         if (dependIndexed == null) {
            dependIndexed = "false";
         }
         
         String dependVal = null;
         boolean thisRequired = false;
         
         if (field.isIndexed() && dependIndexed.equalsIgnoreCase("true")) {
            String key = field.getKey();
            
            if ((key.indexOf("[") > -1) && (key.indexOf("]") > -1)) {
               String ind = key.substring(0, key.indexOf(".") + 1);
               dependProp = ind + dependProp;
            }
         }
         
         dependVal = ValidatorUtils.getValueAsString(form, dependProp);
         
         if (dependTest.equals(FIELD_TEST_NULL) ||
               dependTest.equals(FIELD_TEST_NOTNULL)) {
            thisRequired = GenericValidator.isBlankOrNull(dependVal);
            
            if (dependTest.equals(FIELD_TEST_NOTNULL)) {
               thisRequired = !thisRequired;
            }
         }
         
         if (dependTest.equals(FIELD_TEST_EQUAL)) {
            thisRequired =  dependTestValue.equalsIgnoreCase(dependVal);
         }
         
         if (fieldJoin.equalsIgnoreCase("AND")) {
            required = required && thisRequired;
         } else {
            required = required || thisRequired;
         }
      }
      
      if (required) {
         return ((value != null) && (value.length() > 0));
      }
      
      return true;
   }
   
   private static boolean isString(Object o) {
      return o instanceof String;
   }
   
   public static boolean validateMask(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .matchRegexp(value, field.getVarValue("mask"));
   }
   
   public static boolean validateDate(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isDate(
            value, field.getVarValue("pattern"), false);
   }
   
   public static boolean validateMaxLength(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .maxLength(value, Integer.parseInt(field.getVarValue("maxlength")));
   }
   
   public static boolean validateMinLength(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .minLength(value, Integer.parseInt(field.getVarValue("minlength")));
   }
   
   public static boolean validateLongRange(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || 
            (GenericValidator.isLong(value) && GenericValidator.isInRange(
            Long.parseLong(value), Long.parseLong(field.getVarValue("min")), 
            Long.parseLong(field.getVarValue("max"))));
   }
   
   public static boolean validateDoubleRange(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || 
            (GenericValidator.isDouble(value) && GenericValidator.isInRange(
            Double.parseDouble(value), Double.parseDouble(field.getVarValue(
            "min")), Double.parseDouble(field.getVarValue("max"))));
   }

   /**
    * @deprecated use validateScript(Object,Field) instead
    */
   public static boolean validateJXPath(Object bean, Field field) {
      Script script = ScriptRegistry.getInstance().getScript(ScriptRegistry.JXPATH);
      final ScriptContext ctx = script.newContext(bean);

      return !Boolean.FALSE.equals(ctx.eval(field.getVarValue("jxpath")));
   }

   public static boolean validateScript(Object bean, Field field) {
      Script script = ScriptRegistry.getInstance().getScript();
      final ScriptContext ctx = script.newContext(bean);

      return !Boolean.FALSE.equals(ctx.eval(field.getVarValue("script")));
   }
}