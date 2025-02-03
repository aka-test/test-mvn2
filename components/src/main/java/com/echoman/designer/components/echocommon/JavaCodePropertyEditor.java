/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.*;

/**
 * @author david.morin
 */
public class JavaCodePropertyEditor extends PropertyEditorSupport {

    /**
     * 
     * @return
     */
    @Override
    public Component getCustomEditor() {
        JavaCodeEditorForm dialog = new JavaCodeEditorForm(null, true, this);
        
        dialog.addWindowListener(
            new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent evt) {
                javaCodeEditorFormWindowClosed(evt);
                }
            });

        return dialog;
    }
    
    /**
     * 
     * @param evt
     */
    public void javaCodeEditorFormWindowClosed(java.awt.event.WindowEvent evt) {
        setAsText("");
        ((JavaCodeEditorForm)evt.getWindow()).removeWindowListener(((JavaCodeEditorForm)evt.getWindow()).getWindowListeners()[0]);
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
