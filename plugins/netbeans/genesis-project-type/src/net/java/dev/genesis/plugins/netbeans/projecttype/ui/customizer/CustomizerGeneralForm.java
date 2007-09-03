/*
 * CustomizerGeneralView.java
 *
 * Created on 25 de Agosto de 2007, 00:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.java.dev.genesis.plugins.netbeans.projecttype.ui.customizer;

import java.io.File;

import net.java.dev.genesis.annotation.Form;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Michel Graciano
 */
@Form
public class CustomizerGeneralForm extends GenesisProjectProperties {
    
    /** Creates a new instance of CustomizerGeneralView */
    public CustomizerGeneralForm( GenesisProject project ) throws Exception {
        super( project );
    }
    
    public String getProjectFolder() {
        FileObject projectFolder = this.project.getProjectDirectory();
        File pf = FileUtil.toFile( projectFolder );
        return pf == null ? "" : pf.getPath(); // NOI18N
    }
    
}
