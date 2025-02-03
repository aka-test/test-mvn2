/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
public class ValidationsUserEnteredItemsModel extends DefaultTableModel{
    private TableRowSorter storedRowSorter;
    private TableColumnModel storedColumnModel;
    private ArrayList<RowSorter.SortKey> storedSortKeys;
    private final HashMap<Integer, HashMap<Integer, String>> storedFilter;
    private final Set<Integer> visibleColumns;
    private final JTable validationsListItems;
    private final JTable validationsListCriteria;
    private final JTable validationsListDisplayColumns;
    private final boolean calledFromPropertyEditor;
    private String designtimeSQL = SQLBuilder.EMPTY_STRING;
    private String runtimeSQL = SQLBuilder.EMPTY_STRING;
    private final HashMap<String, String> sqlParts = new HashMap();
    private boolean sqlPartsError = false;
    private String optionListId = SQLBuilder.EMPTY_STRING;
    private HashMap<String, String> validationData = null;
    private String orderbyProperty = SQLBuilder.EMPTY_STRING;
    private String criteriaProperty = SQLBuilder.EMPTY_STRING;
    private boolean creatingData = false;
    private final StringBuilder runtimeSQLSelect = new StringBuilder(RUNTIME_SQL_SELECT);
    
    private static final String RUNTIME_SQL_SELECT = "SELECT \n \t validation_list_values.value \"Value\", validation_list_values.translation \"Translation\" \n";
    private static final String VALUES_ORDER = " ORDER BY validation_list_values.value";
    private static final String NOT_VALID_DATE = " is not a valid date. Please enter a valid date (MM/dd/yyyy).";
    public static final String DELETE_SQL_START = "update validation_list_values set end_date = '";
    public static final String DELETE_SQL_END = "' where validation_list_values_id = '";

    private static final int CHAR_SPACE = ' ';
    private static final String VALUE_COLUMN = "validation_list_values.value";
    private static final String TRANSLATION_COLUMN = "validation_list_values.translation";
    private static final String FILTER_COLUMN = "validation_list_values.filter_column";
    private static final String X1_COLUMN = "validation_list_values.xwalk1_vc";
    private static final String X2_COLUMN = "validation_list_values.xwalk2_vc";
    private static final String X3_COLUMN = "validation_list_values.xwalk3_vc";
    private static final String X4_COLUMN = "validation_list_values.xwalk4_vc";
    private static final String X5_COLUMN = "validation_list_values.xwalk5_vc";
    private static final String X6_COLUMN = "validation_list_values.xwalk6_vc";
    private static final String START_DATE_COLUMN = "validation_list_values.start_date";
    private static final String END_DATE_COLUMN = "validation_list_values.end_date";
    private static final String DESC = "DESC";
    private static final String ID = "id";
    
    /**
     * This is the sql that is used during design which needs all the columns
     * to retrieve the records. It will have the join and where
     * added to it from the individual list records sql stored in the database.
     * 
     */
    private static final String DESIGNTIME_SQL_STATEMENT = "SELECT \n" +
                "\t validation_list_values.validation_list_values_id \"UID\" \n" +
                "\t, validation_list_values.value \"Value\", validation_list_values.translation \"Translation\", validation_list_values.filter_column \"Filter\" \n" +
                "\t, validation_list_values.xwalk1_vc \"X1\", validation_list_values.xwalk2_vc \"X2\" \n" +
                "\t, validation_list_values.xwalk3_vc \"X3\", validation_list_values.xwalk4_vc \"X4\" \n" +
                "\t, validation_list_values.xwalk5_vc \"X5\", validation_list_values.xwalk6_vc \"X6\" \n" +
                "\t, validation_list_values.start_date \"Start Date\", validation_list_values.end_date \"End Date\" \n" +
                "FROM \n" +
                "\t dbo.validation_lists validation_lists \n" +
                "JOIN \n" +
                "\t dbo.validation_list_values validation_list_values \n" +
                "ON \n" +
                "\t validation_lists.validation_list_id = validation_list_values.validation_list_id AND validation_lists.validation_list_id = \n";
    /**
     * We need to use this in order to get the criteria columns, which doesn't include the UID.
     * This is used by the model property of the dataOptionListItemsCriteria table.
     * 
     */
    private static final String CRITERIA_SQL_STATEMENT = "SELECT \n" +
                "\t, validation_list_values.value \"Value\", validation_list_values.translation \"Translation\", validation_list_values.filter_column \"Filter\" \n" +
                "\t, validation_list_values.xwalk1_vc \"X1\", validation_list_values.xwalk2_vc \"X2\" \n" +
                "\t, validation_list_values.xwalk3_vc \"X3\", validation_list_values.xwalk4_vc \"X4\" \n" +
                "\t, validation_list_values.xwalk5_vc \"X5\", validation_list_values.xwalk6_vc \"X6\" \n" +
                "\t, validation_list_values.start_date \"Start Date\", validation_list_values.end_date \"End Date\" \n" +
                "FROM \n" +
                "\t dbo.validation_lists validation_lists \n" +
                "JOIN \n" +
                "\t dbo.validation_list_values validation_list_values \n" +
                "ON \n" +
                "\t validation_lists.validation_list_id = validation_list_values.validation_list_id AND validation_lists.validation_list_id = \n";
    /**
     * This is used to store the runtime sql statement because we only want
     * the value and translation coumns displayed.
     * 
     */
    //Ticket #47
    private static final String RUNTIME_SQL_STATEMENT = 
                "FROM \n" +
                "\t dbo.validation_lists validation_lists \n" +
                "JOIN \n" +
                "\t dbo.validation_list_values validation_list_values \n" +
                "ON \n" +
                "\t validation_lists.validation_list_id = " +
                "validation_list_values.validation_list_id \n" +
                " AND validation_list_values.start_date < getDate() " +
                " AND ((validation_list_values.end_date > (getdate() - 1)) OR (validation_list_values.end_date is null)) " +
                " AND validation_lists.validation_list_id = \n";

    public static final String UID = "UID";
    
    public static enum COLUMNS_ENUM {
        VALUE(0, "Value", "validation_list_values.value \"Value\"")
        , TRANSLATION(1, "Translation", "validation_list_values.translation \"Translation\"")
        , FILTER(2, "Filter", "validation_list_values.filter_column \"Filter\"")
        , X1(3, "X1", "validation_list_values.xwalk1_vc \"X1\"")
        , X2(4, "X2", "validation_list_values.xwalk2_vc \"X2\"")
        , X3(5, "X3", "validation_list_values.xwalk3_vc \"X3\"")
        , X4(6, "X4", "validation_list_values.xwalk4_vc \"X4\"")
        , X5(7, "X5", "validation_list_values.xwalk5_vc \"X5\"")
        , X6(8, "X6", "validation_list_values.xwalk6_vc \"X6\"")
        , START_DATE(9, "Start Date", "validation_list_values.start_date \"Start Date\"")
        , END_DATE(10, "End Date", "validation_list_values.end_date \"End Date\""); 
        
        public final int id;
        public final String caption;
        public final String fieldName;
        
        COLUMNS_ENUM(int id, String caption, String fieldName) {
            this.id = id;
            this.caption = caption;
            this.fieldName = fieldName;
        }
        
        public static String getFieldById(int id) {
            for (COLUMNS_ENUM i : values()) {
                if (i.ordinal() == id) {
                    return i.fieldName;
                }
            }
            return SQLBuilder.EMPTY_STRING;
        }

        public static String getCaptionById(int id) {
            for (COLUMNS_ENUM i : values()) {
                if (i.ordinal() == id) {
                    return i.caption;
                }
            }
            return SQLBuilder.EMPTY_STRING;
        }
    }

    /**
     * 
     * @param validationsList
     * @param validationsListItems
     * @param validationsListCriteria
     * @param columnNames
     * @param rowCount
     * @param sql
     * @param uid
     * @param calledFromPropertyEditor
     * @param validationData
     */
    public ValidationsUserEnteredItemsModel(JTable validationsList, JTable validationsListItems, JTable validationsListCriteria, JTable validationsListDisplayColumns, Object[] columnNames, int rowCount, String sql, String uid, boolean calledFromPropertyEditor, HashMap<String, String> validationData) {
        super(columnNames, rowCount);
        this.validationsListItems = validationsListItems;
        this.validationsListCriteria = validationsListCriteria;
        this.validationsListDisplayColumns = validationsListDisplayColumns;
        this.calledFromPropertyEditor = calledFromPropertyEditor;
        this.validationData = validationData;

        validationsListItems.setRowSorter(getNewRowSorter());
        validationsListItems.setColumnModel(getColumnModel());
        storedFilter = new HashMap();
        visibleColumns = new LinkedHashSet<>();
        storedSortKeys = new ArrayList<>();
        optionListId = uid;
        
        runtimeSQL = runtimeSQLSelect + RUNTIME_SQL_STATEMENT + SQLBuilder.SINGLE_QUOTE + optionListId + SQLBuilder.SINGLE_QUOTE;
        designtimeSQL = DESIGNTIME_SQL_STATEMENT + SQLBuilder.SINGLE_QUOTE + optionListId + SQLBuilder.SINGLE_QUOTE;
        
        // Start with a new blank storedFilter list for the criteria.
        if (!(columnNames == null)) {
            for (int i=0; i<3; i++) {
                HashMap<Integer, String> criteria = new HashMap();
                for (int j=0; j<columnNames.length; j++) {
                    criteria.put(j, SQLBuilder.EMPTY_STRING);
                }
                storedFilter.put(i, criteria);
            }
        }

        // sql is passed in from the query for this validation list.
        if (!SQLBuilder.EMPTY_STRING.equals(sql)) {
            runtimeSQL = sql;
            // Only do this for the currently selected validation.
            if ((validationData != null) && uid.equals(validationData.get(ID))) {
                designtimeSQL = buildDesignTimeSQL(true);
            } else {
                designtimeSQL = buildDesignTimeSQL(false);
            }

            // Add the criteria from the where to the criteria table.
            SQLBuilder.addCriteriaFromWhere(sqlParts, (ValidationsCriteriaModel)validationsListCriteria.getModel());
            // Set the sort order column from the order by.
            setOrderFromSQL();
            // Update the stored filter from the criteria retrieved through the sql.
            for (int i=0; i<3; i++)
                for (int j=0; j<validationsListCriteria.getColumnCount(); j++) {
                   String value = (String)validationsListCriteria.getModel().getValueAt(i,j);
                    HashMap<Integer, String> criteriaRow = storedFilter.get(i);
                    criteriaRow.put(j, value);
                }
        }
   }

    public void addVisibleColumn(int col) {
        if (!visibleColumns.contains(col)) {
            visibleColumns.add(col);
        }
    }
    
    public String getRuntimeSQL() {
        return runtimeSQL;
    }

    public void setRuntimeSQL(String runtimeSQL) {
        this.runtimeSQL = runtimeSQL;
    }
    
    /**
     * 
     * @return
     */
    public String getCriteriaProperty() {
        return criteriaProperty;
    }

    /**
     * 
     * @return
     */
    public String getOrderbyProperty() {
        return orderbyProperty;
    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getSqlParts() {
        if (!sqlPartsError)
          return sqlParts;
        else
          return null;
    }

    public static String getCriteriaSqlStatement() {
        return CRITERIA_SQL_STATEMENT;
    }

    // This is only used for the initial new row and when display columns change.
    public String getRunTimeSqlStatement(String uid) {
        StringBuilder sqlStatement = new StringBuilder(SQLBuilder.EMPTY_STRING);
        sqlStatement.append(runtimeSQLSelect).append(RUNTIME_SQL_STATEMENT)
                .append(SQLBuilder.SINGLE_QUOTE).append(uid)
                .append(SQLBuilder.SINGLE_QUOTE).append(VALUES_ORDER);
        return  sqlStatement.toString();
    }

    public void updateRuntimeSQLSelect() {
        // Clear the current runtime select value and rebuild.
        runtimeSQLSelect.setLength(0);
        for (int col : visibleColumns) {
            if (SQLBuilder.EMPTY_STRING.equals(runtimeSQLSelect.toString())) {
                runtimeSQLSelect.append(SQLBuilder.SELECT_NAME)
                        .append(SQLBuilder.SPACE)
                        .append(COLUMNS_ENUM.getFieldById(col))
                        .append(SQLBuilder.SPACE);
            } else {
                runtimeSQLSelect.append(SQLBuilder.COMMA)
                        .append(SQLBuilder.SPACE)
                        .append(COLUMNS_ENUM.getFieldById(col))
                        .append(SQLBuilder.SPACE);
            }
        }
        // Update the sql statement that is stored.
        updateSqlStatement();
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
     */
    public void setStoredFilter() {
        int row = validationsListCriteria.getSelectedRow();
        int col = validationsListCriteria.getSelectedColumn();
        if ((row != -1) && (col != -1)) {
            String value = (String) validationsListCriteria.getModel().getValueAt(row,col);

            // Ticket 33273
            String error = EchoUtil.dangerousSqlCheck(value);
            if (!"".equals(error)) {
                JOptionPane.showMessageDialog(null, error);
                value = "";
                validationsListCriteria.getModel().setValueAt(value, row,col);
            }
        
            HashMap<Integer, String> criteriaRow = storedFilter.get(row);
            criteriaRow.put(col, value);
            updateSqlStatement();
        }
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
        updateSqlStatement();
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
        for (int i=0; i<3; i++) {
            HashMap<Integer, String> criteria = storedFilter.get(i);
            criteria.put(criteria.size(),(String)columnName);
        }
        super.addColumn(columnName);
    }

    /**
     * 
     * @return
     */
    public RowSorter getNewRowSorter() {
        TableRowSorter newRowSorter = new TableRowSorter(this);
        storedRowSorter = newRowSorter;
        return newRowSorter;
    }

    /**
     * 
     * @return
     */
    private DefaultTableColumnModel getColumnModel() {
        DefaultTableColumnModel newColumnModel = new DefaultTableColumnModel();
        TableColumn newColumn = new TableColumn(0,50);
        newColumn.setHeaderValue(UID);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(1,50);
        newColumn.setHeaderValue(COLUMNS_ENUM.VALUE.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(2,110);
        newColumn.setHeaderValue(COLUMNS_ENUM.TRANSLATION.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(3,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.FILTER.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(4,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X1.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(5,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X2.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(6,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X3.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(7,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X4.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(8,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X5.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(9,35);
        newColumn.setHeaderValue(COLUMNS_ENUM.X6.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(10,65);
        newColumn.setHeaderValue(COLUMNS_ENUM.START_DATE.caption);
        newColumnModel.addColumn(newColumn);
        newColumn = new TableColumn(11,65);
        newColumn.setHeaderValue(COLUMNS_ENUM.END_DATE.caption);
        newColumnModel.addColumn(newColumn);
        storedColumnModel = newColumnModel;
        return newColumnModel;
    }

    private boolean hasDateValue(String dateStr) {
        return dateStr != null && !SQLBuilder.EMPTY_STRING.equals(dateStr) && !SQLBuilder.NULL.equals(dateStr);
    }

    /**
     * 
     * @return
     */
    protected boolean createNewDataListItem() {
        boolean sqlError = false;
        UUID uid = UUID.randomUUID();

        // Disable sorting while adding - the sorting will be restored
        // the next time the validation is selected from the list.
        validationsListItems.setRowSorter(null);
        
        ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).addRow(
                new String[]{SQLBuilder.EMPTY_STRING, "Value", SQLBuilder.EMPTY_STRING, SQLBuilder.EMPTY_STRING,
                    SQLBuilder.EMPTY_STRING, SQLBuilder.EMPTY_STRING, SQLBuilder.EMPTY_STRING, SQLBuilder.EMPTY_STRING,
                    SQLBuilder.EMPTY_STRING, SQLBuilder.EMPTY_STRING, getDateStr(new java.util.Date()), SQLBuilder.EMPTY_STRING});

        int newRow = validationsListItems.getRowCount()-1;
        validationsListItems.requestFocusInWindow();
        validationsListItems.setRowSelectionInterval(newRow, newRow);
        validationsListItems.setColumnSelectionInterval(0, 0);
        validationsListItems.scrollRectToVisible(validationsListItems.getCellRect(newRow, newRow, true));

        if (validationsListItems.editCellAt(newRow, validationsListItems.getSelectedColumn())) {
            Component editor = validationsListItems.getEditorComponent();
            editor.requestFocusInWindow();
            ((JTextComponent)editor).selectAll();
        }

        // Use the model to get the uid because it doesn't exist in the table.
        // But, for the other values we can just use the table and selectedrow since
        // the sort does not affect the selected row, just the model index.
        String value = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 0);
        String trans = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 1);
        String filter = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 2);
        String xwalk1 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 3);
        String xwalk2 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 4);
        String xwalk3 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 5);
        String xwalk4 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 6);
        String xwalk5 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 7);
        String xwalk6 = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 8);
        //Ticket #268 - default startdate to today

        String start_date = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 9);
        String end_date = (String)validationsListItems.getValueAt(validationsListItems.getSelectedRow(), 10);
        java.util.Date startDate = null;
        java.util.Date endDate = null;

        String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME,  SQLBuilder.EMPTY_STRING);
        DatabaseConnection con = DBConnections.getConnection();

        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    StringBuilder sql = new StringBuilder("insert into validation_list_values (validation_list_values_id, " +
                            "validation_list_id, value, translation, filter_column, xwalk1_vc, xwalk2_vc, xwalk3_vc, xwalk4_vc, " +
                            "xwalk5_vc, xwalk6_vc, start_date, end_date, create_dt, createuser_c, touch_date, touch_user)");
                    sql.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
                    if (hasDateValue(start_date)) {
                        startDate = getDate(start_date);
                        if (startDate == null) {
                            validationsListItems.setValueAt(SQLBuilder.EMPTY_STRING, validationsListItems.getSelectedRow(), 9);
                            return false;
                        }
                        sql.append(", ?");
                    }  else {
                        //Ticket #47 - don't default to todays date if start date is blank
                        sql.append(", null");
                    }
                    if (hasDateValue(end_date)) {
                        endDate = getDate(end_date);
                        if (endDate == null) {
                            validationsListItems.setValueAt(SQLBuilder.EMPTY_STRING, validationsListItems.getSelectedRow(), 10);
                            return false;
                        }
                        sql.append(", ?");
                    } else {
                        sql.append(", null");
                    }
                    sql.append(", getdate(), ?, getdate(), ?)");

                    PreparedStatement stmt = conn.prepareStatement(sql.toString());
                    try{
                        int idx = 1;
                        stmt.setString(idx++, uid.toString());
                        stmt.setString(idx++, optionListId);
                        stmt.setString(idx++, value);
                        stmt.setString(idx++, trans);
                        stmt.setString(idx++, filter);
                        stmt.setString(idx++, xwalk1);
                        stmt.setString(idx++, xwalk2);
                        stmt.setString(idx++, xwalk3);
                        stmt.setString(idx++, xwalk4);
                        stmt.setString(idx++, xwalk5);
                        stmt.setString(idx++, xwalk6);
                        if (startDate != null) {
                            stmt.setDate(idx++, new Date(startDate.getTime()));
                        }
                        if (endDate != null) {
                            stmt.setDate(idx++, new Date(endDate.getTime()));
                        }
                        stmt.setString(idx++, dbuser);
                        stmt.setString(idx++, dbuser);
                        stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    sqlError = true;
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
        if (!sqlError) {
            creatingData = true;
            try {
                setValueAt(uid.toString().toUpperCase(), validationsListItems.getSelectedRow(), 0);
                setValueAt(getDateStr(new java.util.Date()), validationsListItems.getSelectedRow(), 10);

                // SET DEFAULT VISIBLE COLUMNS HERE
                validationsListDisplayColumns.setValueAt(true, 0, 0);
                visibleColumns.add(0);
                validationsListDisplayColumns.setValueAt(true, 0, 1);
                visibleColumns.add(1);
            } finally {
                creatingData = false;
            }
        }

        return !sqlError;
    }

    public static String getDateStr(java.util.Date date) {
        SimpleDateFormat f = new SimpleDateFormat(SQLBuilder.SIMPLE_DATE);
        try {
            return f.format(date);
        } catch (Exception e) {
            return SQLBuilder.EMPTY_STRING;
        }
    }

    public boolean isCreatingData() {
        return creatingData;
    }

    /**
     * 
     */
    private void setOrderFromSQL() {
        String sortColumn = sqlParts.get(SQLBuilder.ORDER_NAME);
        String sortDirection = "ASC";
        if (sortColumn != null) {
            final int separatorIndex = sortColumn.indexOf(CHAR_SPACE);
            if (separatorIndex > -1) {
                sortDirection = sortColumn.substring(separatorIndex + 1);
                sortColumn = sortColumn.substring(0, separatorIndex);
            }
            final List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            if (validationsListItems.getRowSorter() != null) {
                final SortOrder sortOrder = DESC.equals(sortDirection) ? SortOrder.DESCENDING : SortOrder.ASCENDING;
                sortKeys.add(new RowSorter.SortKey(getSortColumnIndex(sortColumn), sortOrder));
            }
            validationsListItems.getRowSorter().setSortKeys(sortKeys);
            storedSortKeys = new ArrayList(sortKeys);
        }
    }

    private int getSortColumnIndex(String sortColumnName) {
        switch (sortColumnName) {
            case TRANSLATION_COLUMN:
                return 2;
            case FILTER_COLUMN:
                return 3;
            case X1_COLUMN:
                return 4;
            case X2_COLUMN:
                return 5;
            case X3_COLUMN:
                return 6;
            case X4_COLUMN:
                return 7;
            case X5_COLUMN:
                return 8;
            case X6_COLUMN:
                return 9;
            case START_DATE_COLUMN:
                return 10;
            case END_DATE_COLUMN:
                return 11;
            default: return 1;
        }
    }

    /**
     * 
     * @return
     */
    protected boolean updateSqlStatement() {
        runtimeSQL = getRunTimeSqlStatement(optionListId);
        String sqlStmt = SQLBuilder.rebuildOrderBy(runtimeSQL, validationsListItems, validationsListCriteria);
        String additionalCriteria = SQLBuilder.EMPTY_STRING;
        sqlStmt = SQLBuilder.rebuildWhereClause(sqlStmt, (ValidationsCriteriaModel)validationsListCriteria.getModel(), additionalCriteria);
        if ((!optionListId.equals(SQLBuilder.EMPTY_STRING)) && (saveSqlStatement(sqlStmt))) {
            getExistingRows();
            return true;
        }
        return false;
    }

    /**
     * 
     * @param sqlStmt
     * @return
     */
    private boolean saveSqlStatement(String sqlStmt) {
        boolean sqlError = false;
        // Don't save it to the database, properties set for textfield override at runtime.
        if (!calledFromPropertyEditor) {
            String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME, SQLBuilder.EMPTY_STRING);
            DatabaseConnection con = DBConnections.getConnection();

            if (!(con == null))
            {
                Connection conn = con.getJDBCConnection();
                if (!(conn == null)) {
                    try {
                        StringBuilder sql = new StringBuilder("update validation_lists set sql_statement = ?, touch_date = getdate(), " +
                                "touch_user = ? where validation_list_id = ? ");
                        PreparedStatement stmt = conn.prepareStatement(sql.toString());
                        stmt.setString(1, sqlStmt);
                        stmt.setString(2, dbuser);
                        stmt.setString(3, optionListId);
                        stmt.executeUpdate();
                        stmt.close();
                    } catch (SQLException ex) {
                        sqlError = true;
                        JOptionPane.showMessageDialog(null, ex);
                    }
                }
            }
        }
        if (!sqlError) {
            runtimeSQL = sqlStmt;
            // Rebuild the designtimeSQL.
            designtimeSQL = buildDesignTimeSQL(false);
            // If the user changes criteria or the sort we need to pass the
            // data back to the property.
            criteriaProperty = sqlParts.get(SQLBuilder.WHERE_NAME);
            orderbyProperty = sqlParts.get(SQLBuilder.ORDER_NAME);
        }
        return !sqlError;
    }

    /**
     * 
     * @return
     */
    private String buildDesignTimeSQL(Boolean useValidationData) {
        StringBuilder where = new StringBuilder(SQLBuilder.EMPTY_STRING);
        StringBuilder order = new StringBuilder(SQLBuilder.EMPTY_STRING);
        
        // Here we must use the order and criteria properties, not the sql statement
        // Break the actual query stored for this validation into it's parts
        // so we can use the join and where criteria, but with all of the columns
        // selected, not just the 3 for the runtime popup list.
        sqlPartsError = SQLBuilder.breakSqlIntoParts(runtimeSQL, sqlParts);

        if (sqlPartsError) {
            return SQLBuilder.EMPTY_STRING;
        }
        
        String colStr = sqlParts.get(SQLBuilder.SELECT_NAME);
        // Here we need to set the visibleColumns for the query
        // We always need to do this from select passed in.
        // We need to do it everytime to align these with the 
        // design time columns as far as what order they appear in.
        String[] colStrArray = colStr.split(SQLBuilder.COMMA);
        ArrayList<String> tempVisibleCols = new ArrayList<>();
        for (int i=0; i<colStrArray.length; i++) {
            tempVisibleCols.add(colStrArray[i].trim());
        }
        visibleColumns.clear();
        if (visibleColumns != null) {
            for (int i=0; i < COLUMNS_ENUM.values().length; i++) {
                if (tempVisibleCols.contains(COLUMNS_ENUM.getFieldById(i))) {
                    visibleColumns.add(i);
                }
            }
        }

        if (useValidationData && !validationData.get(SQLBuilder.CRITERIA_NAME).equals(SQLBuilder.EMPTY_STRING)) {
            where.append(SQLBuilder.WHERE_PREFIX).append(validationData.get(SQLBuilder.CRITERIA_NAME));
            criteriaProperty = validationData.get(SQLBuilder.CRITERIA_NAME);
            sqlParts.put(SQLBuilder.WHERE_NAME, validationData.get(SQLBuilder.CRITERIA_NAME));
        }
        else {
            if (!((sqlParts.get(SQLBuilder.WHERE_NAME) == null) || SQLBuilder.EMPTY_STRING.equals(sqlParts.get(SQLBuilder.WHERE_NAME)))) {
                where.append(SQLBuilder.WHERE_PREFIX)
                     .append(sqlParts.get(SQLBuilder.WHERE_NAME));
            }
        }

        if (useValidationData && !validationData.get(SQLBuilder.ORDER_LOWER_NAME).equals(SQLBuilder.EMPTY_STRING)) {
            order.append(SQLBuilder.ORDER_PREFIX).append(validationData.get(SQLBuilder.ORDER_LOWER_NAME));
            orderbyProperty = validationData.get(SQLBuilder.ORDER_LOWER_NAME);
            sqlParts.put(SQLBuilder.ORDER_NAME, validationData.get(SQLBuilder.ORDER_LOWER_NAME));
        }
        else {
            if (!((sqlParts.get(SQLBuilder.ORDER_NAME) == null) || SQLBuilder.EMPTY_STRING.equals(sqlParts.get(SQLBuilder.ORDER_NAME)))) {
                order.append(SQLBuilder.ORDER_PREFIX)
                     .append(sqlParts.get(SQLBuilder.ORDER_NAME));
            }
        }
        
        StringBuilder designStatement = new StringBuilder(ValidationsUserEnteredItemsModel.DESIGNTIME_SQL_STATEMENT);
        designStatement.append(SQLBuilder.SINGLE_QUOTE)
                .append(optionListId)
                .append(SQLBuilder.SINGLE_QUOTE)
                .append(where)
                .append(order);

        return designStatement.toString();
    }

    private boolean isValidDate(String s) {
        SimpleDateFormat f = new SimpleDateFormat(SQLBuilder.SIMPLE_DATE);
        try {
            f.parse(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private java.util.Date getDate(String s) {
        SimpleDateFormat f = new SimpleDateFormat(SQLBuilder.SIMPLE_DATE);
        try {
            return f.parse(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, s + NOT_VALID_DATE);
            return null;
        }
    }

    /**
     * 
     * @param row
     * @return
     */
    protected boolean editCurrentRecord(int row) {
        boolean sqlError = false;
        
        String value = (String)validationsListItems.getValueAt(row, 0);
        String trans = (String)validationsListItems.getValueAt(row, 1);
        String filter = (String)validationsListItems.getValueAt(row, 2);
        String xwalk1 = (String)validationsListItems.getValueAt(row, 3);
        String xwalk2 = (String)validationsListItems.getValueAt(row, 4);
        String xwalk3 = (String)validationsListItems.getValueAt(row, 5);
        String xwalk4 = (String)validationsListItems.getValueAt(row, 6);
        String xwalk5 = (String)validationsListItems.getValueAt(row, 7);
        String xwalk6 = (String)validationsListItems.getValueAt(row, 8);
        String start_date = (String)validationsListItems.getValueAt(row, 9);
        String end_date = (String)validationsListItems.getValueAt(row, 10);

        String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME, SQLBuilder.EMPTY_STRING);
        DatabaseConnection con = DBConnections.getConnection();
        String uid = (String)getValueAt(row, 0);

        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    java.util.Date startDate = null;
                    java.util.Date endDate = null;

                    StringBuilder sql = new StringBuilder("update validation_list_values set value = ?, translation = ?, " +
                            "filter_column = ?, xwalk1_vc = ?, xwalk2_vc = ?, xwalk3_vc = ?, xwalk4_vc = ?, xwalk5_vc = ?," +
                            "xwalk6_vc = ?");

                    if (hasDateValue(start_date)) {
                        startDate = getDate(start_date);
                        if (startDate == null) {
                            validationsListItems.setValueAt(SQLBuilder.EMPTY_STRING, validationsListItems.getSelectedRow(), 9);
                            return false;
                        }
                        sql.append(", start_date = ?");
                    }  else {
                        sql.append(", start_date = null");
                    }
                    if (hasDateValue(end_date)) {
                        endDate = getDate(end_date);
                        if (endDate == null) {
                            validationsListItems.setValueAt(SQLBuilder.EMPTY_STRING, validationsListItems.getSelectedRow(), 10);
                            return false;
                        }
                        sql.append(", end_date = ?");
                    }  else {
                        sql.append(", end_date = null");
                    }
                    sql.append(", touch_date = getdate(), touch_user = ? where validation_list_values_id = ?");

                    PreparedStatement stmt = conn.prepareStatement(sql.toString());
                    int idx = 1;
                    stmt.setString(idx++, value);
                    stmt.setString(idx++, trans);
                    stmt.setString(idx++, filter);
                    stmt.setString(idx++, xwalk1);
                    stmt.setString(idx++, xwalk2);
                    stmt.setString(idx++, xwalk3);
                    stmt.setString(idx++, xwalk4);
                    stmt.setString(idx++, xwalk5);
                    stmt.setString(idx++, xwalk6);
                    if (startDate != null) {
                        stmt.setDate(idx++, new Date(startDate.getTime()));
                    }
                    if (endDate != null) {
                        stmt.setDate(idx++, new Date(endDate.getTime()));
                    }
                    stmt.setString(idx++, dbuser);
                    stmt.setString(idx++, uid);

                    stmt.executeUpdate();
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
    private boolean deleteCurrentRow(String uid, String endDate) {
        boolean sqlError = false;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    Statement stmt = conn.createStatement();
                    String sql = DELETE_SQL_START + endDate + DELETE_SQL_END + uid + SQLBuilder.SINGLE_QUOTE;
                    stmt.executeUpdate(sql);
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
     * @param delFromTable
     */
    public void removeRow(int row, boolean delFromTable) {
        String uid = (String)getValueAt(row, 0);
        java.util.Date endDate = new java.util.Date();
        
        if (delFromTable) {
            // These stay in list with end date showing.
            deleteCurrentRow(uid, getDateStr(endDate));
            // End date is column 11 in the model
            setValueAt(getDateStr(endDate), row, 11);
        } else {
            super.removeRow(row);
        }
    }

    /**
     * 
     */
    private void clearTable() {
        for (int i=getRowCount()-1; i>=0; i--)
            removeRow(i,false);
    }

    /**
     * 
     * @return
     */
    public boolean getExistingRows() {
        boolean sqlError = false;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null))
        {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    clearTable();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(designtimeSQL);
                    SimpleDateFormat formatter = new SimpleDateFormat(SQLBuilder.SIMPLE_DATE);
                    while (rs.next()) {
                        Date d = rs.getDate(11);
                        String start_date = SQLBuilder.EMPTY_STRING;
                        if (d != null)
                            start_date = formatter.format(d);
                        String end_date = SQLBuilder.EMPTY_STRING;
                        d = rs.getDate(12);
                        if (d != null)
                            end_date = formatter.format(d);
                        addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),start_date,end_date});
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException ex) {
                    sqlError = true;
                    JOptionPane.showMessageDialog(null, ex);
                }
            }
        }
        return !sqlError;
    }

    public void removeVisibleColumn(int col) {
        if (visibleColumns.contains(col)) {
            visibleColumns.remove(col);
        }
    }
    
    public Boolean getIsVisibleColumn(int col) {
        if (visibleColumns.contains(col)) {
            return true;
        } else {
            return false;
        }
    }

    public Set<Integer> getVisibleColumns() {
        return visibleColumns;
    }
    
}
