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

import org.apache.tools.ant.DirectoryScanner;

public class EmptyDirectoryScanner extends DirectoryScanner {
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final String[] ONE_EMPTY_STRING_ARRAY = new String[] {""};
         
   public String[] getDeselectedDirectories() {
      return EMPTY_STRING_ARRAY;
    }

   public String[] getDeselectedFiles() {
        return EMPTY_STRING_ARRAY;
    }
   
   public String[] getExcludedDirectories() {
        return EMPTY_STRING_ARRAY;
    }
   
   public String[] getIncludedDirectories() {
      return ONE_EMPTY_STRING_ARRAY;
   }
   
   public int getIncludedDirsCount() {
      return ONE_EMPTY_STRING_ARRAY.length;
    }
   
   public String[] getIncludedFiles() {
       return EMPTY_STRING_ARRAY;
    }
   
   public int getIncludedFilesCount() {
        return 0;
    }
   
   public String[] getNotIncludedDirectories() {
        return EMPTY_STRING_ARRAY;
    }
   
   public String[] getNotIncludedFiles() {
        return EMPTY_STRING_ARRAY;
    }

   public void scan() {
   }
}