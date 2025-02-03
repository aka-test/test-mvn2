/**
 *
 */
package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.ColumnPropForm;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import org.openide.util.Exceptions;

/**
 *
 * @author Dave Athlon
 */
//public class EchoTable extends JLabel implements MouseListener, ActionListener, IEchoComponent{
public class EchoTable extends JScrollPane implements MouseListener, ActionListener, IEchoComponent {

    /**
     * Holds the node data instance.
     *
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoTableNodeData nodeData;
    private transient EchoTableNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;
    private JTable sampleTable;
    private boolean columnBeingMoved = false;
    private boolean columnBeingSized = false;
    //Ticket #502
    private transient HashMap<TableColumn, EchoColumnData> colNodeDataMap =
            new HashMap<>();

    public JTable getTableComponent() {
        return sampleTable;
    }

    public class HeaderMouseListener extends MouseAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
        }

        @Override
        public void mouseClicked(MouseEvent evt) {

            JTable table = ((JTableHeader) evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();

            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());

            // Return if not clicked on any column header
            if (vColIndex == -1) {
                return;
            }

            //Ticket #298
            //find the EchoColumnData object of the column that is clicked
            //by user using the column index and create the node object if it
            //is not yet set then set it as the selected node to show the
            //properties.
            int cnt = -1;
            for (EchoColumnData c : nodeData.getTableColumns().values()) {
                cnt++;
                if (cnt == vColIndex) {
                    if (c.getNode() == null) {
                        TableColumn tblCol = table.getColumnModel().getColumn(vColIndex); 
                        EchoColumnNodeData noded = new EchoColumnNodeData(nodeData, tblCol,                                
                                c, nodeData.getDesignerPage());
                        nodeData.getDesignerPage().getCompList().add(noded);
                        nodeData.getDesignerPage().getInspector().refreshList(nodeData.getDesignerPage().getCompList());
                        colNodeDataMap.put(tblCol, noded.getCol());
                    } else {
                        try {
                            c.getNode().addSelectedNode(false);
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    break;
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (columnBeingMoved || columnBeingSized) {
                if (columnBeingMoved) {
                    nodeData.reorderColumns();
                }
                nodeData.setColumnHeadersAndWidths();
            }
            columnBeingMoved = false;
            columnBeingSized = false;
        }
    }

    /**
     *
     * @return
     */
    private void getSampleTable() {
        TableModel dataModel = new AbstractTableModel() {
            /**
             *
             */
            @Override
            public int getColumnCount() {
                return 0;
            }

            /**
             *
             */
            @Override
            public int getRowCount() {
                return 1;
            }

            /**
             *
             */
            @Override
            public Object getValueAt(int row, int col) {
                return "";
            }
        };

        sampleTable = new JTable(dataModel) {
            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
            }

            @Override
            public void valueChanged(ListSelectionEvent e) {
                super.valueChanged(e);
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
                super.columnMoved(e);
                columnBeingMoved = true;
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                super.columnMarginChanged(e);
                columnBeingSized = true;
            }
        };
        createTableHeaderRenderer();
        sampleTable.getTableHeader().addMouseListener(new HeaderMouseListener());
        sampleTable.getTableHeader().setFont(new Font("Open Sans Semibold", Font.BOLD, 14));
        sampleTable.getTableHeader().setForeground(Color.white);
        sampleTable.getTableHeader().setOpaque(false);
        sampleTable.getTableHeader().setBackground(new Color(1, 85, 149));
        sampleTable.setFont(new Font("Open Sans Semibold", Font.BOLD, 14));
        sampleTable.setForeground(new Color(0, 0, 0));
    }

    //Ticket #502
    private void createTableHeaderRenderer() {
        if (sampleTable.getTableHeader() != null) {
            final TableCellRenderer tcrOs = sampleTable.getTableHeader().getDefaultRenderer();
            sampleTable.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table,
                        Object value, boolean isSelected, boolean hasFocus,
                        int row, int column) {
                    JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table,
                            value, isSelected, hasFocus, row, column);
                    if (lbl != null) {
                        TableColumn tblCol = table.getColumnModel().getColumn(column); 
                        EchoColumnData colData = colNodeDataMap.get(tblCol);
                        if (colData == null) {
                            lbl.setForeground(new Color(255, 255, 255));
                            lbl.setFont(new Font("Open Sans Semibold", Font.BOLD, 14));
                        } else {
                            lbl.setForeground(colData.getHeadingFontColor());
                            lbl.setFont(colData.getHeadingFont());                            
                        }
                    }
                    return lbl;
                }
            });
        }
    }

    //Ticket #502
    public void addColumnHeaderStyle(TableColumn col, EchoColumnData colData) {
        colNodeDataMap.put(col, colData);
    }
    
    //Ticket #502
    public void removeColumn(TableColumn col) {
        getTableComponent().removeColumn(col);
        colNodeDataMap.remove(col);
    }
    
    public void attachHeaderMouseListener() {
        sampleTable.getTableHeader().addMouseListener(new HeaderMouseListener());
    }

    /**
     *
     */
    public final void createPopupMenu() {
        popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Column Properties");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoTableNode) node;
    }

    @Override
    public void remove() {
        node.setIsDestroying(true);
        dropPanel.remove(this);
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    /**
     *
     */
    class PopupListener extends MouseAdapter {

        /**
         *
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         *
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         *
         * @param e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    /**
     *
     * @param dropPanel
     */
    @Override
    public void setDropPanel(JPanel dropPanel) {
        this.dropPanel = dropPanel;
    }

    /**
     *
     * @param node
     */
    public void setNodeData(EchoTableNodeData node) {
        nodeData = node;
    }

    /**
     *
     * @param d
     */
    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        revalidate();
        repaint();
    }

    /**
     *
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    /**
     *
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoTable(EchoTableNodeData nodeData, int index, JPanel dropPanel) {
        super();
        getSampleTable();
        setBackground(Color.WHITE);
        getViewport().setBackground(Color.white);
        sampleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        viewport.add(sampleTable);
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        //Ticket #438
        //setName("Table" + index);
        setName("Table");
        setBorder(BorderFactory.createLineBorder(Color.black, 1));
        setSize(216, 72);
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
    }

    /**
     *
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public void setLocation(int x, int y) {
        int lx = x;
        int ly = y;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isTopLocked()) {
                ly = nodeData.getTop();
            }
            if (nodeData.isLeftLocked()) {
                lx = nodeData.getLeft();
            }
        }
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);

        checkContainers();
    }

    private void checkContainers() {
        if (!nodeData.getLoadingForm()) {
            DataContainerManager.checkContainerComponents(nodeData, nodeData.getDesignerPage().getCompList());
            if (nodeData.getTable().equals("")) {
                nodeData.setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
            }
        }
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public final void setSize(int width, int height) {
        int w = width;
        int h = height;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isHeightLocked()) {
                h = nodeData.getHeight();
            }
            if (nodeData.isWidthLocked()) {
                w = nodeData.getWidth();
            }
        }
        //Ticket #375 - also resize the table to make sure borders are painted
        //properly
        sampleTable.setSize(w, h);
        super.setSize(w, h);
        nodeData.setSizeFromEdit(w, h);
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        node.addSelectedNode(e.isControlDown());
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (columnBeingMoved || columnBeingSized) {
            if (columnBeingMoved) {
                nodeData.reorderColumns();
            }
            nodeData.setColumnHeadersAndWidths();
        }
        columnBeingMoved = false;
        columnBeingSized = false;
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String txt = ((JMenuItem) (e.getSource())).getText();
        if (!node.handleAction(txt)) {
            if (txt.equalsIgnoreCase("Column Properties")) {
                ColumnPropForm cpf = new ColumnPropForm(null, true, nodeData);
                cpf.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Not implemented.");
            }
        }
    }
}
