package net.java.dev.genesis.plugins.netbeans.projecttype.ui.templates;

import java.io.IOException;
import java.util.Set;

import javax.swing.event.ChangeListener;

import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

public class GenesisWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    protected transient WizardDescriptor.InstantiatingIterator delegateIterator;
    protected WizardDescriptor wizard;
    
    public static GenesisWizardIterator create() {
        return new GenesisWizardIterator();
    }
    
    public GenesisWizardIterator() {
        delegateIterator = JavaTemplates.createJavaTemplateIterator();
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        delegateIterator.initialize(wizard);
    }
    
    public void uninitialize(WizardDescriptor wizard) {
        delegateIterator.uninitialize(wizard);
    }
    
    public Set instantiate() throws IOException, IllegalArgumentException {
        Set set = delegateIterator.instantiate();
        
        try {
            FileObject template = (FileObject) set.iterator().next();
            DataObject dobj = DataObject.find(template);
            
            dobj.getPrimaryFile().setAttribute("justCreatedByNewWizard", Boolean.TRUE); // NOI18N
        } catch (Exception ex) {}
        
        return set;
    }
    
    public WizardDescriptor.Panel current() {
        return delegateIterator.current();
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public boolean hasPrevious() {
        return delegateIterator.hasPrevious();
    }
    
    public void nextPanel() {
        delegateIterator.nextPanel();
    }
    
    public void previousPanel() {
        delegateIterator.previousPanel();
    }
    
    public void addChangeListener(ChangeListener l) {
        delegateIterator.addChangeListener(l);
    }
    
    public String name() {
        return delegateIterator.name(); // NOI18N
    }
    
    public void removeChangeListener(ChangeListener l) {
        delegateIterator.removeChangeListener(l);
    }
}
