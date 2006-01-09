package net.java.dev.genesis.plugins.netbeans.projecttype;

import net.java.dev.reusablecomponents.lang.Enum;

public class GenesisProjectKind extends Enum {
   public static final GenesisProjectKind DESKTOP = new GenesisProjectKind("desktop");
   public static final GenesisProjectKind FREEFORM = new GenesisProjectKind("freeform");
   public static final GenesisProjectKind WEB = new GenesisProjectKind("web");
   
   private GenesisProjectKind(String name) {
      super(name);
   }
}
