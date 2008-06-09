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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @genesis.form
 */
public class ValueBean implements Serializable {
   private Object required;
   private Byte someByte;
   private Short someShort;
   private Integer integer;
   private Long someLong;
   private Float someFloat;
   private Double someDouble;
   private BigDecimal someBigDecimal;
   private String email;
   private Object requiredIf;
   private String requiredIfDependency;
   private String mask;
   private Date date;
   private String maxLength;
   private String minLength;
   private Long longRange;
   private Double doubleRange;
   private BigDecimal bigDecimalRange;
   private BigDecimal min;
   private BigDecimal max;
   private String jxpath;
   private Integer jxpathDependency;
   private String url;

   public Object getRequired() {
      return required;
   }

   /**
    * @genesis.validator type="required"
    * @genesis.validator-args arg0resource="validatorTest.required"
    */
   public void setRequired(Object required) {
      this.required = required;
   }

   public Byte getSomeByte() {
      return someByte;
   }

   /**
    * @genesis.validator type="byte"
    * @genesis.validator-args arg0resource="validatorTest.byte"
    */
   public void setSomeByte(Byte oneByte) {
      this.someByte = oneByte;
   }

   public Short getSomeShort() {
      return someShort;
   }

   /**
    * @genesis.validator type="short"
    * @genesis.validator-args arg0resource="validatorTest.short"
    */
   public void setSomeShort(Short short1) {
      someShort = short1;
   }

   public Integer getInteger() {
      return integer;
   }

   /**
    * @genesis.validator type="int"
    * @genesis.validator-args arg0resource="validatorTest.integer"
    */
   public void setInteger(Integer integer) {
      this.integer = integer;
   }

   public Long getSomeLong() {
      return someLong;
   }

   /**
    * @genesis.validator type="long"
    * @genesis.validator-args arg0resource="validatorTest.long"
    */
   public void setSomeLong(Long long1) {
      someLong = long1;
   }

   public Float getSomeFloat() {
      return someFloat;
   }

   /**
    * @genesis.validator type="float"
    * @genesis.validator-args arg0resource="validatorTest.float"
    */
   public void setSomeFloat(Float float1) {
      someFloat = float1;
   }

   public Double getSomeDouble() {
      return someDouble;
   }

   /**
    * @genesis.validator type="double"
    * @genesis.validator-args arg0resource="validatorTest.double"
    */
   public void setSomeDouble(Double double1) {
      someDouble = double1;
   }

   public BigDecimal getSomeBigDecimal() {
      return someBigDecimal;
   }

   /**
    * @genesis.validator type="bigDecimal"
    * @genesis.validator-args arg0resource="validatorTest.bigDecimal"
    */
   public void setSomeBigDecimal(BigDecimal someBigDecimal) {
      this.someBigDecimal = someBigDecimal;
   }

   public String getEmail() {
      return email;
   }

   /**
    * @genesis.validator type="email"
    * @genesis.validator-args arg0resource="validatorTest.email"
    */
   public void setEmail(String email) {
      this.email = email;
   }

   public Object getRequiredIf() {
      return requiredIf;
   }

   /**
    * @genesis.validator type="requiredif"
    * @genesis.validator-args arg0resource="validatorTest.requiredIf"
    * @genesis.validator-var name="field[0]" value="requiredIfDependency"
    * @genesis.validator-var name="fieldTest[0]" value="NOTNULL"
    */
   public void setRequiredIf(Object requiredIf) {
      this.requiredIf = requiredIf;
   }

   public String getRequiredIfDependency() {
      return requiredIfDependency;
   }

   public void setRequiredIfDependency(String requiredIfDependency) {
      this.requiredIfDependency = requiredIfDependency;
   }

   public String getMask() {
      return mask;
   }

   /**
    * @genesis.validator type="mask"
    * @genesis.validator-args arg0resource="validatorTest.mask"
    * @genesis.validator-var name="mask" value="[AZ][0-9]"
    */
   public void setMask(String mask) {
      this.mask = mask;
   }

   public Date getDate() {
      return date;
   }

   /**
    * @genesis.validator type="date"
    * @genesis.validator-args arg0resource="validatorTest.date"
    * @genesis.validator-var name="pattern" value="dd/MM/yyyy"
    */
   public void setDate(Date date) {
      this.date = date;
   }

   public String getMaxLength() {
      return maxLength;
   }

   /**
    * @genesis.validator type="maxlength" arg1value="10"
    * @genesis.validator-args arg0resource="validatorTest.maxLength"
    * @genesis.validator-var name="maxlength" value="10"
    */
   public void setMaxLength(String maxLength) {
      this.maxLength = maxLength;
   }

   public String getMinLength() {
      return minLength;
   }

   /**
    * @genesis.validator type="minlength" arg1value="2"
    * @genesis.validator-args arg0resource="validatorTest.minLength"
    * @genesis.validator-var name="minlength" value="2"
    */
   public void setMinLength(String minLength) {
      this.minLength = minLength;
   }

   public Long getLongRange() {
      return longRange;
   }

   /**
    * @genesis.validator type="longRange"
    * @genesis.validator-args arg0resource="validatorTest.longrange"
    * @genesis.validator-var name="min" value="5"
    * @genesis.validator-var name="max" value="10"
    */
   public void setLongRange(Long longRange) {
      this.longRange = longRange;
   }

   public Double getDoubleRange() {
      return doubleRange;
   }

   /**
    * @genesis.validator type="doubleRange"
    * @genesis.validator-args arg0resource="validatorTest.doublerange"
    * @genesis.validator-var name="min" value="5"
    * @genesis.validator-var name="max" value="10"
    */
   public void setDoubleRange(Double doubleRange) {
      this.doubleRange = doubleRange;
   }

   public BigDecimal getBigDecimalRange() {
      return bigDecimalRange;
   }

   /**
    * @genesis.validator type="bigDecimalRange"
    * @genesis.validator-args arg0resource="validatorTest.bigDecimalRange"
    * @genesis.validator-var name="min" value="5.1234"
    * @genesis.validator-var name="max" value="9.1234"
    */
   public void setBigDecimalRange(BigDecimal bigDecimalRange) {
      this.bigDecimalRange = bigDecimalRange;
   }

   public BigDecimal getMin() {
      return min;
   }

   /**
    * @genesis.validator type="min"
    * @genesis.validator-args arg0resource="validatorTest.min"
    * @genesis.validator-var name="min" value="5.1234"
    */
   public void setMin(BigDecimal min) {
      this.min = min;
   }

   public BigDecimal getMax() {
      return max;
   }

   /**
    * @genesis.validator type="max"
    * @genesis.validator-args arg0resource="validatorTest.max"
    * @genesis.validator-var name="max" value="9.1234"
    */
   public void setMax(BigDecimal max) {
      this.max = max;
   }

   public String getJxpath() {
      return jxpath;
   }

   /**
    * @genesis.validator type="jxpath"
    * @genesis.validator-args arg0resource="validatorTest.jxpath"
    * @genesis.validator-var name="jxpath" value="jxpathDependency = 10"
    */
   public void setJxpath(String jxpath) {
      this.jxpath = jxpath;
   }

   public Integer getJxpathDependency() {
      return jxpathDependency;
   }

   public void setJxpathDependency(Integer jxpathDependency) {
      this.jxpathDependency = jxpathDependency;
   }

   public String getUrl() {
      return url;
   }

   /**
    * @genesis.validator type="url"
    * @genesis.validator-args arg0resource="validatorTest.url"
    */
   public void setUrl(String url) {
      this.url = url;
   }
}