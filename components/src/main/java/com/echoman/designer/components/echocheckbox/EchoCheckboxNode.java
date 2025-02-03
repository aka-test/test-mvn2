/**
 *
 */
package com.echoman.designer.components.echocheckbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.AlignmentPropertyEditor;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.PopupFromFieldValuePropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import java.util.ArrayList;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoCheckboxNode extends EchoBaseNode {
    /**
     * 
     * @param o
     */
    public EchoCheckboxNode(EchoCheckboxNodeData o) {
    	super(o);
        setName("Checkbox" + o.getIndex());
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
            registerProperty(new PropInfo("Alignment", String.class, "alignment", null, null, AlignmentPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Tab Order", Integer.class, "tabOrder"), set);
            registerProperty(new PropInfo("Caption", String.class, "caption"), set);
            registerProperty(new PropInfo("Checked Value", String.class, "checkedValue"), set);
            registerProperty(new PropInfo("Unchecked Value", String.class, "uncheckedValue"), set);
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
            registerProperty(new PropInfo("Popup From Field Value", ArrayList.class, "popupFromFieldValue", null, null, PopupFromFieldValuePropertyEditor.class, null, true), set);
            registerProperty(new PropInfo("Selected", boolean.class, "selected"), set);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            registerProperty(new PropInfo("Background Transparent", boolean.class, "backgroundTransparent"), set2);
            registerProperty(new PropInfo("On Change Procedure", String.class, "changeEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Scroll Procedure", String.class, "scrollEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Insert Procedure", String.class, "insertEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Delete Procedure", String.class, "deleteEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Save Procedure", String.class, "saveEvent", null, null, EventPropertyEditor.class, null), set3);
            
        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoCheckBoxNode] CreateSheet error. " + ex.toString());
        }
        sheet.put(set);
        sheet.put(set2);
        sheet.put(set3);
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
        return nodeData.getIndex() + " - " + ((EchoCheckboxNodeData)nodeData).getColumn();
    }
}
