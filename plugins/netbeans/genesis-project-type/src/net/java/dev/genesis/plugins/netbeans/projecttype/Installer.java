package net.java.dev.genesis.plugins.netbeans.projecttype;

import net.java.dev.genesis.helpers.StartupHelper;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
   
   public void restored() {
      StartupHelper helper = new StartupHelper();
      helper.setLoadValidatorRules(false);
      helper.setRunNoopCommand(false);
      helper.initialize();
   }
   
}
