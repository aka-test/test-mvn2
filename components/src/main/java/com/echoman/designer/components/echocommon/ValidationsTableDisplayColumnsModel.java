/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import com.echoman.designer.databasemanager.DBConnections;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author david.morin
 */
public class ValidationsTableDisplayColumnsModel extends ValidationsDisplayColumnsModel{
    private int colCount;
    private String[] headers;

    @Override
    public String getColumnName(int column) {
        return headers[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * 
     * @param q
     * @return
     */
    private boolean setQuery(String q) {
        boolean sqlError = false;
        DatabaseConnection dbconn = DBConnections.getConnection();
        Connection conn = null;
        if (dbconn == null) {
            JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_SETUP_ERROR);
        } else {
            conn = dbconn.getJDBCConnection();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_ERROR);
            }
        }
        if (conn == null) {
            sqlError = true;
            JOptionPane.showMessageDialog(null, SQLBuilder.DB_CONN_ERROR);
        } else {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(q);
                ResultSetMetaData meta = rs.getMetaData();
                colCount = meta.getColumnCount();

                // Now we must rebuild the headers array with the new column names
                headers = new String[colCount];
                for (int h = 1; h <= colCount; h++) {
                    headers[h - 1] = meta.getColumnName(h);
                }
                rs.close();
                stmt.close();
                fireTableChanged(null); // notify everyone that we have a new table.
            } catch (SQLException se) {
                sqlError = true;
                JOptionPane.showMessageDialog(null, se.getMessage());
            } catch (Exception e) {
                sqlError = true;
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
        return !sqlError;
    }

    /**
     * 
     * @param sql
     * @param columnNames
     * @param rowCount
     */
    public ValidationsTableDisplayColumnsModel(String sql, Object[] columnNames, int rowCount) {
        super(sql, columnNames, rowCount);
        if (!SQLBuilder.EMPTY_STRING.equals(sql)) {
            Boolean validSql = setQuery(sql);
            if (!validSql) {
               JOptionPane.showMessageDialog(null, SQLBuilder.FAILED_SQL_ERROR);
            }
        }
    }
}
