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
public final class ExportAction extends CallableSystemAction {

    /**
     * Create a FormPublisher instance and
     * do the export.
     */
    @Override
    public void performAction() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FormPublisher formPublisher = (FormPublisher)JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                formPublisher.doExport("export");
            }
        });
    }

    /**
     * 
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(ExportAction.class, "CTL_ExportAction");
    }

    /**
     * 
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/export.gif";
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
