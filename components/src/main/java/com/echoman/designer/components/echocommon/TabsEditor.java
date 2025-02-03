/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.List;
import javax.swing.JOptionPane;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import org.openide.nodes.Node;

/**
 *
 * @author Hendra
 */
public class TabsEditor extends PropertyEditorSupport {

    private boolean okToSaveValue = false;
    private static final String CONFIRM_MSG = "] is about to be deleted.\n"
            + "Do you want to move existing components on the tab "
            + "to the first tab or page?\n\n"
            + "Yes: Components will be moved to the first tab or page\n"
            + "No: Components will be deleted with the tab(s)\n\n";
    private static final String CONFIRM_TITLE = "Confirm Delete";

    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new TabPropertyForm(null, true, this) {

            @Override
            public boolean doAcceptValue(String tabs, String placement, List<TabObject> tabObjects) {
                Node[] ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
                EchoFormNodeData nodeData = (EchoFormNodeData) ((IEchoComponentNode) ary[0]).getNodeData();
                for (TabObject to : tabObjects) {
                    if (to.getStatus() == TabObject.DELETED) {
                        int res = JOptionPane.showConfirmDialog(this, "Tab ["
                                + to.getOldCaption() + CONFIRM_MSG,
                                CONFIRM_TITLE, JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            nodeData.deleteTab(to.getOldCaption(), 
                                    Integer.toString(to.getOldIndex()), false);
                        } else if (res == JOptionPane.NO_OPTION) {
                            nodeData.deleteTab(to.getOldCaption(), 
                                    Integer.toString(to.getOldIndex()), true);
                        }
                        // Ticket #381
                    } else if (to.getStatus() == TabObject.EDITED) {
                        nodeData.renameTab(to.getOldCaption(), to.getNewCaption());
                    }
                }
                okToSaveValue = true;
                setAsText(tabs);
                nodeData.setTabPosition(placement);
                nodeData.moveComponentsToFirstPage();
                for (TabObject to : tabObjects) {
                    if (to.getStatus() != TabObject.DELETED) {
                        if (to.getStatus() == TabObject.EDITED) {
                            nodeData.renameTab(to.getOldCaption(), to.getNewCaption());
                        }
                        if (to.getStatus() != TabObject.NEW) {
                            if (to.getOldIndex() != to.getNewIndex()) {
                                nodeData.syncTabComponents(to.getOldIndex(), to.getNewIndex());
                            }
                        }
                    }
                }
                nodeData.syncTabForms();
                return true;
            }
        };
    }

    /**
     *
     * @return
     */
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAsText() {
        return (String) getValue();
    }

    /**
     *
     * @param s
     */
    @Override
    public void setAsText(String s) {
        if (okToSaveValue) {
            setValue(s);
        }
    }
}
