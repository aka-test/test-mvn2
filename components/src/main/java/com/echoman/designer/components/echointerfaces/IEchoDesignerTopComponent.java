/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JPanel;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;

/**
 *
 * @author Dave Athlon
 */
public interface IEchoDesignerTopComponent {

    public interface ComponentChangeListener {
        void componentChange(String componentName, String propertyName);
    }

    void createComponentChangeListener();

    void doComponentChange(String componentName, String propertyName);

    boolean isComponentChanged();

    /**
     * 
     * @return 
     */
    public IEchoInspectorTopComponent getInspector();
    /**
     * 
     * @return 
     */
    public ExplorerManager getMgr();
    /**
     * 
     * @return 
     */
    public ArrayList<IEchoComponentNodeData> getCompList();
    /**
     * 
     * @return 
     */
    public IFormPublisher getFormPublisher();
    /**
     * 
     * @return 
     */
    public String getPKey();
    /**
     * 
     * @param pKey 
     */
    public void setPKey(String pKey);
    /**
     * 
     * @return 
     */
    public String getTable();
    /**
     * 
     * @param table 
     */
    public void setTable(String table);
    /**
     * 
     * @param id
     * @return 
     */
    public JPanel getDropPanel(String id);
    /**
     * 
     * @return 
     */
    public Collection<JPanel> getDropPanels();
    /**
     * 
     * @param id
     * @param dropPanel 
     */
    public void setDropPanel(String id, JPanel dropPanel);
    /**
     * 
     * @return 
     */
    public int getSaveType();
    /**
     * 
     * @param savetype 
     */
    public void setSaveType(int savetype);
    /**
     * 
     * @return 
     */
    public String getSaveName();
    /**
     * 
     * @param saveName 
     */
    public void setSaveName(String saveName);
    /**
     * 
     * @return 
     */
    public Rectangle[][] getGrid();
    /**
     * 
     * @param grid 
     */
    public void setGrid(Rectangle[][] grid);
    /**
     * 
     * @param isModified 
     */
    public void setModified(boolean isModified);
    /**
     * 
     * @return 
     */
    public boolean isModified();
    /**
     * 
     * @param isSaved 
     */
    public void setSaved(boolean isSaved);
    /**
     * 
     * @return 
     */
    public boolean isSaved();
    /**
     * 
     */
    public void clearDropPanels();
    /**
     * 
     * @return 
     */
    public UndoRedo.Manager getUndoManager();
}
