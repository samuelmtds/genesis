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
package net.java.dev.genesis.anttasks.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

public class NativeJar extends Task {
   private static final FileUtils fu = FileUtils.newFileUtils();

   private List dirs = new ArrayList();
   private File destdir;
   private String prefix = "native";
   private String separator = "_";

   public void setDestdir(File destDir) {
      this.destdir = destDir;
   }

   public void addDirset(DirSet set) {
      dirs.add(set);
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void setSeparator(String separator) {
      this.separator = separator;
   }

   public void execute() {
      validate();

      for (Iterator iter = dirs.iterator(); iter.hasNext();) {
         DirSet dirset = (DirSet) iter.next();
         DirectoryScanner ds = dirset.getDirectoryScanner(getProject());
         String[] directories = ds.getIncludedDirectories();
         File currentDir = dirset.getDir(getProject());

         for (int i = 0; i < directories.length; i++) {
            File osDir = fu.resolveFile(currentDir, directories[i]);

            if (currentDir.equals(osDir)) {
               doJar(currentDir);
               continue;
            }
            
            if (currentDir.equals(osDir.getParentFile())) {
               processOs(currentDir, osDir);
               continue;
            }

            if (currentDir.equals(osDir.getParentFile().getParentFile())) {
               processArch(currentDir, osDir.getParentFile(), osDir);
               continue;
            }
         }
      }
   }

   protected void validate() {
      if (dirs.isEmpty()) {
         throw new BuildException("Specify at least a dirset.");
      }

      if (destdir == null) {
         throw new BuildException("You must specify destdir attribute!");
      }

      if (!destdir.isDirectory()) {
         throw new BuildException(destdir + " is not a directory.");
      }
   }

   protected void processOs(File currentDir, File osDir) {
      doJar(currentDir, osDir);
   }

   protected void processArch(File currentDir, File osDir, File archDir) {
      doJar(currentDir, osDir, archDir);
   }

   protected void doJar(File currentDir) {
      doJar(currentDir, (File) null, (File) null);
   }

   protected void doJar(File currentDir, File osDir) {
      doJar(currentDir, osDir, (File) null);
   }

   protected void doJar(File currentDir, File osDir, File archDir) {
      String dirpath = archDir == null ? osDir == null ? "" : osDir.getName()
            : osDir.getName() + File.separatorChar + archDir.getName();

      FileSet fs = new FileSet();
      fs.setProject(getProject());
      fs.setDir(new File(currentDir, dirpath));
      fs.setIncludes("*");
      fs.setExcludes("*.jar");

      DirectoryScanner ds = fs.getDirectoryScanner(getProject());
      if (ds.getIncludedFilesCount() == 0) {
         return;
      }

      String jarName = archDir == null ? getJarName(getOs(osDir)) : getJarName(
            getOs(osDir), getArch(archDir));
      File jarFile = new File(destdir.getAbsolutePath() + File.separatorChar
            + dirpath, jarName);

      jarFile.getParentFile().mkdirs();
      Jar jar = new Jar();
      jar.setProject(getProject());
      jar.setTaskName(getTaskName());
      jar.addFileset(fs);
      jar.setDestFile(jarFile);
      jar.setUpdate(true);
      jar.execute();
   }

   protected String getOs(File osDir) {
      return osDir.getName().toLowerCase();
   }

   protected String getArch(File archDir) {
      return archDir.getName().toLowerCase();
   }

   protected String getJarName(String os) {
      return getJarName(os, (String) null);
   }

   protected String getJarName(String os, String arch) {
      if (arch == null) {
         return prefix + separator + os + ".jar";
      }

      return prefix + separator + os + separator + arch + ".jar";
   }
}
