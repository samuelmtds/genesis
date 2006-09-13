/*
 * The Genesis Project
 * Copyright (C) 2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.ui.metadata.annotation;

import java.util.HashMap;
import java.util.Map;

import net.java.dev.genesis.annotation.CallWhen;
import net.java.dev.genesis.annotation.ClearOn;
import net.java.dev.genesis.annotation.Cloner;
import net.java.dev.genesis.annotation.Condition;
import net.java.dev.genesis.annotation.DataProvider;
import net.java.dev.genesis.annotation.EmptyResolver;
import net.java.dev.genesis.annotation.EmptyValue;
import net.java.dev.genesis.annotation.EnabledWhen;
import net.java.dev.genesis.annotation.EqualityComparator;
import net.java.dev.genesis.annotation.ValidateBefore;
import net.java.dev.genesis.annotation.VisibleWhen;

public class AnnotationHandlerRegistry {
   private static final AnnotationHandlerRegistry instance = new AnnotationHandlerRegistry();
   private Map registry = new HashMap();

   private AnnotationHandlerRegistry() {
      registry.put(Condition.class, new ConditionAnnotationHandler());
      registry.put(EnabledWhen.class, new EnabledWhenAnnotationHandler());
      registry.put(VisibleWhen.class, new VisibleWhenAnnotationHandler());
      registry.put(ValidateBefore.class, new ValidateBeforeAnnotationHandler());
      registry.put(DataProvider.class, new DataProviderAnnotationHandler());
      registry.put(CallWhen.class, new CallWhenAnnotationHandler());
      registry.put(ClearOn.class, new ClearOnAnnotationHandler());
      registry.put(EqualityComparator.class,
            new EqualityComparatorAnnotationHandler());
      registry.put(EmptyResolver.class, new EmptyResolverAnnotationHandler());
      registry.put(EmptyValue.class, new EmptyValueAnnotationHandler());
      registry.put(Cloner.class, new ClonerAnnotationHandler());
   }

   public static AnnotationHandlerRegistry getInstance() {
      return instance;
   }

   public AnnotationHandler get(Class annotationClass) {
      return (AnnotationHandler) registry.get(annotationClass);
   }
}
