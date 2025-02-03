/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

import java.util.ArrayList;

/**
 *
 * @author david.morin
 */
public interface IEchoFormNodeData extends IEchoComponentNodeData {

    /**
     *
     * @return
     */
    public String getTable();

    /**
     *
     * @return
     */
    public ArrayList<String> getFormLocationIds();

}
