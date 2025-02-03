/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import javax.swing.JToolBar;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import org.openide.awt.ToolbarPool;

/**
 *
 * @author Dave Athlon
 */
public class JDesiWindowManager {

    private static IEchoDesignerTopComponent active = null;

    public static IEchoDesignerTopComponent getActiveDesignerPage() {
        return active;
    }

    public static void setActiveDesignerPage(IEchoDesignerTopComponent activePage) {
        active = activePage;
        if (active == null) {
            setToolbarEnabled(false);
        } else {
            setToolbarEnabled(true);
        }
    }

    public static void setToolbarEnabled(boolean enabled) {
        JToolBar bar = ToolbarPool.getDefault().findToolbar("File");
        for (int i = 2; i < bar.getComponentCount() - 1; i++) {
            if (i == 5) {
                //import button is always enabled
                bar.getComponent(i).setEnabled(true);
            } else {
                bar.getComponent(i).setEnabled(enabled);
            }
        }
    }
}
