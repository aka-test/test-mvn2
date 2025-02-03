/**
 *
 */
package com.echoman.designer.components.echoborder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.GhostGlassPane;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author Dave Athlon
 */
public class EchoBorder extends JLabel implements MouseMotionListener, MouseListener,
        ActionListener, IEchoComponent {

    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoBorderNodeData nodeData;
    private transient EchoBorderNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;
    private transient GhostGlassPane glassPane;
    private transient List<IEchoComponentNodeData> compList;
    private boolean mouseDragging = false;

    /**
     *
     */
    public EchoBorder(EchoBorderNodeData nodeData, int index, JPanel dropPanel) {
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        this.glassPane = (GhostGlassPane) ((JFrame) WindowManager.getDefault().getMainWindow()).getGlassPane();
        this.compList = nodeData.getDesignerPage().getCompList();
        addMouseListener(this);
        addMouseMotionListener(this);
        //Ticket #438
        //setName("Box" + index);
        setName("Box");
        setSize(96, 48);
        setOpaque(false);
        setBackground(new Color(204, 221, 238));
        setOpaque(true);
        nodeData.setBorderColor(new Color(204, 221, 238));
        nodeData.setBorderLeftThickness(1);
        nodeData.setBorderRightThickness(1);
        nodeData.setBorderTopThickness(1);
        nodeData.setBorderBottomThickness(1);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(204, 221, 238)));
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
    }

    /**
     * 
     */
    public final void createPopupMenu() {
        popup = new JPopupMenu();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoBorderNode) node;
    }

    @Override
    public void remove() {
        node.setIsDestroying(true);
        dropPanel.remove(this);
        checkZOrder();
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
     * @param node
     */
    public void setNodeData(EchoBorderNodeData node) {
        nodeData = node;
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
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);
        checkZOrder();
    }

    /**
     * 
     * @param width
     * @param height
     */
    @Override
    public final void setSize(int width, int height) {
        int w = width;
        int h = height;
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
        checkZOrder();
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
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Ticket #78 setBorder(BorderFactory.createLineBorder(Color.red, 2));
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (e.isShiftDown()) {

            if (node != null) {
                glassPane.getDragList().clear();
                node.addSelectedNode(false);
                // Border does something strange when it has a background
                // and other components within it so we have to repaint.
                dropPanel.repaint();
            }

            glassPane.getDragList().clear();
            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, this);
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setDragRectPoint(p, true);
            glassPane.setVisible(true);

        } else {
            node.addSelectedNode(e.isControlDown());
            // Border does something strange when it has a background
            // and other components within it so we have to repaint.
            dropPanel.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDragging = true;
        if (e.isShiftDown()) {
            removeSelectedBorder();
            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, this);
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setDragRectPoint(p, false);
            glassPane.paintImmediately(glassPane.getBounds());
        }
    }

    private void removeSelectedBorder() {
        Node[] ary = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        if (ary != null) {
            ArrayList list = new ArrayList(Arrays.asList(ary));
            if (list.contains(node)) {
                list.remove(node);
            }
            Node[] a = (Node[]) list.toArray(new Node[0]);
            try {
                nodeData.getDesignerPage().getMgr().setSelectedNodes(a);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseDragging) {
            mouseDragging = false;
            checkZOrder();
        }
        if (e.isShiftDown()) {
            glassPane.setVisible(false);
            multiSelectComponents();
        }
    }

    private void checkZOrder() {
        if ((!mouseDragging) && (!nodeData.getLoadingForm())) {
            nodeData.fixZOrder();
            dropPanel.repaint();
        }
    }

    private void multiSelectComponents() {
        Point pointStart = new Point(glassPane.getStartPoint().x, glassPane.getStartPoint().y);
        SwingUtilities.convertPointToScreen(pointStart, glassPane);
        SwingUtilities.convertPointFromScreen(pointStart, dropPanel);
        Point pointEnd = new Point(glassPane.getCurrPoint().x, glassPane.getCurrPoint().y);
        SwingUtilities.convertPointToScreen(pointEnd, glassPane);
        SwingUtilities.convertPointFromScreen(pointEnd, dropPanel);
        Rectangle rect = new Rectangle(pointStart.x, pointStart.y, pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
        // Multi-selected components were not being checked for the parent
        // so if there were any tabs, the components on multiple tabs could be selected.
        for (IEchoComponentNodeData cnd : compList) {
            if ((cnd.getComponent() != this) && (!cnd.getClass().getName().contains("EchoFormNodeData"))
                    && (cnd.getParentId().equals(nodeData.getParentId())
                    && rect.contains(((JComponent) cnd.getComponent()).getLocation()))) {
                cnd.getComponent().getNode().addSelectedNode(true);
            }
        }
    }


    /**
     * 
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!node.handleAction(((JMenuItem) (e.getSource())).getText()))
            JOptionPane.showMessageDialog(null, "Not implemented.");
    }

}
