/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echointerfaces;

/**
 *
 * @author Dave Athlon
 */
public interface IFormPublisher {
    /**
     * 
     */
    public void doPreview();
    /**
     * 
     */
    public void doNew();
    /**
     * 
     * @return 
     */
    public int doSave();
    /**
     * 
     * @return 
     */
    public int doSaveToDb();
    /**
     * 
     */
    public void doLoad();
    /**
     * 
     * @param type 
     */
    public void doExport(String type);
    /**
     * 
     */
    public void setBrowserHomePage();
    /**
     * 
     * @return 
     */
    public String getWebPageName();
    /**
     * 
     * @return 
     */
    public String getFormDir();
    /**
     * 
     * @return 
     */
    public String getCacheDir();
    /**
     * 
     * @param name 
     */
    public void loadEchoForm(String name);
}
