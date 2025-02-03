/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author Hendra
 */
public class TableLinkEditor extends PropertyEditorSupport {
    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        //return new TableLinkForm(null, true, this);
        return new TableSetupForm(null, true, this);
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
