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
package net.java.dev.genesis.anttasks.ant.types;

import java.io.File;
import net.java.dev.genesis.anttasks.ant.EmptyDirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;

public class FileSetUtils {
   /**
    * Utility method that does not fail if <code>dir</code> does not exist
    */
   public static DirectoryScanner getDirectoryScanner(AbstractFileSet fs, 
         String dataTypeName, Project p) {
      if (fs.isReference()) {
         // If it is a reference, FileSet delegates to original
         return fs.getDirectoryScanner(p);
      }
      
      File dir = fs.getDir(p);
      if (dir == null) {
         throw new BuildException("No directory specified for " + dataTypeName + 
               ".");
      }

      if (dir.exists() && !dir.isDirectory()) {
         throw new BuildException(dir.getAbsolutePath() + " is not a " +
               "directory.");
      }
      
      DirectoryScanner ds = dir.exists() ? new DirectoryScanner() : 
            new EmptyDirectoryScanner();
      fs.setupDirectoryScanner(ds, p);
      ds.setFollowSymlinks(fs.isFollowSymlinks());
      ds.scan();

      return ds;
   }
}