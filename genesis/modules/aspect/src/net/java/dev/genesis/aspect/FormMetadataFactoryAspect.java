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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.reflection.FieldEntry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.ui.metadata.DataProviderMetadata;
import net.java.dev.genesis.ui.metadata.FieldMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadata;
import net.java.dev.genesis.ui.metadata.FormMetadataFactory;
import net.java.dev.genesis.ui.metadata.MemberMetadata;
import net.java.dev.genesis.ui.metadata.MethodMetadata;
import net.java.dev.genesis.util.GenesisUtils;
import net.java.dev.reusablecomponents.lang.Enum;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.aspectwerkz.CrossCuttingInfo;
import org.codehaus.aspectwerkz.annotation.Annotation;
import org.codehaus.aspectwerkz.annotation.AnnotationInfo;
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
      public interface AnnotationHandler {
         public void processFormAnnotation(final FormMetadata formMetadata,
               final Annotation annotation);

         public void processFieldAnnotation(final FormMetadata formMetadata,
               final FieldMetadata fieldMetadata, final Annotation annotation);

         public void processMethodAnnotation(final FormMetadata formMetadata,
               final MethodMetadata methodMetadata, final Annotation annotation);
      }

      public static class MetadataAttribute extends Enum {
         public static class ConditionAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {

               final String value = ((UntypedAnnotationProxy) annotation)
                     .getValue();
               final int equalIndex = value.indexOf('=');
               if (equalIndex > 0) {
                  formMetadata.addNamedCondition(value.substring(0, equalIndex)
                        .trim(), JXPathContext.compile(value
                        .substring(equalIndex + 1)));
               }
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processFormAnnotation(formMetadata, annotation);
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               processFormAnnotation(formMetadata, annotation);
            }
         }

         public static class EnabledWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EnabledWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processMemberAnnotation(formMetadata, fieldMetadata, annotation);
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getActionMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "EnabledWhen must be a field or action annotation");
               }
               
               processMemberAnnotation(formMetadata, methodMetadata
                     .getActionMetadata(), annotation);
            }

            private void processMemberAnnotation(
                  final FormMetadata formMetadata,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               memberMetadata
                     .setEnabledCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class VisibleWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "VisibleWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               processMemberAnnotation(formMetadata, fieldMetadata, annotation);
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getActionMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "VisibleWhen must be a field or action annotation");
               }

               processMemberAnnotation(formMetadata, methodMetadata
                     .getActionMetadata(), annotation);
            }

            private void processMemberAnnotation(
                  final FormMetadata formMetadata,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               memberMetadata
                     .setVisibleCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class ValidateBeforeAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ValidateBefore cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ValidateBefore cannot be a field annotation");
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {

               if (methodMetadata.getActionMetadata() == null) {
                  throw new UnsupportedOperationException("ValidateBefore must be an action annotation");
               }

               methodMetadata.getActionMetadata().setValidateBefore(
                     annotation != null);
            }
         }

         public static class CallWhenAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "CallWhen cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "CallWhen cannot be a field annotation");
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               methodMetadata
                     .setCallCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }
         }

         public static class DataProviderAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "DataProvider cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "DataProvider cannot be a field annotation");
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {

               final Map attributesMap = GenesisUtils.getAttributesMap(
                     ((UntypedAnnotationProxy) annotation).getValue());
               final String widgetName = (String)attributesMap
                     .get("widgetName");
               final String objectFieldName = (String)attributesMap
                     .get("objectField");
               final String indexFieldName = (String)attributesMap
                     .get("indexField");
               final String callOnInit = (String)attributesMap
                     .get("callOnInit");

               final DataProviderMetadata dataProviderMetadata = methodMetadata
                     .getDataProviderMetadata();

               if (widgetName == null && objectFieldName == null && indexFieldName == null) {
                  throw new RuntimeException("At least one of widgetName, objectField or " + 
                        "indexField must be specified for @DataProvider in " +
                        methodMetadata.getMethodEntry().getMethodName());
               }

               dataProviderMetadata.setCallOnInit(!"false".equals(callOnInit));

               PropertyDescriptor[] descriptors = PropertyUtils
                     .getPropertyDescriptors(formMetadata.getFormClass());

               Map descriptorsPerPropertyName = new HashMap();

               for (int i = 0; i < descriptors.length; i++) {
                  descriptorsPerPropertyName.put(descriptors[i].getName(), 
                        descriptors[i]);
               }

               if (objectFieldName != null) {
                  PropertyDescriptor descriptor = (PropertyDescriptor)
                        descriptorsPerPropertyName.get(objectFieldName);

                  if (descriptor == null) {
                     throw new RuntimeException("The object "
                           + formMetadata.getFormClass()
                           + " doesn´t have a field called " + objectFieldName);
                  }

                  final Class fieldType = descriptor.getPropertyType();

                  if (fieldType.isPrimitive()
                        || (fieldType.isArray() &&
                              fieldType.getComponentType().isPrimitive())) {
                     throw new RuntimeException("The object " + objectFieldName
                           + " has an object field called " + objectFieldName
                           + " that cannot be primitive or array of primitives");
                  }

                  dataProviderMetadata.setObjectField(new FieldEntry(
                        descriptor.getName(), descriptor.getPropertyType()));
               }

               if (indexFieldName != null) {
                  PropertyDescriptor descriptor = (PropertyDescriptor)
                        descriptorsPerPropertyName.get(indexFieldName);

                  if (descriptor == null) {
                     throw new RuntimeException("The object "
                           + formMetadata.getFormClass()
                           + " doesn´t have a field called " + indexFieldName);
                  }

                  final Class fieldType = descriptor.getPropertyType()
                        .isArray() ? descriptor.getPropertyType()
                        .getComponentType() : descriptor.getPropertyType();

                  if (!Collection.class.isAssignableFrom(descriptor
                        .getPropertyType())
                        && !Integer.TYPE.isAssignableFrom(fieldType)
                        && !Integer.class.isAssignableFrom(fieldType)) {
                     throw new RuntimeException(
                           "The object "
                                 + objectFieldName
                                 + " has an index field called "
                                 + indexFieldName
                                 + " that's not an Integer, int, Collection of them, or array of them.");
                  }

                  dataProviderMetadata.setIndexField(new FieldEntry(descriptor
                        .getName(), descriptor.getPropertyType()));
               }

               dataProviderMetadata
                     .setWidgetName(widgetName != null ? widgetName
                           : objectFieldName != null ? objectFieldName
                                 : indexFieldName);
            }
         }

         public static class ClearOnAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "ClearOn cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata
                     .setClearOnCondition(JXPathContext
                           .compile(((UntypedAnnotationProxy) annotation)
                                 .getValue()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getDataProviderMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "ClearOn cannot be a method annotation that's not a DataProvider method");
               }

               methodMetadata.getDataProviderMetadata().setClearOnCondition(
                     JXPathContext.compile(((UntypedAnnotationProxy) annotation).getValue()));
            }
         }

         public static class EqualityComparatorAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EqualityComparator cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setEqualityComparator(getEqualityComparator(
                     fieldMetadata, ((UntypedAnnotationProxy) annotation)
                           .getValue()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EqualityComparator cannot be a method annotation");
            }

            private EqualityComparator getEqualityComparator(
                  final FieldMetadata fieldMetadata, final String attributesLine) {
               final Map attributesMap = GenesisUtils
                     .getAttributesMap(attributesLine);
               final String comparatorClass = (String) attributesMap
                     .remove("class");
               if (comparatorClass == null) {
                  return EqualityComparatorRegistry.getInstance()
                        .getDefaultEqualityComparatorFor(
                              fieldMetadata.getFieldClass(), attributesMap);
               }
               return EqualityComparatorRegistry.getInstance()
                     .getEqualityComparator(comparatorClass, attributesMap);
            }
         }

         public static class EmptyResolverAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyResolver cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setEmptyResolver(getEmptyResolver(fieldMetadata,
                     ((UntypedAnnotationProxy) annotation).getValue()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyResolver cannot be a method annotation");
            }

            private EmptyResolver getEmptyResolver(
                  final FieldMetadata fieldMetadata, final String attributesLine) {
               final Map attributesMap = GenesisUtils
                     .getAttributesMap(attributesLine);
               final String resolverClass = (String) attributesMap
                     .remove("class");
               if (resolverClass == null) {
                  return EmptyResolverRegistry.getInstance()
                        .getDefaultEmptyResolverFor(
                              fieldMetadata.getFieldClass(), attributesMap);
               }
               return EmptyResolverRegistry.getInstance().getEmptyResolver(
                     resolverClass, attributesMap);
            }
         }

         public static class EmptyValueAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyValue cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               final Converter converter = ConvertUtils.lookup(fieldMetadata
                     .getFieldClass());
               fieldMetadata.setConverter(converter);
               fieldMetadata.setEmptyValue(converter.convert(fieldMetadata
                     .getFieldClass(), ((UntypedAnnotationProxy) annotation)
                     .getValue()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyValue cannot be a method annotation");
            }

         }

         public static final MetadataAttribute CONDITION = new MetadataAttribute(
               "Condition", new ConditionAnnotationHandler());
         public static final MetadataAttribute ENABLED_WHEN = new MetadataAttribute(
               "EnabledWhen", new EnabledWhenAnnotationHandler());
         public static final MetadataAttribute VISIBLE_WHEN = new MetadataAttribute(
               "VisibleWhen", new VisibleWhenAnnotationHandler());
         public static final MetadataAttribute VALIDATE_BEFORE = new MetadataAttribute(
               "ValidateBefore", new ValidateBeforeAnnotationHandler());
         public static final MetadataAttribute DATA_PROVIDER = new MetadataAttribute(
               "DataProvider", new DataProviderAnnotationHandler());
         public static final MetadataAttribute CALL_WHEN = new MetadataAttribute(
               "CallWhen", new CallWhenAnnotationHandler());
         public static final MetadataAttribute CLEAR_ON = new MetadataAttribute(
               "ClearOn", new ClearOnAnnotationHandler());
         public static final MetadataAttribute EQUALITY_COMPARATOR = new MetadataAttribute(
               "EqualityComparator", new EqualityComparatorAnnotationHandler());
         public static final MetadataAttribute EMPTY_RESOLVER = new MetadataAttribute(
               "EmptyResolver", new EmptyResolverAnnotationHandler());
         public static final MetadataAttribute EMPTY_VALUE = new MetadataAttribute(
               "EmptyValue", new EmptyValueAnnotationHandler());

         private final AnnotationHandler handler;

         public MetadataAttribute(String name, AnnotationHandler handler) {
            super(name);
            this.handler = handler;
         }

         public AnnotationHandler getHandler() {
            return handler;
         }

         public static MetadataAttribute get(String name) {
            return (MetadataAttribute) Enum.get(MetadataAttribute.class, name);
         }
      }

      private final Map cache = new HashMap();

      public FormMetadata getFormMetadata(final Class formClass) {
         FormMetadata formMetadata = (FormMetadata) cache.get(formClass);
         if (formMetadata == null) {
            formMetadata = new FormMetadata(formClass);
            processAnnotations(formMetadata);
            cache.put(formClass, formMetadata);
         }
         return formMetadata;
      }

      private void processAnnotations(final FormMetadata formMetadata) {
         processFormAnnotations(formMetadata);
         processFieldsAnnotations(formMetadata);
         processMethodsAnnotations(formMetadata);
      }

      private void processFormAnnotations(final FormMetadata formMetadata) {
         final List annotations = Annotations.getAnnotationInfos(formMetadata
               .getFormClass());
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processFormAnnotation(formMetadata,
                  info.getAnnotation());
         }
      }

      private void processFieldsAnnotations(final FormMetadata formMetadata) {
         final Class clazz = formMetadata.getFormClass();
         final PropertyDescriptor[] propertyDescriptors = PropertyUtils
               .getPropertyDescriptors(clazz);
         PropertyDescriptor propDesc;
         FieldMetadata fieldMetadata;
         for (int i = 0; i < propertyDescriptors.length; i++) {
            propDesc = propertyDescriptors[i];

            // Ignoring java.lang.Object.getClass()
            if (propDesc.getName().equals("class")
                  || propDesc.getReadMethod() == null
                  || Annotations.getAnnotation("NotBound", propDesc
                        .getReadMethod()) != null) {
               continue;
            }

            fieldMetadata = new FieldMetadata(propDesc.getName(), propDesc
                  .getPropertyType(), propDesc.getWriteMethod() != null);
            formMetadata.addFieldMetadata(propDesc.getName(), fieldMetadata);
            processFieldAnnotations(formMetadata, fieldMetadata, getReadMethod(
                  clazz, propDesc));

         }
      }

      private void processFieldAnnotations(final FormMetadata formMetadata,
            final FieldMetadata fieldMetadata, final Method fieldGetterMethod) {
         final List annotations = Annotations
               .getAnnotationInfos(fieldGetterMethod);
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processFieldAnnotation(formMetadata,
                  fieldMetadata, info.getAnnotation());
         }
      }

      private void processMethodsAnnotations(final FormMetadata formMetadata) {
         final Method[] methods = formMetadata.getFormClass().getMethods();
         boolean isAction;
         boolean isProvider;
         MethodMetadata methodMetadata;

         for (int i = 0; i < methods.length; i++) {
            isAction = Annotations.getAnnotation("Action", methods[i]) != null;
            isProvider = Annotations.getAnnotation("DataProvider", methods[i]) != null;

            if (!isAction && !isProvider) {
               continue;
            }

            methodMetadata = new MethodMetadata(methods[i], isAction, isProvider);
            formMetadata.addMethodMetadata(methods[i], methodMetadata);
            processMethodAnnotations(formMetadata, methodMetadata, methods[i]);
         }
      }

      private void processMethodAnnotations(final FormMetadata formMetadata,
            final MethodMetadata methodMetadata, final Method actionMethod) {
         final List annotations = Annotations.getAnnotationInfos(actionMethod);
         AnnotationInfo info;
         MetadataAttribute attr;
         for (Iterator iter = annotations.iterator(); iter.hasNext();) {
            info = (AnnotationInfo) iter.next();
            attr = MetadataAttribute.get(info.getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processMethodAnnotation(formMetadata,
                  methodMetadata, info.getAnnotation());
         }
      }

      // Work around for a bug in JDK 1.4.2
      private Method getReadMethod(Class clazz, PropertyDescriptor pd) {
         Method method = pd.getReadMethod();

         if (clazz.equals(method.getDeclaringClass())) {
            return method;
         }

         try {
            method = clazz.getMethod(method.getName(), 
                  method.getParameterTypes());
            return (method == null) ? pd.getReadMethod() : method;
         } catch (NoSuchMethodException nsme) {
            return method;
         }
      }
   }
}