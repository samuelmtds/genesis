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
package net.java.dev.genesis.plugins.netbeans.projecttype.classpath;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisSources;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;

public class ClassPathProviderImpl implements ClassPathProvider {
   private static final Object[][] sharedCompilePaths = new Object[][] {
         new Object[] {"genesis", new String[] {"genesis-shared-"}},
         new Object[] {"lib/hibernate", new String[] {"hibernate"}},
         new Object[] {"lib/commons",  new String[] {"commons-beanutils", 
               "commons-logging", "reusable-components"}}
   };
   private static final Object[][] desktopClientCompilePaths = new Object[][] {
         new Object[] {"genesis", new String[] {"genesis-shared-", 
                        "genesis-client"}},
         new Object[] {"lib/hibernate", new String[] {"hibernate"}},
         new Object[] {"lib/commons", new String[] {"commons-beanutils", 
               "commons-digester", "commons-jxpath", "commons-logging", 
               "commons-validator", "jakarta-oro", "reusable-components"}},
         new Object[] {"lib/thinlet", new String[] {"thinlet"}}
   };
   private static final Object[][] webClientCompilePaths = new Object[][] {
         new Object[] {"genesis", new String[] {"genesis-shared-", 
                        "genesis-client"}},
         new Object[] {"lib/hibernate", new String[] {"hibernate"}},
         new Object[] {"lib/commons", new String[] {"commons-beanutils", 
               "commons-jxpath", "commons-logging", "commons-validator", 
               "reusable-components"}},
         new Object[] {"lib/j2ee", new String[] {"j2ee", "servlet-api"}}
   };

   private final Map classpaths = new HashMap();
   private final GenesisProject project;
   
   private ClassPath bootClassPath;
   
   public ClassPathProviderImpl(final GenesisProject project) {
      this.project = project;
   }
   
   public ClassPath findClassPath(FileObject fo, String type) {
//      System.out.println("fo: " + fo + "; type: " + type);

      if (ClassPath.BOOT.equals(type)) {
         return getBootClassPath();
      }
      
      ClassPath cp = getClassPath(fo, type);
//      System.out.println(cp);

      return cp;
   }
   
   private synchronized ClassPath getBootClassPath() {
      if (bootClassPath == null) {
         JavaPlatform platform = JavaPlatformManager.getDefault()
         .getDefaultPlatform();
         
         JavaPlatform[] platforms = JavaPlatformManager.getDefault()
         .getPlatforms(null, new Specification("j2se",
               new SpecificationVersion(Utils.getSourceLevel(
               project.getHelper()))));
         
         if (platforms.length > 0) {
            platform = platforms[0];
            
            for (int i = 0; i < platforms.length; i++) {
               if (platforms[i].getJavadocFolders().isEmpty()) {
                  platform = platforms[i];
                  break;
               }
            }
            
            for (int i = 0; i < platforms.length; i++) {
               if (platforms[i].getSourceFolders().getRoots().length > 0) {
                  platform = platforms[i];
                  break;
               }
            }
         }
         
         bootClassPath = platform.getBootstrapLibraries();
      }
      
      return bootClassPath;
   }
   
   private ClassPath getClassPath(FileObject fo, String type) {
      synchronized (classpaths) {
         Map classpathsPerType = (Map)classpaths.get(type);
         
         if (classpathsPerType == null) {
            classpathsPerType = new WeakHashMap();
            classpaths.put(type, classpathsPerType);
         }
         
         for (final Iterator i = classpathsPerType.entrySet().iterator();
               i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            FileObject root = (FileObject)entry.getKey();

            if (root == fo || FileUtil.isParentOf(root, fo)) {
               return (ClassPath)entry.getValue();
            }
         }

         GenesisSources sources = (GenesisSources)project.getLookup().lookup(
               GenesisSources.class);
         SourceGroup[] groups = sources.getSourceGroups(
               JavaProjectConstants.SOURCES_TYPE_JAVA);

         for (int i = 0; i < groups.length; i++) {
            FileObject root = groups[i].getRootFolder();

            if (root == fo || FileUtil.isParentOf(root, fo)) {
               ClassPath classPath = createClassPath(root, type, sources);
//               System.out.println("created cp: " + classPath);
               classpathsPerType.put(root, classPath);

               return classPath;
            }
         }
      }

      return null;
   }

   private ClassPath createClassPath(FileObject root, String type, 
         GenesisSources sources) {
      if (ClassPath.SOURCE.equals(type)) {
         return createSourceClassPath(root, sources);
      } else if (ClassPath.COMPILE.equals(type)) {
         return createCompileClassPath(root, sources);
      }

      return null;
   }

   private ClassPath createSourceClassPath(FileObject root, 
         GenesisSources sources){
      if (sources.getClientSourcesRoot() == root && 
            sources.getSharedSourcesRoot() != null) {
         return ClassPathSupport.createClassPath(new FileObject[] {
            sources.getSharedSourcesRoot(), root});
      } else {
         return ClassPathSupport.createClassPath(new FileObject[] {root});
      }
   }

   private ClassPath createCompileClassPath(FileObject root, 
         GenesisSources sources) {
      if (root == sources.getSharedSourcesRoot()) {
         return createCompileClassPath(root, sources, sharedCompilePaths);
      } else if (root == sources.getClientSourcesRoot()) {
         GenesisProjectKind kind = Utils.getKind(project);

         if (kind == GenesisProjectKind.DESKTOP) {
            return createCompileClassPath(root, sources, 
                  desktopClientCompilePaths);
         } else if (kind == GenesisProjectKind.WEB) {
            return createCompileClassPath(root, sources, 
                  webClientCompilePaths);
         } 
      }

      return null;
   }

   private ClassPath createCompileClassPath(FileObject root, 
         GenesisSources sources, Object[][] paths) {
      Collection files = new ArrayList();

      for (int i = 0; i < paths.length; i++) {
         Object[] filesPerRoot = paths[i];
         final String[] filePrefixes = (String[])filesPerRoot[1];

         File rootDir = new File(project.getEvaluator().evaluate(
               "${genesis.home}/" + filesPerRoot[0]));
         File[] filteredFiles = rootDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
               for (int j = 0; j < filePrefixes.length; j++) {
                  if (pathname.getName().startsWith(filePrefixes[j])) {
                     return true;
                  }
               }

               return false;
            }
         });

         for (int j = 0; j < filteredFiles.length; j++) {
            FileObject file = FileUtil.toFileObject(filteredFiles[j]);

            if (FileUtil.isArchiveFile(file)) {
               file = FileUtil.getArchiveRoot(file);
            }

            files.add(file);
         }
      }

      return ClassPathSupport.createClassPath((FileObject[])files.toArray(
            new FileObject[files.size()]));
   }
}