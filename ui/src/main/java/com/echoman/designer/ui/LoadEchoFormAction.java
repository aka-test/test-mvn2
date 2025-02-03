/**
 *
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class LoadEchoFormAction extends CallableSystemAction {

    /**
     * 
     */
    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    // Create a new topcomponent for our new window.
                    EchoDesignerTopComponent ntc = new EchoDesignerTopComponent();
                    // Even though it would be better to do this after the
                    // file selection was done, we need the topcomponent active
                    // in order to find the tomcat server directory where the
                    // forms are stored.
                    ntc.open();
                    ntc.requestActive();
                    JDesiWindowManager.setActiveDesignerPage(ntc);
                    ntc.getFormPublisher().doLoad();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
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
        return NbBundle.getMessage(LoadEchoFormAction.class, "CTL_LoadEchoFormAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/form_imp_16.png";
    }

    /**
     * 
     * @return
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

    @Override
    public boolean isEnabled() {
        return true;
    }

}
