/**
 *
 */
package com.echoman.designer.components.echotextfield;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.DefaultValueEditor;
import com.echoman.designer.components.echocommon.DisplayMaskEditor;
import com.echoman.designer.components.echocommon.EchoUndoableEdit;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.InputMaskEditor;
import com.echoman.designer.components.echocommon.PopupFromFieldValuePropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.ValidationsPropertyEditor;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoTextFieldNode extends EchoBaseNode {

    public static final int PROPERTY_INDEX_VISIBLE = 15;
    public static final int PROPERTY_INDEX_READONLY = 16;
    public static final int PROPERTY_INDEX_REQUIRED = 17;

    /**
     * 
     * @param o
     */
    public EchoTextFieldNode(EchoTextFieldNodeData o) {
        super(o);
        setName("DataField" + o.getIndex());
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
            registerProperty(new PropInfo("Name", String.class, "name"), set);
            registerProperty(new PropInfo("Hint Text", String.class, "hintText"), set);
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Tab Order", Integer.class, "tabOrder"), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Table", String.class, "getTable", null), set);
            registerProperty(new PropInfo("Column", String.class, "column", null, null, ColumnsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Data Type", String.class, "getDataType", null), set);
            registerProperty(new PropInfo("Key Column", boolean.class, "getIsKeyCol", null), set);
            registerProperty(new PropInfo("GUID Column", boolean.class, "getisGUIDCol", null), set);
            registerProperty(new PropInfo("Validations Data", String.class, "validationData", null, null, ValidationsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Validation Description SQL", String.class, "validationDescriptionSql"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Read Only", boolean.class, "readOnly"), set);
            registerProperty(new PropInfo("Required", boolean.class, "required"), set);
            registerProperty(new PropInfo("Default Value", String.class, "defaultValue", null, null, DefaultValueEditor.class, null), set);
            registerProperty(new PropInfo("Input Mask", String.class, "inputMask", null, null, InputMaskEditor.class, null), set);
            registerProperty(new PropInfo("Display Mask", String.class, "displayMask", null, null, DisplayMaskEditor.class, null), set);
            registerProperty(new PropInfo("Popup From Field Value", ArrayList.class, "popupFromFieldValue", null, null, PopupFromFieldValuePropertyEditor.class, null, true), set);
            registerProperty(new PropInfo("Time Zone Offset", boolean.class, "timeZoneOffset"), set);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            registerProperty(new PropInfo("Border", boolean.class, "border_yn"), set2);
            registerProperty(new PropInfo("On Change Procedure", String.class, "changeEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Scroll Procedure", String.class, "scrollEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Insert Procedure", String.class, "insertEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Delete Procedure", String.class, "deleteEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Save Procedure", String.class, "saveEvent", null, null, EventPropertyEditor.class, null), set3);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoTextFieldNode] CreateSheet error. " + ex.toString());
        }

        // Show/hide Required properties based on visible/readonly.
        EchoTextFieldNodeData nd = (EchoTextFieldNodeData) nodeData;
        if (nd.getRequired()) {
            set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(true);
            set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(true);
        } else {
            set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(false);
            set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(false);

            if ((nd.getReadOnly()) || (!nd.getVisible())) {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            } else {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
            }

        }

        sheet.put(set);
        sheet.put(set2);
        sheet.put(set3);
        storeSheet(sheet);
        return sheet;
    }

    /**
     * 
     * @param type
     * @return
     */
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("com/echoman/designer/inspector/icons/icons/indent_radio_button_16.png");
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
        return nodeData.getIndex() + " - " + ((EchoTextFieldNodeData) nodeData).getColumn();
    }

    //Ticket #214
    @Override
    public void delete() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        Node[] selectedNodes = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        List comps = Arrays.asList(selectedNodes);
        EchoUndoableEdit edit = null;
        for (int i = 0; i < comps.size(); i++) {
            EchoBaseNode n = (EchoBaseNode) comps.get(i);
            IEchoComponentNodeData ndata = n.getNodeData();
            EchoBaseNodeData nd = (EchoBaseNodeData) ndata;
            if ((EchoUtil.isRunningAsEchoAdmin()) || (!nd.hasLockedField())) {
                deleteUndoableHappened(nd);
                edit = new EchoUndoableEdit(nd, EchoUndoableEdit.EDIT_TYPE_DELETE);
                compList.remove(ndata);
            }
        }
        if ((EchoUtil.isRunningAsEchoAdmin()) || (!nodeData.hasLockedField())) {
            //find and delete the label and translation
            EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) nodeData;
            IEchoComponentNodeData labelNodeData = null;
            IEchoComponentNodeData transNodeData = null;
            for (int j = 0; j < compList.size(); j++) {
                //Ticket #214
                if (compList.get(j) instanceof EchoLabelNodeData) {
                    EchoLabelNodeData lNodeData = (EchoLabelNodeData) compList.get(j);
                    if (lNodeData.getId().equals(textNodeData.getTranslationLabelId())) {
                        transNodeData = (IEchoComponentNodeData) compList.get(j);
                    } else if (lNodeData.getId().equals(textNodeData.getCaptionLabelId())) {
                        labelNodeData = (IEchoComponentNodeData) compList.get(j);
                    }
                }
            }
            if (labelNodeData != null) {
                if (edit != null) {
                    edit.addNodeData((EchoBaseNodeData) labelNodeData);
                }
                compList.remove(labelNodeData);
            }
            if (transNodeData != null) {
                if (edit != null) {
                    edit.addNodeData((EchoBaseNodeData) transNodeData);
                }
                compList.remove(transNodeData);
            }
            if (edit != null) {
                nodeData.getDesignerPage().getUndoManager().undoableEditHappened(new UndoableEditEvent(this, edit));
            }

        }
        refreshList();
    }
}
