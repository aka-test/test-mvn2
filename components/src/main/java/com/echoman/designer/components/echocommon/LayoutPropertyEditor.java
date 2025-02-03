/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.beans.*;

/**
 *
 */
public class LayoutPropertyEditor extends PropertyEditorSupport {
    private final String[] layout = new String[]{"Vertical", "Horizontal"};

    /**
     *
     * @return
     */
    @Override
    public String[] getTags() {
        return layout;
    }
}
