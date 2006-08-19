/*
 * The Genesis Project
 * Copyright (C) 2005-2006  Summa Technologies do Brasil Ltda.
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
package net.java.dev.genesis.anttasks;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public class Call extends Task {
   private static boolean ant163 = true;
   private String target = null;

   public void setTarget(String target) {
      this.target = target;
   }

   protected boolean isSucceededTarget(String name) {
      Set s = (Set) getProject().getReference(
            SucceededTasksListener.SUCCEEDEDTARGETS_KEY);

      if (s == null) {
         return false;
      }

      return s.contains(name);
   }

   public void execute() {
      if (target == null) {
         throw new BuildException("target is required");
      }


      Vector vector;
      
      if (!ant163) {
         vector = getProject().topoSort(target, getProject().getTargets());
      } else {
         try {
            vector = getProject().topoSort(target, getProject().getTargets(),
                  false);
         } catch (NoSuchMethodError error) {
            ant163 = false;
            vector = getProject().topoSort(target, getProject().getTargets());
         }   
      }

      executeSortedTargets(vector);
   }

   public void executeSortedTargets(Vector sortedTargets) throws BuildException {
      BuildException buildException = null; // first build exception
      for (Enumeration iter = sortedTargets.elements(); iter.hasMoreElements();) {
         Target curtarget = (Target) iter.nextElement();
         if (isSucceededTarget(curtarget.getName())) {
            continue;
         }

         /*
          * improve performance, since all dependencies had already been
          * executed boolean canExecute = true; for (Enumeration depIter =
          * curtarget.getDependencies(); depIter .hasMoreElements();) { String
          * dependencyName = ((String) depIter.nextElement()); if
          * (!isSucceededTarget(dependencyName)) { canExecute = false;
          * log("Cannot execute '" + curtarget.getName() + "' - '" +
          * dependencyName + "' failed or was not executed.", Project.MSG_ERR);
          * break; } } if (!canExecute) { continue; }
          */
         Throwable thrownException = null;
         try {
            curtarget.performTasks();
            Set s = (Set) getProject().getReference(
                  SucceededTasksListener.SUCCEEDEDTARGETS_KEY);
            if (s == null) {
               s = new HashSet();
               getProject().addReference(
                     SucceededTasksListener.SUCCEEDEDTARGETS_KEY, s);
            }

            s.add(curtarget.getName());
         } catch (RuntimeException ex) {
            if (!(getProject().isKeepGoingMode())) {
               throw ex; // throw further
            }
            thrownException = ex;
         } catch (Throwable ex) {
            if (!(getProject().isKeepGoingMode())) {
               throw new BuildException(ex);
            }
            thrownException = ex;
         }
         if (thrownException != null) {
            if (thrownException instanceof BuildException) {
               log("Target '" + curtarget.getName() + "' failed with message '"
                     + thrownException.getMessage() + "'.", Project.MSG_ERR);
               // only the first build exception is reported
               if (buildException == null) {
                  buildException = (BuildException) thrownException;
               }
            } else {
               log("Target '" + curtarget.getName() + "' failed with message '"
                     + thrownException.getMessage() + "'.", Project.MSG_ERR);
               thrownException.printStackTrace(System.err);
               if (buildException == null) {
                  buildException = new BuildException(thrownException);
               }
            }
         }
         if (curtarget.getName().equals(target)) { // old exit condition
            break;
        }
      }
      if (buildException != null) {
         throw buildException;
      }
   }
}