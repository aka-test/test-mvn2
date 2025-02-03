/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;

/**
 * @author david.morin
 */
public class AlignmentPropertyEditor extends PropertyEditorSupport {
    private final String[] textAlignment = new String[]{"Left", "Right"};
    
    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        return textAlignment;
    }
}
