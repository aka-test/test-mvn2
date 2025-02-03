/**
 *
 */
package com.echoman.designer.components.echoradiobutton;

import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.jdesi.PopupFromFieldProperties;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.windows.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author david.morin
 */
public class EchoRadioButtonNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private int tabOrder = 0;
    private String caption;
    private int top;
    private int left;
    private int height;
    private int width;
    private String table = "";
    private String parentContainer = "";
    private String column = "";
    //private String itemValues = "";
    private String items = "";
    private String dataType;
    private boolean isKeyCol;
    private boolean isGUIDCol;
    private Font font = new Font("Open Sans Semibold", Font.BOLD, 14);
    private Color fontColor = new Color(1, 85, 149);
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean backgroundTransparent = true;
    private boolean visible = true;
    private boolean readOnly = false;
    private String selectedValue = "";
    private boolean allowNoSelection = false;
    private int colSize = 0;
    private String changeEvent = "";
    private String scrollEvent = "";
    private String insertEvent = "";
    private String deleteEvent = "";
    private String saveEvent = "";
    private int numberOfColumns = 1;
    private String alignment = "Left";
    private String buttonLayout = "Vertical";
    private ArrayList<PopupFromFieldProperties> popupFromFieldValue = new ArrayList<>();
    private transient HashMap<String, String> columnList;

    public EchoRadioButtonNodeData() {
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

    public int getColSize() {
        return colSize;
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
            try {
                designerPage = JDesiWindowManager.getActiveDesignerPage();
                JPanel dropPanel = designerPage.getDropPanel(parentId);
                listeners = Collections.synchronizedList(new LinkedList());
                EchoRadioButton obj = this.getRadioButton();
                if (obj == null) {
                    obj = createRadioButton(dropPanel);
                } else {
                    obj.initGroup();
                    obj.setDropPanel(dropPanel);
                    obj.createPopupMenu();
                    obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                    new Draggable(obj, dropPanel);
                    new Resizeable(obj);
                    if (isKeyCol) {
                        designerPage.setPKey(column);
                    }
                    setItems(items);
                    setSelectedValue(selectedValue);
                }
                defaultNewlyAddedProperties();
                dropPanel.add(obj);
                ArrayList<IEchoComponentNodeData> compList = designerPage.getCompList();
                compList.add(this);
                // Ticket 439
                dropPanel.setComponentZOrder(obj, 0);
            } catch (Exception e) {
            }
        } finally {
            loadingForm = false;
        }
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

    private EchoRadioButton createRadioButton(JPanel dropPanel) {
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
        String litems = items;
        String ldataType = dataType;
        boolean lisKeyCol = isKeyCol;
        boolean lisGUIDCol = isGUIDCol;
        Font lfont = font;
        Color lfontColor = fontColor;
        String lname = name;
        String lalign = alignment;
        String lbtnlayout = buttonLayout;
        Color lbackgroundColor = backgroundColor;
        boolean lbackgroundTransparent = backgroundTransparent;
        boolean lvisible = visible;
        boolean lreadOnly = readOnly;
        String lselectedValue = selectedValue;
        boolean lallowNoSelection = allowNoSelection;
        int lcolSize = colSize;
        String lchangeEvent = changeEvent;
        String lscrollEvent = scrollEvent;
        String linsertEvent = insertEvent;
        String ldeleteEvent = deleteEvent;
        String lsaveEvent = saveEvent;
        int lnumberOfColumns = numberOfColumns;
        component = new EchoRadioButton(this, index, dropPanel);
        getRadioButton().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        if ((!"".equals(lbtnlayout)) && (lbtnlayout != null)) {
            setButtonLayout(lbtnlayout);
        } else {
            setButtonLayout("Vertical");
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
        items = litems;
        dataType = ldataType;
        isKeyCol = lisKeyCol;
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = lisGUIDCol;
        setVisible(lvisible);
        setReadOnly(lreadOnly);
        // Ticket 544
        //setSelectedValue(lselectedValue);
        colSize = lcolSize;
        setChangeEvent(lchangeEvent);
        setScrollEvent(lscrollEvent);
        setInsertEvent(linsertEvent);
        setDeleteEvent(ldeleteEvent);
        setSaveEvent(lsaveEvent);
        setNumberOfColumns(lnumberOfColumns);
        setItems(items);
        // Ticket 544
        setSelectedValue(lselectedValue);
        setAllowNoSelection(lallowNoSelection);
        setFont(lfont);
        setFontColor(lfontColor);
        setBackgroundColor(lbackgroundColor);
        setBackgroundTransparent(lbackgroundTransparent);
        return getRadioButton();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }

    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoRadioButtonNodeData nodeData = (EchoRadioButtonNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setCaption(nodeData.getCaption());
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setAlignment(nodeData.getAlignment());
        setButtonLayout(nodeData.getButtonLayout());
        table = nodeData.getTable();
        parentContainer = nodeData.getParentContainer();
        popupFromFieldValue = nodeData.getPopupFromFieldValue();
        column = nodeData.getColumn();
        items = nodeData.getItems();
        dataType = nodeData.getDataType();
        isKeyCol = nodeData.getIsKeyCol();
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = nodeData.getisGUIDCol();
        setVisible(nodeData.getVisible());
        setReadOnly(nodeData.getReadOnly());
        setSelectedValue(nodeData.getSelectedValue());
        setAllowNoSelection(nodeData.getAllowNoSelection());
        colSize = nodeData.getColSize();
        setChangeEvent(nodeData.getChangeEvent());
        setScrollEvent(nodeData.getScrollEvent());
        setInsertEvent(nodeData.getInsertEvent());
        setDeleteEvent(nodeData.getDeleteEvent());
        setSaveEvent(nodeData.getSaveEvent());
        setNumberOfColumns(nodeData.getNumberOfColumns());
        setItems(items);
        setSelectedValue(selectedValue);
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBackgroundColor(nodeData.getBackgroundColor());
        setBackgroundTransparent(nodeData.getBackgroundTransparent());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoRadioButtonNodeData nodeData = new EchoRadioButtonNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    @Override
    public void remove() {
        super.remove();
    }

    public EchoRadioButtonNodeData(IEchoDesignerTopComponent designerPage) {
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
    public EchoRadioButtonNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab                
        //tabOrder = getUniqueTabOrder();
        component = new EchoRadioButton(this, index, dropPanel);
        setName(component.getName());
        getRadioButton().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoRadioButton getRadioButton() {
        return (EchoRadioButton) component;
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
            fire("nodename", this.column, "");
            this.column = "";
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
        colSize = 0;
        if (!((dataType == null) || (dataType.equals("")))) {
            int pos = dataType.indexOf("(");
            if (pos == -1) {
                colSize = -1;
            } else {
                try {
                    colSize = Integer.parseInt(dataType.substring(pos + 1,
                            dataType.indexOf(")")));
                } catch (Exception ex) {
                    colSize = 0;
                }
            }
        }
        this.dataType = EchoUtil.getDataTypeString(dataType);
        checkItemValues();
        designerPage.setModified(true);
    }

    private void checkItemValues() {
        if ((colSize != -1) && (colSize > 0)) {
            //Check to see if the value length is equal to the column size
            if (!"".equals(items)) {
                String[] lines = items.split("\\n");
                items = "";
                for (int i = 0; i < lines.length; i++) {
                    String item = lines[i].trim();
                    String val = item;
                    //  ITEM SHOULD NOT BE SPLIT WITH A SPACE - SEE ItemValueForm.java
                    int pos = item.lastIndexOf(" ");
                    if (pos > -1) {
                        val = item.substring(pos + 1);
                        item = item.substring(0, pos);
                    }
                    if ((val != null) && (!val.equals(""))) {
                        val = val.substring(0, (val.length() < colSize)
                                ? val.length() : colSize);
                    }
                    if (item.equals(val)) {
                        items = items + item + "\n";
                    } else {
                        items = items + item + " " + val + "\n";
                    }
                }
            }
        }
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
            if (getRadioButton() != null) {
                getRadioButton().setRadioButtonFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
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
            if (getRadioButton() != null) {
                getRadioButton().setRadioButtonForeground(this.fontColor);
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
            if (getRadioButton() != null) {
                getRadioButton().setBackground(this.backgroundColor);
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
        if (getRadioButton() != null) {
            getRadioButton().setOpaque(!backgroundTransparent);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     */
    private void resetSize() {
        //EchoUtil.resetSize(getRadioButton(), getRadioButton().getText());
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
        if (getRadioButton() != null) {
            ///getRadioButton().setText(caption);
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
        if (getRadioButton() != null) {
            getRadioButton().setLocation(getRadioButton().getLocation().x, top);
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
        if (getRadioButton() != null) {
            getRadioButton().setLocation(left, getRadioButton().getLocation().y);
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
        if (getRadioButton() != null) {
            getRadioButton().setSize(getRadioButton().getWidth(), height);
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
        if (getRadioButton() != null) {
            getRadioButton().setSize(width, getRadioButton().getHeight());
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
        getRadioButton().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"varchar", "char", "int"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
        if (component == null) {
            return;
        }

        EchoRadioButton btn = (EchoRadioButton) component;

        if (allowNoSelection && (selectedValue == null || "".equals(selectedValue))) {
            btn.resetButtons();
            designerPage.setModified(true);
            return;
        }

        String[] sa = items.split("\\n");
        boolean found = false;
        String firstItem = "";
        for (int i = 0; i < sa.length; i++) {
            String item = sa[i].trim();
            String val = item;
            int pos = item.lastIndexOf(" ");
            if (pos > -1) {
                val = item.substring(pos + 1);
                item = item.substring(0, pos);
            }
            if (i == 0) {
                firstItem = item;
            }
            if (selectedValue.equals(val)) {
                found = true;
                btn.selectedRadioButton(item);
                break;
            }
        }
        if (!found) {
            btn.selectedRadioButton(firstItem);
            setSelectedValueByCaption(firstItem);
        }
        designerPage.setModified(true);
    }

    public void setSelectedValueByCaption(String caption) {
        boolean found = false;
        if ("".equals(caption)) {
            setSelectedValue("");
            fire("refresh", 0, 0);
        } else {
            String[] sa = items.split("\\n");
            for (int i = 0; i < sa.length; i++) {
                String item = sa[i].trim();
                String val = item;
                int pos = item.lastIndexOf(" ");
                if (pos > -1) {
                    val = item.substring(pos + 1);
                    item = item.substring(0, pos);
                }
                if (caption.equals(item)) {
                    found = true;
                    setSelectedValue(val);
                    fire("refresh", 0, 0);
                    break;
                }
            }
            if (!found) {
                setSelectedValue("");
                fire("refresh", 0, 0);
            }
        }
    }

    public boolean getAllowNoSelection() {
        return allowNoSelection;
    }

    public void setAllowNoSelection(boolean allowNoSelection) {
        this.allowNoSelection = allowNoSelection;
        if (this.items != null && !allowNoSelection && (selectedValue == null || "".equals(selectedValue))) {
            final String[] items = this.items.split("\\n");
            if (items.length != 0) {
                final String firstItem = items[0];
                String item = firstItem.trim();
                String val = item;
                int pos = item.lastIndexOf(" ");
                if (pos > -1) {
                    val = item.substring(pos + 1);
                }
                setSelectedValue(val);
            }

        }
        designerPage.setModified(true);
    }

    public int getItemValueLength() {
        int len = 0;
        String[] sa = items.split("\\n");
        for (String s : sa) {
            String val = s.trim();
            int pos = val.lastIndexOf(" ");
            if (pos > -1) {
                val = val.substring(pos + 1).trim();
            }
            if (val.length() > len) {
                len = val.length();
            }
        }
        return len;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
        if (component == null) {
            return;
        }

        EchoRadioButton btn = (EchoRadioButton) component;
        String selected = getSelectedValue();
        btn.removeRadioButtons();
        setSelectedValue("");
        if (!"".equals(items)) {
            String[] sa = items.split("\\n");
            for (int i = 0; i < sa.length; i++) {
                String s = sa[i].trim();
                int pos = s.lastIndexOf(" ");
                if (pos > -1) {
                    s = s.substring(0, pos);
                }
                btn.addRadioButton(s);
            }
            if ("Vertical".equals(buttonLayout)) {
                int len = sa.length * 30;
                if (len > btn.getHeight()) {
                    btn.setSize(btn.getWidth(), len);
                }
            }
            if (!allowNoSelection && (selected == null || "".equals(selected))) {
                selected = sa[0].trim();
                int pos = selected.lastIndexOf(" ");
                if (pos > -1) {
                    selected = selected.substring(pos + 1);
                }
            }
            setSelectedValue(selected);
            checkItemValues();
        }
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getRadioButton().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "RadioButton";
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
        if (getRadioButton() != null) {
            if ("Left".equals(this.alignment)) {
                getRadioButton().setAlignment(SwingConstants.LEFT);
            } else {
                getRadioButton().setAlignment(SwingConstants.RIGHT);
            }
        }
    }

    public String getButtonLayout() {
        return buttonLayout;
    }

    public void setButtonLayout(String layout) {
        if (EchoUtil.isNullOrEmpty(layout)) {
            this.buttonLayout = "Vertical";
        } else {
            this.buttonLayout = layout;
        }
        if (getRadioButton() != null) {
            getRadioButton().setButtonLayout(this.buttonLayout);
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
