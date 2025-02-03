/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.SwingUtilities;


/**
 * 
 * @author david.morin
 */
public abstract class AbstractGhostDropManager implements GhostDropListener {
	protected Component component;

	/**
     * 
     */
    public AbstractGhostDropManager() {
		this(null);
	}
	
	/**
     * 
     * @param component
     */
    public AbstractGhostDropManager(Component component) {
		this.component = component;
	}

	/**
     * 
     * @param point
     * @return
     */
    protected Point getTranslatedPoint(Point point) {
        Point p = (Point) point.clone();
        SwingUtilities.convertPointFromScreen(p, component);
		return p;
	}

	/**
     * 
     * @param point
     * @return
     */
    protected boolean isInTarget(Point point) {
        //Ticket #235
        //Rectangle bounds = component.getBounds();
        Rectangle bounds = new Rectangle(0, 0, component.getWidth(), component.getHeight());
	return bounds.contains(point);
    }

	/**
     * 
     * @param e
     */
    @Override
    public void ghostDropped(GhostDropEvent e) {
	}
}