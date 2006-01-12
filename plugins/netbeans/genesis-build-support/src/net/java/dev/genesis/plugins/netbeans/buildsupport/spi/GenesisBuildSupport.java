package net.java.dev.genesis.plugins.netbeans.buildsupport.spi;

import java.io.IOException;

public interface GenesisBuildSupport {
   public void generateBuildFiles(boolean check) throws IOException;
   public GenesisVersion getVersion();
}