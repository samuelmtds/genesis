/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class AspectWerkz extends Java {
   private static final String[] AW_LIBS = new String[] { "dom4j-*.jar",
         "qdox-*.jar", "concurrent-*.jar", "trove-*.jar", "jrexx-*.jar" };

   private boolean offline;
   private String awversion = "2.0";
   private File awlibs;

   public void setOffline(boolean offline) {
      this.offline = offline;
   }

   public void setAwversion(String version) {
      this.awversion = version;
   }

   public void setAwlibs(File awlibs) {
      this.awlibs = awlibs;
   }

   public void execute() throws BuildException {
      if (offline) {
         super.execute();

         return;
      }

      final boolean isAW1X = isAW1X();

      if (isJava5()) {
         File javaagent = new File(awlibs.getAbsolutePath()
               + File.separatorChar + "aspectwerkz-"
               + (isAW1X ? "core" : "jdk5") + '-' + awversion + ".jar");

         createJvmarg().setValue("-javaagent:" + javaagent.getAbsolutePath());

         FileSet fileSet = new FileSet();
         fileSet.setProject(getProject());
         fileSet.setDir(awlibs);
         fileSet.setIncludes("aspectwerkz-core-*.jar");
         fileSet.setIncludes("aspectwerkz-" + awversion + ".jar");
         if (isAW1X) {
            fileSet.setIncludes("aspectwerkz-extensions-*.jar");
            fileSet.setIncludes("javassist-*.jar");
            fileSet.setIncludes("piccolo-*.jar");
         } else {
            fileSet.setIncludes("aspectwerkz-jdk5-*.jar");
         }

         for (int i = 0; i < AW_LIBS.length; i++) {
            fileSet.setIncludes(AW_LIBS[i]);
         }

         Path bootclasspath = new Path(getProject());
         bootclasspath.addFileset(fileSet);

         createBootclasspath().addExisting(bootclasspath);
         createBootclasspath().addExisting(Path.systemBootClasspath, true);

         super.execute();
      } else if (isJRockit()) {
         createJvmarg()
               .setValue(
                     "-Xmanagement:class=org.codehaus.aspectwerkz.extension.jrockit.JRockitPreProcessor");

         FileSet fileSet = new FileSet();
         fileSet.setProject(getProject());
         fileSet.setDir(awlibs);
         fileSet.setIncludes("aspectwerkz-extensions-*.jar");
         fileSet.setIncludes("aspectwerkz-core-*.jar");
         fileSet.setIncludes("aspectwerkz-" + awversion + ".jar");
         fileSet.setIncludes("piccolo-*.jar");
         if (isAW1X) {
            fileSet.setIncludes("javassist-*.jar");
         } else {
            fileSet.setIncludes("aspectwerkz-jdk14-*.jar");
         }

         for (int i = 0; i < AW_LIBS.length; i++) {
            fileSet.setIncludes(AW_LIBS[i]);
         }

         Path bootclasspath = new Path(getProject());
         bootclasspath.addFileset(fileSet);

         createBootclasspath().addExisting(bootclasspath);
         createBootclasspath().addExisting(Path.systemBootClasspath, true);

         super.execute();
      } else {
         Java java = new Java();
         java.setProject(getProject());
         java.setTaskName(getTaskName());
         java.setClassname("org.codehaus.aspectwerkz.hook.ProcessStarter");

         FileSet fileSet = new FileSet();
         fileSet.setProject(getProject());
         fileSet.setDir(awlibs);
         if (isAW1X) {
            fileSet.setIncludes("javassist-*.jar");
         }
         fileSet.setIncludes("aspectwerkz-core-*.jar");

         FileSet toolsFileSet = new FileSet();
         toolsFileSet.setProject(getProject());
         toolsFileSet.setFile(getToolsJar());

         Path classpath = new Path(getProject());
         classpath.addFileset(fileSet);
         classpath.addFileset(toolsFileSet);

         java.setClasspath(classpath);

         Path bootclasspath = new Path(getProject());
         bootclasspath.addFileset(fileSet);

         java.createBootclasspath().addExisting(bootclasspath);
         java.createBootclasspath().addExisting(Path.systemBootClasspath, true);

         FileSet appFileSet = new FileSet();
         appFileSet.setProject(getProject());
         appFileSet.setFile(getAspectWerkzJar());

         FileSet appFileSet2 = new FileSet();
         appFileSet2.setProject(getProject());
         if (!isAW1X) {
            appFileSet2.setIncludes("aspectwerkz-extensions-*.jar");
         }

         appFileSet2.setDir(awlibs);
         for (int i = 0; i < AW_LIBS.length; i++) {
            appFileSet2.setIncludes(AW_LIBS[i]);
         }

         Path appClasspath = new Path(getProject());
         appClasspath.addFileset(appFileSet);
         appClasspath.addFileset(appFileSet2);

         if (!isAW1X) {
            FileSet appFileSet3 = new FileSet();
            appFileSet3.setProject(getProject());
            appFileSet3.setDir(awlibs);
            appFileSet3.setIncludes("aspectwerkz-extensions-*.jar");
            appFileSet3.setIncludes("aspectwerkz-jdk14-*.jar");

            appClasspath.addFileset(appFileSet3);
         }

         setClasspath(appClasspath);

         java.createArg().setLine(getCommandLine().getJavaCommand().toString());

         java.setFork(true);

         java.execute();
      }
   }

   protected void checkParameters() throws BuildException {
      if (!offline) {
         if ((awlibs == null || !awlibs.isDirectory())) {
         throw new BuildException(
               "awlibs must be set to a valid directory when running in online mode");
         }

         setFork(true);
      }
   }

   protected File getToolsJar() {
      String javaHome = System.getProperty("java.home");
      if (javaHome.toLowerCase(Locale.US).endsWith("jre")) {
         javaHome = javaHome.substring(0, javaHome.length() - 4);
      }

      return new File(javaHome + File.separatorChar + "lib"
            + File.separatorChar + "tools.jar");
   }

   protected File getAspectWerkzJar() {
      return new File(awlibs.getAbsolutePath() + File.separatorChar
            + "aspectwerkz-" + awversion + ".jar");
   }

   protected boolean isJava5() {
      return "1.5".equals(getCommandLine().getVmversion());
   }

   protected boolean isJRockit() {
      return "BEA".equals(System.getProperty("java.vendor").toUpperCase());
   }

   protected boolean isAW1X() {
      return awversion.startsWith("1.");
   }
}
