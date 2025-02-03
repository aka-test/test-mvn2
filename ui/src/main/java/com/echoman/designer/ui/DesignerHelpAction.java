/**
 *
 */
package com.echoman.designer.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * 
 * @author david.morin
 */
public final class DesignerHelpAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String helpFileName = System.getProperty("user.dir") + "\\formdesignehr\\JDesiHelp.pdf";
        try {
            File f = new File(helpFileName);
            if (f.exists()) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + f);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
}
