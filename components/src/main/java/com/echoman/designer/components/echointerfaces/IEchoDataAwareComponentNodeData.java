/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

/**
 *
 * @author david.morin
 */
public interface IEchoDataAwareComponentNodeData extends IEchoComponentNodeData {

    /**
     * @return
     */
    public String getParentContainer();

    /**
     * @param parentContainer
     */
    public void setParentContainer(String parentContainer);
    
    /**
     *
     * @return
     */
    public String getTable();

    /**
     *
     * @param table
     */
    public void setTable(String parentContainer, String table);

    /**
     *
     * @param table
     */
    public void setTableFromDefault(String table);

    /**
     *
     */
    public void clearColumn();

    /**
     *
     * @return
     */
    public String getColumn();

    /**
     *
     * @return
     */
    public boolean getIsKeyCol();

    /**
     *
     * @return
     */
    public boolean getisGUIDCol();

    public String getFormLinkColumn();

    public String getFormLinkToColumn();

    public String getUiLinkColumn();

    public String getUiLinkToColumn();
}
