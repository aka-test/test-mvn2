/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import com.echoman.designer.components.echoform.MiscPanel;
import org.openide.util.NbPreferences;

/**
 * 
 * @author david.morin
 */
public class GhostDropManager extends AbstractGhostDropManager {
    private GhostGlassPane glassPane;

    /**
     * 
     * @param target
     * @param grid
     */
    public GhostDropManager(Component target, GhostGlassPane glassPane) {
        super(target);
        this.glassPane = glassPane;
    }

    private void dropDraggedComponents(GhostDropEvent e, Rectangle[][] grid, String snap, Component comp, Point p, Point grid_adj) {
        Point pt = null;
        for (DraggedImage multi_comp : glassPane.getDragList()) {
           if (multi_comp.getComponent() != comp) {
              if ((grid!=null) && (snap.equals("on"))) {
                 pt = new Point(p.x-multi_comp.getDraggedWidth()-grid_adj.x,
                        p.y-multi_comp.getDraggedHeight()-grid_adj.y);
              // Else, just set the new location without the grid.
              } else {
                 pt = new Point(p.x-multi_comp.getDraggedWidth(),
                        p.y-multi_comp.getDraggedHeight());
              }
              multi_comp.getComponent().setLocation(pt);
           }
        }
    }

    /**
     * 
     * @param e
     */
    @Override
    public void ghostDropped(GhostDropEvent e) {
       boolean droppedIt = false;
       Point orig_pt = null;
       Point new_pt = null;
       Point p = e.getDropLocation();
       Rectangle[][] grid = JDesiWindowManager.getActiveDesignerPage().getGrid();
       p = getTranslatedPoint(p);
       String snap = "on";
       if ((e.getAction().equals("nosnap")) ||
          (!NbPreferences.forModule(MiscPanel.class).getBoolean("snapOn", true))) {
          snap = "off";
       }
       // Only drop if the drop point is within the dropPanel.
       if (isInTarget(p)) {
          // If we are snapping to grid then... 
          if ((grid!=null) && (snap.equals("on"))) {
             orig_pt = new Point(p.x-glassPane.getMoveStartPoint().x, p.y-glassPane.getMoveStartPoint().y);

             new_pt = EchoUtil.locateComponentInGrid(grid, orig_pt, e.getComponent().getWidth(), e.getComponent().getHeight());

          // Else, just set the new location without the grid.
          } else {
            orig_pt = new Point(p.x-glassPane.getMoveStartPoint().x, p.y-glassPane.getMoveStartPoint().y);
            new_pt = orig_pt;
          }
          // Get how much the dragged component was adjusted to snap to the grid.
          Point grid_adj = new Point(orig_pt.x-new_pt.x, orig_pt.y-new_pt.y);
          // The check for whether it is in target or not must be relative to the panel
          // location, not the left edge of the window.
          Point pt2 = new Point(new_pt.x+component.getX(),new_pt.y+component.getY());
          if (isInTarget(pt2)) {
              e.getComponent().setLocation(new_pt);
              droppedIt = true;
          }

          if (droppedIt)
              dropDraggedComponents(e, grid, snap, e.getComponent(), p, grid_adj);
       }
   }

}