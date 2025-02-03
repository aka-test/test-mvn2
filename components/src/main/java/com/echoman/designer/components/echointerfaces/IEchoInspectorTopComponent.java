/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

import java.util.List;
import org.openide.explorer.ExplorerManager;

/**
 *
 * @author Dave Athlon
 */
public interface IEchoInspectorTopComponent {
    /**
     * 
     * @return 
     */
    public ExplorerManager getExplorerManager();
    /**
     * 
     * @param l 
     */
    public void refreshList(List<IEchoComponentNodeData> l);
}
