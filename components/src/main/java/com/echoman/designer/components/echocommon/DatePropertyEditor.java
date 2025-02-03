/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import org.jdesktop.swingx.JXDatePicker;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;

/**
 *
 * @author Dave Athlon
 */
public class DatePropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {
    private InplaceEditor ed = null;
    
    /**
     * 
     */
    private static class Inplace implements InplaceEditor {
        private final JXDatePicker picker = new JXDatePicker();
        private PropertyEditor editor = null;
        private PropertyModel model = null;
        
        /**
         * 
         * @param propertyEditor
         * @param env
         */
        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            reset();
        }

        /**
         * 
         * @return
         */
        @Override
        public JComponent getComponent() {
            return picker;
        }

        /**
         * 
         */
        @Override
        public void clear() {
            editor = null;
            model = null;
        }

        /**
         * 
         * @return
         */
        @Override
        public Object getValue() {
            return picker.getDate();
        }

        /**
         * 
         * @param obj
         */
        @Override
        public void setValue(Object obj) {
            picker.setDate((Date)obj);
        }

        /**
         * 
         * @return
         */
        @Override
        public boolean supportsTextEntry() {
            return true;
        }

        /**
         * 
         */
        @Override
        public void reset() {
            Date d = (Date) editor.getValue();
            if (d != null) {
                picker.setDate(d);
            }
        }

        /**
         * 
         * @return
         */
        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        /**
         * 
         * @return
         */
        @Override
        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        /**
         * 
         * @return
         */
        @Override
        public PropertyModel getPropertyModel() {
            return model;
        }

        /**
         * 
         * @param model
         */
        @Override
        public void setPropertyModel(PropertyModel model) {
            this.model = model;
        }

        /**
         * 
         * @param arg0
         */
        @Override
        public void addActionListener(ActionListener arg0) {
        }

        /**
         * 
         * @param arg0
         */
        @Override
        public void removeActionListener(ActionListener arg0) {
        }

        /**
         * 
         * @param comp
         * @return
         */
        @Override
        public boolean isKnownComponent(Component comp) {
            return comp == picker || picker.isAncestorOf(comp);
        }
        
    }
    
    /**
     * 
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new JLabel("Custom editor...");
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
        Date d = (Date) getValue();
        if (d == null) {
            return "No Date Set";
        }
        return new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(d);
    }
    
    /**
     * 
     * @param s
     */
    @Override
    public void setAsText(String s) {
        try {
            setValue(new SimpleDateFormat("MM/dd/yy HH:mm:ss").parse(s));
        } catch (ParseException pe) {
            IllegalArgumentException iae = new IllegalArgumentException("Could not parse date");
            throw iae;
        }
    }

    /**
     * 
     * @param env
     */
    @Override
    public void attachEnv(PropertyEnv env) {
        env.registerInplaceEditorFactory(this);
    }

    /**
     * 
     * @return
     */
    @Override
    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new Inplace();
        }
        return ed;
    }
}
