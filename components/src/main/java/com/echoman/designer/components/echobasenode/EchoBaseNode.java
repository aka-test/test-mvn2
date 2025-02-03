/**
 *
 */
package com.echoman.designer.components.echobasenode;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.CopyPasteManager;
import com.echoman.designer.components.echocommon.EchoUndoableEdit;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.PropLockForm;
import com.echoman.designer.components.echocommon.TablesPropertyEditor;
import com.echoman.designer.components.echoform.EchoForm;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNode;
import com.echoman.designer.components.echointerfaces.ITableColumnListNodeData;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * This is the Echo base node from which all nodes are derived.
 * It includes common functionality like adding to selected nodes
 * and the property change listener.
 * @author Dave Athlon
 */
public abstract class EchoBaseNode extends AbstractNode implements PropertyChangeListener, NodeListener, IEchoComponentNode {

    protected EchoBaseNodeData nodeData;
    private boolean isDestroying;
    protected List<PropInfo> propInfoList = new ArrayList<>();
    private Sheet sheet = null;

    public void storeSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public void restoreSheet() {
        setSheet(sheet);
    }

    /**
     *
     * @return
     */
    @Override
    public String getHtmlDisplayName() {
        if (nodeData != null) {
            return "<font color='!textText'>&nbsp;&nbsp;&nbsp;&nbsp;" + getHtmlDisplayName() + "</font>";
        } else {
            return null;
        }
    }

    /**
     * 
     * @return
     */
    @Override
    public IEchoComponentNodeData getNodeData() {
        return (IEchoComponentNodeData) nodeData;
    }

    /**
     * 
     * @return
     */
    @Override
    public IEchoComponent getComponent() {
        if (!((isDestroying) || (nodeData == null))) {
            return nodeData.getComponent();
        }
        return null;
    }

    /**
     * 
     * @param value
     */
    public void setIsDestroying(boolean value) {
        isDestroying = value;
    }

    @Override
    public boolean getIsDestroying() {
        return isDestroying;
    }

    public EchoBaseNode(EchoBaseNodeData o) {
        super(Children.LEAF, Lookups.singleton(o));
        addNodeListener(this);
        nodeData = o;
        o.setComponentNode(this);
        isDestroying = false;
        o.addPropertyChangeListener(WeakListeners.propertyChange(this, o));
        ((TopComponent) nodeData.getDesignerPage().getInspector()).addPropertyChangeListener(WeakListeners.propertyChange(
                this, ((TopComponent) nodeData.getDesignerPage().getInspector())));
    }

    /**
     * 
     */
    public void removeNode() {
        nodeData.remove();
        nodeData = null;
    }

    /**
     * 
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final PropertyChangeEvent event = evt;
        final EchoBaseNode baseNode = this;

        if (!(isDestroying)) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                @Override
                // Calls like this that affect UI must be done in the AWT thread
                // and this prevents it from running outside of it.
                // We have to check isDestroying within here as well since it
                // can be false in the outer loop, but true when finally called in here.
                public void run() {
                    if ((!isDestroying) && ("selectedNodes".equals(event.getPropertyName()))) {
                        try {
                            Node[] selectedNodes = nodeData.getDesignerPage().getMgr().getSelectedNodes();
                            List comps = Arrays.asList(selectedNodes);
                            if ((comps.contains(baseNode))
                                    && (!nodeData.getClass().getName().contains("EchoDataContainerNodeData"))
                                    && (!nodeData.getClass().getName().contains("EchoFormNodeData"))) {
                                ((JComponent) getComponent()).setBorder(BorderFactory.createLineBorder(Color.red, 1));
                                ((JComponent)getComponent()).repaint(((JComponent)getComponent()).getBounds());
                            } else //Ticket #77
                            {
                                nodeData.setBorder();
                                ((JComponent)getComponent()).repaint(((JComponent)getComponent()).getBounds());
                            }
                        } catch (Exception ex) {
                        }
                        //Ticket #325
                        if (nodeData != null) {
                            ((TopComponent) nodeData.getDesignerPage()).repaint();
                        }
                    }
                    if ((!isDestroying) && ("refresh".equals(event.getPropertyName()))) {
                        if (WindowManager.getDefault().findTopComponent("properties").isOpened()) {
                            WindowManager.getDefault().findTopComponent("properties").repaint();
                        }
                    }
                    if ((!isDestroying) && ("nodename".equals(event.getPropertyName()))) {
                        baseNode.setName(baseNode.getComponent().getName());
                        baseNode.fireDisplayNameChange(null, getDisplayName());
                    }
                }
            });
        }

    }

    /**
     * This function adds another node to the list of selected nodes.
     * If multiSelect is false, then it will clear all other selected nodes
     * and select only the current one.
     * @param multiSelect
     */
    public void addSelectedNode(boolean multiSelect) {
        //Ticket #201
        boolean isNodeSelected = false;

        Node[] ary = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        if (ary != null) {
            ArrayList list = new ArrayList(Arrays.asList(ary));
            if (list.contains(this)) {
                if (multiSelect) {
                    isNodeSelected = true;
                } else {
                    return;
                }
            }
        }

        if (multiSelect) {
            ary = nodeData.getDesignerPage().getMgr().getSelectedNodes();
            if (ary != null) {
                ArrayList list = new ArrayList(Arrays.asList(ary));
                if (isNodeSelected) {
                    list.remove(this);
                } else {
                    list.add(this);
                }
                Node[] a = (Node[]) list.toArray(new Node[0]);
                try {
                    nodeData.getDesignerPage().getMgr().setSelectedNodes(a);
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            ary = new Node[1];
            ary[0] = this;
            try {
                nodeData.getDesignerPage().getMgr().setSelectedNodes(ary);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * 
     * @param arg0
     */
    @Override
    public void childrenAdded(NodeMemberEvent arg0) {
        // No children added to this node.
    }

    /**
     * 
     * @param arg0
     */
    @Override
    public void childrenRemoved(NodeMemberEvent arg0) {
        // No children removed from this node.
    }

    /**
     * 
     * @param arg0
     */
    @Override
    public void childrenReordered(NodeReorderEvent arg0) {
        // No children reordered in this node.
    }

    /**
     * 
     * @param arg0
     */
    @Override
    public void nodeDestroyed(NodeEvent arg0) {
        removeNode();
    }

    private PropertySupport.Reflection createProperty(final PropInfo propInfo,
            final Boolean locked) throws NoSuchMethodException {
        PropertySupport.Reflection prop;
        if (propInfo.hasProperty()) {
            prop = new PropertySupport.Reflection(nodeData,
                    propInfo.getValueType(), propInfo.getProperty()) {

                @Override
                public PropertyEditor getPropertyEditor() {
                    if (!(propInfo.getEditorType() == null) && (propInfo.getEditorType().equals(TablesPropertyEditor.class))) {
                        return new TablesPropertyEditor((ITableColumnListNodeData)nodeData);
                    } else if (!(propInfo.getEditorType() == null) && (propInfo.getEditorType().equals(ColumnsPropertyEditor.class))) {
                        return new ColumnsPropertyEditor((ITableColumnListNodeData)nodeData);
                    } else {
                        return super.getPropertyEditor();
                    }
                }
                // Override getHtmlDisplayName to set the property name red
                // which indicates that it is locked.

                @Override
                public String getHtmlDisplayName() {
                    if (EchoUtil.isRunningAsEchoAdmin()) {
                        if (!locked) {
                            return null;
                        } else {
                            return "<font color='FF0000'>" + propInfo.getName() + "</font>";
                        }
                    } else {
                        return null;
                    }
                }

                //Ticket #183
                @Override
                public boolean canWrite() {
                    if (locked) {
                        return EchoUtil.isRunningAsEchoAdmin();
                    } else {
                        return super.canWrite();
                    }
                }

                @Override
                public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Object oldVal = getValue();
                    EchoBaseNodeData oldNodeData = nodeData.cloneData();
                    super.setValue(val);
                    doPropertyValueChange(oldNodeData, this.getName(), oldVal, val);
                }
            };
        } else {
            prop = new PropertySupport.Reflection(nodeData,
                    propInfo.getValueType(), propInfo.getGetter(), propInfo.getSetter()) {
                // Override getHtmlDisplayName to set the property name red
                // which indicates that it is locked.

                @Override
                public String getHtmlDisplayName() {
                    if (EchoUtil.isRunningAsEchoAdmin()) {
                        if (!locked) {
                            return null;
                        } else {
                            return "<font color='FF0000'>" + propInfo.getName() + "</font>";
                        }
                    } else {
                        return null;
                    }
                }

                //Ticket #183
                @Override
                public boolean canWrite() {
                    if (locked) {
                        return EchoUtil.isRunningAsEchoAdmin();
                    } else {
                        return super.canWrite();
                    }
                }

                @Override
                public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Object oldVal = getValue();
                    EchoBaseNodeData oldNodeData = nodeData.cloneData();
                    super.setValue(val);
                    doPropertyValueChange(oldNodeData, this.getName(), oldVal, val);
                }
            };
        }
        prop.setName(propInfo.getName());
        if (propInfo.hasEditorType()) {
            if (prop.canWrite()) {
                prop.setPropertyEditorClass(propInfo.getEditorType());
            }
        }
        if (propInfo.hasValue()) {
            try {
                prop.setValue(propInfo.getValue());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return prop;
    }

    //Ticket #150
    public void doPropertyValueChange(EchoBaseNodeData oldNodeData,
            String propName, Object oldVal, Object newVal) {

        if (!Objects.equals(oldVal, newVal)) {
            EchoUndoableEdit edit = new EchoUndoableEdit(nodeData, oldNodeData, EchoUndoableEdit.EDIT_TYPE_PROPERTY, propName, oldVal, newVal);
            nodeData.getDesignerPage().getUndoManager().undoableEditHappened(new UndoableEditEvent(this, edit));
        }
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                ((TopComponent) nodeData.getDesignerPage()).requestActive();
            }
        });
    }

    protected void registerProperty(PropInfo propInfo, Set set) throws NoSuchMethodException {
        if (nodeData.isLockedField(propInfo.getName())) {
            //Ticket #183
            //if (EchoUtil.isRunningAsEchoAdmin()) {
            PropertySupport.Reflection prop = createProperty(propInfo, true);
            prop.setDisplayName(propInfo.getName());
            if (propInfo.isOnlyUseCustomEditor()) {
                prop.setValue("canEditAsText", Boolean.FALSE);
            }
            set.put(prop);
            //}
        } else {
            PropertySupport.Reflection prop = createProperty(propInfo, false);
            if (propInfo.isOnlyUseCustomEditor()) {
                prop.setValue("canEditAsText", Boolean.FALSE);
            }
            set.put(prop);
        }
        if (findPropertyInfo(propInfo.getName()) == null) {
            propInfoList.add(propInfo);
        }
    }

    public List<PropInfo> getPropertiesInfo() {
        return propInfoList;
    }

    public PropInfo findPropertyInfo(String name) {
        PropInfo prop = null;
        for (PropInfo p : propInfoList) {
            if (p.getName().equalsIgnoreCase(name)) {
                prop = p;
                break;
            }
        }
        return prop;
    }

    public void refreshProperties() {
        setSheet(createSheet());
    }

    //Ticket #150
    protected void deleteUndoableHappened(EchoBaseNodeData data) {
        EchoUndoableEdit edit = new EchoUndoableEdit(data, EchoUndoableEdit.EDIT_TYPE_DELETE);
        nodeData.getDesignerPage().getUndoManager().undoableEditHappened(new UndoableEditEvent(data, edit));
    }

    private EchoFormNodeData getFormNodeData() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        if (compList.size() > 0) {
            return (EchoFormNodeData) compList.get(0);
        }
        return null;
    }

    protected void refreshList() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        EchoFormNodeData formNodeData = getFormNodeData();
        EchoForm form;
        if ("".equals(nodeData.getParentId()))
            form = formNodeData.getForm();
        else
            form = formNodeData.getForm(Integer.parseInt(nodeData.getParentId()));
        nodeData.getDesignerPage().getInspector().refreshList(compList);
        if (form != null)
            form.repaint();
    }

    @Override
    public void delete() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        Node[] selectedNodes = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        List comps = Arrays.asList(selectedNodes);
        for (int i = 0; i < comps.size(); i++) {
            EchoBaseNode n = (EchoBaseNode) comps.get(i);
            IEchoComponentNodeData ndata = n.getNodeData();
            EchoBaseNodeData nd = (EchoBaseNodeData) ndata;
            if ((EchoUtil.isRunningAsEchoAdmin()) || (!nd.hasLockedField())) {
                deleteUndoableHappened(nd);
                compList.remove(ndata);
            }
        }
        refreshList();        
    }

    public boolean handleAction(String actionStr) {
        if (actionStr.equalsIgnoreCase("Delete")) {
            delete();
            return true;
        } else if (actionStr.equalsIgnoreCase("Cut")) {
            if (nodeData.getDesignerPage().getMgr().getSelectedNodes().length > 0) {
                CopyPasteManager.getInstance().copy(
                        nodeData.getDesignerPage().getMgr().getSelectedNodes());
                List comps = Arrays.asList(nodeData.getDesignerPage().getMgr().getSelectedNodes());
                for (int i = 0; i < comps.size(); i++) {
                    EchoBaseNode n = (EchoBaseNode) comps.get(i);
                    if (!(n.getNodeData() instanceof EchoFormNodeData)) {
                        n.delete();
                        break;
                    }
                }
            }
            return true;
        } else if (actionStr.equalsIgnoreCase("Copy")) {
            if (nodeData.getDesignerPage().getMgr().getSelectedNodes().length > 0) {
                CopyPasteManager.getInstance().copy(
                        nodeData.getDesignerPage().getMgr().getSelectedNodes());
            }
            return true;
        } else if (actionStr.equalsIgnoreCase("Paste")) {
            try {
                if (CopyPasteManager.getInstance().count() > 0) {
                    ArrayList compList = nodeData.getDesignerPage().getCompList();
                    EchoForm form = ((EchoFormNodeData) compList.get(0)).getActiveForm();
                    CopyPasteManager.getInstance().paste(form);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to Paste component. Error: " + 
                        ex.toString());
            }
            return true;
        } else if (actionStr.equalsIgnoreCase("Lock Properties")) {
            PropLockForm dlg = new PropLockForm(null, this);
            dlg.setVisible(true);
            return true;
        } else {
            return false;
        }
    }

}
