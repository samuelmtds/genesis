/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.script;

import net.java.dev.genesis.script.ScriptContext;
import net.java.dev.genesis.script.ScriptFactory;
import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormState;
import net.java.dev.genesis.ui.controller.FormStateImpl;

import org.apache.commons.beanutils.PropertyUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ScriptFunctionsTest extends TestCase {
   private final FormState state = new FormStateImpl();
   private final Map changedMap = state.getChangedMap();
   protected Object root;

   protected abstract ScriptFactory newScriptFactory();

   protected ScriptContext getContext(final Object root) throws Exception {
      this.root = root;

      ScriptContext ctx = newScriptFactory().newScript().newContext(root);
      ctx.declare(FormController.CURRENT_STATE_KEY, state);
      ctx.declare(FormController.FORM_METADATA_KEY, getFormMetadata(root));

      return ctx;
   }

   private void primitiveFieldsEmptyTest(final ScriptContext ctx) {
      emptyTester(ctx, "fieldBoolean", false);
      emptyTester(ctx, "fieldByte", false);
      emptyTester(ctx, "fieldChar", false);
      emptyTester(ctx, "fieldDouble", false);
      emptyTester(ctx, "fieldFloat", false);
      emptyTester(ctx, "fieldInteger", false);
      emptyTester(ctx, "fieldLong", false);
      emptyTester(ctx, "fieldShort", false);
   }

   private void objectFieldsEmptyTest(final ScriptContext ctx,
         final boolean isEmpty) {
      emptyTester(ctx, "fieldBooleanWrapper", isEmpty);
      emptyTester(ctx, "fieldByteWrapper", isEmpty);
      emptyTester(ctx, "fieldCharacterWrapper", isEmpty);
      emptyTester(ctx, "fieldDoubleWrapper", isEmpty);
      emptyTester(ctx, "fieldFloatWrapper", isEmpty);
      emptyTester(ctx, "fieldIntegerWrapper", isEmpty);
      emptyTester(ctx, "fieldLongWrapper", isEmpty);
      emptyTester(ctx, "fieldShortWrapper", isEmpty);
      emptyTester(ctx, "fieldString", isEmpty);
      emptyTester(ctx, "fieldStringWithoutTrim", isEmpty);
      emptyTester(ctx, "fieldBigDecimal", isEmpty);
      emptyTester(ctx, "fieldBigInteger", isEmpty);
      emptyTester(ctx, "fieldUtilDate", isEmpty);
      emptyTester(ctx, "fieldSqlDate", isEmpty);
      emptyTester(ctx, "fieldTime", isEmpty);
      emptyTester(ctx, "fieldTimestamp", isEmpty);
      emptyTester(ctx, "fieldObject", isEmpty);
      emptyTester(ctx, "fieldBean", isEmpty);
      arraysAndCollectionsEmptyTest(ctx, isEmpty);
   }

   private void arraysAndCollectionsEmptyTest(final ScriptContext ctx,
         final boolean isEmpty) {
      emptyTester(ctx, "fieldBooleanArray", isEmpty);
      emptyTester(ctx, "fieldBooleanWrapperArray", isEmpty);
      emptyTester(ctx, "fieldByteArray", isEmpty);
      emptyTester(ctx, "fieldByteWrapperArray", isEmpty);
      emptyTester(ctx, "fieldCharArray", isEmpty);
      emptyTester(ctx, "fieldCharacterWrapperArray", isEmpty);
      emptyTester(ctx, "fieldDoubleArray", isEmpty);
      emptyTester(ctx, "fieldDoubleWrapperArray", isEmpty);
      emptyTester(ctx, "fieldFloatArray", isEmpty);
      emptyTester(ctx, "fieldFloatWrapperArray", isEmpty);
      emptyTester(ctx, "fieldIntegerArray", isEmpty);
      emptyTester(ctx, "fieldIntegerWrapperArray", isEmpty);
      emptyTester(ctx, "fieldLongArray", isEmpty);
      emptyTester(ctx, "fieldLongWrapperArray", isEmpty);
      emptyTester(ctx, "fieldShortArray", isEmpty);
      emptyTester(ctx, "fieldShortWrapperArray", isEmpty);
      emptyTester(ctx, "fieldStringArray", isEmpty);
      emptyTester(ctx, "fieldObjectArray", isEmpty);
      emptyTester(ctx, "fieldBeanArray", isEmpty);
      emptyTester(ctx, "fieldCollection", isEmpty);
      emptyTester(ctx, "fieldMap", isEmpty);
   }

   private void nestedPropertyEmptyTest(final ScriptContext ctx,
         final boolean isEmpty) {
      emptyTester(ctx, "fieldBean.name", isEmpty);
   }

   private void constantsEmptyTest(final ScriptContext ctx) {
      emptyConstantsTester(ctx, toScriptString("the"), false);
      emptyConstantsTester(ctx, toScriptString(""), true);
      emptyConstantsTester(ctx, toScriptBoolean(false), false);
      emptyConstantsTester(ctx, toScriptDouble(123), false);
      emptyConstantsTester(ctx, toScriptChar('a'), false);
      emptyConstantsTester(ctx, toScriptByte((byte) 0x01), false);
      emptyConstantsTester(ctx, toScriptShort((short) 1), false);
      emptyConstantsTester(ctx, toScriptInt(2), false);
      emptyConstantsTester(ctx, toScriptLong(3), false);
      emptyConstantsTester(ctx, toScriptFloat(4), false);
   }

   protected abstract String toScriptField(String fieldName);

   protected abstract String toScriptString(String string);

   protected abstract String toScriptBoolean(boolean b);

   protected abstract String toScriptDouble(double b);

   protected abstract String toScriptChar(char c);

   protected abstract String toScriptByte(byte b);

   protected abstract String toScriptShort(short s);

   protected abstract String toScriptInt(int i);

   protected abstract String toScriptLong(long l);

   protected abstract String toScriptFloat(float f);

   protected abstract String toScriptNot(String expression);

   protected abstract String toScriptMethod(String methodName, String expression);

   private void emptyTester(final ScriptContext ctx, final String fieldName,
         final boolean isEmpty) {
      String expr = toScriptMethod("isEmpty", toScriptField(fieldName));
      final boolean result = Boolean.TRUE.equals(ctx.eval(expr));

      assertTrue("The field '" + fieldName
            + (isEmpty ? "' is not empty" : "' is empty") + "; expression: " + 
            expr, isEmpty ? result : (!result));
   }

   private void emptyConstantsTester(final ScriptContext ctx,
         final String constant, final boolean isEmpty) {
      String expr = toScriptMethod("isEmpty", constant);
      final boolean result = Boolean.TRUE.equals(ctx.eval(expr));

      assertTrue("The constant '" + constant
            + (isEmpty ? "' is not empty" : "' is empty") + "; expression: " + 
            expr, isEmpty ? result : (!result));
   }

   private void populateChangedMap() throws Exception {
      changedMap.putAll(PropertyUtils.describe(new TestForm()));
      changedMap.remove("class");
   }

   private void changeTester(final ScriptContext ctx, final String fieldName,
         final boolean changed) {
      String expr = toScriptMethod("hasChanged", toScriptField(fieldName));
      final boolean result = Boolean.TRUE.equals(ctx.eval(expr));

      assertTrue("The field '" + fieldName
            + (changed ? "' has not changed" : "changed")+ "; expression: " + 
            expr, changed ? result : (!result));
   }

   private Map cloneProperties(final TestForm form) throws Exception {
      final Map properties = PropertyUtils.describe(form);
      final Map cloneProperties = new HashMap();

      for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
         final Map.Entry entry = (Map.Entry) iter.next();
         final String fieldName = entry.getKey().toString();

         if (fieldName.endsWith("Clone")) {
            iter.remove();

            continue;
         }

         cloneProperties.put(fieldName + "Clone", entry.getValue());
      }

      PropertyUtils.copyProperties(form, cloneProperties);

      return properties;
   }

   private void equalsTester(final ScriptContext ctx, final String fieldName,
         final boolean isEqual) {
      String expr = toScriptMethod("equals", toScriptField(fieldName) + ','
            + toScriptField(fieldName + "Clone"));
      final boolean result = Boolean.TRUE.equals(ctx.eval(expr));

      assertTrue("The field '" + fieldName
            + (isEqual ? "' is not equal to " : "' is equal to ") + fieldName
            + "Clone; expression: " + expr, isEqual ? result : (!result));
   }

   private void equalsTester(final ScriptContext ctx, final String fieldName,
         final String value, final boolean isEqual) {
      String expr = toScriptMethod("equals", toScriptField(fieldName) + ','
            + value);
      final boolean result = Boolean.TRUE.equals(ctx.eval(expr));

      assertTrue("The field '" + fieldName
            + (isEqual ? "' is not equal to " : "' is equal to ") + value + 
            "; expression: " + expr, isEqual ? result : (!result));
   }

   public void testIsEmptyFunction() throws Exception {
      final TestForm form = new TestForm();
      final ScriptContext ctx = getContext(form);

      // Primitives cannot be empty
      primitiveFieldsEmptyTest(ctx);

      // Objects must be empty
      objectFieldsEmptyTest(ctx, true);

      // Nested property must be empty
      form.setFieldBean(new RegularJavaBean());
      nestedPropertyEmptyTest(ctx, true);

      // Populate All fields
      form.populateAllFields();

      // Objects cannot be empty
      objectFieldsEmptyTest(ctx, false);

      // Nested property must be empty
      form.setFieldBean(new RegularJavaBean());
      nestedPropertyEmptyTest(ctx, true);

      // Populate nested properties
      form.populateNestedProperties();

      // Nested property must not be empty
      nestedPropertyEmptyTest(ctx, false);

      // Set empty arrays, collections and maps
      form.populateWithEmptyArraysAndCollections();

      // Arrays, collections and maps must be empty
      arraysAndCollectionsEmptyTest(ctx, true);

      form.populateStringWithSpaces();
      emptyTester(ctx, "fieldStringWithoutTrim", false);
      emptyTester(ctx, "fieldString", true);

      // Constants
      constantsEmptyTest(ctx);
   }

   public void testHasChangedFunction() throws Exception {
      changedMap.clear();

      final TestForm form = new TestForm();
      final ScriptContext ctx = getContext(form);

      final Map map = PropertyUtils.describe(form);
      map.remove("class");

      final Set keySet = map.keySet();

      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
         changeTester(ctx, iter.next().toString(), false);
      }

      populateChangedMap();

      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
         changeTester(ctx, iter.next().toString(), true);
      }

      changedMap.clear();

      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
         changeTester(ctx, iter.next().toString(), false);
      }
   }

   public void testIsEqualFunction() throws Exception {
      final TestForm form = new TestForm();
      final ScriptContext ctx = getContext(form);

      final Map properties = cloneProperties(form);
      properties.remove("class");

      for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
         equalsTester(ctx, iter.next().toString(), true);
      }

      form.populateAllFields();
      cloneProperties(form);

      for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
         equalsTester(ctx, iter.next().toString(), true);
      }

      form.populateStringWithSpaces();
      equalsTester(ctx, "fieldStringWithoutTrim", false);
      equalsTester(ctx, "fieldString", true);

      form.setFieldStringWithoutTrim("   a ");
      form.setFieldString("   a ");
      equalsTester(ctx, "fieldStringWithoutTrim", toScriptString("a"), false);
      equalsTester(ctx, "fieldStringWithoutTrim", toScriptString("   a "), true);
      equalsTester(ctx, "fieldString", toScriptString("a"), true);
      equalsTester(ctx, "fieldString", toScriptString("   a "), true);
      equalsTester(ctx, "fieldString", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldString", toScriptDouble(123), false);
      equalsTester(ctx, "fieldString", toScriptByte((byte) 0x38), false);
      equalsTester(ctx, "fieldString", toScriptInt(120), false);
      equalsTester(ctx, "fieldString", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldString", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldString", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldString", toScriptChar('y'), false);

      form.setFieldBoolean(false);
      equalsTester(ctx, "fieldBoolean", toScriptBoolean(false), true);
      equalsTester(ctx, "fieldBoolean", toScriptString("trash"), false);
      equalsTester(ctx, "fieldBoolean", toScriptDouble(123), false);
      equalsTester(ctx, "fieldBoolean", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldBoolean", toScriptByte((byte) 0x38), false);
      equalsTester(ctx, "fieldBoolean", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldBoolean", toScriptInt(120), false);
      equalsTester(ctx, "fieldBoolean", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldBoolean", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldBoolean", toScriptNot(toScriptBoolean(true)),
            true);
      equalsTester(ctx, "fieldBoolean", toScriptChar('y'), false);

      form.setFieldDouble(123);
      equalsTester(ctx, "fieldDouble", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldDouble", toScriptString("trash"), false);
      equalsTester(ctx, "fieldDouble", toScriptDouble(123), true);
      equalsTester(ctx, "fieldDouble", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldDouble", toScriptByte((byte) 0x38), false);
      equalsTester(ctx, "fieldDouble", toScriptShort((short) 110), false);
      equalsTester(ctx, "fieldDouble", toScriptInt(120), false);
      equalsTester(ctx, "fieldDouble", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldDouble", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldDouble", toScriptDouble(133) + " - "
            + toScriptDouble(10), true);
      equalsTester(ctx, "fieldDouble", toScriptChar('y'), false);

      form.setFieldChar('y');
      equalsTester(ctx, "fieldChar", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldChar", toScriptString("trash"), false);
      equalsTester(ctx, "fieldChar", toScriptDouble(123), false);
      equalsTester(ctx, "fieldChar", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldChar", toScriptByte((byte) 0x38), false);
      equalsTester(ctx, "fieldChar", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldChar", toScriptInt(120), false);
      equalsTester(ctx, "fieldChar", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldChar", toScriptChar('b'), false);
      equalsTester(ctx, "fieldChar", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldChar", toScriptChar('y'), true);

      form.setFieldByte((byte) 0x38);
      equalsTester(ctx, "fieldByte", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldByte", toScriptString("trash"), false);
      equalsTester(ctx, "fieldByte", toScriptDouble(123), false);
      equalsTester(ctx, "fieldByte", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldByte", toScriptChar('b'), false);
      equalsTester(ctx, "fieldByte", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldByte", toScriptInt(120), false);
      equalsTester(ctx, "fieldByte", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldByte", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldByte", toScriptByte((byte) 0x18), false);
      equalsTester(ctx, "fieldByte", toScriptByte((byte) 0x38), true);

      form.setFieldShort((short) 120);
      equalsTester(ctx, "fieldShort", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldShort", toScriptString("trash"), false);
      equalsTester(ctx, "fieldShort", toScriptDouble(123), false);
      equalsTester(ctx, "fieldShort", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldShort", toScriptChar('b'), false);
      equalsTester(ctx, "fieldShort", toScriptByte((byte) 0x18), false);
      equalsTester(ctx, "fieldShort", toScriptInt(120), false);
      equalsTester(ctx, "fieldShort", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldShort", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldShort", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldShort", toScriptShort((short) 120), true);

      form.setFieldInteger(382);
      equalsTester(ctx, "fieldInteger", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldInteger", toScriptString("trash"), false);
      equalsTester(ctx, "fieldInteger", toScriptDouble(123), false);
      equalsTester(ctx, "fieldInteger", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldInteger", toScriptChar('b'), false);
      equalsTester(ctx, "fieldInteger", toScriptByte((byte) 0x18), false);
      equalsTester(ctx, "fieldInteger", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldInteger", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldInteger", toScriptInt(120), false);
      equalsTester(ctx, "fieldInteger", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldInteger", toScriptInt(382), true);

      form.setFieldLong(2340242342L);
      equalsTester(ctx, "fieldLong", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldLong", toScriptString("trash"), false);
      equalsTester(ctx, "fieldLong", toScriptDouble(123), false);
      equalsTester(ctx, "fieldLong", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldLong", toScriptChar('b'), false);
      equalsTester(ctx, "fieldLong", toScriptByte((byte) 0x18), false);
      equalsTester(ctx, "fieldLong", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldLong", toScriptInt(120), false);
      equalsTester(ctx, "fieldLong", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldLong", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldLong", toScriptLong(2340242342L), true);

      form.setFieldFloat(284324);
      equalsTester(ctx, "fieldFloat", toScriptBoolean(false), false);
      equalsTester(ctx, "fieldFloat", toScriptString("trash"), false);
      equalsTester(ctx, "fieldFloat", toScriptDouble(123), false);
      equalsTester(ctx, "fieldFloat", toScriptBoolean(true), false);
      equalsTester(ctx, "fieldFloat", toScriptChar('b'), false);
      equalsTester(ctx, "fieldFloat", toScriptByte((byte) 0x18), false);
      equalsTester(ctx, "fieldFloat", toScriptShort((short) 123), false);
      equalsTester(ctx, "fieldFloat", toScriptInt(120), false);
      equalsTester(ctx, "fieldFloat", toScriptLong(23432423111111L), false);
      equalsTester(ctx, "fieldFloat", toScriptFloat(2324), false);
      equalsTester(ctx, "fieldFloat", toScriptFloat(284324), true);
   }

   /**
    * @Form
    */
   public static class TestForm {
      private boolean fieldBoolean;
      private Boolean fieldBooleanWrapper;
      private boolean[] fieldBooleanArray;
      private Boolean[] fieldBooleanWrapperArray;
      private byte fieldByte;
      private Byte fieldByteWrapper;
      private byte[] fieldByteArray;
      private Byte[] fieldByteWrapperArray;
      private char fieldChar;
      private Character fieldCharacterWrapper;
      private char[] fieldCharArray;
      private Character[] fieldCharacterWrapperArray;
      private double fieldDouble;
      private Double fieldDoubleWrapper;
      private double[] fieldDoubleArray;
      private Double[] fieldDoubleWrapperArray;
      private float fieldFloat;
      private Float fieldFloatWrapper;
      private float[] fieldFloatArray;
      private Float[] fieldFloatWrapperArray;
      private int fieldInteger;
      private Integer fieldIntegerWrapper;
      private int[] fieldIntegerArray;
      private Integer[] fieldIntegerWrapperArray;
      private long fieldLong;
      private Long fieldLongWrapper;
      private long[] fieldLongArray;
      private Long[] fieldLongWrapperArray;
      private short fieldShort;
      private Short fieldShortWrapper;
      private short[] fieldShortArray;
      private Short[] fieldShortWrapperArray;
      private String fieldString;
      private String[] fieldStringArray;
      private String fieldStringWithoutTrim;
      private BigDecimal fieldBigDecimal;
      private BigInteger fieldBigInteger;
      private java.util.Date fieldUtilDate;
      private java.sql.Date fieldSqlDate;
      private Time fieldTime;
      private Timestamp fieldTimestamp;
      private Object fieldObject;
      private Object[] fieldObjectArray;
      private RegularJavaBean fieldBean;
      private RegularJavaBean[] fieldBeanArray;
      private Collection fieldCollection;
      private Map fieldMap;
      boolean fieldBooleanClone;
      Boolean fieldBooleanWrapperClone;
      boolean[] fieldBooleanArrayClone;
      Boolean[] fieldBooleanWrapperArrayClone;
      byte fieldByteClone;
      Byte fieldByteWrapperClone;
      byte[] fieldByteArrayClone;
      Byte[] fieldByteWrapperArrayClone;
      char fieldCharClone;
      Character fieldCharacterWrapperClone;
      char[] fieldCharArrayClone;
      Character[] fieldCharacterWrapperArrayClone;
      double fieldDoubleClone;
      Double fieldDoubleWrapperClone;
      double[] fieldDoubleArrayClone;
      Double[] fieldDoubleWrapperArrayClone;
      float fieldFloatClone;
      Float fieldFloatWrapperClone;
      float[] fieldFloatArrayClone;
      Float[] fieldFloatWrapperArrayClone;
      int fieldIntegerClone;
      Integer fieldIntegerWrapperClone;
      int[] fieldIntegerArrayClone;
      Integer[] fieldIntegerWrapperArrayClone;
      long fieldLongClone;
      Long fieldLongWrapperClone;
      long[] fieldLongArrayClone;
      Long[] fieldLongWrapperArrayClone;
      short fieldShortClone;
      Short fieldShortWrapperClone;
      short[] fieldShortArrayClone;
      Short[] fieldShortWrapperArrayClone;
      String fieldStringClone;
      String[] fieldStringArrayClone;
      String fieldStringWithoutTrimClone;
      BigDecimal fieldBigDecimalClone;
      BigInteger fieldBigIntegerClone;
      Date fieldUtilDateClone;
      java.sql.Date fieldSqlDateClone;
      Time fieldTimeClone;
      Timestamp fieldTimestampClone;
      Object fieldObjectClone;
      Object[] fieldObjectArrayClone;
      RegularJavaBean fieldBeanClone;
      RegularJavaBean[] fieldBeanArrayClone;
      Collection fieldCollectionClone;
      Map fieldMapClone;

      public RegularJavaBean getFieldBean() {
         return fieldBean;
      }

      public void setFieldBean(RegularJavaBean fieldBean) {
         this.fieldBean = fieldBean;
      }

      public RegularJavaBean[] getFieldBeanArray() {
         return fieldBeanArray;
      }

      public void setFieldBeanArray(RegularJavaBean[] fieldBeanArray) {
         this.fieldBeanArray = fieldBeanArray;
      }

      public BigDecimal getFieldBigDecimal() {
         return fieldBigDecimal;
      }

      public void setFieldBigDecimal(BigDecimal fieldBigDecimal) {
         this.fieldBigDecimal = fieldBigDecimal;
      }

      public BigInteger getFieldBigInteger() {
         return fieldBigInteger;
      }

      public void setFieldBigInteger(BigInteger fieldBigInteger) {
         this.fieldBigInteger = fieldBigInteger;
      }

      public boolean isFieldBoolean() {
         return fieldBoolean;
      }

      public void setFieldBoolean(boolean fieldBoolean) {
         this.fieldBoolean = fieldBoolean;
      }

      public boolean[] getFieldBooleanArray() {
         return fieldBooleanArray;
      }

      public void setFieldBooleanArray(boolean[] fieldBooleanArray) {
         this.fieldBooleanArray = fieldBooleanArray;
      }

      public Boolean getFieldBooleanWrapper() {
         return fieldBooleanWrapper;
      }

      public void setFieldBooleanWrapper(Boolean fieldBooleanWrapper) {
         this.fieldBooleanWrapper = fieldBooleanWrapper;
      }

      public Boolean[] getFieldBooleanWrapperArray() {
         return fieldBooleanWrapperArray;
      }

      public void setFieldBooleanWrapperArray(Boolean[] fieldBooleanWrapperArray) {
         this.fieldBooleanWrapperArray = fieldBooleanWrapperArray;
      }

      public byte getFieldByte() {
         return fieldByte;
      }

      public void setFieldByte(byte fieldByte) {
         this.fieldByte = fieldByte;
      }

      public byte[] getFieldByteArray() {
         return fieldByteArray;
      }

      public void setFieldByteArray(byte[] fieldByteArray) {
         this.fieldByteArray = fieldByteArray;
      }

      public Byte getFieldByteWrapper() {
         return fieldByteWrapper;
      }

      public void setFieldByteWrapper(Byte fieldByteWrapper) {
         this.fieldByteWrapper = fieldByteWrapper;
      }

      public Byte[] getFieldByteWrapperArray() {
         return fieldByteWrapperArray;
      }

      public void setFieldByteWrapperArray(Byte[] fieldByteWrapperArray) {
         this.fieldByteWrapperArray = fieldByteWrapperArray;
      }

      public char getFieldChar() {
         return fieldChar;
      }

      public void setFieldChar(char fieldChar) {
         this.fieldChar = fieldChar;
      }

      public Character getFieldCharacterWrapper() {
         return fieldCharacterWrapper;
      }

      public void setFieldCharacterWrapper(Character fieldCharacterWrapper) {
         this.fieldCharacterWrapper = fieldCharacterWrapper;
      }

      public Character[] getFieldCharacterWrapperArray() {
         return fieldCharacterWrapperArray;
      }

      public void setFieldCharacterWrapperArray(
            Character[] fieldCharacterWrapperArray) {
         this.fieldCharacterWrapperArray = fieldCharacterWrapperArray;
      }

      public char[] getFieldCharArray() {
         return fieldCharArray;
      }

      public void setFieldCharArray(char[] fieldCharArray) {
         this.fieldCharArray = fieldCharArray;
      }

      public Collection getFieldCollection() {
         return fieldCollection;
      }

      public void setFieldCollection(Collection fieldCollection) {
         this.fieldCollection = fieldCollection;
      }

      public double getFieldDouble() {
         return fieldDouble;
      }

      public void setFieldDouble(double fieldDouble) {
         this.fieldDouble = fieldDouble;
      }

      public double[] getFieldDoubleArray() {
         return fieldDoubleArray;
      }

      public void setFieldDoubleArray(double[] fieldDoubleArray) {
         this.fieldDoubleArray = fieldDoubleArray;
      }

      public Double getFieldDoubleWrapper() {
         return fieldDoubleWrapper;
      }

      public void setFieldDoubleWrapper(Double fieldDoubleWrapper) {
         this.fieldDoubleWrapper = fieldDoubleWrapper;
      }

      public Double[] getFieldDoubleWrapperArray() {
         return fieldDoubleWrapperArray;
      }

      public void setFieldDoubleWrapperArray(Double[] fieldDoubleWrapperArray) {
         this.fieldDoubleWrapperArray = fieldDoubleWrapperArray;
      }

      public float getFieldFloat() {
         return fieldFloat;
      }

      public void setFieldFloat(float fieldFloat) {
         this.fieldFloat = fieldFloat;
      }

      public float[] getFieldFloatArray() {
         return fieldFloatArray;
      }

      public void setFieldFloatArray(float[] fieldFloatArray) {
         this.fieldFloatArray = fieldFloatArray;
      }

      public Float getFieldFloatWrapper() {
         return fieldFloatWrapper;
      }

      public void setFieldFloatWrapper(Float fieldFloatWrapper) {
         this.fieldFloatWrapper = fieldFloatWrapper;
      }

      public Float[] getFieldFloatWrapperArray() {
         return fieldFloatWrapperArray;
      }

      public void setFieldFloatWrapperArray(Float[] fieldFloatWrapperArray) {
         this.fieldFloatWrapperArray = fieldFloatWrapperArray;
      }

      public int getFieldInteger() {
         return fieldInteger;
      }

      public void setFieldInteger(int fieldInteger) {
         this.fieldInteger = fieldInteger;
      }

      public int[] getFieldIntegerArray() {
         return fieldIntegerArray;
      }

      public void setFieldIntegerArray(int[] fieldIntegerArray) {
         this.fieldIntegerArray = fieldIntegerArray;
      }

      public Integer getFieldIntegerWrapper() {
         return fieldIntegerWrapper;
      }

      public void setFieldIntegerWrapper(Integer fieldIntegerWrapper) {
         this.fieldIntegerWrapper = fieldIntegerWrapper;
      }

      public Integer[] getFieldIntegerWrapperArray() {
         return fieldIntegerWrapperArray;
      }

      public void setFieldIntegerWrapperArray(Integer[] fieldIntegerWrapperArray) {
         this.fieldIntegerWrapperArray = fieldIntegerWrapperArray;
      }

      public long getFieldLong() {
         return fieldLong;
      }

      public void setFieldLong(long fieldLong) {
         this.fieldLong = fieldLong;
      }

      public long[] getFieldLongArray() {
         return fieldLongArray;
      }

      public void setFieldLongArray(long[] fieldLongArray) {
         this.fieldLongArray = fieldLongArray;
      }

      public Long getFieldLongWrapper() {
         return fieldLongWrapper;
      }

      public void setFieldLongWrapper(Long fieldLongWrapper) {
         this.fieldLongWrapper = fieldLongWrapper;
      }

      public Long[] getFieldLongWrapperArray() {
         return fieldLongWrapperArray;
      }

      public void setFieldLongWrapperArray(Long[] fieldLongWrapperArray) {
         this.fieldLongWrapperArray = fieldLongWrapperArray;
      }

      public Map getFieldMap() {
         return fieldMap;
      }

      public void setFieldMap(Map fieldMap) {
         this.fieldMap = fieldMap;
      }

      public Object getFieldObject() {
         return fieldObject;
      }

      public void setFieldObject(Object fieldObject) {
         this.fieldObject = fieldObject;
      }

      public Object[] getFieldObjectArray() {
         return fieldObjectArray;
      }

      public void setFieldObjectArray(Object[] fieldObjectArray) {
         this.fieldObjectArray = fieldObjectArray;
      }

      public short getFieldShort() {
         return fieldShort;
      }

      public void setFieldShort(short fieldShort) {
         this.fieldShort = fieldShort;
      }

      public short[] getFieldShortArray() {
         return fieldShortArray;
      }

      public void setFieldShortArray(short[] fieldShortArray) {
         this.fieldShortArray = fieldShortArray;
      }

      public Short getFieldShortWrapper() {
         return fieldShortWrapper;
      }

      public void setFieldShortWrapper(Short fieldShortWrapper) {
         this.fieldShortWrapper = fieldShortWrapper;
      }

      public Short[] getFieldShortWrapperArray() {
         return fieldShortWrapperArray;
      }

      public void setFieldShortWrapperArray(Short[] fieldShortWrapperArray) {
         this.fieldShortWrapperArray = fieldShortWrapperArray;
      }

      public java.sql.Date getFieldSqlDate() {
         return fieldSqlDate;
      }

      public void setFieldSqlDate(java.sql.Date fieldSqlDate) {
         this.fieldSqlDate = fieldSqlDate;
      }

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) {
         this.fieldString = fieldString;
      }

      public String[] getFieldStringArray() {
         return fieldStringArray;
      }

      public void setFieldStringArray(String[] fieldStringArray) {
         this.fieldStringArray = fieldStringArray;
      }

      /**
       * @EmptyResolver class=net.java.dev.genesis.resolvers.StringEmptyResolver
       *                trim=false
       * @EqualityComparator class=net.java.dev.genesis.equality.StringEqualityComparator
       *                     trim=false
       */
      public String getFieldStringWithoutTrim() {
         return fieldStringWithoutTrim;
      }

      public void setFieldStringWithoutTrim(String fieldStringWithoutTrim) {
         this.fieldStringWithoutTrim = fieldStringWithoutTrim;
      }

      public Time getFieldTime() {
         return fieldTime;
      }

      public void setFieldTime(Time fieldTime) {
         this.fieldTime = fieldTime;
      }

      public Timestamp getFieldTimestamp() {
         return fieldTimestamp;
      }

      public void setFieldTimestamp(Timestamp fieldTimestamp) {
         this.fieldTimestamp = fieldTimestamp;
      }

      public java.util.Date getFieldUtilDate() {
         return fieldUtilDate;
      }

      public void setFieldUtilDate(java.util.Date fieldUtilDate) {
         this.fieldUtilDate = fieldUtilDate;
      }

      public RegularJavaBean[] getFieldBeanArrayClone() {
         return fieldBeanArrayClone;
      }

      public void setFieldBeanArrayClone(RegularJavaBean[] fieldBeanArrayClone) {
         this.fieldBeanArrayClone = fieldBeanArrayClone;
      }

      public RegularJavaBean getFieldBeanClone() {
         return fieldBeanClone;
      }

      public void setFieldBeanClone(RegularJavaBean fieldBeanClone) {
         this.fieldBeanClone = fieldBeanClone;
      }

      public BigDecimal getFieldBigDecimalClone() {
         return fieldBigDecimalClone;
      }

      public void setFieldBigDecimalClone(BigDecimal fieldBigDecimalClone) {
         this.fieldBigDecimalClone = fieldBigDecimalClone;
      }

      public BigInteger getFieldBigIntegerClone() {
         return fieldBigIntegerClone;
      }

      public void setFieldBigIntegerClone(BigInteger fieldBigIntegerClone) {
         this.fieldBigIntegerClone = fieldBigIntegerClone;
      }

      public boolean[] getFieldBooleanArrayClone() {
         return fieldBooleanArrayClone;
      }

      public void setFieldBooleanArrayClone(boolean[] fieldBooleanArrayClone) {
         this.fieldBooleanArrayClone = fieldBooleanArrayClone;
      }

      public boolean isFieldBooleanClone() {
         return fieldBooleanClone;
      }

      public void setFieldBooleanClone(boolean fieldBooleanClone) {
         this.fieldBooleanClone = fieldBooleanClone;
      }

      public Boolean[] getFieldBooleanWrapperArrayClone() {
         return fieldBooleanWrapperArrayClone;
      }

      public void setFieldBooleanWrapperArrayClone(
            Boolean[] fieldBooleanWrapperArrayClone) {
         this.fieldBooleanWrapperArrayClone = fieldBooleanWrapperArrayClone;
      }

      public Boolean getFieldBooleanWrapperClone() {
         return fieldBooleanWrapperClone;
      }

      public void setFieldBooleanWrapperClone(Boolean fieldBooleanWrapperClone) {
         this.fieldBooleanWrapperClone = fieldBooleanWrapperClone;
      }

      public byte[] getFieldByteArrayClone() {
         return fieldByteArrayClone;
      }

      public void setFieldByteArrayClone(byte[] fieldByteArrayClone) {
         this.fieldByteArrayClone = fieldByteArrayClone;
      }

      public byte getFieldByteClone() {
         return fieldByteClone;
      }

      public void setFieldByteClone(byte fieldByteClone) {
         this.fieldByteClone = fieldByteClone;
      }

      public Byte[] getFieldByteWrapperArrayClone() {
         return fieldByteWrapperArrayClone;
      }

      public void setFieldByteWrapperArrayClone(
            Byte[] fieldByteWrapperArrayClone) {
         this.fieldByteWrapperArrayClone = fieldByteWrapperArrayClone;
      }

      public Byte getFieldByteWrapperClone() {
         return fieldByteWrapperClone;
      }

      public void setFieldByteWrapperClone(Byte fieldByteWrapperClone) {
         this.fieldByteWrapperClone = fieldByteWrapperClone;
      }

      public Character[] getFieldCharacterWrapperArrayClone() {
         return fieldCharacterWrapperArrayClone;
      }

      public void setFieldCharacterWrapperArrayClone(
            Character[] fieldCharacterWrapperArrayClone) {
         this.fieldCharacterWrapperArrayClone = fieldCharacterWrapperArrayClone;
      }

      public Character getFieldCharacterWrapperClone() {
         return fieldCharacterWrapperClone;
      }

      public void setFieldCharacterWrapperClone(
            Character fieldCharacterWrapperClone) {
         this.fieldCharacterWrapperClone = fieldCharacterWrapperClone;
      }

      public char[] getFieldCharArrayClone() {
         return fieldCharArrayClone;
      }

      public void setFieldCharArrayClone(char[] fieldCharArrayClone) {
         this.fieldCharArrayClone = fieldCharArrayClone;
      }

      public char getFieldCharClone() {
         return fieldCharClone;
      }

      public void setFieldCharClone(char fieldCharClone) {
         this.fieldCharClone = fieldCharClone;
      }

      public Collection getFieldCollectionClone() {
         return fieldCollectionClone;
      }

      public void setFieldCollectionClone(Collection fieldCollectionClone) {
         this.fieldCollectionClone = fieldCollectionClone;
      }

      public double[] getFieldDoubleArrayClone() {
         return fieldDoubleArrayClone;
      }

      public void setFieldDoubleArrayClone(double[] fieldDoubleArrayClone) {
         this.fieldDoubleArrayClone = fieldDoubleArrayClone;
      }

      public double getFieldDoubleClone() {
         return fieldDoubleClone;
      }

      public void setFieldDoubleClone(double fieldDoubleClone) {
         this.fieldDoubleClone = fieldDoubleClone;
      }

      public Double[] getFieldDoubleWrapperArrayClone() {
         return fieldDoubleWrapperArrayClone;
      }

      public void setFieldDoubleWrapperArrayClone(
            Double[] fieldDoubleWrapperArrayClone) {
         this.fieldDoubleWrapperArrayClone = fieldDoubleWrapperArrayClone;
      }

      public Double getFieldDoubleWrapperClone() {
         return fieldDoubleWrapperClone;
      }

      public void setFieldDoubleWrapperClone(Double fieldDoubleWrapperClone) {
         this.fieldDoubleWrapperClone = fieldDoubleWrapperClone;
      }

      public float[] getFieldFloatArrayClone() {
         return fieldFloatArrayClone;
      }

      public void setFieldFloatArrayClone(float[] fieldFloatArrayClone) {
         this.fieldFloatArrayClone = fieldFloatArrayClone;
      }

      public float getFieldFloatClone() {
         return fieldFloatClone;
      }

      public void setFieldFloatClone(float fieldFloatClone) {
         this.fieldFloatClone = fieldFloatClone;
      }

      public Float[] getFieldFloatWrapperArrayClone() {
         return fieldFloatWrapperArrayClone;
      }

      public void setFieldFloatWrapperArrayClone(
            Float[] fieldFloatWrapperArrayClone) {
         this.fieldFloatWrapperArrayClone = fieldFloatWrapperArrayClone;
      }

      public Float getFieldFloatWrapperClone() {
         return fieldFloatWrapperClone;
      }

      public void setFieldFloatWrapperClone(Float fieldFloatWrapperClone) {
         this.fieldFloatWrapperClone = fieldFloatWrapperClone;
      }

      public int[] getFieldIntegerArrayClone() {
         return fieldIntegerArrayClone;
      }

      public void setFieldIntegerArrayClone(int[] fieldIntegerArrayClone) {
         this.fieldIntegerArrayClone = fieldIntegerArrayClone;
      }

      public int getFieldIntegerClone() {
         return fieldIntegerClone;
      }

      public void setFieldIntegerClone(int fieldIntegerClone) {
         this.fieldIntegerClone = fieldIntegerClone;
      }

      public Integer[] getFieldIntegerWrapperArrayClone() {
         return fieldIntegerWrapperArrayClone;
      }

      public void setFieldIntegerWrapperArrayClone(
            Integer[] fieldIntegerWrapperArrayClone) {
         this.fieldIntegerWrapperArrayClone = fieldIntegerWrapperArrayClone;
      }

      public Integer getFieldIntegerWrapperClone() {
         return fieldIntegerWrapperClone;
      }

      public void setFieldIntegerWrapperClone(Integer fieldIntegerWrapperClone) {
         this.fieldIntegerWrapperClone = fieldIntegerWrapperClone;
      }

      public long[] getFieldLongArrayClone() {
         return fieldLongArrayClone;
      }

      public void setFieldLongArrayClone(long[] fieldLongArrayClone) {
         this.fieldLongArrayClone = fieldLongArrayClone;
      }

      public long getFieldLongClone() {
         return fieldLongClone;
      }

      public void setFieldLongClone(long fieldLongClone) {
         this.fieldLongClone = fieldLongClone;
      }

      public Long[] getFieldLongWrapperArrayClone() {
         return fieldLongWrapperArrayClone;
      }

      public void setFieldLongWrapperArrayClone(
            Long[] fieldLongWrapperArrayClone) {
         this.fieldLongWrapperArrayClone = fieldLongWrapperArrayClone;
      }

      public Long getFieldLongWrapperClone() {
         return fieldLongWrapperClone;
      }

      public void setFieldLongWrapperClone(Long fieldLongWrapperClone) {
         this.fieldLongWrapperClone = fieldLongWrapperClone;
      }

      public Map getFieldMapClone() {
         return fieldMapClone;
      }

      public void setFieldMapClone(Map fieldMapClone) {
         this.fieldMapClone = fieldMapClone;
      }

      public Object[] getFieldObjectArrayClone() {
         return fieldObjectArrayClone;
      }

      public void setFieldObjectArrayClone(Object[] fieldObjectArrayClone) {
         this.fieldObjectArrayClone = fieldObjectArrayClone;
      }

      public Object getFieldObjectClone() {
         return fieldObjectClone;
      }

      public void setFieldObjectClone(Object fieldObjectClone) {
         this.fieldObjectClone = fieldObjectClone;
      }

      public short[] getFieldShortArrayClone() {
         return fieldShortArrayClone;
      }

      public void setFieldShortArrayClone(short[] fieldShortArrayClone) {
         this.fieldShortArrayClone = fieldShortArrayClone;
      }

      public short getFieldShortClone() {
         return fieldShortClone;
      }

      public void setFieldShortClone(short fieldShortClone) {
         this.fieldShortClone = fieldShortClone;
      }

      public Short[] getFieldShortWrapperArrayClone() {
         return fieldShortWrapperArrayClone;
      }

      public void setFieldShortWrapperArrayClone(
            Short[] fieldShortWrapperArrayClone) {
         this.fieldShortWrapperArrayClone = fieldShortWrapperArrayClone;
      }

      public Short getFieldShortWrapperClone() {
         return fieldShortWrapperClone;
      }

      public void setFieldShortWrapperClone(Short fieldShortWrapperClone) {
         this.fieldShortWrapperClone = fieldShortWrapperClone;
      }

      public java.sql.Date getFieldSqlDateClone() {
         return fieldSqlDateClone;
      }

      public void setFieldSqlDateClone(java.sql.Date fieldSqlDateClone) {
         this.fieldSqlDateClone = fieldSqlDateClone;
      }

      public String[] getFieldStringArrayClone() {
         return fieldStringArrayClone;
      }

      public void setFieldStringArrayClone(String[] fieldStringArrayClone) {
         this.fieldStringArrayClone = fieldStringArrayClone;
      }

      public String getFieldStringClone() {
         return fieldStringClone;
      }

      public void setFieldStringClone(String fieldStringClone) {
         this.fieldStringClone = fieldStringClone;
      }

      public String getFieldStringWithoutTrimClone() {
         return fieldStringWithoutTrimClone;
      }

      public void setFieldStringWithoutTrimClone(
            String fieldStringWithoutTrimClone) {
         this.fieldStringWithoutTrimClone = fieldStringWithoutTrimClone;
      }

      public Time getFieldTimeClone() {
         return fieldTimeClone;
      }

      public void setFieldTimeClone(Time fieldTimeClone) {
         this.fieldTimeClone = fieldTimeClone;
      }

      public Timestamp getFieldTimestampClone() {
         return fieldTimestampClone;
      }

      public void setFieldTimestampClone(Timestamp fieldTimestampClone) {
         this.fieldTimestampClone = fieldTimestampClone;
      }

      public Date getFieldUtilDateClone() {
         return fieldUtilDateClone;
      }

      public void setFieldUtilDateClone(Date fieldUtilDateClone) {
         this.fieldUtilDateClone = fieldUtilDateClone;
      }

      public void populateAllFields() {
         setFieldBooleanWrapper(Boolean.FALSE);
         setFieldByteWrapper(new Byte(Byte.MIN_VALUE));
         setFieldCharacterWrapper(new Character(Character.MIN_VALUE));
         setFieldDoubleWrapper(new Double(Double.MIN_VALUE));
         setFieldFloatWrapper(new Float(Float.MIN_VALUE));
         setFieldIntegerWrapper(new Integer(Integer.MIN_VALUE));
         setFieldLongWrapper(new Long(Long.MIN_VALUE));
         setFieldShortWrapper(new Short(Short.MIN_VALUE));
         setFieldString("string");
         setFieldStringWithoutTrim("string");
         setFieldBigDecimal(new BigDecimal("1234567890.1234567890"));
         setFieldBigInteger(new BigInteger("12345678901234567890"));
         setFieldUtilDate(new Date());
         setFieldSqlDate(new java.sql.Date(System.currentTimeMillis()));
         setFieldTime(new Time(System.currentTimeMillis()));
         setFieldTimestamp(new Timestamp(System.currentTimeMillis()));
         setFieldObject(new Object());
         setFieldBean(new RegularJavaBean());
         populateArraysAndCollections();
      }

      public void populateNestedProperties() {
         getFieldBean().setName("name");
      }

      public void populateArraysAndCollections() {
         setFieldBooleanArray(new boolean[] { false, true });
         setFieldBooleanWrapperArray(new Boolean[] { Boolean.FALSE,
               Boolean.TRUE });
         setFieldByteArray(new byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE });
         setFieldByteWrapperArray(new Byte[] { new Byte(Byte.MIN_VALUE),
               new Byte(Byte.MAX_VALUE) });
         setFieldCharArray(new char[] { Character.MIN_VALUE,
               Character.MAX_VALUE });
         setFieldCharacterWrapperArray(new Character[] {
               new Character(Character.MIN_VALUE),
               new Character(Character.MAX_VALUE) });
         setFieldDoubleArray(new double[] { Double.MIN_VALUE, Double.MAX_VALUE });
         setFieldDoubleWrapperArray(new Double[] {
               new Double(Double.MIN_VALUE), new Double(Double.MAX_VALUE) });
         setFieldFloatArray(new float[] { Float.MIN_VALUE, Float.MAX_VALUE });
         setFieldFloatWrapperArray(new Float[] { new Float(Float.MIN_VALUE),
               new Float(Float.MAX_VALUE) });
         setFieldIntegerArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE });
         setFieldIntegerWrapperArray(new Integer[] {
               new Integer(Integer.MIN_VALUE), new Integer(Integer.MAX_VALUE) });
         setFieldLongArray(new long[] { Long.MIN_VALUE, Long.MAX_VALUE });
         setFieldLongWrapperArray(new Long[] { new Long(Long.MIN_VALUE),
               new Long(Long.MAX_VALUE) });
         setFieldShortArray(new short[] { Short.MIN_VALUE, Short.MAX_VALUE });
         setFieldShortWrapperArray(new Short[] { new Short(Short.MIN_VALUE),
               new Short(Short.MAX_VALUE) });
         setFieldStringArray(new String[] { "string[0]", "string[1]" });
         setFieldObjectArray(new Object[] { new Object(), new Object() });
         setFieldBeanArray(new RegularJavaBean[] { new RegularJavaBean(),
               new RegularJavaBean() });
         setFieldCollection(Collections.singleton(new Object()));
         setFieldMap(Collections.singletonMap("key", new Object()));
      }

      public void populateWithEmptyArraysAndCollections() {
         setFieldBooleanArray(new boolean[0]);
         setFieldBooleanWrapperArray(new Boolean[0]);
         setFieldByteArray(new byte[0]);
         setFieldByteWrapperArray(new Byte[0]);
         setFieldCharArray(new char[0]);
         setFieldCharacterWrapperArray(new Character[0]);
         setFieldDoubleArray(new double[0]);
         setFieldDoubleWrapperArray(new Double[0]);
         setFieldFloatArray(new float[0]);
         setFieldFloatWrapperArray(new Float[0]);
         setFieldIntegerArray(new int[0]);
         setFieldIntegerWrapperArray(new Integer[0]);
         setFieldLongArray(new long[0]);
         setFieldLongWrapperArray(new Long[0]);
         setFieldShortArray(new short[0]);
         setFieldShortWrapperArray(new Short[0]);
         setFieldStringArray(new String[0]);
         setFieldObjectArray(new Object[0]);
         setFieldBeanArray(new RegularJavaBean[0]);
         setFieldCollection(Collections.EMPTY_SET);
         setFieldMap(Collections.EMPTY_MAP);
      }

      public void populateStringWithSpaces() {
         setFieldString(" ");
         fieldStringClone = "  ";
         setFieldStringWithoutTrim(" ");
         fieldStringWithoutTrimClone = "  ";
      }
   }

   public static final class RegularJavaBean {
      private String name;

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }
   }
}
