/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.jidesoft.swing.SearchableUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 *
 * @author  david.morin
 */
public class ValidationsUserEnteredSetup extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    /**
     * 
     */
    public PropertyEditor editor;
    private boolean calledFromPropertyEditor = false;
    private final HashMap<String, String> validationData;
    private TableModelListener itemTableListener = null;
    private boolean editing = false;

    /**
     * 
     * @return
     */
    public boolean getCalledFromPropertyEditor() {
        return calledFromPropertyEditor;
    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getValidationPropertyData() {
        String id = (String) ((ValidationsUserEnteredModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 0);
        String name = (String) ((ValidationsUserEnteredModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 1);
        String criteria = ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).getCriteriaProperty();
        String order = ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).getOrderbyProperty();
        HashMap<String, String> data = new HashMap();
        data.put("id", id);
        data.put("name", name);
        data.put("storedColumnName", "Value");
        data.put("criteria", criteria);
        data.put("order", order);
        return data;
    }

    /**
     * 
     */
    public class SelectionListener implements ListSelectionListener {
        JTable table;
        String uid;
        HashMap<String, TableModel> storedListModels;

        /**
         * 
         * @param table
         */
        SelectionListener(JTable table) {
            this.table = table;
        }

        /**
         * 
         * @param e
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if ((table.getSelectedRow() >= 0) && (!e.getValueIsAdjusting())) {
                if (table.equals(validationsList)) {
                    stopEditing(validationsListItems);
                    storedListModels = ((ValidationsUserEnteredModel) table.getModel()).getStoredListModels();
                    uid = (String) ((ValidationsUserEnteredModel) table.getModel()).getValueAt(table.getSelectedRow(), 0);
                    if ((storedListModels.containsKey(uid))) {
                        ValidationsUserEnteredItemsModel storedModel = (ValidationsUserEnteredItemsModel) storedListModels.get(uid);
                        validationsListItems.setColumnModel(storedModel.getStoredColumnModel());
                        validationsListItems.setRowSorter(storedModel.getStoredRowSorter());
                        storedModel.getExistingRows();
                        validationsListItems.setModel(storedModel);
                        if (!(storedModel.getStoredSortKeys() == null)) {
                            validationsListItems.getRowSorter().setSortKeys(storedModel.getStoredSortKeys());
                        }
                        
                        // Populate checkboxes for display here
                        for (int j = 0; j < validationsListDisplayColumns.getColumnCount(); j++) {
                            Boolean value = ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).getIsVisibleColumn(j);
                            validationsListDisplayColumns.getModel().setValueAt(value, 0, j);
                        }
                        
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < validationsListCriteria.getColumnCount(); j++) {
                                String value = ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).getStoredFilter(i, j);
                                validationsListCriteria.getModel().setValueAt(value, i, j);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates new form ValidOptionListSetup
     * @param parent
     * @param modal
     * @param editor
     * @param validationData
     */
    public ValidationsUserEnteredSetup(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor, HashMap<String, String> validationData) {
        super(parent, modal);
        
        this.validationData = validationData;
        // This must be done here because it is used in initComponents.
        if (editor != null) {
            calledFromPropertyEditor = true;
        }
        initComponents();
        validationsList.setRowSorter(null);
        validationsList.setAutoCreateRowSorter(false);        
        setLocationRelativeTo(null);
        JTableHeader listHeader = validationsList.getTableHeader();
        listHeader.setOpaque(false);
        listHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        listHeader.setForeground(Color.white);
        listHeader.setBackground(new Color(127, 157, 185));
        listHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        listHeader.setComponentPopupMenu(pmDataOptionList);
        JTableHeader itemsHeader = validationsListItems.getTableHeader();
        itemsHeader.setOpaque(false);
        itemsHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        itemsHeader.setForeground(Color.white);
        itemsHeader.setBackground(new Color(127, 157, 185));
        itemsHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        JTableHeader criteriaHeader = validationsListCriteria.getTableHeader();
        criteriaHeader.setOpaque(false);
        criteriaHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        criteriaHeader.setForeground(Color.white);
        criteriaHeader.setBackground(new Color(127, 157, 185));
        criteriaHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        JTableHeader criteriaDispColHeader = validationsListDisplayColumns.getTableHeader();
        criteriaDispColHeader.setOpaque(false);
        criteriaDispColHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        criteriaDispColHeader.setForeground(Color.white);
        criteriaDispColHeader.setBackground(new Color(127, 157, 185));
        criteriaDispColHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        if (calledFromPropertyEditor) {
            validationsList.setComponentPopupMenu(null);
            validationsListItems.setComponentPopupMenu(null);
            validationsListItems.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsList.setColumnSelectionAllowed(false);
            validationsList.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsListItems.setColumnSelectionAllowed(false);
            validationsListItems.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsListCriteria.setEnabled(true);
            SearchableUtils.installSearchable(validationsList);
            validationsListDisplayColumns.setEnabled(false);
            validationsListDisplayColumns.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsListDisplayColumns.setColumnSelectionAllowed(false);
        } else {
            btnCancel.setVisible(false);
            validationsListCriteria.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsListCriteria.setEnabled(false);
        }
        //Ticket #213
        validationsList.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                ValidationsUserEnteredModel m = (ValidationsUserEnteredModel) validationsList.getModel();

                if ((!m.isEditing()) && (!m.isLoading()) && (!editing) && (validationsList.getRowCount() > 0)) {
                    if (validationsList.getRowCount() == 1) {
                        validationsList.getSelectionModel().setSelectionInterval(0, 0);
                    }
                    int row = validationsList.getSelectedRow();
                    int col = validationsList.getSelectedColumn();
                    if (!((row == -1) || (col == -1))) {
                        boolean populateFromCurrentRecord = false;
                        String uid = (String) m.getValueAt(row, 0);
                        if (!((uid == null) || (uid.equals("")))) {
                            populateFromCurrentRecord = true;
                        }

                        if (populateFromCurrentRecord) {
                            try {
                                m.editCurrentRecord();
                            } finally {
                                populateFromCurrentRecord = false;
                            }
                        }
                    }
                }
            }
        });

        validationsListItems.getModel().addTableModelListener(createItemTableListener());

        validationsListDisplayColumns.setValueAt(true,0, 0);
        validationsListDisplayColumns.setValueAt(true,0, 1);
        validationsListDisplayColumns.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                Boolean doUpdate = true;
                
                // TODO: We need to revisit this once we fix the validation
                // query to be able to properly handle column aliases
                
                if (e.getColumn() < 2) {
                    if (validationsListDisplayColumns.isFocusOwner()) {
                        ValidationsUserEnteredItemsModel model = (ValidationsUserEnteredItemsModel) validationsListItems.getModel();
                            Boolean val = (Boolean)validationsListDisplayColumns.getValueAt(0, e.getColumn());
                            if ((val != null) && val) {
                                // Update the runtime SQL when visible columns change
                                model.addVisibleColumn(e.getColumn());
                            } else {
                                if (model.getVisibleColumns().size() == 1) {
                                    JOptionPane.showMessageDialog(null, SQLBuilder.ONE_VISIBLE_MESSAGE);
                                    // Reset the checkbox if they unchecked the last one.
                                    validationsListDisplayColumns.setValueAt(true, 0, e.getColumn());
                                    doUpdate = false;
                                } else {
                                    model.removeVisibleColumn(e.getColumn());
                                }
                            }
                        // Update the runtime SQL when visible columns change
                        if (doUpdate) {
                            model.updateRuntimeSQLSelect();
                        }
                    }
                }
            }
            
        });

        validationsListCriteria.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).setStoredFilter();
            }
        });
        
    }

    //Ticket #213
    public TableModelListener createItemTableListener() {
        if (itemTableListener == null)
            itemTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                
                ValidationsUserEnteredItemsModel m = (ValidationsUserEnteredItemsModel) validationsListItems.getModel();
                if ((!m.isCreatingData()) && (!(e.getType() == -1))) {
                    // Ticket 33282 - getFirstRow() returns the correct row
                    // whether sorted or not, so we end up with the wrong row
                    // when it goes through convertRowIndexToModel(row) in
                    // ValidationsUserEnteredItemsModel.java.
                    // int row = e.getFirstRow();
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    if (!((row == -1) || (col == -1))) {
                        boolean populateFromCurrentRecord = false;
                        String uid = (String) m.getValueAt(row, 0);
                        if (!((uid == null) || (uid.equals("")))) {
                            populateFromCurrentRecord = true;
                        }

                        if (populateFromCurrentRecord) {
                            try {
                                m.editCurrentRecord(row);
                            } finally {
                                populateFromCurrentRecord = false;
                            }
                        }
                    }
                }
     
            }
        };

        return itemTableListener;
    }

    /**
     * 
     * @param retStatus
     */
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * 
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * 
     * @param table
     */
    private void stopEditing(JTable table) {
        int column = table.getEditingColumn();
        if (column > -1) {
            TableCellEditor cellEditor = table.getColumnModel().getColumn(column).getCellEditor();
            if (cellEditor == null) {
                cellEditor = table.getDefaultEditor(table.getColumnClass(column));
            }
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pmDataOptionList = new javax.swing.JPopupMenu();
        mInsert = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mDelete = new javax.swing.JMenuItem();
        pmUserEnteredItems = new javax.swing.JPopupMenu();
        mItemInsert = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mItemDelete = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        validationsListCriteria = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        validationsListItems = new javax.swing.JTable() {
            public boolean isCellEditable(int row, int column)
            {
                if (calledFromPropertyEditor)
                return false;
                else
                return true;
            }
        }
        ;
        //validationsListItems.getSelectionModel().addListSelectionListener(new ItemsSelectionListener(validationsListItems));
        validationsListItems.setTransferHandler(new TableTransferHandler());
        jScrollPane5 = new javax.swing.JScrollPane();
        validationsListDisplayColumns = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        validationsList = new javax.swing.JTable() {
            public boolean isCellEditable(int row, int column)
            {
                if (calledFromPropertyEditor)
                return false;
                else
                return true;
            }
        }
        ;
        validationsList.getSelectionModel().addListSelectionListener(new SelectionListener(validationsList));
        validationsList.setTransferHandler(new TableTransferHandler());
        jPanel3 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        mInsert.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.mInsert.text")); // NOI18N
        mInsert.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.mInsert.toolTipText")); // NOI18N
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mInsertActionPerformed(evt);
            }
        });
        pmDataOptionList.add(mInsert);
        pmDataOptionList.add(jSeparator1);

        mDelete.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.mDelete.text")); // NOI18N
        mDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDeleteActionPerformed(evt);
            }
        });
        pmDataOptionList.add(mDelete);

        mItemInsert.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.mItemInsert.text")); // NOI18N
        mItemInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mItemInsertActionPerformed(evt);
            }
        });
        pmUserEnteredItems.add(mItemInsert);
        pmUserEnteredItems.add(jSeparator2);

        mItemDelete.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.mItemDelete.text")); // NOI18N
        mItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mItemDeleteActionPerformed(evt);
            }
        });
        pmUserEnteredItems.add(mItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.title")); // NOI18N
        setLocationByPlatform(true);
        setModal(true);
        setName("validOptionListSetup"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        validationsListCriteria.setModel(new com.echoman.designer.components.echocommon.ValidationsCriteriaModel(ValidationsUserEnteredItemsModel.getCriteriaSqlStatement(),new String[]{"Value","Translation","Filter","X1","X2","X3","X4","X5","X6","Start Date","End Date"},1));
        validationsListCriteria.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.validationsListCriteria.toolTipText")); // NOI18N
        validationsListCriteria.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        validationsListCriteria.setDoubleBuffered(true);
        validationsListCriteria.setFillsViewportHeight(true);
        validationsListCriteria.setRowSelectionAllowed(false);
        validationsListCriteria.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListCriteria.getTableHeader().setReorderingAllowed(false);
        validationsListCriteria.getColumnModel().getColumn(0).setPreferredWidth(50);
        validationsListCriteria.getColumnModel().getColumn(1).setPreferredWidth(110);
        validationsListCriteria.getColumnModel().getColumn(2).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(3).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(4).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(5).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(6).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(7).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(8).setPreferredWidth(35);
        validationsListCriteria.getColumnModel().getColumn(9).setPreferredWidth(65);
        validationsListCriteria.getColumnModel().getColumn(10).setPreferredWidth(65);
        validationsListCriteria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListCriteriaKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(validationsListCriteria);
        ((com.echoman.designer.components.echocommon.ValidationsCriteriaModel)validationsListCriteria.getModel()).addRow(new String[]{"","","","","","","","","","",""});
        ((com.echoman.designer.components.echocommon.ValidationsCriteriaModel)validationsListCriteria.getModel()).addRow(new String[]{"","","","","","","","","","",""});

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.jLabel4.text")); // NOI18N
        jLabel4.setOpaque(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.jLabel2.text")); // NOI18N
        jLabel2.setOpaque(true);

        validationsListItems.setAutoCreateColumnsFromModel(false);
        validationsListItems.setModel(new ValidationsUserEnteredItemsModel(validationsList, validationsListItems, validationsListCriteria, validationsListDisplayColumns, new String[]{"UID","Value","Translation","Filter","X1","X2","X3","X4","X5","X6","Start Date","End Date"},0,"","",calledFromPropertyEditor,validationData));
        validationsListItems.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.validationsListItems.toolTipText")); // NOI18N
        validationsListItems.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        validationsListItems.setComponentPopupMenu(pmUserEnteredItems);
        validationsListItems.setDoubleBuffered(true);
        validationsListItems.setRowSelectionAllowed(false);
        validationsListItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListItems.getTableHeader().setReorderingAllowed(false);
        validationsListItems.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validationsListItemsFocusLost(evt);
            }
        });
        validationsListItems.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                validationsListItemsPropertyChange(evt);
            }
        });
        validationsListItems.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListItemsKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(validationsListItems);
        validationsListItems.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        validationsListDisplayColumns.setModel(new com.echoman.designer.components.echocommon.ValidationsDisplayColumnsModel(ValidationsUserEnteredItemsModel.getCriteriaSqlStatement(),new String[]{"Value","Translation","Filter","X1","X2","X3","X4","X5","X6","Start Date","End Date"},1));
        validationsListDisplayColumns.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.validationsListDisplayColumns.toolTipText")); // NOI18N
        validationsListDisplayColumns.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        validationsListDisplayColumns.setDoubleBuffered(true);
        validationsListDisplayColumns.setFillsViewportHeight(true);
        validationsListDisplayColumns.setRowSelectionAllowed(false);
        validationsListDisplayColumns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListDisplayColumns.getTableHeader().setReorderingAllowed(false);
        validationsListDisplayColumns.getColumnModel().getColumn(0).setPreferredWidth(50);
        validationsListDisplayColumns.getColumnModel().getColumn(1).setPreferredWidth(110);
        validationsListDisplayColumns.getColumnModel().getColumn(2).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(3).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(4).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(5).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(6).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(7).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(8).setPreferredWidth(35);
        validationsListDisplayColumns.getColumnModel().getColumn(9).setPreferredWidth(65);
        validationsListDisplayColumns.getColumnModel().getColumn(10).setPreferredWidth(65);
        validationsListDisplayColumns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListDisplayColumnsKeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(validationsListDisplayColumns);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.jLabel5.text")); // NOI18N
        jLabel5.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(13, 13, 13)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.jLabel3.text")); // NOI18N
        jLabel3.setOpaque(true);

        validationsList.setAutoCreateRowSorter(true);
        validationsList.setModel(new com.echoman.designer.components.echocommon.ValidationsUserEnteredModel(validationsList, validationsListItems, validationsListCriteria, validationsListDisplayColumns, new String[]{"UID", "Name", "Description", "SQL"},0,calledFromPropertyEditor, validationData, createItemTableListener()));
        validationsList.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.validationsList.toolTipText")); // NOI18N
        validationsList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        validationsList.setCellSelectionEnabled(true);
        validationsList.setComponentPopupMenu(pmDataOptionList);
        validationsList.setDoubleBuffered(true);
        validationsList.setOpaque(false);
        validationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsList.getTableHeader().setReorderingAllowed(false);
        validationsList.removeColumn(validationsList.getColumnModel().getColumn(0));
        validationsList.removeColumn(validationsList.getColumnModel().getColumn(2));
        validationsList.getColumnModel().getColumn(0).setMinWidth(50);
        validationsList.getColumnModel().getColumn(1).setMinWidth(215);
        validationsList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                validationsListFocusLost(evt);
            }
        });
        validationsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(validationsList);
        validationsList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnOk.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.btnOk.text")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText(org.openide.util.NbBundle.getMessage(ValidationsUserEnteredSetup.class, "ValidationsUserEnteredSetup.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancel(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void doCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancel
    doClose(RET_CANCEL);
}//GEN-LAST:event_doCancel

private void validationsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListKeyPressed

    if ((evt.getKeyCode() == KeyEvent.VK_INSERT) && (!calledFromPropertyEditor)) {
        ((ValidationsUserEnteredModel) validationsList.getModel()).createNewDataListItem(this);
    } else if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && (!calledFromPropertyEditor)) {
        removeDataListItem();
    } else if ((!calledFromPropertyEditor) && ((evt.getKeyCode() == KeyEvent.VK_TAB) && (validationsList.getSelectedColumn() == validationsList.getColumnCount() - 1))) {
        // Editing a new row so create a new items row.
        stopEditing(validationsList);
        evt.consume();
        if (validationsListItems.getRowCount() <= 0) {
            ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).createNewDataListItem();
        }
    } else if (calledFromPropertyEditor && (evt.getKeyCode() == KeyEvent.VK_ENTER)) {
        evt.consume();
    }
}//GEN-LAST:event_validationsListKeyPressed

private void validationsListItemsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListItemsKeyPressed

    if ((evt.getKeyCode() == KeyEvent.VK_INSERT) && (!calledFromPropertyEditor)) {
        ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).createNewDataListItem();
    } else if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && (!calledFromPropertyEditor)) {
        removeValidationsListItems();
    } else if ((!calledFromPropertyEditor) && ((evt.getKeyCode() == KeyEvent.VK_TAB) || (evt.getKeyCode() == KeyEvent.VK_ENTER))) {
        stopEditing(validationsListItems);
        ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).editCurrentRecord(validationsListItems.getSelectedRow());
        if (validationsListItems.getSelectedColumn() == validationsListItems.getColumnCount() - 1) {
            evt.consume();
        }
    } else if (calledFromPropertyEditor && (evt.getKeyCode() == KeyEvent.VK_ENTER)) {
        stopEditing(validationsListItems);
        evt.consume();
    }
}//GEN-LAST:event_validationsListItemsKeyPressed

private void mDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDeleteActionPerformed
    if (!calledFromPropertyEditor) {
        removeDataListItem();
    }
}//GEN-LAST:event_mDeleteActionPerformed

    /**
     *
     */
    private void removeValidationsListItems() {

        String uid = (String) validationsListItems.getModel().getValueAt(validationsListItems.getSelectedRow(), 0);
        // Don't delete the blank rows for inserting new records
        if (!(uid.equals(""))) {
            if ((JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this validation item?")) == JOptionPane.YES_OPTION) {
                
                int rowToDelete = validationsListItems.getSelectedRow();
                
                if (validationsListItems.getSelectedRow() > 0) {
                    validationsListItems.getSelectionModel().setSelectionInterval(validationsListItems.getSelectedRow() - 1, validationsListItems.getSelectedRow() - 1);
                } else if (validationsListItems.getRowCount() > 1) {
                    validationsListItems.getSelectionModel().setSelectionInterval(validationsListItems.getSelectedRow() + 1, validationsListItems.getSelectedRow() + 1);
                }
 
                ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).removeRow(rowToDelete, true);
            }
        }
    }

    /**
     *
     */
    private void removeDataListItem() {
        int rowToDelete = validationsList.getSelectedRow();
        
        if (rowToDelete == -1) {
            return;
        }

        if ((JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this validation?")) == JOptionPane.YES_OPTION) {
            editing = true;
            
            try {
                if (validationsList.getSelectedRow() > 0) {
                    validationsList.getSelectionModel().setSelectionInterval(validationsList.getSelectedRow() - 1, validationsList.getSelectedRow() - 1);
                } else if ((validationsList.getRowCount() > 1) && (validationsList.getSelectedRow() == 0)) {
                    validationsList.getSelectionModel().setSelectionInterval(validationsList.getSelectedRow() + 1, validationsList.getSelectedRow() + 1);
                }

                // Delete items rows in the model when deleting validation.
                // Only if rowCount is 0 for validationsList.
                if (validationsList.getRowCount() == 1) {
                    for (int i=validationsListItems.getModel().getRowCount()-1; i>=0; i--) {
                        ((ValidationsUserEnteredItemsModel)validationsListItems.getModel()).removeRow(i,false);
                    }
                }

                ((ValidationsUserEnteredModel) validationsList.getModel()).removeRow(rowToDelete);

            } finally {
                editing = false;
            }
        }
    }

private void validationsListItemsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_validationsListItemsFocusLost
}//GEN-LAST:event_validationsListItemsFocusLost

private void validationsListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_validationsListFocusLost
}//GEN-LAST:event_validationsListFocusLost

private void validationsListItemsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_validationsListItemsPropertyChange
}//GEN-LAST:event_validationsListItemsPropertyChange

private void validationsListCriteriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListCriteriaKeyPressed
}//GEN-LAST:event_validationsListCriteriaKeyPressed

private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
    //Ticket #379
    stopEditing(validationsList);
    stopEditing(validationsListCriteria);
    stopEditing(validationsListItems);
    doClose(RET_OK);
}//GEN-LAST:event_btnOkActionPerformed

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    validationsList.requestFocusInWindow();
    if ((calledFromPropertyEditor) && (validationsList.getRowCount() > 0)) {
        if (!(validationData == null)) {
            if (!(validationData.get("id").equals(""))) {
                for (int i = 0; i < validationsList.getRowCount(); i++) {
                    if (((String) (validationsList.getModel().getValueAt(i, 0))).equals((String) validationData.get("id"))) {
                        validationsList.setRowSelectionInterval(i, i);
                    }
                }
            }
        }
    } else {
        if (validationsList.getRowCount() > 0) {
            validationsList.setRowSelectionInterval(0, 0);
        }
    }
    validationsList.setColumnSelectionInterval(0, 0);

}//GEN-LAST:event_formWindowOpened

    private void validationsListDisplayColumnsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListDisplayColumnsKeyPressed
    }//GEN-LAST:event_validationsListDisplayColumnsKeyPressed

    private void mInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mInsertActionPerformed
        if (!calledFromPropertyEditor) {
            ((ValidationsUserEnteredModel)validationsList.getModel()).createNewDataListItem(this);
        }
    }//GEN-LAST:event_mInsertActionPerformed

    private void mItemInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mItemInsertActionPerformed
        if (!calledFromPropertyEditor) {
            ((ValidationsUserEnteredItemsModel) validationsListItems.getModel()).createNewDataListItem();
        }
    }//GEN-LAST:event_mItemInsertActionPerformed

    private void mItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mItemDeleteActionPerformed
        if (!calledFromPropertyEditor) {
            removeValidationsListItems();
        }
    }//GEN-LAST:event_mItemDeleteActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem mDelete;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem mItemDelete;
    private javax.swing.JMenuItem mItemInsert;
    private javax.swing.JPopupMenu pmDataOptionList;
    private javax.swing.JPopupMenu pmUserEnteredItems;
    private javax.swing.JTable validationsList;
    private javax.swing.JTable validationsListCriteria;
    private javax.swing.JTable validationsListDisplayColumns;
    private javax.swing.JTable validationsListItems;
    // End of variables declaration//GEN-END:variables
    /**
     * 
     */
    private int returnStatus = RET_CANCEL;
}
