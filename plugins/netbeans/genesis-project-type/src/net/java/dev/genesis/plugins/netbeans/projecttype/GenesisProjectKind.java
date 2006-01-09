package net.java.dev.genesis.plugins.netbeans.projecttype;

import net.java.dev.reusablecomponents.lang.Enum;

public class GenesisProjectKind extends Enum {
   private static final GenesisProjectKind DESKTOP = new GenesisProjectKind("desktop");
   private static final GenesisProjectKind FREEFORM = new GenesisProjectKind("freeform");
   private static final GenesisProjectKind WEB = new GenesisProjectKind("web");
   
   private GenesisProjectKind(String name) {
      super(name);
   }
}
