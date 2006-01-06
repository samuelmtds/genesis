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
         "https://genesis.dev.java.net/nonav/ns/netbeans/projecttype/1";
   private static final String PRIVATE_CONFIGURATION_NAME = "data";
   private static final String PRIVATE_CONFIGURATION_NAMESPACE = 
         "https://genesis.dev.java.net/nonav/ns/netbeans/projecttype-private/1";

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