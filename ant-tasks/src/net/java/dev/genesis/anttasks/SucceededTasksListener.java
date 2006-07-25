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
package net.java.dev.genesis.anttasks;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public class SucceededTasksListener extends Task implements BuildListener {
   public static final String SUCCEEDEDTARGETS_KEY = "ant.succeededtargets";
   private boolean started;
   private Object lock = new Object();
   private List alreadyExecuted;

   public void setAlreadyexecuted(String value) {
      if (value != null && value.length() > 0) {
         StringTokenizer tok = new StringTokenizer(value, ",", true);
         while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();

            if (token.equals("") || token.equals(",")) {
               throw new BuildException("Syntax Error: alreadyexecuted "
                     + "attribute for task \"" + getTaskName()
                     + "\" has an empty string for dependency.");
            }

            addAlreadyexecuted(token);

            if (tok.hasMoreTokens()) {
               token = tok.nextToken();
               if (!tok.hasMoreTokens() || !token.equals(",")) {
                  throw new BuildException("Syntax Error: alreadyexecuted "
                        + "attribute for task \"" + getTaskName()
                        + "\" ends with a , character");
               }
            }
         }
      }
   }

   public void addAlreadyexecuted(String value) {
      if (alreadyExecuted == null) {
         alreadyExecuted = new ArrayList(2);
      }
      alreadyExecuted.add(value);
   }

   protected Set getSucceededTarget() {
      Set s = (Set) getProject().getReference(SUCCEEDEDTARGETS_KEY);
      if (s == null) {
         s = new HashSet();
         getProject().addReference(SUCCEEDEDTARGETS_KEY, s);
      }

      return s;
   }

   protected void addSucceededTarget(String name) {
      getSucceededTarget().add(name);
   }

   public void execute() throws BuildException {
      synchronized (lock) {
         if (!started) {
            removeOwnerDependencies(getOwningTarget());
            if (alreadyExecuted != null) {
               for (Iterator iter = alreadyExecuted.iterator(); iter.hasNext();) {
                  String target = (String) iter.next();
                  Target dep = (Target) getProject().getTargets().get(target);

                  if (dep == null) {
                     throw new BuildException("Target named \"" + target
                           + "\" does not exists");
                  }

                  addSucceededTarget(target);
                  removeOwnerDependencies(dep);
               }
            }
            getProject().addBuildListener(this);
            started = true;
         }
      }
   }

   protected void removeOwnerDependencies(Target owner) {
      if (owner == null) {
         return;
      }

      Enumeration deps = owner.getDependencies();
      while (deps.hasMoreElements()) {
         String dependencyName = (String) deps.nextElement();
         Target dep = (Target) getProject().getTargets().get(dependencyName);
         addSucceededTarget(dependencyName);

         removeOwnerDependencies(dep);
      }
   }

   public void buildStarted(BuildEvent event) {
      getSucceededTarget().clear();
   }

   public void buildFinished(BuildEvent event) {
   }

   public void targetStarted(BuildEvent event) {
   }

   public void targetFinished(BuildEvent event) {
      addSucceededTarget(event.getTarget().getName());
   }

   public void taskStarted(BuildEvent event) {
   }

   public void taskFinished(BuildEvent event) {
   }

   public void messageLogged(BuildEvent event) {
   }
}