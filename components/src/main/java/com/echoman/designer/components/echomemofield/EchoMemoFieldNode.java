/**
 *
 */
package com.echoman.designer.components.echomemofield;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoMemoFieldNode extends EchoBaseNode {
    /**
     * 
     * @param o
     */
    public EchoMemoFieldNode(EchoMemoFieldNodeData o) {
        super(o);
        setName("MemoField" + o.getIndex());
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
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Read Only", boolean.class, "readOnly"), set);
            registerProperty(new PropInfo("Required", boolean.class, "required"), set);
            registerProperty(new PropInfo("Default Value", String.class, "defaultValue"), set);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            registerProperty(new PropInfo("On Change Procedure", String.class, "changeEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Scroll Procedure", String.class, "scrollEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Insert Procedure", String.class, "insertEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Delete Procedure", String.class, "deleteEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Save Procedure", String.class, "saveEvent", null, null, EventPropertyEditor.class, null), set3);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoMemoFieldNode] CreateSheet error. " + ex.toString());
        }

        // Show/hide Required properties based on visible/readonly.
        EchoMemoFieldNodeData nd = (EchoMemoFieldNodeData)nodeData;
        if (nd.getRequired()) {
            set.getProperties()[13].setHidden(true);
            set.getProperties()[14].setHidden(true);
        } else {
            set.getProperties()[13].setHidden(false);
            set.getProperties()[14].setHidden(false);

            if ((nd.getReadOnly()) || (!nd.getVisible())) {
                set.getProperties()[15].setHidden(true);
            } else {
                set.getProperties()[15].setHidden(false);
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
    public Image getIcon (int type) {
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
        return nodeData.getIndex() + " - " + ((EchoMemoFieldNodeData)nodeData).getColumn();
    }
}
