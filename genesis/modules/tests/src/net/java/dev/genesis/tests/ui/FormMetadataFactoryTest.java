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

import net.java.dev.genesis.equality.DefaultEqualityComparator;
import net.java.dev.genesis.equality.StringEqualityComparator;
import net.java.dev.genesis.resolvers.DefaultEmptyResolver;
import net.java.dev.genesis.resolvers.StringEmptyResolver;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.StringConverter;



public class FormMetadataFactoryTest extends TestCase {

   FieldMetadata codeField;
   FieldMetadata nameField;
   FieldMetadata objField;
   FieldMetadata numberField;
   FieldMetadata fieldField;
   FieldMetadata descriptionField;

   static {
      ConvertUtils.register(ConvertUtils.lookup(String.class), Object.class);
   }

   public void setUp() throws Exception {
      super.setUp();
      final FormMetadata formMetadata = getFormMetadata(new FooForm());

      codeField = formMetadata.getFieldMetadata("code");
      nameField = formMetadata.getFieldMetadata("name");
      objField = formMetadata.getFieldMetadata("obj");
      numberField = formMetadata.getFieldMetadata("number");
      fieldField = formMetadata.getFieldMetadata("field");
      descriptionField = formMetadata.getFieldMetadata("description");

   }

   public void testParseFormMetadata() {
      final FormMetadata formMetadata = getFormMetadata(new FooForm());
      System.out.println(formMetadata);
   }

   public void testConditions() {
      final FormMetadata formMetadata = getFormMetadata(new FooForm());
      assertEquals("string-length(normalize-space(code)) != 1", formMetadata
            .getNamedCondition("codeCondition").toString());
      assertEquals("string-length(normalize-space(code)) != 0", formMetadata
            .getNamedCondition("objCondition").toString());
   }

   public void testClearOn() {

      assertEquals("string-length(normalize-space(code)) != 1", codeField
            .getClearOnCondition().toString());

      assertNull(nameField.getClearOnCondition());

      assertNull(objField.getClearOnCondition());

      assertNull(numberField.getClearOnCondition());

      assertNull(fieldField.getClearOnCondition());

      assertNull(descriptionField.getClearOnCondition());
   }

   public void testEnabledCondition() {

      assertEquals("string-length(normalize-space(code)) != 0", codeField
            .getEnabledCondition().toString());

      assertNull(nameField.getEnabledCondition());

      assertNull(objField.getEnabledCondition());

      assertEquals("string-length(normalize-space(code)) != 0", numberField
            .getEnabledCondition().toString());

      assertNull(fieldField.getEnabledCondition());

      assertNull(descriptionField.getEnabledCondition());
   }

   public void testDisplayOn() {

      assertTrue(codeField.isDisplayOnly());
      assertFalse(nameField.isDisplayOnly());
      assertFalse(objField.isDisplayOnly());
      assertFalse(numberField.isDisplayOnly());
      assertFalse(fieldField.isDisplayOnly());
      assertFalse(descriptionField.isDisplayOnly());
   }

   public void testEmptyResolver() {

      assertEquals(codeField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);
      assertTrue(((StringEmptyResolver) codeField.getEmptyResolver()).isTrim());

      assertEquals(nameField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(objField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);
      assertTrue(((StringEmptyResolver) objField.getEmptyResolver()).isTrim());

      assertEquals(numberField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(fieldField.getEmptyResolver().getClass(),
            DefaultEmptyResolver.class);

      assertEquals(descriptionField.getEmptyResolver().getClass(),
            StringEmptyResolver.class);

      assertSame(fieldField.getEmptyResolver(), numberField.getEmptyResolver());
      assertSame(descriptionField.getEmptyResolver(), codeField
            .getEmptyResolver());
   }

   public void testEqualityComparator() {

      assertEquals(codeField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);
      assertTrue(((StringEqualityComparator) codeField.getEqualityComparator())
            .isTrim());

      assertEquals(nameField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(objField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);
      assertTrue(((StringEqualityComparator) objField.getEqualityComparator())
            .isTrim());

      assertEquals(numberField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(fieldField.getEqualityComparator().getClass(),
            DefaultEqualityComparator.class);

      assertEquals(descriptionField.getEqualityComparator().getClass(),
            StringEqualityComparator.class);

      assertSame(fieldField.getEqualityComparator(), numberField
            .getEqualityComparator());

      assertNotSame(descriptionField.getEqualityComparator(), codeField
            .getEqualityComparator());

   }

   public void testConverter() {

      assertNotNull(codeField.getConverter());
      assertEquals(codeField.getConverter().getClass(), StringConverter.class);

      assertNotNull(nameField.getConverter());
      assertEquals(nameField.getConverter().getClass(), StringConverter.class);

      assertNotNull(objField.getConverter());
      assertEquals(objField.getConverter().getClass(), StringConverter.class);

      assertNotNull(numberField.getConverter());
      assertEquals(numberField.getConverter().getClass(),
            IntegerConverter.class);

      assertNotNull(fieldField.getConverter());
      assertEquals(fieldField.getConverter().getClass(), StringConverter.class);

      assertNotNull(descriptionField.getConverter());
      assertEquals(descriptionField.getConverter().getClass(),
            StringConverter.class);

   }

   /**
    * @Form
    */
   public static class FooForm {

      private String code;
      private String name;
      private Object obj;
      private int number;
      private Object field;
      private String description;

      /**
       * @Condition
       * 		codeCondition=string-length(normalize-space(code)) != 1
       * @ClearOn
       * 		string-length(normalize-space(code)) != 1 
       * @EnabledWhen
       * 		string-length(normalize-space(code)) != 0
       * @EqualityComparator
       * 		trim=true
       * @EmptyResolver
       * @DisplayOnly
       */
      public String getCode() {
         return code;
      }

      public void setCode(String code) {
         this.code = code;
      }

      /**
       * @EnabledWhen
       * 		string-length(normalize-space(code)) != 0
       * @return
       */
      public int getNumber() {
         return number;
      }

      public void setNumber(int number) {
         this.number = number;
      }

      /**
       * @EqualityComparator
       * 		class=net.java.dev.genesis.equality.DefaultEqualityComparator
       * @EmptyResolver
       * 		class=net.java.dev.genesis.resolvers.DefaultEmptyResolver
       */
      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      /**
       * @Condition
       * 		objCondition=string-length(normalize-space(code)) != 0
       * @EqualityComparator
       * 		class=net.java.dev.genesis.equality.StringEqualityComparator trim=true
       * @EmptyResolver
       * 		class=net.java.dev.genesis.resolvers.StringEmptyResolver trim=true
       */
      public Object getObj() {
         return obj;
      }

      public void setObj(Object obj) {
         this.obj = obj;
      }

      /**
       * @EmptyResolver
       */
      public String getDescription() {
         return description;
      }

      public void setDescription(String description) {
         this.description = description;
      }

      /**
       * @EmptyResolver
       */
      public Object getField() {
         return field;
      }

      public void setField(Object field) {
         this.field = field;
      }

   }
}