/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * @author david.morin
 */
public class EchoDefaultStylePropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {
    private String propertyName = "";

    public static final String DEFAULT_CAPTION = "Default Caption";
    public static String DEFAULT_HEADER = "Default Header";
    public static String LARGE_HEADER = "Large Header";
    public static String DEFAULT_BOX = "Default Box";
    public static String SOLID_UNDERLINE = "Solid Underline";
    public static String DOTTED_UNDERLINE = "Dotted Underline";
    public static String DASHED_UNDERLINE = "Dashed Underline";
    // Not implemented yet - can't use LineBorder and not have all sides
    // Can use MatteBorder with Icon, but changing size or grid spacing
    // affects how Icon is displayed.  Defer until later.
    public static String DOUBLE_UNDERLINE = "Double Underline";
    public static String SINGLE_DOUBLE_UNDERLINE = "Single/Double Underline";

    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        if (propertyName.equals("Echo Default Style")) {
            return new String[]{DEFAULT_CAPTION, DEFAULT_HEADER, LARGE_HEADER};
        } else if (propertyName.equals("Echo Default Border")) {
            return new String[]{DEFAULT_BOX, SOLID_UNDERLINE, DOTTED_UNDERLINE, DASHED_UNDERLINE};
        } else {
            return new String[]{""};
        }
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        propertyName = pe.getFeatureDescriptor().getName();
    }
}
