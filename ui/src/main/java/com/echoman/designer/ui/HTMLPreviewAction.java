/**
 * 
 */
package com.echoman.designer.ui;

import java.awt.EventQueue;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.publish.FormPublisher;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * 
 * @author david.morin
 */
public final class HTMLPreviewAction extends CallableSystemAction {

    /**
     * 
     */
    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FormPublisher formPublisher = (FormPublisher)JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                formPublisher.doPreview();
            }
        });
     }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(HTMLPreviewAction.class, "CTL_HTMLPreviewAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/preview.gif";
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
        //return super.isEnabled();
    }
}
