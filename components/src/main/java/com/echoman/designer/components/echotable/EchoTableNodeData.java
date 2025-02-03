/**
 *
 */
package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.jdesi.FormData;
import org.openide.util.WeakListeners;
import org.openide.windows.WindowManager;

import static com.echoman.designer.components.echocommon.EchoUtil.isNullOrEmpty;

/**
 *
 * @author david.morin
 */
public class EchoTableNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private int tabOrder = 0;
    private int top;
    private int left;
    private int height;
    private int width;
    private Font font = new Font("Open Sans Semibold", Font.BOLD, 14);
    private Color fontColor = new Color(0, 0, 0);
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean visible = true;
    private boolean editable = false;
    private boolean readOnly = true;
    private int rowsPerPage = 20;
    private String parentContainer = "";
    private String table = "";
    private String columns = "";
    private LinkedHashMap<String, EchoColumnData> tableColumns = new LinkedHashMap<>();
    // Ticket 447
    private String insertEvent = "";
    private String deleteEvent = "";
    private String saveEvent = "";
    private String scrollEvent = "";
    private String sql = "";
    private String defaultSortColumns = "";
    private boolean haveKeyCol;
    private boolean haveGUIDCol;
    private boolean navigationGrid = false;
    private boolean hideInsert = false;
    private boolean hideDelete = false;
    private String formLinkColumn = "";
    private String formLinkToColumn = "";
    private FormData popupFdNextForm = null;
    private String popupFormName = "";
    private String uiLinkColumn = "";
    private String uiLinkToColumn = "";
    private String popupUiName = "";
    private String formLinkFromKeyField = "";
    private String formLinkToKeyField = "";

    public EchoTableNodeData() {
    }

    public boolean isNavigationGrid() {
        return navigationGrid;
    }

    public void setNavigationGrid(boolean navigationGrid) {
        this.navigationGrid = navigationGrid;
        if (navigationGrid) {
            clearExistingNavigationGrid();
        }
    }
    
    private void clearExistingNavigationGrid() {
        for (IEchoComponentNodeData nd : designerPage.getCompList()) {
            if ((nd instanceof EchoTableNodeData) && (!nd.equals(this))) {
                ((EchoTableNodeData)nd).setNavigationGrid(false);
            }
        }
    }

    public boolean isHideInsert() {
        return hideInsert;
    }

    public void setHideInsert(boolean hideInsert) {
        this.hideInsert = hideInsert;
    }

    public boolean isHideDelete() {
        return hideDelete;
    }

    public void setHideDelete(boolean hideDelete) {
        this.hideDelete = hideDelete;
    }

    @Override
    public void clearColumn() {
        removeMissingColumns(new ArrayList());
        this.tableColumns.clear();
        this.columns = "";
        updateColumnsProperty();
    }

    @Override
    public void setTable(String parentContainer, String table) {
        if (!(table.equals(this.table))) {
            clearColumn();
        }
        this.parentContainer = parentContainer;
       this.table = table ;
    }

    @Override
    public String getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    private void createSql() {
        sql = "";
        sql = sql + "SELECT " + getColumnsForSql()
                + " FROM " + table + " ";
    }

    public String getSql() {
        return sql;
    }

    public LinkedHashMap<String, EchoColumnData> getTableColumns() {
        return tableColumns;
    }

    @Override
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
        designerPage.setModified(true);

    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = new Color(backgroundColor.getRGB());
        designerPage.setModified(true);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        designerPage.setModified(true);
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = new Color(fontColor.getRGB());
        designerPage.setModified(true);
    }

    public String getDeleteEvent() {
        return deleteEvent;
    }

    public void setDeleteEvent(String deleteEvent) {
        this.deleteEvent = deleteEvent;
        designerPage.setModified(true);
    }

    public String getInsertEvent() {
        return insertEvent;
    }

    public void setInsertEvent(String insertEvent) {
        this.insertEvent = insertEvent;
        designerPage.setModified(true);
    }

    public String getSaveEvent() {
        return saveEvent;
    }

    public void setSaveEvent(String saveEvent) {
        this.saveEvent = saveEvent;
        designerPage.setModified(true);
    }

    public String getScrollEvent() {
        return scrollEvent;
    }

    public void setScrollEvent(String scrollEvent) {
        this.scrollEvent = scrollEvent;
    }

    /**
     * This is called by serialization when constructing the object.
     * No constructor is called so you have to tap into it here.
     * @return
     */
    private Object readResolve() {
        loadingForm = true;
        try {
            designerPage = JDesiWindowManager.getActiveDesignerPage();
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            listeners = Collections.synchronizedList(new LinkedList());
            EchoTable obj = this.getTableComp();
            if (obj == null) {
                obj = createTable(dropPanel);
            } else {
                obj.setDropPanel(dropPanel);
                obj.createPopupMenu();
                obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                obj.attachHeaderMouseListener();
                new Draggable(obj, dropPanel);
                new Resizeable(obj);
            }
            defaultNewlyAddedProperties();
            dropPanel.add(obj);
            ArrayList<IEchoComponentNodeData> compList = designerPage.getCompList();
            compList.add(this);
            // Ticket 439
            dropPanel.setComponentZOrder(obj, 0);
        } finally {
            loadingForm = false;
        }
        // at the end returns itself
        return this;
    }

    private void defaultNewlyAddedProperties() {
        if (parentContainer == null) {
            parentContainer = "";
            DataContainerManager.checkContainerComponents(this, getDesignerPage().getCompList());
            if (getTable().equals("")) {
                setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
            }
        }
    }

    private EchoTable createTable(JPanel dropPanel) {
        int ltabOrder = tabOrder;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        Font lfont = font;
        Color lfontColor = fontColor;
        String lname = name;
        Color lbackgroundColor = backgroundColor;
        boolean lvisible = visible;
        boolean leditable = editable;
        boolean lreadOnly = readOnly;
        int lrowsPerPage = rowsPerPage;
        String lparentContainer = parentContainer;
        String ltable = table;
        String lcolumns = columns;
        LinkedHashMap<String, EchoColumnData> ltableColumns = new LinkedHashMap<>();
        for (EchoColumnData col : tableColumns.values()) {
            ltableColumns.put(col.getColName(), col.clone());
        }
        String linsertEvent = insertEvent;
        String ldeleteEvent = deleteEvent;
        String lsaveEvent = saveEvent;
        String lscrollEvent = scrollEvent;
        String lsql = sql;
        String ldefaultSortColumns = defaultSortColumns;
        boolean lhaveKeyCol = haveKeyCol;
        boolean lhaveGUIDCol = haveGUIDCol;
        boolean lnavigationGrid = navigationGrid;
        boolean lhideInsert = hideInsert;
        boolean lhideDelete = hideDelete;
        // Ticket 529
        String lformLinkColumn = formLinkColumn;
        String lformLinkToColumn = formLinkToColumn;
        FormData lpopupFdNextForm = popupFdNextForm;
        String lpopupFormName = popupFormName;
        String luiLinkColumn = uiLinkColumn;
        String luiLinkToColumn = uiLinkToColumn;
        String lpopupUiName = popupUiName;
        String lformLinkToKeyField = formLinkToKeyField;
        String lformLinkFromKeyField = formLinkFromKeyField;

        component = new EchoTable(this, index, dropPanel);
        getTableComp().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        getTableComp().attachHeaderMouseListener();
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        tabOrder = ltabOrder;
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setWidth(lwidth);
        setFont(lfont);
        setFontColor(lfontColor);
        setBackgroundColor(lbackgroundColor);
        setRowsPerPage(lrowsPerPage);
        parentContainer = lparentContainer;
        table = ltable;
        setVisible(lvisible);
        setEditable(leditable);
        haveKeyCol = lhaveKeyCol;
        haveGUIDCol = lhaveGUIDCol;
        defaultSortColumns = ldefaultSortColumns;
        navigationGrid = lnavigationGrid;
        hideInsert = lhideInsert;
        hideDelete = lhideDelete;
        // Ticket 529
        formLinkColumn = lformLinkColumn;
        formLinkToColumn = lformLinkToColumn;
        popupFdNextForm = lpopupFdNextForm;
        popupFormName = lpopupFormName;
        uiLinkColumn = luiLinkColumn == null ? "" : luiLinkColumn;;
        uiLinkToColumn = luiLinkToColumn == null ? "" : luiLinkToColumn;
        popupUiName = lpopupUiName == null ? "" : lpopupUiName;
        formLinkToKeyField = lformLinkToKeyField == null ? "" : lformLinkToKeyField;
        formLinkFromKeyField = lformLinkFromKeyField == null ? "" : lformLinkFromKeyField;
        tableColumns.clear();
        for (EchoColumnData col : ltableColumns.values()) {
            tableColumns.put(col.getColName(), col.clone());
        }
        createNewColumns();
        columns = lcolumns;
        setReadOnly(lreadOnly);
        sql = lsql;
        setInsertEvent(linsertEvent);
        setDeleteEvent(ldeleteEvent);
        setSaveEvent(lsaveEvent);
        setScrollEvent(lscrollEvent);
        return getTableComp();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoTableNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoTableNodeData nodeData = (EchoTableNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBackgroundColor(nodeData.getBackgroundColor());
        setRowsPerPage(nodeData.getRowsPerPage());
        parentContainer = nodeData.getParentContainer();
        table = nodeData.getTable();
        setVisible(nodeData.getVisible());
        haveKeyCol = nodeData.getHaveKeyCol();
        haveGUIDCol = nodeData.getHaveGUIDCol();
        defaultSortColumns = nodeData.getDefaultSortColumns();
        navigationGrid = nodeData.isNavigationGrid();
        hideInsert = nodeData.isHideInsert();
        hideDelete = nodeData.isHideDelete();
        // Ticket 529
        formLinkColumn = nodeData.getFormLinkColumn();
        formLinkToColumn = nodeData.getFormLinkToColumn();
        popupFdNextForm = nodeData.getPopupFdNextForm();
        popupFormName = nodeData.getPopupFormName();
        uiLinkColumn = nodeData.getUiLinkColumn();
        uiLinkToColumn = nodeData.getUiLinkToColumn();
        popupUiName = nodeData.getPopupUiName();
        formLinkToKeyField = nodeData.getFormLinkToKeyField();
        formLinkFromKeyField = nodeData.getFormLinkFromKeyField();

        tableColumns.clear();
        for (EchoColumnData col : nodeData.getTableColumns().values()) {
           tableColumns.put(col.getColName(), col.clone());
        }
        removeMissingColumns(new ArrayList(nodeData.getTableColumns().keySet()));
        createNewColumns();
        columns = nodeData.getColumns();
        setReadOnly(nodeData.getReadOnly());
        sql = nodeData.getSql();
        setInsertEvent(nodeData.getInsertEvent());
        setDeleteEvent(nodeData.getDeleteEvent());
        setSaveEvent(nodeData.getSaveEvent());
        setScrollEvent(nodeData.getScrollEvent());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoTableNodeData nodeData = new EchoTableNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoTableNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab                        
        //tabOrder = getUniqueTabOrder();
    }

    //Ticket #464 
    /**
     * set parent id for different tab and assign tab order
     * 
     * @param parentId 
     */    
    @Override
    public void setParentId(String parentId, boolean windesiImport) {
        super.setParentId(parentId, windesiImport);
        if (!windesiImport) {
            tabOrder = getUniqueTabOrder();
        }
    }
        
    
    /**
     * 
     * @param glassPane
     * @param dropPanel
     */
    public EchoTableNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab                        
        //tabOrder = getUniqueTabOrder();
        component = new EchoTable(this, index, dropPanel);
        setName(component.getName());
        getTableComp().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoTable getTableComp() {
        return (EchoTable) component;
    }

    /**
     * 
     * @return
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        tabOrder = getTabOrder(tabOrder, visible);
        designerPage.setModified(true);
    }

    /**
     *
     * @return
     */
    public boolean getEditable() {
        return editable;
    }

    /**
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getReadOnly() {
        return readOnly;
    }

    /**
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        designerPage.setModified(true);
    }

    /**
     *
     */
    @Override
    public int getTabOrder() {
        return tabOrder;
    }

    /**
     * 
     * @param tabOrder
     */
    @Override
    public void setTabOrder(Integer tabOrder) {
        if (this.tabOrder != tabOrder)
            incNextTabOrder(tabOrder);
        this.tabOrder = tabOrder;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public int getTop() {
        return top;
    }

    /**
     * 
     * @param top
     */
    @Override
    public void setTop(Integer top) {
        this.top = top;
        if (getTableComp() != null) {
            getTableComp().setLocation(getTableComp().getLocation().x, top);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public int getLeft() {
        return left;
    }

    /**
     * 
     * @param left
     */
    @Override
    public void setLeft(Integer left) {
        this.left = left;
        if (getTableComp() != null) {
            getTableComp().setLocation(left, getTableComp().getLocation().y);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 
     * @param height
     */
    public void setHeight(Integer height) {
        this.height = height;
        if (getTableComp() != null) {
            getTableComp().setSize(getTableComp().getWidth(), height);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     */
    public void setWidth(Integer width) {
        this.width = width;
        if (getTableComp() != null) {
            getTableComp().setSize(width, getTableComp().getHeight());
        }
        designerPage.setModified(true);
    }

    /**
     *
     * @param width
     * @param height
     */
    public void setSize(Point pnt) {
        setWidth(pnt.x);
        setHeight(pnt.y);
    }

    /**
     *
     * @param left
     * @param top
     */
    public void setLocation(Point pnt) {
        setLeft(pnt.x);
        setTop(pnt.y);
    }

    /**
     * 
     * @param width
     * @param height
     */
    public void setSizeFromEdit(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        fire("refresh", 0, 0);
        designerPage.setModified(true);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void setLocationFromEdit(Integer x, Integer y) {
        undoableHappened("location", new Point(this.left, this.top), new Point(x, y));
        this.top = y;
        this.left = x;
        fire("refresh", 0, 0);
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return index + " - ";
    }

    public String getColumns() {
        boolean haveFirst = false;
        StringBuilder cols = new StringBuilder();
        for (String col : tableColumns.keySet()) {
            if (!haveFirst) {
                cols.append(col);
                haveFirst = true;
            } else {
                cols.append(",").append(col);
            }
        }
        return cols.toString();
    }

    @Override
    public String getColumnsForSql() {
        boolean haveFirst = false;
        StringBuilder cols = new StringBuilder();
        for (String col : tableColumns.keySet()) {
            if (!col.equals("***")) {
                if (!haveFirst) {
                    cols.append(col);
                    haveFirst = true;
                } else {
                    cols.append(",").append(col);
                }
            }
        }
        return cols.toString();
    }

    public void setColumns(String columns) {
        // The columns come over in the following format:
        // table_name;col1 datatype   ;col2 datatype  pKey; etc.
        ArrayList<String> values = null;
        ArrayList<String> keys = new ArrayList<>();
        String selectedTableColumns = "";
        if (!columns.equals("") && (columns.indexOf(";") != -1)) {
            values = new ArrayList<>();
            table = columns.substring(0, columns.indexOf(";"));
            selectedTableColumns = columns.substring(columns.indexOf(";") + 1, columns.length());
            if (!selectedTableColumns.equals("")) {
                StringTokenizer allTokens = new StringTokenizer(selectedTableColumns, ";");
                while (allTokens.hasMoreTokens()) {
                    StringTokenizer colTokens = new StringTokenizer(allTokens.nextToken());
                    values.clear();
                    while (colTokens.hasMoreTokens()) {
                        values.add(colTokens.nextToken());
                    }
                    String justColumns = values.get(0).trim();
                    keys.add(justColumns);
                    // Columns end up in the list like this:
                    // Col1
                    // Col2
                    // etc.
                    // with a toString() value of [Col1, Col2, etc.] and this is
                    // what is passed to the publisher.
                    // Add any new columns.
                    if (!(tableColumns.containsKey(justColumns))) {
                        tableColumns.put(justColumns, new EchoColumnData(justColumns));
                    }
                    // Always do this in case a data type has changed in the table.
                    if (values.size() > 1) {
                        setDataType(justColumns, (String) values.get(1));
                        // Ticket 337
                        if (values.contains("char(36)") && values.contains("pKey")) {
                            setHaveGUIDCol(true);
                        }
                        if (values.contains("pKey")) {
                            setHaveKeyCol(true);
                        }
                    } else {
                        setDataType(justColumns.trim(), "");
                        setHaveGUIDCol(false);
                        setHaveKeyCol(false);
                    }
                }
            }
        }

        removeMissingColumns(keys);
        createNewColumns();
        updateColumnsProperty();
        // Widths could have changed because of autofit so update again.
        setColumnHeadersAndWidths();
        setReadOnly(readOnly);
        createSql();
        designerPage.setModified(true);
    }

    private void removeMissingColumns(ArrayList<String> keys) {
        // Remove any columns that no longer exist.
        if (getTableComp() != null) {
            TableColumnModel model = getTableComp().getTableComponent().getColumnModel();
            int idx = 0;
            while (getTableComp().getTableComponent().getColumnCount() > idx) {
                TableColumn col = model.getColumn(idx);
                String key = col.getIdentifier().toString();
                if (keys.contains(key)) {
                    idx++;
                    EchoColumnData coldata = tableColumns.get(key);
                    if (coldata != null)
                        coldata.setWidth(col.getWidth());
                } else {
                    getTableComp().removeColumn(col);
                }
            }
        }
    }

    private void updateColumnsProperty() {
        if (tableColumns.size() > 0) {
            this.columns = getColumns();
            fire("nodename", this.columns, getColumns());
            WindowManager.getDefault().findTopComponent("properties").repaint();
        } else {
            this.columns = "";
            fire("nodename", this.columns, "");
            WindowManager.getDefault().findTopComponent("properties").repaint();
        }
        designerPage.setModified(true);
    }

    public void createNewColumns() {
        if (getTableComp() == null) {
            return;
        }

        TableColumn newColumn;
        int i = 0;
        TableColumnModel model = getTableComp().getTableComponent().getColumnModel();
        for (String key : tableColumns.keySet()) {
            EchoColumnData colData = tableColumns.get(key);
            boolean haveCol = false;
            TableColumn colmn = null;
            for (int j = 0; j < model.getColumnCount(); j++) {
                colmn = model.getColumn(j);
                String col = (String) colmn.getIdentifier();
                if (col.equals(key)) {
                    haveCol = true;
                    break;
                }
            }
            index++;
            int colwidth = 50;
            if (colData.getWidth() > 0) {
                colwidth = colData.getWidth();
            }
            if (haveCol && (colmn != null)) {
                if (isNullOrEmpty(colData.getHeader())) {
                    colmn.setHeaderValue(colData.getColName());
                } else {
                    colmn.setHeaderValue(colData.getHeader());
                }
                designerPage.getDropPanel(parentId).repaint();
                int idx = model.getColumnIndex(key);
                model.moveColumn(idx, i);
                colmn.setWidth(colwidth);
            } else {
                newColumn = new TableColumn(i, colwidth);      
                newColumn.setMinWidth(50);
                if (isNullOrEmpty(colData.getHeader())) {
                    newColumn.setHeaderValue(colData.getColName());
                } else {
                    newColumn.setHeaderValue(colData.getHeader());
                }
                newColumn.setIdentifier(key);
                model.addColumn(newColumn);
                colData.setWidth(newColumn.getWidth());
                //Ticket #502
                getTableComp().addColumnHeaderStyle(newColumn, colData);
            }
            i++;
        }
        designerPage.setModified(true);
    }

    //Ticket #298
    /**
     * Column has moved
     */
    public void reorderColumns() {
        if (getTableComp() == null) {
            return;
        }
        HashMap<String, EchoColumnData> tmp = new HashMap<>();
        tmp.putAll(tableColumns);
        tableColumns.clear();
        JTable theTable = getTableComp().getTableComponent();
        TableColumnModel model = getTableComp().getTableComponent().getColumnModel();
        for (int i = 0; i < theTable.getColumnCount(); i++) {
            String colName = (String) model.getColumn(i).getIdentifier();
            EchoColumnData colData = tmp.get(colName);
            if (colData != null) {
                tableColumns.put(colName, colData);
            }
        }
        tmp.clear();
        updateColumnsProperty();
        createSql();
        designerPage.setModified(true);
    }
    /**
     * Column has moved, header has changed, or column has been sized.
     */
    public void setColumnHeadersAndWidths() {
        if (getTableComp() == null) {
            return;
        }
        JTable theTable = getTableComp().getTableComponent();
        TableColumnModel model = getTableComp().getTableComponent().getColumnModel();
        for (int i = 0; i < theTable.getColumnCount(); i++) {
            String colName = (String) model.getColumn(i).getIdentifier();
            EchoColumnData colData = tableColumns.get(colName);
            if (colData == null) {
                tableColumns.put(colName,
                        new EchoColumnData(colName, model.getColumn(i).getWidth(),
                        (String) model.getColumn(i).getHeaderValue()));
            } else {
                colData.setHeader((String) model.getColumn(i).getHeaderValue());
                colData.setWidth(model.getColumn(i).getWidth());
            }
        }
        updateColumnsProperty();
        createSql();
        designerPage.setModified(true);
    }

    /**
     *
     * @return
     */
    public String getDataType(String column) {
        EchoColumnData col = tableColumns.get(column);
        if (col == null) {
            return null;
        } else {
            return col.getDataType();
        }
    }

    /**
     *
     *
     *
     * @param dataType
     */
    public void setDataType(String colName, String dataType) {
        EchoColumnData col = tableColumns.get(colName);
        if (col != null) {
            col.setDataType(EchoUtil.getDataTypeString(dataType));
            designerPage.setModified(true);
        }
    }

    /**
     *
     * @return
     */
    public boolean getHaveKeyCol() {
        return haveKeyCol;
    }

    /**
     *
     * @param isKeyCol
     */
    public void setHaveKeyCol(boolean haveKeyCol) {
        this.haveKeyCol = haveKeyCol;
        designerPage.setModified(true);
    }

    /**
     *
     * @return
     */
    public boolean getHaveGUIDCol() {
        return haveGUIDCol;
    }

    /**
     *
     * @param isGUIDCol
     */
    public void setHaveGUIDCol(boolean haveGUIDCol) {

        this.haveGUIDCol = haveGUIDCol;
        designerPage.setModified(true);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String getColumn() {
        // This is different for tables - return the columns in the form
        // col1, col2, col4
        return columns;
    }

    public String getHeaders() {
        boolean haveFirst = false;
        StringBuilder headers = new StringBuilder();
        for (EchoColumnData col : tableColumns.values()) {
            if (!haveFirst) {
                headers.append(col.getHeader());
                haveFirst = true;
            } else {
                headers.append(",").append(col.getHeader());
            }
        }
        return headers.toString();
    }

    public String getWidths() {
        boolean haveFirst = false;
        StringBuilder widths = new StringBuilder();
        for (EchoColumnData col : tableColumns.values()) {
            if (!haveFirst) {
                widths.append(col.getWidth());
                haveFirst = true;
            } else {
                widths.append(",").append(col.getWidth());
            }
        }
        return widths.toString();
    }

    public String getDataTypes() {
        boolean haveFirst = false;
        StringBuilder dataTypes = new StringBuilder();
        for (EchoColumnData col : tableColumns.values()) {
            if (!haveFirst) {
                dataTypes.append(col.getDataType());
                haveFirst = true;
            } else {
                dataTypes.append(",").append(col.getDataType());
            }
        }
        return dataTypes.toString();
    }

    @Override
    public void updateName(int index) {
        getTableComp().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"char", "varchar", "longvarchar", "uniqueidentifier", "datetime", "date",
                    "time", "int", "numeric", "numeric()", "double", "smalldatetime",
                    "decimal", "tinyint", "smallint", "bigint", "real", "float", "money"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    public String getDefaultSortColumns() {
        return defaultSortColumns;
    }

    public void setDefaultSortColumns(String sortCols) {
        this.defaultSortColumns = sortCols;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getTableComp().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "Table";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    @Override
    public void setTableFromDefault(String table) {
        if (!(table.equals(this.table))) {
            clearColumn();
        }
        this.parentContainer = "Default";
        this.table = table;
    }

    @Override
    public boolean getIsKeyCol() {
        return false;
    }

    @Override
    public boolean getisGUIDCol() {
        return false;
    }

    @Override
    public void clearUncopiableProperties(String table) {
        this.table = table;
        columns = "";
        haveKeyCol = false;
        haveGUIDCol = false;
    }

    @Override
    public String getFormLinkColumn() {
        return formLinkColumn;
    }

    @Override
    public String getFormLinkToColumn() {
        return formLinkToColumn;
    }

    public void setFormLinkToColumn(String formLinkToColumn) {
        List values = null;
        String linkColumns;
        String tbl = "";
        if (!formLinkToColumn.equals("") && (formLinkToColumn.indexOf(";") != -1)) {
            values = new ArrayList();
            tbl = formLinkToColumn.substring(0, formLinkToColumn.indexOf(";"));
            linkColumns = formLinkToColumn.substring(formLinkToColumn.indexOf(";") + 1, formLinkToColumn.length());
            if (!linkColumns.equals("")) {
                StringTokenizer tokens = new StringTokenizer(linkColumns);
                while (tokens.hasMoreTokens()) {
                    values.add(tokens.nextToken());
                }
            }
        }
        if ((values != null) && (values.size() > 0)) {
            String col = tbl + "." + (String) values.get(0);
            fire("nodename", this.formLinkToColumn, col);
            this.formLinkToColumn = col;
        } else {
            fire("nodename", this.formLinkToColumn, formLinkToColumn);
            this.formLinkToColumn = formLinkToColumn;
        }
    }

    public void setFormLinkColumn(String formLinkColumn) {
        List values = null;
        String linkColumns;
        
        if (!formLinkColumn.equals("") && (formLinkColumn.indexOf(";") != -1)) {
            values = new ArrayList();
            linkColumns = formLinkColumn.substring(formLinkColumn.indexOf(";") + 1, formLinkColumn.length());
            if (!linkColumns.equals("")) {
                StringTokenizer tokens = new StringTokenizer(linkColumns);
                while (tokens.hasMoreTokens()) {
                    values.add(tokens.nextToken());
                }
            }
        }
        if ((values != null) && (values.size() > 0)) {
            fire("nodename", this.formLinkColumn, (String) values.get(0));
            this.formLinkColumn = (String) values.get(0);
        } else {
            this.formLinkColumn = "";
        }
    }

    public FormData getPopupFdNextForm() {
        return popupFdNextForm;
    }

    public void setPopupFdNextForm(FormData popupFdNextForm) {
        if (popupFdNextForm != null) {
            resetButtonTypeProperties();
        }
        this.popupFdNextForm = popupFdNextForm;
    }

    public String getPopupFormName() {
        return popupFormName;
    }

    public void setPopupFormName(String popupFormName) {
        if (!isNullOrEmpty(popupFormName)) {
            resetButtonTypeProperties();
        }
        this.popupFormName = popupFormName;
    }

    @Override
    public String getUiLinkColumn() {
        return uiLinkColumn;
    }

    public void setUiLinkColumn(String uiLinkColumn) {
        final List values = new ArrayList();
        if (uiLinkColumn.indexOf(";") > -1) {
            final String columns = uiLinkColumn.substring(uiLinkColumn.indexOf(";") + 1);
            if (!columns.equals("")) {
                StringTokenizer tokens = new StringTokenizer(columns);
                while (tokens.hasMoreTokens()) {
                    values.add(tokens.nextToken());
                }
            }
        }
        if (values != null && values.size() > 0) {
            final String value = (String) values.get(0);
            fire("nodename", this.uiLinkColumn, value);
            this.uiLinkColumn = value;
        } else {
            this.uiLinkColumn = "";
        }
    }

    public String getPopupUiName() {
        return popupUiName;
    }

    public void setPopupUiName(String popupUiName) {
        if (popupUiName != null && !"".equals(popupUiName.trim())) {
            resetButtonTypeProperties();
            final String[] values = popupUiName.split("\\|");
            this.popupUiName = values[0];
            if (values.length > 1) {
                setUiLinkToColumn(values[1]);
            }
        } else {
            this.popupUiName = "";
            setUiLinkToColumn("");
        }
        designerPage.setModified(true);
    }

    @Override
    public String getUiLinkToColumn() {
        return uiLinkToColumn;
    }

    public void setUiLinkToColumn(String uiLinkToColumn) {
        this.uiLinkToColumn = uiLinkToColumn;
    }

    private void resetButtonTypeProperties() {
        this.popupFormName = "";
        this.formLinkColumn = "";
        this.formLinkToColumn = "";
        this.popupUiName = "";
        this.uiLinkToColumn = "";
        this.uiLinkColumn = "";
        this.formLinkToKeyField = "";
        this.formLinkFromKeyField = "";
        this.popupFdNextForm = null;
    }

    public String getFormLinkToKeyField() {
        return formLinkToKeyField;
    }

    public void setFormLinkToKeyField(String formLinkToKeyField) {
        fire("nodename", this.formLinkToKeyField, formLinkToKeyField);
        this.formLinkToKeyField = formLinkToKeyField;
    }

    public String getFormLinkFromKeyField() {
        return formLinkFromKeyField;
    }

    public void setFormLinkFromKeyField(String formLinkFromKeyField) {
        fire("nodename", this.formLinkFromKeyField, formLinkFromKeyField);
        this.formLinkFromKeyField = formLinkFromKeyField;
    }
}
