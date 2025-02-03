/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * 
 * @author david.morin
 */
public class GhostDropAdapter extends MouseAdapter {
    protected GhostGlassPane glassPane;
	protected String action;
	private List<GhostDropListener> listeners;

    /**
     * 
     * @param glassPane
     * @param action
     */
    public GhostDropAdapter(GhostGlassPane glassPane, String action) {
        this.glassPane = glassPane;
        this.action = action;
        this.listeners = new ArrayList<GhostDropListener>();
    }

    /**
     * 
     * @param listener
     */
    public void addGhostDropListener(GhostDropListener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    /**
     * 
     * @param listener
     */
    public void removeGhostDropListener(GhostDropListener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    /**
     * 
     * @param evt
     */
    protected void fireGhostDropEvent(GhostDropEvent evt) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
        	((GhostDropListener) it.next()).ghostDropped(evt);
        }
    }
}