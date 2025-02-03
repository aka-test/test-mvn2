/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.echoman.designer.components.echointerfaces.ITableColumnListNodeData;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * @author david.morin
 */
public class ColumnsPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {
    private HashMap<String, String> columns = null;
    private ArrayList<String> columnList = new ArrayList<String>();
    private ArrayList<String> columnKeys = new ArrayList<String>();
    private String propertyName = "";
    private ITableColumnListNodeData nodeData;

    public ColumnsPropertyEditor(ITableColumnListNodeData nodeData) {
        super(nodeData);
        this.nodeData = nodeData;
    }

    @Override
    public void setValue(Object value) {
        // Use the key (column name only) to get the value (column name, datatype, pKey) from columnList.
        if ((columnKeys.size() > 0) && (!columnList.get(columnKeys.indexOf(value)).equals(""))) {
            String columnAndInfo = columnList.get(columnKeys.indexOf(value));
            super.setValue(columnAndInfo);
        // Else, just return the column name.
        } else
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
        // Ticket 443
        if (propertyName.equals("Master Link Field")) {
             columns = nodeData.getMasterColumnList(propertyName);
        } else {
            columns = nodeData.getColumnList(propertyName);
        }
        columnKeys.clear();
        columnList.clear();
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            columnKeys.add(key);
            columnList.add(value);
        }
        return columnKeys.toArray(new String[columnKeys.size()]);
    }

    @Override
    public void attachEnv(PropertyEnv pe) {
        propertyName = pe.getFeatureDescriptor().getName();
    }
}
