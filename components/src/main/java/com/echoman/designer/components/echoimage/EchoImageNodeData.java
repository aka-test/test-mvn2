/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echoimage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;

public class EchoImageNodeData extends EchoBaseNodeData {

    private int top;
    private int left;
    private int height;
    private int width;
    private boolean visible = true;
    private String filename = "";
    private transient String fullfilename = "";
    private transient BufferedImage image;

    public EchoImageNodeData() {
    }

    private Object readResolve() {
        designerPage = JDesiWindowManager.getActiveDesignerPage();
        JPanel dropPanel = designerPage.getDropPanel(parentId);
        listeners = Collections.synchronizedList(new LinkedList());
        EchoImage obj = this.getEchoImage();
        if (obj == null) {
            obj = createImage(dropPanel);
        } else {
            obj.setDropPanel(dropPanel);
            obj.createPopupMenu();
            obj.addPropertyChangeListener(WeakListeners.propertyChange(this, obj));
            new Draggable(obj, dropPanel);
            new Resizeable(obj);
        }
        defaultNewlyAddedProperties();
        dropPanel.add(obj);
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
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
    
    private EchoImage createImage(JPanel dropPanel) {
        int ltop = top;
        int lleft = left;
        int lheight = height;
        int lwidth = width;
        boolean lvisible = visible;
        String lname = name;
        String lfilename = filename;
        //Ticket #208
        //No longer store image in file
        component = new EchoImage(this, index, dropPanel);
        getEchoImage().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
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
        filename = lfilename;
        fullfilename = "";
        if ((filename != null) && (!"".equals(filename))) {
            fullfilename = getDesignerPage().getFormPublisher().getCacheDir() +
                    filename;
            if (new File(fullfilename).exists())
                getEchoImage().loadImage(fullfilename);
        }
        return getEchoImage();
    }

    @Override
    public void copy(EchoBaseNodeData data) {
        copy(data, true);
    }

    public void copy(EchoBaseNodeData data, boolean copyId) {
        if (copyId) {
            super.copy(data);
        }
        EchoImageNodeData nodeData = (EchoImageNodeData) data;
        InputStream strm = null;
        if (nodeData.getImage() != null) {
            try {
                ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
                ImageIO.write(nodeData.getImage(), "png", imagebuffer);
                strm = new ByteArrayInputStream(imagebuffer.toByteArray());
            } catch (Exception e) {
            }
        }
        setTop(nodeData.getTop());
        setLeft(nodeData.getLeft());
        setHeight(nodeData.getHeight());
        setWidth(nodeData.getWidth());
        setVisible(nodeData.getVisible());
        filename = nodeData.getFilename();
        fullfilename = "";
        if ((filename != null) && (!"".equals(filename))) 
            fullfilename = getDesignerPage().getFormPublisher().getCacheDir() +
                    filename;
        if (strm != null) {
            try {
                image = ImageIO.read(strm);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public EchoBaseNodeData cloneData() {        
        EchoImageNodeData nodeData = new EchoImageNodeData(designerPage);
        nodeData.copy(this);
        return nodeData;
    }

    public EchoImageNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
    }

    public EchoImageNodeData(IEchoDesignerTopComponent designerPage, JPanel dropPanel) {
        super(designerPage);
        component = new EchoImage(this, index, dropPanel);
        setName(component.getName());
        getEchoImage().addPropertyChangeListener(WeakListeners.propertyChange(this, component));
    }

    public final EchoImage getEchoImage() {
        return (EchoImage) component;
    }

    @Override
    public void updateName(int index) {
        getEchoImage().setName(getNodeType() + index);
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"varchar"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    public String toString() {
        return getNodeType() + " - " + index;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        if (getEchoImage() != null) {
            if (new File(filename).exists()) {
                fullfilename = filename;
                getEchoImage().loadImage(filename);
                String s = filename;
                if (s.contains("\\")) {
                    s = s.substring(s.lastIndexOf("\\") + 1);
                }
                this.filename = s;
            } else
                JOptionPane.showMessageDialog(null, "[" + filename +
                        "] does not exist.");
            designerPage.setModified(true);
        }
    }

    public String getFullFilename() {
        return fullfilename;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
        if (getEchoImage() != null) {
            getEchoImage().setSize(getEchoImage().getWidth(), height);
        }
        designerPage.setModified(true);
    }

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public void setLeft(Integer left) {
        this.left = left;
        if (getEchoImage() != null) {
            getEchoImage().setLocation(left, getEchoImage().getLocation().y);
        }
        designerPage.setModified(true);
    }

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public void setTop(Integer top) {
        this.top = top;
        if (getEchoImage() != null) {
            getEchoImage().setLocation(getEchoImage().getLocation().x, top);
        }
        designerPage.setModified(true);
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        designerPage.setModified(true);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
        if (getEchoImage() != null) {
            getEchoImage().setSize(width, getEchoImage().getHeight());
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


    public void setLocationFromEdit(Integer x, Integer y) {
        undoableHappened("location", new Point(this.left, this.top), new Point(x, y));
        this.top = y;
        this.left = x;
        fire("refresh", 0, 0);
        designerPage.setModified(true);

    }

    public void setSizeFromEdit(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        fire("refresh", 0, 0);
        designerPage.setModified(true);

    }

    @Override
    public void remove() {
        super.remove();
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public final void setName(String name) {
        super.setName(name);
        getEchoImage().setName(getName());
    }

    @Override
    public String getNodeType() {
        return "Image";
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
