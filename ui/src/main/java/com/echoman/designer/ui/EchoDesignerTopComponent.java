/*
 * The main package for the JDesi form designer.
 */
package com.echoman.designer.ui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echointerfaces.IEchoInspectorTopComponent;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IFormPublisher;
import com.echoman.designer.components.echocommon.GhostGlassPane;
import com.echoman.designer.components.echoform.EchoFormNode;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import com.echoman.designer.inspector.EchoInspectorTopComponent;
import com.echoman.designer.palette.PaletteSupport;
import com.echoman.designer.publish.FormPublisher;
import org.netbeans.editor.StatusBar;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import static java.util.Arrays.asList;

/**
 * The main JDesi window.
 * @author david.morin
 */
public final class EchoDesignerTopComponent extends TopComponent implements IEchoDesignerTopComponent {

    /** The id used by the lookup. */
    private static final String PREFERRED_ID = "EchoDesignerTopComponent";
    //private Lookup.Result result = null;
    /** Instance for the form publishier class. */
    private IFormPublisher formPublisher;
    private IEchoInspectorTopComponent inspector;
    private ExplorerManager mgr;
    private ArrayList<IEchoComponentNodeData> compList;
    private ComponentChangeListener componentChangeListener = null;
    private boolean componentChanged = false;
    private String pKey;
    private String table;
    //private JPanel dropPanel;
    private Map<String, JPanel> dropPanels = new LinkedHashMap<String, JPanel>();
    private String saveName;
    private Rectangle[][] grid;
    private static int count = 0;
    private boolean modified = false;
    private boolean saved = false;
    //Ticket #150
    private UndoRedo.Manager unredoMan = new UndoRedo.Manager();
     //Ticket #354
    private int saveType = EchoUtil.SAVE_TO_FILE;

    @Override
    public IFormPublisher getFormPublisher() {
        return formPublisher;
    }

    @Override
    public ArrayList<IEchoComponentNodeData> getCompList() {
        return compList;
    }

    @Override
    public IEchoInspectorTopComponent getInspector() {
        return inspector;
    }

    @Override
    public ExplorerManager getMgr() {
        return mgr;
    }

    @Override
    public String getPKey() {
        return pKey;
    }

    @Override
    public void setPKey(String pKey) {
        this.pKey = pKey;
    }

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public void clearDropPanels() {
        dropPanels.clear();
    }

    @Override
    public Collection<JPanel> getDropPanels() {
        return dropPanels.values();
    }

    @Override
    public JPanel getDropPanel(String id) {
        JPanel firstPnl = null;
        for (Iterator it = dropPanels.values().iterator(); it.hasNext();) {
            firstPnl = (JPanel) it.next();
            break;
        }
        if (id == null) {
            return firstPnl;
        }
        if ("".equals(id)) {
            return firstPnl;
        }
        if (!dropPanels.containsKey(id)) {
            return firstPnl;
        }
        return dropPanels.get(id);
    }

    @Override
    public void setDropPanel(String id, JPanel dropPanel) {
        this.dropPanels.put(id, dropPanel);
    }

    @Override
    public String getSaveName() {
        return saveName;
    }

    @Override
    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    @Override
    public Rectangle[][] getGrid() {
        return grid;
    }

    @Override
    public void setGrid(Rectangle[][] grid) {
        this.grid = grid;
    }

    @Override
    protected void componentActivated() {
        JDesiWindowManager.setActiveDesignerPage(this);
        //formPublisher.setBrowserHomePage();
        StatusBar.setGlobalCell(StatusBar.INSERT_LOCALE, null);
        StatusBar.setGlobalCell(StatusBar.CELL_TYPING_MODE, null);
        if (inspector != null) {
            ((TopComponent) inspector).requestActive();
        }
        super.componentActivated();
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated();
    }

    /**
     * 
     */
    public EchoDesignerTopComponent() throws Exception {
        super();

        /* Ticket #85
        try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
        Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
        Exceptions.printStackTrace(ex);
        } catch (UnsupportedLookAndFeelException ex) {
        Exceptions.printStackTrace(ex);
        }
         * 
         */

        //result = Utilities.actionsGlobalContext().lookup(new Lookup.Template(Object.class));
        //result.addLookupListener(this);
        //result.allInstances();

        // Search projects for CTL_MainWindow_Title to see where this is set
        // to avoid the build number showing in the title.
        //WindowManager.getDefault().getMainWindow().setTitle("Echo Form DesignEHR");
        
        // Moved from FormComponentShown to here because it was causing an issue when
        // loading a form because the table property was trying to get a connection
        // before this was complete.
        if (!(DBConnections.dbConnect())) {
            throw new Exception("Please set up a valid database connection under the Tools/Options menu.");
            //JOptionPane.showMessageDialog(null, "Please set up a valid database connection under the Tools/Options menu.");
        }

        if (!DBConnections.checkDatabaseStructure()) {
            throw new Exception("The EchoVantage version installed is incompatible with this version of Form DesignEHR.");
        }

        initComponents();

        setName(NbBundle.getMessage(EchoDesignerTopComponent.class, "CTL_EchoDesignerTopComponent"));
        setToolTipText(NbBundle.getMessage(EchoDesignerTopComponent.class, "HINT_EchoDesignerTopComponent"));

        compList = new ArrayList<IEchoComponentNodeData>();

        // This is how you can have more than one lookup associated.
        Lookup first = Lookups.fixed(new Object[]{PaletteSupport.createPalette()});
        inspector = new EchoInspectorTopComponent();

        // This puts the inspector window in the proper position on the right.
        // Even though we are hiding it, leave this in case we do want to show it at some point.
        WindowManager.getDefault().findMode("echoinspector").dockInto((TopComponent) inspector);

        mgr = inspector.getExplorerManager();
        Lookup second = ExplorerUtils.createLookup(mgr, getActionMap());
        ProxyLookup merge = new ProxyLookup(first, second);
        associateLookup(merge);

        // TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("designer");
        // if (group != null) {
        //    group.open();
        // }

        // Create the GhostGlassPane for dragging if it hasn't alread been created.
        if (!(((JFrame) WindowManager.getDefault().getMainWindow()).getGlassPane() instanceof GhostGlassPane)) {
            ((JFrame) WindowManager.getDefault().getMainWindow()).setGlassPane(new GhostGlassPane());
        }

        formPublisher = new FormPublisher(this);
        setHtmlDisplayName("Form" + count);

        //Ticket #188
        this.setFocusable(true);
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    Node[] ary = getMgr().getSelectedNodes();
                    for (int i = 0; i < ary.length; i++) {
                        Node node = ary[i];
                        if (node instanceof EchoBaseNode) {
                            ((IEchoComponentNode) node).delete();
                             break;
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    Node[] ary = getMgr().getSelectedNodes();
                    for (int i = 0; i < ary.length; i++) {
                        Node node = ary[i];
                        if ((node instanceof EchoBaseNode) && (!(node instanceof EchoFormNode))) {
                            JComponent comp = (JComponent)((IEchoComponentNode) node).getComponent();
                            if (e.isShiftDown()) {
                                comp.setSize(comp.getWidth()+1, comp.getHeight());
                            } else {
                                comp.setLocation(comp.getX()+1, comp.getY());
                            }
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    Node[] ary = getMgr().getSelectedNodes();
                    for (int i = 0; i < ary.length; i++) {
                        Node node = ary[i];
                        if ((node instanceof EchoBaseNode) && (!(node instanceof EchoFormNode))) {
                            JComponent comp = (JComponent)((IEchoComponentNode) node).getComponent();
                            if (e.isShiftDown()) {
                                comp.setSize(comp.getWidth()-1, comp.getHeight());
                            } else {
                                comp.setLocation(comp.getX()-1, comp.getY());
                            }
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    Node[] ary = getMgr().getSelectedNodes();
                    for (int i = 0; i < ary.length; i++) {
                        Node node = ary[i];
                        if ((node instanceof EchoBaseNode) && (!(node instanceof EchoFormNode))) {
                            JComponent comp = (JComponent)((IEchoComponentNode) node).getComponent();
                            if (e.isShiftDown()) {
                                comp.setSize(comp.getWidth(), comp.getHeight()-1);
                            } else {
                                comp.setLocation(comp.getX(), comp.getY()-1);
                            }
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    Node[] ary = getMgr().getSelectedNodes();
                    for (int i = 0; i < ary.length; i++) {
                        Node node = ary[i];
                        if ((node instanceof EchoBaseNode) && (!(node instanceof EchoFormNode))) {
                            JComponent comp = (JComponent)((IEchoComponentNode) node).getComponent();
                            if (e.isShiftDown()) {
                                comp.setSize(comp.getWidth(), comp.getHeight()+1);
                            } else {
                                comp.setLocation(comp.getX(), comp.getY()+1);
                            }
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(java.awt.Color.lightGray);
        setForeground(java.awt.Color.black);
        setDoubleBuffered(true);
        setHtmlDisplayName("Workspace");
        setName("workspace"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        setLayout(new java.awt.GridLayout(1, 0));
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
// MOVED ABOVE        if (!(DBConnections.dbConnect())) {
//            JOptionPane.showMessageDialog(null, "Please set up a valid database connection under the Tools/Options menu.");
//        }
    }//GEN-LAST:event_formComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * 
     * @return
     */
    @Override
    public int getPersistenceType() {
        // Do not persist the top components or it will cause problems.
        return TopComponent.PERSISTENCE_NEVER;
    }

    /**
     * 
     */
    @Override
    public void componentOpened() {
        // This was just here to bring the database explorer to life
        // or else it would only connect to the db on the second attempt.
        // This appears to have been resolved in a later release of
        // Netbeans, but I'm leaving this here as a reminder.
        /*
        ConnectionManager conMgr = ConnectionManager.getDefault();
        DatabaseConnection[] conns = conMgr.getConnections();
        if (conns.length > 0) {
        Connection conn = conns[0].getJDBCConnection();
        }
         */
    }

    /**
     * 
     */
    @Override
    public void componentClosed() {
        //Ticket #189 - check if this is the current active page before
        //setting it to null
        if (JDesiWindowManager.getActiveDesignerPage() == this) {
            JDesiWindowManager.setActiveDesignerPage(null);
            //need to clear component list so no node got left behind
            //in the properties window when TopComponent is closed
            compList.clear();
            inspector.refreshList(compList);
            componentChangeListener = null;
        }
        
        if (this.inspector != null) {
            ((TopComponent) inspector).close();
        }
    }

    public int promptSave() {
        int result = JOptionPane.showConfirmDialog(this, "Do you want to save the current form?",
                "Save the current form",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        switch (result) {
            case JOptionPane.YES_OPTION:
                return 1;
            case JOptionPane.NO_OPTION:
                return 0;
            case JOptionPane.CANCEL_OPTION:
                return -1;
        }
        return -1;
    }

    //Ticket #6
    @Override
    public boolean canClose() {
        if (this.modified) {
            int status = promptSave();
            switch (status) {
                case -1:
                    return false;
                case 1:
                    int saveStatus = 1;
                    String name = this.getSaveName();
                    if (!((name == null) || (name.equals("")))) {
                        //Ticket #402 - always save to DB
                        //((FormPublisher) this.getFormPublisher()).saveEchoForm(name);
                        ((FormPublisher) this.getFormPublisher()).saveEchoForm(name, false, false);
                    } else {
                        //Ticket #402 - always save to DB
                        //saveStatus = this.getFormPublisher().doSave();
                        saveStatus = this.getFormPublisher().doSaveToDb();
                    }
                    return saveStatus == 1;
            }
        }
        return super.canClose();
    }

    /**
     * 
     * @return
     */
    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public void setModified(boolean isModified) {
        this.modified = isModified;
        if (isModified) {
            this.saved = false;
        }
    }

    @Override
    public boolean isComponentChanged() {
        return componentChanged;
    }

    @Override
    public void createComponentChangeListener() {
        componentChangeListener = (componentName, propertyName) -> {
            //excludes these properties when tracking whether the component is changed for a signed form
            final List<String> excludedProperties = asList("Form Locations", "Hide Insert");
            if (!excludedProperties.contains(propertyName)) {
                componentChanged = true;
            }
        };
    }

    @Override
    public void doComponentChange(String componentName, String propertyName) {
        if (componentChangeListener != null) {
            componentChangeListener.componentChange(componentName, propertyName);
        }
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }

    @Override
    public void setSaved(boolean isSaved) {
        this.saved = isSaved;
    }

    @Override
    public boolean isSaved() {
        return this.saved;
    }

    @Override
    public UndoRedo getUndoRedo() {
        return unredoMan;
    }

    @Override
    public UndoRedo.Manager getUndoManager() {
        return unredoMan;
    }

    @Override
    public int getSaveType() {
        return saveType;
    }

    @Override
    public void setSaveType(int savetype) {
        this.saveType = savetype;
    }

}
