/**
 *
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import java.io.File;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.publish.FormPublisher;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class SaveEchoFormAction extends CallableSystemAction {

    /**
     * 
     */
    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String name = JDesiWindowManager.getActiveDesignerPage().getSaveName();
                FormPublisher formPublisher = (FormPublisher) JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                if (!((name == null) || (name.equals("")))) {
                    // Ticket #354
                    if (name.indexOf("\\") == -1) {
                        JDesiWindowManager.getActiveDesignerPage().setSaveName(null);
                        if (formPublisher.doSave() == -1) {
                            JDesiWindowManager.getActiveDesignerPage().setSaveName(name);
                        }
                    } else {
                        File f = new File(name);
                        if (f.exists()) {
                            formPublisher.saveEchoForm(name);
                        } else {
                            JDesiWindowManager.getActiveDesignerPage().setSaveName(null);
                            if (formPublisher.doSave() == -1) {
                                JDesiWindowManager.getActiveDesignerPage().setSaveName(name);
                            }
                        }
                    }
                } else {
                    formPublisher.doSave();
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        //Ticket #32
        return (JDesiWindowManager.getActiveDesignerPage() != null);
        //return super.isEnabled();
    }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(SaveEchoFormAction.class, "CTL_SaveEchoFormAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/save.gif";
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
