/**
 *
 */
package com.echoman.designer.components.echointerfaces;

import java.util.List;

/**
 *
 * @author david.morin
 */
public interface IEchoComponentNodeData {
    /**
     *
     * @return
     */
    public IEchoComponent getComponent();
    
    /**
     *
     * @return
     */
    public boolean getPreventMultipleRecords();

    /**
     *
     * @return
     */
    public String getSortOrder();

    /**
     *
     * @return
     */
    public String getFilterSql();

    /**
     *
     * @return
     */
    public int getIndex();

    /**
     *
     * @param index
     */
    public void setIndex(int index);

    /**
     *
     * @return
     */
    public String getLKey();

    /**
     *
     * @param lkey
     */
    public void setLKey(String lkey);

    /**
     * 
     * @param index
     */
    public void updateName(int index);

    /**
     *
     * @return
     */
    public String getColumnsForSql();

    /**
     *
     * @return
     */
    public int getRowsPerPage();

    /**
     * 
     * @return
     */
    public String[] getExpectedDataType();

    /**
     * 
     * @return
     */
    public int getExpectedSize();

    /**
     *
     * @param fieldName
     */
    public void addLockedField(String fieldName);

    /**
     *
     * @param fieldName
     */
    public void removeLockedField(String fieldName);

    /**
     *
     */
    public void clearLockedFields();

    /**
     *
     * @return
     */
    public List<String> getLockedFields();

    /**
     *
     * @return
     */
    public String getParentId();

    /**
     *
     * @param parentId
     */
    public void setParentId(String parentId, boolean windesiImport);

    /**
     *
     */
    public void initDone();

    /**
     *
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public int getTabOrder();

    /**
     *
     * @param tabOrder
     */
    public void setTabOrder(Integer tabOrder);
    public void initCreate();

    /**
     * 
     */
    public void setBorder();
}
