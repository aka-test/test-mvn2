/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * @author david.morin
 */
public class ValidationsPropertyEditor extends PropertyEditorSupport {

    /**
     * 
     * @param validationData
     * @return
     */
    private HashMap<String, String> getPropertyDataItems(String validationData) {
        HashMap<String, String> data = new HashMap();
        // Initialize to ""
        data.put("type","");
        data.put("id","");
        data.put("name","");
        data.put("storedColumnName","");
        data.put("criteria","");
        data.put("order","");
        StringTokenizer tokens = new StringTokenizer(validationData, "~");
        while (tokens.hasMoreTokens()) {
            String key = "";
            String value = "";
            String pair = tokens.nextToken();
            // Can't split on = here because = is in the criteria
            if (pair.contains("=")) {
                // No key
                if (!(pair.startsWith("=")))
                    key = pair.substring(0, pair.indexOf("="));
                // No value
                if (!(pair.endsWith("=")))
                    value = pair.substring(pair.indexOf("=")+1, pair.length());
                data.put(key, value);
            }
        }
        return data;
    }

    /**
     * 
     * @return
     */
    @Override
    public Component getCustomEditor() {
        // The value return by the dialog corresponds to the position in the options list 0 - ...
        Object[] options = {"Table Driven Validations", "User Entered Validations"};
        switch (JOptionPane.showOptionDialog(null, "Please select Table Driven (populated from database tables) \n or User Entered (user entered values)", "Select Validation Type", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, 0)) {
            case 0: {
                HashMap<String, String> validationData = getPropertyDataItems(getAsText());
                ValidationsTableListSetup dialog = new ValidationsTableListSetup(null, true, this, validationData);
                dialog.addWindowListener(
                    new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent evt) {
                        validDataListSetupWindowClosed(evt);
                        }
                    });
                return dialog;
            }
            case 1: default: {
                HashMap<String, String> validationData = getPropertyDataItems(getAsText());
                ValidationsUserEnteredSetup dialog = new ValidationsUserEnteredSetup(null, true, this, validationData);
                dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                dialog.addWindowListener(
                    new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent evt) {
                        validOptionListSetupWindowClosed(evt);
                        }
                    });
                return dialog;
            }
        }
    }

    /**
     * 
     * @param evt
     */
    public void validOptionListSetupWindowClosed(java.awt.event.WindowEvent evt) {
        HashMap<String, String> data;
        String id = "";
        String name = "";
        String storedColumnName = "";
        String criteria = "";
        String order = "";
        if (((ValidationsUserEnteredSetup)evt.getWindow()).getReturnStatus() == ValidationsUserEnteredSetup.RET_OK) {
            data = ((ValidationsUserEnteredSetup)evt.getWindow()).getValidationPropertyData();
            if (!(data.get("id")==null))
                id = data.get("id");
            if (!(data.get("name")==null))
                name = data.get("name");
            if (!(data.get("storedColumnName")==null))
                storedColumnName = data.get("storedColumnName");
            if (!(data.get("criteria")==null))
                criteria = data.get("criteria");
            if (!(data.get("order")==null))
                order = data.get("order");
            setAsText("type=user~" + "name=" + name + "~" + "id=" + id + "~" + "criteria=" + criteria + "~" + "order=" + order + "~" + "storedColumnName=" + storedColumnName);
            ((ValidationsUserEnteredSetup)evt.getWindow()).removeWindowListener(((ValidationsUserEnteredSetup)evt.getWindow()).getWindowListeners()[0]);
        }
    }

    /**
     * 
     * @param evt
     */
    public void validDataListSetupWindowClosed(java.awt.event.WindowEvent evt) {
        HashMap<String, String> data;
        String id = "";
        String name = "";
        String storedColumnName = "";
        String criteria = "";
        String order = "";
        if (((ValidationsTableListSetup)evt.getWindow()).getReturnStatus() == ValidationsTableListSetup.RET_OK) {
            data = ((ValidationsTableListSetup)evt.getWindow()).getValidationPropertyData();
            if (!(data.get("id")==null))
                id = data.get("id");
            if (!(data.get("name")==null))
                name = data.get("name");
            if (!(data.get("storedColumnName")==null))
                storedColumnName = data.get("storedColumnName");
            if (!(data.get("criteria")==null))
                criteria = data.get("criteria");
            if (!(data.get("order")==null))
                order = data.get("order");
            setAsText("type=table~" + "id=" + id + "~" + "name=" + name + "~" + "criteria=" + criteria + "~" + "order=" + order + "~" + "storedColumnName=" + storedColumnName);
            // If you don't remove this listener it will be called until the window is garbage collected.
            ((ValidationsTableListSetup)evt.getWindow()).removeWindowListener(((ValidationsTableListSetup)evt.getWindow()).getWindowListeners()[0]);
        }
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
        setValue(s);
    }
}
