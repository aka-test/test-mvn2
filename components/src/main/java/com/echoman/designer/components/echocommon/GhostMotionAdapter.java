/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.SwingUtilities;

/**
 * 
 * @author david.morin
 */
public class GhostMotionAdapter extends MouseMotionAdapter
{
    private GhostGlassPane glassPane;
    private Component mDraggable;
    private int resize_size;
    private GhostComponentAdapter ghostComponentAdapter;

    /**
     * 
     * @param glassPane
     * @param mDraggable
     * @param resize_size
     */
    public GhostMotionAdapter(GhostComponentAdapter ghostComponentAdapter,
            GhostGlassPane glassPane, Component mDraggable, int resize_size) {
            this.glassPane = glassPane;
            this.mDraggable = mDraggable;
            this.resize_size = resize_size;
            this.ghostComponentAdapter = ghostComponentAdapter;
    }

    /**
     * 
     * @param p
     */
    private void setCursorType(Point p) {
           if (mDraggable.getCursor().equals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))) 
               return;
            Point loc = mDraggable.getLocation();
            Dimension size = mDraggable.getSize();
            if ((p.y + resize_size < loc.y + size.height) && (p.x + resize_size < p.x + size.width)) {
                    mDraggable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
    }

    /**
     * 
     * @param me
     */
    @Override
    public void mouseMoved(MouseEvent me) {
            setCursorType(me.getPoint());
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e)
    {
       if (!ghostComponentAdapter.mouseIsPressed())
           return;

       double currX = ghostComponentAdapter.getCurrPoint().getX();
       double currY = ghostComponentAdapter.getCurrPoint().getY();

       // Make sure we don't do this until we actually move a bit.
        if ((e.getX()>(currX+3)) || (e.getX()<(currX-3))
                || (e.getY()>(currY+3)) || (e.getY()<(currY-3))) {
           if (mDraggable.getCursor().equals(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))) {
                ghostComponentAdapter.setMovedEnough(true);
                if (!glassPane.isVisible())
                    glassPane.setVisible(true);
                Component c = e.getComponent();
                Point p = (Point) e.getPoint().clone();
                SwingUtilities.convertPointToScreen(p, c);
                SwingUtilities.convertPointFromScreen(p, glassPane);
                glassPane.clearImages(c, p);
           }
        }
    }
}