/**
 *
 */
package com.echoman.designer.components.echoform;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * 
 * @author david.morin
 */
final class MiscOptionsPanelController extends OptionsPanelController {

    private MiscPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     * 
     */
    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     * 
     */
    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    /**
     * 
     */
    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     * 
     * @return
     */
    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /**
     * 
     * @param masterLookup
     * @return
     */
    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /**
     * 
     * @param l
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * 
     * @param l
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    /**
     * 
     * @return
     */
    private MiscPanel getPanel() {
        if (panel == null) {
            panel = new MiscPanel();
        }
        return panel;
    }

    /**
     * 
     */
    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
