/**
 *
 */
package com.echoman.designer.components.echobutton;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.border.Border;
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
import org.openide.nodes.Node.PropertySet;
import org.openide.util.WeakListeners;

import static com.echoman.designer.components.echobutton.EchoButtonNode.showAllLinkProperties;
import static com.echoman.designer.components.echobutton.EchoButtonNode.showFileLinkProperties;
import static com.echoman.designer.components.echobutton.EchoButtonNode.showFormLinkProperties;
import static com.echoman.designer.components.echobutton.EchoButtonNode.showUrlLinkProperties;
import static com.echoman.designer.components.echocommon.EchoUtil.isNullOrEmpty;

/**
 *
 * @author david.morin
 */
public class EchoButtonNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private String caption;
    private int tabOrder;
    private int top;
    private int left;
    private int height;
    private int width;
    private Font font = new Font("Open Sans", Font.PLAIN, 14);
    private Color fontColor = new Color(1, 85, 149);
    private Color btnBackgroundColor = new Color(224, 223, 227);
    private Color backgroundColor = new Color(224, 223, 227);
    private boolean inheritParentLock = false;
    private boolean visible = true;
    private String buttonType = "Button";
    private FormData popupFdNextForm = null;
    private String popupFormName = "";
    private String formLinkColumn = "";
    private String formLinkToColumn = "";
    private String procedureName = "";
    private List<String> procedureParamFields = null;
    private List<String> procedureResultFields = null;
    private String clickEvent = "";
    private transient Border origBorder = null;
    private String table = "";
    private String parentContainer = "";
    private String urlLink = "";
    private String fileLink = "";
    private transient File fileName;

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    @Override
    public String getFormLinkColumn() {
        return formLinkColumn;
    }

    @Override
    public String getFormLinkToColumn() {
        return formLinkToColumn;
    }

    public EchoButtonNodeData() {
    }

    public void setFormLinkToColumn(String formLinkToColumn) {
        List values = null;
        String columns;
        String tbl = "";
        if (!formLinkToColumn.equals("") && (formLinkToColumn.indexOf(";") != -1)) {
            values = new ArrayList();
            tbl = formLinkToColumn.substring(0, formLinkToColumn.indexOf(";"));
            columns = formLinkToColumn.substring(formLinkToColumn.indexOf(";") + 1, formLinkToColumn.length());
            if (!columns.equals("")) {
                StringTokenizer tokens = new StringTokenizer(columns);
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
        String columns;
        if (!formLinkColumn.equals("") && (formLinkColumn.indexOf(";") != -1)) {
            values = new ArrayList();
            columns = formLinkColumn.substring(formLinkColumn.indexOf(";") + 1, formLinkColumn.length());
            if (!columns.equals("")) {
                StringTokenizer tokens = new StringTokenizer(columns);
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

    /**
     *
     * @return
     */
    public String getButtonType() {
        return buttonType;
    }

    /**
     *
     * @param buttonType
     */
    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
        String useCaption = caption;
        if (getButton() == null) {
            return;
        }
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (caption == null) {
                useCaption = "Button";
            }
            // When changing types reset the default background color.
            if (buttonType.equals("URL Link")) {
                backgroundColor = new Color(getDesignerPage().getDropPanel(parentId).getBackground().getRGB());
                getButton().setBackground(getDesignerPage().getDropPanel(parentId).getBackground());
                getButton().setText("<html><u>" + useCaption + "</u></html>");
                showUrlLinkProperties(set);
            } else if (buttonType.equals("Form Link")) {
                backgroundColor = new Color(getDesignerPage().getDropPanel(parentId).getBackground().getRGB());
                getButton().setBackground(getDesignerPage().getDropPanel(parentId).getBackground());
                getButton().setText("<html><u>" + useCaption + "</u></html>");
                showFormLinkProperties(set);
            } else if (buttonType.equals("File Link")) {
                backgroundColor = new Color(getDesignerPage().getDropPanel(parentId).getBackground().getRGB());
                getButton().setBackground(getDesignerPage().getDropPanel(parentId).getBackground());
                getButton().setText("<html><u>" + useCaption + "</u></html>");
                showFileLinkProperties(set);
            } else {
                backgroundColor = new Color(btnBackgroundColor.getRGB());
                getButton().setBackground(new Color(btnBackgroundColor.getRGB()));
                getButton().setText(useCaption);
                showAllLinkProperties(set);
            }
            resetSize();
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
    }

    /**
     *
     * @return
     */
    public String getClickEvent() {
        return clickEvent;
    }

    /**
     *
     * @param clickEvent
     */
    public void setClickEvent(String clickEvent) {
        this.clickEvent = clickEvent;
        designerPage.setModified(true);

    }

    public FormData getPopupFdNextForm() {
        return popupFdNextForm;
    }

    public void setPopupFdNextForm(FormData popupFdNextForm) {
        if (popupFdNextForm != null) {
            resetPopupProperties();
        }
        this.popupFdNextForm = popupFdNextForm;
        designerPage.setModified(true);
    }

    /**
     *
     * @return
     */
    public String getPopupFormName() {
        return popupFormName;
    }

    /**
     * 
     * @param popupFormName
     */
    public void setPopupFormName(String popupFormName) {
        if (!isNullOrEmpty(popupFormName)) {
            resetPopupProperties();
        }
        this.popupFormName = popupFormName;
        designerPage.setModified(true);

    }

    public Border getOrigBorder() {
        return origBorder;
    }

    /**
     * This is called by serialization when constructing the object.
     * No constructor is called so you have to tap into it here.
     * @return
     */
    private Object readResolve() {
        designerPage = JDesiWindowManager.getActiveDesignerPage();
        JPanel dropPanel = designerPage.getDropPanel(parentId);
        listeners = Collections.synchronizedList(new LinkedList());
        EchoButton obj = this.getButton();
        if (obj == null) {
            obj = createButton(dropPanel);
        } else {
            origBorder = obj.getBorder();
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
        // Ticket 439
        dropPanel.setComponentZOrder(obj, 0);
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

    private EchoButton createButton(JPanel dropPanel) {
        String lcaption = caption;
        int ltabOrder = tabOrder;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        Font lfont = font;
        String lname = name;
        Color lfontColor = fontColor;
        Color lbtnBackgroundColor = btnBackgroundColor;
        Color lbackgroundColor = backgroundColor;
        boolean linheritParentLock = inheritParentLock;
        boolean lvisible = visible;
        String lbuttonType = buttonType;
        FormData lpopupFdNextForm = popupFdNextForm;
        String lpopupFormName = popupFormName;
        String lformLinkColumn = formLinkColumn;
        String lformLinkToColumn = formLinkToColumn;
        String lclickEvent = clickEvent;
        String ltable = table;
        String lparentContainer = parentContainer;
        String lurlLink = urlLink;
        String lfileLink = fileLink;
        component = new EchoButton(this, index, dropPanel);
        origBorder = ((EchoButton) component).getBorder();
        getButton().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        setCaption(lcaption);
        tabOrder = ltabOrder;
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setWidth(lwidth);
        setFont(lfont);
        setFontColor(lfontColor);
        btnBackgroundColor = lbtnBackgroundColor;
        if (lbackgroundColor == null) {
            if (lbtnBackgroundColor != null) {
                setBackgroundColor(lbtnBackgroundColor);
            }
        } else {
            setBackgroundColor(lbackgroundColor);
        }
        setInheritParentLock(linheritParentLock);
        setVisible(lvisible);
        setButtonType(lbuttonType);
        setPopupFdNextForm(lpopupFdNextForm);
        setPopupFormName(lpopupFormName);
        setFormLinkColumn(lformLinkColumn);
        formLinkColumn = lformLinkColumn;
        if (lformLinkToColumn == null) {
            lformLinkToColumn = "";
        }
        formLinkToColumn = lformLinkToColumn;
        setClickEvent(lclickEvent);
        setTable(lparentContainer, ltable);
        setUrlLink(lurlLink);
        setFileLink(lfileLink);
        return getButton();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }

    /**
     *
     * @param EchoButtonNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoButtonNodeData nodeData = (EchoButtonNodeData) data;
        setCaption(nodeData.getCaption());
        tabOrder = nodeData.getTabOrder();
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        btnBackgroundColor = nodeData.getBackgroundColor();
        if (nodeData.getBackgroundColor() != null) {
            setBackgroundColor(nodeData.getBackgroundColor());
        }
        setInheritParentLock(nodeData.getInheritParentLock());
        setVisible(nodeData.getVisible());
        setButtonType(nodeData.getButtonType());
        setPopupFdNextForm(nodeData.getPopupFdNextForm());
        setPopupFormName(nodeData.getPopupFormName());
        setFormLinkColumn(nodeData.getFormLinkColumn());
        formLinkColumn = nodeData.getFormLinkColumn();
        formLinkToColumn = nodeData.getFormLinkToColumn();
        setClickEvent(nodeData.getClickEvent());
        setTable(nodeData.getParentContainer(), nodeData.getTable());
        setUrlLink(nodeData.getUrlLink());
        setFileLink(nodeData.getFileLink());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoButtonNodeData nodeData = new EchoButtonNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoButtonNodeData(IEchoDesignerTopComponent designerPage) {
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
    public EchoButtonNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        //Ticket #464 Move to setParentId so we can set tab order per tab        
        //tabOrder = getUniqueTabOrder();
        component = new EchoButton(this, index, dropPanel);
        setName(component.getName());
        caption = ((EchoButton)component).getText();
        origBorder = ((EchoButton) component).getBorder();
        ((EchoButton)component).addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    /**
     * 
     * @return
     */
    public EchoButton getButton() {
        return (EchoButton) component;
    }

    public boolean getInheritParentLock() {
        return inheritParentLock;
    }

    public void setInheritParentLock(boolean inheritParentLock) {
        this.inheritParentLock = inheritParentLock;
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
            if (getButton() != null) {
                getButton().setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
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
            if (getButton() != null) {
                getButton().setForeground(this.fontColor);
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
            if (getButton() != null) {
                getButton().setBackground(this.backgroundColor);
            }
        }
        designerPage.setModified(true);
    }

    /**
     * 
     */
    private void resetSize() {
        String text = getButton().getText();
        if (text.contains("<html>")) //<html><u>text</u></html>
        {
            text = text.substring(text.indexOf("<u>") + 3, text.indexOf("</u>"));
        }
        EchoUtil.resetSize(getButton(), text);
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
        if (getButton() != null) {
            getButton().setLocation(getButton().getLocation().x, top);
        }
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
        if (getButton() != null) {
            if (buttonType.equals("Button")) {
                getButton().setText(caption);
            } else {
                getButton().setText("<html><u>" + caption + "</u></html>");
            }
            resetSize();
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
        if (getButton() != null) {
            getButton().setLocation(left, getButton().getLocation().y);
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
        if (getButton() != null) {
            getButton().setSize(getButton().getWidth(), height);
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
        if (getButton() != null) {
            getButton().setSize(width, getButton().getHeight());
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

    @Override
    public void updateName(int index) {
        getButton().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"char", "varchar", "longvarchar", "uniqueidentifier",
                    "int", "numeric", "numeric()", "smallint", "bigint"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    public void setTable(String parentContainer, String table) {
        this.parentContainer = parentContainer;
        this.table = table;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    @Override
    public String getParentContainer() {
        return parentContainer;
    }

    @Override
    public void setParentContainer(String parentContainer) {
        this.parentContainer = parentContainer;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
        designerPage.setModified(true);
    }

    public File getFileName() {
        return fileName;
    }

    public void setFileName(File file) {
        this.fileName = file;
        if (file == null) {
            setFileLink("");
        } else {
            if (file.isFile()) {
                setFileLink(file.getAbsolutePath());
            }
        }
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getButton().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "Button";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<String> getProcedureParamFields() {
        if (procedureParamFields == null) {
            procedureParamFields = new ArrayList<>();
        }
        return procedureParamFields;
    }

    public void addProcedureParamField(String fieldName) {
        if (procedureParamFields == null) {
            procedureParamFields = new ArrayList<>();
        }
        procedureParamFields.add(fieldName);
    }

    public List<String> getProcedureResultFields() {
        return procedureResultFields;
    }

    public void addProcedureResultField(String fieldName) {
        if (procedureResultFields == null) {
            procedureResultFields = new ArrayList<>();
        }
        procedureResultFields.add(fieldName);
    }

    @Override
    public void setTableFromDefault(String table) {
        this.parentContainer = "Default";
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
    public void clearUncopiableProperties(String table) {
        //not implemented
    }

    private void resetPopupProperties() {
        this.popupFormName = "";
        this.formLinkColumn = "";
        this.formLinkToColumn = "";
        this.popupFdNextForm = null;
    }

}
