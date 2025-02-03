/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;

/**
 * @author david.morin
 */
public class ButtonTypePropertyEditor extends PropertyEditorSupport {
    private final String[] buttonType = new String[]{"Button", "File Link", "Form Link", "URL Link"};
    
    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        return buttonType;
    }
}
