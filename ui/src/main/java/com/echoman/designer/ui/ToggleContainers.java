/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import java.util.ArrayList;
import javax.swing.JComponent;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ToggleContainers extends CallableSystemAction {
    boolean showing = true;

    @Override
    public String getName() {
        return NbBundle.getMessage(ToggleContainers.class, "CTL_ToggleContainers");
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/toggleContainers.gif";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public void performAction() {
        ArrayList<IEchoComponentNodeData> allNodes = JDesiWindowManager.getActiveDesignerPage().getCompList();
        for (IEchoComponentNodeData thisNode : allNodes) {
            if (thisNode.getClass().getName().contains("EchoDataContainer")) {
                if (showing) {
                    ((JComponent)thisNode.getComponent()).setVisible(false);
                } else {
                    ((JComponent)thisNode.getComponent()).setVisible(true);
                }
            }
        }
        showing = !showing;
    }

    @Override
    public boolean isEnabled() {
        return (JDesiWindowManager.getActiveDesignerPage() != null);
    }
}
