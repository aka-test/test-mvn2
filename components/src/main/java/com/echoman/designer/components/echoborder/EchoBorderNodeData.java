/**
 *
 */
package com.echoman.designer.components.echoborder;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.EchoDefaultStylePropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
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
public class EchoBorderNodeData extends EchoBaseNodeData {
    private int top;
    private int left;
    private int height;
    private int width;
    private int zOrder;
    private boolean visible = true;
    private Color borderColor = new Color(204, 221, 238);
    private Color borderBackgroundColor = new Color(204, 221, 238);
    private boolean borderBackgroundTransparent = false;
    private int borderLeftThickness;
    private int borderRightThickness;
    private int borderTopThickness;
    private int borderBottomThickness;
    private String echoDefaultStyle = "";
    private transient ImageIcon underlineIcon = null;

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
            EchoBorder obj = this.getBorder();
            if (obj == null) {
                obj = createBorder(dropPanel);
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
        // at the end returns itself
        return this;
    }

    public EchoBorderNodeData() {
    }    

    private EchoBorder createBorder(JPanel dropPanel) {
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        int lzOrder = zOrder;
        String lname = name;
        boolean lvisible = visible;
        Color lborderColor = borderColor;
        Color lborderBackgroundColor = borderBackgroundColor;
        boolean lborderBackgroundTransparent = borderBackgroundTransparent;
        int lborderLeftThickness = borderLeftThickness;
        int lborderRightThickness = borderRightThickness;
        int lborderTopThickness = borderTopThickness;
        int lborderBottomThickness = borderBottomThickness;
        // Any new properties added from now on must check for null because
        // the property may not have existed in previously created forms.
        String lechoDefaultStyle = (echoDefaultStyle == null) ? "" : echoDefaultStyle;
        component = new EchoBorder(this, index, dropPanel);
        getBorder().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        if ((!"".equals(lname)) && (lname != null)) 
            setName(lname);
        else 
            setName(getNodeType() + index);
        //Ticket #202
        setLeft(lleft);
        setTop(ltop);
        setWidth(lwidth);
        setHeight(lheight);
        setVisible(lvisible);
        setBorderBackgroundColor(lborderBackgroundColor);
        setBorderTopThickness(lborderTopThickness);
        setBorderLeftThickness(lborderLeftThickness);
        setBorderBottomThickness(lborderBottomThickness);
        setBorderRightThickness(lborderRightThickness);
        setBorderColor(lborderColor);
        setBorderBackgroundTransparent(lborderBackgroundTransparent);
        setEchoDefaultStyle(lechoDefaultStyle);
        setZOrder(lzOrder);
        return getBorder();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }
    /**
     *
     * @param EchoBorderNodeData
     *
     */
    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoBorderNodeData nodeData = (EchoBorderNodeData) data;
        setLeft(nodeData.getLeft());
        setTop(nodeData.getTop());
        setWidth(nodeData.getWidth());
        setHeight(nodeData.getHeight());
        setVisible(nodeData.getVisible());
        setBorderBackgroundColor(nodeData.getBorderBackgroundColor());
        setBorderTopThickness(nodeData.getBorderTopThickness());
        setBorderLeftThickness(nodeData.getBorderLeftThickness());
        setBorderBottomThickness(nodeData.getBorderBottomThickness());
        setBorderRightThickness(nodeData.getBorderRightThickness());
        setBorderColor(nodeData.getBorderColor());
        setBorderBackgroundTransparent(nodeData.getBorderBackgroundTransparent());
        setEchoDefaultStyle(nodeData.getEchoDefaultStyle());
        setZOrder(nodeData.getZOrder());
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoBorderNodeData nodeData = new EchoBorderNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoBorderNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
        zOrder = -1;
    }

    /**
     * 
     * @param glassPane
     * @param dropPanel
     */
    public EchoBorderNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        zOrder = -1;
        component = new EchoBorder(this, index, dropPanel);
        setName(component.getName());
        getBorder().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    @Override
    public void remove() {
        super.remove();
        fixZOrder();
    }
    
    public String getEchoDefaultStyle() {
        return echoDefaultStyle;
    }

    public void setEchoDefaultStyle(String echoDefaultStyle) {
        this.echoDefaultStyle = echoDefaultStyle;
        if (EchoUtil.isNullOrEmpty(echoDefaultStyle)) {
            return;
        }
        if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.DEFAULT_BOX)) {
            this.borderTopThickness = 1;
            this.borderLeftThickness = 1;
            this.borderBottomThickness = 1;
            this.borderRightThickness = 1;
            this.borderColor = new Color(204, 221, 238);
            this.borderBackgroundColor = new Color(204, 221, 238);
            this.borderBackgroundTransparent = false;
            this.underlineIcon = null;
            // Must do this to handle when a copy of the nodeData is being made
            if (!(getBorder() == null)) {
                getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
                getBorder().setOpaque(true);
                getBorder().setSize(getBorder().getWidth(), 48);
            }
        } else if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.SOLID_UNDERLINE)) {
            this.borderTopThickness = 0;
            this.borderLeftThickness = 0;
            this.borderBottomThickness = 1;
            this.borderRightThickness = 0;
            this.borderColor = new Color(34, 68, 136);
            this.borderBackgroundTransparent = true;
            this.underlineIcon = null;
            // Must do this to handle when a copy of the nodeData is being made
            if (!(getBorder() == null)) {
                getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
                getBorder().setOpaque(false);
                getBorder().setSize(getBorder().getWidth(), 24);
            }
        } else if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.DOTTED_UNDERLINE)) {
            this.borderTopThickness = 0;
            this.borderLeftThickness = 0;
            this.borderBottomThickness = 1;
            this.borderRightThickness = 0;
            this.borderColor = new Color(34, 68, 136);
            this.borderBackgroundTransparent = true;
            // Setting the Stroke only works with LineBorder, not MatteBorder, so it's not possible to have
            // a single line border that is not a complete box that uses different strokes.
            //this.borderStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{1.0f, 2.0f},0.0f);
            
            // So, we'll use a MatteBorder with an icon to show the different strokes, but this
            // will be for the underline styles only.
            this.underlineIcon = new ImageIcon(EchoBorderNodeData.class.getResource("dots.png"));

            // Must do this to handle when a copy of the nodeData is being made
            if (!(getBorder() == null)) {
                getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, underlineIcon));
                getBorder().setOpaque(false);
                getBorder().setSize(getBorder().getWidth(), 24);
                if (EchoUtil.isSelected(getDesignerPage(), component)) {
                   getBorder().setBorder(BorderFactory.createLineBorder(Color.red, 1));
                }
            }
        } else if (echoDefaultStyle.equals(EchoDefaultStylePropertyEditor.DASHED_UNDERLINE)) {
            this.borderTopThickness = 0;
            this.borderLeftThickness = 0;
            this.borderBottomThickness = 1;
            this.borderRightThickness = 0;
            this.borderColor = new Color(34, 68, 136);
            this.borderBackgroundTransparent = true;
            // We'll use a MatteBorder with an icon to show the different strokes, but this
            // will only be for the underline styles.
            this.underlineIcon = new ImageIcon(EchoBorderNodeData.class.getResource("dash.png"));
            // Must do this to handle when a copy of the nodeData is being made
            if (!(getBorder() == null)) {
                getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, underlineIcon));
                getBorder().setOpaque(false);
                getBorder().setSize(getBorder().getWidth(), 24);
                if (EchoUtil.isSelected(getDesignerPage(), component)) {
                   getBorder().setBorder(BorderFactory.createLineBorder(Color.red, 1));
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public ImageIcon getUnderlineIcon() {
        return underlineIcon;
    }

    /**
     * 
     * @return
     */
    public final EchoBorder getBorder() {
        return (EchoBorder)component;
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
    public Color getBorderColor() {
        return borderColor;
    }
    
    /**
     * 
     * @param borderColor
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = new Color(borderColor.getRGB());
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness,
                    borderLeftThickness, borderBottomThickness, borderRightThickness, this.borderColor));
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public Color getBorderBackgroundColor() {
        return borderBackgroundColor;
    }
    
    /**
     * 
     * @param borderBackgroundColor
     */
    public void setBorderBackgroundColor(Color borderBackgroundColor) {
        this.borderBackgroundColor = new Color(borderBackgroundColor.getRGB());
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
            getBorder().setBackground(this.borderBackgroundColor);
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public boolean getBorderBackgroundTransparent() {
        return borderBackgroundTransparent;
    }
    
    /**
     * 
     * @param borderBackgroundTransparent
     */
    public void setBorderBackgroundTransparent(boolean borderBackgroundTransparent) {
        this.borderBackgroundTransparent = borderBackgroundTransparent;
        if (getBorder() != null) {
            getBorder().setOpaque(!borderBackgroundTransparent);
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public int getBorderLeftThickness() {
        return borderLeftThickness;
    }
    
    /**
     * 
     * @param borderLeftThickness
     */
    public void setBorderLeftThickness(Integer borderLeftThickness) {
        this.borderLeftThickness = borderLeftThickness;
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public int getBorderRightThickness() {
        return borderRightThickness;
    }
    
    /**
     * 
     * @param borderRightThickness
     */
    public void setBorderRightThickness(Integer borderRightThickness) {
        this.borderRightThickness = borderRightThickness;
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public int getBorderTopThickness() {
        return borderTopThickness;
    }
    
    /**
     * 
     * @param borderTopThickness
     */
    public void setBorderTopThickness(Integer borderTopThickness) {
        this.borderTopThickness = borderTopThickness;
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
    public int getBorderBottomThickness() {
        return borderBottomThickness;
    }
    
    /**
     * 
     * @param borderBottomThickness
     */
    public void setBorderBottomThickness(Integer borderBottomThickness) {
        this.borderBottomThickness = borderBottomThickness;
        if (getBorder() != null) {
            getBorder().setBorder(BorderFactory.createMatteBorder(borderTopThickness, borderLeftThickness, borderBottomThickness, borderRightThickness, new Color(borderColor.getRGB())));
            JPanel dropPanel = designerPage.getDropPanel(parentId);
            dropPanel.repaint(getBorder().getBounds());
        }
        designerPage.setModified(true);
        echoDefaultStyle = "";
    }

    /**
     * 
     * @return
     */
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
        if (getBorder() != null)
            getBorder().setLocation(getBorder().getLocation().x, top);
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
        if (getBorder() != null)
            getBorder().setLocation(left, getBorder().getLocation().y);
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
        if (getBorder() != null)
            getBorder().setSize(getBorder().getWidth(), height);
        designerPage.setModified(true);
        echoDefaultStyle = "";
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
        if (getBorder() != null)
            getBorder().setSize(width, getBorder().getHeight());
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
        getBorder().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[] {"varchar"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }


    //Ticket #77
    @Override
    public void setBorder() {
        if ((!echoDefaultStyle.equals("")) && (underlineIcon != null)) {
            ((JComponent)component).setBorder(BorderFactory.createMatteBorder(borderTopThickness,
                borderLeftThickness, borderBottomThickness, borderRightThickness,
                underlineIcon));
        } else {
            ((JComponent)component).setBorder(BorderFactory.createMatteBorder(borderTopThickness,
                borderLeftThickness, borderBottomThickness, borderRightThickness,
                new Color(borderColor.getRGB())));
        }
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getBorder().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "Border";
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    @Override
    public void clearUncopiableProperties(String table) {
        //not implemented
    }

}
