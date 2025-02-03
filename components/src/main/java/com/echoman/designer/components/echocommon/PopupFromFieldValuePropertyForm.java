/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.jdesi.FormData;
import com.echoman.jdesi.PopupFromFieldProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditorSupport;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import org.openide.nodes.Node;

import static java.util.Comparator.comparing;

/**
 *
 * @author  david.morin
 */
public class PopupFromFieldValuePropertyForm extends javax.swing.JDialog{
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    public PropertyEditorSupport editor;
    private boolean isLoading = false;
    
    private class EchoJTable extends JTable {

        @Override
        public TableCellEditor getCellEditor(int row, int column) {
            if (column == 2) {
                popupFormProperties.getColumn("Popup Form Link Field").setCellEditor(null);
                populatePopupFormLinkField(row);
                return new DefaultCellEditor(popupFormLinkField);
            } else {
                return super.getCellEditor(row, column);
            }
        }
    
    }
    
    /**
     * Creates new form TablesColumnsForm
     * @param parent
     * @param modal
     * @param editor
     */
    public PopupFromFieldValuePropertyForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor) {
        super(parent, modal);        
        this.editor = editor;
        initComponents();
        popupFormName.setRenderer(new BasicComboBoxRenderer() {
            // Custom renderer to display form name, not id in combobox.
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FormData) {
                    setText(((FormData) value).getFormName());
                }
                return this;
            }
            
        });
        popupFormProperties.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            // Custom renderer to display form name, not id in table cell.
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof FormData) {
                    setText(((FormData) value).getFormName());
                }
                return this;
            }
            
        });
        JTableHeader listHeader = popupFormProperties.getTableHeader();
        listHeader.setOpaque(false);
        listHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        listHeader.setForeground(Color.white);
        listHeader.setBackground( new Color(127,157,185));
        ((DefaultTableCellRenderer)listHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        // Doing just this one here with only row selection allowed in the 
        // table properties will give you a single highlighted cell.
        popupFormProperties.setCellSelectionEnabled(true);
        populatePopupFormProperties();
        if (popupFormProperties.getRowCount() > 0) {
            populateComboboxes();
        }
    }

    private void populateComboboxes() {
        isLoading = true;
        try {
            populateFormNames();
            populateCurrentFormLinkField();
            populatePopupFormLinkField(0);
        } finally {
            isLoading = false;
        }
    }

    private void populateFormNames() {
        try {
            List<FormData> forms = new ArrayList();
            forms.addAll(DBConnections.getForms());
            forms.addAll(DBConnections.getFdNextForms());
            popupFormName.removeAllItems();
            forms.stream()
                    .sorted(comparing(FormData::getFormName))
                    .forEach(form -> popupFormName.addItem(form));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "The list of forms could not be retrieved from the database." + "\n" +
                    ex.getMessage());
        }
    }

    private void populateCurrentFormLinkField() {
        Node[] comps = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        for (Node node : comps) {
            if ((node instanceof EchoBaseNode) &&
                    ((EchoBaseNode)node).getNodeData() instanceof IEchoDataAwareComponentNodeData) {
                String table = ((IEchoDataAwareComponentNodeData)((EchoBaseNode)node).getNodeData()).getTable();
                try {
                    ArrayList<String> columns = DBConnections.getTableColumns(table);
                    currentFormLinkField.removeAllItems();
                    columns.forEach((col)->currentFormLinkField.addItem(col));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "The list of table columns could not be retrieved from the database." + "\n" +
                            ex.getMessage());
                }
            }
        }
    }

    private void populatePopupFormLinkField(int row) {
        isLoading = true;
        try {
            Node[] comps = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
            for (Node node : comps) {
                if ((node instanceof EchoBaseNode) &&
                        ((EchoBaseNode)node).getNodeData() instanceof IEchoDataAwareComponentNodeData) {
                    if (!(popupFormProperties.getSelectedRow() == -1)
                            && (popupFormProperties.getValueAt(row, 0) instanceof FormData)) {
                        String table = ((FormData)popupFormProperties.getValueAt(row, 0)).getLinkTableName();
                        try {
                            ArrayList<String> columns = DBConnections.getTableColumns(table);
                            popupFormLinkField.removeAllItems();
                            columns.forEach((col)->popupFormLinkField.addItem(col));
                            popupFormProperties.repaint();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "The list of table columns could not be retrieved from the database." + "\n" +
                                    ex.getMessage());
                        }
                    }
                }
            }
        } finally {
            isLoading = false;
        }
    }
        
    private void populatePopupFormProperties() {
        ArrayList<PopupFromFieldProperties> records = (ArrayList<PopupFromFieldProperties>)editor.getValue();
        if (records.size() <= 0) {
            insertItem();
        } else {
            for (PopupFromFieldProperties prop : records) {
                insertItem();
                populateRow(prop, popupFormProperties.getSelectedRow());
            }
        }
    }

    private void populateRow(PopupFromFieldProperties prop, int row) {
        popupFormProperties.getModel().setValueAt(prop.getPopupForm(), row, 0);
        popupFormProperties.getModel().setValueAt(prop.getCurrentFormLinkField(), row, 1);
        popupFormProperties.getModel().setValueAt(prop.getPopupFormLinkField(), row, 2);
        popupFormProperties.getModel().setValueAt(prop.getCurrentFormLinkValue(), row, 3);
        popupFormProperties.getModel().setValueAt(prop.getTabOrSave(), row, 4);
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

        saveOrTab = new javax.swing.JComboBox<>();
        popupFormName = new javax.swing.JComboBox<>();
        currentFormLinkField = new javax.swing.JComboBox<>();
        popupFormLinkField = new javax.swing.JComboBox<>();
        tablePopupMenu = new javax.swing.JPopupMenu();
        menuItemInsert = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemDelete = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        popupFormProperties = new EchoJTable();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        saveOrTab.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Save", "Tab" }));
        saveOrTab.setDoubleBuffered(true);

        popupFormName.setModel(new DefaultComboBoxModel<FormData>());
        popupFormName.setDoubleBuffered(true);
        popupFormName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                popupFormNameItemStateChanged(evt);
            }
        });
        popupFormName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                popupFormNamePropertyChange(evt);
            }
        });

        currentFormLinkField.setModel(new DefaultComboBoxModel<String>()
        );
        currentFormLinkField.setDoubleBuffered(true);

        popupFormLinkField.setModel(new DefaultComboBoxModel<String>());
        popupFormLinkField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        popupFormLinkField.setDoubleBuffered(true);

        menuItemInsert.setText(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.menuItemInsert.text")); // NOI18N
        menuItemInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemInsertActionPerformed(evt);
            }
        });
        tablePopupMenu.add(menuItemInsert);
        tablePopupMenu.add(jSeparator1);

        menuItemDelete.setText(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.menuItemDelete.text")); // NOI18N
        menuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeleteActionPerformed(evt);
            }
        });
        tablePopupMenu.add(menuItemDelete);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.title")); // NOI18N
        setAlwaysOnTop(true);
        setBackground(java.awt.Color.white);
        setModal(true);
        setName("PopupFromFieldPropertyForm"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(441, 220));

        popupFormProperties.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        popupFormProperties.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Popup Form", "Current Form Link Field", "Popup Form Link Field", "Current Form Link Value", "Save/Tab"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        popupFormProperties.setColumnSelectionAllowed(true);
        popupFormProperties.setComponentPopupMenu(tablePopupMenu);
        popupFormProperties.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        popupFormProperties.setDoubleBuffered(true);
        popupFormProperties.setMinimumSize(new java.awt.Dimension(30, 290));
        popupFormProperties.setOpaque(false);
        popupFormProperties.setPreferredSize(null);
        popupFormProperties.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        popupFormProperties.getTableHeader().setReorderingAllowed(false);
        popupFormProperties.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                popupFormPropertiesKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(popupFormProperties);
        popupFormProperties.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (popupFormProperties.getColumnModel().getColumnCount() > 0) {
            popupFormProperties.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.popupFormProperties.columnModel.title0")); // NOI18N
            popupFormProperties.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(popupFormName));
            popupFormProperties.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.popupFormProperties.columnModel.title1")); // NOI18N
            popupFormProperties.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(currentFormLinkField));
            popupFormProperties.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.popupFormProperties.columnModel.title2")); // NOI18N
            popupFormProperties.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(popupFormLinkField));
            popupFormProperties.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.popupFormProperties.columnModel.title3")); // NOI18N
            popupFormProperties.getColumnModel().getColumn(4).setMinWidth(70);
            popupFormProperties.getColumnModel().getColumn(4).setPreferredWidth(70);
            popupFormProperties.getColumnModel().getColumn(4).setMaxWidth(70);
            popupFormProperties.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.popupFormProperties.columnModel.title4")); // NOI18N
            popupFormProperties.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(saveOrTab)
            );
        }

        okButton.setText(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(PopupFromFieldValuePropertyForm.class, "PopupFromFieldValuePropertyForm.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(534, 534, 534)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap(37, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ArrayList<PopupFromFieldProperties> records = new ArrayList<>();
        for (int row = 0; row < popupFormProperties.getModel().getRowCount(); ++row) {
            FormData col0 = (FormData)popupFormProperties.getModel().getValueAt(row, 0);
            String col1 = (String)popupFormProperties.getModel().getValueAt(row, 1);
            String col2 = (String)popupFormProperties.getModel().getValueAt(row, 2);
            String col3 = (String)popupFormProperties.getModel().getValueAt(row, 3);
            String col4 = (String)popupFormProperties.getModel().getValueAt(row, 4);

            if ((col0 == null) || col1.isEmpty() || col2.isEmpty() || col3.isEmpty() || col4.isEmpty()) {
                JOptionPane.showMessageDialog(popupFormProperties, "Please enter all values.");
                return;
            }
            
            PopupFromFieldProperties props = new PopupFromFieldProperties();
            props.setPopupForm(((FormData)popupFormProperties.getModel().getValueAt(row, 0)));

            Node[] comps = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
            for (Node node : comps) {
                if ((node instanceof EchoBaseNode) &&
                        ((EchoBaseNode)node).getNodeData() instanceof IEchoDataAwareComponentNodeData) {
                    props.setCurrentFormLinkFieldContainer(((IEchoDataAwareComponentNodeData)((EchoBaseNode)node).getNodeData()).getParentContainer());
                    props.setCurrentFormLinkFieldTable(((IEchoDataAwareComponentNodeData)((EchoBaseNode)node).getNodeData()).getTable());
                }
            }
            
            props.setCurrentFormLinkField((String)popupFormProperties.getModel().getValueAt(row, 1));
            props.setPopupFormLinkField((String)popupFormProperties.getModel().getValueAt(row, 2));
            props.setCurrentFormLinkValue((String)popupFormProperties.getModel().getValueAt(row, 3));
            props.setTabOrSave((String)popupFormProperties.getModel().getValueAt(row, 4));
            records.add(props);
        }
        
        editor.setValue(records);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void menuItemInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemInsertActionPerformed
        // Insert a new row.
        insertItem();
    }//GEN-LAST:event_menuItemInsertActionPerformed
    
    private void insertItem() {
        // Insert a new row.
        stopEditing(popupFormProperties);
        Object[] rowData = new Object[] {null, "", "", "", ""};
        ((DefaultTableModel)popupFormProperties.getModel()).addRow(rowData);
        popupFormProperties.requestFocus();
        popupFormProperties.setRowSelectionInterval(popupFormProperties.getRowCount()-1 ,
                popupFormProperties.getRowCount()-1);
        popupFormProperties.setColumnSelectionInterval(0, 0);
    }
    
    private void menuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeleteActionPerformed
        // Delete the row.
        removeItem();
    }//GEN-LAST:event_menuItemDeleteActionPerformed

    private void popupFormPropertiesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_popupFormPropertiesKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_INSERT:
                insertItem();
                evt.consume();
                break;
            case KeyEvent.VK_DELETE:
                removeItem();
                evt.consume();
                break;
            case KeyEvent.VK_ENTER:
                popupFormProperties.editCellAt(popupFormProperties.getSelectedRow(), popupFormProperties.getSelectedColumn());
                evt.consume();
                break;
            case KeyEvent.VK_DOWN:
                if (popupFormProperties.isEditing()) {
                    switch (popupFormProperties.getSelectedColumn()) {
                        case 0:
                            if (!popupFormName.isPopupVisible()) {
                                popupFormName.showPopup();
                                popupFormName.requestFocus();
                                evt.consume();
                            }
                            break;
                        case 1:
                            if (!currentFormLinkField.isPopupVisible()) {
                                currentFormLinkField.showPopup();
                                currentFormLinkField.requestFocus();
                                evt.consume();
                            }
                            break;
                        case 2:
                            if (!popupFormLinkField.isPopupVisible()) {
                                popupFormLinkField.showPopup();
                                popupFormLinkField.requestFocus();
                                evt.consume();
                            }
                            break;
                        case 4:
                            if (!saveOrTab.isPopupVisible()) {
                                saveOrTab.showPopup();
                                saveOrTab.requestFocus();
                                evt.consume();
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }//GEN-LAST:event_popupFormPropertiesKeyPressed

    private void popupFormNamePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_popupFormNamePropertyChange
    }//GEN-LAST:event_popupFormNamePropertyChange

    private void popupFormNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_popupFormNameItemStateChanged
        JComboBox cb = (JComboBox) evt.getSource();
        Object item = evt.getItem();
        Object tableValue = popupFormProperties.getValueAt(popupFormProperties.getSelectedRow(), 0);
        
        // Only do this when the user has opened the selection list and is
        // changing the itme.
        if (!isLoading && cb.isPopupVisible() && (evt.getStateChange() == ItemEvent.SELECTED) &&
                ((item != null) && !item.equals(tableValue))) {
            // Update the popup form link field list when the form changes.
            populatePopupFormLinkField(popupFormProperties.getSelectedRow());
            // Clear the cell value when the Form changes because it will be a new list of
            // columns.
            if (!(popupFormProperties.getSelectedRow() == -1)) {
                popupFormProperties.setValueAt("", popupFormProperties.getSelectedRow(), 2);
            }
        }
    }//GEN-LAST:event_popupFormNameItemStateChanged

    private void removeItem() {
        stopEditing(popupFormProperties);
        int deleteRow = popupFormProperties.getSelectedRow();
        if (popupFormProperties.getRowCount() > 0) {
            popupFormProperties.setRowSelectionInterval(0, 0);
            popupFormProperties.setColumnSelectionInterval(0, 0);
        }
        ((DefaultTableModel)popupFormProperties.getModel()).removeRow(deleteRow);
    }

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
     * @param retStatus
     */
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> currentFormLinkField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem menuItemDelete;
    private javax.swing.JMenuItem menuItemInsert;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> popupFormLinkField;
    private javax.swing.JComboBox<FormData> popupFormName;
    private javax.swing.JTable popupFormProperties;
    private javax.swing.JComboBox<String> saveOrTab;
    private javax.swing.JPopupMenu tablePopupMenu;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     */
    private int returnStatus = RET_CANCEL;
}
