/**
 *
 */
package com.echoman.designer.ui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class DebugLogAction extends CallableSystemAction {

    /**
     * Constructor.
     * Specify no icon for menu item.
     */
    public DebugLogAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(DebugLogAction.class, "CTL_DebugLogAction"); // NOI18N
    }

    /**
     * 
     * @return
     */
    @Override public boolean asynchronous() {
        return false ;
    }

    /**
     * Show the log file in the output window.
     */
    @Override
    public void performAction() {
        String userDir = System.getProperty("netbeans.user");
        if (userDir == null)
                return;
        File f = new File(userDir + "/var/log/messages.log");
            DebugLogViewerSupport p = new DebugLogViewerSupport(f, NbBundle.getMessage(DebugLogAction.class, "CTL_DebugLogAction"));
        try {
            p.showLogViewer();
        } catch (java.io.IOException ex) {
            Logger.getLogger(DebugLogAction.class.getName()).log(Level.INFO, "Showing IDE log action failed", ex);
        }
    }

    /**
     * 
     * @return
     */
    @Override public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
