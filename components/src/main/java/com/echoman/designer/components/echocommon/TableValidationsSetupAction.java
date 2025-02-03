/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import com.echoman.designer.databasemanager.DBConnections;

/**
 * 
 * @author david.morin
 */
public final class TableValidationsSetupAction implements ActionListener {

    /**
     * 
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (DBConnections.dbConnect())  {
            ValidationsTableListSetup setup = new ValidationsTableListSetup(null, true, null, null);
            setup.setLocationRelativeTo(null);
            setup.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "You must have a valid database connection set up.");
        }
    }
}
