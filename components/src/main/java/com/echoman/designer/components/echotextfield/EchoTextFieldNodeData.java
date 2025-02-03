/**
 *
 */
package com.echoman.designer.components.echotextfield;

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
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.jdesi.PopupFromFieldProperties;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.windows.WindowManager;

import static com.echoman.designer.components.echotextfield.EchoTextFieldNode.PROPERTY_INDEX_READONLY;
import static com.echoman.designer.components.echotextfield.EchoTextFieldNode.PROPERTY_INDEX_REQUIRED;
import static com.echoman.designer.components.echotextfield.EchoTextFieldNode.PROPERTY_INDEX_VISIBLE;

/**
 *
 * @author david.morin
 */
public class EchoTextFieldNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private int tabOrder = 0;
    private int top;
    private int left;
    private int height;
    private int width;
    private Font font = new Font("Open Sans", Font.PLAIN, 14);
    private Color fontColor = new Color(102, 102, 102);
    private boolean border_yn = true;
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean visible = true;
    private boolean readOnly = false;
    private boolean timeZoneOffset = true;
    private String column = "";
    private String table = "";
    private String parentContainer = "";
    private String validationData = "";
    private String validationDescriptionSql = "";
    private boolean required = false;
    private String defaultValue = "";
    private String inputMask = "";
    private String displayMask = "";
    private String dataType;
    private boolean isKeyCol;
    private boolean isGUIDCol;
    private String validationCriteria = "";
    private String validationId = "";
    private String validationOrder = "";
    private String validationStoredColumnName = "";
    private String changeEvent = "";
    private String scrollEvent = "";
    private String insertEvent = "";
    private String deleteEvent = "";
    private String saveEvent = "";
    private String translationLabelId = "";
    private String captionLabelId = "";
    private ArrayList<PopupFromFieldProperties> popupFromFieldValue = new ArrayList<>();
    private transient String lastTranslationLabelId = "";
    private transient String lastCaptionLabelId = "";
    private transient HashMap<String, String> columnList;

    public EchoTextFieldNodeData() {
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
     * This will relink the textfield caption and translation labels.
     * They have to be set to transient on the textfield otherwise they
     * will be created twice through the object streamer when loading
     * a form back into the designer.
     */
    public static void linkCaptionTranslationLabels(IEchoDesignerTopComponent designerPage) {
        ArrayList compList = designerPage.getCompList();
        for (int i = 0; i < compList.size(); i++) {
            if (compList.get(i) instanceof EchoTextFieldNodeData) {
                EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) compList.get(i);
                for (int j = 0; j < compList.size(); j++) {
                    //Ticket #214
                    if (compList.get(j) instanceof EchoLabelNodeData) {
                        EchoLabelNodeData labelNodeData = (EchoLabelNodeData) compList.get(j);
                        if (labelNodeData.getId().equals(textNodeData.getTranslationLabelId())) {
                            textNodeData.getTextField().setTranslationLabel(labelNodeData.getLabel());
                            labelNodeData.addPropertyChangeListener(textNodeData.getTextField());
                        } else if (labelNodeData.getId().equals(textNodeData.getCaptionLabelId())) {
                            textNodeData.getTextField().setCaptionLabel(labelNodeData.getLabel());
                        }
                    }
                }
            }
        }
    }

    /**
     * This will relink the textfield caption and translation labels.
     * They have to be set to transient on the textfield otherwise they
     * will be created twice through the object streamer when loading
     * a form back into the designer.
     */
    public void linkCaptionTranslationLabels() {
        ArrayList compList = designerPage.getCompList();
        for (int i = 0; i < compList.size(); i++) {
            if (compList.get(i) instanceof EchoTextFieldNodeData) {
                EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) compList.get(i);
                if (textNodeData == this) {
                    for (int j = 0; j < compList.size(); j++) {
                        if (compList.get(j) instanceof EchoLabelNodeData) {
                            EchoLabelNodeData labelNodeData = (EchoLabelNodeData) compList.get(j);
                            if (labelNodeData.getId().equals(textNodeData.getTranslationLabelId())) {
                                textNodeData.getTextField().setTranslationLabel(labelNodeData.getLabel());
                                labelNodeData.addPropertyChangeListener(textNodeData.getTextField());
                            } else if (labelNodeData.getId().equals(textNodeData.getCaptionLabelId())) {
                                textNodeData.getTextField().setCaptionLabel(labelNodeData.getLabel());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * This will relink the textfield caption and translation labels.
     * They have to be set to transient on the textfield otherwise they
     * will be created twice through the object streamer when loading
     * a form back into the designer.
     */
    public void linkCaptionTranslationLabelsById() {
        ArrayList compList = designerPage.getCompList();
        for (int i = 0; i < compList.size(); i++) {
            if (compList.get(i) instanceof EchoTextFieldNodeData) {
                EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) compList.get(i);
                if (textNodeData.getId().equals(getId())) {
                    for (int j = 0; j < compList.size(); j++) {
                        if (compList.get(j) instanceof EchoLabelNodeData) {
                            EchoLabelNodeData labelNodeData = (EchoLabelNodeData) compList.get(j);
                            if (labelNodeData.getId().equals(textNodeData.getTranslationLabelId())) {
                                textNodeData.getTextField().setTranslationLabel(labelNodeData.getLabel());
                                labelNodeData.addPropertyChangeListener(textNodeData.getTextField());
                            } else if (labelNodeData.getId().equals(textNodeData.getCaptionLabelId())) {
                                textNodeData.getTextField().setCaptionLabel(labelNodeData.getLabel());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    public String getValidationStoredColumnName() {
        return validationStoredColumnName;
    }

    /**
     * 
     * @param validationStoredColumnName
     */
    public void setValidationStoredColumnName(String validationStoredColumnName) {
        this.validationStoredColumnName = validationStoredColumnName;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public String getValidationOrder() {
        return validationOrder;
    }

    /**
     * 
     * @param validationOrder
     */
    public void setValidationOrder(String validationOrder) {
        this.validationOrder = validationOrder;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public String getValidationCriteria() {
        return validationCriteria;
    }

    /**
     * 
     * @param validationCriteria
     */
    public void setValidationCriteria(String validationCriteria) {
        this.validationCriteria = validationCriteria;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public String getValidationId() {
        return validationId;
    }

    /**
     * 
     * @param validationId
     */
    public void setValidationId(String validationId) {
        this.validationId = validationId;
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
            EchoTextField obj = this.getTextField();
            if (obj == null) {
                obj = createText(dropPanel);
            } else {
                obj.setDropPanel(dropPanel);
                obj.createPopupMenu();
                obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
                new Draggable(obj, dropPanel);
                new Resizeable(obj);
            }
            defaultNewlyAddedProperties();
            dropPanel.add(obj);
            dropPanel.addContainerListener(obj);
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

    private EchoTextField createText(JPanel dropPanel) {
        int ltabOrder = tabOrder;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        Font lfont = font;
        Color lfontColor = fontColor;
        boolean lborder_yn = border_yn;
        Color lbackgroundColor = backgroundColor;
        boolean lvisible = visible;
        boolean lreadOnly = readOnly;
        boolean ltimeZoneOffset = timeZoneOffset;
        String lparentContainer = parentContainer;
        ArrayList<PopupFromFieldProperties> lpopupFromFieldValue = popupFromFieldValue;
        String ltable = table;
        String lcolumn = column;
        String lname = name;
        String lvalidationData = validationData;
        String lvalidationDescriptionSql = validationDescriptionSql;
        boolean lrequired = required;
        String ldefaultValue = defaultValue;
        String linputMask = inputMask;
        String ldisplayMask = displayMask;
        String ldataType = dataType;
        boolean lisKeyCol = isKeyCol;
        boolean lisGUIDCol = isGUIDCol;
        String lvalidationCriteria = validationCriteria;
        String lvalidationId = validationId;
        String lvalidationOrder = validationOrder;
        String lvalidationStoredColumnName = validationStoredColumnName;
        String lchangeEvent = changeEvent;
        String lscrollEvent = scrollEvent;
        String linsertEvent = insertEvent;
        String ldeleteEvent = deleteEvent;
        String lsaveEvent = saveEvent;
        String ltranslationLabelId = translationLabelId;
        String lcaptionLabelId = captionLabelId;
        component = new EchoTextField(this, index, dropPanel, false);
        getTextField().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        tabOrder = ltabOrder;
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setFont(lfont);
        setFontColor(lfontColor);
        setBorder_yn(lborder_yn);
        setBackgroundColor(lbackgroundColor);
        setVisible(lvisible);
        setReadOnly(lreadOnly);
        parentContainer = lparentContainer;
        popupFromFieldValue = lpopupFromFieldValue;
        table = ltable;
        column = lcolumn;
        //Ticket #192
        getTextField().setText(column);
        //Ticket #225
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        setWidth(lwidth);
        validationData = lvalidationData;
        setValidationDescriptionSql(lvalidationDescriptionSql);
        required = lrequired;
        setDefaultValue(ldefaultValue);
        setInputMask(linputMask);
        setDisplayMask(ldisplayMask);
        dataType = ldataType;
        isKeyCol = lisKeyCol;
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = lisGUIDCol;
        validationStoredColumnName = lvalidationStoredColumnName;
        validationId = lvalidationId;
        validationCriteria = lvalidationCriteria;
        validationOrder = lvalidationOrder;
        setTimeZoneOffset(ltimeZoneOffset);
        setChangeEvent(lchangeEvent);
        setScrollEvent(lscrollEvent);
        setInsertEvent(linsertEvent);
        setDeleteEvent(ldeleteEvent);
        setSaveEvent(lsaveEvent);
        translationLabelId = ltranslationLabelId;
        captionLabelId = lcaptionLabelId;
        return getTextField();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoTextFieldNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoTextFieldNodeData nodeData = (EchoTextFieldNodeData) data;
        tabOrder = nodeData.getTabOrder();
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBorder_yn(nodeData.getBorder_yn());
        setBackgroundColor(nodeData.getBackgroundColor());
        setVisible(nodeData.getVisible());
        setReadOnly(nodeData.getReadOnly());
        table = nodeData.getTable();
        parentContainer = nodeData.getParentContainer();
        popupFromFieldValue = nodeData.getPopupFromFieldValue();
        column = nodeData.getColumn();
        if ((column != null) && (!"".equals(column))) {
            if (getTextField() != null) {
                getTextField().setText(column);
                //Ticket #438
                getTextField().setName(getNodeType() + index);
                getTextField().setName(getNodeType());
            }
        }
        if (EchoUtil.isNullOrEmpty(getName())) {
            setName(getNodeType() + index);
        }
        validationData = nodeData.getValidationData();
        setValidationDescriptionSql(nodeData.getValidationDescriptionSql());
        required = nodeData.getRequired();
        setDefaultValue(nodeData.getDefaultValue());
        setInputMask(nodeData.getInputMask());
        setDisplayMask(nodeData.getDisplayMask());
        dataType = nodeData.getDataType();
        isKeyCol = nodeData.getIsKeyCol();
        if (isKeyCol) {
            designerPage.setPKey(column);
        }
        isGUIDCol = nodeData.getisGUIDCol();
        validationStoredColumnName = nodeData.getValidationStoredColumnName();
        validationId = nodeData.getValidationId();
        validationCriteria = nodeData.getValidationCriteria();
        validationOrder = nodeData.getValidationOrder();
        setTimeZoneOffset(nodeData.isTimeZoneOffset());
        setChangeEvent(nodeData.getChangeEvent());
        setScrollEvent(nodeData.getScrollEvent());
        setInsertEvent(nodeData.getInsertEvent());
        setDeleteEvent(nodeData.getDeleteEvent());
        setSaveEvent(nodeData.getSaveEvent());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoTextFieldNodeData nodeData = new EchoTextFieldNodeData(designerPage);
        nodeData.copy(this);
        nodeData.setTranslationLabelId(translationLabelId);
        nodeData.setCaptionLabelId(captionLabelId);
        return nodeData;
    }

    public EchoTextFieldNodeData(IEchoDesignerTopComponent designerPage) {
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
    public EchoTextFieldNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        this(designerPage, dropPanel, true);
    }

    public EchoTextFieldNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel, boolean createLabel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab
        //tabOrder = getUniqueTabOrder();
        //component = new EchoTextField(this, index, designerPage.getDropPanel(), createLabel);
        component = new EchoTextField(this, index, dropPanel, createLabel);
        setName(component.getName());
        getTextField().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public final EchoTextField getTextField() {
        return (EchoTextField) component;
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
            if (getTextField() != null) {
                getTextField().setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
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
            if (getTextField() != null) {
                getTextField().setForeground(this.fontColor);
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
            if (getTextField() != null) {
                getTextField().setBackground(this.backgroundColor);
            }
        }
        designerPage.setModified(true);

    }

    /**
     * 
     */
    private void resetSize() {
        EchoUtil.resetSizeNoAlign(getTextField(), getTextField().getText());
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
            if (getTextField() != null) {
                this.getTextField().setText(this.column);
            }
            // CDT-530
            if (changingColumnFixName || (name == null) || name.isEmpty() || name.contains(getNodeType())) {
                setName(this.column);
            }
        } else {
            this.column = "";
            fire("nodename", this.column, "");
            if (getTextField() != null) {
                this.getTextField().setText("");
            }
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
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
                } else {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
                }
            } else {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            }
            tabOrder = getTabOrder(tabOrder, visible);
            getComponent().getNode().restoreSheet();
            if (getTextField() != null) {
                if (this.getTextField().getCaptionLabelNodeData() != null) {
                    this.getTextField().getCaptionLabelNodeData().setVisible(visible);
                }
            }
            designerPage.setModified(true);
        }
    }

    public boolean isTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(boolean timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
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
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (readOnly) {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            } else {
                if (visible) {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
                } else {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
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
    public boolean getBorder_yn() {
        return border_yn;
    }

    /**
     *
     * @param readOnly
     */
    public void setBorder_yn(boolean border_yn) {
        this.border_yn = border_yn;
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
        if (getTextField() != null) {
            getTextField().setLocation(getTextField().getLocation().x, top);
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
        if (getTextField() != null) {
            getTextField().setLocation(left, getTextField().getLocation().y);
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
        if (getTextField() != null) {
            getTextField().setSize(getTextField().getWidth(), height);
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
        if (getTextField() != null) {
            getTextField().setSize(width, getTextField().getHeight());
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
    public String getDataTypeClass() {
        if (dataType.equals("String")) {
            return "String.class";
        } else if (dataType.equals("Date")) {
            return "Date.class";
        } else if (dataType.equals("int")) {
            return "Integer.class";
        } else if (dataType.equals("double")) {
            return "Double.class";
        } else if (dataType.equals("boolean")) {
            return "Boolean.class";
        } else {
            return "String.class";
        }
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
    public String getValidationData() {
        return validationData;
    }

    public void setValidation(String validationData, boolean createLabel) {
        String[] valDataArray = EchoUtil.parseValidationData(validationData);
        String valId = valDataArray[EchoUtil.ARRAY_VALIDATION_ID_POS];
        this.validationData = valDataArray[EchoUtil.ARRAY_VALIDATION_DATA_POS];
        if (valDataArray[EchoUtil.ARRAY_VALIDATION_TYPE_POS].equals("user")) {
            this.validationStoredColumnName = "Value";
        } else {
            this.validationStoredColumnName = valDataArray[EchoUtil.ARRAY_VALIDATION_STORED_COL_POS];
        }
        this.validationId = valId;
        this.validationCriteria = valDataArray[EchoUtil.ARRAY_VALIDATION_CRITERIA_POS];
        this.validationOrder = valDataArray[EchoUtil.ARRAY_VALIDATION_ORDER_POS];
        if (createLabel) {
            if (!valId.equals("")) {
                if (getTextField() != null) {
                    getTextField().addTranslationLabel();
                }
            } else {
                if (getTextField() != null) {
                    getTextField().removeTranslationLabel();
                }
            }
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @param validationData
     */
    public void setValidationData(String validationData) {
        // This gets set from the ValidationsPropertyEditor
        //Ticket #370
        //setValidation(validationData, true);
        setValidation(validationData, false);
    }

    public String getValidationDescriptionSql() {
        return validationDescriptionSql;
    }

    public void setValidationDescriptionSql(String validationDescriptionSql) {
        this.validationDescriptionSql = validationDescriptionSql == null ? "" : validationDescriptionSql;
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
                set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(true);
                set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(true);
            } else {
                set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(false);
                set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(false);
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
     * 
     * @return
     */
    public String getInputMask() {
        return inputMask;
    }

    /**
     * 
     * @param inputMask
     */
    public void setInputMask(String inputMask) {
        this.inputMask = inputMask;
        designerPage.setModified(true);

    }

    /**
     * 
     * @return
     */
    public String getDisplayMask() {
        return displayMask;
    }

    /**
     * 
     * @param displayMask
     */
    public void setDisplayMask(String displayMask) {
        this.displayMask = displayMask;
        designerPage.setModified(true);

    }

    public String getTranslationLabelId() {
        return translationLabelId;
    }

    public void setTranslationLabelId(String translationLabelId) {
        //Ticket #219
        //record translationLabelId
        //since it will be reset when component is removed from the panel
        if (translationLabelId != null) {
            if (!translationLabelId.equals(this.translationLabelId)) {
                this.lastTranslationLabelId = this.translationLabelId;
                this.translationLabelId = translationLabelId;
            }
        }
    }

    public String getCaptionLabelId() {
        return captionLabelId;
    }

    public void setCaptionLabelId(String captionLabelId) {
        //Ticket #219
        //record captionLabelId
        //since it will be reset when component is removed from the panel
        this.lastCaptionLabelId = this.captionLabelId;
        this.captionLabelId = captionLabelId;
    }

    //Ticket #219
    //Set last label id back
    public void setLastLabelIds() {
        this.captionLabelId = this.lastCaptionLabelId;
        this.translationLabelId = this.lastTranslationLabelId;
        linkCaptionTranslationLabels(designerPage);
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
    public String getTable() {
        return table;
    }

    @Override
    public void updateName(int index) {
        getTextField().setName(getNodeType() + index);
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

    @Override
    public final void setName(String name) {
        super.setName(name);
        if (getTextField() != null) {
            getTextField().setName(getName());
        }

    }

    @Override
    public String getNodeType() {
        return "DataField";
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
