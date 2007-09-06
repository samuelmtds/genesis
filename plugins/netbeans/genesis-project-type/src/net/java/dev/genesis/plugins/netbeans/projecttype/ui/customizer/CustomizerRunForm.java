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

import javax.swing.JFileChooser;

import net.java.dev.genesis.annotation.Action;

import net.java.dev.genesis.annotation.Form;
import net.java.dev.genesis.plugins.netbeans.projecttype.GenesisProject;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.WindowManager;

/**
 *
 * @author Michel Graciano
 */
@Form
public class CustomizerRunForm extends GenesisProjectProperties {
    
    /** Creates a new instance of CustomizerGeneralView */
    public CustomizerRunForm( GenesisProject project ) throws Exception {
        super( project );
    }
}
