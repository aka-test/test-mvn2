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

public final class SaveAsEchoFormToDBAction extends CallableSystemAction  {

    @Override
    public void performAction() {

        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                FormPublisher formPublisher = (FormPublisher)JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                if (formPublisher.canLoadFromDb()) {
                    formPublisher.doSaveToDb(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Failed to save to database. Please make sure jdesi forms table is properly setup.");
                }
            }
        });
    }
    
    @Override
    public boolean isEnabled() {
        return (JDesiWindowManager.getActiveDesignerPage() != null);
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(SaveEchoFormToDBAction.class, "CTL_SaveAsEchoFormToDBAction");
    }

    /**
     *
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/saveasToDb.gif";
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
