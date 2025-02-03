/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import java.beans.PropertyVetoException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echoform.EchoForm;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

public class EchoUndoableEdit extends AbstractUndoableEdit {

    public static final int EDIT_TYPE_PROPERTY = 0;
    public static final int EDIT_TYPE_ADD = 1;
    public static final int EDIT_TYPE_DELETE = 2;
    private EchoBaseNodeData nodeData;
    private EchoBaseNodeData oldNodeData;
    private final int editType;
    private String propName = "";
    private Object oldValue;
    private Object newValue;
    private String compName = "";
    private HashMap<String, EchoBaseNodeData> nodedatas = new HashMap<String, EchoBaseNodeData>();

    public EchoUndoableEdit(EchoBaseNodeData nodeData, int editType) {
        this.nodeData = nodeData.cloneData();
        this.editType = editType;
        compName = nodeData.getName();
        if (editType != EDIT_TYPE_PROPERTY) {
            nodeData.getDesignerPage().doComponentChange(compName, Integer.toString(editType));
        }
    }

    public EchoUndoableEdit(EchoBaseNodeData nodeData,
            EchoBaseNodeData oldNodeData,
            int editType, String propName,
            Object oldValue, Object newValue) {
        this(nodeData, editType);
        this.oldNodeData = oldNodeData;
        this.propName = propName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        if (editType == EDIT_TYPE_PROPERTY) {
            nodeData.getDesignerPage().doComponentChange(compName, propName);
        }
    }

    public void addNodeData(EchoBaseNodeData baseNodeData) {
        nodedatas.put(baseNodeData.getName(), baseNodeData.cloneData());
    }

    @Override
    public boolean canRedo() {
        //no redo for tabs
        if (propName.equalsIgnoreCase("tabs")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canUndo() {
        //no undo for tabs
        if (propName.equalsIgnoreCase("tabs")) {
            return false;
        }
        return true;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (this.editType == EDIT_TYPE_PROPERTY) {
            undoPropertyChange();
        } else if (this.editType == EDIT_TYPE_ADD) {
            undoAdd();
        } else if (this.editType == EDIT_TYPE_DELETE) {
            undoDelete();
        }
    }

    @Override
    public void redo() throws CannotUndoException {
        if (this.editType == EDIT_TYPE_PROPERTY) {
            redoPropertyChange();
        } else if (this.editType == EDIT_TYPE_ADD) {
            redoAdd();
        } else if (this.editType == EDIT_TYPE_DELETE) {
            redoDelete();
        }
    }

    private void undoPropertyChange() {
        setPropertyValue(oldValue, oldNodeData);
//        System.out.println("Undo property Change [" + propName + "]=" + oldValue);
    }

    private void redoPropertyChange() {
        setPropertyValue(newValue, nodeData);
//        System.out.println("Redo property Change [" + propName + "]=" + newValue);
    }

    private void undoAdd() {
        nodedatas.clear();
        deleteComponent();
//        System.out.println("Undo add " + nodeData.getClass().toString());
    }

    private void redoAdd() {
        addComponent();
//        System.out.println("Redo add " + nodeData.getClass().toString());
    }

    private void undoDelete() {
        addComponent();
//        System.out.println("Undo delete " + nodeData.getClass().toString());
    }

    private void redoDelete() {
        nodedatas.clear();
        deleteComponent();
//        System.out.println("Redo delete " + nodeData.getClass().toString());
    }

    private void addComponent() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        if (compList.size() > 0) {
            //if it is a label check to see if it is tied to a text field
            if (nodeData instanceof EchoLabelNodeData) {
                for (int i = 0; i < compList.size(); i++) {
                    if (compList.get(i) instanceof EchoTextFieldNodeData) {
                        EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) compList.get(i);
                        EchoLabelNodeData labelNodeData = (EchoLabelNodeData) nodeData;
                        if ((labelNodeData.getId().equals(textNodeData.getTranslationLabelId())) ||
                            (labelNodeData.getId().equals(textNodeData.getCaptionLabelId()))) {
                            return;
                        }
                    }
                }
            }

            EchoTextFieldNodeData tnd = null;
            EchoFormNodeData formNodeData = (EchoFormNodeData) compList.get(0);
            EchoForm form = null;
            if ("".equals(nodeData.getParentId())) {
                form = formNodeData.getForm();
            } else {
                form = formNodeData.getForm(Integer.parseInt(nodeData.getParentId()));
            }
            if (form != null) {
                for (Entry e : nodedatas.entrySet()) {
                    String cname = "";
                    if (!EchoUtil.isNullOrEmpty(e.getKey())) {
                        cname = e.getKey().toString();
                    }
                    EchoBaseNodeData nd = (EchoBaseNodeData) e.getValue();
                    EchoBaseNodeData nnd = form.createEchoComponent(nd);
                    if (!EchoUtil.isNullOrEmpty(cname)) {
                        nnd.setName(cname);
                    }
                    if (nd instanceof EchoTextFieldNodeData) {
                        tnd = (EchoTextFieldNodeData) nd;
                    }
                }
                EchoBaseNodeData nodedata = form.createEchoComponent(nodeData);
                if (!EchoUtil.isNullOrEmpty(compName)) {
                    nodedata.setName(compName);
                }
                if (nodedata instanceof EchoTextFieldNodeData) {
                    tnd = (EchoTextFieldNodeData) nodedata;
                }
                if (tnd != null) {
                    tnd.linkCaptionTranslationLabelsById();
                }
            }

        }
    }

    private void setPropertyValue(Object val, EchoBaseNodeData data) {
        Object obj = null;
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        if (compList.size() > 0) {
            for (int i = 0; i < compList.size(); i++) {
                EchoBaseNodeData nodedata = (EchoBaseNodeData) compList.get(i);
                if (nodedata.getId().equalsIgnoreCase(nodeData.getId())) {
                    obj = nodedata;
                    break;
                }
            }
        }
        if (obj != null) {
            String mthdName = "set" + propName.replaceAll(" ", "");
            try {
                boolean mthdInvoked = false;
                Method[] mthds = nodeData.getClass().getMethods();
                for (int i = 0; i < mthds.length; i++) {
                    Method mthd = mthds[i];
                    if (mthd.getName().equalsIgnoreCase(mthdName)) {
                        try {
                            mthd.invoke(obj, val);
                            mthdInvoked = true;
                        } catch (IllegalAccessException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IllegalArgumentException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        break;
                    }
                }
                EchoBaseNodeData nd = (EchoBaseNodeData) obj;
                if (!mthdInvoked) {
                    if (data != null) {
                        nd.copy(data);
                        mthdInvoked = true;
                    }
                }
                if (mthdInvoked) {
                    nd.refreshComponent();
                    if (WindowManager.getDefault().findTopComponent("properties").isOpened()) {
                        WindowManager.getDefault().findTopComponent("properties").repaint();
                    }
                }
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void deleteComponent() {
        ArrayList compList = nodeData.getDesignerPage().getCompList();
        EchoBaseNodeData nd = null;
        if (compList.size() > 0) {
            for (int i = 0; i < compList.size(); i++) {
                EchoBaseNodeData nodedata = (EchoBaseNodeData) compList.get(i);
                if (nodedata.getId().equalsIgnoreCase(nodeData.getId())) {
                    nd = nodedata;
                    nodeData.copy(nodedata);
                    break;
                }
            }

            if (nd != null) {
                if (nd instanceof EchoTextFieldNodeData) {
                    //find and delete the label and translation
                    EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData) nd;
                    IEchoComponentNodeData labelNodeData = null;
                    IEchoComponentNodeData transNodeData = null;
                    for (int j = 0; j < compList.size(); j++) {
                        //Ticket #214
                        if (compList.get(j) instanceof EchoLabelNodeData) {
                            EchoLabelNodeData lNodeData = (EchoLabelNodeData) compList.get(j);
                            if (lNodeData.getId().equals(textNodeData.getTranslationLabelId())) {
                                transNodeData = (IEchoComponentNodeData) compList.get(j);
                            } else if (lNodeData.getId().equals(textNodeData.getCaptionLabelId())) {
                                labelNodeData = (IEchoComponentNodeData) compList.get(j);
                            }
                        }
                    }
                    if (labelNodeData != null) {
                        addNodeData((EchoBaseNodeData) labelNodeData);
                        compList.remove(labelNodeData);
                    }
                    if (transNodeData != null) {
                        addNodeData((EchoBaseNodeData) transNodeData);
                        compList.remove(transNodeData);
                    }
                } else if (nd instanceof EchoLabelNodeData) {
                    //find and delete the text Field component if any
                    EchoLabelNodeData labelNodeData = (EchoLabelNodeData) nd;
                    IEchoComponentNodeData textNodeData = null;
                    for (int j = 0; j < compList.size(); j++) {
                        if (compList.get(j) instanceof EchoTextFieldNodeData) {
                            EchoTextFieldNodeData tNodeData = (EchoTextFieldNodeData) compList.get(j);
                            if (tNodeData.getCaptionLabelId().equals(labelNodeData.getId())) {
                                textNodeData = (IEchoComponentNodeData) compList.get(j);
                            } else if (tNodeData.getTranslationLabelId().equals(labelNodeData.getId())) {
                                textNodeData = (IEchoComponentNodeData) compList.get(j);
                            }
                        }
                    }
                    if (textNodeData != null) {
                        IEchoComponentNodeData transNodeData = null;
                        for (int j = 0; j < compList.size(); j++) {
                            //Ticket #214
                            if (compList.get(j) instanceof EchoLabelNodeData) {
                                EchoLabelNodeData lNodeData = (EchoLabelNodeData) compList.get(j);
                                if (lNodeData.getId().equals(((EchoTextFieldNodeData) textNodeData).getTranslationLabelId())) {
                                    transNodeData = (IEchoComponentNodeData) compList.get(j);
                                }
                            }
                        }
                        addNodeData((EchoBaseNodeData) textNodeData);
                        compList.remove(textNodeData);
                        if (transNodeData != null) {
                            addNodeData((EchoBaseNodeData) transNodeData);
                            compList.remove(transNodeData);
                        }
                    }
                }

                compList.remove(nd);

                nodeData.getDesignerPage().getInspector().refreshList(compList);
                EchoFormNodeData formNodeData = (EchoFormNodeData) compList.get(0);
                formNodeData.getForm().repaint();
                try {
                    nodeData.getDesignerPage().getMgr().setSelectedNodes(new Node[]{formNodeData.getComponent().getNode()});
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
