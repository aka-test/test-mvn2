/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import com.echoman.designer.components.echocommon.TableForm;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class EchoDefaultColumns extends CallableSystemAction {

    @Override
    public String getName() {
        return NbBundle.getMessage(LinkForms.class, "CTL_EchoDefaultColumns");
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/adddefcol.gif";
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
        TableForm tblForm = new TableForm(null, true, null);
        tblForm.setVisible(true);
    }
}
