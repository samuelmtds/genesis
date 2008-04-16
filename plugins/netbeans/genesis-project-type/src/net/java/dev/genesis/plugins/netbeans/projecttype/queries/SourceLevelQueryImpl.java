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
package net.java.dev.genesis.plugins.netbeans.projecttype.queries;

import net.java.dev.genesis.plugins.netbeans.projecttype.Utils;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.openide.filesystems.FileObject;
import org.openide.util.Mutex;

public class SourceLevelQueryImpl implements SourceLevelQueryImplementation {
   private final AntProjectHelper helper;
   private String sourceLevel;

   public SourceLevelQueryImpl(AntProjectHelper helper) {
      this.helper = helper;
      helper.addAntProjectListener(new AntProjectListener() {
         public void configurationXmlChanged(AntProjectEvent e) {
            synchronized (SourceLevelQueryImpl.this) {
               sourceLevel = null;
            }
         }

         public void propertiesChanged(AntProjectEvent e) {
         }
      });
   }

   public synchronized String getSourceLevel(FileObject fo) {
      if (sourceLevel == null) {
         sourceLevel = ProjectManager.mutex().readAccess(
               new Mutex.Action<String>() {
                  public String run() {
                     return Utils.getSourceLevel(helper);
                  }
               });
      }

      return sourceLevel;
   }
}
