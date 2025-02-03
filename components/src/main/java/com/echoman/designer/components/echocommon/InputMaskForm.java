/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import org.openide.util.Exceptions;

/**
 *
 * @author  david.morin
 */
public class InputMaskForm extends javax.swing.JDialog implements TableModelListener {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    public PropertyEditor editor;
    private MaskFormatter phoneMask;
    private MaskFormatter ssnMask;
    private MaskFormatter dateMask;
    private MaskFormatter zipMask;
    private MaskFormatter upperMask;
    private MaskFormatter lowerMask;
    private MaskFormatter properMask;
    private MaskFormatter customMask;
    private String inputMask = "";

    /**
     * Creates new form TablesColumnsForm
     * @param parent
     * @param modal
     * @param editor
     */
    public InputMaskForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor) {
        super(parent, modal);
        this.editor = editor;
        try {
            phoneMask = new MaskFormatter("(###)###-####");
            ssnMask = new MaskFormatter("###-##-####");
            zipMask = new MaskFormatter("#####-####");
            dateMask = new MaskFormatter("##/##/####");
            upperMask = new MaskFormatter("UUUUUUUUUUUUUUU");
            lowerMask = new MaskFormatter("LLLLLLLLLLLLLLL");
            properMask = new MaskFormatter("ULLLLLLLLLLLLLL");
            properMask.setInvalidCharacters(" ");
            customMask = new MaskFormatter("***************");
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
        initComponents();
        setLocationRelativeTo(null);
        JTableHeader listHeader = maskList.getTableHeader();
        listHeader.setOpaque(false);
        listHeader.setFont(new Font("Tahoma", Font.BOLD, 11));
        listHeader.setForeground(Color.white);
        listHeader.setBackground(new Color(127, 157, 185));
        listHeader.setBorder(new LineBorder(new Color(127, 157, 185)));
        maskList.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        maskList.setColumnSelectionAllowed(false);
        maskList.setRowSelectionAllowed(true);
        maskList.getModel().addTableModelListener(this);
        setCurrentMask();
        testInput.requestFocus();
    }

    public InputMaskForm(java.awt.Frame parent, String mask) {
        this(parent, true, null);
        this.inputMask = mask;
        setCurrentMask();
        testInput.requestFocus();
    }

    private void setCurrentMask() {
        int idx = -1;
        String mask;
        if (this.editor == null)
            mask = this.inputMask;
        else
            mask = this.editor.getAsText();
        if ((mask != null) && (!mask.equals(""))) {
            for (int i = 0; i < maskList.getModel().getRowCount(); i++) {
                String rowMask = maskList.getModel().getValueAt(i, 1).toString();
                idx = i;
                if (rowMask.equals(mask)) {
                    break;
                }
            }
        }
        if (idx > -1) {
            //if it is the last row then it is the custom mask
            if (idx == maskList.getModel().getRowCount() - 1) {
                maskList.getModel().setValueAt(mask, idx, 1);
            }
            maskList.setRowSelectionInterval(idx, idx);
        }
    }

    /**
     * 
     * @returnthe return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * 
     * @param e
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        if ((e.getFirstRow() == 7) && (e.getColumn() == 1)) {
            testInput.setText("");
            testInput.setValue("");
            testInput.setFormatterFactory(null);
            try {
                customMask.setMask((String) maskList.getValueAt(7, 1));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Invalid mask.");
            } catch (Exception ex1) {
                JOptionPane.showMessageDialog(null, "Invalid mask.");
            }
            testInput.setFormatterFactory(new DefaultFormatterFactory(customMask, customMask, customMask));
            testInput.requestFocusInWindow();
        }
    }

    /**
     * 
     */
    public class SelectionListener implements ListSelectionListener {

        JTable table;
        int modelIndex;

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
                if (table.equals(maskList)) {
                    testInput.setText("");
                    testInput.setValue("");
                    testInput.setFormatterFactory(null);
                    if (!(table.getRowSorter() == null)) {
                        modelIndex = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
                    } else {
                        modelIndex = table.getSelectedRow();
                    }
                    switch (modelIndex) {
                        case 0:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(phoneMask, phoneMask, phoneMask));
                            break;
                        case 1:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(ssnMask, ssnMask, ssnMask));
                            break;
                        case 2:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(zipMask, zipMask, zipMask));
                            break;
                        case 3:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(dateMask, dateMask, dateMask));
                            break;
                        case 4:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(lowerMask, lowerMask, lowerMask));
                            break;
                        case 5:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(upperMask, upperMask, upperMask));
                            break;
                        case 6:
                            testInput.setFormatterFactory(new DefaultFormatterFactory(properMask, properMask, properMask));
                            break;
                        case 7:
                            try {
                                customMask.setMask((String) table.getValueAt(table.getSelectedRow(), 1));
                            } catch (ParseException ex) {
                                JOptionPane.showMessageDialog(null, "Invalid mask.");
                            } catch (Exception ex1) {
                                JOptionPane.showMessageDialog(null, "Invalid mask.");
                            }
                            testInput.setFormatterFactory(new DefaultFormatterFactory(customMask, customMask, customMask));
                            break;
                        default:
                            break;
                    }
                    testInput.requestFocusInWindow();
                }
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

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        testInput = new JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        maskList = new javax.swing.JTable() {
            public boolean isCellEditable(int row, int column)
            {
                if ((row == 7) && (column == 1))
                return true;
                else
                return false;
            }
        };
        maskList.getSelectionModel().addListSelectionListener(new SelectionListener(maskList));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Input Mask");
        setAlwaysOnTop(true);
        setModal(true);
        setName("inputtMaskForm"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "TablesColumnsForm.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "TablesColumnsForm.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        testInput.setText(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "InputMaskForm.testInput.text")); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "InputMaskForm.jLabel1.text")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(441, 220));

        maskList.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.disabledBackground"));
        maskList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        maskList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Phone", "(###)###-####"},
                {"SSN", "###-##-####"},
                {"Long Zip", "#####-####"},
                {"Date", "##/##/####"},
                {"Lower Case", "L"},
                {"Upper Case", "U"},
                {"Proper Case", "P"},
                {"Custom", ""}
            },
            new String [] {
                "Name", "Mask"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        maskList.setColumnSelectionAllowed(true);
        maskList.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        maskList.setDoubleBuffered(true);
        maskList.setMinimumSize(new java.awt.Dimension(30, 210));
        maskList.setOpaque(false);
        maskList.setPreferredSize(new java.awt.Dimension(0, 239));
        maskList.getTableHeader().setReorderingAllowed(false);
        maskList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maskListFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(maskList);
        maskList.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (maskList.getColumnModel().getColumnCount() > 0) {
            maskList.getColumnModel().getColumn(0).setResizable(false);
            maskList.getColumnModel().getColumn(0).setPreferredWidth(10);
            maskList.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "InputMaskForm.maskList.columnModel.title0")); // NOI18N
            maskList.getColumnModel().getColumn(1).setResizable(false);
            maskList.getColumnModel().getColumn(1).setPreferredWidth(200);
            maskList.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(InputMaskForm.class, "InputMaskForm.maskList.columnModel.title1")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(testInput, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                        .addGap(42, 42, 42)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void doAcceptValue(String mask) {
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (this.editor != null)
            this.editor.setAsText((String) maskList.getValueAt(maskList.getSelectedRow(), 1));
        doAcceptValue((String) maskList.getValueAt(maskList.getSelectedRow(), 1));
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void maskListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maskListFocusLost
        
    }//GEN-LAST:event_maskListFocusLost

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable maskList;
    private javax.swing.JButton okButton;
    private javax.swing.JFormattedTextField testInput;
    // End of variables declaration//GEN-END:variables
    /**
     * 
     */
    private int returnStatus = RET_CANCEL;
}
