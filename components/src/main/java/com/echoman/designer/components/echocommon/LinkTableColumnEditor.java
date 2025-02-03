/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 */
public class LinkTableColumnEditor extends PropertyEditorSupport implements ExPropertyEditor {
    private String propertyName = "";

    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new TablesColumnsForm(null, true, this, false, true, false, propertyName);
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

    @Override
    public void attachEnv(PropertyEnv pe) {
        propertyName = pe.getFeatureDescriptor().getName();
    }
}
