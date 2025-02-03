/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

/**
 *
 * @author Dave Athlon
 */
public class InputMaskEditor extends PropertyEditorSupport {
    
    /**
     * 
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new InputMaskForm(null, true, this);
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
