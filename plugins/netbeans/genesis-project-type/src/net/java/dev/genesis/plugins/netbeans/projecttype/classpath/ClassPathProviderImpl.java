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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
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
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.Lookup;

public class ClassPathProviderImpl implements ClassPathProvider {
   private final Map classpaths = new HashMap();
   private final GenesisProject project;
   
   private ClassPath bootClassPath;
   
   public ClassPathProviderImpl(final GenesisProject project) {
      this.project = project;
   }
   
   public ClassPath findClassPath(FileObject fo, String type) {
      System.out.println("fo: " + fo + "; type: " + type);

      if (ClassPath.BOOT.equals(type)) {
         return getBootClassPath();
      }
      
      ClassPath cp = getClassPath(fo, type);
      System.out.println(cp);

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
               System.out.println("created cp: " + classPath);
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
}