/**
 *
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class NewEchoFormAction extends CallableSystemAction {

    /**
     * 
     */
    @Override
    public void performAction() {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                // Create a new topcomponent for our new window.
                if (!DBConnections.isConnecting()) {
                    try {
                        EchoDesignerTopComponent ntc = new EchoDesignerTopComponent();
                        JDesiWindowManager.setActiveDesignerPage(ntc);
                        ntc.open();
                        ntc.requestActive();
                        ntc.getFormPublisher().doNew();
                        JDesiWindowManager.setToolbarEnabled(true);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(NewEchoFormAction.class, "CTL_NewEchoFormAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/new.gif";
    }

    /**
     *
     */
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * 
     * @return
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }
}
