/*
 * The Genesis Project
 * Copyright (C) 2004-2009  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.aspect;

import java.lang.reflect.Method;
import java.util.Map;

import net.java.dev.genesis.annotation.Criteria;
import net.java.dev.genesis.command.hibernate.CriteriaCommandExecutor;
import net.java.dev.genesis.command.hibernate.CriteriaResolver;

import net.java.dev.genesis.util.Bundle;
import org.codehaus.aspectwerkz.AspectContext;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodRtti;
import org.codehaus.backport175.reader.Annotations;

/**
 * @Aspect("perJVM")
 */
public class CriteriaCommandExecutionAspect extends CommandInvocationAspect {
   private ThreadLocal threadLocal = new InheritableThreadLocal();
   
   private boolean preventStackOverflow;

   public CriteriaCommandExecutionAspect(final AspectContext ctx) {
      super(ctx);
      preventStackOverflow = "true".equals(ctx.getParameter("preventStackOverflow")); // NOI18N
   }

   /**
    * @Around("criteriaCommandExecution")
    */
   public Object commandExecution(JoinPoint jp) throws Throwable {
      final MethodRtti rtti = (MethodRtti)jp.getRtti();

      if (preventStackOverflow) {
         Method m = (Method)threadLocal.get();
         if (m != null && m.equals(rtti.getMethod())) {
            Object value = jp.proceed();
            threadLocal.set(null);
            return value;
         }
      }

      final CriteriaResolver obj = (CriteriaResolver)jp.getTarget();
      final Class[] classes = rtti.getParameterTypes();
      final String[] classNames = new String[classes.length];

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }

      final String methodName = rtti.getMethod().getName();
      final Object[] parameterValues = rtti.getParameterValues();

      final Criteria annon = (Criteria) Annotations.getAnnotation(
            Criteria.class, rtti.getMethod());
      
      final CriteriaAnnotationParser parser = new CriteriaAnnotationParser(annon);
      
      if (preventStackOverflow) {
         threadLocal.set(rtti.getMethod());
      }

      try {
         return new CriteriaCommandExecutor(obj, methodName, classNames,
               parameterValues, parser.getPersisterClassName(), parser
                     .getOrderBy(), parser.isAsc(), obj.getPropertiesMap())
               .execute();
      } finally {
         if (preventStackOverflow) {
            threadLocal.set(null);
         }
      }
   }

   public static class CriteriaAnnotationParser {
      private String persisterClassName;
      private String[] orderBy;
      private boolean[] isAsc;

      public CriteriaAnnotationParser(Criteria annotation) {
         Class klazz = annotation.value();
         if (klazz != null && !Object.class.equals(annotation.value())) {
            persisterClassName = annotation.value().getName();
         }
         
         String[] values = annotation.orderby();
         orderBy = new String[values.length];
         isAsc = new boolean[values.length];
         for (int i = 0; i < values.length; i++) {
            String[] props = values[i].split("\\s+"); // NOI18N

            if (props.length == 0 || props.length > 2) {
               throw new IllegalArgumentException(
                     Bundle.getMessage(CriteriaCommandExecutionAspect.class,
                     "MALFORMED_CRITERIA_ANNOTATION")); // NOI18N
            }
            orderBy[i] = props[0];

            if (props.length < 2) {
               isAsc[i] = true;
               continue;
            }

            if (props[1].equalsIgnoreCase("asc")) { // NOI18N
               isAsc[i] = true;
            } else if (props[1].equalsIgnoreCase("desc")) { // NOI18N
               isAsc[i] = false;
            } else {
               throw new IllegalArgumentException(
                     Bundle.getMessage(CriteriaCommandExecutionAspect.class,
                     "MALFORMED_CRITERIA_ANNOTATION_ORDER_BY_CLAUSE_MUST_BE_ASC_OR_DESC")); // NOI18N
            }

         }
      }

      public String getPersisterClassName() {
         return persisterClassName;
      }

      public boolean[] isAsc() {
         return isAsc;
      }

      public String[] getOrderBy() {
         return orderBy;
      }
   }

   /**
    * @Mixin(pointcut="criteriaResolverIntroduction", isTransient=true, deploymentModel="perInstance")
    */
   public static class CriteriaResolverImpl implements CriteriaResolver {
      private Map propertiesMap;

      public Map getPropertiesMap() {
         return propertiesMap;
      }

      public void setPropertiesMap(Map propertiesMap) {
         this.propertiesMap = propertiesMap;
      }
   }   
}