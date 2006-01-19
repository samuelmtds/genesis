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
package net.java.dev.genesis.plugins.netbeans.buildsupport.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisBuildSupport;
import net.java.dev.genesis.plugins.netbeans.buildsupport.spi.GenesisProjectKind;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.Lookup;

public final class GenesisBuildSupportManager {
   private static final GenesisBuildSupportManager instance = 
         new GenesisBuildSupportManager();

   private GenesisBuildSupportManager() {
   }

   public static GenesisBuildSupportManager getInstance() {
      return instance;
   }

   public boolean generateBuildFiles(GenesisProjectKind kind, 
         GeneratedFilesHelper helper, String version, boolean check) 
         throws IOException {
      Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(
            GenesisBuildSupport.class));

      for (Iterator i = result.allInstances().iterator(); i.hasNext(); ) {
         GenesisBuildSupport support = (GenesisBuildSupport)i.next();

         if (support.getVersion().toString().equals(version)) {
            support.generateBuildFiles(kind, helper, check);
            return true;
         }
      }

      return false;
   }

   public FileObject getGenesisHome(String version) throws MalformedURLException {
      return URLMapper.findFileObject(new URL("nbinst:///modules/ext/" +
            "genesis/" + version));
   }

   public List getBuildSupport() {
      List ret = new ArrayList();
      Lookup.Result result = Lookup.getDefault().lookup(new Lookup.Template(
            GenesisBuildSupport.class));

      for (Iterator i = result.allInstances().iterator(); i.hasNext(); ) {
         ret.add(i.next());
      }

      Collections.sort(ret, Collections.reverseOrder());

      return ret;
   }
}