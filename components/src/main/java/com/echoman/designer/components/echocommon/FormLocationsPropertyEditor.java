/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * @author david.morin
 */
public class FormLocationsPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {
    private String propertyName = "";

    @Override
    public Component getCustomEditor() {
        return new FormLocationPropertyEditorForm(null, Boolean.TRUE, this);
    }

    @Override
    public boolean supportsCustomEditor() {
        return Boolean.TRUE;
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        propertyName = pe.getFeatureDescriptor().getName();
    }
}
