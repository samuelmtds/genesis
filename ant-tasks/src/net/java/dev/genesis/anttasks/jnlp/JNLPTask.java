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
package net.java.dev.genesis.anttasks.jnlp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;


/**
 * Creates a Java Web Start file.
 */
public class JNLPTask extends OrangeJNLPTask {
   private static final FileUtils fu = FileUtils.newFileUtils();
   private String nativeprefix = "native";

   public void setNativeprefix(String prefix) {
      nativeprefix = prefix;
   }

   public NativeResources createNativeResources() {
      NativeResources nativeRes = new NativeResources();
      resources.add(nativeRes);

      return nativeRes;
   }

   public class NativeResources extends Resources {
      private List files = new ArrayList();
      private List baseFiles = new ArrayList();
      private Map osMap = new HashMap();
      private Map archMap = new HashMap();
      private String download;

      public void setDownload(String download) {
         this.download = download;
      }

      public void addFileset(FileSet fileset) {
         files.add(fileset);
      }

      protected void initFilesets() {
         for (Iterator iter = files.iterator(); iter.hasNext();) {
            FileSet fileset = (FileSet) iter.next();
            DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
            String[] f = ds.getIncludedFiles();
            File currentDir = fileset.getDir(getProject());

            for (int i = 0; i < f.length; i++) {
               File file = fu.resolveFile(currentDir, f[i]);

               if (!file.isFile()) {
                  continue;
               }

               if (currentDir.equals(file.getParentFile())) {
                  baseFiles.add(file);

                  continue;
               }

               if (file.getParentFile() == null) {
                  continue;
               }

               if (currentDir.equals(file.getParentFile().getParentFile())) {
                  List osList = (List) osMap
                        .get(file.getParentFile().getName());

                  if (osList == null) {
                     osList = new ArrayList();
                     osMap.put(file.getParentFile().getName(), osList);
                  }

                  osList.add(file);

                  continue;
               }

               if (file.getParentFile().getParentFile() == null) {
                  continue;
               }

               if (currentDir.equals(file.getParentFile().getParentFile()
                     .getParentFile())) {
                  OsArchKey key = new OsArchKey();
                  key.os = file.getParentFile().getParentFile().getName();
                  key.arch = file.getParentFile().getName();

                  List archList = (List) archMap.get(key);

                  if (archList == null) {
                     archList = new ArrayList();
                     archMap.put(key, archList);
                  }

                  archList.add(file);

                  continue;
               }
            }
         }
      }

      protected void createNativeLib(Resources resources, String name) {
         NativeLib nativeLib = resources.createNativeLib();
         nativeLib.setHref(name);
         nativeLib.setDownload(download);
      }

      protected void createJar(Resources resources, String name) {
         Jar jar = resources.createJar();
         jar.setHref(name);
         jar.setDownload(download);
      }

      public void toString(StringBuffer sb, int depth) {
         initFilesets();

         for (Iterator iter = baseFiles.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            Resources resources = new Resources();

            if (file.getName().toLowerCase().startsWith(nativeprefix)) {
               createNativeLib(resources, file.getName());

               continue;
            }

            createJar(resources, file.getName());

            resources.toString(sb, depth);
         }

         for (Iterator iter = osMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            List osFiles = (List) entry.getValue();
            String os = (String) entry.getKey();
            Resources resources = new Resources();
            resources.setOs(os);

            for (Iterator iterator = osFiles.iterator(); iterator.hasNext();) {
               File file = (File) iterator.next();

               if (file.getName().toLowerCase().startsWith(nativeprefix)) {
                  createNativeLib(resources, os + '/' + file.getName());

                  continue;
               }

               createJar(resources, os + '/' + file.getName());

               resources.toString(sb, depth);
            }
         }

         for (Iterator iter = archMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            List archFiles = (List) entry.getValue();
            OsArchKey key = (OsArchKey) entry.getKey();
            Resources resources = new Resources();
            resources.setOs(key.os);
            resources.setArch(key.arch);

            for (Iterator iterator = archFiles.iterator(); iterator.hasNext();) {
               File file = (File) iterator.next();

               if (file.getName().toLowerCase().startsWith(nativeprefix)) {
                  createNativeLib(resources, key.os + '/' + key.arch + '/'
                        + file.getName());

                  continue;
               }

               createJar(resources, key.os + '/' + key.arch + '/'
                     + file.getName());

               resources.toString(sb, depth);
            }
         }
      }

      public class OsArchKey {
         String os;
         String arch;

         public boolean equals(Object obj) {
            OsArchKey that = (OsArchKey) obj;

            if (this == that) {
               return true;
            }

            return this.os.equals(that.os) && this.arch.equals(that.arch);
         }

         public int hashCode() {
            return this.os.hashCode() + this.arch.hashCode();
         }
      }
   }
}
