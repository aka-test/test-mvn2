/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.jidesoft.swing.SearchableUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author  david.morin
 */
public class ValidationsTableListSetup extends javax.swing.JDialog implements DropTargetListener {

    private int returnStatus = RET_CANCEL;
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    public PropertyEditor editor;
    private boolean keyPressed = false;
    private boolean populateFromCurrentRecord = false;
    private boolean calledFromPropertyEditor = false;
    private HashMap<String, String> validationData;
    private TableModelListener criteriaTableListener = null;
    private TableModelListener displayColumnsTableListener = null;
    private TableModelListener validationListTableListener = null;
    private boolean editing = false;
    //Ticket #410
    //Used to stored currently selected validation id when form opened
    private String currentValidationId = "";
    //Ticket #470
    private SQLTableVisualComponent.SQLTableVisualEvent removeEvent = null;

    /**
     * 
     * @return
     */
    public HashMap<String, String> getValidationPropertyData() {
        String id = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 0);
        String name = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 1);
        String storedColumnName = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 3);
        String criteria = ((QueryTableModel) validationsListItems.getModel()).getCriteriaProperty();
        String order = ((QueryTableModel) validationsListItems.getModel()).getOrderbyProperty();
        HashMap<String, String> data = new HashMap();
        data.put("id", id);
        data.put("name", name);
        data.put("storedColumnName", storedColumnName);
        data.put("criteria", criteria);
        data.put("order", order);
        return data;
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        if (!(src.getText().equals(""))) {
            src.setText(src.getText() + ",");
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        //Ticket #48 - prevent adding the same string on a textField multiple times
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        if (src == listOrderBy) {
            try {
                Transferable tr = dtde.getTransferable();
                String s = tr.getTransferData(DataFlavor.stringFlavor).toString();
                if (src.getText().contains(s)) {
                    dtde.rejectDrag();
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        } else if (src == listStoredCol) {
            if (!src.getText().equals("")) {
                dtde.rejectDrag();
            }
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * 
     * @param dte
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        JTextField src = (JTextField) ((DropTarget) dte.getSource()).getComponent();
        if ((!(src.getText().equals(""))) && (src.getText().endsWith(",")) && ((DropTarget) (dte.getSource())).isActive()) {
            src.setText(src.getText().substring(0, src.getText().length() - 1));
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
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
                    storedListModels = ((ValidationsTableListModel) table.getModel()).getStoredListModels();
                    if (table.getSelectedRow() > table.getModel().getRowCount()-1) {
                        uid = "";
                    } else {
                        uid = (String) ((ValidationsTableListModel) table.getModel()).getValueAt(table.getSelectedRow(), 0);
                    }
                    if (storedListModels.containsKey(uid)) {
                        QueryTableModel storedModel = (QueryTableModel) storedListModels.get(uid);
                        validationsListItems.setModel(storedModel);
                        dataSQLEdit.setText(((QueryTableModel) validationsListItems.getModel()).getRuntimeSQL());

                        // This sets the query and rebuilds the criteria and display column models
                        executeQuery(dataSQLEdit.getText());

                        // Populate checkboxes for display here
                        for (int j = 0; j < validationsListDisplayColumns.getColumnCount(); j++) {
                            ValidationsTableDisplayColumnsModel dispMod = (ValidationsTableDisplayColumnsModel) validationsListDisplayColumns.getModel();
                            String colName = dispMod.getPhysicalColumn(j);
                            Boolean value = ((QueryTableModel) validationsListItems.getModel()).getIsVisibleColumn(colName);
                            validationsListDisplayColumns.getModel().setValueAt(value, 0, j);
                        }
                        
                    } else if ((uid != null) && (uid.equals(""))) {
                        validationsListItems.setModel(new QueryTableModel(validationData));
                        dataSQLEdit.setText("");
                        validationsListCriteria.setModel(new ValidationsTableCriteriaModel("", null, 0));
                        
                        // Clear the checkboxes until record is created.
                        validationsListDisplayColumns.setModel(new ValidationsTableDisplayColumnsModel("", null, 0));
                    }
                    //Ticket #45, #46, #51
                    // New criteria and display columns models here, so attach listener - old model reference
                    // is gone and should be GC.
                    validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
                    validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
                }
            }
        }
    }

    /**
     * Creates new form ValidDataListSetup
     * @param parent
     * @param modal
     * @param editor
     * @param validationData
     */
    public ValidationsTableListSetup(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor, HashMap<String, String> validationData) {
        super(parent, modal);

        this.validationData = validationData;

        //Ticket #410 - set validation id to be selected and
        //clear the validation data so all the values get reset from db
        if (this.validationData != null) {
            this.currentValidationId = this.validationData.get("id");
            this.validationData.put("order", "");
        }

        if (editor != null) {
            calledFromPropertyEditor = true;
        }
        initComponents();
        validationsList.setRowSorter(null);
        validationsList.setAutoCreateRowSorter(false);
        //Ticket #45, #46, #51
        criteriaTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                int row = validationsListCriteria.getSelectedRow();
                int col = validationsListCriteria.getSelectedColumn();
                if (!((row == -1) || (col == -1))) {
                    String value = (String) validationsListCriteria.getModel().getValueAt(row, col);
                    value = value.trim();

                    // Ticket 33273
                    String error = EchoUtil.dangerousSqlCheck(value);
                    if (!"".equals(error)) {
                        JOptionPane.showMessageDialog(null, error);
                        value = "";
                        validationsListCriteria.getModel().setValueAt(value, row,col);
                    }
                    
                    QueryTableModel qtm = (QueryTableModel) validationsListItems.getModel();
                    qtm.setColumn(col, ((ValidationsTableCriteriaModel) validationsListCriteria.getModel()).getPhysicalColumn(col));
                    qtm.setStoredFilter(row, col, value);
                    dataSQLEdit.setText(SQLBuilder.rebuildWhereClause(dataSQLEdit.getText(), (ValidationsTableCriteriaModel) validationsListCriteria.getModel(), ""));
                    ((ValidationsTableListModel) validationsList.getModel()).editCurrentRecord(dataSQLEdit.getText(), (String) validationsList.getValueAt(validationsList.getSelectedRow(), 2));
                    // New models have been created at this point, so re-attach the listener.
                    validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
                    validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
                }

            }
        };

        validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);

        displayColumnsTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (validationsListDisplayColumns.isFocusOwner()) {
                    QueryTableModel model = (QueryTableModel) validationsListItems.getModel();
                    ValidationsTableDisplayColumnsModel dispMod = (ValidationsTableDisplayColumnsModel) validationsListDisplayColumns.getModel();
                        Boolean val = (Boolean)validationsListDisplayColumns.getValueAt(0, e.getColumn());
                        String colName = dispMod.getPhysicalColumn(e.getColumn());
                        if ((val != null) && (val)) {
                            // Update the runtime SQL when visible columns change
                            model.addVisibleColumn(colName);
                        } else {
                            if (model.getVisibleColumns().size() == 1) {
                                JOptionPane.showMessageDialog(null, SQLBuilder.ONE_VISIBLE_MESSAGE);
                            } else {
                                model.removeVisibleColumn(colName);
                            }
                        }
                    // Update the runtime SQL when visible columns change
                    // Visible columns are rebuilt using the SELECT when it goes through editCurrentRecord/buildRuntimeSQL
                    String newSelect = SQLBuilder.rebuildSelectClause(dataSQLEdit.getText(), ((QueryTableModel)model).getVisibleColumnsAsString());
                    ((ValidationsTableListModel) validationsList.getModel()).editCurrentRecord(newSelect, (String) validationsList.getValueAt(validationsList.getSelectedRow(), 2));
                    dataSQLEdit.setText(((QueryTableModel)model).getRuntimeSQL());
                    // New models have been created at this point, so re-attach the listener.
                    validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
                    validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
                }
            }
            
        };

        validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);

        //Ticket #41
        validationListTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                ValidationsTableListModel m = (ValidationsTableListModel) validationsList.getModel();
                if ((!m.isEditing()) && (!m.isLoading()) && (!editing)) {
                    if (validationsList.getRowCount() == 1) {
                        validationsList.getSelectionModel().setSelectionInterval(0, 0);
                    }
                    int row = validationsList.getSelectedRow();
                    int col = validationsList.getSelectedColumn();
                    if (!((row == -1) || (col == -1))) {
                        if (col < validationsList.getColumnCount() - 1) {
                            populateFromCurrentRecord = false;
                            String uid = (String) m.getValueAt(validationsList.getSelectedRow(), 0);
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

            }
        };

        validationsList.getModel().addTableModelListener(validationListTableListener);
        this.setLocationRelativeTo(null);
        try {
            listOrderBy.getDropTarget().addDropTargetListener(this);
            listStoredCol.getDropTarget().addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            Exceptions.printStackTrace(ex);
        }
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
        JTableHeader displayColumnsHeader = validationsListDisplayColumns.getTableHeader();
        displayColumnsHeader.setOpaque(false);
        displayColumnsHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        displayColumnsHeader.setForeground(Color.white);
        displayColumnsHeader.setBackground(new Color(127, 157, 185));
        displayColumnsHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        if (calledFromPropertyEditor) {
            validationsList.setComponentPopupMenu(null);
            dataSQLEdit.setEditable(false);
            dataSQLEdit.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsList.setColumnSelectionAllowed(false);
            validationsList.setBackground(Color.getColor("EditorPane.disabledBackground"));
            SearchableUtils.installSearchable(validationsList);
            validationsListDisplayColumns.setEnabled(false);
            validationsListDisplayColumns.setBackground(Color.getColor("EditorPane.disabledBackground"));
            validationsListDisplayColumns.setColumnSelectionAllowed(false);
        } else {
            btnCancel.setVisible(false);
        }

        // Disabled until completed...
        btnSQLBuilder.setVisible(false);
        if (dataSQLEdit.isEnabled()) {
            dataSQLEdit.requestFocus();
        }
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
        miEdit = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miDelete = new javax.swing.JMenuItem();
        validDataListWizard = new javax.swing.JDialog();
        jLabel15 = new javax.swing.JLabel();
        tableLayoutPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        listOrderBy = new javax.swing.JTextField();
        listStoredCol = new javax.swing.JTextField();
        tableList = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        btnASC = new javax.swing.JRadioButton();
        btnDESC = new javax.swing.JRadioButton();
        btnWizOk = new javax.swing.JButton();
        btnWizCancel = new javax.swing.JButton();
        btnGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        validationsList = new javax.swing.JTable() {
            public boolean isCellEditable(int row, int column)
            {
                if ((calledFromPropertyEditor) || (column == 2))
                return false;
                else
                return true;
            }

        }
        ;
        validationsList.getSelectionModel().addListSelectionListener(new SelectionListener(validationsList));
        validationsList.setTransferHandler(new TableTransferHandler());
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        dataSQLEdit = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        validationsListItems = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        validationsListCriteria = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        btnSQLBuilder = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        validationsListDisplayColumns = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(mInsert, org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.mInsert.text")); // NOI18N
        mInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mInsertActionPerformed(evt);
            }
        });
        pmDataOptionList.add(mInsert);

        miEdit.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.miEdit.text")); // NOI18N
        miEdit.setName("miEdit"); // NOI18N
        miEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miEditActionPerformed(evt);
            }
        });
        pmDataOptionList.add(miEdit);
        pmDataOptionList.add(jSeparator1);

        miDelete.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.miDelete.text")); // NOI18N
        miDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DataListOptionDelete(evt);
            }
        });
        pmDataOptionList.add(miDelete);

        validDataListWizard.setTitle(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.validDataListWizard.title")); // NOI18N
        validDataListWizard.setMinimumSize(new java.awt.Dimension(624, 447));
        validDataListWizard.setModal(true);
        validDataListWizard.setName("validDataListWizard"); // NOI18N
        validDataListWizard.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                validDataListWizardWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                validDataListWizardWindowOpened(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel15.text")); // NOI18N
        jLabel15.setOpaque(true);

        tableLayoutPanel.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        tableLayoutPanel.setName("tableLayoutPanel"); // NOI18N
        tableLayoutPanel.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentRemoved(java.awt.event.ContainerEvent evt) {
                compRemoved(evt);
            }
        });
        tableLayoutPanel.setLayout(null);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel14.text")); // NOI18N
        jLabel14.setOpaque(true);

        listOrderBy.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.listOrderBy.text")); // NOI18N
        listOrderBy.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.listOrderBy.toolTipText")); // NOI18N
        listOrderBy.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        listOrderBy.setName("listOrderBy"); // NOI18N
        listOrderBy.setVerifyInputWhenFocusTarget(false);
        listOrderBy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listOrderByMouseReleased(evt);
            }
        });
        listOrderBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                listOrderByKeyTyped(evt);
            }
        });

        listStoredCol.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.listStoredCol.text")); // NOI18N
        listStoredCol.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.listStoredCol.toolTipText")); // NOI18N
        listStoredCol.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        listStoredCol.setName("listStoredCol"); // NOI18N
        listStoredCol.setVerifyInputWhenFocusTarget(false);
        listStoredCol.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                listStoredColKeyTyped(evt);
            }
        });

        tableList.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.tableList.toolTipText")); // NOI18N
        tableList.setName("tableList"); // NOI18N
        SearchableUtils.installSearchable(tableList);
        tableList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableListActionPerformed(evt);
            }
        });
        tableList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableListKeyPressed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel24.text")); // NOI18N
        jLabel24.setOpaque(true);

        btnGroup.add(btnASC);
        btnASC.setSelected(true);
        btnASC.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnASC.text")); // NOI18N
        btnASC.setName("btnASC"); // NOI18N

        btnGroup.add(btnDESC);
        btnDESC.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnDESC.text")); // NOI18N
        btnDESC.setName("btnDESC"); // NOI18N

        btnWizOk.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnWizOk.text")); // NOI18N
        btnWizOk.setName("btnWizOk"); // NOI18N
        btnWizOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWizOkActionPerformed(evt);
            }
        });

        btnWizCancel.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnWizCancel.text")); // NOI18N
        btnWizCancel.setName("btnWizCancel"); // NOI18N
        btnWizCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWizCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout validDataListWizardLayout = new javax.swing.GroupLayout(validDataListWizard.getContentPane());
        validDataListWizard.getContentPane().setLayout(validDataListWizardLayout);
        validDataListWizardLayout.setHorizontalGroup(
            validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validDataListWizardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableLayoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validDataListWizardLayout.createSequentialGroup()
                        .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(listStoredCol, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                            .addComponent(listOrderBy, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(validDataListWizardLayout.createSequentialGroup()
                                .addComponent(btnWizOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnWizCancel))
                            .addGroup(validDataListWizardLayout.createSequentialGroup()
                                .addComponent(btnASC, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDESC, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validDataListWizardLayout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableList, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        validDataListWizardLayout.setVerticalGroup(
            validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validDataListWizardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableList, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableLayoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listOrderBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnASC)
                    .addComponent(btnDESC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(validDataListWizardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listStoredCol, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnWizCancel)
                    .addComponent(btnWizOk))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.title")); // NOI18N
        setModal(true);
        setName("validDataListSetup"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        validationsList.setAutoCreateRowSorter(true);
        validationsList.setModel(new com.echoman.designer.components.echocommon.ValidationsTableListModel(validationsList, validationsListItems, validationsListCriteria, validationsListDisplayColumns, new String[]{"UID", "Name", "Description", "Stored Column", "SQL", "DesignColumns"},0,calledFromPropertyEditor,validationData));
        validationsList.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.validationsList.toolTipText")); // NOI18N
        validationsList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        validationsList.setCellSelectionEnabled(true);
        validationsList.setComponentPopupMenu(pmDataOptionList);
        validationsList.setDoubleBuffered(true);
        validationsList.setOpaque(false);
        validationsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsList.getTableHeader().setReorderingAllowed(false);
        validationsList.removeColumn(validationsList.getColumnModel().getColumn(0));
        validationsList.removeColumn(validationsList.getColumnModel().getColumn(3));
        validationsList.removeColumn(validationsList.getColumnModel().getColumn(3));
        validationsList.getColumnModel().getColumn(0).setMinWidth(50);
        validationsList.getColumnModel().getColumn(1).setMinWidth(192);
        validationsList.getColumnModel().getColumn(2).setMinWidth(25);
        validationsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(validationsList);
        validationsList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setAutoscrolls(true);
        jScrollPane3.setHorizontalScrollBar(null);

        dataSQLEdit.setEditable(false);
        dataSQLEdit.setColumns(1);
        dataSQLEdit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        dataSQLEdit.setLineWrap(true);
        dataSQLEdit.setTabSize(4);
        dataSQLEdit.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidDataListSetup.sqlStatement.text")); // NOI18N
        dataSQLEdit.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.dataSQLEdit.toolTipText")); // NOI18N
        dataSQLEdit.setWrapStyleWord(true);
        dataSQLEdit.setDoubleBuffered(true);
        dataSQLEdit.setName("sqlStatement"); // NOI18N
        dataSQLEdit.setVerifyInputWhenFocusTarget(false);
        dataSQLEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DataOptionSQLAdd(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dataSQLEditKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(dataSQLEdit);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel2.text")); // NOI18N

        validationsListItems.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.disabledBackground"));
        validationsListItems.setModel(new QueryTableModel(validationData));
        validationsListItems.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.validationsListItems.toolTipText")); // NOI18N
        validationsListItems.setDoubleBuffered(true);
        validationsListItems.setOpaque(false);
        validationsListItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListItems.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(validationsListItems);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel12.text")); // NOI18N

        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        validationsListCriteria.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.validationsListCriteria.toolTipText")); // NOI18N
        validationsListCriteria.setDoubleBuffered(true);
        validationsListCriteria.setFillsViewportHeight(true);
        validationsListCriteria.setRowSelectionAllowed(false);
        validationsListCriteria.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListCriteria.getTableHeader().setReorderingAllowed(false);
        validationsListCriteria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListCriteriaKeyPressed(evt);
            }
        });
        jScrollPane7.setViewportView(validationsListCriteria);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel3.text")); // NOI18N

        btnSQLBuilder.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnSQLBuilder.text")); // NOI18N
        btnSQLBuilder.setEnabled(false);
        btnSQLBuilder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSQLBuilder.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSQLBuilder.setName("btnSQLBuilder"); // NOI18N
        btnSQLBuilder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLBuilderActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.jLabel5.text")); // NOI18N
        jLabel5.setOpaque(true);

        validationsListDisplayColumns.setToolTipText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.validationsListDisplayColumns.toolTipText")); // NOI18N
        validationsListDisplayColumns.setDoubleBuffered(true);
        validationsListDisplayColumns.setFillsViewportHeight(true);
        validationsListDisplayColumns.setRowSelectionAllowed(false);
        validationsListDisplayColumns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListDisplayColumns.getTableHeader().setReorderingAllowed(false);
        validationsListDisplayColumns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListDisplayColumnsKeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(validationsListDisplayColumns);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQLBuilder, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7)
                    .addComponent(jScrollPane5)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSQLBuilder, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel4.setOpaque(false);

        btnOk.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnOk.text")); // NOI18N
        btnOk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText(org.openide.util.NbBundle.getMessage(ValidationsTableListSetup.class, "ValidationsTableListSetup.btnCancel.text")); // NOI18N
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancel(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(798, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void doCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancel
    doClose(RET_CANCEL);
}//GEN-LAST:event_doCancel

private void DataListOptionDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DataListOptionDelete
    if (!calledFromPropertyEditor) {
        removeDataListItem();
    }
}//GEN-LAST:event_DataListOptionDelete

private void DataOptionSQLAdd(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DataOptionSQLAdd
    if (((evt.getKeyCode() == KeyEvent.VK_ENTER)) && (evt.isShiftDown()) && (!calledFromPropertyEditor)) {
        ((ValidationsTableListModel) validationsList.getModel()).editCurrentRecord(dataSQLEdit.getText(), (String) validationsList.getValueAt(validationsList.getSelectedRow(), 2));
        evt.consume();
    }
}//GEN-LAST:event_DataOptionSQLAdd

    /**
     *
     * @param sql
     */
    private void executeQuery(String sql) {
        if (((QueryTableModel) validationsListItems.getModel()).setQuery(sql)) {
            SQLBuilder.buildCriteriaModel(validationsListItems, validationsListCriteria);
            SQLBuilder.buildDisplayColumnsModel(validationsListItems, validationsListDisplayColumns);
        }
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

    /**
     *
     */
    private void startWizard() {
        validDataListWizard.setLocationRelativeTo(null);

        populateFromCurrentRecord = false;
        String uid = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 0);
        if (!((uid == null) || ("".equals(uid)))) {
            populateFromCurrentRecord = true;
        }

        if (populateFromCurrentRecord) {
            populateFromCurrentRecord();
        }

        String sql = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 4);
        boolean editData = (sql == null) || ("".equals(sql));
        tableList.setEnabled(editData);
        validDataListWizard.setVisible(true);
        if (editData) {
            editing = true;
            try {
                ((ValidationsTableListModel) validationsList.getModel()).setValueAt(dataSQLEdit.getText(), validationsList.getSelectedRow(), 4);
            } finally {
                editing = false;
            }
        }
        tableList.requestFocus();
    }

    //Ticket #470
    private SQLTableVisualComponent.SQLTableVisualEvent createSQLTableRemoveEvent() {
        if (removeEvent == null) {
            removeEvent = new SQLTableVisualComponent.SQLTableVisualEvent() {

                @Override
                public void doEvent() {
                    tableList.setEnabled(true);
                    tableList.setSelectedIndex(0);
                }
            };
        }
        return removeEvent;
    }
    
    /**
     * Fill in the table selection wizard screen with current data
     */
    private void populateFromCurrentRecord() {
        HashMap<String, String> sqlParts = ((QueryTableModel) validationsListItems.getModel()).getSqlParts();
        int orderEndIndex = -1;
        String selectedColumns = "";
        String tableName = "";
        boolean foundDescMatch = false;
        boolean foundAscMatch = false;
        if (!((sqlParts == null) || (sqlParts.isEmpty()))) {
            // THIS NEEDS TO BE THE DESIGN COLUMNS, NOT FROM SELECT
            selectedColumns = ((QueryTableModel) validationsListItems.getModel()).getDesignTimeColumnsAsString();
            tableName = sqlParts.get("FROM");
            if (!(sqlParts.get("ORDER BY") == null)) {
                try {
                    Pattern regex = Pattern.compile("(?i)\\b(?:DESC)\\b");
                    Matcher regexMatcher = regex.matcher(sqlParts.get("ORDER BY"));
                    foundDescMatch = regexMatcher.find();
                    if (!foundDescMatch) {
                        regex = Pattern.compile("(?i)\\b(?:ASC)\\b");
                        regexMatcher = regex.matcher(sqlParts.get("ORDER BY"));
                        foundAscMatch = regexMatcher.find();
                    }
                } catch (PatternSyntaxException ex) {
                    // Syntax error in the regular expression
                    JOptionPane.showMessageDialog(null, ex);
                }
                if (foundAscMatch) {
                    orderEndIndex = sqlParts.get("ORDER BY").indexOf(" ASC");
                    btnASC.setSelected(true);
                } else if (foundDescMatch) {
                    orderEndIndex = sqlParts.get("ORDER BY").indexOf("DESC");
                    btnDESC.setSelected(true);
                } else {
                    orderEndIndex = sqlParts.get("ORDER BY").length();
                }
                listOrderBy.setText(sqlParts.get("ORDER BY").substring(0, orderEndIndex));
            }
        }
        try {
            listStoredCol.setText((String) validationsList.getValueAt(validationsList.getSelectedRow(), 2));
            if ((tableName != null) && (!tableName.equals(""))) {
                //Ticket #341
                if (tableName.indexOf(" ") == -1) {
                    SQLBuilder.addVisualTable(tableLayoutPanel, tableName, 
                            selectedColumns, 300, 250, createSQLTableRemoveEvent());
                } else {
                    SQLBuilder.addVisualTable(tableLayoutPanel, 
                            tableName.substring(0, tableName.indexOf(" ")), 
                            selectedColumns, 300, 250, createSQLTableRemoveEvent());
                }
            }
        } catch (Exception ex) {
        }

    }

private void dataSQLEditKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataSQLEditKeyTyped
}//GEN-LAST:event_dataSQLEditKeyTyped

private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
    //Ticket #379
    stopEditing(validationsList);
    stopEditing(validationsListCriteria);
    stopEditing(validationsListItems);
    doClose(RET_OK);
}//GEN-LAST:event_btnOkActionPerformed

private void validationsListCriteriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListCriteriaKeyPressed
}//GEN-LAST:event_validationsListCriteriaKeyPressed

private void tableListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableListActionPerformed
    if (!(keyPressed)) {
        //Ticket #470
        if (SQLBuilder.addVisualTable(tableLayoutPanel, 
                (String) tableList.getSelectedItem(), "", 300, 250, 
                createSQLTableRemoveEvent()) != null) {
            tableList.setEnabled(false);
        }
    }
    keyPressed = false;
}//GEN-LAST:event_tableListActionPerformed

private void tableListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableListKeyPressed
    keyPressed = true;
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        //Ticket #470
        if (SQLBuilder.addVisualTable(tableLayoutPanel, 
                (String) tableList.getSelectedItem(), "", 300, 250, 
                createSQLTableRemoveEvent()) != null) {
            tableList.setEnabled(false);
        }
        keyPressed = false;
    }
}//GEN-LAST:event_tableListKeyPressed

private void validDataListWizardWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_validDataListWizardWindowOpened
    // This is only called the first time the window is opened.
    SQLBuilder.AddTables(tableList);
}//GEN-LAST:event_validDataListWizardWindowOpened

private void btnSQLBuilderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLBuilderActionPerformed
    SQLBuilder sql = new SQLBuilder(null, true, null, dataSQLEdit.getText()) {

        @Override
        public void dispose() {
            if (getReturnStatus() == RET_OK) {
                dataSQLEdit.setText(getSQL());
                executeQuery(dataSQLEdit.getText());
            }
            super.dispose();
        }
    };
    sql.setLocationRelativeTo(null);
    sql.setVisible(true);
}//GEN-LAST:event_btnSQLBuilderActionPerformed

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

                if (validationsList.getRowCount() == 1) {
                    validationsListItems.setModel(new QueryTableModel(validationData));
                    dataSQLEdit.setText("");
                    validationsListCriteria.setModel(new ValidationsTableCriteriaModel("", null, 0));

                    // Clear the checkboxes until record is created.
                    validationsListDisplayColumns.setModel(new ValidationsTableDisplayColumnsModel("", null, 0));
                }

                ((ValidationsTableListModel) validationsList.getModel()).removeRow(rowToDelete);
                
            } finally {
                editing = false;
            }
        }
    }

private void btnWizOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWizOkActionPerformed

    boolean success = false;
    String sqlText = "";
    if ((listStoredCol.getText() == null) || (listStoredCol.getText().equals(""))) {
        JOptionPane.showMessageDialog(null, "Please select a stored column.");
    } else {
        try {
            sqlText = SQLBuilder.createSQLStatement(tableLayoutPanel, listOrderBy.getText(), btnDESC.isSelected(), "");
            if (validationsListCriteria.getModel() instanceof ValidationsTableCriteriaModel) {
                dataSQLEdit.setText(SQLBuilder.rebuildWhereClause(sqlText, (ValidationsTableCriteriaModel) validationsListCriteria.getModel(), ""));
            } else {
                dataSQLEdit.setText(sqlText);
            }

            // Clear the current design time columns because it's changing.
            HashMap<String, TableModel> storedListModels = ((ValidationsTableListModel) validationsList.getModel()).getStoredListModels();
            String uid = (String) ((ValidationsTableListModel) validationsList.getModel()).getValueAt(validationsList.getSelectedRow(), 0);
            if (storedListModels.containsKey(uid)) {
                QueryTableModel storedModel = (QueryTableModel) storedListModels.get(uid);
                storedModel.setDesignTimeColumns(null);
            }

            success = ((ValidationsTableListModel) validationsList.getModel()).editCurrentRecord(dataSQLEdit.getText(), listStoredCol.getText());
            // Possible new criteria and display columns models here
            validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
            validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
        } finally {
            if (success) {
                validDataListWizard.setVisible(false);
                clearValidDataListWizard();
                populateFromCurrentRecord = false;
            }
        }
    }
}//GEN-LAST:event_btnWizOkActionPerformed

private void btnWizCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWizCancelActionPerformed
    validDataListWizard.setVisible(false);
    clearValidDataListWizard();
}//GEN-LAST:event_btnWizCancelActionPerformed

private void validDataListWizardWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_validDataListWizardWindowClosed
    clearValidDataListWizard();
}//GEN-LAST:event_validDataListWizardWindowClosed

private void listOrderByMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listOrderByMouseReleased
}//GEN-LAST:event_listOrderByMouseReleased

private void validationsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListKeyPressed
    if ((evt.getKeyCode() == KeyEvent.VK_INSERT) && (!calledFromPropertyEditor)) {
        ((ValidationsTableListModel)validationsList.getModel()).createNewDataListItem();
    } else if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && (!calledFromPropertyEditor)) {
        removeDataListItem();

    } else if ((!calledFromPropertyEditor) && ((evt.getKeyCode() == KeyEvent.VK_TAB) && (validationsList.getSelectedColumn() == validationsList.getColumnCount() - 2))) {
        // This will stop the editing in case they haven't moved out of the cell.
        // It retains the value that was input.
        editing = true;
        try {
            stopEditing(validationsList);
            evt.consume();
            dataSQLEdit.requestFocusInWindow();
            startWizard();
        } finally {
            editing = false;
        }
    } else if (calledFromPropertyEditor && (evt.getKeyCode() == KeyEvent.VK_ENTER)) {
        evt.consume();
    }
}//GEN-LAST:event_validationsListKeyPressed

private void miEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miEditActionPerformed
    startWizard();
}//GEN-LAST:event_miEditActionPerformed

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    validationsList.requestFocusInWindow();
    if ((calledFromPropertyEditor) && (validationsList.getRowCount() > 0)) {
        if (!(validationData == null)) {
            if (!(currentValidationId.equals(""))) {
                for (int i = 0; i < validationsList.getRowCount(); i++) {
                    if (((String) (validationsList.getModel().getValueAt(i, 0))).equals(currentValidationId)) {
                        validationsList.setRowSelectionInterval(i, i);
                        break;
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

private void listOrderByKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listOrderByKeyTyped
}//GEN-LAST:event_listOrderByKeyTyped

private void listStoredColKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listStoredColKeyTyped
}//GEN-LAST:event_listStoredColKeyTyped
//Ticket #341
private void compRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_compRemoved
    if (tableLayoutPanel.getComponentCount() == 0) {
        tableList.setEnabled(true);
        listOrderBy.setText("");
        listStoredCol.setText("");
    }
}//GEN-LAST:event_compRemoved

    private void validationsListDisplayColumnsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListDisplayColumnsKeyPressed
    }//GEN-LAST:event_validationsListDisplayColumnsKeyPressed

    private void mInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mInsertActionPerformed
        ((ValidationsTableListModel)validationsList.getModel()).createNewDataListItem();
    }//GEN-LAST:event_mInsertActionPerformed

    /**
     *
     */
    private void clearValidDataListWizard() {
        if (tableList.getItemCount() > 0) {
            tableList.setSelectedIndex(0);
        }
        tableLayoutPanel.removeAll();
        listStoredCol.setText("");
        listOrderBy.setText("");
        btnASC.setSelected(true);
        btnDESC.setSelected(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnASC;
    private javax.swing.JButton btnCancel;
    private javax.swing.JRadioButton btnDESC;
    private javax.swing.ButtonGroup btnGroup;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSQLBuilder;
    private javax.swing.JButton btnWizCancel;
    private javax.swing.JButton btnWizOk;
    private javax.swing.JTextArea dataSQLEdit;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextField listOrderBy;
    private javax.swing.JTextField listStoredCol;
    private javax.swing.JMenuItem mInsert;
    private javax.swing.JMenuItem miDelete;
    private javax.swing.JMenuItem miEdit;
    private javax.swing.JPopupMenu pmDataOptionList;
    private javax.swing.JPanel tableLayoutPanel;
    private javax.swing.JComboBox tableList;
    private javax.swing.JDialog validDataListWizard;
    private javax.swing.JTable validationsList;
    private javax.swing.JTable validationsListCriteria;
    private javax.swing.JTable validationsListDisplayColumns;
    private javax.swing.JTable validationsListItems;
    // End of variables declaration//GEN-END:variables
}
