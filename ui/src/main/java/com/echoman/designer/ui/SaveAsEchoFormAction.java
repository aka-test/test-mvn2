/**
 *
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.publish.FormPublisher;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class SaveAsEchoFormAction extends CallableSystemAction {

    /**
     * 
     */
    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, "Warning - The Export Scripts created will overwrite any existing Forms and Validations.\n");
                FormPublisher formPublisher = (FormPublisher)JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                formPublisher.doSave();
            }
        });
    }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(SaveAsEchoFormAction.class, "CTL_SaveAsEchoFormAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/form_exp_16.png";
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
        //Ticket #32
        return (JDesiWindowManager.getActiveDesignerPage() != null);
//        return super.isEnabled();
    }


}
