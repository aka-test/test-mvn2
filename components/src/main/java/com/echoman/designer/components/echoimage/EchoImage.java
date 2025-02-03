/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echoimage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import org.openide.util.Exceptions;

public class EchoImage extends JLabel implements MouseListener, ActionListener, IEchoComponent {

    private EchoImageNodeData nodeData;
    private transient EchoImageNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;

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

    public EchoImage(EchoImageNodeData nodeData, int index, JPanel dropPanel) {
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        addMouseListener((MouseListener)this);
        //Ticket #438
        //setName("Image" + index);
        setName("Image");
        setSize(96, 96);
        setOpaque(false);
        setBackground(null);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
    }

    public final void createPopupMenu() {
        popup = new JPopupMenu();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

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
    }

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

    @Override
    public void setDropPanel(JPanel dropPanel) {
        this.dropPanel = dropPanel;
    }

    public void loadImage(String filename) {
        if ("".equals(filename)) {
            nodeData.setImage(null);
        } else {
            try {
                nodeData.setImage(ImageIO.read(new File(filename)));
                this.repaint();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        //Ticket #186
        if (nodeData.getImage() != null) {
            g.drawImage(nodeData.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
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
    public void actionPerformed(ActionEvent e) {
        if (!node.handleAction(((JMenuItem) (e.getSource())).getText()))
            JOptionPane.showMessageDialog(null, "Not implemented.");
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoImageNode)node;
    }

    @Override
    public void remove() {
       node.setIsDestroying(true);
       dropPanel.remove(this);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
    }

}
