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
package net.java.dev.genesis.aspect;

import java.util.Map;

import net.java.dev.genesis.command.hibernate.CriteriaCommandExecutor;
import net.java.dev.genesis.command.hibernate.CriteriaResolver;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.MethodTuple;
import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotationProxy;
import org.codehaus.aspectwerkz.joinpoint.JoinPoint;
import org.codehaus.aspectwerkz.joinpoint.MethodRtti;
import org.codehaus.aspectwerkz.joinpoint.impl.MethodRttiImpl;

/**
 * @Aspect perJVM
 */
public class CriteriaCommandExecutionAspect extends CommandInvocationAspect {
   private static final String CRITERIA_ATTRIBUTE = "Criteria";
   
   private boolean useOriginalMethod;

   public CriteriaCommandExecutionAspect(CrossCuttingInfo ccInfo) {
      super(ccInfo);
      
      useOriginalMethod = "true".equals(ccInfo.getParameter("useOriginalMethod"));
   }

   /**
    * @Around criteriaCommandExecution
    */
   public Object commandExecution(JoinPoint jp) throws Throwable {
      final CriteriaResolver obj = (CriteriaResolver)jp.getTarget();
      final MethodRtti rtti = (MethodRtti) jp.getRtti();
      final Class[] classes = rtti.getParameterTypes();
      final String[] classNames = new String[classes.length];

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }

      final MethodTuple methodTuple = ((MethodRttiImpl) rtti).getMethodTuple();
      final String methodName = useOriginalMethod ? methodTuple
            .getOriginalMethod().getName() : methodTuple.getWrapperMethod()
            .getName();
      final Object[] parameterValues = rtti.getParameterValues();

      final UntypedAnnotationProxy annon = (UntypedAnnotationProxy) Annotations
            .getAnnotation(CRITERIA_ATTRIBUTE, rtti.getMethod());
      final CriteriaAnnotationParser parser = new CriteriaAnnotationParser(annon.getValue());
      
      return new CriteriaCommandExecutor(obj, methodName, classNames,
            parameterValues, parser.getPersisterClassName(), parser
                  .getOrderBy(), parser.isAsc(), obj.getPropertiesMap())
            .execute();
   }

   public static class CriteriaAnnotationParser {
      private String persisterClassName;
      private String[] orderBy;
      private boolean[] isAsc;

      public CriteriaAnnotationParser(String annotation) {
         String[] values = annotation.trim().split("(order-by\\s*=)");
         
         if (values.length > 0) {
            persisterClassName = values[0].trim();

            if (values.length > 1) {
               values = values[1].trim().split("(\\s*,\\s*)");
               orderBy = new String[values.length];
               isAsc = new boolean[values.length];

               for (int i = 0; i < values.length; i++) {
                  String[] props = values[i].split("\\s+");

                  if (props.length == 0 || props.length > 2) {
                     throw new IllegalArgumentException(
                           "Malformed Criteria annotation");
                  }
                  orderBy[i] = props[0];

                  if (props.length < 2) {
                     isAsc[i] = true;
                     continue;
                  }

                  if (props[1].equalsIgnoreCase("asc")) {
                     isAsc[i] = true;
                  } else if (props[1].equalsIgnoreCase("desc")) {
                     isAsc[i] = false;
                  } else {
                     throw new IllegalArgumentException(
                           "Malformed Criteria"
                                 + "annotation: order-by clause must be 'asc' or 'desc'");
                  }
               }
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
    * @Introduce criteriaResolverIntroduction deployment-model=perInstance
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