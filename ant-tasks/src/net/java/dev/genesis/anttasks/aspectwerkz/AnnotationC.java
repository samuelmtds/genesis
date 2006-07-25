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
package net.java.dev.genesis.anttasks.aspectwerkz;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.java.dev.genesis.anttasks.aspectwerkz.annotation.ClassnamePropertiesPreProcessor;
import net.java.dev.genesis.anttasks.aspectwerkz.annotation.CriteriaPreprocessor;
import net.java.dev.genesis.anttasks.aspectwerkz.annotation.DataProviderPreprocessor;
import net.java.dev.genesis.anttasks.aspectwerkz.annotation.MultiStringPreprocessor;
import net.java.dev.genesis.anttasks.aspectwerkz.annotation.SingleStringPreprocessor;

import org.codehaus.backport175.com.thoughtworks.qdox.model.DocletTag;
import org.codehaus.backport175.com.thoughtworks.qdox.model.JavaClass;
import org.codehaus.backport175.com.thoughtworks.qdox.model.JavaField;
import org.codehaus.backport175.com.thoughtworks.qdox.model.JavaMethod;
import org.codehaus.backport175.compiler.AnnotationInterfaceRepository;
import org.codehaus.backport175.compiler.CompilerException;
import org.codehaus.backport175.compiler.MessageHandler;
import org.codehaus.backport175.compiler.bytecode.AnnotationEnhancer;
import org.codehaus.backport175.compiler.javadoc.JavaDocParser;
import org.codehaus.backport175.compiler.javadoc.RawAnnotation;
import org.codehaus.backport175.compiler.javadoc.SourceParseException;
import org.codehaus.backport175.compiler.parser.ParseException;

public class AnnotationC {
   private final MessageHandler m_handler;
   private final ClassLoader m_loader;
   private final boolean m_ignoreUnknown;
   private final JavaDocParser m_javaDocParser;
   private final AnnotationInterfaceRepository m_repository;

   private static final Map annotationsPreProcessors = new HashMap();

   static {
      annotationsPreProcessors
            .put("AfterAction", new MultiStringPreprocessor());
      annotationsPreProcessors.put("BeforeAction",
            new MultiStringPreprocessor());
      annotationsPreProcessors.put("CallWhen", new SingleStringPreprocessor());
      annotationsPreProcessors.put("ClearOn", new SingleStringPreprocessor());
      annotationsPreProcessors.put("Cloner",
            new ClassnamePropertiesPreProcessor());
      annotationsPreProcessors.put("Condition", new SingleStringPreprocessor());
      annotationsPreProcessors.put("Criteria", new CriteriaPreprocessor());
      annotationsPreProcessors.put("EmptyResolver",
            new ClassnamePropertiesPreProcessor());
      annotationsPreProcessors
            .put("EmptyValue", new SingleStringPreprocessor());
      annotationsPreProcessors.put("EnabledWhen",
            new SingleStringPreprocessor());
      annotationsPreProcessors.put("EqualityComparator",
            new ClassnamePropertiesPreProcessor());
      annotationsPreProcessors.put("VisibleWhen",
            new SingleStringPreprocessor());
      annotationsPreProcessors.put("DataProvider",
            new DataProviderPreprocessor());
   }

   private AnnotationC(final ClassLoader loader, final JavaDocParser parser,
         final AnnotationInterfaceRepository repository,
         final MessageHandler handler, final boolean ignoreUnknown) {
      m_loader = loader;
      m_javaDocParser = parser;
      m_repository = repository;
      m_handler = handler;
      m_ignoreUnknown = ignoreUnknown;
   }

   public static void compile(final String[] srcDirs, final String[] srcFiles,
         final String[] classpath, final String destDir,
         final String[] annotationPropertiesFiles,
         final MessageHandler messageHandler, final boolean ignoreUnknown) {

      URL[] classPath = new URL[classpath.length];
      final ClassLoader compilationLoader;
      try {
         for (int i = 0; i < classpath.length; i++) {
            classPath[i] = new File(classpath[i]).toURL();
         }
         compilationLoader = new URLClassLoader(classPath, AnnotationC.class
               .getClassLoader());
      } catch (MalformedURLException e) {
         String message = "URL [" + classPath + "] is not valid: "
               + e.toString();
         messageHandler.error(new CompilerException(message, e));
         return;
      }

      String destDirToUse = destDir;
      if (destDir == null) {
         if (classpath.length != 1) {
            messageHandler.error(new CompilerException(
                  "destDir must be specified since classpath is composite"));
            return;
         }
         destDirToUse = classpath[0];
      }

      // set up the parser sources
      final JavaDocParser javaDocParser = new JavaDocParser();
      try {
         // add classloader
         javaDocParser.addClassLoaderToSearchPath(compilationLoader);

         // add src dirs
         StringBuffer logDirs = new StringBuffer("parsing source dirs:");
         for (int i = 0; i < srcDirs.length; i++) {
            logDirs.append("\n\t" + srcDirs[i]);
         }
         messageHandler.info(logDirs.toString());
         javaDocParser.addSourceTrees(srcDirs);

         // add src files
         logDirs = new StringBuffer();
         for (int i = 0; i < srcFiles.length; i++) {
            logDirs.append("\n\t" + srcFiles[i]);
            javaDocParser.addSource(srcFiles[i]);
         }

         final AnnotationInterfaceRepository repository = new AnnotationInterfaceRepository(
               messageHandler);
         repository.registerPropertiesFiles(annotationPropertiesFiles,
               compilationLoader);

         final AnnotationC compiler = new AnnotationC(compilationLoader,
               javaDocParser, repository, messageHandler, ignoreUnknown);

         // do the actual compile
         compiler.doCompile(classPath, destDirToUse);

         // sum-up
         if (ignoreUnknown) {
            int i = 0;
            StringBuffer buffer = new StringBuffer("ignored tags: ");
            for (Iterator iterator = repository.getIgnoredDocletNames()
                  .iterator(); iterator.hasNext(); i++) {
               String doclet = (String) iterator.next();
               if (i > 0) {
                  buffer.append(", ");
               }
               buffer.append('@');
               buffer.append(doclet);
            }

            messageHandler.info(buffer.toString());
         }
      } catch (SourceParseException e) {
         messageHandler.error(e);
         return;
      } catch (CompilerException e) {
         messageHandler.error(e);
         return;
      } catch (Throwable t) {
         messageHandler.error(new CompilerException("unexpected exception: "
               + t.toString(), t));
         return;
      }
   }

   private void doCompile(final URL[] classPath, final String destDir) {
      logInfo("compiling annotations...");

      // get all the classes
      JavaClass[] classes = m_javaDocParser.getJavaClasses();
      for (int i = 0; i < classes.length; i++) {
         JavaClass clazz = classes[i];
         logInfo("parsing class [" + clazz.getFullyQualifiedName() + ']');
         try {
            AnnotationEnhancer enhancer = new AnnotationEnhancer(m_handler);
            if (enhancer.initialize(clazz.getFullyQualifiedName(), classPath)) {
               handleClassAnnotations(enhancer, clazz);
               // handleInnerClassAnnotations(enhancer, clazz, destDir);
               JavaMethod[] methods = clazz.getMethods();
               for (int j = 0; j < methods.length; j++) {
                  JavaMethod method = methods[j];
                  if (method.isConstructor()) {
                     handleConstructorAnnotations(enhancer, method);
                  } else {
                     handleMethodAnnotations(enhancer, method);
                  }
               }
               JavaField[] fields = clazz.getFields();
               for (int j = 0; j < fields.length; j++) {
                  handleFieldAnnotations(enhancer, fields[j]);
               }

               // write enhanced class to disk
               enhancer.write(destDir);
            }
         } catch (ParseException pe) {
            m_handler.error(pe);
            // non critical, go on
         } catch (CompilerException ce) {
            m_handler.error(ce);
            return;
         } catch (Throwable t) {
            t.printStackTrace();
            m_handler.error(new CompilerException(
                  "could not compile annotations for class ["// FIXME location
                        + clazz.getFullyQualifiedName() + "] due to: "
                        + t.toString()));
            return;
         }
      }
      logInfo("compiled classes written to " + destDir);
      logInfo("compilation successful");
   }

   private void handleClassAnnotations(final AnnotationEnhancer enhancer,
         final JavaClass clazz) {
      DocletTag[] tags = clazz.getTags();

      Class interfaceClass = null;
      StringBuffer buffer = null;
      int line = 0;
      for (int i = 0; i < tags.length; i++) {
         RawAnnotation rawAnnotation = getRawAnnotation(tags[i], enhancer
               .getClassName(), enhancer.getClassFileName());
         if (rawAnnotation == null) {
            continue;
         }

         if ("net.java.dev.genesis.annotation.Condition".equals(rawAnnotation
               .getAnnotationClass().getName())) {
            String value = tags[i].getValue().trim();
            if (!value.startsWith("(") && !value.endsWith(")")) {
               if (interfaceClass != null) {
                  buffer.append(',');
               } else {
                  buffer = new StringBuffer();
                  buffer.append('{');
                  line = rawAnnotation.getLineNumber();
                  interfaceClass = rawAnnotation.getAnnotationClass();
               }
               buffer.append(rawAnnotation.getValue());
            }
         }
         enhancer.insertClassAnnotation(rawAnnotation);
         logInfo("\tprocessing class annotation [" + rawAnnotation.getName()
               + " @ " + clazz.getFullyQualifiedName() + ']');
      }
      
      if (interfaceClass != null) {
         buffer.append('}');
         RawAnnotation conditionAnnon = new RawAnnotation(interfaceClass,
               buffer.toString(), line, enhancer.getClassName(), enhancer
                     .getClassFileName());
         enhancer.insertClassAnnotation(conditionAnnon);
         logInfo("\tprocessing class annotation [" + conditionAnnon.getName()
               + " @ " + clazz.getFullyQualifiedName() + ']');
      }
   }

   private void handleMethodAnnotations(final AnnotationEnhancer enhancer,
         final JavaMethod method) {
      DocletTag[] tags = method.getTags();
      
      Class interfaceClass = null;
      StringBuffer buffer = null;
      int line = 0;
      for (int i = 0; i < tags.length; i++) {
         if (tags[i].getName().startsWith("Condition")) {
            System.out.println(tags[i].getName() + " - " + tags[i].getValue());
         }
         RawAnnotation rawAnnotation = getRawAnnotation(tags[i], enhancer
               .getClassName(), enhancer.getClassFileName());
         if (rawAnnotation == null) {
            continue;
         }
         
         if ("net.java.dev.genesis.annotation.Condition".equals(rawAnnotation
               .getAnnotationClass().getName())) {
            String value = tags[i].getValue().trim();
            if (!value.startsWith("(") && !value.endsWith(")")) {
               if (interfaceClass != null) {
                  buffer.append(',');
               } else {
                  buffer = new StringBuffer();
                  buffer.append('{');
                  line = rawAnnotation.getLineNumber();
                  interfaceClass = rawAnnotation.getAnnotationClass();
               }
               buffer.append(rawAnnotation.getValue());
            }
         }
         enhancer.insertMethodAnnotation(method, rawAnnotation);
         logInfo("\tprocessing method annotation [" + rawAnnotation.getName()
               + " @ " + method.getParentClass().getName() + '.'
               + method.getName() + ']');
      }
      
      if (interfaceClass != null) {
         buffer.append('}');
         RawAnnotation conditionAnnon = new RawAnnotation(interfaceClass,
               buffer.toString(), line, enhancer.getClassName(), enhancer
                     .getClassFileName());
         enhancer.insertClassAnnotation(conditionAnnon);
         logInfo("\tprocessing method annotation [" + conditionAnnon.getName()
               + " @ " + method.getParentClass().getName() + '.'
               + method.getName() + ']');
      }
   }

   private void handleConstructorAnnotations(final AnnotationEnhancer enhancer,
         final JavaMethod constructor) {
      DocletTag[] tags = constructor.getTags();
      for (int i = 0; i < tags.length; i++) {
         RawAnnotation rawAnnotation = getRawAnnotation(tags[i], enhancer
               .getClassName(), enhancer.getClassFileName());
         if (rawAnnotation == null) {
            continue;
         }
         enhancer.insertConstructorAnnotation(constructor, rawAnnotation);
         logInfo("\tprocessing constructor annotation ["
               + rawAnnotation.getName() + " @ "
               + constructor.getParentClass().getName() + '.'
               + constructor.getName() + ']');
      }
   }

   private void handleFieldAnnotations(final AnnotationEnhancer enhancer,
         final JavaField field) {
      DocletTag[] tags = field.getTags();
      for (int i = 0; i < tags.length; i++) {
         RawAnnotation rawAnnotation = getRawAnnotation(tags[i], enhancer
               .getClassName(), enhancer.getClassFileName());
         if (rawAnnotation == null) {
            continue;
         }
         enhancer.insertFieldAnnotation(field, rawAnnotation);
         logInfo("\tprocessing field annotation [" + rawAnnotation.getName()
               + " @ " + field.getName() + ']');
      }
   }

   private RawAnnotation getRawAnnotation(final DocletTag tag,
         final String enclosingClassName, final String enclosingClassFileName) {
      String annotationName = tag.getName();
      int index = annotationName.indexOf('(');
      if (index != -1) {
         annotationName = annotationName.substring(0, index);
      }

      Class annotationInterface = m_repository.getAnnotationInterfaceFor(
            annotationName, m_loader);
      if (annotationInterface == null) {
         // not found, and the AnnotationInterfaceRepository.ANNOTATION_IGNORED
         // has been populated
         if (!m_ignoreUnknown) {
            logInfo("JavaDoc tag ["
                  + annotationName
                  + "] is not treated as an annotation - class could not be resolved"
                  + " at " + enclosingClassName + " in "
                  + enclosingClassFileName + ", line " + tag.getLineNumber());
         }
         return null;
      }

      return net.java.dev.genesis.anttasks.aspectwerkz.JavaDocParser
            .getRawAnnotation(annotationInterface, annotationName, tag,
                  enclosingClassName, enclosingClassFileName,
                  annotationsPreProcessors);
   }

   public void logInfo(final String message) {
      m_handler.info(message);
   }

}
