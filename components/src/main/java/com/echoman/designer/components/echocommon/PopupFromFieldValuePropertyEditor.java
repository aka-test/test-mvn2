/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.echoman.jdesi.PopupFromFieldProperties;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.nodes.Node;

/**
 *
 * @author Dave Athlon
 */
public class PopupFromFieldValuePropertyEditor extends PropertyEditorSupport {
    
    /**
     * 
     * @return
     */
    @Override
    public Component getCustomEditor() {
        // Cannot support multi-select with PopupFromFieldValue property because
        // the components can be connected to different tables and we limit the
        // columns you can select to that table.
        Node[] comps = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        if (comps.length > 1) {
            Frame frame = null;
            final JDialog dialog = new JDialog(frame, "Invalid Operation", true);
            dialog.setLocationRelativeTo(null);
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            JLabel label = new JLabel("Property cannot be edited with multiple components selected.");
            JButton button = new JButton("OK") {
                @Override
                public boolean isDefaultButton() {
                    return true;
                }
            };

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
                
            });

            panel.add(label);
            panel.add(button);
            dialog.add(panel);
            dialog.pack();
            return dialog;
        }
        
        return new PopupFromFieldValuePropertyForm(null, true, this);
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
        if ((getValue() != null) && (!((ArrayList<PopupFromFieldProperties>)getValue()).isEmpty())) {
            return "<Popup Properties>";
        }
        return "";
    }
    
    /**
     * 
     * @param s
     */
    @Override
    public void setAsText(String s) {
        setValue(s);
    }

}
