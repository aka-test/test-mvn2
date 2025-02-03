/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Font;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.DefaultValueEditor;
import com.echoman.designer.components.echocommon.DisplayMaskEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.InputMaskEditor;
import com.echoman.designer.components.echocommon.PopupFromFieldValuePropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.ValidationsPropertyEditor;
import java.util.ArrayList;
import org.openide.nodes.Sheet;

/**
 *
 */
public class EchoColumnNode extends EchoBaseNode {

    public static final int PROPERTY_INDEX_VISIBLE = 5;
    public static final int PROPERTY_INDEX_READONLY = 6;
    public static final int PROPERTY_INDEX_REQUIRED = 7;

    public EchoColumnNode(EchoColumnNodeData nodeData) {
        super(nodeData);
        setName("Column" + nodeData.getIndex());
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
        //Ticket #502
        Sheet.Set set2 = Sheet.createPropertiesSet();
        set2.setDisplayName("Style");
        set2.setName("style");
        set2.setValue("tabName", "Style");        
        Sheet.Set set3 = Sheet.createPropertiesSet();
        set3.setDisplayName("Events");
        set3.setName("events");
        set3.setValue("tabName", "Events");

        try {

            registerProperty(new PropInfo("Name", String.class, "getName", "setName"), set);
            registerProperty(new PropInfo("Header", String.class, "getHeader", "setHeader"), set);
            registerProperty(new PropInfo("Width", Integer.class, "getWidth", "setWidth"), set);
            //Ticket #350 - Added property for Validations Data, Visible, Read only, Required,
            //    Default Value, Input Mask, Display Mask
            registerProperty(new PropInfo("Validations Data", String.class, null,
                    "getValidationData", "setValidationData", ValidationsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Validation Description SQL", String.class,
                    "getValidationDescriptionSql", "setValidationDescriptionSql"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "isVisible", "setVisible"), set);
            registerProperty(new PropInfo("Read Only", boolean.class, "isReadOnly", "setReadOnly"), set);
            registerProperty(new PropInfo("Required", boolean.class, "isRequired", "setRequired"), set);
            registerProperty(new PropInfo("Default Value", String.class, null,
                    "getDefaultValue", "setDefaultValue", DefaultValueEditor.class, null), set);
            registerProperty(new PropInfo("Input Mask", String.class, null,
                    "getMask", "setMask", InputMaskEditor.class, null), set);
            registerProperty(new PropInfo("Display Mask", String.class, null,
                    "getDisplayMask", "setDisplayMask", DisplayMaskEditor.class, null), set);
            registerProperty(new PropInfo("Popup From Field Value", ArrayList.class, "popupFromFieldValue", null, null, PopupFromFieldValuePropertyEditor.class, null, true), set);
            registerProperty(new PropInfo("Time Zone Offset", boolean.class, "timeZoneOffset"), set);

            //Ticket #502
            registerProperty(new PropInfo("Header Font", Font.class, "headingFont", true), set2);
            registerProperty(new PropInfo("Header Font Color", Color.class, "headingFontColor"), set2);

            // Ticket 447
            registerProperty(new PropInfo("Change Event", String.class, "changeEvent", null, null, EventPropertyEditor.class, null), set3);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoColumnNode] CreateSheet error. " + ex.toString());
        }

        // Show/hide Required properties based on visible/readonly.
        EchoColumnNodeData nd = (EchoColumnNodeData) nodeData;
        if (nd.isRequired()) {
            set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(true);
            set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(true);
        } else {
            set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(false);
            set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(false);

            if ((nd.isReadOnly()) || (!nd.isVisible())) {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            } else {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
            }

        }


        sheet.put(set);
        //Ticket #502
        sheet.put(set2);
        sheet.put(set3);
        storeSheet(sheet);
        return sheet;
    }


    @Override
    public String getNodeDisplayName() {
        return "EchoColumn " + nodeData.getIndex();
    }

}
