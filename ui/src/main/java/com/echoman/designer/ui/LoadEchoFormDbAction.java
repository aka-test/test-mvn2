/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.publish.FormPublisher;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class LoadEchoFormDbAction extends CallableSystemAction {

    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    // Create a new topcomponent for our new window.
                    EchoDesignerTopComponent ntc = new EchoDesignerTopComponent();
                    FormPublisher fp = ((FormPublisher)ntc.getFormPublisher());
                    // Even though it would be better to do this after the
                    // file selection was done, we need the topcomponent active
                    // in order to find the tomcat server directory where the
                    // forms are stored.
                    if (fp.canLoadFromDb()) {
                        ntc.open();
                        ntc.requestActive();
                        JDesiWindowManager.setActiveDesignerPage(ntc);
                        fp.doLoadFromDb();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Failed to load from database. Please make sure jdesi forms table is properly setup.");
                    }
                                
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
        return NbBundle.getMessage(LoadEchoFormAction.class, "CTL_LoadEchoFormDbAction");
    }

    /**
     *
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/loadFromDb.gif";
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
}
