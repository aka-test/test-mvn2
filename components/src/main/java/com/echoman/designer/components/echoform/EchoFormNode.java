/**
 *
 */
package com.echoman.designer.components.echoform;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.FormLocationsPropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.TablesPropertyEditor;
import com.echoman.designer.components.echocommon.TabsEditor;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 *
 * @author Dave Athlon
 */
public class EchoFormNode extends EchoBaseNode {

    /**
     * 
     * @param o
     */
    public EchoFormNode(EchoFormNodeData o) {
        super(o);
        setName("Form" + o.getIndex());
    }

    /**
     * 
     * @return
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("property");
        Sheet.Set set2 = Sheet.createPropertiesSet();
        set2.setDisplayName("Style");
        set2.setName("style");
        set2.setValue("tabName", "Style");
        Sheet.Set set3 = Sheet.createPropertiesSet();
        set3.setDisplayName("Events");
        set3.setName("events");
        set3.setValue("tabName", "Events");

        try {
            JPanel dropPanel = (JPanel) getComponent();

            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            //Remove Form Name from Properties Pane but keep formName property in XML with actual saved form name.
            //registerProperty(new PropInfo("Form Name", String.class, "formName"), set);
            // True at the end signifies that only the custom editor is used to set the value.
            registerProperty(new PropInfo("Form Locations", ArrayList.class, "formLocations", null, null, FormLocationsPropertyEditor.class, null, true), set);
            registerProperty(new PropInfo("Caption", String.class, "caption"), set);
            registerProperty(new PropInfo("Full Screen", boolean.class, "fullScreen"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Tabs", String.class, "tabs", null, null, TabsEditor.class, null, true), set);
            //registerProperty(new PropInfo("Table", String.class, "table", null, null, TableLinkEditor.class, null), set);
            registerProperty(new PropInfo("Table", String.class, "table", null, null, TablesPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Form Link Field", String.class, "formLinkField", null, null, ColumnsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Filter SQL", String.class, "filterSql"), set);
            registerProperty(new PropInfo("Sort SQL", String.class, "sortOrder"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Read Only", boolean.class, "readOnly"), set);
            registerProperty(new PropInfo("Signable", boolean.class, "signable"), set);
            registerProperty(new PropInfo("Show Navigator", boolean.class, "showNavigator"), set);
            registerProperty(new PropInfo("Allow Close", boolean.class, "closable"), set);
            registerProperty(new PropInfo("Modal Form", boolean.class, "modal"), set);
            registerProperty(new PropInfo("History Form", boolean.class, "historyForm"), set);
            // Ticket 447
            registerProperty(new PropInfo("Hide Insert", boolean.class, "hideInsert"), set);
            registerProperty(new PropInfo("Hide Delete", boolean.class, "hideDelete"), set);
            // Ticket 521 Removed Record Description Property - not used.
            if (dropPanel == null) {
                registerProperty(new PropInfo("Color", Color.class, "Color", null, null, null, Color.white), set2);
            } else {
                registerProperty(new PropInfo("Color", Color.class, "Color", null, null, null, dropPanel.getBackground()), set2);
            }
            registerProperty(new PropInfo("On Form Open", String.class, "formOpenEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Form Close", String.class, "formCloseEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Form After Save", String.class, "formAfterSaveEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("Prevent Multiple Records", boolean.class, "preventMultipleRecords"), set);
            registerProperty(new PropInfo("Use Legacy Styles", boolean.class, "useLegacyStyles"), set);
        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoFormNode] CreateSheet error. " + ex.toString());
        }

        boolean fullScreen = ((EchoFormNodeData)nodeData).isFullScreen();
        if (fullScreen) {
            set.getProperties()[5].setHidden(true);
            set.getProperties()[6].setHidden(true);
        } else {
            set.getProperties()[5].setHidden(false);
            set.getProperties()[6].setHidden(false);
        }


        sheet.put(set);
        sheet.put(set2);
        sheet.put(set3);
        storeSheet(sheet);
        nodeData.getDesignerPage().getUndoManager().discardAllEdits();
        return sheet;
    }

    /**
     * 
     * @param type
     * @return
     */
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("com/echoman/designer/inspector/radio_button_16.png");
    }

    /**
     * 
     * @param i
     * @return
     */
    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public String getNodeDisplayName() {
        return nodeData.getIndex() + " - " + ((EchoFormNodeData) nodeData).getCaption();
    }

    @Override
    public void delete() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        Node[] selectedNodes = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        //Ticket #253
        //if there are selected nodes other then formnode
        //delete the node instead of clear the form.
        for (Node node : selectedNodes) {
            if (node instanceof EchoFormNode) {
                break;
            }
            ((EchoBaseNode)node).delete();
            return;
        }
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to clear this form?") == 0) {
            List lbls = new ArrayList();
            int cnt = 0;
            while (compList.size() != cnt) {
                EchoBaseNodeData nd = (EchoBaseNodeData) compList.get(cnt);
                if ((EchoUtil.isRunningAsEchoAdmin()) || (!nd.hasLockedField())) {
                    //make sure to delete caption and translation labels of text field
                    //so it won't register with undo manager
                    if (nd instanceof EchoFormNodeData) {
                        cnt++;
                        continue;
                    } else if (nd instanceof EchoTextFieldNodeData) {
                        Object captionlbl = null;
                        Object translbl = null;
                        EchoTextFieldNodeData tnd = (EchoTextFieldNodeData) nd;
                        for (int j = 0; j < compList.size(); j++) {
                            Object o = compList.get(j);
                            if (((EchoBaseNodeData) o).getId().equals(tnd.getCaptionLabelId())) {
                                captionlbl = o;
                            } else if (((EchoBaseNodeData) o).getId().equals(tnd.getTranslationLabelId())) {
                                translbl = o;
                            }
                        }
                        if (captionlbl != null) {
                            compList.remove(captionlbl);
                            cnt--;
                        }
                        if (translbl != null) {
                            compList.remove(translbl);
                            cnt--;
                        }
                    } else if (nd instanceof EchoLabelNodeData) {
                        lbls.add(nd);
                        cnt++;
                        continue;
                    }
                    deleteUndoableHappened((EchoBaseNodeData) nd);
                    compList.remove(nd);
                } else {
                    cnt++;
                }
            }
            for (int i = 0; i < lbls.size(); i++) {
                Object o = lbls.get(i);
                if (compList.contains(o)) {
                    deleteUndoableHappened((EchoBaseNodeData) o);
                    compList.remove(o);
                }
            }
            nodeData.getDesignerPage().getInspector().refreshList(compList);
            ((TopComponent) nodeData.getDesignerPage()).paintImmediately(((TopComponent) nodeData.getDesignerPage()).getVisibleRect());
        }
    }
}
