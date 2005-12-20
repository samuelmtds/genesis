/*
 * The Genesis Project
 * Copyright (C) 2005  Summa Technologies do Brasil Ltda.
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
   private String target = null;

   public void setTarget(String target) {
      this.target = target;
   }

   public void execute() {
      if (target == null) {
         throw new BuildException("target is required");
      }

      Vector vector = getProject().topoSort(target, getProject().getTargets(), false);

      Set succeededTargets = new HashSet(vector.size());
      removeOwnerDependencies(getOwningTarget(), vector, succeededTargets);
      executeSortedTargets(vector, succeededTargets);
   }

   protected void removeOwnerDependencies(Target owner, Vector vector,
         Set succeededTargets) {
      if (owner == null) {
         return;
      }

      removeOwnerDependencies(owner.getDependencies(), vector, succeededTargets);
   }

   protected void removeOwnerDependencies(Enumeration deps, Vector vector,
         Set succeededTargets) {
      while (deps.hasMoreElements()) {
         String dependencyName = (String) deps.nextElement();
         Target dep = (Target) getProject().getTargets().get(dependencyName);
         vector.remove(dep);
         succeededTargets.add(dependencyName);

         removeOwnerDependencies(dep.getDependencies(), vector,
               succeededTargets);
      }
   }

   public void executeSortedTargets(Vector sortedTargets, Set succeededTargets)
         throws BuildException {
      BuildException buildException = null; // first build exception
      for (Enumeration iter = sortedTargets.elements(); iter.hasMoreElements();) {
         Target curtarget = (Target) iter.nextElement();
         boolean canExecute = true;
         for (Enumeration depIter = curtarget.getDependencies(); depIter
               .hasMoreElements();) {
            String dependencyName = ((String) depIter.nextElement());
            if (!succeededTargets.contains(dependencyName)) {
               canExecute = false;
               log("Cannot execute '" + curtarget.getName() + "' - '"
                     + dependencyName + "' failed or was not executed.",
                     Project.MSG_ERR);
               break;
            }
         }
         if (canExecute) {
            Throwable thrownException = null;
            try {
               curtarget.performTasks();
               succeededTargets.add(curtarget.getName());
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
                  log("Target '" + curtarget.getName()
                        + "' failed with message '"
                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
                  // only the first build exception is reported
                  if (buildException == null) {
                     buildException = (BuildException) thrownException;
                  }
               } else {
                  log("Target '" + curtarget.getName()
                        + "' failed with message '"
                        + thrownException.getMessage() + "'.", Project.MSG_ERR);
                  thrownException.printStackTrace(System.err);
                  if (buildException == null) {
                     buildException = new BuildException(thrownException);
                  }
               }
            }
         }
      }
      if (buildException != null) {
         throw buildException;
      }
   }
}