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

import net.java.dev.genesis.tests.TestCase;
import net.java.dev.genesis.ui.controller.DefaultFormController;
import net.java.dev.genesis.ui.controller.FormController;
import net.java.dev.genesis.ui.controller.FormState;
import net.java.dev.genesis.ui.controller.FormStateImpl;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

public class FormControllerTest extends TestCase {

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   private Map getSomeValues() throws Exception {
      final Map newValues = BeanUtils.describe(new FooForm());

      final boolean fieldBoolean = true;
      final char fieldChar = 'Z';
      final byte fieldByte = (byte) 0xFE;
      final short fieldShort = 125;
      final int fieldInt = 3452342;
      final long fieldLong = 123721281238L;
      final double fieldDouble = 23423.23429;
      final float fieldFloat = 234234234234.43535F;
      final Boolean fieldBooleanWrapper = Boolean.TRUE;
      final Character fieldCharacterWrapper = new Character('w');
      final Byte fieldByteWrapper = new Byte((byte) 0xCA);
      final Short fieldShortWrapper = new Short((short) 115);
      final Integer fieldIntegerWrapper = new Integer(23423423);
      final Long fieldLongWrapper = new Long(34223409813244L);
      final Double fieldDoubleWrapper = new Double(234234423.000123);
      final Float fieldFloatWrapper = new Float(
            445095043543435421321333.434344324233F);
      final String fieldString = "aisfBCASADr";

      newValues.put("fieldBoolean", Boolean.toString(fieldBoolean));
      newValues.put("fieldChar", Character.toString(fieldChar));
      newValues.put("fieldByte", Byte.toString(fieldByte));
      newValues.put("fieldShort", Short.toString(fieldShort));
      newValues.put("fieldInt", Integer.toString(fieldInt));
      newValues.put("fieldLong", Long.toString(fieldLong));
      newValues.put("fieldDouble", Double.toString(fieldDouble));
      newValues.put("fieldFloat", Float.toString(fieldFloat));
      newValues.put("fieldBooleanWrapper", fieldBooleanWrapper.toString());
      newValues.put("fieldCharacterWrapper", fieldCharacterWrapper.toString());
      newValues.put("fieldByteWrapper", fieldByteWrapper.toString());
      newValues.put("fieldShortWrapper", fieldShortWrapper.toString());
      newValues.put("fieldIntegerWrapper", fieldIntegerWrapper.toString());
      newValues.put("fieldLongWrapper", fieldLongWrapper.toString());
      newValues.put("fieldDoubleWrapper", fieldDoubleWrapper.toString());
      newValues.put("fieldFloatWrapper", fieldFloatWrapper.toString());
      newValues.put("fieldString", fieldString.toString());
      newValues.put("fieldBigDecimal", "234423,423234");

      return newValues;
   }

   public void testNoopPopulate() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);
      final Map describedMap = BeanUtils.describe(someForm);
      controller.populate(describedMap);
      final Map afterPopulateMap = BeanUtils.describe(someForm);
      assertDescribedMapEquals(describedMap, afterPopulateMap);
   }

   public void testSimplePopulate() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);

      final Map someValues = getSomeValues();
      controller.populate(someValues);
      final Map newValuesAfterPopulate = BeanUtils.describe(someForm);
      newValuesAfterPopulate.put("fieldBigDecimal", newValuesAfterPopulate.get(
            "fieldBigDecimal").toString().replaceAll("[.]", ","));

      assertDescribedMapEquals(someValues, newValuesAfterPopulate);
   }

   public void testReset() throws Exception {
      final FooForm someForm = new FooForm();
      final FormController controller = getController(someForm);
      final Map describedMap = PropertyUtils.describe(someForm);
      final Map someValues = getSomeValues();
      final FormState state = new FormStateImpl(controller.getFormState());
      controller.populate(someValues);
      controller.reset(state);
      assertDescribedMapEquals(describedMap, PropertyUtils.describe(someForm));
   }

   /**
    * @Form
    */
   public static class FooForm {

      private boolean fieldBoolean;
      private char fieldChar;
      private byte fieldByte;
      private short fieldShort;
      private int fieldInt;
      private long fieldLong;
      private double fieldDouble;
      private float fieldFloat;
      private Boolean fieldBooleanWrapper;
      private Character fieldCharacterWrapper;
      private Byte fieldByteWrapper;
      private Short fieldShortWrapper;
      private Integer fieldIntegerWrapper;
      private Long fieldLongWrapper;
      private Double fieldDoubleWrapper;
      private Float fieldFloatWrapper;
      private String fieldString;
      private BigDecimal fieldBigDecimal;

      // TODO: java.util e java.sql Date, Time e Timestamp ??

      public BigDecimal getFieldBigDecimal() {
         return fieldBigDecimal;
      }

      public void setFieldBigDecimal(BigDecimal fieldBigDecimal) {
         this.fieldBigDecimal = fieldBigDecimal;
      }

      public boolean isFieldBoolean() {
         return fieldBoolean;
      }

      public void setFieldBoolean(boolean fieldBoolean) {
         this.fieldBoolean = fieldBoolean;
      }

      public Boolean getFieldBooleanWrapper() {
         return fieldBooleanWrapper;
      }

      public void setFieldBooleanWrapper(Boolean fieldBooleanWrapper) {
         this.fieldBooleanWrapper = fieldBooleanWrapper;
      }

      public byte getFieldByte() {
         return fieldByte;
      }

      public void setFieldByte(byte fieldByte) {
         this.fieldByte = fieldByte;
      }

      public Byte getFieldByteWrapper() {
         return fieldByteWrapper;
      }

      public void setFieldByteWrapper(Byte fieldByteWrapper) {
         this.fieldByteWrapper = fieldByteWrapper;
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

      public double getFieldDouble() {
         return fieldDouble;
      }

      public void setFieldDouble(double fieldDouble) {
         this.fieldDouble = fieldDouble;
      }

      public Double getFieldDoubleWrapper() {
         return fieldDoubleWrapper;
      }

      public void setFieldDoubleWrapper(Double fieldDoubleWrapper) {
         this.fieldDoubleWrapper = fieldDoubleWrapper;
      }

      public float getFieldFloat() {
         return fieldFloat;
      }

      public void setFieldFloat(float fieldFloat) {
         this.fieldFloat = fieldFloat;
      }

      public Float getFieldFloatWrapper() {
         return fieldFloatWrapper;
      }

      public void setFieldFloatWrapper(Float fieldFloatWrapper) {
         this.fieldFloatWrapper = fieldFloatWrapper;
      }

      public int getFieldInt() {
         return fieldInt;
      }

      public void setFieldInt(int fieldInt) {
         this.fieldInt = fieldInt;
      }

      public Integer getFieldIntegerWrapper() {
         return fieldIntegerWrapper;
      }

      public void setFieldIntegerWrapper(Integer fieldIntegerWrapper) {
         this.fieldIntegerWrapper = fieldIntegerWrapper;
      }

      public long getFieldLong() {
         return fieldLong;
      }

      public void setFieldLong(long fieldLong) {
         this.fieldLong = fieldLong;
      }

      public Long getFieldLongWrapper() {
         return fieldLongWrapper;
      }

      public void setFieldLongWrapper(Long fieldLongWrapper) {
         this.fieldLongWrapper = fieldLongWrapper;
      }

      public short getFieldShort() {
         return fieldShort;
      }

      public void setFieldShort(short fieldShort) {
         this.fieldShort = fieldShort;
      }

      public Short getFieldShortWrapper() {
         return fieldShortWrapper;
      }

      public void setFieldShortWrapper(Short fieldShortWrapper) {
         this.fieldShortWrapper = fieldShortWrapper;
      }

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) {
         this.fieldString = fieldString;
      }
   }

}