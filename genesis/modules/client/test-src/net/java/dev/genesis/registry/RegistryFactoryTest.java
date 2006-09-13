/*
 * The Genesis Project
 * Copyright (C) 2005 Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import net.java.dev.genesis.GenesisTestCase;

public class RegistryFactoryTest extends GenesisTestCase {
   public static class SimpleBean {
      static Throwable t;

      private String property;

      public SimpleBean() throws Throwable {
         if (t != null) {
            throw t;
         }
      }

      public SimpleBean(String property) {
         setProperty(property);
      }

      public String getProperty() {
         return property;
      }

      public void setProperty(String property) {
         this.property = property;
      }

      // Just in case...
      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         if (!(o instanceof SimpleBean)) {
            return false;
         }

         SimpleBean s = (SimpleBean)o;
         return property == null ? s.getProperty() == null : 
               getProperty().equals(s.getProperty());
      }
   }

   private RegistryFactory factory;

   public void setUp() {
      factory = new RegistryFactory();
      SimpleBean.t = null;
   }

   public void testGet() {
      factory.register(Object.class, new Object());
      factory.register(Integer.class, new Integer(1));

      // Test for regular class with registered instance
      assertEquals(Integer.class, factory.get(Integer.class).getClass());

      // Test for regular class with no registered instance
      assertNull(factory.get(Comparable.class));

      // Test for primitive
      assertEquals(Object.class, factory.get(Byte.TYPE).getClass());

      // Test for Object
      assertEquals(Object.class, factory.get(Object.class).getClass());
   }

   public void testGetNewInstance() throws Throwable {
      // Test with no properties
      assertEquals(new SimpleBean(), factory.getNewInstance(SimpleBean.class
            .getName(), null));
      assertEquals(new SimpleBean(), factory.getNewInstance(SimpleBean.class
            .getName(), new HashMap()));

      // Test with properties
      Map attributes = new HashMap();
      attributes.put("property", "value");

      assertEquals(new SimpleBean("value"), factory.getNewInstance(
            SimpleBean.class.getName(), attributes));

      // Test for non-existing class
      try {
         factory.getNewInstance("NonExistingClass", null);
         fail("Should have thrown a wrapped ClassNotFoundException");
      } catch (RuntimeException re) {
         assertEquals(ClassNotFoundException.class, re.getCause().getClass());
      }

      checkExceptionGetNewInstance(new IllegalAccessException());
      checkExceptionGetNewInstance(new InvocationTargetException(new Exception()));
      checkExceptionGetNewInstance(new InstantiationException());
   }

   private void checkExceptionGetNewInstance(Exception e) {
      SimpleBean.t = e;

      try {
         factory.getNewInstance(SimpleBean.class.getName(), null);
         fail("Should have thrown a wrapped " + e.getClass().getName());
      } catch (RuntimeException re) {
         assertEquals(e.getClass(), re.getCause().getClass());
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   public void testGetExistingInstance() throws Throwable {
      SimpleBean instance = new SimpleBean();
      factory.register(SimpleBean.class, instance);

      // Test with no properties
      assertSame(instance, factory.getExistingInstance(SimpleBean.class, null));
      assertSame(instance, factory.getExistingInstance(SimpleBean.class, 
            new HashMap()));

      // Test with properties
      Map attributes = new HashMap();
      attributes.put("property", "value");

      assertEquals(new SimpleBean("value"), factory.getExistingInstance(
            SimpleBean.class, attributes));

      checkExceptionGetExistingInstance(new NoSuchMethodException());
      checkExceptionGetExistingInstance(new IllegalAccessException());
      checkExceptionGetExistingInstance(new InvocationTargetException(
            new Exception()));
      checkExceptionGetExistingInstance(new InstantiationException());
   }

   private void checkExceptionGetExistingInstance(Exception e) {
      SimpleBean.t = e;

      Map attributes = new HashMap();
      attributes.put("property", "value");

      try {
         factory.getExistingInstance(SimpleBean.class, attributes);
         fail("Should have thrown a wrapped " + e.getClass().getName());
      } catch (RuntimeException re) {
         assertEquals(e.getClass(), re.getCause().getClass());
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }
}