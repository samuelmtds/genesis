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
    
}
