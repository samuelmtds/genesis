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

import java.util.Iterator;
import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileObject;
import org.openide.modules.SpecificationVersion;

public class ClassPathProviderImpl implements ClassPathProvider {
   private final AntProjectHelper helper;
   private ClassPath bootClassPath;

   public ClassPathProviderImpl(final AntProjectHelper helper) {
      this.helper = helper;
   }

   public ClassPath findClassPath(FileObject fo, String type) {
      if (ClassPath.BOOT.equals(type)) {
         return getBootClassPath();
      }

      System.out.println("type: " + type + "; fo: " + fo);

      return null;
   }

   private synchronized ClassPath getBootClassPath() {
      if (bootClassPath == null) {
         JavaPlatform platform = JavaPlatformManager.getDefault()
               .getDefaultPlatform();

         JavaPlatform[] platforms = JavaPlatformManager.getDefault()
               .getPlatforms(null, new Specification("j2se", 
               new SpecificationVersion(Utils.getSourceLevel(helper))));

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
}
