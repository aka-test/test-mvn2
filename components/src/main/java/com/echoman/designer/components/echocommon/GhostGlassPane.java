/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import org.openide.nodes.Node;

/**
 * 
 * @author david.morin
 */
public class GhostGlassPane extends JPanel
{
    private AlphaComposite composite;
    private Point moveStartPoint = new Point(0, 0);
    private Point startPoint = new Point(0, 0);
    private Point currPoint = new Point(0, 0);
    private ArrayList<DraggedImage> dragList = new ArrayList<DraggedImage>();

    /**
     * 
     */
    public GhostGlassPane()
    {
        setOpaque(false);
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
    }

    public Point getCurrPoint() {
        return currPoint;
    }

    public Point getMoveStartPoint() {
        return moveStartPoint;
    }

    public void setMoveStartPoint(Point moveStartPoint) {
        this.moveStartPoint = moveStartPoint;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public ArrayList<DraggedImage> getDragList() {
        return dragList;
    }

    public void clearImages(Component comp, Point p) {
        setPoint(p);

        for (int i=dragList.size()-1; i>=0; i--) {
            int useX = dragList.get(i).getLastPoint().x-((dragList.get(i).getDraggedWidth())+5);
            int useY = dragList.get(i).getLastPoint().y-((dragList.get(i).getDraggedHeight())+5);
            int useWidth = ((int) (p.x+(dragList.get(i).getComponent().getWidth()-dragList.get(i).getDraggedWidth())+5)-useX);
            int useHeight = ((int) (p.y+(dragList.get(i).getComponent().getHeight()-dragList.get(i).getDraggedHeight())+5)-useY);
            if (dragList.get(i).getLastPoint().x > p.x) {
                useX = p.x-(dragList.get(i).getDraggedWidth())-5;
                useWidth = ((int)(dragList.get(i).getLastPoint().x+(dragList.get(i).getComponent().getWidth()-dragList.get(i).getDraggedWidth())+5)-useX);
            }
            if (dragList.get(i).getLastPoint().y > p.y) {
                useY = p.y-(dragList.get(i).getDraggedHeight())-5;
                useHeight = ((int)(dragList.get(i).getLastPoint().y+(dragList.get(i).getComponent().getHeight()-dragList.get(i).getDraggedHeight())+5)-useY);
            }
            Rectangle visRect = new Rectangle(useX, useY, useWidth, useHeight);
            paintImmediately(visRect);

            /* DEBUG erase rectangle.
            Graphics2D g2 = (Graphics2D)dragList.get(i).getComponent().getParent().getGraphics();
            g2.setColor(Color.yellow);
            g2.drawRect(visRect.x, visRect.y, visRect.width, visRect.height);
            g2.dispose();
            */
        }
    }

    public void addImages(Component comp, Point p)
    {
        dragList.clear();
        Node[] selectedNodes = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        List comps = Arrays.asList(selectedNodes);
        SwingUtilities.convertPointToScreen(p, comp);
        SwingUtilities.convertPointFromScreen(p, this);

        for (int i=0; i<comps.size(); i++) {
            EchoBaseNode node = (EchoBaseNode)comps.get(i);
            Component c = (Component)node.getComponent();

            // Create a new point and convert if from the dropPanel
            // and then from the glasspane.
            Point np = new Point(c.getX(),c.getY());
            SwingUtilities.convertPointToScreen(np, c.getParent());
            SwingUtilities.convertPointFromScreen(np, this);
            
            Point cursoroffset = new Point(p.x-np.x,p.y-np.y);

            // If this is the component being dragged
            if (c.equals(comp)) {
                dragList.add(new DraggedImage(c, p, cursoroffset, true));
            } else {
                dragList.add(new DraggedImage(c, np, cursoroffset, false));
            }
        }
    }

    /**
     * 
     * @param location
     * @return
     */
    public void setPoint(Point location)
    {
        // Set points for all dragged images in here.
        for (int i=0; i<dragList.size(); i++) {
            dragList.get(i).setCurrPoint(location);
        }
    }

    /**
     *
     */
    public void setDragRectPoint(Point point, boolean start) {
        if (start)
            startPoint = point;
        currPoint = point;
    }

    /**
     * 
     * @param g
     */
    @Override
    public void paintComponent(Graphics g)
    {
        if ((dragList == null) || dragList.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;
            try {
               g2.setComposite(composite);
               g2.setColor(Color.red);
               g2.drawRect(startPoint.x, startPoint.y, currPoint.x-startPoint.x, currPoint.y-startPoint.y);
           } finally {
              g2.dispose();
           }
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        try {
           g2.setComposite(composite);
           g2.setColor(Color.red);
           g2.setBackground(Color.yellow);
           for (int i=0; i<dragList.size(); i++) {
               g2.drawImage(dragList.get(i).getDragged(),
                        (int) (dragList.get(i).getCurrPoint().x - dragList.get(i).getDraggedWidth()),
                        (int) (dragList.get(i).getCurrPoint().y - dragList.get(i).getDraggedHeight()),
                        null);
           }
       } finally {
          g2.dispose();
       }
    }
}