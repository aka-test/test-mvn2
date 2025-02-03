/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import com.echoman.designer.components.echocommon.CopyPasteManager;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class CopyComponentAction  extends CallableSystemAction {

    @Override
    public boolean isEnabled() {
        if (JDesiWindowManager.getActiveDesignerPage() == null) {
            return false;
        } else {
            return JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes().length > 0;
        }
    }

    @Override
    public void performAction() {
        if (JDesiWindowManager.getActiveDesignerPage() != null) {
            if (JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes().length > 0) {
                CopyPasteManager.getInstance().copy(
                        JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes());
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CopyComponentAction.class, "CTL_CopyComponentAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/copy.png";
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

}

