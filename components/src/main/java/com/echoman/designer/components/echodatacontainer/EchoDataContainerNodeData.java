/**
 *
 */
package com.echoman.designer.components.echodatacontainer;

import java.awt.Point;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echotable.EchoTableNodeData;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoDataContainerNodeData;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

/**
 *
 * @author david.morin
 */
public class EchoDataContainerNodeData extends EchoBaseNodeData implements IEchoDataContainerNodeData {
    private int top;
    private int left;
    private int height;
    private int width;
    private int zOrder;
    private String table = "";
    private String parentContainer = "";
    private String sortOrder = "";
    private String filterSql = "";
    private String masterTable= "";
    private String masterLinkField = "";
    private String linkField = "";
    private boolean preventMultipleRecords = false;
    private transient String[] tableList;
    private transient HashMap<String, String> columnList;
    // Ticket 443
    private transient HashMap<String, String> masterColumnList;

    public String getFilterSql() {
        return filterSql;
    }

    public void setFilterSql(String filterSql) {
        String error = EchoUtil.dangerousSqlCheck(filterSql); 
        if (!"".equals(error)) {
            JOptionPane.showMessageDialog(null, error);
            this.filterSql = "";
        } else {
            this.filterSql = filterSql;
        }
    }

    public boolean getPreventMultipleRecords() {
        return preventMultipleRecords;
    }

    public void setPreventMultipleRecords(boolean preventMultipleRecords) {
        this.preventMultipleRecords = preventMultipleRecords;
    }

    public int getZOrder() {
        return zOrder;
    }

    /**
     *
     * @param zOrder
     */
    public void setNewZOrder(Integer zOrder) {
        this.zOrder = zOrder;
        designerPage.setModified(true);
    }

    /**
     *
     * @param zOrder
     */
    public void setZOrder(Integer zOrder) {
        this.zOrder = zOrder;
        fixZOrder();
        designerPage.setModified(true);
    }

    /**
    *
    */
    public void fixZOrder() {
        if (!((loadingForm) || (parentId == null))) {
            EchoUtil.fixZOrder(designerPage, parentId);
        }
    }

    @Override
    public String getMasterLinkField() {
        return masterLinkField;
    }

    @Override
    public void setMasterLinkField(String masterLinkField) {
        this.masterLinkField = masterLinkField;
        setBorderTitle();
    }

    @Override
    public String getMasterTable() {
        return masterTable;
    }

    private void setBorderTitle() {
        if (!(getDataContainer() == null)) {
            if (!masterTable.equals("")) {
                getDataContainer().getTb().setTitle(masterTable + "." + masterLinkField + " -> " + table + "." + linkField);
                getDataContainer().getSelectedTB().setTitle(masterTable + "." + masterLinkField + " -> " + table + "." + linkField);
            } else {
                 getDataContainer().getTb().setTitle(table);
                getDataContainer().getSelectedTB().setTitle(table);
            }
        }
    }

    @Override
    public void setMasterTable(String parentContainer, String masterTable) {
        this.parentContainer = parentContainer;
        if (!(parentContainer.equals("Default"))) {
            this.masterTable = masterTable;
            if (this.masterLinkField.equals("")) {
               this.masterLinkField = EchoUtil.getPrimaryKeyForTable(masterTable);
            } else {
                try {
                    if (!(DBConnections.getTableColumns(masterTable)).contains(masterLinkField)) {
                        this.masterLinkField = "";
                        this.linkField = "";
                    }
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            // Check to see if the new master table has the link field...
        } else {
            this.masterTable = "";
            this.masterLinkField = "";
            this.linkField = "";
        }
        
        // Ticket 443

        masterColumnList = null;
        getMasterColumnList("Master Link Field");
        setBorderTitle();
    }

    @Override
    public String getLinkField() {
        return linkField;
    }

    @Override
    public void setLinkField(String linkField) {
        this.linkField = linkField;
        setBorderTitle();
    }

    @Override
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        String error = EchoUtil.dangerousSqlCheck(sortOrder); 
        if (!"".equals(error)) {
            JOptionPane.showMessageDialog(null, error);
            this.sortOrder = "";
        } else {
            this.sortOrder = sortOrder;
        }
    }

    @Override
    public String getTable() {
        return table;
    }
    
    public void setTable(String table) {
        doSetTable(parentContainer, table);
    }

    @Override
    public void setTableFromDefault(String tableName) {
        this.parentContainer = "Default";
        this.table = tableName;
        setBorderTitle();
        columnList = null;
    }

    public void doSetTable(String parentContainer, String tableName) {
        this.table = tableName;
        
        try {
            // If the new table doesn't have the current column, clear it.
            if ((!tableName.equals("")) && (!linkField.equals("")) && (!(DBConnections.getTableColumns(tableName)).contains(linkField))) {
                setLinkField("");
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (!((loadingForm) || (clonedData) || (component == null) || (component.getNode() == null) || (component.getNode().getIsDestroying()))) {
            DataContainerManager.checkContainerContainers(this,getDesignerPage().getCompList());
            updateContainerComponents();
        }

        setBorderTitle();
        columnList = null;
    }
    
    @Override
    public void setTable(String parentContainer, String tableName) {
        doSetTable(parentContainer, tableName);
    }

    @Override
    public String getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    public void updateContainerComponents() {
        // Here we need to loop the components to update all contained with the new table
        DataContainerManager.updateContainerComponents(parentId, getDesignerPage().getCompList());
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
            EchoDataContainer obj = this.getDataContainer();
            if (obj == null) {
                obj = createDataContainer(dropPanel);
            } else {
                obj.setDropPanel(dropPanel);
                obj.createPopupMenu();
                obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                new Draggable(obj, dropPanel);
                new Resizeable(obj);
            }
            dropPanel.add(obj);
            List<IEchoComponentNodeData> compList = designerPage.getCompList();
            compList.add(this);
        } finally {
            loadingForm = false;
        }
        // set any new properties that have been added since the form was created.
        defaultNewlyAddedProperties();
        // at the end returns itself
        return this;
    }

    private void defaultNewlyAddedProperties() {
        if ((parentContainer == null) || (parentContainer.equals(""))) {
            parentContainer = "";
            DataContainerManager.checkContainerContainers(this, getDesignerPage().getCompList());
            updateContainerComponents();
        }
        
        if (filterSql == null) {
            filterSql = "";
        }

    }
    
    private EchoDataContainer createDataContainer(JPanel dropPanel) {
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        int lzOrder = zOrder;
        String lname = name;
        String ltableName = table;
        component = new EchoDataContainer(this, index, dropPanel);
        getDataContainer().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        if ((!"".equals(lname)) && (lname != null)) 
            setName(lname);
        else 
            //Ticket #438
            //setName(getNodeType() + index);
            setName(getNodeType());
        //Ticket #202
        setLeft(lleft);
        setTop(ltop);
        setWidth(lwidth);
        setHeight(lheight);
        setTable("", ltableName);
        setZOrder(lzOrder);
        return getDataContainer();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }

    /**
     *
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoDataContainerNodeData nodeData = (EchoDataContainerNodeData) data;
        setLeft(nodeData.getLeft());
        setTop(nodeData.getTop());
        setWidth(nodeData.getWidth());
        setHeight(nodeData.getHeight());
        setTable("", nodeData.getTable());
        preventMultipleRecords = nodeData.preventMultipleRecords;
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoDataContainerNodeData nodeData = new EchoDataContainerNodeData(designerPage);
        nodeData.clonedData = true;
        nodeData.copy(this);
        return nodeData;
    }

    public EchoDataContainerNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
        zOrder = -1;
    }

    /**
     * 
     * @param glassPane
     * @param dropPanel
     */
    public EchoDataContainerNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        zOrder = -1;
        component = new EchoDataContainer(this, index, dropPanel);
        //Ticket #438
        //name = component.getName();
        setName(component.getName());
        getDataContainer().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    @Override
    public void remove() {
        super.remove();
        fixZOrder();
    }
    
    /**
     * 
     * @return
     */
    public final EchoDataContainer getDataContainer() {
        return (EchoDataContainer)component;
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
        if (getDataContainer() != null)
            getDataContainer().setLocation(getDataContainer().getLocation().x, top);
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
        if (getDataContainer() != null)
            getDataContainer().setLocation(left, getDataContainer().getLocation().y);
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
        if (getDataContainer() != null)
            getDataContainer().setSize(getDataContainer().getWidth(), height);
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
        if (getDataContainer() != null)
            getDataContainer().setSize(width, getDataContainer().getHeight());
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

    @Override
    public void updateName(int index) {
        getDataContainer().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[] {"form"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    //Ticket #77
    @Override
    public void setBorder() {
        if (EchoUtil.isSelected(getDesignerPage(), component)) {
            ((JComponent)component).setBorder(getDataContainer().getSelectedTB());
        } else {
            ((JComponent)component).setBorder(getDataContainer().getTb());
        }
    }

    @Override
    public final void setName(String name) {
        final String oldName = this.name;
        super.setName(name);
        getDataContainer().setName(getName());
        updateContainerReferences(oldName);
    }

    private void updateContainerReferences(String oldName) {
        if (EchoUtil.isNullOrEmpty(oldName)) {
            return;
        }
        // update reference to parentContainer property
        designerPage.getCompList().stream()
                .filter(nodeData -> nodeData instanceof IEchoDataAwareComponentNodeData)
                .map(nodeData -> (IEchoDataAwareComponentNodeData) nodeData)
                .filter(nodeData -> oldName.equals(nodeData.getParentContainer()))
                .forEach(nodeData -> nodeData.setParentContainer(name));

        // update reference to Text Field Popup From Field currentFormLinkFieldContainer property
        designerPage.getCompList().stream()
                .filter(nodeData -> nodeData instanceof EchoTextFieldNodeData)
                .map(nodeData -> (EchoTextFieldNodeData) nodeData)
                .filter(nodeData -> !EchoUtil.isNullOrEmpty(nodeData.getPopupFromFieldValue()))
                .flatMap(nodeData -> nodeData.getPopupFromFieldValue().stream())
                .filter(popupFromFieldProperties -> oldName.equals(popupFromFieldProperties.getCurrentFormLinkFieldContainer()))
                .forEach(popupFromFieldProperties -> popupFromFieldProperties.setCurrentFormLinkFieldContainer(name));

        // update reference to Grid Popup formLinkFromKeyField property
        designerPage.getCompList().stream()
                .filter(nodeData -> nodeData instanceof EchoTableNodeData)
                .map(nodeData -> (EchoTableNodeData) nodeData)
                .filter(nodeData -> !EchoUtil.isNullOrEmpty(nodeData.getFormLinkFromKeyField()))
                .filter(nodeData -> nodeData.getFormLinkFromKeyField().startsWith(oldName + "."))
                .forEach(nodeData -> nodeData.setFormLinkFromKeyField(nodeData.getFormLinkFromKeyField().replaceFirst(oldName, name)));
    }

    @Override
    public String getNodeType() {
        return "DataContainer";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    @Override
    public void clearColumn() {
   }

    @Override
    public String getColumn() {
        return "";
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
    public String[] getTableList(String propertyName) {
        if (tableList == null) {
            tableList = EchoUtil.getTables(this, propertyName);
        }
        return tableList;
    }

    @Override
    public HashMap<String, String> getColumnList(String propertyName) {
        if (columnList == null) {
            columnList = EchoUtil.getTableColumns(this, propertyName);
        }
        return columnList;
    }

    @Override
    // Ticket 443
    public HashMap<String, String> getMasterColumnList(String propertyName) {
        if (masterColumnList == null) {
            masterColumnList = EchoUtil.getTableColumns(this, propertyName);
        }
        return masterColumnList;
    }

    @Override
    public void clearUncopiableProperties(String table) {
        this.table = table;
        sortOrder = "";
        masterTable= "";
        masterLinkField = "";
        linkField = "";
    }
}
