/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.io.File;
import com.echoman.designer.databasemanager.DesignerPanel;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbPreferences;

/**
 *
 * @author Hendra
 */
public class EventPropertyEditor extends PropertyEditorSupport {

    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new SelectListForm("Select methods...", null, true, this, getEventMethods());
    }

    /**
     *
     * @return
     */
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAsText() {
        return (String) getValue();
    }

    /**
     *
     * @param s
     */
    @Override
    public void setAsText(String s) {
        setValue(s);
    }

    private String[] getEventMethods() {
        //String jarPath = NbPreferences.forModule(DesignerPanel.class).get("appServerDir", "") +
                //"\\webapps\\JDesiWebApp\\WEB-INF\\lib\\";
        // Changing to get actual location of JDesiWebApp which is different
        // while debugging in the IDE and at runtime.
        //        String jarPath = ".\\formdesigner\\JDesiWebApp\\WEB-INF\\lib\\";
        File f = InstalledFileLocator.getDefault().locate("JDesiWebApp", "", false);
        String jarPath = f.getAbsolutePath() + "\\WEB-INF\\lib\\";
//        String[] excludedClasses = new String[] {"echoDynamicForm.jar",
//                                  "echoJdesiCommon.jar",
//                                  "gson-2.1.jar",
//                                  "jtds-1.2.2.jar",
//                                  "vaadin-6.7.6.jar",
//                                  "echoJdesiWidgets.jar",
//                                  "xpp3_min-1.1.4c.jar",
//                                  "xstream-1.3.jar"};
        try {
//            return (String[]) EchoUtil.getJarClassMethods(jarPath, excludedClasses).toArray(new String[0]);
            return (String[]) EchoUtil.getJarClassMethods(jarPath, null).toArray(new String[0]);
        } catch (Exception ex) {
            return new String[] {};
        }        
    }

    /**
     *
     * @return
     */
    /*
    @Override
    public String[] getTags() {
        return getEventMethods();
    }

     * 
     */
}
