/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Point;

/**
 * 
 * @author david.morin
 */
public class GhostDropEvent {
	private Point point;
	private String action;
   private Component comp;

	/**
     * 
     * @param action
     * @param point
     * @param comp
     */
    public GhostDropEvent(String action, Point point, Component comp) {
		this.action = action;
		this.point = point;
      this.comp = comp;
	}

	/**
     * 
     * @return
     */
    public String getAction() {
		return action;
	}

	/**
     * 
     * @return
     */
    public Point getDropLocation() {
		return point;
	}
	
    /**
     * 
     * @return
     */
    public Component getComponent() {
		return comp;
	}
}
