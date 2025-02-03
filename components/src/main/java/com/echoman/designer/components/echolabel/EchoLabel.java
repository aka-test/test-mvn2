/**
 *
 */
package com.echoman.designer.components.echolabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;

/**
 *
 * @author Dave Athlon
 */
public class EchoLabel extends JLabel implements MouseListener, ActionListener, IEchoComponent {

    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoLabelNodeData nodeData;
    private transient EchoLabelNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;

    /**
     * 
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoLabel(EchoLabelNodeData nodeData, int index, JPanel dropPanel) {
        super();
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        setOpaque(false);
        setFont(new Font("Open Sans Semibold", Font.BOLD, 14));
        setForeground(new Color(1, 85, 149));
        //Ticket #438
        //setName("Label" + index);
        setName("Label");
        setAlignment(nodeData.getAlignment());
        setText("Echo Label");
        //Ticket #324 - needs to be done in Vaadin
        //setVerticalAlignment(SwingConstants.TOP);
        //setSize(70, 20);
        setSize(72, 20);
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
    }

    public final void setAlignment(String align) {
        if (align == null) {
            setHorizontalAlignment(RIGHT);
        } else {
            if (align.equals("Left")) {
                setHorizontalAlignment(LEFT);
            } else {
                setHorizontalAlignment(RIGHT);
            }
        }
    }

    /**
     * 
     * @return
     */
    public EchoLabelNodeData getNodeData() {
        return nodeData;
    }

    /**
     * Must do this to adjust for labels that are larger than the text because
     * html does not include the leading blank space which moves it to the left.
     * @return
     */
    public Point getHtmlLocation() {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        Rectangle2D rect = fm.getStringBounds(this.getText(), this.getGraphics());
        Point htmlLoc = new Point((int) ((getLocation().x + getWidth()) - rect.getWidth()), (int) getLocation().getY());
        return htmlLoc;
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
        this.node = (EchoLabelNode) node;
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
        // Just clear both since the edit control is deleted anyway
        nodeData.setIsLinkedToCaption(false);
        nodeData.setIsLinkedToTranslation(false);
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
    public void setNodeData(EchoLabelNodeData node) {
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
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.red, 1));
        node.addSelectedNode(e.isControlDown());
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
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
