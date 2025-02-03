/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import com.echoman.jdesi.XmlDbFileParser;
import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.List;
import com.echoman.designer.databasemanager.DBConnections;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 */
public class SelectFormPropertyEditor extends PropertyEditorSupport {

    //private final String formsDir = NbPreferences.forModule(DesignerPanel.class).get("appServerDir", "") +
    //        "\\webapps\\JDesiWebApp\\WEB-INF\\forms\\";
    //private final String formsDir = NbPreferences.forModule(EchoUtil.class).get("FormFileLocation",
    //            EchoUtil.FORMDIR);
    
    /*
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        return new SelectListForm("Select forms...", null, true, this, getFormMethods());
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

    private String[] getFormMethods() {
        XmlDbFileParser parser = new XmlDbFileParser(DBConnections.getConnection().getJDBCConnection());
        List<String> forms = null;
        String[] justFormNames = null;
        // Ticket 532
        try {
            forms = parser.loadFormNames();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        if (forms != null) {
            justFormNames = forms.toArray(new String[forms.size()]);
        }

        if (justFormNames == null)
            return new String[]{};
        else
            return justFormNames;
    }
}
