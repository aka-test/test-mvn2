/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 * @author david.morin
 */
package com.echoman.designer.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import org.netbeans.editor.StatusBar;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall implements Runnable {

    @Override
    public void restored() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Exceptions.printStackTrace(ex);
        }
        WindowManager.getDefault().invokeWhenUIReady(this);
    }

    @Override
    public void run() {
        //Ticket #484
        VersionUtil.getInstance().showVersion();
        
        JToolBar[] tb = findToolbars();
        for (int i = 0; i < tb.length; i++) {
            processToolbar(tb[i]);
        }

        StatusBar.setGlobalCell(StatusBar.INSERT_LOCALE, null);
        StatusBar.setGlobalCell(StatusBar.CELL_TYPING_MODE, null);

        //Ticket #190
        addServicesPanelListener();
        
    }

    private void addServicesPanelListener() {
        //add property change listener to listen for when the service panel
        //is closed so that we can set focus to the active top component if any
        //otherwise the property window will be blank
        TopComponent tc = WindowManager.getDefault().findTopComponent("services");
        if (tc != null) {
            tc.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("ancestor")) {
                        //if services panel is closed
                        if (evt.getNewValue() == null) {
                            if (JDesiWindowManager.getActiveDesignerPage() != null) {
                                ((TopComponent) JDesiWindowManager.getActiveDesignerPage()).requestActive();
                            }
                        }
                    }
                }
            });
        }
    }

    private JToolBar[] findToolbars() {
        List l = new ArrayList();
        JFrame jf = (JFrame) WindowManager.getDefault().getMainWindow();
        findToolbars(jf.getContentPane(), l);
        JToolBar[] tb = (JToolBar[]) l.toArray(new JToolBar[l.size()]);
        return tb;
    }

    private void findToolbars(Component c, List l) {
        // If it's the panel for the ToolbarPool, then remove that
        // mouselistener because this is the one that has the 'Customize...'
        // option that we shouldn't allow access to.
        if (c instanceof JPanel) {
            MouseListener[] ml = c.getMouseListeners();
            for (int j = 0; j < ml.length; j++) {
                if (ml[j].getClass().getName().indexOf("ToolbarPool") >= 0) {
                    c.removeMouseListener(ml[j]);
                }
            }
        }
        // Get a list of toolbar components.
        if (c instanceof JToolBar) {
            l.add(c);
            // Else if it's a container, recursively check it's components.
        } else if (c instanceof Container) {
            Component[] cc = ((Container) c).getComponents();
            for (int i = 0; i < cc.length; i++) {
                findToolbars(cc[i], l);
            }
        }
    }

    private void processToolbar(JToolBar bar) {
        // For any toolbar component, remove the popup mouse listener
        // to prevent the popup menu from showing and allowing them to 
        // change the toolbar settings.
        MouseListener[] ml = bar.getMouseListeners();
        for (int i = 0; i < ml.length; i++) {
            if (ml[i].getClass().getName().indexOf("PopupListener") >= 0) {
                bar.removeMouseListener(ml[i]);
            }
        }
    }

    //Ticket #6
    @Override
    public boolean closing() {
        for (Mode mode : WindowManager.getDefault().getModes()) {
            for (TopComponent tc : WindowManager.getDefault().getOpenedTopComponents(mode)) {
                if (tc.getClass() == EchoDesignerTopComponent.class) {
                    if (!tc.close()) {
                        return false;
                    }
                }
            }
        }
        return super.closing();
    }
}
