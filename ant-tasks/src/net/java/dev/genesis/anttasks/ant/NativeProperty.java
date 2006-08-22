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

import org.apache.tools.ant.taskdefs.Property;

public class NativeProperty extends Property {
   public void setLocation(File location) {
      super.setLocation(getRealDir(location));
   }

   protected String getOs() {
      String osFullName = System.getProperty("os.name");

      int indexOfSpace = osFullName.indexOf(' ');

      return indexOfSpace > 0 ? osFullName.substring(0, indexOfSpace)
            : osFullName;
   }

   protected String getArch() {
      return System.getProperty("os.arch");
   }

   protected File getRealDir(File dir) {
      File osDir = new File(dir, getOs());
      File archDir = new File(osDir, getArch());

      if (archDir.isDirectory()) {
         return archDir;
      }

      if (osDir.isDirectory()) {
         return osDir;
      }

      return dir;
   }
}
