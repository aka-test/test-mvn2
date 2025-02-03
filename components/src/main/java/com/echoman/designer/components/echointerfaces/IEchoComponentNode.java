/**
 *
 */
package com.echoman.designer.components.echointerfaces;

/**
 *
 * @author david.morin
 */
public interface IEchoComponentNode {

    /**
     * 
     * @return
     */
    public IEchoComponentNodeData getNodeData();
    
    /**
     * 
     * @return
     */
    public IEchoComponent getComponent();

    /**
     * 
     * @return
     */
    public boolean getIsDestroying();

    /**
     *
     * @return
     */
    public String getNodeDisplayName();

    /**
     * 
     */
    public void delete();
}
