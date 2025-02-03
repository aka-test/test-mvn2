/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;
import com.echoman.designer.components.echointerfaces.ITableColumnListNodeData;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * @author david.morin
 */
public class TablesPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {
    private String[] tables = null;
    private String propertyName = "";
    private ITableColumnListNodeData nodeData;

    public TablesPropertyEditor(ITableColumnListNodeData nodeData) {
        super(nodeData);
        this.nodeData = nodeData;
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }
    
    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        /**
         * The nodeData object for the current property is responsible
         * for returning the list so that it only gets created once.
         */
        tables = nodeData.getTableList(propertyName);
        return tables;
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        propertyName = pe.getFeatureDescriptor().getName();
    }
}
