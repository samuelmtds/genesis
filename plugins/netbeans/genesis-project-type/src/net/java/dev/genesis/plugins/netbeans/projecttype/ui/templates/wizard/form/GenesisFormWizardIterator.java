package net.java.dev.genesis.plugins.netbeans.projecttype.ui.templates.wizard.form;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.java.dev.genesis.plugins.netbeans.projecttype.ui.templates.GenesisWizardIterator;

import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

public final class GenesisFormWizardIterator extends GenesisWizardIterator {
    
    public static GenesisWizardIterator create() {
        return new GenesisFormWizardIterator();
    }
    
    public Set instantiate() throws IOException, IllegalArgumentException {
        final FileObject dir = Templates.getTargetFolder( this.wizard );
        final String targetName = Templates.getTargetName( this.wizard );
        final Set< FileObject > createdFiles = new HashSet< FileObject >();
        
        dir.getFileSystem().runAtomicAction( new FileSystem.AtomicAction() {
            public void run() throws IOException {
                DataFolder df = DataFolder.findFolder( dir );
                FileObject template = Templates.getTemplate( wizard );
                
                DataObject dTemplate = DataObject.find( template );
                DataObject dobj = dTemplate.createFromTemplate( df, targetName );
                createdFiles.add( dobj.getPrimaryFile() );
                
                createdFiles.add( createFormPojo( targetName, df ) );
                
                dobj.getPrimaryFile().setAttribute("justCreatedByNewWizard", Boolean.TRUE); // NOI18N
            }
        });
        
        return createdFiles;
    }
    
    private FileObject createFormPojo( String fileBaseName, DataFolder targetFolder ) throws IOException {
        String formFileName = fileBaseName + "Form";
        
        FileObject template = Repository.getDefault().getDefaultFileSystem().
                findResource( "net-java-dev-genesis-plugins-netbeans-projecttype-templates/GenesisForm.java"); //NOI18N
        DataObject dTemplate = DataObject.find( template );
        DataObject dobj = dTemplate.createFromTemplate( targetFolder, formFileName );
        try {
            dobj.getPrimaryFile().setAttribute("justCreatedByNewWizard", Boolean.TRUE); // NOI18N
        } catch (Exception ex) {}
        
        return dobj.getPrimaryFile();
    }
}