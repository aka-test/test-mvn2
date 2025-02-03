/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TableLinkForm.java
 *
 * Created on Dec 8, 2010, 10:28:34 AM
 */
package com.echoman.designer.components.echocommon;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditorSupport;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author U000_hwidja_000U
 */
public class TableLinkForm extends javax.swing.JDialog {

    private static final String LINK_TO = " is linked to ";
    private static final String LINK_USING = " using ";
    private static final String EQUAL = " = ";
    private static final String AND = " and ";
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    private final PropertyEditorSupport editor;
    private int returnStatus = RET_CANCEL;
    private final EchoFormNodeData formNodeData;
    private List<HashMap<String, String>> colIdxList = new ArrayList<HashMap<String, String>>();
    private List<KeyValue> tblIdxList = new ArrayList<KeyValue>();
    private DefaultListModel colModel = new DefaultListModel();
    private DefaultListModel tableModel = new DefaultListModel();
    private boolean showMessage = true;

    private void init() {
        EchoUtil.changeCursorWaitStatus(true);
        try {
            initComboBoxes();
            initLists();
            setMultiEnabled();
            setCreateEchoButtonEnabled();
        } finally {
            EchoUtil.changeCursorWaitStatus(false);
        }
    }

    private void setMultiEnabled() {
        lbl.setVisible(chkmulti.isSelected());
        btnAddCol.setEnabled(chkmulti.isSelected());
        btnRemCol.setEnabled(chkmulti.isSelected());
        lstCol.setEnabled(chkmulti.isSelected());
    }

    private void setCreateEchoButtonEnabled() {
        btnCreateEchoColumns.setEnabled(!hasEchoColumns());
    }

    private boolean hasEchoColumns() {
        boolean bHaveUid = false;
        boolean bHaveCreateUser = false;
        boolean bHaveTouchUser = false;
        boolean bHaveCreateDate = false;
        boolean bHaveTouchDate = false;
        boolean bHaveLinkId = false;
        boolean bHaveSomeEchoCols = true;
        if (cbMaster.getSelectedItem() != null) {
            DatabaseConnection con = DBConnections.getConnection();
            if (!(con == null)) {
                String tableName = cbMaster.getSelectedItem().toString();
                if (!"".equals(tableName)) {
                    String getColTableName = tableName.substring(tableName.indexOf('.') + 1);
                    String getColTableSchema = tableName.substring(0, tableName.indexOf('.'));
                    Connection conn = con.getJDBCConnection();
                    if (!(conn == null)) {
                        try {
                            DatabaseMetaData md = conn.getMetaData();
                            ResultSet rsc = md.getColumns(null, null, getColTableName, null);
                            bHaveSomeEchoCols = false;
                            while (rsc.next()) {
                                String colKey = "";
                                String colName = rsc.getString("COLUMN_NAME");
                                if (!bHaveUid) {
                                    bHaveUid = colName.equals("uid");
                                }
                                if (!bHaveCreateUser) {
                                    bHaveCreateUser = colName.equals("create_user");
                                }
                                if (!bHaveTouchUser) {
                                    bHaveTouchUser = colName.equals("touch_user");
                                }
                                if (!bHaveCreateDate) {
                                    bHaveCreateDate = colName.equals("create_date");
                                }
                                if (!bHaveTouchDate) {
                                    bHaveTouchDate = colName.equals("touch_date");
                                }
                                if (!bHaveLinkId) {
                                    bHaveLinkId = colName.equals("linkid_c");
                                }
                                if (bHaveUid || bHaveCreateUser || bHaveTouchUser || bHaveCreateDate || bHaveTouchDate || bHaveLinkId) {
                                    bHaveSomeEchoCols = true;
                                }
                            }
                        } catch (SQLException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                    }
                }
            }

        }
        return bHaveSomeEchoCols;
    }

    private class KeyValue {

        private String key;
        private String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /** Creates new form TableLinkForm */
    public TableLinkForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor) {
        super(parent, modal);
        this.editor = editor;
        initComponents();
        Node[] ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        formNodeData = (EchoFormNodeData) ((IEchoComponentNode) ary[0]).getNodeData();
        init();
        setLocationRelativeTo(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        cbMaster = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstTbl = new javax.swing.JList();
        btnRemTbl = new javax.swing.JButton();
        btnRepTbl = new javax.swing.JButton();
        btnAddTbl = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cbTable1 = new javax.swing.JComboBox();
        cbTable2 = new javax.swing.JComboBox();
        cbColumn1 = new javax.swing.JComboBox();
        cbColumn2 = new javax.swing.JComboBox();
        chkmulti = new javax.swing.JCheckBox();
        btnAddCol = new javax.swing.JButton();
        btnRemCol = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstCol = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        okbtn = new javax.swing.JButton();
        cancelbtn = new javax.swing.JButton();
        btnCreateEchoColumns = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.title")); // NOI18N
        setIconImage(null);
        setIconImages(null);
        setModal(true);
        setResizable(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jLabel2.text")); // NOI18N

        cbMaster.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbMaster.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMasterItemStateChanged(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jPanel2.border.title"))); // NOI18N

        lstTbl.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstTbl);

        btnRemTbl.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnRemTbl.text")); // NOI18N
        btnRemTbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemTblActionPerformed(evt);
            }
        });

        btnRepTbl.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnRepTbl.text")); // NOI18N
        btnRepTbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepTblActionPerformed(evt);
            }
        });

        btnAddTbl.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnAddTbl.text")); // NOI18N
        btnAddTbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTblActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jPanel1.border.title"))); // NOI18N

        cbTable1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbTable2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbColumn1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbColumn2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        chkmulti.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.chkmulti.text")); // NOI18N
        chkmulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkmultiActionPerformed(evt);
            }
        });

        btnAddCol.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnAddCol.text")); // NOI18N
        btnAddCol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddColActionPerformed(evt);
            }
        });

        btnRemCol.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnRemCol.text")); // NOI18N
        btnRemCol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemColActionPerformed(evt);
            }
        });

        lstCol.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstCol);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jLabel3.text")); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jLabel1.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.jLabel5.text")); // NOI18N

        lbl.setForeground(new java.awt.Color(51, 102, 255));
        lbl.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.lbl.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cbColumn1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                    .addComponent(cbTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkmulti)
                                .addGap(85, 85, 85)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbTable2, 0, 259, Short.MAX_VALUE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(cbColumn2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddCol)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRemCol)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbTable2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbColumn2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(26, 26, 26))
                        .addComponent(cbColumn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chkmulti)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemCol)
                    .addComponent(btnAddCol)
                    .addComponent(lbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(btnAddTbl, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRepTbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemTbl))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddTbl)
                    .addComponent(btnRepTbl)
                    .addComponent(btnRemTbl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        okbtn.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.okbtn.text")); // NOI18N
        okbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okbtnActionPerformed(evt);
            }
        });

        cancelbtn.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.cancelbtn.text")); // NOI18N
        cancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelbtnActionPerformed(evt);
            }
        });

        btnCreateEchoColumns.setText(org.openide.util.NbBundle.getMessage(TableLinkForm.class, "TableLinkForm.btnCreateEchoColumns.text")); // NOI18N
        btnCreateEchoColumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateEchoColumnsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbMaster, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreateEchoColumns, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jSeparator1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(okbtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelbtn)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelbtn, okbtn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbMaster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnCreateEchoColumns))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelbtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelbtn, okbtn});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelbtnActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelbtnActionPerformed

    private void showMessageAndFocus(String msg, JComponent comp) {
        if (showMessage) {
            JOptionPane.showMessageDialog(null, msg);
            if (comp != null) {
                comp.requestFocus();
            }
        }
    }

    private void okbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okbtnActionPerformed
        if (cbMaster.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a master table.", cbMaster);
        } else {
            editor.setAsText(cbMaster.getSelectedItem().toString());
            showMessage = false;
            btnAddTblActionPerformed(null);
            applyLinkTables();
            doClose(RET_OK);
        }
    }//GEN-LAST:event_okbtnActionPerformed

    private void applyLinkTables() {
        formNodeData.getLinkTables().clear();
        if (!tableModel.isEmpty()) {
            for (int i = 0; i < tableModel.size(); i++) {
                KeyValue kv = tblIdxList.get(i);
                String tbl1 = kv.getKey();
                String tbl2 = kv.getValue();
                LinkedHashMap<String, LinkedHashMap<String, String>> tblMap = formNodeData.getLinkTables().get(tbl1);
                if (tblMap == null) {
                    tblMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
                }
                LinkedHashMap<String, String> colMap = tblMap.get(tbl2);
                if (colMap == null) {
                    colMap = new LinkedHashMap<String, String>();
                }
                HashMap<String, String> colIdxMap = colIdxList.get(i);
                for (Map.Entry e : colIdxMap.entrySet()) {
                    colMap.put(e.getKey().toString(), e.getValue().toString());
                }
                tblMap.put(tbl2, colMap);
                formNodeData.getLinkTables().put(tbl1, tblMap);
            }
        }
    }

    private boolean isValidToAddColumn() {
        if (cbTable1.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a table to link.", cbTable1);
            return false;
        } else if (cbTable2.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a table to link.", cbTable2);
            return false;
        } else if (cbColumn1.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a column to link.", cbColumn1);
            return false;
        } else if (cbColumn2.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a column to link.", cbColumn2);
            return false;
        } else if (cbTable1.getSelectedItem().equals(cbTable2.getSelectedItem())) {
            showMessageAndFocus("Unable to link the same table. Please select another table.", cbColumn2);
            return false;
        }
        return true;
    }

    private List<String> getSelectedTables() {
        List<String> tbls = new ArrayList<String>();
        tbls.add(cbMaster.getSelectedItem().toString());
        for (KeyValue kv : tblIdxList) {
            String tbl = kv.getKey();
            if (!tbls.contains(tbl)) {
                tbls.add(tbl);
            }
            tbl = kv.getValue();
            if (!tbls.contains(tbl)) {
                tbls.add(tbl);
            }
        }
        return tbls;
    }

    private boolean isValidLink() {
        List<String> tbls = getSelectedTables();
        String tbl1 = cbTable1.getSelectedItem().toString();
        String tbl2 = cbTable2.getSelectedItem().toString();
        if ((!tbls.contains(tbl1)) && (!tbls.contains(tbl2))) {
            showMessageAndFocus("One of the link table must either the master " +
                    "table or one of the previously selected table.", cbTable1);
            return false;
        }
        return true;
    }

    private boolean isValidToAddTable() {
        if (cbMaster.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a master table before setting links.", cbMaster);
            return false;
        } else if (cbTable1.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a table to link.", cbTable1);
            return false;
        } else if (cbTable2.getSelectedIndex() == 0) {
            showMessageAndFocus("Please select a table to link.", cbTable2);
            return false;
        } else if (cbTable1.getSelectedItem().equals(cbTable2.getSelectedItem())) {
            showMessageAndFocus("Unable to link the same table. Please select another table.", cbTable1);
            return false;
        } else if (!isValidLink()) {
            return false;
        } else if ((colModel.isEmpty()) && ((cbColumn1.getSelectedIndex() == 0)
                || (cbColumn2.getSelectedIndex() == 0))) {
            showMessageAndFocus("Please select link columns.", cbColumn1);
            return false;
        }
        return true;
    }

    private void btnAddColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddColActionPerformed
        if ((isValidToAddColumn()) && (isValidColumn(-1))) {
            ((DefaultListModel) lstCol.getModel()).addElement(
                    cbColumn1.getSelectedItem().toString().concat(EQUAL).
                    concat(cbColumn2.getSelectedItem().toString()));
        }
    }//GEN-LAST:event_btnAddColActionPerformed

    private void btnRemColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemColActionPerformed
        if (lstCol.getSelectedIndex() == -1) {
            showMessageAndFocus("Please select a column entry.", null);
        } else {
            colModel.remove(lstCol.getSelectedIndex());
        }
    }//GEN-LAST:event_btnRemColActionPerformed

    private String getTableStringToAdd(int idx) {
        String s = "";
        if ((isValidToAddTable()) && (isValidTable(idx))) {

            //Ticket #185
            //Make sure the fist table in the join is one of the previously
            //selected tables
            String tbl1 = cbTable1.getSelectedItem().toString();
            String tbl2 = cbTable2.getSelectedItem().toString();
            String col1 = cbColumn1.getSelectedItem().toString();
            String col2 = cbColumn2.getSelectedItem().toString();
            
            List<String> tbls = getSelectedTables();
            if (tbls.contains(tbl2)) {
                cbTable1.setSelectedItem(tbl2);
                cbTable2.setSelectedItem(tbl1);
                cbColumn1.setSelectedItem(col2);
                cbColumn2.setSelectedItem(col1);
            }


            s = cbTable1.getSelectedItem().toString().
                    concat(LINK_TO).concat(cbTable2.getSelectedItem().toString()).concat(LINK_USING);
            //find table link using idx
            KeyValue k = null;
            if (idx == -1) {
                k = new KeyValue(cbTable1.getSelectedItem().toString(),
                        cbTable2.getSelectedItem().toString());
            } else {
                k = tblIdxList.get(idx);
                if (k == null) {
                    k = new KeyValue(cbTable1.getSelectedItem().toString(),
                            cbTable2.getSelectedItem().toString());
                } else {
                    k.setKey(cbTable1.getSelectedItem().toString());
                    k.setValue(cbTable2.getSelectedItem().toString());
                }
            }
            //find columns link using idx
            HashMap<String, String> map = null;
            if (idx == -1) {
                map = new HashMap<String, String>();
            } else {
                map = colIdxList.get(idx);
                if (map == null) {
                    map = new HashMap<String, String>();
                } else {
                    map.clear();
                }
            }
            //if don't have multiple columns
            if (colModel.isEmpty()) {
                s = s.concat(cbColumn1.getSelectedItem().toString()).concat(EQUAL).
                        concat(cbColumn2.getSelectedItem().toString());
                map.put(cbColumn1.getSelectedItem().toString(), cbColumn2.getSelectedItem().toString());
            } else {
                //if have multiple columns then loop thru the columns
                String colStr = "";
                for (int i = 0; i < colModel.size(); i++) {
                    if (!"".equals(colStr)) {
                        colStr = colStr.concat(AND);
                    }
                    String col = colModel.getElementAt(i).toString();
                    String[] cola = col.split(EQUAL);
                    if (cola.length == 2) {
                        map.put(cola[0], cola[1]);
                    }
                    colStr = colStr.concat(col);
                }
                s = s.concat(colStr);
            }

            if (idx == -1) {
                tblIdxList.add(k);
                colIdxList.add(map);
            } else {
                tblIdxList.set(idx, k);
                colIdxList.set(idx, map);
            }

        }
        return s;
    }

    private void btnAddTblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTblActionPerformed
        String s = getTableStringToAdd(-1);
        if (!"".equals(s)) {
            tableModel.addElement(s);
        }
    }//GEN-LAST:event_btnAddTblActionPerformed

    private void btnRepTblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepTblActionPerformed
        if (lstTbl.getSelectedIndex() == -1) {
            showMessageAndFocus("Please select a table entry.", null);
        } else {
            String s = getTableStringToAdd(lstTbl.getSelectedIndex());
            if (!"".equals(s)) {
                tableModel.setElementAt(s, lstTbl.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_btnRepTblActionPerformed

    private void btnRemTblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemTblActionPerformed
        if (lstTbl.getSelectedIndex() == -1) {
            showMessageAndFocus("Please select a table entry.", null);
        } else {
            colIdxList.remove(lstTbl.getSelectedIndex());
            tblIdxList.remove(lstTbl.getSelectedIndex());
            tableModel.remove(lstTbl.getSelectedIndex());
            cbTable1.setSelectedIndex(0);
            cbTable2.setSelectedIndex(0);
            cbColumn1.setSelectedIndex(0);
            cbColumn2.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnRemTblActionPerformed

    private void chkmultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkmultiActionPerformed
        setMultiEnabled();
    }//GEN-LAST:event_chkmultiActionPerformed

    private void btnCreateEchoColumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateEchoColumnsActionPerformed
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            String tableName = "";
            if (cbMaster.getSelectedItem() != null) {
                tableName = cbMaster.getSelectedItem().toString();
            }
            if (!(tableName.equals(""))) {
                Connection conn = con.getJDBCConnection();
                if (!(conn == null)) {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute("alter table " + tableName + " add "
                                + "uid uniqueidentifier DEFAULT NEWID() ROWGUIDCOL NOT NULL PRIMARY KEY, "
                                + "linkid_c varchar(36), "
                                + "create_user varchar(10), "
                                + "touch_user varchar(10), "
                                + "create_date datetime, "
                                + "touch_date datetime");
                        stmt.execute("alter table " + tableName + " alter column "
                                + "create_user varchar(10) NOT NULL");
                        stmt.execute("alter table " + tableName + " alter column "
                                + "touch_user varchar(10) NOT NULL");
                        stmt.execute("alter table " + tableName + " alter column "
                                + "create_date datetime NOT NULL");
                        stmt.execute("alter table " + tableName + " alter column "
                                + "touch_date datetime NOT NULL");
                        stmt.close();
                        setCreateEchoButtonEnabled();
                    } catch (SQLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnCreateEchoColumnsActionPerformed

    private void cbMasterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMasterItemStateChanged
        setCreateEchoButtonEnabled();
    }//GEN-LAST:event_cbMasterItemStateChanged

    private boolean isValidColumn(int excludeIdx) {
        for (int i = 0; i < colModel.size(); i++) {
            if (i != excludeIdx) {
                String s = colModel.getElementAt(i).toString();
                String[] sa = s.split(EQUAL);
                if (sa.length == 2) {
                    if (sa[0].equals(cbColumn1.getSelectedItem().toString())) {
                        showMessageAndFocus("Column [" + sa[0] + "] already selected.", null);
                        return false;
                    }
                    if (sa[1].equals(cbColumn2.getSelectedItem().toString())) {
                        showMessageAndFocus("Column [" + sa[1] + "] already selected.", null);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isValidTable(int excludeIdx) {
        for (int i = 0; i < tableModel.size(); i++) {
            if (i != excludeIdx) {
                KeyValue kv = tblIdxList.get(i);
                if (((kv.getKey().equals(cbTable1.getSelectedItem().toString()))
                        && (kv.getValue().equals(cbTable2.getSelectedItem().toString())))
                        || ((kv.getKey().equals(cbTable2.getSelectedItem().toString()))
                        && (kv.getValue().equals(cbTable1.getSelectedItem().toString())))) {
                    showMessageAndFocus("Link table [" + kv.getKey()
                            + " - " + kv.getValue() + "] is already defined.", null);
                    return false;
                }
            }
        }
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCol;
    private javax.swing.JButton btnAddTbl;
    private javax.swing.JButton btnCreateEchoColumns;
    private javax.swing.JButton btnRemCol;
    private javax.swing.JButton btnRemTbl;
    private javax.swing.JButton btnRepTbl;
    private javax.swing.JButton cancelbtn;
    private javax.swing.JComboBox cbColumn1;
    private javax.swing.JComboBox cbColumn2;
    private javax.swing.JComboBox cbMaster;
    private javax.swing.JComboBox cbTable1;
    private javax.swing.JComboBox cbTable2;
    private javax.swing.JCheckBox chkmulti;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbl;
    private javax.swing.JList lstCol;
    private javax.swing.JList lstTbl;
    private javax.swing.JButton okbtn;
    // End of variables declaration//GEN-END:variables

    private void initColumnComboBoxes(JComboBox combo, String tableName) {
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            combo.removeAllItems();
            combo.addItem("");
            if (!"".equals(tableName)) {
                String getColTableName = tableName.substring(tableName.indexOf('.') + 1);
                String getColTableSchema = tableName.substring(0, tableName.indexOf('.'));
                Connection conn = con.getJDBCConnection();
                if (!(conn == null)) {
                    try {
                        DatabaseMetaData md = conn.getMetaData();
                        ResultSet rsc = md.getColumns(null, getColTableSchema, getColTableName, null);
                        List<String> items = new ArrayList<String>();
                        while (rsc.next()) {
                            items.add(rsc.getString("COLUMN_NAME"));
                        }
                        rsc.close();
                        Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
                        for (String item : items) {
                            combo.addItem(item);
                        }
                        // This connection should not be closed here...it is controlled through the DatabaseExplorer
                        //conn.close();
                    } catch (SQLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    private void initComboBoxes() {
        DatabaseConnection con = DBConnections.getConnection();
        cbMaster.removeAllItems();
        cbTable1.removeAllItems();
        cbTable2.removeAllItems();
        cbColumn1.removeAllItems();
        cbColumn2.removeAllItems();
        cbMaster.addItem("");
        cbTable1.addItem("");
        cbTable2.addItem("");
        cbColumn1.addItem("");
        cbColumn2.addItem("");

        if (!(con == null)) {
            String[] tableTypes = {"TABLE", "VIEW"};
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    DatabaseMetaData md = conn.getMetaData();
                    ResultSet rs = md.getTables(null, null, null, tableTypes);
                    List<String> items = new ArrayList<String>();
                    while (rs.next()) {
                        String table = rs.getString("TABLE_SCHEM") + "." + rs.getString("TABLE_NAME");
                        items.add(table);
                    }
                    rs.close();
                    Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
                    for (String item : items) {
                        cbMaster.addItem(item);
                        cbTable1.addItem(item);
                        cbTable2.addItem(item);
                    }
                    cbTable1.addItemListener(createItemListener());
                    cbTable2.addItemListener(createItemListener());
                    // This connection should not be closed here...it is controlled through the DatabaseExplorer
                    //conn.close();
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        if (cbMaster.getItemCount() > 1) {
            cbMaster.setSelectedItem(formNodeData.getTable());
        }
    }

    private ItemListener createItemListener() {
        return new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() == cbTable1) {
                    initColumnComboBoxes(cbColumn1, cbTable1.getSelectedItem().toString());
                } else if (e.getSource() == cbTable2) {
                    initColumnComboBoxes(cbColumn2, cbTable2.getSelectedItem().toString());
                }
            }
        };
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    private void initLists() {
        if (formNodeData.getLinkTables() != null) {
            int idx = 0;
            for (Map.Entry m : formNodeData.getLinkTables().entrySet()) {
                String tblMain = m.getKey().toString();
                HashMap<String, HashMap<String, String>> ctbl = (HashMap<String, HashMap<String, String>>) m.getValue();
                HashMap<String, String> colMap = null;
                KeyValue tblMap = null;
                for (Map.Entry c : ctbl.entrySet()) {
                    tblMap = new KeyValue(tblMain, c.getKey().toString());
                    String tbl = tblMain.concat(LINK_TO).concat(c.getKey().toString()).concat(LINK_USING);
                    HashMap<String, String> cols = (HashMap<String, String>) c.getValue();
                    colMap = new HashMap<String, String>();
                    String colStr = "";
                    for (Map.Entry col : cols.entrySet()) {
                        if (!"".equals(colStr)) {
                            colStr = colStr.concat(AND);
                        }
                        colStr = colStr.concat(col.getKey().toString()).concat(EQUAL).concat(col.getValue().toString());
                        colMap.put(col.getKey().toString(), col.getValue().toString());
                    }
                    tbl = tbl.concat(colStr);
                    if (tblMap != null) {
                        tblIdxList.add(tblMap);
                    }
                    if (colMap != null) {
                        colIdxList.add(colMap);
                    }
                    tableModel.addElement(tbl);
                    idx = idx + 1;
                }
            }
        }
        lstTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstTbl.setModel(tableModel);
        lstTbl.addListSelectionListener(createListSelectionListener());
        lstCol.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCol.setModel(colModel);

    }

    private void initColList(int index) {
        if (index == -1) {
            cbTable1.setSelectedItem("");
            cbTable2.setSelectedItem("");
            cbColumn1.setSelectedItem("");
            cbColumn2.setSelectedItem("");
            colModel.removeAllElements();
        } else {
            if (index < tblIdxList.size()) {
                KeyValue kv = tblIdxList.get(index);
                if (kv != null) {
                    cbTable1.setSelectedItem(kv.getKey());
                    cbTable2.setSelectedItem(kv.getValue());
                }
                HashMap<String, String> map = colIdxList.get(index);
                if (map != null) {
                    if (map.size() == 1) {
                        chkmulti.setSelected(false);
                        for (Map.Entry e : map.entrySet()) {
                            cbColumn1.setSelectedItem(e.getKey().toString());
                            cbColumn2.setSelectedItem(e.getValue().toString());
                        }
                    } else {
                        chkmulti.setSelected(true);
                        for (Map.Entry e : map.entrySet()) {
                            String s = e.getKey().toString().concat(EQUAL).concat(e.getValue().toString());
                            if (!colModel.contains(s)) {
                                colModel.addElement(s);
                            }
                        }
                    }
                }
            }
        }
    }

    private ListSelectionListener createListSelectionListener() {
        return new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    initColList(lstTbl.getSelectedIndex());
                }
            }
        };
    }
}
