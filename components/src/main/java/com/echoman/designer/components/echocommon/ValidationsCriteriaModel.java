/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author david.morin
 */
public class ValidationsCriteriaModel extends DefaultTableModel{
    protected String[] physicalColumns;
    protected String sql;

    static final String COLUMNS_REGEX = "(?i)((\\[)?[\\w]+(\\])?.(\\[)?[\\w]+(\\])?)(?= +\"[\\w| ]+\",? ?)";
    
    /**
     * 
     * @return
     */
    public String getSql() {
        return sql;
    }

    /**
     * 
     * @param sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 
     */
    private void extractPhysicalColumnsFromSQL() {
        int index = 0;
        // This regex will pull the physical columns out of a string with
        // the format Address.city "city", Address.state "state", etc.
        //Ticket #411
        //includes format that uses square bracket eg. [address].[city]
        //Pattern regex = Pattern.compile("(?i)([\\w]+.[\\w]+)(?= +\"[\\w| ]+\",? ?)");
        Pattern regex = Pattern.compile(COLUMNS_REGEX);
        Matcher regexMatcher = regex.matcher(sql);
        while (regexMatcher.find()) {
            physicalColumns[index++] = regexMatcher.group();
        }


        //Ticket #353 - to handle select clause with format: city, state, etc
        String[] cols = sql.split(SQLBuilder.COMMA);
        for (int i = 0; i < physicalColumns.length; i++) {
            String col = physicalColumns[i];
            if (col == null) {
                try {
                    physicalColumns[i] = cols[i].trim();
                } catch (Exception e) {
                    Logger.getLogger(ValidationsCriteriaModel.class.getName()).log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    /**
     * 
     * @param index
     * @return
     */
    public String getPhysicalColumn(int index) {
        return physicalColumns[index];
    }

    /**
     * 
     * @param columnName
     */
    @Override
    public void addColumn(Object columnName) {
        super.addColumn(columnName);
    }

    /**
     * 
     * @param rowData
     */
    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
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
     * @param row
     */
    @Override
    public void removeRow(int row) {
        super.removeRow(row);
    }

    /**
     * 
     * @param sql
     * @param columnNames
     * @param rowCount
     */
    public ValidationsCriteriaModel(String sql, Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
        this.sql = sql;
        if (!(columnNames == null)) {
            physicalColumns = new String[columnNames.length];
            // The jTDS jdbc driver returns the alias for the column name, not the
            // physical column.
            extractPhysicalColumnsFromSQL();
        }
    }
}
