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
package net.java.dev.genesis.tests.ui;

import java.math.BigDecimal;
import java.util.Map;

import net.java.dev.genesis.commons.beanutils.converters.BigDecimalConverter;
import net.java.dev.genesis.reflection.MethodEntry;
import net.java.dev.genesis.registry.DefaultValueRegistry;
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



public class FormControllerEvaluateConditionsTest extends TestCase {

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

   public void testConditions() throws Exception {
      final FooForm form = new FooForm();
      final FormController controller = getController(form);

      final Map someValues = BeanUtils.describe(form);
      someValues.put("field2", "12");
      someValues.put("field3", "abc");
      controller.populate(someValues);

      assertEquals(form.getField3(), DefaultValueRegistry.getInstance().get(
            form.getField3()));
      assertEquals(controller.getEnabledMap().get("field2"), Boolean.TRUE);
      controller.save();

      someValues.put("field2", "12");
      someValues.put("field3", "abcd");
      someValues.put("field4", "notEmpty");
      controller.populate(someValues);

      final MethodEntry calculateEntry = new MethodEntry("calculate", new String[]{});
      assertEquals(form.getField3(), "abcd");
      assertEquals(controller.getEnabledMap().get("field2"), Boolean.FALSE);
      assertEquals(controller.getVisibleMap().get("field4"), Boolean.TRUE);
      assertEquals(controller.getCallMap().get(calculateEntry), Boolean.TRUE);

      controller.reset();
      assertEquals(form.getField3(), DefaultValueRegistry.getInstance().get(
            form.getField3()));
      assertEquals(controller.getEnabledMap().get("field2"), Boolean.TRUE);
      assertEquals(controller.getVisibleMap().get("field4"), Boolean.FALSE);
      assertEquals(controller.getCallMap().get(calculateEntry), Boolean.FALSE);
   }

   /**
    * @Form
    */
   public static class FooForm {

      private Integer field1;
      private String field2;
      private String field3;
      private String field4;

      /**
       * @Condition
       * 		Field3EqualsField4=g:isEqual(field3,field4)
       * @Condition
       * 		Field4Empty=g:isEmpty(field4)
       */
      public Integer getField1() {
         return field1;
      }

      public void setField1(Integer field1) {
         this.field1 = field1;
      }

      /**
       * @EnabledWhen $Field4Empty
       * @DisplayOnly
       */
      public String getField2() {
         return field2;
      }

      public void setField2(String field2) {
         this.field2 = field2;
      }

      /**
       * @ClearOn g:isEqual(field3,'abc')
       */
      public String getField3() {
         return field3;
      }

      public void setField3(String field3) {
         this.field3 = field3;
      }
      
      /**
       * @VisibleWhen g:isEqual(field3,'abcd')
       */
      public String getField4() {
         return field4;
      }

      public void setField4(String field4) {
         this.field4 = field4;
      }

      /**
       * @Action 
       * @CallWhen g:isEqual(field3,'abcd')
       */
      public void calculate(){
      }
   }

}