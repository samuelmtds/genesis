/*
 * The Genesis Project
 * Copyright (C) 2006-2009 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.mockobjects;

import java.io.Serializable;

public class MockBean implements Serializable {
   private String key;
   private String value;
   private MockBean bean;

   public MockBean(String key, String value) {
      this(key, value, null);
   }

   public MockBean(String key, String value, MockBean bean) {
      this.key = key;
      this.value = value;
      this.bean = bean;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public MockBean getBean() {
      return bean;
   }

   public void setBean(MockBean bean) {
      this.bean = bean;
   }

   public String toString() {
      return key;
   }
}
