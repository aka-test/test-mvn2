/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TableSetupForm.java
 *
 * Created on Feb 8, 2012, 7:04:57 AM
 */
package com.echoman.designer.components.echocommon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditorSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echocommon.SQLTableVisualComponent.LinkRecord;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

public class TableSetupForm extends javax.swing.JDialog implements DropTargetListener {

    public static final String SCHEMA_INFO = "INFORMATION_SCHEMA";
    public static final String SCHEMA_SYS = "sys";
    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    private final PropertyEditorSupport editor;
    private final EchoFormNodeData formNodeData;
    private int returnStatus = RET_CANCEL;
    private DefaultListModel tableModel = new DefaultListModel();
    private SQLTableVisualComponent activeTableVisualComponent = null;
    private SQLTableVisualComponent firstTableVisualComponent = null;
    private HashMap<String, String> sorts = new HashMap<String, String>();
    private HashMap<String, String> restrictions = new HashMap<String, String>();
    private boolean initializing = false;

    /** Creates new form TableSetupForm */
    public TableSetupForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor) {
        super(parent, modal);
        this.editor = editor;
        initComponents();
        Node[] ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        formNodeData = (EchoFormNodeData) ((IEchoComponentNode) ary[0]).getNodeData();
        init();
        setLocationRelativeTo(null);
    }

    private void init() {
        EchoUtil.changeCursorWaitStatus(true);
        initializing = true;
        try {
            initTableList();
            initSearchField();
            initTblInfo();
            initTableSorts();
            initTableRestrictions();
            initMasterTable();
            initLinkTables();
            if (firstTableVisualComponent != null) {
                try {
                    firstTableVisualComponent.setSelected(true);
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } finally {
            initializing = false;
            EchoUtil.changeCursorWaitStatus(false);
        }
    }

    private void initTableSorts() {
        sorts.clear();
        if (formNodeData.getTableSorts() != null) {
            for (Map.Entry e : formNodeData.getTableSorts().entrySet()) {
                sorts.put(e.getKey().toString(), e.getValue().toString());
            }
        }
    }

    private void initTableRestrictions() {
        restrictions.clear();
        if (formNodeData.getTableRestrictions() != null) {
            for (Map.Entry e : formNodeData.getTableRestrictions().entrySet()) {
                restrictions.put(e.getKey().toString(), e.getValue().toString());
            }
        }
    }

    private void initMasterTable() {
        if ((formNodeData.getTable() != null) && (!("".equals(formNodeData.getTable())))) {
            addTableVisualComponent(formNodeData.getTable());
        }
    }

    private int findItemIndex(ListModel model, String item) {
        for (int i = 0; i < model.getSize(); i++) {
            String s = model.getElementAt(i).toString();
            if (s.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    private void initLinkTables() {
        if (formNodeData.getLinkTables() != null) {
            for (Map.Entry m : formNodeData.getLinkTables().entrySet()) {
                String tblMain = m.getKey().toString();
                SQLTableVisualComponent mtvc = addTableVisualComponent(tblMain);
                if (firstTableVisualComponent == null) {
                    firstTableVisualComponent = mtvc;
                }
                HashMap<String, HashMap<String, String>> ctbl = (HashMap<String, HashMap<String, String>>) m.getValue();
                if (ctbl != null) {
                    for (Map.Entry c : ctbl.entrySet()) {
                        String tblChild = c.getKey().toString();
                        SQLTableVisualComponent ctvc = addTableVisualComponent(tblChild);
                        HashMap<String, String> cols = (HashMap<String, String>) c.getValue();
                        for (Map.Entry col : cols.entrySet()) {
                            String mcol = col.getKey().toString();
                            String ccol = col.getValue().toString();
                            int cidx = findItemIndex(ctvc.checkBoxList.getModel(), ccol);
                            int midx = findItemIndex(mtvc.checkBoxList.getModel(), mcol);
                            String name = mtvc.getTableName() + "_" + ctvc.getTableName();
                            ctvc.addLinkConnector(name, cidx, "to", null);
                            mtvc.addLinkConnector(name, midx, "from", ctvc);
                        }
                    }
                }
            }
        }
    }

    private void initTblInfo() {
        try {
            lblTbl.setText("");
            txtSort.setText("");
            txtSort.getDropTarget().addDropTargetListener(this);
            txtRes.setText("");
        } catch (TooManyListenersException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initSearchField() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchItem();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchItem();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchItem();
            }

            public void searchItem() {
                if (!"".equals(txtSearch.getText())) {
                    int idx = -1;
                    Enumeration elems = tableModel.elements();
                    while (elems.hasMoreElements()) {
                        idx++;
                        Object item = elems.nextElement();
                        if (item.toString().startsWith(txtSearch.getText())) {
                            lstTbl.setSelectedIndex(idx);
                            lstTbl.ensureIndexIsVisible(idx);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void paintLinkConnectors(Graphics g) {
        g.setColor(Color.red);
        try {
            for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
                if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                    SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                    Iterator iter = comp.getLinksToOtherTables().keySet().iterator();
                    while (iter.hasNext()) {
                        String tableName = (String) iter.next();
                        Point from = comp.getPoint(tableName);
                        Point to = comp.getToPoint(tableName);
                        if (!((from == null) || (to == null))) {
                            g.drawLine(from.x + 8, from.y - 2, to.x - 10, to.y - 2);
                            g.drawString("o", from.x, from.y + 3);
                            g.drawString(">", to.x - 10, to.y + 3);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SQLTableVisualComponent addTableVisualComponent(String tableName) {
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                if (comp.getTableName().equals(tableName)) {
                    return comp;
                }

            }
        }
        return createTableVisualComponent(tableName);
    }

    private String getSorts() {
        String sort = "";
        if (activeTableVisualComponent != null) {
            sort = sorts.get(activeTableVisualComponent.getTableName());
            if (sort == null) {
                sort = "";
            }
        }
        return sort;
    }

    private String getRestrictions() {
        String res = "";
        if (activeTableVisualComponent != null) {
            res = restrictions.get(activeTableVisualComponent.getTableName());
            if (res == null) {
                res = "";
            }
        }
        return res;
    }

    private SQLTableVisualComponent createTableVisualComponent(String tableName) {
        final SQLTableVisualComponent c = new SQLTableVisualComponent(tableName,
                pnlTbl, "", 140, 160, false) {

            @Override
            public void setSelected(boolean selected) throws PropertyVetoException {
                super.setSelected(selected);
                if (selected) {
                    assignSorts();
                    assignRestrictions();
                    activeTableVisualComponent = this;
                    lblTbl.setText(getTableName());
                    txtSort.setText(getSorts());
                    txtRes.setText(getRestrictions());
                }
            }

            @Override
            public void addLinkConnector(String name, int cell, String dir, SQLTableVisualComponent toTable) {
                super.addLinkConnector(name, cell, dir, toTable);
                checkBoxList.setCheckBoxListSelectedIndex(cell);
            }

            public String getSelectedValue() {
                if (checkBoxList.getSelectedValue() == null) {
                    return "";
                } else {
                    return checkBoxList.getSelectedValue().toString();
                }
            }
        };
        JMenuItem miRemoveLink = new JMenuItem();
        miRemoveLink.setText("Remove Link");
        miRemoveLink.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String tblName = c.getTableName();
                List<String> linkList = new ArrayList<String>();
                for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
                    if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                        SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                        for (Map.Entry e : comp.getLinksToOtherTables().entrySet()) {
                            String linkName = e.getKey().toString();
                            if (comp == c) {
                                if (!linkList.contains(linkName)) {
                                    linkList.add(linkName);
                                }
                            } else {
                                LinkRecord link = (LinkRecord) e.getValue();
                                if (link.getToTable() != null) {
                                    String toTbl = link.getToTable().getTableName();
                                    if (tblName.equals(toTbl)) {
                                        if (!linkList.contains(linkName)) {
                                            linkList.add(linkName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
                    if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                        SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                        HashMap<String, LinkRecord> links = comp.getLinksToOtherTables();
                        for (String linkName : linkList) {
                            if (links.containsKey(linkName)) {
                                links.remove(linkName);
                            }
                        }
                    }
                }
                c.updateLinkConnector();
            }
        });
        c.getPopupMenu().add(miRemoveLink);
        c.columnCaption.setVisible(false);
        c.columnIndex.setVisible(false);
        c.checkBoxList.setCheckBoxEnabled(false);

        //Try to position the tables
        //TODO: need a cleaner positioning logic here
        int left = 10;
        int top = 10;
        if (pnlTbl.getComponentCount() > 6) {
            left = pnlTbl.getComponent(pnlTbl.getComponentCount() - 1).getX() + 10;
        } else {
            if (pnlTbl.getComponentCount() > 1) {
                if (pnlTbl.getComponentCount() > 3) {
                    top = 190;
                    if (pnlTbl.getComponentCount() != 4) {
                        left = pnlTbl.getComponent(pnlTbl.getComponentCount() - 2).getX() + 170;
                    }
                } else {
                    left = pnlTbl.getComponent(pnlTbl.getComponentCount() - 2).getX() + 170;
                }
            }
        }
        c.setLocation(left, top);
        return c;
    }

    private void initTableList() {
        DatabaseConnection con = DBConnections.getConnection();
        tableModel.removeAllElements();
        if (!(con == null)) {
            String[] tableTypes = {"TABLE", "VIEW"};
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    DatabaseMetaData md = conn.getMetaData();
                    ResultSet rs = md.getTables(null, null, null, tableTypes);
                    List<String> items = new ArrayList<String>();
                    while (rs.next()) {
                        String schema = rs.getString("TABLE_SCHEM");
                        //don't include system tables
                        if ((!SCHEMA_SYS.equals(schema)) && (!SCHEMA_INFO.equals(schema))) {
                            String table = schema + "." + rs.getString("TABLE_NAME");
                            items.add(table);
                        }
                    }
                    rs.close();
                    Collections.sort(items, String.CASE_INSENSITIVE_ORDER);
                    for (String item : items) {
                        tableModel.addElement(item);
                    }
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        lstTbl.setModel(tableModel);
        lstTbl.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList) e.getSource();
                // if double-click
                if (e.getClickCount() == 2) {
                    int itemIdx = list.locationToIndex(e.getPoint());
                    try {
                        Object item = tableModel.get(itemIdx);
                        addTableVisualComponent(item.toString());
                    } catch (IndexOutOfBoundsException ex) {
                        //do nothing
                    }
                }
            }
        });
    }

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    private void showMessageAndFocus(String msg, JComponent comp) {
        JOptionPane.showMessageDialog(null, msg);
        if (comp != null) {
            comp.requestFocus();
        }
    }

    private boolean hasSelectedTable() {
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                return true;
            }
        }
        return false;
    }

    private SQLTableVisualComponent findMasterTableVisualComponent() {
        SQLTableVisualComponent res = null;
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            boolean isMaster = true;
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                for (LinkRecord link : comp.getLinksToOtherTables().values()) {
                    if ("to".equals(link.getDir())) {
                        isMaster = false;
                        break;
                    }
                }
                if (isMaster) {
                    res = comp;
                    break;
                }
            }
        }
        return res;
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        String tblName = "";
        if (activeTableVisualComponent != null) {
            tblName = activeTableVisualComponent.getTableName() + ".";
        }
        if (src.getText().equals("")) {
            src.setText(src.getText() + tblName);
        } else {
            src.setText(src.getText() + "," + tblName);
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        try {
            Transferable tr = dtde.getTransferable();
            String s = tr.getTransferData(DataFlavor.stringFlavor).toString();
            if (activeTableVisualComponent != null) {
                s = activeTableVisualComponent.getTableName() + "." + s;
            }
            if (src.getText().contains(s)) {
                dtde.rejectDrag();
            }
        } catch (UnsupportedFlavorException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        JTextField src = (JTextField) ((DropTarget) dte.getSource()).getComponent();
        if ((!(src.getText().equals(""))) && (src.getText().endsWith("."))
                && ((DropTarget) (dte.getSource())).isActive()) {
            int len = 1;
            if (activeTableVisualComponent != null) {
                len = (activeTableVisualComponent.getTableName() + ".").length();
            }
            src.setText(src.getText().substring(0, src.getText().length() - len));
        }
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        if (src == txtSort) {
            assignSorts();
        }
    }

    private boolean hasValidTable(String tblStr) {
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                if ((tblStr.equals(comp.getTableName()))
                        || (tblStr.startsWith(comp.getTableName() + "."))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyTableSorts() {
        formNodeData.getTableSorts().clear();
        for (Map.Entry e : sorts.entrySet()) {
            String tbl = e.getKey().toString();
            if (hasValidTable(tbl)) {
                formNodeData.getTableSorts().put(tbl, e.getValue().toString());
            }
        }
    }

    private void applyTableRestrictions() {
        formNodeData.getTableRestrictions().clear();
        for (Map.Entry e : restrictions.entrySet()) {
            String tbl = e.getKey().toString();
            if (hasValidTable(tbl)) {
                formNodeData.getTableRestrictions().put(tbl, e.getValue().toString());
            }
        }
    }

    private void applyLinkTables() {
        formNodeData.getLinkTables().clear();
        int tableCount = 0;
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                tableCount++;
            }
        }
        for (int i = 0; i < pnlTbl.getComponentCount(); i++) {
            if (pnlTbl.getComponent(i) instanceof SQLTableVisualComponent) {
                SQLTableVisualComponent comp = (SQLTableVisualComponent) pnlTbl.getComponent(i);
                String tbl1 = comp.getTableName();
                for (Map.Entry e : comp.getLinksToOtherTables().entrySet()) {
                    String linkName = e.getKey().toString();
                    LinkRecord link = (LinkRecord) e.getValue();
                    if (link.getToTable() != null) {
                        String tbl2 = link.getToTable().getTableName();
                        LinkedHashMap<String, LinkedHashMap<String, String>> tblMap = formNodeData.getLinkTables().get(tbl1);
                        if (tblMap == null) {
                            tblMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
                        }
                        LinkedHashMap<String, String> colMap = tblMap.get(tbl2);
                        if (colMap == null) {
                            colMap = new LinkedHashMap<String, String>();
                        }
//                        comp.checkBoxList.getModel().getElementAt(link.getCell());
                        String mcol = comp.checkBoxList.getModel().getElementAt(link.getCell()).toString();
                        LinkRecord clink = link.getToTable().getLinksToOtherTables().get(linkName);
                        String ccol = link.getToTable().checkBoxList.getModel().getElementAt(clink.getCell()).toString();
                        colMap.put(mcol, ccol);
                        tblMap.put(tbl2, colMap);
                        formNodeData.getLinkTables().put(tbl1, tblMap);
                    }
                }
                if (tableCount > 1) {
                    if (!formNodeData.getLinkTables().containsKey(tbl1)) {
                        formNodeData.getLinkTables().put(tbl1, new LinkedHashMap<String, LinkedHashMap<String, String>>());
                    }
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstTbl = new javax.swing.JList();
        pnlTbl = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintLinkConnectors(g);
            }
        };
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtSort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRes = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.title")); // NOI18N
        setModal(true);
        setResizable(false);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel1.text")); // NOI18N

        txtSearch.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.txtSearch.text")); // NOI18N

        lstTbl.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstTbl);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlTblLayout = new javax.swing.GroupLayout(pnlTbl);
        pnlTbl.setLayout(pnlTblLayout);
        pnlTblLayout.setHorizontalGroup(
            pnlTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
        );
        pnlTblLayout.setVerticalGroup(
            pnlTblLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 396, Short.MAX_VALUE)
        );

        jLabel6.setForeground(new java.awt.Color(153, 153, 153));
        jLabel6.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel6.text")); // NOI18N

        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel7.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlTbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnOk.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.btnOk.text")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtSort.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.txtSort.text")); // NOI18N
        txtSort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSortFocusLost(evt);
            }
        });

        jLabel2.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel2.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel5.text")); // NOI18N

        lblTbl.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.lblTbl.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel3.text")); // NOI18N

        txtRes.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.txtRes.text")); // NOI18N
        txtRes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtResFocusLost(evt);
            }
        });

        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText(org.openide.util.NbBundle.getMessage(TableSetupForm.class, "TableSetupForm.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel5)
                        .addGap(12, 12, 12)
                        .addComponent(lblTbl))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRes)
                            .addComponent(txtSort)
                            .addComponent(jLabel4))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblTbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(315, 315, 315)
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancel, btnOk});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        if (hasSelectedTable()) {
            SQLTableVisualComponent comp = findMasterTableVisualComponent();
            if (comp == null) {
                showMessageAndFocus("Please select a table.", null);
            } else {
                assignSorts();
                assignRestrictions();
                editor.setAsText(comp.getTableName());
                applyLinkTables();
                applyTableSorts();
                applyTableRestrictions();
                doClose(RET_OK);
            }
        } else {
            showMessageAndFocus("Please select a table.", null);
        }
    }//GEN-LAST:event_btnOkActionPerformed

    private void assignSorts() {
        if (!initializing) {
            if (activeTableVisualComponent != null) {
                String s = txtSort.getText().trim();
                if (!"".equals(s)) {
                    sorts.put(activeTableVisualComponent.getTableName(), s);
                } else {
                    sorts.remove(activeTableVisualComponent.getTableName());
                }
            }
        }
    }

    private void assignRestrictions() {
        if (!initializing) {
            if (activeTableVisualComponent != null) {
                String s = txtRes.getText().trim();
                if (!"".equals(s)) {
                    restrictions.put(activeTableVisualComponent.getTableName(), s);
                } else {
                    restrictions.remove(activeTableVisualComponent.getTableName());
                }
            }
        }
    }

    private void txtSortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSortFocusLost
        assignSorts();
    }//GEN-LAST:event_txtSortFocusLost

    private void txtResFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtResFocusLost
        assignRestrictions();
    }//GEN-LAST:event_txtResFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTbl;
    private javax.swing.JList lstTbl;
    private javax.swing.JPanel pnlTbl;
    private javax.swing.JTextField txtRes;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSort;
    // End of variables declaration//GEN-END:variables
}
