/**
 *
 */
package com.echoman.designer.components.echobutton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.plaf.basic.BasicButtonUI;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;

/**
 *
 * @author Dave Athlon
 */
public class EchoButton extends JButton implements MouseListener, ActionListener, IEchoComponent {
    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoButtonNodeData nodeData;
    private transient EchoButtonNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;
    
    /**
     * 
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoButton(EchoButtonNodeData nodeData, int index, JPanel dropPanel) {
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        addMouseListener(this);
        //Ticket #438
        //setName("Button" + index);
        setName("Button");
        setText("Button");
        setSize(48,24);
        setFont(new Font("Open Sans", Font.PLAIN, 14));
        setForeground(new Color(1, 85, 149));
        //setBorder(new ButtonBorder());
        setUI(new BasicButtonUI());
        setBorderPainted(true);
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
        setUI(new BasicButtonUI());
        setBorderPainted(true);
        setBorder(nodeData.getOrigBorder());
        this.node = (EchoButtonNode)node;
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
     * @param node
     */
    public void setNodeData(EchoButtonNodeData node){
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
            if (nodeData.isTopLocked() ) {
                ly = nodeData.getTop();
            }
            if (nodeData.isLeftLocked() ) {
                lx = nodeData.getLeft();
            }
        }
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);
        
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
     * @param width
     * @param height
     */
    @Override
    public final void setSize(int width, int height) {
        int w = width;
        int h = height;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isHeightLocked() ) {
                h = nodeData.getHeight();
            }
            if (nodeData.isWidthLocked() ) {
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
