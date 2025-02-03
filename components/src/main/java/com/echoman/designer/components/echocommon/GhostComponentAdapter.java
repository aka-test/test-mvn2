/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;

/**
 * 
 * @author david.morin
 */
public class GhostComponentAdapter extends GhostDropAdapter {
    protected Component comp;
    private boolean mouseIsPressed = false;
    Point currPoint = null;
    boolean movedEnough = false;
    
    public Point getCurrPoint() {
        return currPoint;
    }

    public void setMovedEnough(boolean movedEnough) {
        this.movedEnough = movedEnough;
    }

    public void setCurrPoint(Point currPoint) {
        this.currPoint = currPoint;
    }

    /**
     * 
     * @param glassPane
     * @param action
     * @param comp
     */
    public GhostComponentAdapter(GhostGlassPane glassPane, String action, Component comp) {
        super(glassPane, action);
        this.comp = comp;
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        if ((e.getButton() != MouseEvent.BUTTON1) || (e.isShiftDown()))
            return;
        if (e.isAltDown()) {
           action = "nosnap";
        } else {
            action = "";
        }
        if (comp.getCursor().equals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))) {
            // Get the current point when the mouse was pressed.
            if (currPoint == null) {
                currPoint = e.getPoint();
                glassPane.setMoveStartPoint(new Point(e.getX(),e.getY()));
            }
            mouseIsPressed = true;
            Component c = e.getComponent();
            //Ticket #209
            if ((c.getClass().getName().contains("EchoDataContainer"))
                    || (c.getClass().getName().contains("EchoForm"))) {
                ((IEchoComponentNodeData)((IEchoComponentNode)((IEchoComponent)c).getNode()).getNodeData()).setBorder();
            } else {
                ((JComponent)c).setBorder(BorderFactory.createLineBorder(Color.red, 1));
            }
            glassPane.setCursor(comp.getCursor());
            glassPane.addImages(c, (Point)e.getPoint().clone());
       }
    }

    public boolean mouseIsPressed() {
        return this.mouseIsPressed;
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e)
    {
        try {
            if (comp.getCursor().equals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))) {
              if (mouseIsPressed) {
                   mouseIsPressed = false;
               } else {
                   return;
               }
               // Don't do this just by clicking, it must have been moved.
               if (movedEnough) {
                    Component c = e.getComponent();
                    Point p = (Point) e.getPoint().clone();
                    SwingUtilities.convertPointToScreen(p, c);
                    Point eventPoint = (Point) p.clone();
                    glassPane.setVisible(false);
                    fireGhostDropEvent(new GhostDropEvent(action, eventPoint, comp));
              }
           }
        } finally {
            currPoint = null;
            glassPane.setMoveStartPoint(new Point(0,0));
            movedEnough = false;
        }
    }
}