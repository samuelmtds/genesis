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
package net.java.dev.genesis.script;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.ClassInfo;
import net.sf.cglib.core.CodeEmitter;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.Local;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.RejectModifierPredicate;
import net.sf.cglib.core.Signature;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.proxy.Mixin;

import org.apache.commons.beanutils.PropertyUtils;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public class DynamicProxy {
   public static ScriptableObject proxy(final Object object) {
      final ScriptableObjectImpl scriptableImpl = new ScriptableObjectImpl();
      Mixin.Generator gen = new Mixin.Generator() {
         public void generateClass(ClassVisitor v) {
            new ScriptEmitter(object, scriptableImpl, v, getClassName());
         }
      };

      gen.setStyle(Mixin.STYLE_EVERYTHING);
      gen.setDelegates(new Object[] { object, scriptableImpl });

      return (ScriptableObject) gen.create();
   }

   public static class ScriptEmitter extends ClassEmitter {
      private static final String FIELD_NAME = "CGLIB$ORIGINALOBJECT";
      private static final String RESOLVER_FIELD = "CGLIB$RESOLVER";
      private static final Type MIXIN = TypeUtils
            .parseType("net.sf.cglib.proxy.Mixin");
      private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils
            .parseConstructor("Object[]");
      private static final Signature NEW_INSTANCE = TypeUtils
            .parseSignature("net.sf.cglib.proxy.Mixin newInstance(Object[])");
      private Object root;

      public ScriptEmitter(Object root, Object resolver, ClassVisitor v,
            String className) {
         super(v);

         final Type rootType = Type.getType(root.getClass());
         generateClass(root, className, rootType);
      }

      protected void generateClass(Object root, String className, Type rootType) {
         this.root = root;

         final Type resolverType = Type.getType(ScriptableObjectImpl.class);

         begin_class(Constants.ACC_PUBLIC, className, MIXIN, TypeUtils
               .getTypes(getInterfaces(root.getClass())), Constants.SOURCE_FILE);
         // Default Constructor
         EmitUtils.null_constructor(this);
         EmitUtils.factory_method(this, NEW_INSTANCE);

         // Declaring private field, handling the original object
         // e.g.: private <OBJECT_CLASS> CGLIB$ORIGINALOBJECT
         declare_field(Constants.ACC_PRIVATE, FIELD_NAME, rootType, null, null);
         declare_field(Constants.ACC_PRIVATE, RESOLVER_FIELD, resolverType,
               null, null);

         Collection fields = new ArrayList(getFields(root.getClass()));

         for (Iterator iter = fields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();
            // Declaring each public field
            // e.g.: public <MODIFIERS> <FIELD_CLASS> <FIELD_NAME>;
            declare_field(field.getModifiers(), field.getName(), Type
                  .getType(field.getType()), null, null);
         }

         // Static block
         Collection staticFields = CollectionUtils.filter(fields,
               new Predicate() {
                  public boolean evaluate(Object arg) {
                     return Modifier.isStatic(((Member) arg).getModifiers());
                  }
               });

         CodeEmitter ce = begin_static();

         for (Iterator iter = staticFields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();
            // For every static field
            // e.g.: <FIELD_NAME> = <ORIGINAL_OBJECT_CLASS>.<FIELD_NAME>
            ce.getstatic(Type.getType(root.getClass()), field.getName(), Type
                  .getType(field.getType()));
            ce.putfield(field.getName());
         }

         ce.end_method();

         // Creating constructor
         // e.g.: public <CLASSNAME>(<ORIGINAL_OBJECT_CLASS> object,
         // ScriptableObjectMixin resolver)
         CodeEmitter e = begin_method(Constants.ACC_PUBLIC,
               CSTRUCT_OBJECT_ARRAY, null, null);
         Collection nonStaticFields = CollectionUtils.filter(fields,
               new RejectModifierPredicate(Modifier.STATIC));

         e.load_this();
         e.super_invoke_constructor();
         e.load_this();
         e.load_arg(0);
         e.aaload(0);
         e.checkcast(rootType);
         e.putfield(FIELD_NAME);

         e.load_this();
         e.load_arg(0);
         e.aaload(1);
         e.checkcast(resolverType);
         e.putfield(RESOLVER_FIELD);

         for (Iterator iter = nonStaticFields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();
            // For every non static field
            // this.<FIELD_NAME> = object.<FIELD_NAME>
            e.load_this();
            e.load_this();
            e.getfield(FIELD_NAME);
            e
                  .getfield(rootType, field.getName(), Type.getType(field
                        .getType()));
            e.putfield(field.getName());
         }

         // End of constructor
         e.return_value();
         e.end_method();

         Set unique = new HashSet();
         PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(root);

         // For every getter and setter method, create an equivalent proxy
         // getter or setter method
         for (int i = 0; i < desc.length; i++) {
            if (acceptMethod(desc[i].getReadMethod())) {
               declareMethod(desc[i].getReadMethod(), desc[i].getName());
               unique.add(desc[i].getReadMethod());
            }

            if (acceptMethod(desc[i].getWriteMethod())) {
               declareMethod(desc[i].getWriteMethod(), null);
               unique.add(desc[i].getWriteMethod());
            }
         }

         // For other public methods, proxy the method
         Method[] methods = getMethods(root.getClass());

         for (int j = 0; j < methods.length; j++) {
            if (unique.contains(methods[j])) {
               continue;
            }

            declareMethod(methods[j], null);
            unique.add(methods[j]);
         }

         methods = getMethods(ScriptableObjectImpl.class);

         for (int j = 0; j < methods.length; j++) {
            if (unique.contains(methods[j])) {
               continue;
            }

            declareMethod(methods[j], null, true);
            unique.add(methods[j]);
         }

         end_class();
      }

      protected boolean acceptMethod(Method method) {
         if (method == null) {
            return false;
         }

         if (!Modifier.isPublic(method.getModifiers())) {
            return false;
         }

         if (method.getDeclaringClass() == Object.class) {
            final String name = method.getName();

            return (name.equals("hashCode") || name.equals("equals") || name
                  .equals("toString"));
         }

         return true;
      }

      protected void declareMethod(Method m, String propertyName) {
         declareMethod(m, propertyName, false);
      }

      protected void declareMethod(Method m, String propertyName, boolean flag) {
         MethodInfo method = ReflectUtils.getMethodInfo(m);
         CodeEmitter e = EmitUtils.begin_method(this, method, Modifier.PUBLIC);

         if (Modifier.isStatic(m.getModifiers())) {
            e.load_args();
            e.invoke_static(Type.getType(m.getDeclaringClass()), method
                  .getSignature());
            e.return_value();
            e.end_method();

            return;
         }

         if (propertyName != null) {
            proxyMethod(e, m, method, propertyName);

            return;
         }

         if (flag && m.getName().equals("beforeEval")) {
            declareBeforeEval(e, root);

            return;
         } else if (flag && m.getName().equals("afterEval")) {
            declareAfterEval(e, root);

            return;
         }

         e.load_this();
         e.getfield(flag ? RESOLVER_FIELD : FIELD_NAME);
         e.load_args();
         e.invoke(method);

         e.return_value();
         e.end_method();

         return;
      }

      protected void proxyMethod(CodeEmitter e, Method m, MethodInfo method,
            String propertyName) {
         Local local = e.make_local(Type.getType(m.getReturnType()));
         e.load_this();
         e.getfield(FIELD_NAME);
         e.load_args();
         e.invoke(method);
         e.store_local(local);

         e.load_this();
         e.getfield(RESOLVER_FIELD);
         e.push(propertyName);
         e.load_local(local);

         if (m.getReturnType().isPrimitive()) {
            e.box(Type.getType(m.getReturnType()));
         }

         e.push(m.getReturnType().isPrimitive());

         MethodInfo proxyMethod = new MethodInfo() {
            private ClassInfo ci;
            private Type[] exceptionTypes = new Type[0];
            private Signature sig = TypeUtils
                  .parseSignature("void visitField(String, Object, boolean)");

            public ClassInfo getClassInfo() {
               if (ci == null) {
                  ci = ReflectUtils.getClassInfo(ScriptableObjectImpl.class);
               }

               return ci;
            }

            public int getModifiers() {
               return Modifier.PUBLIC;
            }

            public Signature getSignature() {
               return sig;
            }

            public Type[] getExceptionTypes() {
               return exceptionTypes;
            }

            public Attribute getAttribute() {
               return null;
            }
         };

         e.invoke(proxyMethod);
         e.load_local(local);

         e.return_value();
         e.end_method();
      }

      protected void declareBeforeEval(CodeEmitter e, Object root) {
         Collection fields = new ArrayList(getFields(root.getClass()));
         Collection nonStaticFields = CollectionUtils.filter(fields,
               new RejectModifierPredicate(Modifier.FINAL));

         for (Iterator iter = nonStaticFields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();

            if (Modifier.isStatic(field.getModifiers())) {
               e.getfield(field.getName());
               e.putstatic(Type.getType(root.getClass()), field.getName(), Type
                     .getType(field.getType()));

               continue;
            }

            // For every non static field
            // this.<FIELD_NAME> = object.<FIELD_NAME>
            e.load_this();
            e.getfield(FIELD_NAME);
            e.load_this();
            e.getfield(field.getName());
            e.putfield(Type.getType(root.getClass()), field.getName(), Type
                  .getType(field.getType()));
         }
         
         e.return_value();
         e.end_method();
      }

      protected void declareAfterEval(CodeEmitter e, Object root) {
         Collection fields = new ArrayList(getFields(root.getClass()));
         Collection nonStaticFields = CollectionUtils.filter(fields,
               new RejectModifierPredicate(Modifier.FINAL));

         for (Iterator iter = nonStaticFields.iterator(); iter.hasNext();) {
            Field field = (Field) iter.next();

            if (Modifier.isStatic(field.getModifiers())) {
               e.getstatic(Type.getType(root.getClass()), field.getName(), Type
                     .getType(field.getType()));
               e.putfield(field.getName());

               continue;
            }

            // For every non static field
            // this.<FIELD_NAME> = object.<FIELD_NAME>
            e.load_this();
            e.load_this();
            e.getfield(FIELD_NAME);
            e.getfield(Type.getType(root.getClass()), field.getName(), Type
                  .getType(field.getType()));
            e.putfield(field.getName());
         }

         e.return_value();
         e.end_method();
      }

      protected Class[] getInterfaces(Class rootClass) {
         List list = new ArrayList();
         list.add(ScriptableObject.class);
         ReflectUtils.addAllInterfaces(rootClass, list);

         return (Class[]) list.toArray(new Class[list.size()]);
      }

      protected List getFields(Class type) {
         return Arrays.asList(type.getFields());
      }

      protected Method[] getMethods(Class type) {
         List methods = new ArrayList(Arrays.asList(type.getMethods()));
         CollectionUtils.filter(methods, new Predicate() {
            public boolean evaluate(Object arg) {
               return acceptMethod((Method) arg);
            }
         });

         return (Method[]) methods.toArray(new Method[methods.size()]);
      }
   }

}
