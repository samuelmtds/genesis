/*
 * GenesisProjectProperties.java
 *
 * Created on 25 de Agosto de 2007, 00:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;


import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;
import net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer.annotation.Property;

/**
 *
 * @author Michel Graciano
 */
public class GenesisProjectProperties {
    
    protected GenesisProject project;
    
    /** Creates a new instance of GenesisProjectProperties */
    GenesisProjectProperties( GenesisProject project ) {
        this.project = project;
    }
    
    /**
     * Mantém o valor da propriedade name.
     */
    private String name;
    
    /**
     * "Getter" para a propriedade name.
     * @return Valor para a propriedade name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * "Setter" para a propriedade name.
     * @param name Novo valor para a propriedade name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Mantém o valor da propriedade prettyName.
     */
    private String prettyName;
    
    /**
     * "Getter" para a propriedade prettyName.
     * @return Valor para a propriedade prettyName.
     */
    @Property(value="genesisBasedApplication.prettyName")
    public String getPrettyName() {
        return this.prettyName;
    }
    
    /**
     * "Setter" para a propriedade prettyName.
     * @param prettyName Novo valor para a propriedade prettyName.
     */
    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    /**
     * Mantém o valor da propriedade genesisHomePath.
     */
    private String genesisHomePath;

    /**
     * "Getter" para a propriedade genesisHomePath.
     * @return Valor para a propriedade genesisHomePath.
     */
    @Property("genesis.home")
    public String getGenesisHomePath() {
        return this.genesisHomePath;
    }

    /**
     * "Setter" para a propriedade genesisHomePath.
     * @param genesisHomePath Novo valor para a propriedade genesisHomePath.
     */
    public void setGenesisHomePath(String genesisHomePath) {
        this.genesisHomePath = genesisHomePath;
    }

    /**
     * Mantém o valor da propriedade mainClass.
     */
    private String mainClass;

    /**
     * "Getter" para a propriedade mainClass.
     * @return Valor para a propriedade mainClass.
     */
    @Property("genesisBasedApplication.mainClass")
    public String getMainClass() {
        return this.mainClass;
    }

    /**
     * "Setter" para a propriedade mainClass.
     * @param mainClass Novo valor para a propriedade mainClass.
     */
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Mantém o valor da propriedade localMode.
     */
    private boolean localMode;

    /**
     * "Getter" para a propriedade localMode.
     * @return Valor para a propriedade localMode.
     */
    @Property("local.mode")
    public boolean isLocalMode() {
        return this.localMode;
    }

    /**
     * "Setter" para a propriedade localMode.
     * @param localMode Novo valor para a propriedade localMode.
     */
    public void setLocalMode(boolean localMode) {
        this.localMode = localMode;
    }

    /**
     * Mantém o valor da propriedade remoteMode.
     */
    private boolean remoteMode;

    /**
     * "Getter" para a propriedade remoteMode.
     * @return Valor para a propriedade remoteMode.
     */
    @Property("remote.mode")
    public boolean isRemoteMode() {
        return this.remoteMode;
    }

    /**
     * "Setter" para a propriedade remoteMode.
     * @param remoteMode Novo valor para a propriedade remoteMode.
     */
    public void setRemoteMode(boolean remoteMode) {
        this.remoteMode = remoteMode;
    }
    
}
