/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import java.awt.Frame;
import org.openide.windows.WindowManager;

/**
 * VersionUtil is a singleton that is used to set application 
 * information such as title and version. It is called from Installer when
 * application starts. 
 * 
 * @author Hendra
 */
public class VersionUtil {
    
    private static VersionUtil instance = null;
    private static final String title = "Echo Form DesignEHR";
    private static final String version = "6.0.43";

    protected VersionUtil() {
    }        
    
    public static VersionUtil getInstance() {
        if (instance == null) {
            instance = new VersionUtil();
        }
        return instance;
    }
    
    public void showVersion() {
        Frame f = WindowManager.getDefault().getMainWindow();
        f.setTitle(title);        
        System.setProperty("netbeans.buildnumber", version);
    }
    
}
