/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.cloning;

import net.java.dev.genesis.GenesisTestCase;

public class BeanUtilsClonerTest extends GenesisTestCase {
   public static class SomeBean {
      private Object field;

      public Object getField() {
         return field;
      }

      public void setField(Object field) {
         this.field = field;
      }
   }

   public BeanUtilsClonerTest() {
      super("BeanUtils Cloner Unit Test");
   }

   public void testClone() {
      BeanUtilsCloner cloner = new BeanUtilsCloner();

      assertNull(cloner.clone(null));

      SomeBean bean = new SomeBean();
      bean.setField(new Object());
      SomeBean clone = (SomeBean)cloner.clone(bean);

      assertNotSame(bean, clone);
      assertSame(bean.getField(), clone.getField());

   }
}
