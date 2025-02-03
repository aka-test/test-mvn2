/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.databasemanager.DesignerPanel;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.NbPreferences;

/**
 *
 * @author david.morin
 */
public class ValidationsTableListModel extends DefaultTableModel {

    private TableRowSorter storedRowSorter;
    private TableColumnModel storedColumnModel;
    private ArrayList<RowSorter.SortKey> storedSortKeys;
    private final HashMap<Integer, HashMap<Integer, String>> storedFilter;
    private final HashMap<String, TableModel> storedListModels;
    private final JTable validationsList;
    private final JTable validationsListItems;
    private final JTable validationsListCriteria;
    private final JTable validationsListDisplayColumns;
    private final boolean calledFromPropertyEditor;
    private final HashMap<String, String> validationData;
    private boolean editing = false;
    private boolean loading = false;

    /**
     * 
     */
    private static final String SQLSTATEMENT = "SELECT \n"
            + "\t validation_lists.validation_list_id \"UID\", validation_lists.list_name \"Name\", validation_lists.description \"Description\", validation_lists.stored_column \"Stored\", validation_lists.sql_statement \"SQL\", validation_lists.DesignTimeColumns \"DesignColumns\" \n"
            + "FROM \n"
            + "\t dbo.validation_lists validation_lists \n"
            + "WHERE \n"
            + "\t validation_lists.validation_type = 'table' and ((validation_lists.end_date is null) or (validation_lists.end_date > getDate()))"
            + " ORDER BY validation_lists.list_name";
    private static final String ID = "id";
    
    /**
     * 
     * @return
     */
    public HashMap<String, TableModel> getStoredListModels() {
        return storedListModels;
    }

    /**
     * 
     * @param uid
     * @param model
     */
    public void addStoredListModel(String uid, TableModel model) {
        storedListModels.put(uid, model);
    }

    /**
     * 
     * @param uid
     */
    public void removeStoredListModel(String uid) {
        storedListModels.remove(uid);
    }

    /**
     * 
     * @return
     */
    public List<? extends RowSorter.SortKey> getStoredSortKeys() {
        return storedSortKeys;
    }

    /**
     * 
     * @param row
     * @param col
     * @return
     */
    public String getStoredFilter(Integer row, Integer col) {
        HashMap<Integer, String> criteriaRow = storedFilter.get(row);
        return criteriaRow.get(col);
    }

    /**
     * 
     * @param row
     * @param col
     * @param value
     */
    public void setStoredFilter(Integer row, Integer col, String value) {
        HashMap<Integer, String> criteriaRow = storedFilter.get(row);
        criteriaRow.put(col, value);
    }

    /**
     * 
     * @param sortKeys
     */
    public void setStoredSortKeys(List<? extends RowSorter.SortKey> sortKeys) {
        this.storedSortKeys = null;
        this.storedSortKeys = new ArrayList<>();
        Iterator it = sortKeys.listIterator();
        while (it.hasNext()) {
            this.storedSortKeys.add((SortKey) it.next());
        }
    }

    /**
     * 
     * @return
     */
    public TableRowSorter getStoredRowSorter() {
        return storedRowSorter;
    }

    /**
     * 
     * @return
     */
    public TableColumnModel getStoredColumnModel() {
        return storedColumnModel;
    }

    /**
     * 
     * @param storedRowSorter
     */
    public void setStoredRowSorter(TableRowSorter storedRowSorter) {
        this.storedRowSorter = storedRowSorter;
    }

    /**
     * 
     * @param storedColumnModel
     */
    public void setStoredColumnModel(TableColumnModel storedColumnModel) {
        this.storedColumnModel = storedColumnModel;
    }

    /**
     * 
     * @param columnName
     */
    @Override
    public void addColumn(Object columnName) {
        for (int i = 0; i < 3; i++) {
            HashMap<Integer, String> criteria = storedFilter.get(i);
            criteria.put(criteria.size(), (String) columnName);
        }
        super.addColumn(columnName);
    }

    // This just creates a new validation record with the name and type
    protected boolean createNewDataListItem() {
        boolean sqlError = false;
        UUID uid = null;
        editing = true;
        
        try {
            addRow(new String[]{SQLBuilder.EMPTY_STRING
                    ,"Name"
                    ,SQLBuilder.EMPTY_STRING
                    ,SQLBuilder.EMPTY_STRING
                    ,SQLBuilder.EMPTY_STRING});
            validationsList.requestFocusInWindow();
            validationsList.setRowSelectionInterval(validationsList.getRowCount() - 1, validationsList.getRowCount() - 1);
            validationsList.setColumnSelectionInterval(0, 0);
	
            validationsList.scrollRectToVisible(validationsList.getCellRect(validationsList.getSelectedRow(),validationsList.getSelectedColumn(), true));

            if (validationsList.editCellAt(validationsList.getSelectedRow(), validationsList.getSelectedColumn()))
            {
                Component editor = validationsList.getEditorComponent();
                editor.requestFocusInWindow();
                ((JTextComponent)editor).selectAll();
            }
            
            String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME, SQLBuilder.EMPTY_STRING);
            DatabaseConnection con = DBConnections.getConnection();

            String name = (String) validationsList.getValueAt(validationsList.getSelectedRow(), 0);
            if (!((name == null) || (name.equals(SQLBuilder.EMPTY_STRING)))) {
                if (!(con == null)) {
                    Connection conn = con.getJDBCConnection();
                    if (!(conn == null)) {
                        try {
                            Statement stmt = conn.createStatement();
                            uid = UUID.randomUUID();
                            StringBuilder sql = new StringBuilder(SQLBuilder.VALIDATION_NAME_INSERT_SQL);
                            sql.append(SQLBuilder.INSERT_VALUES_OPEN)
                                    .append(uid)
                                    .append(SQLBuilder.SINGLE_QUOTE_COMMA_SINGLE_QUOTE)
                                    .append(name)
                                    .append(SQLBuilder.SINGLE_QUOTE_COMMA_SINGLE_QUOTE)
                                    .append(SQLBuilder.TABLE_LOWER)
                                    .append(SQLBuilder.SINGLE_QUOTE_COMMA)
                                    .append(SQLBuilder.GETDATE)
                                    .append(SQLBuilder.COMMA)
                                    .append(SQLBuilder.GETDATE)
                                    .append(SQLBuilder.COMMA_SINGLE_QUOTE)
                                    .append(dbuser)
                                    .append(SQLBuilder.SINGLE_QUOTE_COMMA)
                                    .append(SQLBuilder.GETDATE)
                                    .append(SQLBuilder.COMMA_SINGLE_QUOTE)
                                    .append(dbuser)
                                    .append(SQLBuilder.INSERT_VALUES_CLOSE);
                            try {
                                stmt.executeUpdate(sql.toString());
                            } finally {
                                stmt.close();
                            }
                        } catch (SQLException ex) {
                            sqlError = true;
                            JOptionPane.showMessageDialog(null, ex);
                        }
                    }
                }
                if (!sqlError && (uid != null)) {
                    setValueAt(uid.toString().toUpperCase(), validationsList.getSelectedRow(), 0);
                    QueryTableModel newModel = new QueryTableModel(validationData);
                    validationsListItems.setModel(newModel);
                    if (!(validationsListItems.getRowSorter() == null)) {
                        validationsListItems.setRowSorter(null);
                    }
                    storedListModels.put(uid.toString().toUpperCase(), validationsListItems.getModel());
                }
            }
        } finally {
            editing = false;
        }
        return !sqlError;
    }

    /**
     * 
     * @param sqlTextIn
     * @param storedCol
     * @return
     */
    protected boolean editCurrentRecord(String sqlTextIn, String storedCol) {
        String sqlText = sqlTextIn;
        boolean sqlError = false;
        editing = true;
        try {
            String stored = SQLBuilder.EMPTY_STRING;
            
            if (!SQLBuilder.EMPTY_STRING.equals(storedCol)) {
                stored = storedCol;
            }

            String uid = (String) getValueAt(validationsList.getSelectedRow(), 0);
            String name = (String) validationsList.getValueAt(validationsList.getSelectedRow(), 0);
            String desc = (String) validationsList.getValueAt(validationsList.getSelectedRow(), 1);

            QueryTableModel storedModel = (QueryTableModel) storedListModels.get(uid);
            String storedSql = SQLBuilder.EMPTY_STRING;
            
            if (!calledFromPropertyEditor) {
                String designTimeColumns = SQLBuilder.EMPTY_STRING;
                if (storedModel != null) {
                    storedSql = storedModel.buildRuntimeTimeSQL(sqlTextIn, false);
                    designTimeColumns = storedModel.getDesignTimeColumnsAsString();
                    // The sql we check is different than the one we store because of quotes.
                    sqlError = !storedModel.checkQuery(storedSql, stored);
                }

                if (!sqlError) {
                    if (!SQLBuilder.EMPTY_STRING.equals(SQLBuilder.updateValidationRecord(uid, name, desc, designTimeColumns, sqlText, stored))) {
                        sqlError = true;
                    }
                }
            }
            
            if (!sqlError && (storedModel != null)) {
                validationsList.setValueAt(storedCol, validationsList.getSelectedRow(), 2);
                
                if (calledFromPropertyEditor) {
                    storedSql = storedModel.buildRuntimeTimeSQL(sqlTextIn, false);
                }

                // Use the original sql text not the double quoted one.
                if (((QueryTableModel) validationsListItems.getModel()).setQuery(storedSql)) {
                    SQLBuilder.buildCriteriaModel(validationsListItems, validationsListCriteria);
                    SQLBuilder.buildDisplayColumnsModel(validationsListItems, validationsListDisplayColumns);

                    //Populate checkboxes for display here
                    for (int j = 0; j < validationsListDisplayColumns.getColumnCount(); j++) {
                        ValidationsTableDisplayColumnsModel dispMod = (ValidationsTableDisplayColumnsModel) validationsListDisplayColumns.getModel();
                        String colName = dispMod.getPhysicalColumn(j);
                        Boolean value = ((QueryTableModel) validationsListItems.getModel()).getIsVisibleColumn(colName);
                        validationsListDisplayColumns.getModel().setValueAt(value, 0, j);
                    }
                    
                } else {
                    sqlError = true;
                }
            }
        } finally {
            editing = false;
        }

        return !sqlError;
    }

    // This updates just the name and description of the validation
    protected boolean editCurrentRecord() {
        boolean sqlError = false;
        editing = true;

        try {
            String uid = (String) getValueAt(validationsList.getSelectedRow(), 0);
            String name = (String) validationsList.getValueAt(validationsList.getSelectedRow(), 0);
            String desc = (String) validationsList.getValueAt(validationsList.getSelectedRow(), 1);

            if (!calledFromPropertyEditor) {
                if (!SQLBuilder.EMPTY_STRING.equals(SQLBuilder.updateValidationRecord(uid, name, desc, "", "", ""))) {
                    sqlError = true;
                }
            }
            
            if (!sqlError) {
                QueryTableModel storedModel = null;
                if (storedListModels.containsKey(uid)) {
                    storedModel = (QueryTableModel) storedListModels.get(uid);
                }
                validationsListItems.setModel(storedModel);
                storedListModels.put(uid, validationsListItems.getModel());
            }
        } finally {
            editing = false;
        }
        return !sqlError;
    }

    /**
     * 
     * @param row
     * @param rowData
     */
    @Override
    public void insertRow(int row, Object[] rowData) {
        super.insertRow(row, rowData);
    }

    /**
     * 
     * @param e
     */
    @Override
    public void newRowsAdded(TableModelEvent e) {
        super.newRowsAdded(e);
    }

    /**
     * 
     * @param uid
     * @return
     */
    private boolean deleteCurrentRow(String uid) {
        boolean sqlError = false;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    Statement stmt = conn.createStatement();
                    StringBuilder sql = new StringBuilder(SQLBuilder.VALIDATION_DELETE_SQL);
                    sql.append(SQLBuilder.UPDATE_WHERE)
                            .append(uid)
                            .append(SQLBuilder.SINGLE_QUOTE);
                    stmt.executeUpdate(sql.toString());
                    stmt.close();
                } catch (SQLException ex) {
                    sqlError = true;
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
        return !sqlError;
    }

    /**
     * 
     * @param row
     */
    @Override
    public void removeRow(int row) {
        String uid = (String) getValueAt(row, 0);
        if (deleteCurrentRow(uid)) {
            removeStoredListModel(uid);
            super.removeRow(row);
        }
    }

    /**
     * 
     * @return
     */
    private boolean getExistingRows() {
        boolean sqlError = false;
        String runtimeTimeSql;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                loading = true;
                try {
                    try {
                        Statement stmt = conn.createStatement();
                        String sql = ValidationsTableListModel.SQLSTATEMENT;
                        ResultSet rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            String uid = rs.getString(1);
                            addRow(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
                            QueryTableModel storedModel = new QueryTableModel(validationData);
                            //If criteria property exists, then use that here....
                            runtimeTimeSql = SQLBuilder.EMPTY_STRING;
                            if (!((rs.getString(5) == null) || (SQLBuilder.EMPTY_STRING.equals(rs.getString(5))))) {
                                // If we have a sql statement then build the runtime sql and set the design columns
                                // If the design columns have never been saved (old record) they will be set when
                                // the runtime sql is built the first time.
                                storedModel.setDesignTimeColumnsFromString(rs.getString(6));
                                if (calledFromPropertyEditor && uid.equals(validationData.get(ID))) {
                                    // Only do this for the currently selected validation
                                    runtimeTimeSql = storedModel.buildRuntimeTimeSQL(rs.getString(5), true);
                                } else {
                                    runtimeTimeSql = storedModel.buildRuntimeTimeSQL(rs.getString(5), false);
                                }
                            }
                            if (!SQLBuilder.EMPTY_STRING.equals(runtimeTimeSql)) {
                                storedModel.setRuntimeSQL(runtimeTimeSql);
                            } else {
                                storedModel.setRuntimeSQL(rs.getString(5));
                            }
                            storedListModels.put(rs.getString(1), storedModel);
                        }
                        rs.close();
                        stmt.close();
                    } catch (SQLException ex) {
                        sqlError = true;
                        JOptionPane.showMessageDialog(null, ex);
                    }
                } finally {
                    loading = false;
                }
            }
        }
        return !sqlError;
    }

    /**
     * 
     * @param validationsList
     * @param validationsListItems
     * @param validationsListCriteria
     * @param validationsListDisplayColumns
     * @param columnNames
     * @param rowCount
     * @param calledFromPropertyEditor
     * @param validationData
     */
    public ValidationsTableListModel(JTable validationsList, JTable validationsListItems, JTable validationsListCriteria, JTable validationsListDisplayColumns, Object[] columnNames, int rowCount, boolean calledFromPropertyEditor, HashMap<String, String> validationData) {
        super(columnNames, rowCount);
        this.calledFromPropertyEditor = calledFromPropertyEditor;
        this.validationData = validationData;
        this.validationsList = validationsList;
        this.validationsListItems = validationsListItems;
        this.validationsListCriteria = validationsListCriteria;
        this.validationsListDisplayColumns = validationsListDisplayColumns;
        storedFilter = new HashMap<>();
        storedListModels = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HashMap<Integer, String> criteria = new HashMap<>();
            for (int j = 0; j < columnNames.length; j++) {
                criteria.put(j, SQLBuilder.EMPTY_STRING);
            }
            storedFilter.put(i, criteria);
        }
        getExistingRows();
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isLoading() {
        return loading;
    }
}
