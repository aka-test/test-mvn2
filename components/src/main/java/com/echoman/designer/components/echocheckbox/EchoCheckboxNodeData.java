/**
 *
 */
package com.echoman.designer.components.echocheckbox;

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
import javax.swing.SwingConstants;
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
import com.echoman.jdesi.PopupFromFieldProperties;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.windows.WindowManager;

/**
 *
 * @author david.morin
 */
public class EchoCheckboxNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private int tabOrder = 0;
    private String caption;
    private int top;
    private int left;
    private int height;
    private int width;
    private String table = "";
    String parentContainer = "";
    private String column = "";
    private String dataType;
    private boolean isKeyCol;
    private boolean isGUIDCol;
    private Font font = new Font("Open Sans Semibold", Font.BOLD, 14);
    private Color fontColor = new Color(1, 85, 149);
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean backgroundTransparent = true;
    private boolean visible = true;
    private boolean readOnly = false;
    private boolean selected = false;
    private String changeEvent = "";
    private String scrollEvent = "";
    private String insertEvent = "";
    private String deleteEvent = "";
    private String saveEvent = "";
    private String checkedValue = "";
    private String uncheckedValue = "";
    private String alignment = "Left";
    private ArrayList<PopupFromFieldProperties> popupFromFieldValue = new ArrayList<>();
    private transient HashMap<String, String> columnList;

    public EchoCheckboxNodeData() {
    }

    public ArrayList<PopupFromFieldProperties> getPopupFromFieldValue() {
        return popupFromFieldValue;
    }

    public void setPopupFromFieldValue(ArrayList<PopupFromFieldProperties> popupFromFieldValue) {
        this.popupFromFieldValue = popupFromFieldValue;
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
        this.table = table;
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
            EchoCheckbox obj = this.getCheckbox();
            if (obj == null) {
                obj = createCheckbox(dropPanel);
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

        if (popupFromFieldValue == null) {
            popupFromFieldValue = new ArrayList<>();
        }
    }

    public boolean getSelected() {
        return selected;
    }

    private EchoCheckbox createCheckbox(JPanel dropPanel) {
        int ltabOrder = tabOrder;
        String lcaption = caption;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        String ltable = table;
        String lparentContainer = parentContainer;
        ArrayList<PopupFromFieldProperties> lpopupFromFieldValue = popupFromFieldValue;
        String lcolumn = column;
        String ldataType = dataType;
        boolean lisKeyCol = isKeyCol;
        boolean lisGUIDCol = isGUIDCol;
        String lname = name;
        String lalign = alignment;
        Font lfont = font;
        Color lfontColor = fontColor;
        Color lbackgroundColor = backgroundColor;
        boolean lbackgroundTransparent = backgroundTransparent;
        boolean lvisible = visible;
        boolean lreadOnly = readOnly;
        boolean lselected = selected;
        String lchangeEvent = changeEvent;
        String lscrollEvent = scrollEvent;
        String linsertEvent = insertEvent;
        String ldeleteEvent = deleteEvent;
        String lsaveEvent = saveEvent;
        String lcheckedValue = checkedValue;
        String luncheckedValue = uncheckedValue;
        component = new EchoCheckbox(this, index, dropPanel);
        getCheckbox().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        if ((!"".equals(lalign)) && (lalign != null)) {
            setAlignment(lalign);
        } else {
            setAlignment("Left");
        }
        tabOrder = ltabOrder;
        setCaption(lcaption);
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setWidth(lwidth);
        table = ltable;
        parentContainer = lparentContainer;
        popupFromFieldValue = lpopupFromFieldValue;
        column = lcolumn;
        dataType = ldataType;
        isKeyCol = lisKeyCol;
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = lisGUIDCol;
        setFont(lfont);
        setFontColor(lfontColor);
        setBackgroundColor(lbackgroundColor);
        setBackgroundTransparent(lbackgroundTransparent);
        setVisible(lvisible);
        setReadOnly(lreadOnly);
        setSelected(lselected);
        setChangeEvent(lchangeEvent);
        setScrollEvent(lscrollEvent);
        setInsertEvent(linsertEvent);
        setDeleteEvent(ldeleteEvent);
        setSaveEvent(lsaveEvent);
        setCheckedValue(lcheckedValue);
        setUncheckedValue(luncheckedValue);
        return getCheckbox();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }

    /**
     *
     * @param EchoCheckBoxNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoCheckboxNodeData nodeData = (EchoCheckboxNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setCaption(nodeData.getCaption());
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setAlignment(nodeData.getAlignment());
        table = nodeData.getTable();
        parentContainer = nodeData.getParentContainer();
        popupFromFieldValue = nodeData.getPopupFromFieldValue();
        column = nodeData.getColumn();
        dataType = nodeData.getDataType();
        isKeyCol = nodeData.getIsKeyCol();
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = nodeData.getisGUIDCol();
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBackgroundColor(nodeData.getBackgroundColor());
        setBackgroundTransparent(nodeData.getBackgroundTransparent());
        setVisible(nodeData.getVisible());
        setReadOnly(nodeData.getReadOnly());
        setSelected(nodeData.getSelected());
        setChangeEvent(nodeData.getChangeEvent());
        setScrollEvent(nodeData.getScrollEvent());
        setInsertEvent(nodeData.getInsertEvent());
        setDeleteEvent(nodeData.getDeleteEvent());
        setSaveEvent(nodeData.getSaveEvent());
        setCheckedValue(nodeData.getCheckedValue());
        setUncheckedValue(nodeData.getUncheckedValue());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoCheckboxNodeData nodeData = new EchoCheckboxNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoCheckboxNodeData(IEchoDesignerTopComponent designerPage) {
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
    public EchoCheckboxNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab        
        //tabOrder = getUniqueTabOrder();
        component = new EchoCheckbox(this, index, dropPanel);
        setName(component.getName());
        caption = getCheckbox().getText();
        getCheckbox().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoCheckbox getCheckbox() {
        return (EchoCheckbox) component;
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
            // CDT-530
            if (changingColumnFixName || (name == null) || name.isEmpty() || name.contains(getNodeType())) {
                setName(this.column);
            }
        } else {
            this.column = "";
            fire("nodename", this.column, "");
            WindowManager.getDefault().findTopComponent("properties").repaint();
        }
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
            if (getCheckbox() != null) {
                getCheckbox().setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
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
            if (getCheckbox() != null) {
                getCheckbox().setForeground(this.fontColor);
            }
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
            if (getCheckbox() != null) {
                getCheckbox().setBackground(this.backgroundColor);
            }
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getBackgroundTransparent() {
        return backgroundTransparent;
    }

    /**
     * 
     * @param backgroundTransparent
     */
    public void setBackgroundTransparent(boolean backgroundTransparent) {
        this.backgroundTransparent = backgroundTransparent;
        if (getCheckbox() != null) {
            getCheckbox().setOpaque(!backgroundTransparent);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     */
    private void resetSize() {
        EchoUtil.resetSize(getCheckbox(), getCheckbox().getText());
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 
     * @param caption
     */
    public void setCaption(String caption) {
        fire("nodename", this.caption, caption);
        this.caption = caption;
        if (getCheckbox() != null) {
            getCheckbox().setText(caption);
            resetSize();
        }
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
        if (getCheckbox() != null) {
            getCheckbox().setLocation(getCheckbox().getLocation().x, top);
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
        if (getCheckbox() != null) {
            getCheckbox().setLocation(left, getCheckbox().getLocation().y);
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
        if (getCheckbox() != null) {
            getCheckbox().setSize(getCheckbox().getWidth(), height);
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
        if (getCheckbox() != null) {
            getCheckbox().setSize(width, getCheckbox().getHeight());
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
        return index + " - " + caption;
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
        getCheckbox().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"varchar", "char", "bit"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        designerPage.setModified(true);
        fire("refresh", 0, 0);
    }

    public String getCheckedValue() {
        return checkedValue;
    }

    public void setCheckedValue(String checkedValue) {
        this.checkedValue = checkedValue;
    }

    public String getUncheckedValue() {
        return uncheckedValue;
    }

    public void setUncheckedValue(String uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getCheckbox().setName(getName());

    }

    @Override
    public String getNodeType() {
        return "Checkbox";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        if (EchoUtil.isNullOrEmpty(alignment)) {
            this.alignment = "Left";
        } else {
            this.alignment = alignment;
        }
        if (getCheckbox() != null) {
            if ("Left".equals(this.alignment)) {
                getCheckbox().setHorizontalAlignment(SwingConstants.LEFT);
                getCheckbox().setHorizontalTextPosition(SwingConstants.RIGHT);
            } else {
                getCheckbox().setHorizontalAlignment(SwingConstants.RIGHT);
                getCheckbox().setHorizontalTextPosition(SwingConstants.LEFT);
            }
        }
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
