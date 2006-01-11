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
package net.java.dev.genesis.plugins.netbeans.projecttype;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

public class GenesisProjectType implements AntBasedProjectType {
   public static final String TYPE = 
         "net.java.dev.genesis.plugins.netbeans.projecttype";
   private static final String PROJECT_CONFIGURATION_NAME = "data";
   public static final String PROJECT_CONFIGURATION_NAMESPACE = 
         "https://genesis.dev.java.net/ns/netbeans/projecttype/1";
   private static final String PRIVATE_CONFIGURATION_NAME = "data";
   private static final String PRIVATE_CONFIGURATION_NAMESPACE = 
         "https://genesis.dev.java.net/ns/netbeans/projecttype-private/1";

   public String getType() {
      return TYPE;
   }

   public Project createProject(AntProjectHelper helper) throws IOException {
      return new GenesisProject(helper);
   }

   public String getPrimaryConfigurationDataElementName(boolean shared) {
      return shared ? PROJECT_CONFIGURATION_NAME : PRIVATE_CONFIGURATION_NAME;
   }

   public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
      return shared ? PROJECT_CONFIGURATION_NAMESPACE : 
            PRIVATE_CONFIGURATION_NAMESPACE;
   }
}