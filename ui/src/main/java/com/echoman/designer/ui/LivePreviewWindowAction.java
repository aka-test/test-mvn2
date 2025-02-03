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
public final class LivePreviewWindowAction extends CallableSystemAction {//implements ActionListener {

    /**
     * 
     * @param e
     */
    @Override
    public void performAction() {
        // This is required to avoid swing awt threat errors.
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                FormPublisher formPublisher = (FormPublisher)JDesiWindowManager.getActiveDesignerPage().getFormPublisher();
                formPublisher.doLivePreview();
            }
        });
    }
    /*
    @Override
    public void actionPerformed(ActionEvent e) {
    try{
    URLDisplayer.getDefault().showURL
    (new URL(HtmlBrowser.getHomePage()));
    } catch (Exception ex){
    return;//nothing much to do
    }
    }

     *
     */

    @Override
    public boolean isEnabled() {
        //Ticket #32
        return (JDesiWindowManager.getActiveDesignerPage() != null);
        //return super.isEnabled();
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(LivePreviewWindowAction.class, "CTL_LivePreviewWindow");
    }

    /**
     *
     * @return
     */
    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/livepreview.gif";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
