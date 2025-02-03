/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.ui;

import com.echoman.jdesi.XmlDbFileParser;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.publish.FormPublisher;
import com.echoman.designer.publish.RecentForms;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class RecentFormsAction extends CallableSystemAction {

    class RecentFormsMenu extends JMenu implements DynamicMenuContent {

        public RecentFormsMenu() {
            super();
            this.setText("Recent Forms");

            RecentForms rforms = RecentForms.getInstance();
            rforms.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (!evt.getPropertyName().equals(RecentForms.RECENT_FORM_LIST_PROPERTY)) {
                        return;
                    }
                    updateMenu();
                }
            });
            updateMenu();
        }

        private void updateMenu() {
            removeAll();
            RecentForms rforms = RecentForms.getInstance();
            List<String> list = rforms.getFormList();
            for (String name : list) {
                Action action = createAction(name);
                action.putValue(Action.NAME, name);
                JMenuItem menuItem = new JMenuItem(action);
                add(menuItem);
            }
            if (getItemCount() == 0) {
                String name = "None";
                Action action = createAction(name);
                action.putValue(Action.NAME, name);
                JMenuItem menuItem = new JMenuItem(action);
                menuItem.setEnabled(false);
                add(menuItem);
            }
        }

        private Action createAction(String actionCommand) {
            Action action = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    menuItemActionPerformed(e);
                }
            };

            action.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
            return action;
        }

        private void menuItemActionPerformed(ActionEvent evt) {

            final String formName = evt.getActionCommand();
            if ((formName != null) && (!"".equals(formName))) {
                File file = new File(formName);
                if (file.exists()) {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                EchoDesignerTopComponent ntc = new EchoDesignerTopComponent();
                                ntc.open();
                                ntc.requestActive();
                                JDesiWindowManager.setActiveDesignerPage(ntc);
                                ntc.getFormPublisher().loadEchoForm(formName);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, e.getMessage());
                            }
                        }
                    });
                } else {
                    //Ticket #354
                    XmlDbFileParser p = new XmlDbFileParser(DBConnections.getConnection().getJDBCConnection());
                    try {
                        if (p.isFormExists(formName)) {
                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        EchoDesignerTopComponent ntc = new EchoDesignerTopComponent();
                                        ntc.open();
                                        ntc.requestActive();
                                        JDesiWindowManager.setActiveDesignerPage(ntc);
                                        ((FormPublisher) ntc.getFormPublisher()).loadEchoFormDb(formName);
                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, e.getMessage());
                                    }
                                }
                            });
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid form. [" + formName + "] does not exist.");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
        }

        @Override
        public JComponent[] getMenuPresenters() {
            return new JComponent[]{this};
        }

        @Override
        public JComponent[] synchMenuPresenters(JComponent[] jcs) {
            return getMenuPresenters();
        }
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu menu = new RecentFormsMenu();
        return menu;
    }

    @Override
    public void performAction() {
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RecentFormsAction.class, "CTL_RecentFormsAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
