/**
 *
 */
package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.AllTableColumnEditor;
import com.echoman.designer.components.echocommon.ColumnPropEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.LinkTableColumnEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.SelectFDNextFormPropertyEditor;
import com.echoman.designer.components.echocommon.SelectFormPropertyEditor;
import com.echoman.designer.components.echocommon.SelectUIPropertyEditor;
import com.echoman.jdesi.FormData;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoTableNode extends EchoBaseNode {
    /**
     * 
     * @param o
     */
    public EchoTableNode(EchoTableNodeData o) {
        super(o);
        setName("Table" + o.getIndex());
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
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Tab Order", Integer.class, "tabOrder"), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Table", String.class, "getTable", null), set);
            registerProperty(new PropInfo("Table Columns", String.class, "columns", null, null, ColumnPropEditor.class, null), set);
            //Take this out until paging is implemented for table
            //registerProperty(new PropInfo("Rows Per Page", Integer.class, "rowsPerPage"), set);
            registerProperty(new PropInfo("Allow Editing", boolean.class, "editable"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);

            // FDNext Forms
            registerProperty(new PropInfo("FDNext Form", FormData.class, "popupFdNextForm", null, null, SelectFDNextFormPropertyEditor.class, null, true), set);

            // Ticket 529
            registerProperty(new PropInfo("Form Name", String.class, "popupFormName", null, null, SelectFormPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Form Link To Column", String.class, "formLinkToColumn", null, null, AllTableColumnEditor.class, null), set);
            registerProperty(new PropInfo("Form Link Value Column", String.class, "formLinkColumn", null, null, LinkTableColumnEditor.class, null), set);
            registerProperty(new PropInfo("Form Link to Key Field", String.class, "formLinkToKeyField"), set);
            registerProperty(new PropInfo("Form Link From Key Field", String.class, "formLinkFromKeyField"), set);
            registerProperty(new PropInfo("UI Name", String.class, "popupUiName", null, null, SelectUIPropertyEditor.class, null), set);
            registerProperty(new PropInfo("UI Link To Column", String.class, "getUiLinkToColumn", null), set);
            registerProperty(new PropInfo("UI Link Value Column", String.class, "uiLinkColumn", null, null, LinkTableColumnEditor.class, null), set);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            // Ticket 447
            registerProperty(new PropInfo("Insert Event", String.class, "insertEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("Delete Event", String.class, "deleteEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("Save Event", String.class, "saveEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("Scroll Event", String.class, "scrollEvent", null, null, EventPropertyEditor.class, null), set3);

            registerProperty(new PropInfo("Navigation Grid", boolean.class, "navigationGrid"), set);
            registerProperty(new PropInfo("Hide Insert", boolean.class, "hideInsert"), set);
            registerProperty(new PropInfo("Hide Delete", boolean.class, "hideDelete"), set);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoTableNode] CreateSheet error. " + ex.toString());
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
        return nodeData.getIndex() + " - " + ((EchoTableNodeData)nodeData).getClass().getName();
    }
}
