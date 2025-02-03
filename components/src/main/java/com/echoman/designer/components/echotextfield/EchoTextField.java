/**
 *
 */
package com.echoman.designer.components.echotextfield;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echolabel.EchoLabel;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import org.openide.nodes.Node;

/**
 *
 * @author Dave Athlon
 */
public final class EchoTextField extends JTextField implements MouseListener,
        ActionListener, ContainerListener, PropertyChangeListener, IEchoComponent {

    /**
     * Holds the node data instance.
     *
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoTextFieldNodeData nodeData;
    private transient EchoTextFieldNode node;
    private transient JPanel dropPanel;
    private transient IEchoComponent captionLabel;
    private transient IEchoComponent translationLabel;
    private transient EchoLabelNodeData captionLabelNodeData;
    private transient JPopupMenu popup;

    /**
     *
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoTextField(EchoTextFieldNodeData nodeData, int index, JPanel dropPanel) {
        this(nodeData, index, dropPanel, true);
    }

    /**
     *
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoTextField(EchoTextFieldNodeData nodeData, int index, JPanel dropPanel, boolean createLabel) {
        super();
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        setText(" Echo Data Field ");
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.lightGray));
        setFocusable(false);
        setFont(new Font("Open Sans", Font.PLAIN, 14));
        setForeground(new Color(102, 102, 102));
        setSize(110, 20);
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        dropPanel.addContainerListener(this);
        //createPopupMenu();
        if (createLabel) {
            addCaptionLabel();
        }
        createPopupMenu();
    }

    /**
     *
     * @return
     */
    public IEchoComponent getCaptionLabel() {
        return captionLabel;
    }

    /**
     *
     * @return
     */
    public EchoLabelNodeData getCaptionLabelNodeData() {
        return captionLabelNodeData;
    }

    /**
     *
     * @param captionLabel
     */
    public void setCaptionLabel(IEchoComponent captionLabel) {
        this.captionLabel = captionLabel;
        //Ticket #459
        if (captionLabel != null) {
            captionLabelNodeData = (EchoLabelNodeData) captionLabel.getNode().getNodeData();
            captionLabelNodeData.addPropertyChangeListener(this);
        }
        // Must recreate popup menu after forms are opened and caption label
        // is assigned.
        createPopupMenu();
    }

    /**
     *
     * @return
     */
    public IEchoComponent getTranslationLabel() {
        return translationLabel;
    }

    /**
     *
     * @param translationLabel
     */
    public void setTranslationLabel(IEchoComponent translationLabel) {
        this.translationLabel = translationLabel;
    }

    /**
     *
     */
    public void createPopupMenu() {
        popup = new JPopupMenu();

        //nodeData.createPopupMenu(popup, this);
        createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    public void createPopupMenu(JPopupMenu popup, ActionListener listener) {
        JMenuItem menuItem;
        //Ticket #187
        boolean enabled = ((EchoUtil.isRunningAsEchoAdmin()) || (!nodeData.hasLockedField()));
        boolean copyEnabled = ((EchoUtil.isRunningAsEchoAdmin()) || (!nodeData.hasLockedPosition()));

        if (!(captionLabel == null)) {
            menuItem = new JMenuItem("Align Label Top");
            menuItem.setEnabled(enabled);
            menuItem.addActionListener(listener);
            popup.add(menuItem);
            menuItem = new JMenuItem("Align Label Left");
            menuItem.setEnabled(enabled);
            menuItem.addActionListener(listener);
            popup.add(menuItem);
            popup.addSeparator();
        }

        menuItem = new JMenuItem("Delete");
        menuItem.setEnabled(enabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);
        popup.addSeparator();
        //Ticket #236
        menuItem = new JMenuItem("Cut");
        menuItem.setEnabled(enabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.setEnabled(copyEnabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);

        if (EchoUtil.isRunningAsEchoAdmin()) {
            popup.addSeparator();
            menuItem = new JMenuItem("Lock Properties");
            menuItem.addActionListener(listener);
            popup.add(menuItem);
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentAdded(ContainerEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void componentRemoved(ContainerEvent e) {
        // Detach deleted labels for captions and translations.
        if (e.getChild().equals(captionLabel)) {
            captionLabel = null;
            nodeData.setCaptionLabelId("");
            // Recreate the popup menu when the caption label is
            // deleted so the align menu option is removed.
            createPopupMenu();
            //captionLabelName = "";
        } else if (e.getChild().equals(translationLabel)) {
            translationLabel = null;
            nodeData.setTranslationLabelId("");
            //translationLabelName = "";
        } else if (e.getChild().equals(this)) {
            if (captionLabel != null) {
                captionLabel.clearLinkToEdit();
            }
            if (translationLabel != null) {
                translationLabel.clearLinkToEdit();
            }
        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (node != null) {
            addSelectedNode(e.isControlDown());
        }
    }

    public void addSelectedNode(boolean multiSelected) {
        node.addSelectedNode(multiSelected);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoTextFieldNode) node;
    }

    @Override
    public void remove() {
        node.setIsDestroying(true);
        dropPanel.remove(this);
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    /**
     *
     */
    class PopupListener extends MouseAdapter {

        /**
         *
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         *
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         *
         * @param e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    /**
     *
     * @param dropPanel
     */
    @Override
    public void setDropPanel(JPanel dropPanel) {
        this.dropPanel = dropPanel;
    }

    /**
     *
     */
    public void removeTextField() {
        node.setIsDestroying(true);
        dropPanel.remove(this);
    }

    /**
     *
     * @param node
     */
    public void setNodeData(EchoTextFieldNodeData node) {
        nodeData = node;
    }

    /**
     *
     * @param node
     */
    public void setNode(EchoTextFieldNode node) {
        this.node = node;
    }

    /**
     *
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public void setLocation(int x, int y) {
        int lx = x;
        int ly = y;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isTopLocked()) {
                ly = nodeData.getTop();
            }
            if (nodeData.isLeftLocked()) {
                lx = nodeData.getLeft();
            }
        }
        int xDiff = lx - getLocation().x;
        int yDiff = ly - getLocation().y;
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);
        // We have to check for linked captions and translations being moved
        // because they will have their location set by the drop but it
        // may have already been changed by the edit here and will then be off.
        Node[] selectedNodes = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        List comps = Arrays.asList(selectedNodes);
        if ((captionLabel != null) && (!(comps.contains(captionLabel.getNode())))) {
            int currentX = ((JLabel) captionLabel).getLocation().x;
            int currentY = ((JLabel) captionLabel).getLocation().y;
            ((JLabel) captionLabel).setLocation(currentX + xDiff, currentY + yDiff);
        }
        if ((translationLabel != null) && (!(comps.contains(translationLabel.getNode())))) {
            int currentX = ((JLabel) translationLabel).getLocation().x;
            int currentY = ((JLabel) translationLabel).getLocation().y;
            ((JLabel) translationLabel).setLocation(currentX + xDiff, currentY + yDiff);
        }

        checkContainers();
    }

    private void checkContainers() {
        if (!nodeData.getLoadingForm()) {
            DataContainerManager.checkContainerComponents(nodeData, nodeData.getDesignerPage().getCompList());
            if (nodeData.getTable().equals("")) {
                nodeData.setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
            }
        }
    }

    /**
     *
     */
    public void addTranslationLabel() {
        if (translationLabel == null) {
            EchoLabelNodeData labelNodeData = new EchoLabelNodeData(nodeData.getDesignerPage(), false, true, dropPanel);
            labelNodeData.setParentId(this.getNode().getNodeData().getParentId(), false);
            Point newLoc = new Point(getLocation().x + getWidth() + 12, getLocation().y);
            labelNodeData.getLabel().setLocation(newLoc);
            dropPanel.add(labelNodeData.getLabel());
            dropPanel.setComponentZOrder(labelNodeData.getLabel(), 0);
            Rectangle visRect = getVisibleRect();
            dropPanel.paintImmediately(visRect.x, visRect.y, visRect.width, visRect.height);
            ArrayList compList = nodeData.getDesignerPage().getCompList();
            compList.add(labelNodeData);
            nodeData.getDesignerPage().getInspector().refreshList(compList);
            translationLabel = labelNodeData.getLabel();
            ((JLabel) translationLabel).setText("Translation");
            labelNodeData.setCaption("Translation");
            //translationLabelName = translationLabel.getName();
            nodeData.setTranslationLabelId(labelNodeData.getId());
            // These must be added this way so the listener is stored in a transient
            // list, otherwise the label has a reference to this edit which will cause
            // it to be recreated during the ReadResolve.
            labelNodeData.addPropertyChangeListener(this);
        }
    }

    /**
     *
     */
    public void removeTranslationLabel() {
        if (!(translationLabel == null)) {
            ArrayList compList = nodeData.getDesignerPage().getCompList();
            compList.remove(((EchoLabel) translationLabel).getNodeData());
            nodeData.getDesignerPage().getInspector().refreshList(compList);
            nodeData.setTranslationLabelId("");
            dropPanel.repaint();
        }
    }

    /**
     *
     */
    public void addCaptionLabel() {
        if (captionLabel == null) {
            captionLabelNodeData = new EchoLabelNodeData(nodeData.getDesignerPage(), true, false, dropPanel);
            Point newLoc = new Point(getLocation().x, getLocation().y-20);
            dropPanel.add(captionLabelNodeData.getLabel());
            captionLabelNodeData.getLabel().setLocation(newLoc);
            dropPanel.setComponentZOrder(captionLabelNodeData.getLabel(), 0);
            Rectangle visRect = getVisibleRect();
            dropPanel.paintImmediately(visRect.x, visRect.y, visRect.width, visRect.height);
            ArrayList compList = nodeData.getDesignerPage().getCompList();
            compList.add(captionLabelNodeData);
            // Fix the DataField name because the caption label is being created
            // before the DataField gets added to the compList.
            nodeData.setIndex(compList.size());
            //Ticket #438
            //setName("DataField" + nodeData.getIndex());
            setName("DataField");
            // Don't need to do this here because the drop of the textfield will call it after
            // this so the caption label will be included.
            captionLabel = captionLabelNodeData.getLabel();
            ((JLabel) captionLabel).setText("Caption");
            captionLabelNodeData.setCaption(((JLabel) captionLabel).getText());
            ((JLabel) captionLabel).setHorizontalAlignment(LEFT);
            //captionLabelName = captionLabel.getName();
            nodeData.setCaptionLabelId(captionLabelNodeData.getId());
            captionLabelNodeData.addPropertyChangeListener(this);
        }
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
        int w = width;
        int h = height;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isHeightLocked()) {
                h = nodeData.getHeight();
            }
            if (nodeData.isWidthLocked()) {
                w = nodeData.getWidth();
            }
        }
        super.setSize(w, h);
        nodeData.setSizeFromEdit(w, h);
    }

    /**
     *
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    /**
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String menuItem = ((JMenuItem) (e.getSource())).getText();
        if (menuItem.contains("Align Label")) {
            if (menuItem.equals("Align Label Left")) {
                Point newLoc = new Point(getLocation().x - 73, getLocation().y);
                captionLabelNodeData.getLabel().setLocation(newLoc);
                ((JLabel) captionLabel).setHorizontalAlignment(RIGHT);
                captionLabelNodeData.setAlignment("Right");
            } else if (menuItem.equals("Align Label Top")) {
                Point newLoc = new Point(getLocation().x, getLocation().y - 20);
                captionLabelNodeData.getLabel().setLocation(newLoc);
                ((JLabel) captionLabel).setHorizontalAlignment(LEFT);
                captionLabelNodeData.setAlignment("Left");
            }
        } else if (!node.handleAction(((JMenuItem) (e.getSource())).getText())) {
            JOptionPane.showMessageDialog(null, "Not implemented.");
        }
    }
}
