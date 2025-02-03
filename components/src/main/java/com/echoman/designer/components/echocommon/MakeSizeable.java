/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.echoman.designer.components.echoform.MiscPanel;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;
 
/**
 * 
 * @author david.morin
 */
public abstract class MakeSizeable {
    
    public static final int RESIZE_MARGIN_SIZE = 4;
	
    /**
     * 
     */
    public static class Draggable {
        Component mDraggable;
        JPanel dropPanel;
        GhostGlassPane glassPane;
        GhostComponentAdapter componentAdapter;
        GhostMotionAdapter motionAdapter;
        GhostDropListener listener;
        
        /**
         * 
         * @param w
         * @param glassPane
         * @param dropPanel
         * @param grid
         */
        public Draggable(Component w, JPanel dropPanel) {
            mDraggable = w;
            this.glassPane = (GhostGlassPane)((JFrame)WindowManager.getDefault().getMainWindow()).getGlassPane();
            this.dropPanel = dropPanel;
            listener = new GhostDropManager(dropPanel, glassPane);
            w.addMouseListener(componentAdapter = new GhostComponentAdapter(glassPane, "", w));
            componentAdapter.addGhostDropListener(listener);
            w.addMouseMotionListener(motionAdapter = new GhostMotionAdapter(componentAdapter, glassPane, mDraggable, RESIZE_MARGIN_SIZE));
        }
    }
	
    /**
     * 
     */
    public static class Resizeable extends MouseAdapter implements MouseMotionListener {
    	int fix_pt_x = -1;
        int fix_pt_y = -1;
        Component mResizeable;
    	Cursor mOldcursor;
        Boolean mouseDown = false;
        private boolean resized = false;
        private Point origPoint;
		
        /**
         * 
         * @param c
         */
        public Resizeable(Component c) {
            mResizeable = c;
            c.addMouseListener(this);
            c.addMouseMotionListener(this);
    	}
 
        /**
         * 
         * @param me
         */
        @Override
        public void mouseEntered(MouseEvent me) {
                setCursorType(me.getPoint());
        }

        /**
         *
         * @param p
         */
        private void setCursorType(Point p) {
              if (!mouseDown) {
                    boolean n = p.y <= RESIZE_MARGIN_SIZE;
                    boolean s = p.y + RESIZE_MARGIN_SIZE >= mResizeable.getHeight();
                    boolean w = p.x <= RESIZE_MARGIN_SIZE;
                    boolean e = p.x + RESIZE_MARGIN_SIZE >= mResizeable.getWidth();
                    boolean out = ((p.y + RESIZE_MARGIN_SIZE < mResizeable.getHeight()) &&
                                  (p.x + RESIZE_MARGIN_SIZE < mResizeable.getWidth()) &&
                                  //allow for JPanel to be used
                                  //if mResizeable instanceof JPanel is used
                                  //no other JPanel would be allowed to move
                                  //(mResizeable instanceof JPanel));
                                  (mResizeable instanceof GhostGlassPane));
                    if (out) {
                       mResizeable.setCursor(Cursor.getDefaultCursor());
                       return;
                    }
                    if (e) {
                        if (s) {
                            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                            return;
                        } else if (n) {
                            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                            return;
                        }
                        mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    } else if (w) {
                        if (s) {
                            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                            return;
                        } else if (n) {
                            mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                            return;
                        }
                        mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    } else if (s) {
                        mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    } else if (n) {
                        mResizeable.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    }
              }
           }

        /**
         * 
         * @param me
         */
        @Override
        public void mouseExited(MouseEvent me) {
            if (mouseDown)
               me.getComponent().getParent().setCursor(mResizeable.getCursor());
            if (mOldcursor != null)
                ((Component)me.getSource()).setCursor(mOldcursor);
            mOldcursor = null;
        }

       /**
        * 
        * @param me
        */
        @Override
        public void mousePressed(MouseEvent me) {
            resized = false;
            mouseDown = true;
            origPoint = new Point(mResizeable.getWidth(), mResizeable.getHeight());
            Cursor c = mResizeable.getCursor();
            Point loc = mResizeable.getLocation();
            if ((c.equals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)))
                    || ((c.equals(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))))
                    || ((c.equals(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))))
                    || ((c.equals(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))))) {
                fix_pt_x = loc.x;
                fix_pt_y = loc.y;
            } else if ((c.equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)))
                    || ((c.equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))))) {
                fix_pt_x = loc.x;
                fix_pt_y = -1;
            } else if ((c.equals(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)))
                    || ((c.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))))) {
                fix_pt_x = -1;
                fix_pt_y = loc.y;
            }
        }

        /**
         * 
         * @param me
         */
        @Override
        public void mouseReleased(MouseEvent me) {
            String snap = "on";

            if ((me.isAltDown()) ||
               (!NbPreferences.forModule(MiscPanel.class).getBoolean("snapOn", true))) {
               snap = "off";
            }
            mouseDown = false;
            me.getComponent().getParent().setCursor(Cursor.getDefaultCursor());
            if (resized) {
               IEchoComponent comp = (IEchoComponent) mResizeable;
                Point compLoc = me.getComponent().getLocation();
                Rectangle compBounds = me.getComponent().getBounds();
                if (snap.equals("on")) {
                    compBounds = EchoUtil.locateSizeInGrid(mResizeable.getCursor(), JDesiWindowManager.getActiveDesignerPage().getGrid(),
                              compLoc, me.getComponent().getWidth(), me.getComponent().getHeight());
                }
                //mResizeable.setBounds(compBounds);
                mResizeable.setLocation(compBounds.x, compBounds.y);
                mResizeable.setSize(compBounds.width, compBounds.height);
                // Hendra - we need to handle when the location changes now as well
                // since they can size from all sides.  Undo/Redo must be revisited
                // anyway as it sometimes has strange behavior - we can look at this then.
                comp.getNode().doPropertyValueChange(null, "size", origPoint,
                        new Point(mResizeable.getWidth(), mResizeable.getHeight()));
            }
            fix_pt_x = -1;
            fix_pt_y = -1;
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
         * @param me
         */
        @Override
        public void mouseDragged(MouseEvent me) {
            Cursor c = mResizeable.getCursor();
            if (((c.equals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))) ||
                 (c.equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))) ||
                 (c.equals(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))))) {
                   Point p = me.getPoint();
                   int width = fix_pt_x == -1 ? mResizeable.getWidth() : p.x;
                   int height = fix_pt_y == -1 ? mResizeable.getHeight() : p.y;
                   mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                   resized = true;
            } else if (((c.equals(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))) ||
                 (c.equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))) ||
                 (c.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))))) {
                   Point p = me.getPoint();
                   int newX = fix_pt_x == -1 ? mResizeable.getX() : mResizeable.getX() + p.x;
                   int newY = fix_pt_y == -1 ? mResizeable.getY() : mResizeable.getY() + p.y;
                   mResizeable.setLocation(newX, newY);
                   int width = fix_pt_x == -1 ? mResizeable.getWidth() : mResizeable.getWidth() - p.x;
                   int height = fix_pt_y == -1 ? mResizeable.getHeight() :  mResizeable.getHeight() - p.y;
                   mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                   resized = true;
            } else if ((c.equals(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)))) {
                   Point p = me.getPoint();
                   int newX = fix_pt_x == -1 ? mResizeable.getX() : mResizeable.getX()+ p.x;
                   int newY = fix_pt_y == -1 ? mResizeable.getY() : mResizeable.getY();
                   mResizeable.setLocation(newX, newY);
                   int width = fix_pt_x == -1 ? mResizeable.getWidth() : mResizeable.getWidth() - p.x;
                   int height = fix_pt_y == -1 ? mResizeable.getHeight() : p.y;
                   mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                   resized = true;
            } else if ((c.equals(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)))) {
                   Point p = me.getPoint();
                   int newX = fix_pt_x == -1 ? mResizeable.getX() : mResizeable.getX();
                   int newY = fix_pt_y == -1 ? mResizeable.getY() : mResizeable.getY() + p.y;
                   mResizeable.setLocation(newX, newY);
                   int width = fix_pt_x == -1 ? mResizeable.getWidth() : p.x;
                   int height = fix_pt_y == -1 ? mResizeable.getHeight() :  mResizeable.getHeight() - p.y;
                   mResizeable.setSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
                   resized = true;
            }
        }
    }
}

