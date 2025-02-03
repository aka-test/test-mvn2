/**
 *
 */
package com.echoman.designer.components.echomemofield;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
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
public final class EchoMemoField extends JTextArea implements MouseListener, ActionListener, IEchoComponent{
    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoMemoFieldNodeData nodeData;
    private transient EchoMemoFieldNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;
    
    /**
     * 
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoMemoField(EchoMemoFieldNodeData nodeData, int index, JPanel dropPanel) {
        super();
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        setText(" Echo Memo Field ");
        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.lightGray));
        setFocusable(false);
        setFont(new Font("Open Sans", Font.PLAIN, 14));
        setForeground(new Color(102, 102, 102));
        setSize(312,96);
        //Ticket #438
        //setName("MemoField" + index);
        setName("MemoField");
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
    }
    
    /**
     * 
     */
    public void createPopupMenu() {
        popup = new JPopupMenu();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.red, 1));
        node.addSelectedNode(e.isControlDown());
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
        this.node = (EchoMemoFieldNode)node;
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
    public void setNodeData(EchoMemoFieldNodeData node) {
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
            DataContainerManager.checkContainerComponents(nodeData,nodeData.getDesignerPage().getCompList());
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
    public void setSize(int width, int height) {
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
    public void actionPerformed(ActionEvent e) {
        if (!node.handleAction(((JMenuItem) (e.getSource())).getText()))
            JOptionPane.showMessageDialog(null, "Not implemented.");
    }
}
