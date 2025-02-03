/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import java.util.Arrays;
import java.util.List;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.CopyPasteManager;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class CutComponentAction extends CallableSystemAction {

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
                List comps = Arrays.asList(JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes());
                for (int i = 0; i < comps.size(); i++) {
                    EchoBaseNode n = (EchoBaseNode) comps.get(i);
                    if (!(n.getNodeData() instanceof EchoFormNodeData)) {
                        n.delete();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CutComponentAction.class, "CTL_CutComponentAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/cut.png";
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

}
