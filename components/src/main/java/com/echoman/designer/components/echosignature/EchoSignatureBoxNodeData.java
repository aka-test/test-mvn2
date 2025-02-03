/**
 *
 */
package com.echoman.designer.components.echosignature;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import org.openide.util.WeakListeners;

/**
 *
 * @author david.morin
 */
public class EchoSignatureBoxNodeData extends EchoBaseNodeData {

    private int tabOrder = 0;
    private int top;
    private int left;
    private int height;
    private int width;
    private boolean visible = true;
    // Possible future use
    //private String table = "";
    //private String parentContainer = "";
    private String signEvent = "";
    private String changeEvent = "";
    private String signatureType = "Script";
    
    private transient HashMap<String, String> columnList;

    public EchoSignatureBoxNodeData() {
    }

    public String getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
        getSignatureBox().setSignatureType(signatureType);
    }

// Possible future use
//    @Override
//    public void setTable(String parentContainer, String table) {
//        try {
//            if ((!(table.equals(this.table)))
//               && (!(DBConnections.getTableColumns(table).contains(column)))) {
//                clearColumn();
//            }
//        } catch (SQLException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        this.parentContainer = parentContainer;
//        this.table = table ;
//        columnList = null;
//    }
//
//    @Override
//    public String getParentContainer() {
//        return parentContainer;
//    }
//
//    public void setParentContainer(String parentContainer) {
//        this.parentContainer = parentContainer;
//    }

    public String getSignEvent() {
        return signEvent;
    }

    public void setSignEvent(String signEvent) {
        this.signEvent = signEvent;
        designerPage.setModified(true);
    }

    public String getChangeEvent() {
        return changeEvent;
    }

    public void setChangeEvent(String changeEvent) {
        this.changeEvent = changeEvent;
        designerPage.setModified(true);
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
            EchoSignatureBox obj = this.getSignatureBox();
            if (obj == null) {
                obj = createSignatureBox(dropPanel);
            } else {
                obj.setDropPanel(dropPanel);
                obj.createPopupMenu();
                obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                new Draggable(obj, dropPanel);
                new Resizeable(obj);
            }
            // Possible future use
            //defaultNewlyAddedProperties();
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

    // Possible future use
    // This is required when adding new properties to existing components.
    // Not required when adding completely new components.
    //private void defaultNewlyAddedProperties() {
    //}

    private EchoSignatureBox createSignatureBox(JPanel dropPanel) {
        int ltabOrder = tabOrder;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        boolean lvisible = visible;
        // Possible future use
        //String ltable = table;
        //String lparentContainer = parentContainer;
        //String lcolumn = column;
        String lsignEvent = signEvent;
        String lchangeEvent = changeEvent;
        String lsignatureType = signatureType;
        String lname = name;
        component = new EchoSignatureBox(this, index, dropPanel);
        getSignatureBox().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        tabOrder = ltabOrder;
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setWidth(lwidth);
        setVisible(lvisible);
        setSignatureType(lsignatureType);
        // Possible future use
        //table = ltable;
        //parentContainer = lparentContainer;
        setSignEvent(lsignEvent);
        setChangeEvent(lchangeEvent);
        return getSignatureBox();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoSignatureBoxNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoSignatureBoxNodeData nodeData = (EchoSignatureBoxNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setVisible(nodeData.getVisible());
        signatureType = nodeData.getSignatureType();
        // Possible future use
        //table = nodeData.getTable();
        //parentContainer = nodeData.getParentContainer();
        setSignEvent(nodeData.getSignEvent());
        setChangeEvent(nodeData.getChangeEvent());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoSignatureBoxNodeData nodeData = new EchoSignatureBoxNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoSignatureBoxNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
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
    public EchoSignatureBoxNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        component = new EchoSignatureBox(this, index, dropPanel);
        setName(component.getName());
        getSignatureBox().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoSignatureBox getSignatureBox() {
        return (EchoSignatureBox) component;
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
        if (this.tabOrder != tabOrder) {
            incNextTabOrder(tabOrder);
        }
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
        if (getSignatureBox() != null) {
            getSignatureBox().setLocation(getSignatureBox().getLocation().x, top);
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
        if (getSignatureBox() != null) {
            getSignatureBox().setLocation(left, getSignatureBox().getLocation().y);
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
        if (getSignatureBox() != null) {
            getSignatureBox().setSize(getSignatureBox().getWidth(), height);
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
        if (getSignatureBox() != null) {
            getSignatureBox().setSize(width, getSignatureBox().getHeight());
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
        return index + " - " + name;
    }

    @Override
    public void updateName(int index) {
        getSignatureBox().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

// Possible future use
//    @Override
//    public String getTable() {
//        return table;
//    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{""};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    final public void setName(String name) {
        super.setName(name);
        getSignatureBox().setName(getName());

    }

    @Override
    public String getNodeType() {
        return "SignatureBox";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

// Possible future use
//    @Override
//    public void setTableFromDefault(String table) {
//        try {
//            if ((!(table.equals(this.table)))
//               && (!(DBConnections.getTableColumns(table).contains(column)))) {
//                clearColumn();
//            }
//        } catch (SQLException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        this.parentContainer = "Default";
//        this.table = table;
//        columnList = null;
//    }
//
//    @Override
//    public String[] getTableList(String propertyName) {
//        return null;
//    }

    @Override
    public void clearUncopiableProperties(String table) {
        // Possible future use
        //this.table = table;
    }

}
