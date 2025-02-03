/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

/**
 *
 * @author david.morin
 */
public interface ITomcatRestartCallback {
    /**
     * 
     * @param success 
     */
    public void tomcatRestartCompleted(boolean success);
}

