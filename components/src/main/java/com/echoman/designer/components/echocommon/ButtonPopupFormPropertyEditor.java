/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.beans.*;
import java.io.File;
import java.util.ArrayList;
import com.echoman.designer.databasemanager.DesignerPanel;
import org.openide.util.NbPreferences;

/**
 * @author david.morin
 */
public class ButtonPopupFormPropertyEditor extends PropertyEditorSupport {
    // Change to use Forms directory instead of webapp
    private final String formsDir = NbPreferences.forModule(EchoUtil.class).get("FormFileLocation",
                EchoUtil.getFormDir());
    //private final String formsDir = NbPreferences.forModule(DesignerPanel.class).get("appServerDir", "") + "\\webapps\\JDesiWebApp\\WEB-INF\\forms\\";
    
    /**
     * 
     * @return
     */
    @Override
    public String[] getTags() {
        File formDir = new File(formsDir);
        String[] forms = formDir.list();
        ArrayList<String> removeFormExt = new ArrayList<String>();
        for (int i=0; i<forms.length; i++)
            if (forms[i].endsWith(".form"))
                removeFormExt.add(forms[i].substring(0, forms[i].indexOf(".")));
        String[] justFormNames = new String[removeFormExt.size()];
        justFormNames = removeFormExt.toArray(justFormNames);


        if (justFormNames != null)
            return justFormNames;
        else
            return new String[]{"Directory not found"};
    }
}
