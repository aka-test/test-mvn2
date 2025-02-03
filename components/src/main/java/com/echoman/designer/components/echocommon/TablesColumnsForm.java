/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Cursor;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echobutton.EchoButtonNode;
import com.echoman.designer.components.echodatacontainer.EchoDataContainerNodeData;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echoradiobutton.EchoRadioButtonNodeData;
import com.echoman.designer.components.echotable.EchoTableNode;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author  david.morin
 */
public class TablesColumnsForm extends javax.swing.JDialog {
    public static final String PROPERTY_FORM_LINK_TO_COL = "Form Link To Column";
    public static final String PROPERTY_FORM_LINK_COL = "Form Link Value Column";
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    public PropertyEditor editor = null;
    public String tableName = "";
    public String columns = "";
    public String storedTable = JDesiWindowManager.getActiveDesignerPage().getTable();
    boolean bHaveAllEchoCols = false;
    boolean includeAllTables = false;
    boolean includeAllColumns = false;
    private IEchoComponentNodeData nodedata;
    private DialogCloseListener dialogCloseListener = null;

    public interface DialogCloseListener {

        public void onClose(String selectedColumns);
    }

    // Called from tables for form link column property.
    // Called from buttons for form link column property.
    public TablesColumnsForm(java.awt.Frame parent, boolean modal,
            PropertyEditorSupport editor, boolean multipleColumn,
            boolean includeAllColumns, boolean allowTableSelection, String propertyName) {
        this(parent, modal, editor, propertyName);
        this.includeAllColumns = includeAllColumns;
        if (multipleColumn) {
            columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        tableList.setEnabled(allowTableSelection);
    }

    // Called from tables, once columns have been selected, or for table column sorts.
    public TablesColumnsForm(java.awt.Frame parent, boolean modal,
            PropertyEditorSupport editor, boolean multipleColumn,
            boolean includeAllColumns, boolean allowTableSelection,
            String tableName, String selectedColumns, String propertyName) {
        this(parent, modal, editor, multipleColumn, includeAllColumns,
                allowTableSelection, propertyName);
        this.tableName = tableName;
        this.columns = selectedColumns;
    }

    /**
     * Creates new form TablesColumnsForm
     * @param parent
     * @param editor
     */
    // Called from tables for form link to column property.
    // Called from buttons for form link to column property.
    public TablesColumnsForm(java.awt.Frame parent, PropertyEditorSupport editor, String propertyName) {
        this(parent, true, editor, propertyName);
        includeAllTables = true;
    }

    /**
     * Creates new form TablesColumnsForm
     * @param parent
     * @param modal
     * @param editor
     */
    // This is called for tables, when tables have not had any columns selected yet.
    // Also called to set Button form link columns.
    public TablesColumnsForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor, String propertyName) {
        super(parent, modal);
        this.editor = editor;
        boolean isLinkColumn = false;
        boolean isLinkToColumn = false;
        initComponents();
        setLocationRelativeTo(null);
        // This will only return one in the array because only the form has a table
        // property.  All the other components set the table when the column or columns
        // property is set.  Tables likewise also are the only component that have
        // a Table Columns property, and so only one can be selected in that case.
        // If mulitple components with a column property are selected, then they
        // will both have the same table name set.  This may or may not present
        // a problem when we actually support multiple tables on the form.
        Node[] ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        // Ticket 528, 529
        IEchoComponentNode node = (IEchoComponentNode) ary[0];
        tableName = ((IEchoDataAwareComponentNodeData) node.getNodeData()).getTable();
        if ((node instanceof EchoButtonNode) ||
            ((node instanceof EchoTableNode) && 
                ((propertyName.equals(PROPERTY_FORM_LINK_TO_COL)) || 
                    (propertyName.equals(PROPERTY_FORM_LINK_COL))))) {
            isLinkColumn = true;
            final IEchoDataAwareComponentNodeData nodeData = (IEchoDataAwareComponentNodeData) node.getNodeData();
            String LinkToColumn = nodeData.getFormLinkToColumn();
            if (EchoUtil.isNullOrEmpty(LinkToColumn)) {
                LinkToColumn = nodeData.getUiLinkToColumn();
            }
            String LinkColumn = nodeData.getFormLinkColumn();
            if (EchoUtil.isNullOrEmpty(LinkColumn)) {
                LinkColumn = nodeData.getUiLinkColumn();
            }
            if (propertyName.equalsIgnoreCase(PROPERTY_FORM_LINK_TO_COL)) {
                isLinkToColumn = true;
                if (!EchoUtil.isNullOrEmpty(LinkToColumn)) {
                    String tbl = LinkToColumn.substring(0, LinkToColumn.lastIndexOf('.'));
                    String col = LinkToColumn.substring(LinkToColumn.lastIndexOf('.')+1, LinkToColumn.length());
                    tableName = tbl;
                    columns = col;
                } else {
                    tableName = "";
                    columns = "";
                }
            } else {
                if (!EchoUtil.isNullOrEmpty(LinkColumn)) {
                    columns = LinkColumn;
                } else {
                    columns = "";
                }
            }
        } else {
            columns = ((IEchoDataAwareComponentNodeData)node.getNodeData()).getColumn();
        }
        nodedata = ((IEchoDataAwareComponentNodeData)node.getNodeData());

        // The form master table.
        if (tableName.equals("")) {
            tableName = storedTable;
        }

        if (isLinkColumn) {
            setTitle("Select Column");
            tableList.setEnabled(isLinkToColumn);
            columnList.setEnabled(true);
            columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            setTitle("Select Columns");
            tableList.setEnabled(false);
            columnList.setEnabled(true);
            columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        btnCreateEchoDefCols.setVisible(false);
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        columnList = new javax.swing.JList();
        btnCreateEchoDefCols = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TablesColumnsForm.class, "TablesColumnsForm.title")); // NOI18N
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                AddTablesAndColumns(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(TablesColumnsForm.class, "TablesColumnsForm.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(TablesColumnsForm.class, "TablesColumnsForm.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        tableList.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        tableList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableList.setFocusCycleRoot(true);
        tableList.setName("tableList"); // NOI18N
        tableList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectItem(evt);
            }
        });
        tableList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                getTableColumns(evt);
            }
        });
        jScrollPane1.setViewportView(tableList);

        columnList.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        columnList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        columnList.setFocusTraversalPolicyProvider(true);
        columnList.setName("columnList"); // NOI18N
        columnList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectItem(evt);
            }
        });
        jScrollPane2.setViewportView(columnList);

        btnCreateEchoDefCols.setText(org.openide.util.NbBundle.getMessage(TablesColumnsForm.class, "TablesColumnsForm.btnCreateEchoDefCols.text")); // NOI18N
        btnCreateEchoDefCols.setToolTipText(org.openide.util.NbBundle.getMessage(TablesColumnsForm.class, "TablesColumnsForm.btnCreateEchoDefCols.toolTipText")); // NOI18N
        btnCreateEchoDefCols.setEnabled(false);
        btnCreateEchoDefCols.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCreateEchoDefCols(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCreateEchoDefCols)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCreateEchoDefCols))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        StringBuilder tableColumns = new StringBuilder();
        tableColumns.append(tableList.getSelectedValue());
        for (int i = 0; i < columnList.getSelectedValues().length; i++) {
            tableColumns.append(";").append(columnList.getSelectedValues()[i]);
        }
        columns = tableColumns.toString();
        if (this.editor != null) {
            this.editor.setAsText(tableColumns.toString());
        }
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private List<String> getFormTables() {
        // CDT-367 FormNodeData only returns the linked tables and includes
        // the container.  Just get it directly through the form or container.
        List<String> formTables = new ArrayList<>();
        
        List<IEchoComponentNodeData> compList = JDesiWindowManager.getActiveDesignerPage().getCompList();
        for (IEchoComponentNodeData nd : compList) {
            if (nd instanceof EchoFormNodeData) {
                if (!formTables.contains(((EchoFormNodeData) nd).getTable())) {
                    formTables.add(((EchoFormNodeData) nd).getTable());
                }
            } else if (nd instanceof EchoDataContainerNodeData) {
                if (!formTables.contains(((EchoDataContainerNodeData) nd).getTable())) {
                    formTables.add(((EchoDataContainerNodeData) nd).getTable());
                }
            }
        }

        if (formTables.size() > 0) {
            return formTables;
        }

        return null;
    }

private void AddTablesAndColumns(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_AddTablesAndColumns

    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
        DatabaseConnection con = DBConnections.getConnection();

        if (!(con == null)) {
            String selectedObject = "";
            DefaultListModel tableListModel = new DefaultListModel();

            boolean formHasNoTableSet = true;
            if (!includeAllTables) {
                // Grids have only one table to select
                if (getTitle().equals("Select Columns")) {
                    tableListModel.addElement(tableName);
                    selectedObject = (String) tableListModel.get(0);
                    formHasNoTableSet = false;
                } else {
                    List<String> tables = getFormTables();
                    if (tables != null) {
                        for (String tbl : tables) {
                            tableListModel.addElement(tbl);
                            if (tableName.equals(tbl)) {
                                selectedObject = (String) tableListModel.get(tableListModel.size() - 1);
                            }
                            formHasNoTableSet = false;
                        }
                    }
                }
            }

            if (formHasNoTableSet) {
                String[] tableTypes = {"TABLE", "VIEW"};
                Connection conn = con.getJDBCConnection();
                if (!(conn == null)) {
                    try {
                        DatabaseMetaData md = conn.getMetaData();
                        ResultSet rs = md.getTables(null, null, null, tableTypes);
                        while (rs.next()) {
                            tableListModel.addElement(rs.getString("TABLE_SCHEM") + "." + rs.getString("TABLE_NAME"));
                            if (tableName.equals(rs.getString("TABLE_SCHEM") + "." + rs.getString("TABLE_NAME"))) {
                                selectedObject = (String) tableListModel.get(tableListModel.size() - 1);
                            }
                        }
                        rs.close();
                        // This connection should not be closed here...it is controlled through the DatabaseExplorer
                        //conn.close();
                    } catch (SQLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            tableList.setModel(tableListModel);
            if (selectedObject.equals("")) {
                tableList.setSelectedIndex(-1);
            } else {
                tableList.setSelectedValue(selectedObject, true);
            }
        }
    } finally {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}//GEN-LAST:event_AddTablesAndColumns

private void getTableColumns(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_getTableColumns
    ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
    boolean bHaveUid = false;
    boolean bHaveCreateUser = false;
    boolean bHaveTouchUser = false;
    boolean bHaveCreateDate = false;
    boolean bHaveTouchDate = false;
    boolean bHaveSomeEchoCols = false;
    bHaveAllEchoCols = false;

    DatabaseConnection con = DBConnections.getConnection();
    if (!(con == null)) {
        DefaultListModel columnListModel = new DefaultListModel();
        tableName = (String) ((JList) evt.getSource()).getSelectedValue();
        String getColTableName = tableName.substring(tableName.indexOf('.') + 1);
        String getColTableSchema = tableName.substring(0, tableName.indexOf('.'));
        Connection conn = con.getJDBCConnection();
        List primaryKeys = new ArrayList();

        if (!(conn == null)) {
            try {

                DatabaseMetaData md = conn.getMetaData();

                ResultSet rspk = md.getPrimaryKeys(null, getColTableSchema, getColTableName);
                while (rspk.next()) {
                    primaryKeys.add(rspk.getString("COLUMN_NAME"));
                }
                rspk.close();
                ResultSet rsc = md.getColumns(null, getColTableSchema, getColTableName, null);
                while (rsc.next()) {
                    String colKey = "";
                    String colName = rsc.getString("COLUMN_NAME");
                    int colSize = rsc.getInt("COLUMN_SIZE");
                    if (!bHaveUid) {
                        bHaveUid = colName.equals("uid");
                    }
                    if (!bHaveCreateUser) {
                        bHaveCreateUser = colName.equals("CreateUser");
                    }
                    if (!bHaveTouchUser) {
                        bHaveTouchUser = colName.equals("UpdateUser");
                    }
                    if (!bHaveCreateDate) {
                        bHaveCreateDate = colName.equals("CreateDate");
                    }
                    if (!bHaveTouchDate) {
                        bHaveTouchDate = colName.equals("UpdateDate");
                    }
                    if (bHaveUid && bHaveCreateUser && bHaveTouchUser && bHaveCreateDate && bHaveTouchDate) {
                        bHaveAllEchoCols = true;
                    }
                    if (bHaveUid || bHaveCreateUser || bHaveTouchUser || bHaveCreateDate || bHaveTouchDate) {
                        bHaveSomeEchoCols = true;
                    }
                    if (primaryKeys.contains(colName)) {
                        colKey = "pKey";
                    }
                    String padColName = String.format("%1$-20s ", colName);
                    String colType = rsc.getString("TYPE_NAME");
                    //Ticket #1 restrict column selection by data type and size
                    boolean addCol = false;
                    if (this.includeAllColumns) {
                        addCol = true;
                    } else {

                        for (String s : nodedata.getExpectedDataType()) {
                            if ((s.equalsIgnoreCase(colType))
                                    || (s.equalsIgnoreCase("form"))) {
                                if ((nodedata.getExpectedSize() == -1)
                                        || (nodedata.getExpectedSize() == colSize)) {
                                    if (nodedata.getClass() == EchoRadioButtonNodeData.class) {
                                        int len = ((EchoRadioButtonNodeData) nodedata).getItemValueLength();
                                        if (len <= colSize) {
                                            addCol = true;
                                            break;
                                        }
                                    } else {
                                        addCol = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (addCol) {
                        //Ticket #87
                        if (colType.equalsIgnoreCase("varchar")
                                || colType.equalsIgnoreCase("char")) {
                            colType = colType + "(" + colSize + ")";
                        }


                        String padColType = String.format("%1$-20s ", colType);
                        String colInfo = padColName + padColType + colKey;
                        columnListModel.addElement(colInfo);
                        if (columns.contains(colName)) {
                            selectedIndices.add(columnListModel.indexOf(colInfo));
                        }
                    }
                }
                rsc.close();
                // This connection should not be closed here...it is controlled through the DatabaseExplorer
                //conn.close();
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        columnList.setModel(columnListModel);
        if (selectedIndices.size() <= 0) {
            columnList.setSelectedIndex(-1);
        } else {
            int[] indices = new int[selectedIndices.size()];
            for (int i = 0; i < selectedIndices.size(); ++i) {
                indices[i] = selectedIndices.get(i);
            }
            columnList.setSelectedIndices(indices);
        }
        btnCreateEchoDefCols.setEnabled(!bHaveSomeEchoCols);

        //allow for tables that don't have all the echo cols
        okButton.setEnabled(columnList.getSelectedIndices().length > 0);
    }
}//GEN-LAST:event_getTableColumns

private void selectItem(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectItem
    if (evt.getClickCount() > 1) {
        okButton.doClick();
    //allow for tables that don't have all the echo cols
    } else if (columnList.getSelectedValues().length > 0) {
        okButton.setEnabled(true);
    } else {
        okButton.setEnabled(false);
    }
}//GEN-LAST:event_selectItem

    private void doCreateEchoDefCols(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCreateEchoDefCols
        boolean error = false;
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            if (!(tableName.equals(""))) {
                Connection conn = con.getJDBCConnection();
                if (!(conn == null)) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    try {
                        try {
                            Statement stmt = conn.createStatement();
                            stmt.execute(EchoUtil.getAlterDefaultColumnQuery(tableName));
                            stmt.close();
                            tableList.setSelectedIndex(tableList.getSelectedIndex() - 1);
                            tableList.setSelectedIndex(tableList.getSelectedIndex() + 1);
                        } catch (SQLException ex) {
                            error = true;
                            JOptionPane.showMessageDialog(this, "There was an error adding the default Echo Columns." + "\n" + ex.getMessage());
                        }
                        if (!error) {
                            JOptionPane.showMessageDialog(this, "The Echo Default Columns were successfully added.");
                        }
                    } finally {
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        }
    }//GEN-LAST:event_doCreateEchoDefCols

    public void addDialogCloseListener(DialogCloseListener listener) {
        dialogCloseListener = listener;
    }

    /**
     *
     * @param retStatus
     */
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        if (dialogCloseListener != null) {
            dialogCloseListener.onClose(columns);
        }
        setVisible(false);
        dispose();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateEchoDefCols;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList columnList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okButton;
    private javax.swing.JList tableList;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
