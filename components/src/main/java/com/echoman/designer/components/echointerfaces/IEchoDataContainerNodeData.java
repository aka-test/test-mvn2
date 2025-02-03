/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

/**
 *
 * @author david.morin
 */
public interface IEchoDataContainerNodeData extends IEchoDataAwareComponentNodeData{

    /**
     *
     * @return
     */
    public String getMasterTable();

    /**
     *
     * @param masterTable
     */
    public void setMasterTable(String parentContainer, String masterTable);

    /**
     *
     * @return
     */
    public String getLinkField();

    /**
     *
     * @param linkField
     */
    public void setLinkField(String linkField);

    /**
     *
     * @return
     */
    public String getMasterLinkField();

    /**
     *
     * @param masterLinkField
     */
    public void setMasterLinkField(String masterLinkField);

}
