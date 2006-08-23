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
import org.apache.tools.ant.types.ZipFileSet;

public class OptionalFileSet extends ZipFileSet {
   public DirectoryScanner getDirectoryScanner(Project p) {
      if (isReference()) {
         return getRef(p).getDirectoryScanner(p);
      }
      
      File dir = getDir(p);      
      if (dir == null) {
         throw new BuildException("No directory specified for "
               + getDataTypeName() + ".");
      }
      
      if (dir.exists() && !dir.isDirectory()) {
         throw new BuildException(dir.getAbsolutePath()
         + " is not a directory.");
      }
      
      DirectoryScanner ds = dir.exists() ? new DirectoryScanner() : 
            new EmptyDirectoryScanner();
      setupDirectoryScanner(ds, p);
      ds.setFollowSymlinks(isFollowSymlinks());
      ds.scan();

      return ds;
   }
}