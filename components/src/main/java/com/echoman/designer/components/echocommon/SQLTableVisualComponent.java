/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.jidesoft.swing.CheckBoxList;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.echoman.designer.databasemanager.DBConnections;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.Exceptions;

/**
 *
 * @author david.morin
 */
public class SQLTableVisualComponent extends JInternalFrame implements ComponentListener, ListSelectionListener, ChangeListener {

    private JScrollPane scrollPane;
    protected CheckBoxList checkBoxList;
    protected JTextField columnCaption;
    protected JTextField columnIndex;
    private String tableName;
    private HashMap<String, LinkRecord> linksToOtherTables;
    private JPanel tableLayoutPanel;
    private boolean includeTableNameInColumn = true;
    private JPopupMenu pmRemoveTable;
    private SQLTableVisualEvent removeEvent = null;

    //Ticket #470
    public interface SQLTableVisualEvent {

        public void doEvent();
    }

    /**
     * Each table component has link records for all of it's links to other
     * tables. A reference to the other table component is stored in the toTable
     * property. Each table component maintains the location of the points when
     * the component is moved/scrolled etc. When the painting is done, the
     * toTable reference is used to get the to points.
     *
     */
    public class LinkRecord {

        private int cell;
        private String dir;
        private Point point;
        private SQLTableVisualComponent toTable;

        /**
         *
         * @param cell
         * @param dir
         * @param point
         * @param toTable
         */
        LinkRecord(int cell, String dir, Point point, SQLTableVisualComponent toTable) {
            this.cell = cell;
            this.point = point;
            this.toTable = toTable;
            this.dir = dir;
        }

        /**
         *
         * @return
         */
        public String getDir() {
            return dir;
        }

        /**
         *
         * @return
         */
        public int getCell() {
            return cell;
        }

        /**
         *
         * @param point
         */
        public void setPoint(Point point) {
            this.point = point;
        }

        /**
         *
         * @return
         */
        public Point getPoint() {
            return point;
        }

        /**
         *
         * @param name
         * @return
         */
        public Point getToPoint(String name) {
            if (!(toTable == null)) {
                return toTable.getPoint(name);
            } else {
                return null;
            }
        }

        /**
         *
         * @return
         */
        public SQLTableVisualComponent getToTable() {
            return toTable;
        }
    }

    /**
     *
     * @return
     */
    public String getSelect() {
        SortedMap<Integer, String> tm = new TreeMap<Integer, String>();
        CheckBoxSelectionModel model = (CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel();
        String select = "";
        for (int i = 0; i < checkBoxList.getCheckBoxListSelectedIndices().length; i++) {
            int key = checkBoxList.getCheckBoxListSelectedIndices()[i];
            Integer index = new Integer(Integer.valueOf(model.indexExists(key)));
            String caption = model.captionExists(key);
            String column = (String) model.getModel().getElementAt(key);
            if (caption.equals("")) {
                caption = column.substring(column.indexOf(".") + 1);
            }
            tm.put(index, ", " + column + " \"" + caption + "\"");
        }
        String noLeadingComma = tm.get(tm.firstKey()).substring(1);
        tm.put(tm.firstKey(), noLeadingComma);
        Iterator iterator = tm.values().iterator();
        while (iterator.hasNext()) {
            select = select + iterator.next();
        }
        return select;
    }

    /**
     *
     * @return
     */
    public String getFrom() {
        return tableName + " " + tableName.substring(tableName.indexOf(".") + 1);
    }

    /**
     *
     * @return
     */
    public String getJoin() {
        return "";
    }

    /**
     *
     * @return
     */
    public String getWhere() {
        return "";
    }

    /**
     *
     * @return
     */
    public String getOrderBy() {
        return "";
    }

    /**
     *
     * @return
     */
    public String getGroupBy() {
        return "";
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        return tableName;
    }

    /**
     *
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     *
     * @param name
     * @return
     */
    public Point getPoint(String name) {
        return linksToOtherTables.get(name).getPoint();
    }

    /**
     *
     * @param name
     * @return
     */
    public Point getToPoint(String name) {
        return linksToOtherTables.get(name).getToPoint(name);
    }

    /**
     *
     */
    public class ColumnListDropTarget extends DropTarget {

        @Override
        public synchronized void drop(DropTargetDropEvent dtde) {
            super.drop(dtde);
            dtde.acceptDrop(DnDConstants.ACTION_LINK);
        }
    }

    public JPopupMenu getPopupMenu() {
        return pmRemoveTable;
    }

    /**
     *
     * @param tableName
     * @param tableLayoutPanel
     * @param selectedColumns
     * @param width
     * @param height
     */
    public SQLTableVisualComponent(String tableName, JPanel tableLayoutPanel,
            String selectedColumns, int width, int height,
            boolean includeTableNameInColumn) {
        this.includeTableNameInColumn = includeTableNameInColumn;
        pmRemoveTable = new JPopupMenu();
        JMenuItem miRemoveTable = new JMenuItem();
        miRemoveTable.setText("Remove Table");
        miRemoveTable.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miRemoveTableActionPerformed(evt);
            }
        });
        pmRemoveTable.add(miRemoveTable);
        this.tableLayoutPanel = tableLayoutPanel;
        linksToOtherTables = new HashMap<String, LinkRecord>();
        scrollPane = new JScrollPane();
        addComponentListener(this);
        columnCaption = new JTextField();
        columnCaption.setText("");
        columnCaption.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                captionColumnUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                captionColumnUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                captionColumnUpdate(e);
            }
        });
        columnCaption.setEnabled(false);
        columnIndex = new JTextField();
        columnIndex.setText("");
        columnIndex.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                indexColumnUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                indexColumnUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                indexColumnUpdate(e);
            }
        });
        columnIndex.setEnabled(false);
        checkBoxList = new com.jidesoft.swing.CheckBoxList();
        checkBoxList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        checkBoxList.setCheckBoxListSelectionModel(new CheckBoxSelectionModel(checkBoxList, columnCaption, columnIndex));
        checkBoxList.addListSelectionListener(this);
        checkBoxList.setDragEnabled(true);
        checkBoxList.setComponentPopupMenu(pmRemoveTable);
        //checkBoxList.setDropTarget(new ColumnListDropTarget());
        checkBoxList.setTransferHandler(new LinkTransferHandler());
        setResizable(true);
        setBorder(null);
        this.tableName = tableName;
        setTitle(tableName);
        getTableColumns(checkBoxList, selectedColumns);
        scrollPane.setViewportView(checkBoxList);
        scrollPane.getViewport().addChangeListener(this);
        GroupLayout jInternalFrame1Layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(jInternalFrame1Layout);

        jInternalFrame1Layout.setHorizontalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrame1Layout.createSequentialGroup().addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE).addGroup(jInternalFrame1Layout.createSequentialGroup().addComponent(columnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(1, 1, 1).addComponent(columnCaption, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE).addGap(0, 0, 0))).addGap(0, 0, 0)));
        jInternalFrame1Layout.setVerticalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrame1Layout.createSequentialGroup().addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE).addGap(1, 1, 1).addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(columnIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(columnCaption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))));
        tableLayoutPanel.add(this);
        setBounds((tableLayoutPanel.getWidth() / 2) - (width / 2), (tableLayoutPanel.getHeight() / 2) - (height / 2), width, height);
        setLocation(150, 25);
        setVisible(true);
    }

    public void addRemoveEvent(SQLTableVisualEvent ev) {
        removeEvent = ev;
    }

    /**
     *
     * @param evt
     */
    private void captionColumnUpdate(DocumentEvent evt) {
        ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).setCaption(checkBoxList.getSelectedIndex(), columnCaption.getText());
    }

    /**
     *
     * @param evt
     */
    private void indexColumnUpdate(DocumentEvent evt) {
        if (!(columnIndex.getText().equals(""))) {
            ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).setIndex(checkBoxList.getSelectedIndex(), Integer.valueOf(columnIndex.getText()));
        } else {
            ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).setIndex(checkBoxList.getSelectedIndex(), ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).getMaxIndex());
        }
    }

    /**
     *
     * @param evt
     */
    private void miRemoveTableActionPerformed(java.awt.event.ActionEvent evt) {
        // Add a new Table/Column List for the SQL.
        for (int i = 0; i < tableLayoutPanel.getComponentCount(); i++) {
            if ((tableLayoutPanel.getComponent(i) instanceof SQLTableVisualComponent)
                    && (!(tableLayoutPanel.getComponent(i).equals(this)))) {
                SQLTableVisualComponent comp = (SQLTableVisualComponent) tableLayoutPanel.getComponent(i);
                Iterator key = linksToOtherTables.keySet().iterator();
                while (key.hasNext()) {
                    String name = (String) key.next();
                    if (comp.getLinksToOtherTables().containsKey(name)) {
                        comp.getLinksToOtherTables().remove(name);
                    }
                }
            }
        }
        tableLayoutPanel.remove(this);
        this.dispose();
        tableLayoutPanel.repaint();
        if (removeEvent != null) {
            removeEvent.doEvent();
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentResized(ComponentEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentMoved(ComponentEvent e) {
        updateLinkConnector();
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentShown(ComponentEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentHidden(ComponentEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // This must be done here and in the DataCheckBoxListSelectionModel listener
        // because it is possible for the user to click the checkbox before selecting
        // the item in the list and here it is correct, but in the DataCheckBoxSelectionModel
        // the selected index is not correct at that point.  Also, it is possible for
        // the user to check and uncheck the checkbox without changing the selected item
        // in which case this event never fires, so it must be handled in the
        // DataCheckBoxSelectionModel listener.
        String caption = ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).captionExists(checkBoxList.getSelectedIndex());
        int index = ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).indexExists(checkBoxList.getSelectedIndex());
        if (checkBoxList.getCheckBoxListSelectionModel().isSelectedIndex(checkBoxList.getSelectedIndex())) {
            String column = ((String) checkBoxList.getSelectedValue());
            column = column.substring(column.indexOf(".") + 1);
            int maxindex = ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).getMaxIndex();

            if (!(caption.equals(""))) {
                columnCaption.setText(caption);
            } else {
                ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).addCaption(checkBoxList.getSelectedIndex(), "");
                columnCaption.setText(column);
            }
            if (!(index == -1)) {
                columnIndex.setText(Integer.toString(index));
            } else {
                ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).addIndex(checkBoxList.getSelectedIndex(), maxindex);
                columnIndex.setText(Integer.toString(maxindex));
            }
            columnCaption.setEnabled(true);
            columnIndex.setEnabled(true);
        } else {
            if (!(caption.equals(""))) {
                ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).removeCaption(checkBoxList.getSelectedIndex());
                columnCaption.setText("");
            } else {
                columnCaption.setText("");
            }
            if (!(index == -1)) {
                ((CheckBoxSelectionModel) checkBoxList.getCheckBoxListSelectionModel()).removeIndex(checkBoxList.getSelectedIndex());
                columnIndex.setText("");
            } else {
                columnIndex.setText("");
            }
            columnCaption.setEnabled(false);
            columnIndex.setEnabled(false);
        }
        updateLinkConnector();
    }

    /**
     *
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        updateLinkConnector();
    }

    /**
     *
     * @param name
     * @param cell
     * @param dir
     * @param toTable
     */
    public void addLinkConnector(String name, int cell, String dir, SQLTableVisualComponent toTable) {
        // Add this component's initial link points.        
        int y = checkBoxList.getCellBounds(cell, cell).y;
        int yheight = (int) checkBoxList.getCellBounds(cell, cell).getHeight();
        int scrollOffset = scrollPane.getViewport().getViewPosition().y;
        int cellAddOn = (y + yheight + 16 - scrollOffset);
        int useX = getX();
        if (cellAddOn < 0) {
            cellAddOn = 0;
        } else if (cellAddOn > getHeight()) {
            cellAddOn = getHeight();
        }
        if (dir.equals("from")) {
            useX = getLocation().x + getWidth();
        }
        Point fromPoint = new Point(useX, getLocation().y + cellAddOn);
        linksToOtherTables.put(name, new LinkRecord(cell, dir, fromPoint, toTable));
        tableLayoutPanel.repaint();
    }

    /**
     *
     */
    public void updateLinkConnector() {
        // Update this components link points.
        Iterator it = linksToOtherTables.values().iterator();
        while (it.hasNext()) {
            LinkRecord link = (LinkRecord) it.next();
            int y = checkBoxList.getCellBounds(link.getCell(), link.getCell()).y;
            int yheight = (int) checkBoxList.getCellBounds(link.getCell(), link.getCell()).getHeight();
            int scrollOffset = scrollPane.getViewport().getViewPosition().y;
            int useX = getX();
            int cellAddOn = (y + yheight + 16 - scrollOffset);
            if (cellAddOn < 0) {
                cellAddOn = 0;
            } else if (cellAddOn > getHeight()) {
                cellAddOn = getHeight();
            }
            if (link.getDir().equals("from")) {
                useX = getLocation().x + getWidth();
            }
            Point point = new Point(useX, getLocation().y + cellAddOn);
            link.setPoint(point);
        }
        tableLayoutPanel.repaint();
    }

    /**
     *
     * @return
     */
    public HashMap<String, LinkRecord> getLinksToOtherTables() {
        return linksToOtherTables;
    }

    /**
     *
     * @param columnList
     * @param selectedColumns
     */
    private void getTableColumns(CheckBoxList columnList, String selectedColumns) {
        Pattern regex;
        Matcher regexMatcher;
        int startIndex = -1;
        String column = "";
        String[] cols = selectedColumns.split(",");

        DatabaseConnection con = DBConnections.getConnection();
        if ((!(con == null)) && ((!(tableName == null))) && (!(tableName.equals(""))) && (!(tableName.equals("Table")))) {
            DefaultListModel columnListModel = new DefaultListModel();
            int idx = tableName.indexOf('.');
            String getColSchemaName = null;
            String getColTableName = tableName;
            if (idx > -1) {
                getColSchemaName = tableName.substring(0, idx);
                getColTableName = tableName.substring(idx + 1);
            }
            Connection conn = con.getJDBCConnection();

            if (!(conn == null)) {
                try {

                    DatabaseMetaData md = conn.getMetaData();

                    ResultSet rsc = md.getColumns(null, getColSchemaName, getColTableName, null);
                    while (rsc.next()) {
                        if (includeTableNameInColumn) {
                            columnListModel.addElement(getColTableName + "." + rsc.getString("COLUMN_NAME"));
                        } else {
                            columnListModel.addElement(rsc.getString("COLUMN_NAME"));
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

            for (int c = 0; c < cols.length; c++) {
                startIndex = -1;
                //Ticket #341
                String colname = cols[c].trim().replace("[", "").replace("]", "");
                if (!colname.startsWith(getColTableName + ".")) {
                    colname = getColTableName + "." + colname + " \"" + colname + "\"";
                }
                for (int i = 0; i < columnList.getModel().getSize(); i++) {
                    try {
                        String listcolname = columnList.getModel().getElementAt(i).toString();
                        regex = Pattern.compile("(?i)\\b(" + listcolname + ")\\b");
                        regexMatcher = regex.matcher(colname);
                        if (regexMatcher.find()) {
                            startIndex = regexMatcher.end();
                            regex = Pattern.compile("(?i)\".+\"");
                            regexMatcher.usePattern(regex);
                            if (regexMatcher.find(startIndex)) {
                                columnList.addCheckBoxListSelectedIndex(i);
                                column = colname.substring(regexMatcher.start() + 1, regexMatcher.end() - 1);
                                ((CheckBoxSelectionModel) columnList.getCheckBoxListSelectionModel()).addCaption(i, column);
                            }
                            ((CheckBoxSelectionModel) columnList.getCheckBoxListSelectionModel()).addIndex(i, c + 1);
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
            }

        } else if (tableName.equals("Table")) {
            clearColumns(columnList);
        }
    }

    /**
     *
     * @param columnList
     */
    private void clearColumns(CheckBoxList columnList) {
        columnList.setModel(new DefaultListModel());
    }
}
