/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import java.util.ArrayList;
import com.echoman.designer.components.echocommon.CopyPasteManager;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echoform.EchoForm;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class PasteComponentAction extends CallableSystemAction {

    @Override
    public boolean isEnabled() {
        if (JDesiWindowManager.getActiveDesignerPage() == null) {
            return false;
        } else {
            return CopyPasteManager.getInstance().count() > 0;
        }
    }

    @Override
    public void performAction() {
        if (JDesiWindowManager.getActiveDesignerPage() != null) {
            if (CopyPasteManager.getInstance().count() > 0) {
                ArrayList compList = JDesiWindowManager.getActiveDesignerPage().getCompList();
                EchoForm form = ((EchoFormNodeData) compList.get(0)).getActiveForm();
                CopyPasteManager.getInstance().paste(form);
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(PasteComponentAction.class, "CTL_PasteComponentAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/paste.png";
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

