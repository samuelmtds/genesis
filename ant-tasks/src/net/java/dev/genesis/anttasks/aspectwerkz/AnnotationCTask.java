/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.dev.genesis.anttasks.aspectwerkz.annotation.AnnotationPreprocessor;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;
import org.codehaus.backport175.compiler.CompilerException;
import org.codehaus.backport175.compiler.MessageHandler;

public class AnnotationCTask extends MatchingTask {
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private final FileUtils fileUtils = FileUtils.newFileUtils();
   private String[] compileList = EMPTY_STRING_ARRAY;
   private boolean verbose;
   private Path classpath;
   private Path src;
   private Path properties;
   private File destDir;
   private List filesets = new ArrayList();
   private List preprocessors = new ArrayList();

   public Path createSrc() {
      if (src == null) {
         src = new Path(getProject());
      }

      return src.createPath();
   }

   public Path getSrcdir() {
      return src;
   }

   public void setSrcdir(Path srcDir) {
      if (src == null) {
         src = srcDir;
      } else {
         src.append(srcDir);
      }
   }

   public Path createProperties() {
      if (properties == null) {
         properties = new Path(getProject());
      }

      return properties;
   }

   public void setProperties(Path annotationFile) {
      createProperties().append(annotationFile);
   }

   public void setVerbose(boolean isVerbose) {
      verbose = isVerbose;
   }

   public void setDestdir(File destdir) {
      this.destDir = destdir;
   }

   public void setSourcepath(Path sourcepath) {
      if (src == null) {
         src = sourcepath;
      } else {
         src.append(sourcepath);
      }
   }

   public Path createSourcepath() {
      if (src == null) {
         src = new Path(getProject());
      }

      return src.createPath();
   }

   public void setSourcepathRef(Reference r) {
      createSourcepath().setRefid(r);
   }

   public void setClasspath(Path classpath) {
      if (this.classpath == null) {
         this.classpath = classpath;
      } else {
         this.classpath.append(classpath);
      }
   }

   public Path createClasspath() {
      if (this.classpath == null) {
         this.classpath = new Path(getProject());
      }

      return this.classpath.createPath();
   }

   public void addClasspath(Path path) {
      createClasspath().add(path);
   }

   public void setClasspathRef(Reference r) {
      createClasspath().setRefid(r);
   }

   public void addFileset(FileSet fileset) {
      filesets.add(fileset);
   }

   public void addPreprocessor(Preprocessor preprocessor) {
      preprocessors.add(preprocessor);
   }

   protected void scanDir(File srcDir, final File destDir, String[] files,
         final long propertiesLastModified) {
      GlobPatternMapper m = new GlobPatternMapper();
      m.setFrom("*.java");
      m.setTo("*.class");

      SourceFileScanner sfs = new SourceFileScanner(this) {
         public Resource getResource(String name) {
            File src = fileUtils.resolveFile(destDir, name);
            long srcLastModified = src.lastModified();

            return new Resource(name, src.exists(),
                  ((propertiesLastModified > srcLastModified) ? 0
                        : srcLastModified), src.isDirectory());
         }
      };

      String[] newFiles = sfs.restrict(files, srcDir, destDir, m);

      if (newFiles.length > 0) {
         String[] newCompileList = new String[compileList.length
               + newFiles.length];
         System
               .arraycopy(compileList, 0, newCompileList, 0, compileList.length);

         for (int i = 0; i < newCompileList.length; i++) {
            newCompileList[compileList.length + i] = srcDir.getAbsolutePath()
                  + File.separatorChar + newFiles[i];
         }

         compileList = newCompileList;
      }
   }

   public void execute() throws BuildException {
      checkParameters();
      resetFileLists();

      String[] list = src.list();

      String[] props = properties.list();
      String[] allProps = new String[props.length];
      long propertiesLastModified = 0;

      for (int i = 0; i < props.length; i++) {
         File property = getProject().resolveFile(props[i]);
         allProps[i] = property.getAbsolutePath();
         long lastModified = property.lastModified();
         
         if (propertiesLastModified < lastModified) {
            propertiesLastModified = lastModified;
         }
      }

      for (int i = 0; i < list.length; i++) {
         File srcDir = getProject().resolveFile(list[i]);

         if (!srcDir.exists()) {
            throw new BuildException("srcdir \"" + srcDir.getPath()
                  + "\" does not exist!", getLocation());
         }

         DirectoryScanner ds = getDirectoryScanner(srcDir);
         String[] files = ds.getIncludedFiles();

         scanDir(srcDir, (destDir != null) ? destDir : srcDir, files,
               propertiesLastModified);
      }

      if (compileList.length == 0) {
         return;
      }

      log("Annotating " + compileList.length + " class file"
            + ((compileList.length == 1) ? "" : "s")
            + ((destDir != null) ? (" to " + destDir) : ""));

      try {
         AnnotationC compiler = new AnnotationC(EMPTY_STRING_ARRAY,
               compileList, classpath.list(), (destDir == null) ? null
                     : destDir.getAbsolutePath(), allProps,
               new MessageHandler.PrintWriter(verbose) {
                  public void error(CompilerException ex) {
                     super.error(ex);
                     throw ex;
                  }
               }, true);
         for (Iterator iter = preprocessors.iterator(); iter.hasNext();) {
            Preprocessor preProc = (Preprocessor) iter.next();
            preProc.validate();
            Class preProcClass = Class.forName(preProc.getPreprocessor());

            compiler.register(preProc.getAnnotation(), (AnnotationPreprocessor) preProcClass.newInstance());
         }

         compiler.compile();
      } catch (Exception ex) {
         throw new BuildException("Annotation failed: " + ex.getMessage(), ex,
               getLocation());
      }
   }

   protected void resetFileLists() {
      compileList = EMPTY_STRING_ARRAY;
   }

   protected void checkParameters() throws BuildException {
      if ((src == null) || (src.size() == 0)) {
         throw new BuildException("srcdir attribute must be set!",
               getLocation());
      }

      if (classpath == null) {
         throw new BuildException(
               "No classes specified [<classpath, classpath=.. classpathref=..]",
               getLocation());
      }

      if ((destDir == null) && (classpath.size() > 1)) {
         throw new BuildException(
               "When using more than one classpath directory, it is mandatory to specify [destdir=..]",
               getLocation());
      }

      if ((filesets.size() == 0) && ((src == null) || (src.size() == 0))) {
         throw new BuildException(
               "No source specified [<include, <sourcepath, srcdir=..]",
               getLocation());
      }
   }

   public static class Preprocessor {
      private String annotation;
      private String preprocessor;

      public String getAnnotation() {
         return annotation;
      }

      public void setAnnotation(String annotation) {
         this.annotation = annotation;
      }

      public String getPreprocessor() {
         return preprocessor;
      }

      public void setPreprocessor(String preprocessor) {
         this.preprocessor = preprocessor;
      }
      
      public void validate() throws BuildException {
         if (annotation == null) {
            throw new BuildException("Missing annotation attribute for preprocessor");
         }

         if (preprocessor == null) {
            throw new BuildException("Missing preprocessor attribute for preprocessor");
         }
      }

   }
}
