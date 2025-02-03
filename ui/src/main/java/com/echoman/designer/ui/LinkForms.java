/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import javax.swing.JOptionPane;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class LinkForms extends CallableSystemAction {

    @Override
    public String getName() {
        return NbBundle.getMessage(LinkForms.class, "CTL_LinkForms");
    }

    @Override
    protected String iconResource() {
        return "com/echoman/designer/ui/icons/flow.gif";
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
        JOptionPane.showMessageDialog(null, "Window link dialog here...");
    }

    @Override
    public boolean isEnabled() {
        //Ticket #32
        return (JDesiWindowManager.getActiveDesignerPage() != null);
        //return super.isEnabled();
    }
}

