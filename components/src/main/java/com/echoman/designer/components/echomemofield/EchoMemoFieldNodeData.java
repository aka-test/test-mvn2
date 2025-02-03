/**
 *
 */
package com.echoman.designer.components.echomemofield;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.windows.WindowManager;

/**
 *
 * @author david.morin
 */
public class EchoMemoFieldNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private int tabOrder;
    private int top;
    private int left;
    private int height;
    private int width;
    private Font font = new Font("Open Sans", Font.PLAIN, 14);
    private Color fontColor = new Color(102, 102, 102);
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean visible = true;
    private boolean readOnly = false;
    private String table = "";
    private String parentContainer = "";
    private String column = "";
    private String dataType;
    private boolean isKeyCol;
    private boolean isGUIDCol;
    private String enterEvent = "";
    private String exitEvent = "";
    private boolean required = false;
    private String defaultValue = "";
    private String changeEvent = "";
    private String scrollEvent = "";
    private String insertEvent = "";
    private String deleteEvent = "";
    private String saveEvent = "";
    private transient HashMap<String, String> columnList;

    public EchoMemoFieldNodeData() {
    }


    @Override
    public void setTable(String parentContainer, String table) {
        try {
            if ((!(table.equals(this.table)))
               && (!(DBConnections.getTableColumns(table).contains(column)))) {
                clearColumn();
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        this.parentContainer = parentContainer;
        this.table = table ;
        columnList = null;
    }

    @Override
    public String getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    public String getChangeEvent() {
        return changeEvent;
    }

    public void setChangeEvent(String changeEvent) {
        this.changeEvent = changeEvent;
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
        designerPage.setModified(true);
    }

    /**
     *
     * @return
     */
    public boolean getRequired() {
        return required;
    }

    /**
     *
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (required) {
                set.getProperties()[13].setHidden(true);
                set.getProperties()[14].setHidden(true);
            } else {
                set.getProperties()[13].setHidden(false);
                set.getProperties()[14].setHidden(false);
            }
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
    }

    /**
     *
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
            EchoMemoField obj = this.getMemoField();
            if (obj == null) {
                obj = createMemo(dropPanel);
            } else {
                obj.setDropPanel(dropPanel);
                obj.createPopupMenu();
                obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                new Draggable(obj, dropPanel);
                new Resizeable(obj);
            }
            defaultNewlyAddedProperties();
            dropPanel.add(obj);
            ArrayList<IEchoComponentNodeData> compList = designerPage.getCompList();
            compList.add(this);
            if (isKeyCol) {
                designerPage.setPKey(column);
            }
            // Ticket 439
            dropPanel.setComponentZOrder(obj, 0);
        } finally {
            loadingForm = false;
        }
        // at the end returns itself
        return this;
    }

    private void defaultNewlyAddedProperties() {
        if (hintText == null) {
            hintText = "";
        }
        
        if (parentContainer == null) {
            parentContainer = "";
            DataContainerManager.checkContainerComponents(this, getDesignerPage().getCompList());
            if (getTable().equals("")) {
                setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
            }
        }
    }

    private EchoMemoField createMemo(JPanel dropPanel) {
        int ltabOrder = tabOrder;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        Font lfont = font;
        Color lfontColor = fontColor;
        Color lbackgroundColor = backgroundColor;
        boolean lvisible = visible;
        boolean lreadOnly = readOnly;
        String ltable = table;
        String lparentContainer = parentContainer;
        String lcolumn = column;
        String ldataType = dataType;
        boolean lisKeyCol = isKeyCol;
        boolean lisGUIDCol = isGUIDCol;
        String lenterEvent = enterEvent;
        String lexitEvent = exitEvent;
        boolean lrequired = required;
        String ldefaultValue = defaultValue;
        String lchangeEvent = changeEvent;
        String lscrollEvent = scrollEvent;
        String linsertEvent = insertEvent;
        String ldeleteEvent = deleteEvent;
        String lsaveEvent = saveEvent;
        String lname = name;
        component = new EchoMemoField(this, index, dropPanel);
        getMemoField().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
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
        setVisible(lvisible);
        setReadOnly(lreadOnly);
        table = ltable;
        parentContainer = lparentContainer;
        column = lcolumn;
        //Ticket #192
        getMemoField().setText(column);
        dataType = ldataType;
        isKeyCol = lisKeyCol;
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = lisGUIDCol;
        setEnterEvent(lenterEvent);
        setExitEvent(lexitEvent);
        required = lrequired;
        setDefaultValue(ldefaultValue);
        setChangeEvent(lchangeEvent);
        setScrollEvent(lscrollEvent);
        setInsertEvent(linsertEvent);
        setDeleteEvent(ldeleteEvent);
        setSaveEvent(lsaveEvent);
        return getMemoField();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoMemoFieldNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoMemoFieldNodeData nodeData = (EchoMemoFieldNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBackgroundColor(nodeData.getBackgroundColor());
        setVisible(nodeData.getVisible());
        setReadOnly(nodeData.getReadOnly());
        table = nodeData.getTable();
        parentContainer = nodeData.getParentContainer();
        column = nodeData.getColumn();
        if (getMemoField() != null)
            getMemoField().setText(column);
        dataType = nodeData.getDataType();
        isKeyCol = nodeData.getIsKeyCol();
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = nodeData.getisGUIDCol();
        setEnterEvent(nodeData.getEnterEvent());
        setExitEvent(nodeData.getEnterEvent());
        required = nodeData.getRequired();
        setDefaultValue(nodeData.getDefaultValue());
        setChangeEvent(nodeData.getChangeEvent());
        setScrollEvent(nodeData.getScrollEvent());
        setInsertEvent(nodeData.getInsertEvent());
        setDeleteEvent(nodeData.getDeleteEvent());
        setSaveEvent(nodeData.getSaveEvent());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoMemoFieldNodeData nodeData = new EchoMemoFieldNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoMemoFieldNodeData(IEchoDesignerTopComponent designerPage) {
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
    public EchoMemoFieldNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab                
        //tabOrder = getUniqueTabOrder();
        component = new EchoMemoField(this, index, dropPanel);
        setName(component.getName());
        getMemoField().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoMemoField getMemoField() {
        return (EchoMemoField) component;
    }

    /**
     * 
     * @return
     */
    public String getEnterEvent() {
        return enterEvent;
    }

    /**
     * 
     * @param enterEvent
     */
    public void setEnterEvent(String enterEvent) {
        this.enterEvent = enterEvent;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public String getExitEvent() {
        return exitEvent;
    }

    /**
     * 
     * @param exitEvent
     */
    public void setExitEvent(String exitEvent) {
        this.exitEvent = exitEvent;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public Font getFont() {
        return font;
    }

    /**
     * 
     * @param font
     */
    public void setFont(Font font) {
        this.font = font;
        if (font != null) {
            if (getMemoField() != null) {
                getMemoField().setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
                resetSize();
            }
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public Color getFontColor() {
        return fontColor;
    }

    /**
     * 
     * @param fontColor
     */
    public void setFontColor(Color fontColor) {
        if (fontColor != null) {
            this.fontColor = new Color(fontColor.getRGB());
            if (getMemoField() != null)
                getMemoField().setForeground(this.fontColor);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * 
     * @param backgroundColor
     */
    public void setBackgroundColor(Color backgroundColor) {
        if (backgroundColor != null) {
            this.backgroundColor = new Color(backgroundColor.getRGB());
            if (getMemoField() != null)
                getMemoField().setBackground(this.backgroundColor);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     */
    private void resetSize() {
        EchoUtil.resetSizeNoAlign(getMemoField(), getMemoField().getText());
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public String getColumn() {
        return column;
    }

    /**
     * 
     * @param column
     */
    public void setColumn(String column) {
        List values = null;
        if (!column.equals("")) {
            values = new ArrayList();
            StringTokenizer tokens = new StringTokenizer(column);
            while (tokens.hasMoreTokens()) {
                values.add(tokens.nextToken());
            }
        }
        // See if we have more than the column
        if ((values != null) && (values.size() > 1)) {
            setDataType((String) values.get(1));
            // Ticket 337
            if (values.contains("char(36)") && values.contains("pKey")) {
                setisGUIDCol(true);
            } else {
                setisGUIDCol(false);
            }
            if (values.contains("pKey")) {
                designerPage.setPKey((String) values.get(0));
                setIsKeyCol(true);
            } else {
                setIsKeyCol(false);
            }
        } else {
            setDataType("");
            setisGUIDCol(false);
            setIsKeyCol(false);
        }

        if ((values != null) && (values.size() > 0)) {
            boolean changingColumnFixName = (name == null) ? false : name.contains(this.column);

            fire("nodename", this.column, (String) values.get(0));
            this.column = (String) values.get(0);
            if (getMemoField() != null)
                this.getMemoField().setText(this.column);
            // CDT-530
            if (changingColumnFixName || (name == null) || name.isEmpty() || name.contains(getNodeType())) {
                setName(this.column);
            }
        } else {
            this.column = "";
            fire("nodename", this.column, "");
            if (getMemoField() != null)
                this.getMemoField().setText("");
            WindowManager.getDefault().findTopComponent("properties").repaint();
        }
        designerPage.setModified(true);
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
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (visible) {
                if (readOnly) {
                    set.getProperties()[15].setHidden(true);
                } else {
                    set.getProperties()[15].setHidden(false);
                }
            } else {
                set.getProperties()[15].setHidden(true);
            }
            tabOrder = getTabOrder(tabOrder, visible);
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
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
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (readOnly) {
                set.getProperties()[15].setHidden(true);
            } else {
                if (visible) {
                    set.getProperties()[15].setHidden(false);
                } else {
                    set.getProperties()[15].setHidden(true);
                }
            }
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
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
        if (getMemoField() != null) {
            getMemoField().setLocation(getMemoField().getLocation().x, top);
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
        if (getMemoField() != null) {
            getMemoField().setLocation(left, getMemoField().getLocation().y);
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
        if (getMemoField() != null) {
            getMemoField().setSize(getMemoField().getWidth(), height);
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
        if (getMemoField() != null) {
            getMemoField().setSize(width, getMemoField().getHeight());
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
    public String getDataType() {
        return dataType;
    }

    /**
     * 
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = EchoUtil.getDataTypeString(dataType);
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean getIsKeyCol() {
        return isKeyCol;
    }

    /**
     * 
     * @param isKeyCol
     */
    public void setIsKeyCol(boolean isKeyCol) {
        this.isKeyCol = isKeyCol;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean getisGUIDCol() {
        return isGUIDCol;
    }

    /**
     * 
     * @param isGUIDCol
     */
    public void setisGUIDCol(boolean isGUIDCol) {
        this.isGUIDCol = isGUIDCol;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return index + " - " + column;
    }

    /**
     * 
     */
    @Override
    public void clearColumn() {
        setColumn("");
        designerPage.setModified(true);
    }

    @Override
    public void updateName(int index) {
        getMemoField().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"char", "varchar", "text"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getMemoField().setName(getName());

    }

    @Override
    public String getNodeType() {
        return "MemoField";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    @Override
    public void setTableFromDefault(String table) {
        try {
            if ((!(table.equals(this.table)))
               && (!(DBConnections.getTableColumns(table).contains(column)))) {
                clearColumn();
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        this.parentContainer = "Default";
        this.table = table;
        columnList = null;
    }

    @Override
    public String[] getTableList(String propertyName) {
        return null;
    }

    @Override
    public HashMap<String, String> getColumnList(String propertyName) {
        if (columnList == null) {
            columnList = EchoUtil.getTableColumns(this, propertyName);
        }
        return columnList;
    }

    @Override
    public void clearUncopiableProperties(String table) {
        this.table = table;
        column = "";
        dataType = "";
        isKeyCol = false;
        isGUIDCol = false;
    }

}
