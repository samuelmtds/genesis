/*
 * The Genesis Project
 * Copyright (C) 2004-2006  Summa Technologies do Brasil Ltda.
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
import java.util.Map;

import net.java.dev.genesis.annotation.Action;
import net.java.dev.genesis.annotation.CallWhen;
import net.java.dev.genesis.annotation.ClearOn;
import net.java.dev.genesis.annotation.Condition;
import net.java.dev.genesis.annotation.DataProvider;
import net.java.dev.genesis.annotation.EmptyValue;
import net.java.dev.genesis.annotation.EnabledWhen;
import net.java.dev.genesis.annotation.NotBound;
import net.java.dev.genesis.annotation.ValidateBefore;
import net.java.dev.genesis.annotation.VisibleWhen;
import net.java.dev.genesis.cloning.Cloner;
import net.java.dev.genesis.cloning.ClonerRegistry;
import net.java.dev.genesis.equality.EqualityComparator;
import net.java.dev.genesis.equality.EqualityComparatorRegistry;
import net.java.dev.genesis.reflection.ClassesCache;
import net.java.dev.genesis.reflection.FieldEntry;
import net.java.dev.genesis.resolvers.EmptyResolver;
import net.java.dev.genesis.resolvers.EmptyResolverRegistry;
import net.java.dev.genesis.script.Script;
import net.java.dev.genesis.script.ScriptExpression;
import net.java.dev.genesis.script.ScriptFactory;
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
import org.codehaus.aspectwerkz.aspect.management.Mixins;
import org.codehaus.backport175.reader.Annotation;
import org.codehaus.backport175.reader.Annotations;

public class FormMetadataFactoryAspect {
   /**
    * @Mixin(pointcut="formMetadataFactoryIntroduction", isTransient=true, deploymentModel="perJVM")
    */
   public static class AspectFormMetadataFactory implements FormMetadataFactory {
      private final Map FACTORIES = new HashMap();
      {
         FACTORIES.put("jxpath", "net.java.dev.genesis.script.jxpath.JXPathScriptFactory");
         FACTORIES.put("javascript", "net.java.dev.genesis.script.bsf.BSFScriptFactory");
         FACTORIES.put("beanshell", "net.java.dev.genesis.script.bsf.BSFScriptFactory");
         FACTORIES.put("el", "net.java.dev.genesis.script.el.ELScriptFactory");
      }

      private String scriptFactoryParameter;
      private ScriptFactory scriptFactory;
      private Script script;
      
      public AspectFormMetadataFactory() 
            throws Exception {
         Map parameters = Mixins.getParameters(getClass(), getClass()
               .getClassLoader());
         scriptFactoryParameter = (String)parameters.get("scriptFactory");
         String scriptFactoryProperties = (String)parameters
               .get("scriptFactoryProperties");

         Map properties = new HashMap();

         if (scriptFactoryParameter == null) {
            scriptFactoryParameter = "jxpath";
         } 

         String scriptFactoryName = (String)FACTORIES.get(scriptFactoryParameter);
         if (scriptFactoryName == null) {
            scriptFactoryName = scriptFactoryParameter;
         } else {
            properties.put("lang", scriptFactoryParameter);
         }

         scriptFactory = (ScriptFactory)ClassesCache
               .getClass(scriptFactoryName).newInstance();

         if (scriptFactoryProperties != null) {
            properties.putAll(GenesisUtils
                  .getAttributesMap(scriptFactoryProperties, ",", "="));
         }

         if (!properties.isEmpty()) {
            PropertyUtils.copyProperties(scriptFactory, properties);
         }
      }
      
      private Script getScript() {         
         if (script == null) {
            script = scriptFactory.newScript();
         }
         return script;
      }

      public boolean isCurrentScriptFactoryNameFor(String alias) {
         if (scriptFactoryParameter.equals(alias)) {
            return true;
         }

         String name = (String)FACTORIES.get(alias);
         return scriptFactoryParameter.equals(name);
      }

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

               final Condition condition = (Condition) annotation;
               final String[] values = condition.value();
               
               for (int i = 0; i < values.length; i++) {
                  final int equalIndex = values[i].indexOf('=');
                  if (equalIndex > 0) {
                     formMetadata.addNamedCondition(values[i].substring(0,
                           equalIndex).trim(), compile(
                           formMetadata.getScript(), values[i]
                                 .substring(equalIndex + 1)));
                  }   
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
               processMemberAnnotation(formMetadata.getScript(), fieldMetadata, annotation);
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getActionMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "EnabledWhen must be a field or action annotation");
               }
               
               processMemberAnnotation(formMetadata.getScript(), methodMetadata.getActionMetadata(), 
                     annotation);
            }

            private void processMemberAnnotation(final Script script,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               EnabledWhen annon = (EnabledWhen) annotation;
               memberMetadata
                     .setEnabledCondition(compile(script, annon.value()));
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
               processMemberAnnotation(formMetadata.getScript(), fieldMetadata, annotation);
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getActionMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "VisibleWhen must be a field or action annotation");
               }

               processMemberAnnotation(formMetadata.getScript(), methodMetadata.getActionMetadata(), 
                     annotation);
            }

            private void processMemberAnnotation(final Script script,
                  final MemberMetadata memberMetadata,
                  final Annotation annotation) {
               VisibleWhen annon = (VisibleWhen) annotation;
               String[] values = annon.value();
               if (values.length == 0) {
                  throw new IllegalArgumentException(
                        "VisibleWhen must define at least one script condition");
               }

               if (values.length == 1) {
                  memberMetadata
                  .setVisibleCondition(compile(script, values[0]));
               } else if (values.length % 2 != 0) {
                  throw new IllegalArgumentException(
                        "VisibleWhen must define at least one script condition or pairs of script conditions");
               } else {
                  AspectFormMetadataFactory mixin = (AspectFormMetadataFactory) Mixins
                        .mixinOf(AspectFormMetadataFactory.class);
                  for (int i = 0; i < values.length / 2; i+=2) {
                     if (!mixin.isCurrentScriptFactoryNameFor(values[i])) {
                        continue;
                     }

                     memberMetadata.setVisibleCondition(compile(script,
                           values[i + 1]));
                     break;
                  }
               }
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
               CallWhen annon = (CallWhen) annotation;
               methodMetadata
                     .setCallCondition(compile(formMetadata.getScript(), annon.value()));
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

               final DataProvider annon = (DataProvider) annotation;
               final String widgetName = annon.widgetName();
               final String objectFieldName = annon.objectField();
               final String indexFieldName = annon.indexField();
               final boolean callOnInit = annon.callOnInit();
               final boolean resetSelection = annon.resetSelection();

               final DataProviderMetadata dataProviderMetadata = methodMetadata
                     .getDataProviderMetadata();

               if (GenesisUtils.isBlank(widgetName) && GenesisUtils.isBlank(objectFieldName) && GenesisUtils.isBlank(indexFieldName)) {
                  throw new RuntimeException("At least one of widgetName, objectField or " + 
                        "indexField must be specified for @DataProvider in " +
                        methodMetadata.getMethodEntry().getMethodName());
               }

               dataProviderMetadata.setCallOnInit(callOnInit);
               dataProviderMetadata.setResetSelection(resetSelection);

               PropertyDescriptor[] descriptors = PropertyUtils
                     .getPropertyDescriptors(formMetadata.getFormClass());

               Map descriptorsPerPropertyName = new HashMap();

               for (int i = 0; i < descriptors.length; i++) {
                  descriptorsPerPropertyName.put(descriptors[i].getName(), 
                        descriptors[i]);
               }

               if (!GenesisUtils.isBlank(objectFieldName)) {
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

               if (!GenesisUtils.isBlank(indexFieldName)) {
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
                     .setWidgetName(!GenesisUtils.isBlank(widgetName) ? widgetName
                           : !GenesisUtils.isBlank(objectFieldName) ? objectFieldName
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
               ClearOn annon = (ClearOn) annotation;
               fieldMetadata
                     .setClearOnCondition(compile(formMetadata.getScript(), annon.value()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               if (methodMetadata.getDataProviderMetadata() == null) {
                  throw new UnsupportedOperationException(
                        "ClearOn cannot be a method annotation that's not a DataProvider method");
               }

               ClearOn annon = (ClearOn) annotation;
               methodMetadata.getDataProviderMetadata().setClearOnCondition(
                     compile(formMetadata.getScript(), annon.value()));
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
               net.java.dev.genesis.annotation.EqualityComparator annon =
                  (net.java.dev.genesis.annotation.EqualityComparator)annotation;
               
               
               fieldMetadata.setEqualityComparator(getEqualityComparator(
                     fieldMetadata, annon) );
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EqualityComparator cannot be a method annotation");
            }

            private EqualityComparator getEqualityComparator(
                  final FieldMetadata fieldMetadata, final net.java.dev.genesis.annotation.EqualityComparator annon) {
               
               final Map map = GenesisUtils.getAttributesMap(annon.properties());
               Class klazz = annon.value();
               if (klazz == null || Object.class.equals(klazz)) {
                  return EqualityComparatorRegistry.getInstance()
                        .getDefaultEqualityComparatorFor(
                              fieldMetadata.getFieldClass(), map);
               }
               return EqualityComparatorRegistry.getInstance()
                     .getEqualityComparator(annon.value().getName(), map);
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
                     (net.java.dev.genesis.annotation.EmptyResolver) annotation));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyResolver cannot be a method annotation");
            }

            private EmptyResolver getEmptyResolver(
                  final FieldMetadata fieldMetadata, final net.java.dev.genesis.annotation.EmptyResolver annon) {
               
               Map map = GenesisUtils.getAttributesMap(annon.properties());
               Class klazz = annon.value();
               if (klazz == null || Object.class.equals(klazz)) {
                  return EmptyResolverRegistry.getInstance()
                        .getDefaultEmptyResolverFor(
                              fieldMetadata.getFieldClass(), map);
               }
               return EmptyResolverRegistry.getInstance().getEmptyResolver(
                     annon.value().getName(), map);
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
                     .getFieldClass(), ((EmptyValue) annotation)
                     .value()));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "EmptyValue cannot be a method annotation");
            }

         }

         public static class ClonerAnnotationHandler implements
               AnnotationHandler {
            public void processFormAnnotation(final FormMetadata formMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "Cloner cannot be a form annotation");
            }

            public void processFieldAnnotation(final FormMetadata formMetadata,
                  final FieldMetadata fieldMetadata, final Annotation annotation) {
               fieldMetadata.setCloner(getCloner(fieldMetadata,
                     (net.java.dev.genesis.annotation.Cloner)annotation));
            }

            public void processMethodAnnotation(
                  final FormMetadata formMetadata,
                  final MethodMetadata methodMetadata,
                  final Annotation annotation) {
               throw new UnsupportedOperationException(
                     "Cloner cannot be a method annotation");
            }

            private Cloner getCloner(final FieldMetadata fieldMetadata,
                  final net.java.dev.genesis.annotation.Cloner annon) {
               
               final Map map = GenesisUtils.getAttributesMap(annon.properties());
               Class klazz = annon.value();
               if (klazz == null || Object.class.equals(klazz)) {
                  return ClonerRegistry.getInstance().getDefaultClonerFor(
                        fieldMetadata.getFieldClass(), map);
               }

               return ClonerRegistry.getInstance().getCloner(annon.value().getName(),
                     map);
            }

         }

         public static final MetadataAttribute CONDITION = new MetadataAttribute(
               Condition.class.getName(), new ConditionAnnotationHandler());
         public static final MetadataAttribute ENABLED_WHEN = new MetadataAttribute(
               EnabledWhen.class.getName(), new EnabledWhenAnnotationHandler());
         public static final MetadataAttribute VISIBLE_WHEN = new MetadataAttribute(
               VisibleWhen.class.getName(), new VisibleWhenAnnotationHandler());
         public static final MetadataAttribute VALIDATE_BEFORE = new MetadataAttribute(
               ValidateBefore.class.getName(), new ValidateBeforeAnnotationHandler());
         public static final MetadataAttribute DATA_PROVIDER = new MetadataAttribute(
               DataProvider.class.getName(), new DataProviderAnnotationHandler());
         public static final MetadataAttribute CALL_WHEN = new MetadataAttribute(
               CallWhen.class.getName(), new CallWhenAnnotationHandler());
         public static final MetadataAttribute CLEAR_ON = new MetadataAttribute(
               ClearOn.class.getName(), new ClearOnAnnotationHandler());
         public static final MetadataAttribute EQUALITY_COMPARATOR = new MetadataAttribute(
               net.java.dev.genesis.annotation.EqualityComparator.class.getName(),
               new EqualityComparatorAnnotationHandler());
         public static final MetadataAttribute EMPTY_RESOLVER = new MetadataAttribute(
               net.java.dev.genesis.annotation.EmptyResolver.class.getName(),
               new EmptyResolverAnnotationHandler());
         public static final MetadataAttribute EMPTY_VALUE = new MetadataAttribute(
               EmptyValue.class.getName(), new EmptyValueAnnotationHandler());
         public static final MetadataAttribute CLONER = new MetadataAttribute(
               net.java.dev.genesis.annotation.Cloner.class.getName(),
               new ClonerAnnotationHandler());

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
         
         private static ScriptExpression compile(Script script, String value) {
            return script.compile(value);
         }
      }

      private final Map cache = new HashMap();

      public FormMetadata getFormMetadata(final Class formClass) {
         FormMetadata formMetadata = (FormMetadata) cache.get(formClass);
         if (formMetadata == null) {
            formMetadata = new FormMetadata(formClass, getScript());
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
         final Annotation[] annotations = Annotations.getAnnotations(formMetadata
               .getFormClass());
         MetadataAttribute attr;
         for (int i = 0; i < annotations.length; i++) {
            attr = MetadataAttribute.get(annotations[i].annotationType().getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processFormAnnotation(formMetadata,
                  annotations[i]);
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
                  || Annotations.getAnnotation(NotBound.class, propDesc
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
         final Annotation[] annotations = Annotations.getAnnotations(fieldGetterMethod);
         MetadataAttribute attr;
         for (int i = 0; i < annotations.length; i++) {
            attr = MetadataAttribute.get(annotations[i].annotationType().getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processFieldAnnotation(formMetadata,
                  fieldMetadata, annotations[i]);
         }
      }

      private void processMethodsAnnotations(final FormMetadata formMetadata) {
         final Method[] methods = formMetadata.getFormClass().getMethods();
         boolean isAction;
         boolean isProvider;
         MethodMetadata methodMetadata;

         for (int i = 0; i < methods.length; i++) {
            isAction = Annotations.getAnnotation(Action.class, methods[i]) != null;
            isProvider = Annotations.getAnnotation(DataProvider.class, methods[i]) != null;

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
         final Annotation[] annotations = Annotations.getAnnotations(actionMethod);
         MetadataAttribute attr;
         for (int i = 0; i < annotations.length; i++) {
            attr = MetadataAttribute.get(annotations[i].annotationType().getName());

            if (attr == null) {
               continue;
            }

            attr.getHandler().processMethodAnnotation(formMetadata,
                  methodMetadata, annotations[i]);
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