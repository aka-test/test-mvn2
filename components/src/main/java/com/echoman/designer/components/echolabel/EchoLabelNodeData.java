package com.echoman.designer.components.echolabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.EchoDefaultStylePropertyEditor;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import org.openide.util.WeakListeners;

/**
 *
 * @author david.morin
 */
public class EchoLabelNodeData extends EchoBaseNodeData {
    private boolean isLinkedToCaption = false;
    private boolean isLinkedToTranslation = false;
    private String caption;
    private String alignment = "Left";
    private int top;
    private int left;
    private int height;
    private int width;
    private Font font = new Font("Open Sans Semibold", Font.BOLD, 14);
    private Color fontColor = new Color(1, 85, 149);
    private Color backgroundColor = new Color(Color.WHITE.getRGB());
    private boolean backgroundTransparent = true;
    private boolean visible = true;
    private String echoDefaultStyle = "";
    //Ticket #340
    private boolean autoSize = true;

    /**
     * This is called by serialization when constructing the object.
     * No constructor is called so you have to tap into it here.
     * @return
     */
    private Object readResolve() {
        designerPage = JDesiWindowManager.getActiveDesignerPage();
        JPanel dropPanel = designerPage.getDropPanel(parentId);
        listeners = Collections.synchronizedList(new LinkedList());
        EchoLabel obj = this.getLabel();
        if (obj == null) {
            obj = createLabel(dropPanel);
        } else {
            obj.setDropPanel(dropPanel);
            obj.createPopupMenu();
            obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
            new Draggable(obj, dropPanel);
            new Resizeable(obj);
        }
        defaultNewlyAddedProperties();
        dropPanel.add(obj);
        if ((this.alignment == null) || ("".equals(this.alignment))) {
            this.alignment = "Left";
        }
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
    }
    
    private EchoLabel createLabel(JPanel dropPanel) {
        boolean lisLinkedToCaption = isLinkedToCaption;
        boolean lisLinkedToTranslation = isLinkedToTranslation;
        String lcaption = caption;
        if (lcaption == null)
            lcaption = "";
        boolean lautoSize = autoSize;
        String lalignment = alignment;
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        Font lfont = font;
        String lname = name;
        Color lfontColor = fontColor;
        Color lbackgroundColor = backgroundColor;
        boolean lbackgroundTransparent = backgroundTransparent;
        // Any new properties added from now on must check for null because
        // the property may not have existed in previously created forms.
        String lechoDefaultStyle = (echoDefaultStyle == null) ? "" : echoDefaultStyle;
        boolean lvisible = visible;
        component = new EchoLabel(this, index, dropPanel);
        getLabel().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        setIsLinkedToCaption(lisLinkedToCaption);
        setIsLinkedToTranslation(lisLinkedToTranslation);
        if ((!"".equals(lname)) && (lname != null)) {
            setName(lname);
        } else {
            setName(getNodeType() + index);
        }
        setAlignment(lalignment);
        setAutoSize(lautoSize);
        setCaption(lcaption);
        setTop(ltop);
        setLeft(lleft);
        setHeight(lheight);
        setWidth(lwidth);        
        setFont(lfont);
        setFontColor(lfontColor);
        setBackgroundColor(lbackgroundColor);
        setBackgroundTransparent(lbackgroundTransparent);
        this.echoDefaultStyle = lechoDefaultStyle;
        setVisible(lvisible);
        return getLabel();
    }


    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoLabelNodeData
     * 
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoLabelNodeData nodeData = (EchoLabelNodeData) data;
        setIsLinkedToCaption(nodeData.getIsLinkedToCaption());
        setIsLinkedToTranslation(nodeData.getIsLinkedToTranslation());
        setCaption(nodeData.getCaption());
        setAlignment(nodeData.getAlignment());
        setAutoSize(nodeData.isAutoSize());
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setFont(nodeData.getFont());
        setFontColor(nodeData.getFontColor());
        setBackgroundColor(nodeData.getBackgroundColor());
        setBackgroundTransparent(nodeData.getBackgroundTransparent());
        this.echoDefaultStyle = nodeData.getEchoDefaultStyle();
        setVisible(nodeData.getVisible());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoLabelNodeData nodeData = new EchoLabelNodeData(designerPage, 
                isLinkedToCaption, isLinkedToTranslation);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoLabelNodeData(IEchoDesignerTopComponent designerPage, boolean isLinkedToCaption,
            boolean isLinkedToTranslation) {
        super(designerPage);
        this.isLinkedToCaption = isLinkedToCaption;
        this.isLinkedToTranslation = isLinkedToTranslation;
        if (isLinkedToTranslation) {
            this.alignment = "Left";
        }
    }
    /**
     * 
     * @param glassPane
     * @param dropPanel
     * @param isLinkedToCaption
     * @param isLinkedToTranslation
     */
    public EchoLabelNodeData(IEchoDesignerTopComponent designerPage, boolean isLinkedToCaption, 
            boolean isLinkedToTranslation, JPanel dropPanel) {
        super(designerPage);
        this.isLinkedToCaption = isLinkedToCaption;
        this.isLinkedToTranslation = isLinkedToTranslation;
        if (isLinkedToTranslation) {
            this.alignment = "Left";
        }
        component = new EchoLabel(this, index, dropPanel);
        setName(component.getName());
        caption = getLabel().getText();
        getLabel().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
        if (getLabel() != null)
            getLabel().setAlignment(alignment);
    }

    public String getEchoDefaultStyle() {
        return echoDefaultStyle;
    }

    public void setEchoDefaultStyle(String echoDefaultStyle) {
        this.echoDefaultStyle = echoDefaultStyle;
        if (EchoUtil.isNullOrEmpty(echoDefaultStyle)) {
            return;
        }
        if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.DEFAULT_CAPTION)) {
            this.font = new Font("Open Sans Semibold", Font.BOLD, 14);
            this.fontColor = new Color(1, 85, 149);
        } else if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.DEFAULT_HEADER)) {
            this.font = new Font("Open Sans", Font.BOLD, 16);
            this.fontColor = new Color(1, 85, 149);
        } else if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.LARGE_HEADER)) {
            this.font = new Font("Open Sans", Font.BOLD, 18);
            this.fontColor = new Color(1, 85, 149);
        }
        // Must do this to handle when a copy of the nodeData is being made
        if (!(getLabel() == null)) {
            getLabel().setFont(font);
            getLabel().setForeground(fontColor);
        }
    }

    public EchoLabelNodeData() {
    }

    /**
     * 
     * @return
     */
    public boolean getIsLinkedToCaption() {
        return isLinkedToCaption;
    }

    /**
     * 
     * @param isLinkedToCaption
     */
    public void setIsLinkedToCaption(boolean isLinkedToCaption) {
        this.isLinkedToCaption = isLinkedToCaption;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getIsLinkedToTranslation() {
        return isLinkedToTranslation;
    }

    /**
     * 
     * @param isLinkedToTranslation
     */
    public void setIsLinkedToTranslation(boolean isLinkedToTranslation) {
        this.isLinkedToTranslation = isLinkedToTranslation;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public final EchoLabel getLabel() {
        return (EchoLabel)component;
    }

    /**
     *
     * @return
     */
    public String getDataType() {
        return "String";
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
            if (getLabel() != null) {
                getLabel().setFont(new Font(font.getFontName(), font.getStyle(), font.getSize()));
                resetSize();
            }
        }
        this.echoDefaultStyle = "";
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
            if (getLabel() != null)
                getLabel().setBackground(this.backgroundColor);
        }
        this.echoDefaultStyle = "";
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
        if (component != null) {
            getLabel().setOpaque(!backgroundTransparent);
        }
        this.echoDefaultStyle = "";
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
        //Ticket #220 - convert this to RGB value so it can be convert
        //by xstream without the class definition
        //this.fontColor = fontColor;
        if (fontColor != null) {
            this.fontColor = new Color(fontColor.getRGB());
            if (getLabel() != null)
                getLabel().setForeground(this.fontColor);
        }
        this.echoDefaultStyle = "";
        designerPage.setModified(true);
    }
    
    /**
     * 
     */
    private void resetSize() {
        EchoUtil.resetSize(getLabel(), getLabel().getText());
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
        if (getLabel() != null) {
            String text = "";
            String[] lines = caption.split("\\n");
            if (lines.length > 1) {
                for (String s : lines) {
                    text = text + s + "<br>";
                }
                if (!"".equals(text)) {
                    text = "<HTML>" + text + "<HTML>";
                }
            } else {
                text = caption;
            }
            getLabel().setText(text);
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
        if (getLabel() != null)
            getLabel().setLocation(getLabel().getLocation().x, top);
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
        if (getLabel() != null)
            getLabel().setLocation(left, getLabel().getLocation().y);
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
        if (getLabel() != null)
            getLabel().setSize(getLabel().getWidth(), height);
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
        if (getLabel() != null)
            getLabel().setSize(width, getLabel().getHeight());
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
     * @return
     */
//    @Override
//    public String getColumn() {
//        return getLabel().getName();
//    }

    @Override
    public void updateName(int index) {
        getLabel().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[] {"varchar", "uniqueidentifier"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getLabel().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "Label";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    @Override
    public void clearUncopiableProperties(String table) {
        //not implemented
    }

}
