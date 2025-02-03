/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import com.echoman.designer.databasemanager.DBConnections;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author david.morin
 */
public class ValidationsUserEnteredModel extends DefaultTableModel{
    /**
     * 
     */
    public static final String sqlStatement = "SELECT \n" +
                "\t validation_lists.validation_list_id \"UID\", validation_lists.list_name \"Name\", validation_lists.description \"Description\", validation_lists.sql_statement \"SQL\" \n" +
                "FROM \n" +
                "\t dbo.validation_lists validation_lists \n" +
                "WHERE \n" +
                "\t validation_lists.validation_type = 'user' and ((validation_lists.end_date is null) or (validation_lists.end_date > getDate()))"
                + " ORDER BY validation_lists.list_name";
    private static final String VALUE = "value";
    private static final String DELETE_SQL = "update validation_lists set end_date=getdate() where validation_list_id = '";
    private static final String DELETE_SQL_END = "' where validation_list_id = '";

    private TableRowSorter storedRowSorter;
    private TableColumnModel storedColumnModel;
    private ArrayList<RowSorter.SortKey> storedSortKeys;
    private final HashMap<String,TableModel> storedListModels;
    private final JTable validationsList;
    private final JTable validationsListItems;
    private final JTable validationsListCriteria;
    private final JTable validationsListDisplayColumns;
    private final boolean calledFromPropertyEditor;
    private final HashMap<String, String> validationData;
    private TableModelListener itemTableListener = null;
    private boolean editing = false;
    private boolean loading = false;

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
     * @param sortKeys
     */
    public void setStoredSortKeys(List<? extends RowSorter.SortKey> sortKeys) {
        this.storedSortKeys = null;
        this.storedSortKeys = new ArrayList<>();
        Iterator it = sortKeys.listIterator();
        while (it.hasNext())
            this.storedSortKeys.add((SortKey) it.next());
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
     */
    public class headerClicked implements MouseListener {
        /**
         * This happens after the sortChanged called.
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            //stopEditing(validationsListItems);
            if (validationsListItems.getRowSorter() == null) {
                RowSorter newRowSorter = ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).getNewRowSorter();
                List<? extends SortKey> storedSortKeys = ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).getStoredSortKeys();
                validationsListItems.setRowSorter(newRowSorter);
                ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).setStoredSortKeys(storedSortKeys);
            } else {
                ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).setStoredSortKeys(validationsListItems.getRowSorter().getSortKeys());
            }
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

//    public void setItemTableListener(TableModelListener itemTableListener) {
//        this.itemTableListener = itemTableListener;
//    }

    /**
     * 
     * @param sql
     * @param uid
     */
    private void addNewItemsRow(String sql, String uid) {
        if (!(validationsListItems.getRowSorter() == null)) {
            validationsListItems.setRowSorter(null);
        }
        ValidationsUserEnteredItemsModel newModel = new ValidationsUserEnteredItemsModel(validationsList, validationsListItems
                , validationsListCriteria
                , validationsListDisplayColumns
                , new String[]{ValidationsUserEnteredItemsModel.UID
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.VALUE.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.TRANSLATION.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.FILTER.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X1.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X2.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X3.caption 
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X4.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X5.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.X6.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.START_DATE.caption
                        ,ValidationsUserEnteredItemsModel.COLUMNS_ENUM.END_DATE.caption
                        },0,sql,uid,calledFromPropertyEditor,validationData);
        newModel.addTableModelListener(itemTableListener);
        validationsListItems.setModel(newModel);
        validationsListItems.getTableHeader().addMouseListener(new headerClicked());
        
        // Clear the criteria table model.
        for (int i=0; i<3; i++) {
            for (int j=0; j<validationsListCriteria.getColumnCount(); j++)
                validationsListCriteria.getModel().setValueAt(SQLBuilder.EMPTY_STRING, i,j);
        }

        // Clear the checkboxes until record is created.
        for (int j = 0; j < validationsListDisplayColumns.getColumnCount(); j++) {
            validationsListDisplayColumns.getModel().setValueAt(false, 0, j);
        }
        
        validationsListItems.removeColumn(validationsListItems.getColumnModel().getColumn(0));
    }

    /**
     * 
     * @param setup
     * @return
     */
    protected boolean createNewDataListItem(ValidationsUserEnteredSetup setup) {
        boolean sqlError = false;
        UUID uid = UUID.randomUUID();
        String stored = VALUE;
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

            // Create the user entered items model.
            addNewItemsRow(SQLBuilder.EMPTY_STRING,uid.toString().toUpperCase());

            String name = (String)validationsList.getValueAt(validationsList.getSelectedRow(), 0);
            String desc = (String)validationsList.getValueAt(validationsList.getSelectedRow(), 1);
            String sqlst = ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).getRunTimeSqlStatement(uid.toString());

            if (!SQLBuilder.EMPTY_STRING.equals(SQLBuilder.createValidationRecord(uid.toString(), name, desc, SQLBuilder.USER_LOWER, sqlst, "", stored))) {
                sqlError = true;
            }

            if (!sqlError) {
                setValueAt(uid.toString().toUpperCase(), validationsList.getSelectedRow(), 0);
                storedListModels.put(uid.toString().toUpperCase(), validationsListItems.getModel());
            }
        } finally {
            editing = false;
        }
        
        return !sqlError;
    }

    /**
     * 
     * @return
     */
    protected boolean editCurrentRecord() {
        boolean sqlError = false;
        String stored = VALUE;

        String uid = (String)getValueAt(validationsList.getSelectedRow(), 0);
        String name = (String)validationsList.getValueAt(validationsList.getSelectedRow(), 0);
        String desc = (String)validationsList.getValueAt(validationsList.getSelectedRow(), 1);

        if(!SQLBuilder.EMPTY_STRING.equals(SQLBuilder.updateValidationRecord(uid, name, desc, "", "", stored))) {
            sqlError = true;
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
        java.util.Date endDate = new java.util.Date();

        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    // We need to end date the validation items for the list just deleted here...
                    Statement stmt = conn.createStatement();
                    StringBuilder sql = new StringBuilder(ValidationsUserEnteredItemsModel.DELETE_SQL_START);
                    sql.append(ValidationsUserEnteredItemsModel.getDateStr(endDate));
                    sql.append(DELETE_SQL_END);
                    sql.append(uid)
                            .append(SQLBuilder.SINGLE_QUOTE);
                    stmt.executeUpdate(sql.toString());

                    sql.setLength(0);
                    sql.append(DELETE_SQL)
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
        String uid = (String)getValueAt(row, 0);
        if (!(SQLBuilder.EMPTY_STRING.equals(uid))) {
            if (deleteCurrentRow(uid)) {
                removeStoredListModel(uid);
                super.removeRow(row);
            }
        }
    }

    /**
     * 
     * @return
     */
    private boolean getExistingRows() {
        boolean sqlError = false;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                loading = true;
                try {
                    try {
                        Statement stmt = conn.createStatement();
                        String sql = ValidationsUserEnteredModel.sqlStatement;
                        ResultSet rs = stmt.executeQuery(sql);
                        while (rs.next()) {
                            addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
                            addNewItemsRow(rs.getString(4),rs.getString(1));
                            storedListModels.put(rs.getString(1), validationsListItems.getModel());
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
     * @param itemTableListener
     */
    public ValidationsUserEnteredModel(JTable validationsList, JTable validationsListItems, 
            JTable validationsListCriteria, JTable validationsListDisplayColumns, Object[] columnNames, int rowCount,
            boolean calledFromPropertyEditor,
            HashMap<String, String> validationData,
            TableModelListener itemTableListener) {
        super(columnNames, rowCount);
        this.validationData = validationData;
        this.validationsList = validationsList;
        this.validationsListItems = validationsListItems;
        this.validationsListCriteria = validationsListCriteria;
        this.validationsListDisplayColumns = validationsListDisplayColumns;
        this.calledFromPropertyEditor = calledFromPropertyEditor;
        this.itemTableListener = itemTableListener;
        storedListModels = new HashMap();
        getExistingRows();
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isLoading() {
        return loading;
    }
}
