package net.java.dev.genesis.plugins.netbeans.projecttype;

import net.java.dev.reusablecomponents.lang.Enum;

public class GenesisProjectExecutionMode extends Enum {
   public static final GenesisProjectExecutionMode LOCAL_MODE_ONLY = 
         new GenesisProjectExecutionMode("LOCAL_MODE_ONLY");
   public static final GenesisProjectExecutionMode REMOTE_MODE_ONLY = 
         new GenesisProjectExecutionMode("REMOTE_MODE_ONLY");
   public static final GenesisProjectExecutionMode LOCAL_AND_REMOTE = 
         new GenesisProjectExecutionMode("LOCAL_AND_REMOTE");

   private GenesisProjectExecutionMode(String name) {
      super(name);
   }
}
