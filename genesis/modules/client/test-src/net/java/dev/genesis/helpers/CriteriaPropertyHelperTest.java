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
package net.java.dev.genesis.helpers;
import net.java.dev.genesis.GenesisTestCase;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;

public class CriteriaPropertyHelperTest extends GenesisTestCase {
   public static class MockHibernateCriteria extends net.java.dev.genesis.mockobjects.MockHibernateCriteria  {
      private String property1;
      private String property2;
      private String criteriaProperty;

      public String getProperty1() {
         return property1;
      }

      public void setProperty1(String property1) {
         this.property1 = property1;
      }

      public String getProperty2() {
         return property2;
      }

      public void setProperty2(String property2) {
         this.property2 = property2;
      }

      public String getCriteriaProperty() {
         return criteriaProperty;
      }

      public void setCriteriaProperty(String criteriaProperty) {
         this.criteriaProperty = criteriaProperty;
      }
   }

   public static class MockForm extends net.java.dev.genesis.mockobjects.MockForm {
      private String property1;
      private String property2;
      private String formProperty;

      public String getProperty1() {
         return property1;
      }

      public void setProperty1(String property1) {
         this.property1 = property1;
      }

      public String getProperty2() {
         return property2;
      }

      public void setProperty2(String property2) {
         this.property2 = property2;
      }

      public String getFormProperty() {
         return formProperty;
      }

      public void setFormProperty(String formProperty) {
         this.formProperty = formProperty;
      }
   }

   public void testFillCriteria() throws Exception {
      MockHibernateCriteria criteria = new MockHibernateCriteria();
      MockForm form = new MockForm();

      FormMetadata metadata = form.getFormMetadata();
      metadata.addFieldMetadata("property1", new FieldMetadata("property1", 
            String.class, true));

      // Simple test with "incomplete" metadata
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());

      // Tests for correct behaviour when a field is empty
      String value = new String();
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(0, criteria.getPropertiesMap().size());

      // Tests for correct behaviour when a field is populated
      value = "value";
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property1"));

      // Tests for correct behaviour when a field with no FieldMetadata is filled
      form.setProperty1(null);
      form.setProperty2(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property2"));

      // Fields that don't in both classes shouldn't be copied
      form.setProperty2(null);
      form.setFormProperty(value);
      criteria.setCriteriaProperty(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());
   }
}