/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;

/**
 * @author david.morin
 */
public class SignatureTypePropertyEditor extends PropertyEditorSupport {
    private final String[] signatureType = new String[]{"Script", "Password"};
    
    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        return signatureType;
    }
}
