/**
 *
 */
package com.echoman.designer.components.echobasenode;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.UndoableEditEvent;
import com.echoman.designer.components.echocommon.EchoUndoableEdit;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echointerfaces.ITableColumnListNodeData;

/**
 * The is the Echo base node data object from which all node data objects are
 * derived. EchoBaseNode is the Explorer node and EchoBaseNodeData holds the
 * properties for the node ie, textfield, label, memo properties.
 *
 * @author david.morin
 */
public abstract class EchoBaseNodeData implements PropertyChangeListener, IEchoComponentNodeData, ITableColumnListNodeData {

    protected transient List listeners = Collections.synchronizedList(new LinkedList());
    protected transient IEchoDesignerTopComponent designerPage;
    protected int index;
    protected String id;
    protected String parentId;
    protected transient IEchoComponent component;
    protected String lKey;
    protected String name;
    protected String hintText = "";
    transient private List<String> lockedFields;
    transient private boolean lockedAllFields = false;
    transient private boolean initialized = false;
    transient protected boolean loadingForm = false;
    transient protected boolean clonedData = false;

    public IEchoDesignerTopComponent getDesignerPage() {
        return designerPage;
    }

    public EchoBaseNodeData() {
    }

    /**
     *
     * @param glassPane
     * @param dropPanel
     */
    public EchoBaseNodeData(IEchoDesignerTopComponent designerPage) {
        super();
        id = UUID.randomUUID().toString().toUpperCase();
        lockedFields = new ArrayList<>();
        this.designerPage = designerPage;
        ArrayList<IEchoComponentNodeData> compList = designerPage.getCompList();
        if (compList != null) {
            index = compList.size();
        } else {
            index = 0;
        }
        listeners = Collections.synchronizedList(new LinkedList());
        this.lKey = EchoUtil.encryptDecrypt(getNoKeyStr(), "ENCRYPT");
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public boolean getLoadingForm() {
        return loadingForm;
    }

    public void setComponent(IEchoComponent component) {
        this.component = component;
    }

    public void setComponentNode(EchoBaseNode node) {
        this.component.setNode(node);
    }

    @Override
    public IEchoComponent getComponent() {
        return component;
    }

    /**
     *
     */
    public void remove() {
        fixNames();
        component.remove();
        component = null;
    }

    public void fixNames() {
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        int oldIndex;
        int newIndex;
        for (int i = 0; i < compList.size(); i++) {
            oldIndex = compList.get(i).getIndex();
            newIndex = i;
            compList.get(i).setIndex(newIndex);
            //Ticket #438
            //compList.get(i).updateName(newIndex);
            //compList.get(i).setIndex(compList.get(i).getIndex()-1);
            //compList.get(i).updateName(compList.get(i).getIndex());
            // This notifies the node to change it's display name.
            ((EchoBaseNodeData) compList.get(i)).fire("nodename", oldIndex, newIndex);
        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // This fires the focus event to the propety sheet
        fire(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    /**
     *
     * @param pcl
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    /**
     *
     * @param pcl
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    public boolean isLockedField(String name) {
        return (getLockedFields().contains(name) || lockedAllFields);
    }

    public boolean isTopLocked() {
        return isLockedField("Top");
    }

    public boolean isLeftLocked() {
        return isLockedField("Left");
    }

    public boolean isWidthLocked() {
        return isLockedField("Width");
    }

    public boolean isHeightLocked() {
        return isLockedField("Height");
    }

    public boolean hasLockedField() {
        return (!getLockedFields().isEmpty() || lockedAllFields);
    }

    public boolean hasLockedPosition() {
        return isTopLocked() || isLeftLocked();
    }

    /**
     *
     * @param propertyName
     * @param old
     * @param nue
     */
    protected void fire(String propertyName, Object old, Object nue) {
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getPreventMultipleRecords() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public String getSortOrder() {
        return "";
    }

    /**
     *
     * @return
     */
    @Override
    public String getFilterSql() {
        return "";
    }

    /**
     *
     * @return
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param i
     */
    @Override
    public void setIndex(int i) {
        index = i;
    }

    public void getId(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
        this.lKey = EchoUtil.encryptDecrypt(getNoKeyStr(), "ENCRYPT");
    }

    private String getNoKeyStr() {
        return id + ";NO_LOCK";
    }

    @Override
    public String getLKey() {
        return this.lKey;

    }

    @Override
    public void setLKey(String lkey) {
        if ((lkey == null) || (lkey.equals(""))) {
            this.lKey = EchoUtil.encryptDecrypt(getNoKeyStr(), "ENCRYPT");
        } else {
            this.lKey = lkey;
            String lk = EchoUtil.encryptDecrypt(lkey, "DECRYPT");
            writeLockedFields(lkey);
        }
    }

    private List<String> getInternalLockedFields() {
        if (lockedFields == null) {
            lockedFields = new ArrayList<>();
        }
        return lockedFields;
    }

    private void writeLockedFields(String lkey) {
        getInternalLockedFields().clear();
        if (!lkey.equals(getNoKeyStr())) {
            String[] lkeys = lkey.split(";");
            boolean checkedKey = false;
            for (String s : lkeys) {
                if (!checkedKey) {
                    //if (index == Integer.parseInt(s)) {
                    if (id.equals(s)) {
                        checkedKey = true;
                    } else {
                        lockedAllFields = true;
                        break;
                    }
                } else {
                    lockedFields.add(s);
                }
            }
        }
    }

    @Override
    public List<String> getLockedFields() {
        try {
            writeLockedFields(EchoUtil.encryptDecrypt(this.lKey, "DECRYPT"));
        } catch (Exception e) {
            lockedAllFields = true;
        }
        return lockedFields;
    }

    @Override
    public int getRowsPerPage() {
        return -1;
    }

    @Override
    public String getColumnsForSql() {
        return "";
    }

    //Ticket #77
    @Override
    public void setBorder() {
        if (getComponent() != null) {
            ((JComponent) getComponent()).setBorder(BorderFactory.createLineBorder(Color.lightGray));
        }
    }

    private void writeLKey() {
        String lk = "";
        for (String s : lockedFields) {
            lk = lk + s + ";";
        }
        if (lk.equals("")) {
            this.lKey = EchoUtil.encryptDecrypt(getNoKeyStr(), "ENCRYPT");
        } else {
            lk = lk.substring(0, lk.length() - 1);
            //have to use id instead of index because index will change if
            //components are deleted
            //this.lKey = EchoUtil.encryptDecrypt(index + ";" + lk, "ENCRYPT");
            this.lKey = EchoUtil.encryptDecrypt(id + ";" + lk, "ENCRYPT");
        }
    }

    @Override
    public void addLockedField(String fieldName) {
        if ((fieldName != null) && (!fieldName.equals(""))) {
            if (!lockedFields.contains(fieldName)) {
                lockedFields.add(fieldName);
                writeLKey();
            }
        }
    }

    @Override
    public void removeLockedField(String fieldName) {
        if ((fieldName != null) && (!fieldName.equals(""))) {
            if (lockedFields.contains(fieldName)) {
                lockedFields.remove(fieldName);
                writeLKey();
            }
        }
    }

    @Override
    public void clearLockedFields() {
        lockedFields.clear();
        writeLKey();
    }

    public String getId() {
        return id;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId, boolean windesiImport) {
        this.parentId = parentId;
    }

    //Ticket #150
    protected void undoableHappened(String propName, Object oldVal, Object newVal) {
        if (initialized) {
            if (!oldVal.equals(newVal)) {
                EchoUndoableEdit edit = new EchoUndoableEdit(this, null,
                        EchoUndoableEdit.EDIT_TYPE_PROPERTY, propName, oldVal, newVal);
                designerPage.getUndoManager().undoableEditHappened(new UndoableEditEvent(this, edit));
            }
        }
    }

    @Override
    public void initDone() {
        initialized = true;
    }

    public void copy(EchoBaseNodeData data) {
        index = data.getIndex();
        id = data.getId();
        parentId = data.getParentId();
        lKey = data.getLKey();
    }

    public void refreshComponent() {
        if (getComponent() != null) {
            JComponent comp = (JComponent) getComponent();
            comp.revalidate();
            comp.repaint();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        // Ticket 438
        this.name = getUniqueName(this.name, name, 0);
        designerPage.setModified(true);
    }

    // Ticket 438
    private String getUniqueName(String oldName, String changedName, Integer idx) {
        int lastIdx = idx;
        for (IEchoComponentNodeData nd : designerPage.getCompList()) {
            if (nd.getName() != null) {
                // Find the first available index or
                if ((!nd.getName().equalsIgnoreCase(oldName)) && (nd.getName().equalsIgnoreCase(changedName))) {
                    idx++;
                }
            }
        }
        if (idx == lastIdx) {
            return changedName;
        } else {
            String newName = changedName;
            //int newIdx = -1;
            String[] splitName = changedName.split("(?<=\\p{L})(?=\\d)|(?<=\\d)(?=\\p{L})");
            if (splitName.length > 0) {
                newName = splitName[0];
            }
            if (idx != 0) {
                newName = newName + Integer.toString(idx);
            }
            return getUniqueName(oldName, newName, idx);
        }
    }

    @Override
    public int getTabOrder() {
        return -2;
    }

    @Override
    public void setTabOrder(Integer tabOrder) {
    }

    public int getUniqueTabOrder() {
        int cnt = -1;
        for (IEchoComponentNodeData nd : designerPage.getCompList()) {
            //Ticket #464
            if (nd.getParentId() != null) {
                if (nd.getParentId().equals(parentId)) {
                    if (nd.getTabOrder() > cnt) {
                        cnt = nd.getTabOrder();
                    }
                }
            }
        }
        return cnt + 1;
    }

    protected void incNextTabOrder(int tabOrder) {
        for (IEchoComponentNodeData nd : designerPage.getCompList()) {
            //Ticket #464
            if (nd.getParentId() != null) {
                if (nd.getParentId().equals(parentId)) {
                    if (nd.getTabOrder() == tabOrder) {
                        nd.setTabOrder(tabOrder + 1);
                        return;
                    }
                }
            }
        }
    }

    public String getNodeType() {
        return "";
    }

    public void createPopupMenu(JPopupMenu popup, ActionListener listener) {
        JMenuItem menuItem;
        //Ticket #187
        boolean enabled = ((EchoUtil.isRunningAsEchoAdmin()) || (!hasLockedField()));
        boolean copyEnabled = ((EchoUtil.isRunningAsEchoAdmin()) || (!hasLockedPosition()));

        menuItem = new JMenuItem("Delete");
        menuItem.setEnabled(enabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);
        popup.addSeparator();
        //Ticket #236
        menuItem = new JMenuItem("Cut");
        menuItem.setEnabled(enabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.setEnabled(copyEnabled);
        menuItem.addActionListener(listener);
        popup.add(menuItem);

        if (EchoUtil.isRunningAsEchoAdmin()) {
            popup.addSeparator();
            menuItem = new JMenuItem("Lock Properties");
            menuItem.addActionListener(listener);
            popup.add(menuItem);
        }
    }

    public abstract EchoBaseNodeData cloneData();

    public abstract void setTop(Integer top);

    public abstract void setLeft(Integer left);

    public abstract int getTop();

    public abstract int getLeft();

    //Ticket #413
    //Call to clear all properties that no need to be copied
    public abstract void clearUncopiableProperties(String table);

    @Override
    public String[] getTableList(String propertyName) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, String> getColumnList(String propertyName) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, String> getMasterColumnList(String propertyName) {
        return null;
    }

    // Ticket 529
    public String getFormLinkColumn() {
        return "";
    }

    public String getFormLinkToColumn() {
        return "";
    }

    public String getUiLinkColumn() {
        return "";
    }

    public String getUiLinkToColumn() {
        return "";
    }

    public int getTabOrder(int tabOrder, boolean visible) {
        final int order = Math.abs(tabOrder);
        return visible ? order : order * -1;
    }

}
