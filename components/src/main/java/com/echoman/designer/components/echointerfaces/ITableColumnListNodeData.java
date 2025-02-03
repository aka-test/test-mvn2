/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

import java.util.HashMap;

/**
 *
 * @author david.morin
 */
public interface ITableColumnListNodeData {
    /**
     *
     * @return
     */
    public String[] getTableList(String propertyName);

    /**
     *
     * @return
     */
    public HashMap<String,String> getColumnList(String propertyName);

    /**
     *
     * @return
     */
    public HashMap<String,String> getMasterColumnList(String propertyName);
}
