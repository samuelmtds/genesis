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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.registry.DefaultValueRegistry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.reusablecomponents.lang.Enum;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.annotation.Annotation;
import org.codehaus.aspectwerkz.annotation.Annotations;
import org.codehaus.aspectwerkz.annotation.UntypedAnnotationProxy;

public class FormMetadataFactoryAspect {
   protected final CrossCuttingInfo ccInfo;

   public FormMetadataFactoryAspect(final CrossCuttingInfo ccInfo) {
      this.ccInfo = ccInfo;
   }
   
   /**
    * @Introduce formMetadataFactoryIntroduction deploymentModel=perJVM
    */
   public static class AspectFormMetadataFactory implements FormMetadataFactory {
      public static class MetadataAttribute extends Enum {
         public static final MetadataAttribute CONDITION = new MetadataAttribute(
               "Condition");
         public static final MetadataAttribute ENABLED_WHEN = new MetadataAttribute(
               "EnabledWhen");
         public static final MetadataAttribute CLEAR_ON = new MetadataAttribute(
               "ClearOn");
         public static final MetadataAttribute DISPLAY_ONLY = new MetadataAttribute(
               "DisplayOnly");
         public static final MetadataAttribute EQUALITY_COMPARATOR = new MetadataAttribute(
               "EqualityComparator");
         public static final MetadataAttribute EMPTY_RESOLVER = new MetadataAttribute(
               "EmptyResolver");
         public static final MetadataAttribute EMPTY_VALUE = new MetadataAttribute(
               "EmptyValue");
         public static final MetadataAttribute VISIBLE_WHEN = new MetadataAttribute(
               "VisibleWhen");

         public MetadataAttribute(String name) {
            super(name);
         }
      }

      private final Map cache = new HashMap();

      public FormMetadata getFormMetadata(final Class formClass) {
         FormMetadata formMetadata = (FormMetadata) cache.get(formClass);
         if (formMetadata == null) {
            formMetadata = new FormMetadata(formClass);
            processFormMetadata(formMetadata, formClass);
            cache.put(formClass, formMetadata);
         }
         return formMetadata;
      }

      private void processFormMetadata(final FormMetadata formMetadata,
            final Class formClass) {
         processConditionAnnotation(formMetadata, Annotations.getAnnotations(
               MetadataAttribute.CONDITION.getName(), formClass));
         try {
            processFieldMetadatas(formMetadata, formClass);
         } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
         } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
         }
      }

      private void processFieldMetadatas(final FormMetadata formMetadata,
            final Class formClass) throws InvocationTargetException,
            IllegalAccessException {
         try {
            final PropertyDescriptor[] propertyDescriptors = Introspector
                  .getBeanInfo(formClass).getPropertyDescriptors();
            PropertyDescriptor propDesc;
            FieldMetadata fieldMetadata;
            for (int i = 0; i < propertyDescriptors.length; i++) {
               propDesc = propertyDescriptors[i];
               // Ignoring java.lang.Object.getClass()
               if (!propDesc.getName().equals("class")) {
                  fieldMetadata = new FieldMetadata(propDesc.getName(),
                        propDesc.getPropertyType());
                  formMetadata.addFieldMetadata(propDesc.getName(),
                        fieldMetadata);
                  processFieldMetadata(formMetadata, fieldMetadata, propDesc);
               }
            }
         } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
         }
      }

      private void processFieldMetadata(final FormMetadata formMetadata,
            final FieldMetadata fieldMetadata,
            final PropertyDescriptor fieldDescriptor)
            throws InvocationTargetException, IllegalAccessException {
         if (fieldDescriptor.getReadMethod() == null) {
            return;
         }
         final Method readMethod = fieldDescriptor.getReadMethod();
         processConditionAnnotation(formMetadata, Annotations.getAnnotations(
               MetadataAttribute.CONDITION.getName(), readMethod));
         processEnabledWhenAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.ENABLED_WHEN.getName(), readMethod));
         processClearOnAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.CLEAR_ON.getName(), readMethod));
         processDisplayOnlyAnnotation(fieldMetadata, Annotations.getAnnotation(
               MetadataAttribute.DISPLAY_ONLY.getName(), readMethod));
         processVisibleWhenAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.VISIBLE_WHEN.getName(), readMethod));
         processEmptyValueAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.EMPTY_VALUE.getName(), readMethod));
         processEqualityComparatorAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.EQUALITY_COMPARATOR.getName(),
                     readMethod));
         processEmptyResolverAnnotation(fieldMetadata,
               (UntypedAnnotationProxy) Annotations.getAnnotation(
                     MetadataAttribute.EMPTY_RESOLVER.getName(), readMethod));
      }

      private void processConditionAnnotation(final FormMetadata formMetadata,
            List annotations) {
         UntypedAnnotationProxy annotation;
         String value;
         int equalIndex;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            annotation = (UntypedAnnotationProxy) iter.next();
            value = annotation.getValue();
            equalIndex = value.indexOf('=');
            if (equalIndex > 0) {
               formMetadata.addNamedCondition(value.substring(0, equalIndex),
                     JXPathContext.compile(value.substring(equalIndex + 1)));
            }
         }
      }

      private void processEnabledWhenAnnotation(
            final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         if (annotation != null) {
            fieldMetadata.setEnabledCondition(JXPathContext.compile(annotation
                  .getValue()));
         }
      }

      private void processClearOnAnnotation(final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         if (annotation != null) {
            fieldMetadata.setClearOnCondition(JXPathContext.compile(annotation
                  .getValue()));
         }
      }

      private void processDisplayOnlyAnnotation(
            final FieldMetadata fieldMetadata, final Annotation annotation) {
         fieldMetadata.setDisplayOnly(annotation != null);
      }

      private void processVisibleWhenAnnotation(
            final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         if (annotation != null) {
            fieldMetadata.setVisibleCondition(JXPathContext.compile(annotation
                  .getValue()));
         }
      }

      private void processEmptyValueAnnotation(
            final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         final Converter converter = ConvertUtils.lookup(fieldMetadata
               .getFieldClass());
         fieldMetadata.setConverter(converter);
         Object value;
         if (annotation != null) {
            value = converter.convert(fieldMetadata.getFieldClass(), annotation
                  .getValue());
         } else {
            value = DefaultValueRegistry.getInstance().get(
                  fieldMetadata.getFieldClass(), true);
         }
         fieldMetadata.setEmptyValue(value);
      }

      private void processEqualityComparatorAnnotation(
            final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         EqualityComparator comparator;
         if (annotation != null) {
            comparator = getEqualityComparator(fieldMetadata, annotation
                  .getValue());
         } else {
            comparator = EqualityComparatorRegistry.getInstance()
                  .getDefaultEqualityComparatorFor(
                        fieldMetadata.getFieldClass());
         }
         fieldMetadata.setEqualityComparator(comparator);
      }

      private void processEmptyResolverAnnotation(
            final FieldMetadata fieldMetadata,
            final UntypedAnnotationProxy annotation) {
         EmptyResolver resolver;
         if (annotation != null) {
            resolver = getEmptyResolver(fieldMetadata, annotation.getValue());
         } else {
            resolver = EmptyResolverRegistry.getInstance()
                  .getDefaultEmptyResolverFor(fieldMetadata.getFieldClass());
         }
         fieldMetadata.setEmptyResolver(resolver);
      }

      private EqualityComparator getEqualityComparator(
            final FieldMetadata fieldMetadata, final String attributesLine) {
         final Map attributesMap = getAttributesMap(attributesLine);
         final String comparatorClass = (String) attributesMap.remove("class");
         if (comparatorClass == null) {
            return EqualityComparatorRegistry.getInstance()
                  .getDefaultEqualityComparatorFor(
                        fieldMetadata.getFieldClass(), attributesMap);
         }
         return EqualityComparatorRegistry.getInstance().getEqualityComparator(
               comparatorClass, attributesMap);
      }

      private EmptyResolver getEmptyResolver(final FieldMetadata fieldMetadata,
            final String attributesLine) {
         final Map attributesMap = getAttributesMap(attributesLine);
         final String resolverClass = (String) attributesMap.remove("class");
         if (resolverClass == null) {
            return EmptyResolverRegistry.getInstance()
                  .getDefaultEmptyResolverFor(fieldMetadata.getFieldClass(),
                        attributesMap);
         }
         return EmptyResolverRegistry.getInstance().getEmptyResolver(
               resolverClass, attributesMap);
      }

      /**
       * Reads a string line containing <code>key1=value1 key2=value2</code>
       * and makes a Map.
       * 
       * @param attributesLine
       * @return
       */
      private Map getAttributesMap(final String attributesLine) {
         final StringTokenizer spaceSeparator = new StringTokenizer(
               attributesLine);
         final Map attributesMap = new HashMap();
         while (spaceSeparator.hasMoreTokens()) {
            final StringTokenizer equalSeparator = new StringTokenizer(
                  spaceSeparator.nextToken(), "=");
            if (equalSeparator.countTokens() == 2) {
               attributesMap.put(equalSeparator.nextToken(), equalSeparator
                     .nextToken());
            }
         }
         return attributesMap;
      }
   }
}