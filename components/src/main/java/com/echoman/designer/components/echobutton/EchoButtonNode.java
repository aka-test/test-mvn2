/**
 *
 */
package com.echoman.designer.components.echobutton;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.AllTableColumnEditor;
import com.echoman.designer.components.echocommon.ButtonTypePropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.LinkTableColumnEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.SelectFDNextFormPropertyEditor;
import com.echoman.designer.components.echocommon.SelectFormPropertyEditor;
import com.echoman.jdesi.FormData;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoButtonNode extends EchoBaseNode {

    private static final int PROPERTY_INDEX_FDNEXT_FORM = 10;
    private static final int PROPERTY_INDEX_FORM_NAME = 11;
    private static final int PROPERTY_INDEX_FORM_LINK_TO_COLUMN = 12;
    private static final int PROPERTY_INDEX_FORM_LINK_VALUE_COLUMN = 13;
    private static final int PROPERTY_INDEX_URL_LINK = 14;
    private static final int PROPERTY_INDEX_FILE_NAME = 15;
    private static final int PROPERTY_INDEX_INHERIT_PARENT_LOCK = 16;

    /**
     * 
     * @param o
     */
    public EchoButtonNode(EchoButtonNodeData o) {
        super(o);
        setName("Button" + o.getIndex());
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
            registerProperty(new PropInfo("Caption", String.class, "caption"), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Button Type", String.class, "buttonType", null, null, ButtonTypePropertyEditor.class, null), set);
            registerProperty(new PropInfo("FDNext Form", FormData.class, "popupFdNextForm", null, null, SelectFDNextFormPropertyEditor.class, null, true), set);
            registerProperty(new PropInfo("Form Name", String.class, "popupFormName", null, null, SelectFormPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Form Link To Column", String.class, "formLinkToColumn", null, null, AllTableColumnEditor.class, null), set);
            registerProperty(new PropInfo("Form Link Value Column", String.class, "formLinkColumn", null, null, LinkTableColumnEditor.class, null), set);
            //registerProperty(new PropInfo("Form Link To Column", String.class, "formLinkToColumn"), set);
            //registerProperty(new PropInfo("Form Name", String.class, "popupFormName", null, null, ButtonPopupFormPropertyEditor.class, null), set);
            registerProperty(new PropInfo("URL Link", String.class, "urlLink"), set);
            registerProperty(new PropInfo("File Name", File.class, "fileName"), set);
            registerProperty(new PropInfo("Inherit Parent Lock", boolean.class, "inheritParentLock"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            registerProperty(new PropInfo("Click Event", String.class, "clickEvent", null, null, EventPropertyEditor.class, null), set3);


        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoButtonNode] CreateSheet error. " + ex.toString());
        }

        // Show/hide properties based on the button type.
        String buttonType = ((EchoButtonNodeData)nodeData).getButtonType();
        if (buttonType.equals("URL Link")) {
            showUrlLinkProperties(set);
        } else if (buttonType.equals("Form Link")) {
            showFormLinkProperties(set);
        } else if (buttonType.equals("File Link")) {
            showFileLinkProperties(set);
        } else {
            showAllLinkProperties(set);
        }
        
        sheet.put(set);
        sheet.put(set2);
        sheet.put(set3);
        storeSheet(sheet);
        return sheet;
    }

    public static void showUrlLinkProperties(PropertySet set) {
        set.getProperties()[PROPERTY_INDEX_FDNEXT_FORM].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_TO_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_VALUE_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_URL_LINK].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_FILE_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_INHERIT_PARENT_LOCK].setHidden(true);
    }

    public static void showFormLinkProperties(PropertySet set) {
        set.getProperties()[PROPERTY_INDEX_FDNEXT_FORM].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_FORM_NAME].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_TO_COLUMN].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_VALUE_COLUMN].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_URL_LINK].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FILE_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_INHERIT_PARENT_LOCK].setHidden(false);
    }

    public static void showFileLinkProperties(PropertySet set) {
        set.getProperties()[PROPERTY_INDEX_FDNEXT_FORM].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_TO_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_VALUE_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_URL_LINK].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FILE_NAME].setHidden(false);
        set.getProperties()[PROPERTY_INDEX_INHERIT_PARENT_LOCK].setHidden(true);
    }

    public static void showAllLinkProperties(PropertySet set) {
        set.getProperties()[PROPERTY_INDEX_FDNEXT_FORM].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_TO_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FORM_LINK_VALUE_COLUMN].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_URL_LINK].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_FILE_NAME].setHidden(true);
        set.getProperties()[PROPERTY_INDEX_INHERIT_PARENT_LOCK].setHidden(true);
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
        return nodeData.getIndex() + " - " + ((EchoButtonNodeData)nodeData).getCaption();
    }
}
