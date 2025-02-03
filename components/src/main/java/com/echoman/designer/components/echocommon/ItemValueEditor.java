/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 *
 */
public class ItemValueEditor extends PropertyEditorSupport {

    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new ItemValueForm(null, true, this) {

            @Override
            public boolean doAcceptValue(String oldValue, String newValue) {
                setAsText(newValue);
                return true;
            }

        } ;
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
