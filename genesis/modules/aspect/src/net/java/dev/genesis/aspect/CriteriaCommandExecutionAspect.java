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
package net.java.dev.genesis.aspect;

import java.util.Map;

import net.java.dev.genesis.command.hibernate.CriteriaCommandExecutor;
import net.java.dev.genesis.command.hibernate.CriteriaResolver;
import net.java.dev.genesis.reflection.ClassesCache;

import org.codehaus.aspectwerkz.CrossCuttingInfo;
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

   public CriteriaCommandExecutionAspect(CrossCuttingInfo ccInfo) {
      super(ccInfo);
   }

   /**
    * @Around criteriaCommandExecution
    */
   public Object commandExecution(JoinPoint jp) throws Throwable {
      final CriteriaResolver obj = (CriteriaResolver)jp.getTargetInstance();
      final MethodRtti rtti = (MethodRtti) jp.getRtti();
      UntypedAnnotationProxy annon = (UntypedAnnotationProxy) Annotations
            .getAnnotation(CRITERIA_ATTRIBUTE, rtti.getMethod());
      final Class persisterClass = ClassesCache.getClass(annon.getValue());
      final Class[] classes = rtti.getParameterTypes();
      final String[] classNames = new String[classes.length];

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }
      final String methodName = ((MethodRttiImpl) rtti).getMethodTuple()
            .getWrapperMethod().getName();
      final Object[] parameterValues = rtti.getParameterValues();
      return new CriteriaCommandExecutor(obj, methodName, classNames,
            parameterValues, persisterClass, obj.getPropertiesMap()).execute();
   }
   
   /**
    * @Introduce criteriaResolverIntroduction deployment-model=perInstance
    */
   public static class CriteriaResolverImpl implements CriteriaResolver {

      private Map propertiesMap;

      public Map getPropertiesMap() throws Throwable {
         return propertiesMap;
      }

      public void setPropertiesMap(Map propertiesMap) {
         this.propertiesMap = propertiesMap;
      }
   }
}