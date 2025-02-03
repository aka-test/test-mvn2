/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import com.echoman.designer.databasemanager.DBConnections;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 * This creates a READ ONLY view of the database table data.
 * @author david.morin
 */
public class QueryTableModel extends AbstractTableModel {

    private Vector cache;
    private int colCount;
    private String[] headers;
    private DatabaseConnection dbconn = null;
    private Connection conn = null;
    private TableRowSorter storedRowSorter;
    private TableColumnModel storedColumnModel;
    private ArrayList<RowSorter.SortKey> storedSortKeys;
    private HashMap<Integer, HashMap<Integer, String>> storedFilter;
    private String runtimeSQL = SQLBuilder.EMPTY_STRING;
    private String designSQL = SQLBuilder.EMPTY_STRING;
    private HashMap<String, String> sqlParts = new HashMap<>();
    private boolean sqlPartsError = false;
    private HashMap<String, String> validationData = null;
    private String criteriaProperty = SQLBuilder.EMPTY_STRING;
    private String orderbyProperty = SQLBuilder.EMPTY_STRING;
    private HashMap<Integer, String> columns = new HashMap<>();
    private Set<String> visibleColumns = new LinkedHashSet<>();
    private ArrayList<String> designTimeColumns;

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
        if (!sqlPartsError) {
            return sqlParts;
        } else {
            return null;
        }
    }

    /**
     * 
     * @return
     */
    public List<? extends RowSorter.SortKey> getStoredSortKeys() {
        return storedSortKeys;
    }

    public String getDesignSQL() {
        return designSQL;
    }

    public void setDesignSQL(String runtimeSQL) {
        if (runtimeSQL != null) {
            String newDesignSQL;
            HashMap<String, String> newSqlParts = new HashMap();
            SQLBuilder.breakSqlIntoParts(runtimeSQL, newSqlParts);
            newDesignSQL = SQLBuilder.SELECT_PREFIX + newSqlParts.get(SQLBuilder.SELECT_NAME) + SQLBuilder.FROM_PREFIX + newSqlParts.get(SQLBuilder.FROM_NAME);
            if ((designTimeColumns != null) && (!designTimeColumns.isEmpty())) {
                newDesignSQL = SQLBuilder.rebuildSelectClause(newDesignSQL, getDesignTimeColumnsAsString());
            }
            this.designSQL = newDesignSQL + SQLBuilder.WHERE_PREFIX + SQLBuilder.ONE_EQUALS_ZERO;
        }
    }

    /**
     * 
     * @return
     */
    public String getRuntimeSQL() {
        return runtimeSQL;
    }

    /**
     * 
     * @param value
     */
    public void setRuntimeSQL(String runtimeSQL) {
        this.runtimeSQL = runtimeSQL;
        // Build a new design time sql based on the new runtime sql.
        setDesignSQL(runtimeSQL);
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
     * @param col
     * @param colname
     *
     */
    public void setColumn(Integer col, String colname) {
        columns.put(col, colname);
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
     * @param validationData
     */
    public QueryTableModel(HashMap<String, String> validationData) {
        this.validationData = validationData;

        // A new cache will clear all rows
        cache = new Vector();
        storedFilter = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            HashMap<Integer, String> criteria = new HashMap<>();
            storedFilter.put(i, criteria);
        }
        
        dbconn = DBConnections.getConnection();
        if (dbconn == null) {
            JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_SETUP_ERROR);
        } else {
            conn = dbconn.getJDBCConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_ERROR);
            }
        }
    }

    public void addVisibleColumn(String col) {
        if (!visibleColumns.contains(col)) {
            visibleColumns.add(col);
        }
    }
    
    public void removeVisibleColumn(String col) {
        if (visibleColumns.contains(col)) {
            visibleColumns.remove(col);
        }
    }
    
    public Boolean getIsVisibleColumn(String col) {
        if (visibleColumns.contains(col)) {
            return true;
        } else {
            return false;
        }
    }

    public void setDesignTimeColumns(ArrayList columns) {
        designTimeColumns = columns;
    }
    
    public String getVisibleColumnsAsString() {
        String cols = visibleColumns.toString();
        String result = cols.substring(1, cols.length()-1);
        return result;
    }
    
    public String getDesignTimeColumnsAsString() {
        String cols = designTimeColumns.toString();
        String result = cols.substring(1, cols.length()-1);
        return result;
    }
    
    public String[] getDesignTimeColumnsAsArray() {
        String[] cols = new String[designTimeColumns.size()];
        return designTimeColumns.toArray(cols);
    }
    
    public void setDesignTimeColumnsFromString(String columns) {
        if ((columns != null) && (designTimeColumns == null)) {
            designTimeColumns = new ArrayList<>();
            String[] colsArray = columns.split(SQLBuilder.COMMA);
            for (String col : colsArray) {
                designTimeColumns.add(col.trim());
            }
        }
    }

    public Set<String> getVisibleColumns() {
        return visibleColumns;
    }
    
    /**
     * 
     * @param i
     * @return
     */
    @Override
    public String getColumnName(int i) {
        return headers[i];
    }

    /**
     * 
     * @return
     */
    @Override
    public int getColumnCount() {
        return colCount;
    }

    /**
     * 
     * @return
     */
    @Override
    public int getRowCount() {
        return cache.size();
    }

    /**
     * 
     * @param row
     * @param col
     * @return
     */
    @Override
    public Object getValueAt(int row, int col) {
        return ((String[]) cache.elementAt(row))[col];
    }

    /**
     * 
     * @param storedSql
     * @return
     */
    public String buildRuntimeTimeSQL(String storedSql, Boolean useValidationData) {
        StringBuilder select = new StringBuilder(SQLBuilder.EMPTY_STRING);
        StringBuilder from = new StringBuilder(SQLBuilder.EMPTY_STRING);
        StringBuilder where = new StringBuilder(SQLBuilder.EMPTY_STRING);
        StringBuilder order = new StringBuilder(SQLBuilder.EMPTY_STRING);
        
        // Here we must use the order and criteria properties, not the sql statement
        // Break the actual query stored for this validation into it's parts
        // so we can use the join and where criteria, but with all of the columns
        // selected, not just the 3 for the runtime popup list.
        sqlPartsError = SQLBuilder.breakSqlIntoParts(storedSql, sqlParts);

        if (sqlPartsError) {
            return SQLBuilder.EMPTY_STRING;
        }
        
        if (useValidationData && !validationData.get(SQLBuilder.CRITERIA_NAME).equals(SQLBuilder.EMPTY_STRING)) {
            where.append(SQLBuilder.WHERE_PREFIX).append(validationData.get(SQLBuilder.CRITERIA_NAME));
            criteriaProperty = where.toString();
            sqlParts.put(SQLBuilder.WHERE_NAME, validationData.get(SQLBuilder.CRITERIA_NAME));
        } else {
            if (!((sqlParts.get(SQLBuilder.WHERE_NAME) == null) || SQLBuilder.EMPTY_STRING.equals(sqlParts.get(SQLBuilder.WHERE_NAME)))) {
                where.append(SQLBuilder.WHERE_PREFIX)
                     .append(sqlParts.get(SQLBuilder.WHERE_NAME));
            }
        }

        // The order by always stays the same
        if (!((sqlParts.get(SQLBuilder.ORDER_NAME) == null) || SQLBuilder.EMPTY_STRING.equals(sqlParts.get(SQLBuilder.ORDER_NAME)))) {
            order.append(SQLBuilder.ORDER_PREFIX)
                 .append(sqlParts.get(SQLBuilder.ORDER_NAME));
        }

        // Get a list of columns from select clause
        // This is here to support existing data that may not have the 
        // columns properly bracketed 
        String[] cols = sqlParts.get(SQLBuilder.SELECT_NAME).split(SQLBuilder.COMMA);
        StringBuilder colStr = new StringBuilder(SQLBuilder.EMPTY_STRING);
        for (String col : cols) {
            //get actual column name and alias for each column
            if (!EchoUtil.isNullOrEmpty(colStr)) {
                colStr.append(SQLBuilder.COMMA);
            }
            
            String[] colAlias = col.trim().split(" ");
            for (String ca : colAlias) {
                //properly format each column name and alias with bracket
                //to handle reserved word
                if (ca.contains(SQLBuilder.ESCAPED_QUOTE)) {
                    colStr.append(SQLBuilder.SPACE).append(ca);
                } else {
                    if (ca.contains(SQLBuilder.LEFT_BRACKET)) {
                        colStr.append(ca);
                    } else {
                        colStr.append(SQLBuilder.SPACE).append(SQLBuilder.LEFT_BRACKET).append(ca.replace(SQLBuilder.PERIOD, SQLBuilder.BRACKET_WITH_PERIOD)).append(SQLBuilder.RIGHT_BRACKET);
                    }
                }
            }
        }

        // If they haven't been set from a stored value, then use all 
        // the columns in the select.  This is to support records before
        // the DesignTimeColumns field was added.
        setDesignTimeColumnsFromString(colStr.toString());

        // Here we need to set the visibleColumns for the query
        // We always need to do this from select passed in.
        // We need to do it everytime to align these with the 
        // design time columns as far as what order they appear in.
        String[] colStrArray = colStr.toString().split(SQLBuilder.COMMA);
        ArrayList<String> tempVisibleCols = new ArrayList<>();
        for (int i=0; i<colStrArray.length; i++) {
            tempVisibleCols.add(colStrArray[i].trim());
        }
        visibleColumns.clear();
        if (designTimeColumns != null) {
            for (String column : designTimeColumns) {
                if (tempVisibleCols.contains(column)) {
                    visibleColumns.add(column);
                }
            }
        } else {
            visibleColumns.addAll(tempVisibleCols);
        }
        
        select.append(SQLBuilder.SELECT_PREFIX)
              .append((getVisibleColumnsAsString()));
        from.append(SQLBuilder.FROM_PREFIX)
            .append(sqlParts.get(SQLBuilder.FROM_NAME));

        // Rebuild the sql now with the new select columns and order.
        StringBuilder newSql = new StringBuilder(SQLBuilder.EMPTY_STRING);
        newSql.append(select)
              .append(from)
              .append(where)
              .append(order);
        
        // Update the sqlParts
        sqlPartsError = SQLBuilder.breakSqlIntoParts(newSql.toString(), sqlParts);
        criteriaProperty = sqlParts.get(SQLBuilder.WHERE_NAME);
        orderbyProperty = sqlParts.get(SQLBuilder.ORDER_NAME);
        
        return newSql.toString();
    }

    //Ticket #353
    /**
     *
     * Replace tokens with values
     *
     * @param qry
     * @return string
     */
    private String parseQueryForToken(String qry, boolean wantRestrictionWithToken) {
        HashMap<String, String> parts = new HashMap();
        SQLBuilder.breakSqlIntoParts(qry, parts);
        String where = parts.get(SQLBuilder.WHERE_NAME);
        String order = parts.get(SQLBuilder.ORDER_NAME);
        StringBuilder select = new StringBuilder(SQLBuilder.EMPTY_STRING);
        select.append(SQLBuilder.SELECT_PREFIX).append(parts.get(SQLBuilder.SELECT_NAME));
        StringBuilder from = new StringBuilder(SQLBuilder.EMPTY_STRING);
        from.append(SQLBuilder.FROM_PREFIX).append(parts.get(SQLBuilder.FROM_NAME));
        
        //Ticket #411 change token char from [ to { }
        if (!EchoUtil.isNullOrEmpty(where)) {
            where = where.replace(SQLBuilder.DATE_TOKEN_LOWER, SQLBuilder.DATE_CAST).
                    replace(SQLBuilder.DATE_TOKEN_HUMP, SQLBuilder.DATE_CAST).
                    replace(SQLBuilder.TIME_TOKEN_LOWER, SQLBuilder.GETDATE).
                    replace(SQLBuilder.TIME_TOKEN_HUMP, SQLBuilder.GETDATE).
                    replace(SQLBuilder.USERID_TOKEN_LOWER, SQLBuilder.SUSER_NAME).
                    replace(SQLBuilder.USERID_TOKEN_HUMP, SQLBuilder.SUSER_NAME);
            List<String> tokens = new ArrayList<>();
            Pattern regex = Pattern.compile(SQLBuilder.DATE_TOKEN_REGEX);
            Matcher regexMatcher = regex.matcher(where);
            while (regexMatcher.find()) {
                tokens.add(regexMatcher.group());
            }
            if (wantRestrictionWithToken) {
                for (String token : tokens) {
                    where = where.replace(token, SQLBuilder.NULL);
                }
            } else {
                //Ticket #411
                //if token is used just ignore the where clause so
                //all records can be displayed
                if (!tokens.isEmpty()) {
                    where = SQLBuilder.EMPTY_STRING;
                }
            }
        }

        StringBuilder ret = new StringBuilder(SQLBuilder.EMPTY_STRING);
        ret.append(select).append(from);
        if (!EchoUtil.isNullOrEmpty(where)) {
            ret.append(SQLBuilder.WHERE_PREFIX).append(where);
        }
        if (!EchoUtil.isNullOrEmpty(order)) {
            ret.append(SQLBuilder.ORDER_PREFIX).append(order);
        }
        return ret.toString();
    }

    /**
     *
     * @param q
     * @return
     */
    public boolean checkQuery(String q, String stored) {
        boolean validSql = false;
        HashMap<String, String> parts = new HashMap();
        StringBuilder newSelect = new StringBuilder(SQLBuilder.EMPTY_STRING);
        newSelect.append(SQLBuilder.SELECT_NAME).append(SQLBuilder.SPACE).append(stored).append(SQLBuilder.COMMA);
        String sql = q.replace(SQLBuilder.SELECT_NAME, newSelect.toString());

        if (!SQLBuilder.breakSqlIntoParts(sql, parts)) {
            if (conn == null) {
                JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_ERROR);
            } else {
                try {
                    Statement stmt = conn.createStatement();
                    String whereclause = parts.get(SQLBuilder.WHERE_NAME);
                    if (!EchoUtil.isNullOrEmpty(whereclause)) {
                        if (whereclause.indexOf(SQLBuilder.LEFT_BRACKET) > -1) {
                            sql = parseQueryForToken(sql, true);
                        }
                    }
                    ResultSet rs = stmt.executeQuery(sql);
                    validSql = true;
                    rs.close();
                    stmt.close();
                } catch (SQLException se) {
                    JOptionPane.showMessageDialog(null, SQLBuilder.FAILED_SQL_ERROR + se.getMessage());
                }
            }
        }
        return validSql;
    }

    /**
     * 
     * @param q
     * @return
     */
    public boolean setQuery(String q) {
        boolean sqlError = false;
        sqlPartsError = SQLBuilder.breakSqlIntoParts(q, sqlParts);
        setRuntimeSQL(q);
        cache = new Vector();
        if (conn == null) {
            sqlError = true;
            JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_ERROR);
        } else {
            try {
                Statement stmt = conn.createStatement();
                q = parseQueryForToken(q, false);
                ResultSet rs = stmt.executeQuery(q);
                ResultSetMetaData meta = rs.getMetaData();
                colCount = meta.getColumnCount();

                // Now we must rebuild the headers array with the new column names
                headers = new String[colCount];
                for (int h = 1; h <= colCount; h++) {
                    headers[h - 1] = meta.getColumnName(h);
                }

                // This would not be practical if we were expecting a
                // few million records in response to our query, but
                // we aren't, so we can do this.
                while (rs.next()) {
                    String[] record = new String[colCount];
                    for (int i = 0; i < colCount; i++) {
                        record[i] = rs.getString(i + 1);
                    }
                    cache.addElement(record);
                }
                rs.close();
                stmt.close();
                fireTableChanged(null); // notify everyone that we have a new table.
            } catch (SQLException se) {
                sqlError = true;
                JOptionPane.showMessageDialog(null, se.getMessage());
                cache = new Vector(); // blank it out and keep going.
            } catch (Exception e) {
                sqlError = true;
                cache = new Vector(); // blank it out and keep going.
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        return !sqlError;
    }
}
