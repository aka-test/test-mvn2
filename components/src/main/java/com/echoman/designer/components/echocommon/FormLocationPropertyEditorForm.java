/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FormLocationPropertyEditorForm.java
 *
 * Created on Jan 11, 2013, 7:34:36 PM
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import com.echoman.designer.components.echointerfaces.IEchoFormNodeData;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

/**
 *
 * @author david.morin
 */
public class FormLocationPropertyEditorForm extends javax.swing.JDialog {
    /**
     * Access to the calling property editor.
     */
    private PropertyEditorSupport editor = null;
    /**
     * All form locations loaded from the database.  Location is the identifier
     * and LocationInfo is the class that holds all the information
     */
    private ArrayList<LocationInfo> formLocations =  new ArrayList<LocationInfo>();
    /**
     * Store the selected tree node value.
     */
    ArrayList<String> selectedNodeValue = new ArrayList<String>();
    /**
     * Store the selected component node.
     */
    IEchoFormNodeData nodeData = null;

    /**
     * CustomIconRenderer to replace the default icons which didn't make
     * sense in this application.
     */
    private class CustomIconRenderer extends DefaultTreeCellRenderer {
        ImageIcon nonSelectableIcon;
        ImageIcon selectableIcon;
        
        /**
         * Replacement Icons for the tree nodes.
         */
        public CustomIconRenderer() {
            nonSelectableIcon = new ImageIcon(getClass().getClassLoader().getResource("com/echoman/designer/components/echocommon/nonselectableIcon.png"));
            selectableIcon = new ImageIcon(getClass().getClassLoader().getResource("com/echoman/designer/components/echocommon/selectableIcon.png"));
        }
        
        @Override
        public Component getTreeCellRendererComponent(javax.swing.JTree tree,
                Object value, boolean selected, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            Object nodeObj = ((DefaultMutableTreeNode)value).getUserObject();
            LocationInfo locInfo = null;
            if (nodeObj instanceof LocationInfo) {
                locInfo = (LocationInfo)nodeObj;
            }
            /**
             * Change the icon based on whether the node is selectable.
             */
            if ((locInfo == null) || (locInfo.hasChildren.equals("N"))) {
                setIcon(nonSelectableIcon);
            } else {
                setIcon(selectableIcon);
            }
            return this;
        }
    }

    /** 
     * Creates new form FormLocationPropertyEditorForm
     */
    public FormLocationPropertyEditorForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setLocationRelativeTo(parent);
        initComponents();
    }

    /** 
     * Creates new form FormLocationPropertyEditorForm
     */
    public FormLocationPropertyEditorForm(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor) {
        super (parent, modal);
        setLocationRelativeTo(parent);
        initComponents();
        /**
         * Get the selected component nodeData object.
         */
        Node[] ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        nodeData = ((IEchoFormNodeData)((IEchoComponentNode) ary[0]).getNodeData());
        this.editor = editor;
        addTreeListener();
        /**
         * Use a custom renderer to replace the default icons.
         */
        locationTree.setCellRenderer(new CustomIconRenderer());
        buildTree();
    }

    /**
     * Tree listener to monitor node selection.
     */
    private void addTreeListener() {
        locationTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent evt) {
                /**
                 *  Get all nodes whose selection status has changed
                 */
                TreePath[] paths = evt.getPaths();
                /**
                 * Iterate through all affected nodes.
                 */
                for (int i = 0; i < paths.length; i++) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                    LocationInfo locInfo = (LocationInfo)node.getUserObject();
                    String formLocation = locInfo.formLocation;
                    String location = locInfo.location;
                    if ((evt.isAddedPath(i)) && (locInfo.hasChildren.equals("N"))) {
                        /**
                         * This is a node where we don't allow selection.
                         */
                        ((javax.swing.JTree)evt.getSource()).removeSelectionPath(paths[i]);
                    } else if (evt.isAddedPath(i)) {
                        /**
                         *  This node has been selected so add it to the return values.
                         */
                        selectedNodeValue.add(formLocation + ";" + location);
                    } else {
                        /**
                         *  This node has been deselected so remove it to from return values.
                         */
                        selectedNodeValue.remove(formLocation + ";" + location);
                    }
                }
            }
        });
    }

  /**
   * This class stores the location information required to build the tree.
   * It is populated from the database.
   */
    private class LocationInfo {
        public String formLocation;
        public String parentLocation;
        public String location;
        public String hasChildren;

        public LocationInfo(String formLocation, String parentLocation, String location, String hasChildren) {
            this.formLocation = formLocation;
            this.parentLocation = parentLocation;
            this.location = location;
            this.hasChildren = hasChildren;
        }

        @Override
        /**
         * The displayed value in the tree should be just the location.
         */
        public String toString() {
            return location;
        }
    }

    /**
     * Build the tree by querying the data for the values and then
     * adding the tree nodes recursively.
     */
    private void buildTree() {
        /**
         * Need a single root node to start, though we won't display it.
         */
        DefaultMutableTreeNode top =
        new DefaultMutableTreeNode("Form Locations");
        try {
            queryLocationData();
        } catch (SQLException ex) {
            final String msg = ex.getMessage() + "\n Retrieving the FormLocations data.";
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, msg);
                }
            });
        }
        addRootCategories(top);
        removeEmptyNodes(top);
        /**
         * Hide the root node.
         */
        locationTree.setRootVisible(false);
        locationTree.setShowsRootHandles(true);
        locationTree.setExpandsSelectedPaths(true);
        /**
         * Replace the original tree model with our new one.
         */
        locationTree.setModel(new DefaultTreeModel(top));
        setSelectedNode(top);
    }

    /**
     * Set nodes as selected based on the current property value.
     * @param top
     */
    private void setSelectedNode(DefaultMutableTreeNode top) {
        ArrayList<TreePath> treePaths = new ArrayList<TreePath>();
        ArrayList<String> formLocationIds = nodeData.getFormLocationIds();
        for (Enumeration e = top.breadthFirstEnumeration(); e.hasMoreElements();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            LocationInfo locInfo = null;
            if (node.getUserObject() instanceof LocationInfo) {
                 locInfo = (LocationInfo)node.getUserObject();
                 for (String formLocation : formLocationIds) {
                     if (formLocation.equals(locInfo.formLocation)) {
                         treePaths.add(new TreePath(node.getPath()));
                     }
                }
            }
        }
        locationTree.setSelectionPaths(treePaths.toArray(new TreePath[treePaths.size()]));
    }

    /**
     * Remove any empty nodes that are not selectable.
     * @param top
     */
    private void removeEmptyNodes(DefaultMutableTreeNode top) {
        ArrayList<DefaultMutableTreeNode> emptyNodes = new ArrayList<DefaultMutableTreeNode>();
        for (Enumeration e = top.breadthFirstEnumeration(); e.hasMoreElements();) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            LocationInfo locInfo = null;
            if (node.getUserObject() instanceof LocationInfo) {
                 locInfo = (LocationInfo)node.getUserObject();
            }
            if ((node.getChildCount() == 0) && (!(locInfo == null)) && (locInfo.hasChildren.equals("N")))
                /**
                 *  Keep a list because the enumeration is invalidated if a node is removed here.
                 */
                emptyNodes.add(node);
        }
        for (DefaultMutableTreeNode node: emptyNodes) {
            /**
             *  As we remove each node, check to see if it's parent now childless.
             */
            checkForChildlessParent(node);
        }
    }

    /**
     * Recursively check for non-selectable parents without children.
     * @param node
     */
    private void checkForChildlessParent(DefaultMutableTreeNode node) {
        /**
         *  Get the parent of this node so we can check if it has any children left.
         */
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
        LocationInfo locInfo = null;
        if (node.getUserObject() instanceof LocationInfo) {
             locInfo = (LocationInfo)node.getUserObject();
        }
        if ((!(node == null)) && (node.getChildCount() == 0) && (!(locInfo == null)) && (locInfo.hasChildren.equals("N"))) {
            node.removeFromParent();
            /**
             *  Recursively check to see if this parent or it's parents have any children left.
             */
            checkForChildlessParent(parentNode);
        }
    }

    /**
     * Add the root categories - those without parents.
     * @param top
     */
    private void addRootCategories(DefaultMutableTreeNode top) {
        for (LocationInfo value : formLocations) {
            /**
             * If parentLocation is NULL, then this is a top level category so get it's children
             */
            if (value.parentLocation.equals("")) {
                addTreeCategory(top, value);
            }
        }
    }

    /**
     * Add the tree category, and then add all it's children.
     * @param top
     * @param location
     */
    private void addTreeCategory(DefaultMutableTreeNode top, LocationInfo location) {
        DefaultMutableTreeNode category = new DefaultMutableTreeNode(location);
        category.setUserObject(location);
        top.add(category);
        getCategoryChildren(category, location);
    }

    /**
     * Add the tree child.
     * @param top
     * @param location
     */
    private DefaultMutableTreeNode addTreeChild(DefaultMutableTreeNode category, LocationInfo location) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(location);
        child.setUserObject(location);
        category.add(child);
        return child;
    }

    /**
     * Recursively get the children for each tree node.
     * @param category
     * @param location
     */
    private void getCategoryChildren(DefaultMutableTreeNode category, LocationInfo location) {
        DefaultMutableTreeNode newParent = null;
        for (LocationInfo value : formLocations) {
            if ((!location.formLocation.equals(value.formLocation) && value.parentLocation.equals(location.formLocation))) {
                newParent = addTreeChild(category, value);
                getCategoryChildren(newParent, value);
            }
        }
    }

    /**
     * Query all the Form Location data in the database and store locally to build the tree.
     * @throws SQLException
     */
    private void queryLocationData() throws SQLException {
        String qry = "select FormLocation, COALESCE(RTrim(LTrim(ParentLocation)), '') ParentLocation, COALESCE(RTrim(LTrim(Location)), '') Location, HasChildren from dbo.FormLocations";
        DatabaseConnection con = DBConnections.getConnection();
        if (!(con == null)) {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(qry);
                while (rs.next()) {
                    LocationInfo location = new LocationInfo(rs.getString("FormLocation"), rs.getString("ParentLocation"), rs.getString("Location"), rs.getString("HasChildren"));
                    formLocations.add(location);
                    }
                rs.close();
                stmt.close();
                }
            }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        locationTree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        OKBtn = new javax.swing.JButton();
        CancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(FormLocationPropertyEditorForm.class, "FormLocationPropertyEditorForm.title")); // NOI18N
        setAlwaysOnTop(true);
        setForeground(java.awt.Color.white);
        setIconImage(null);
        setIconImages(null);
        setModal(true);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        locationTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        locationTree.setAutoscrolls(true);
        jScrollPane2.setViewportView(locationTree);

        OKBtn.setText(org.openide.util.NbBundle.getMessage(FormLocationPropertyEditorForm.class, "FormLocationPropertyEditorForm.OKBtn.text")); // NOI18N
        OKBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKBtnActionPerformed(evt);
            }
        });

        CancelBtn.setText(org.openide.util.NbBundle.getMessage(FormLocationPropertyEditorForm.class, "FormLocationPropertyEditorForm.CancelBtn.text")); // NOI18N
        CancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(OKBtn)
                .addGap(42, 42, 42)
                .addComponent(CancelBtn)
                .addGap(123, 123, 123))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OKBtn)
                    .addComponent(CancelBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OKBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKBtnActionPerformed
        // TODO add your handling code here:
        editor.setValue(selectedNodeValue);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_OKBtnActionPerformed

    private void CancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBtnActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        dispose();
    }//GEN-LAST:event_CancelBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelBtn;
    private javax.swing.JButton OKBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree locationTree;
    // End of variables declaration//GEN-END:variables

}
