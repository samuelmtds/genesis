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
package net.java.dev.genesis.tests.jxpath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;



public class ExtensionJXPathFunctionsTest extends TestCase {

   static {
      ConvertUtils.register(ConvertUtils.lookup(String.class), Object.class);
      ConvertUtils.register(new BigDecimalConverter(), BigDecimal.class);
      ConvertUtils.register(new BooleanConverter(null), Boolean.class);
      ConvertUtils.register(new CharacterConverter(null), Character.class);
      ConvertUtils.register(new ByteConverter(null), Byte.class);
      ConvertUtils.register(new ShortConverter(null), Short.class);
      ConvertUtils.register(new IntegerConverter(null), Integer.class);
      ConvertUtils.register(new LongConverter(null), Long.class);
      ConvertUtils.register(new DoubleConverter(null), Double.class);
      ConvertUtils.register(new FloatConverter(null), Float.class);
   }

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   public void testIsEmptyFunction() throws Exception {
      final FooForm form = new FooForm();
      final FormController controller = getController(form);

      controller.populate(BeanUtils.describe(form));
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.TRUE);
      controller.save();

      final Map newValues = new HashMap();
      newValues.put("integer1", "0");
      newValues.put("string1", null);
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.FALSE);

      controller.reset();
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.TRUE);

      newValues.put("integer1", null);
      newValues.put("string1", "");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.TRUE);

      newValues.put("integer1", null);
      newValues.put("string1", "        ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.TRUE);

      newValues.put("integer1", null);
      newValues.put("string1", " abc ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.FALSE);

      controller.reset();
      assertEquals(controller.getEnabledMap().get("integer2"), Boolean.TRUE);
   }

   public void testIsEqualFunction() throws Exception {

      final FooForm form = new FooForm();
      final FormController controller = getController(form);

      controller.populate(BeanUtils.describe(form));
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.TRUE);
      controller.save();

      final Map newValues = new HashMap();
      newValues.put("string1", null);
      newValues.put("string2", "");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.TRUE);

      newValues.put("string1", "             ");
      newValues.put("string2", "    ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.TRUE);

      newValues.put("string1", "             ");
      newValues.put("string2", "  a  ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.FALSE);

      controller.reset();
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.TRUE);

      newValues.put("string1", "             ");
      newValues.put("string2", "  a  ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.FALSE);

      newValues.put("string1", "            a ");
      newValues.put("string2", "  a  ");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("integer1"), Boolean.TRUE);

      newValues.put("string1", "a b cc");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("string2"), Boolean.TRUE);
      
      newValues.put("string1", "2");
      controller.populate(newValues);
      assertEquals(controller.getEnabledMap().get("string2"), Boolean.FALSE);

      controller.reset();
      assertEquals(controller.getEnabledMap().get("string2"), Boolean.FALSE);
      
      assertEquals(controller.getEnabledMap().get("string1"), Boolean.TRUE);
      
   }
   
   /**
    * @Form
    */
   public static class FooForm {
      private Integer integer1;
      private Integer integer2;
      private String string1;
      private String string2;
      private String[] arrayString;
      
      public String[] getArrayString() {
         return arrayString;
      }

      /**
       * @Condition
       * 		testStringArray1Empty=g:isEmpty(arrayString[1])
       */
      public void setArrayString(String[] arrayString) {
         this.arrayString = arrayString;
      }

      /**
       * @Condition
       * 		testInteger1Empty=g:isEmpty(integer1)
       * @EnabledWhen $testString1EqualsString2=true()
       */
      public Integer getInteger1() {
         return integer1;
      }

      public void setInteger1(Integer integer1) {
         this.integer1 = integer1;
      }

      /**
       * @EnabledWhen $testInteger1Empty=true() and $testString1Empty=true()
       */
      public Integer getInteger2() {
         return integer2;
      }

      public void setInteger2(Integer integer2) {
         this.integer2 = integer2;
      }

      /**
       * @Condition
       * 		testString1Empty=g:isEmpty(string1)
       * @Condition
       * 		testString1EqualsString2=g:equals(string1,string2)
       * @EnabledWhen g:equals(12,12) and g:equals(' abc ','   abc')
       */
      public String getString1() {
         return string1;
      }

      public void setString1(String string1) {
         this.string1 = string1;
      }

      /**
       * @EnabledWhen g:equals(string1,'a b cc') or g:equals(string1,2)
       */
      public String getString2() {
         return string2;
      }

      public void setString2(String string2) {
         this.string2 = string2;
      }
   }
}