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
import java.util.Date;

/**
 * @struts.form
 */
public class ValueBean implements Serializable {
   private Object required;
   private Byte someByte;
   private Short someShort;
   private Integer integer;
   private Long someLong;
   private Float someFloat;
   private Double someDouble;
   private String email;
   private Object requiredIf;
   private String requiredIfDependency;
   private String mask;
   private Date date;
   private String maxLength;
   private String minLength;
   private Long longRange;
   private Double doubleRange;
   private String jxpath;
   private Integer jxpathDependency;

   public Object getRequired() {
      return required;
   }

   /**
    * @struts.validator type="required"
    * @struts.validator-args arg0resource="validatorTest.required"
    */
   public void setRequired(Object required) {
      this.required = required;
   }

   public Byte getSomeByte() {
      return someByte;
   }

   /**
    * @struts.validator type="byte"
    * @struts.validator-args arg0resource="validatorTest.byte"
    */
   public void setSomeByte(Byte oneByte) {
      this.someByte = oneByte;
   }

   public Short getSomeShort() {
      return someShort;
   }

   /**
    * @struts.validator type="short"
    * @struts.validator-args arg0resource="validatorTest.short"
    */
   public void setSomeShort(Short short1) {
      someShort = short1;
   }

   public Integer getInteger() {
      return integer;
   }

   /**
    * @struts.validator type="int"
    * @struts.validator-args arg0resource="validatorTest.integer"
    */
   public void setInteger(Integer integer) {
      this.integer = integer;
   }

   public Long getSomeLong() {
      return someLong;
   }

   /**
    * @struts.validator type="long"
    * @struts.validator-args arg0resource="validatorTest.long"
    */
   public void setSomeLong(Long long1) {
      someLong = long1;
   }

   public Float getSomeFloat() {
      return someFloat;
   }

   /**
    * @struts.validator type="float"
    * @struts.validator-args arg0resource="validatorTest.float"
    */
   public void setSomeFloat(Float float1) {
      someFloat = float1;
   }

   public Double getSomeDouble() {
      return someDouble;
   }

   /**
    * @struts.validator type="double"
    * @struts.validator-args arg0resource="validatorTest.double"
    */
   public void setSomeDouble(Double double1) {
      someDouble = double1;
   }

   public String getEmail() {
      return email;
   }

   /**
    * @struts.validator type="email"
    * @struts.validator-args arg0resource="validatorTest.email"
    */
   public void setEmail(String email) {
      this.email = email;
   }

   public Object getRequiredIf() {
      return requiredIf;
   }

   /**
    * @struts.validator type="requiredif"
    * @struts.validator-args arg0resource="validatorTest.requiredIf"
    * @struts.validator-var name="field[0]" value="requiredIfDependency"
    * @struts.validator-var name="fieldTest[0]" value="NOTNULL"
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
    * @struts.validator type="mask"
    * @struts.validator-args arg0resource="validatorTest.mask"
    * @struts.validator-var name="mask" value="[AZ][0-9]"
    */
   public void setMask(String mask) {
      this.mask = mask;
   }

   public Date getDate() {
      return date;
   }

   /**
    * @struts.validator type="date"
    * @struts.validator-args arg0resource="validatorTest.date"
    * @struts.validator-var name="pattern" value="dd/MM/yyyy"
    */
   public void setDate(Date date) {
      this.date = date;
   }

   public String getMaxLength() {
      return maxLength;
   }

   /**
    * @struts.validator type="maxlength" arg1value="10"
    * @struts.validator-args arg0resource="validatorTest.maxLength"
    * @struts.validator-var name="maxlength" value="10"
    */
   public void setMaxLength(String maxLength) {
      this.maxLength = maxLength;
   }

   public String getMinLength() {
      return minLength;
   }

   /**
    * @struts.validator type="minlength" arg1value="2"
    * @struts.validator-args arg0resource="validatorTest.minLength"
    * @struts.validator-var name="minlength" value="2"
    */
   public void setMinLength(String minLength) {
      this.minLength = minLength;
   }

   public Long getLongRange() {
      return longRange;
   }

   /**
    * @struts.validator type="longRange"
    * @struts.validator-args arg0resource="validatorTest.longrange"
    * @struts.validator-var name="min" value="5"
    * @struts.validator-var name="max" value="10"
    */
   public void setLongRange(Long longRange) {
      this.longRange = longRange;
   }

   public Double getDoubleRange() {
      return doubleRange;
   }

   /**
    * @struts.validator type="doubleRange"
    * @struts.validator-args arg0resource="validatorTest.doublerange"
    * @struts.validator-var name="min" value="5"
    * @struts.validator-var name="max" value="10"
    */
   public void setDoubleRange(Double doubleRange) {
      this.doubleRange = doubleRange;
   }

   public String getJxpath() {
      return jxpath;
   }

   /**
    * @struts.validator type="jxpath"
    * @struts.validator-args arg0resource="validatorTest.jxpath"
    * @struts.validator-var name="jxpath" value="jxpathDependency = 10"
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
}