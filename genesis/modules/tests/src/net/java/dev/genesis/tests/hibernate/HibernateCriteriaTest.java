/*
 * The Genesis Project
 * Copyright (C) 2004-2005  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.tests.hibernate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import net.java.dev.genesis.command.hibernate.AbstractHibernateCriteria;
import net.java.dev.genesis.command.hibernate.CriteriaResolver;
import net.java.dev.genesis.helpers.CriteriaPropertyHelper;
import net.java.dev.genesis.tests.TestCase;
import net.sf.hibernate.expression.Expression;


public class HibernateCriteriaTest extends TestCase {

   private final DbActions dbActions = new DbActions();

   public void tearDown() throws Exception {
      dbActions.deleteAll();
   }

   public void test() throws Exception {
      dbActions.insert(3);
      CriteriaTester tester = new CriteriaTester();
      HibernateBeanForm form = new HibernateBeanForm();
      form.setCode("");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      List results = tester.find();
      assertEquals(results.size(), 3);

      form.setCode("2");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      for (Iterator iter = results.iterator(); iter.hasNext();) {
         System.out.println(iter.next());
         
      }
      assertEquals(results.size(), 1);

      form.setCode("abc");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      assertTrue(results.isEmpty());
   }

   public void testCriteriaIntroduction() throws Exception {
      CriteriaTester c1 = new CriteriaTester();
      CriteriaTester c2 = new CriteriaTester();

      Map map = new HashMap();
      ((CriteriaResolver)c1).setPropertiesMap(map);

      assertNotSame(map, ((CriteriaResolver)c2).getPropertiesMap());
   }

   public static class CriteriaTester extends AbstractHibernateCriteria {

      public void setCode(String code) {
         getCriteria().add(Expression.eq("code", code));
      }

      /**
       * @Criteria net.java.dev.genesis.tests.hibernate.HibernateBean
       */
      public List find() throws Exception {
         return getCriteria().list();
      }

   }

   /**
    * @Form
    */
   public static class HibernateBeanForm {

      private Long pk;

      private String code;

      public String getCode() {
         return code;
      }

      public void setCode(String code) {
         this.code = code;
      }

      public Long getPk() {
         return pk;
      }

      public void setPk(Long pk) {
         this.pk = pk;
      }
   }

}