/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echoform.EchoForm;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.designer.components.echotextfield.EchoTextField;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import org.openide.nodes.Node;

public class CopyPasteManager {

    private List<EchoBaseNodeData> compList = new ArrayList<EchoBaseNodeData>();
    private static CopyPasteManager instance;

    private CopyPasteManager() {
    }

    public static CopyPasteManager getInstance() {
        if (instance == null) {
            instance = new CopyPasteManager();
        }
        return instance;
    }

    private EchoBaseNodeData cloneNodeData(EchoBaseNodeData nodedata) {
        EchoBaseNodeData clone = nodedata.cloneData();
        if (nodedata instanceof EchoTextFieldNodeData) {
            EchoTextFieldNodeData cloneTextNodeData = (EchoTextFieldNodeData)clone;
            EchoTextFieldNodeData textNodeData = (EchoTextFieldNodeData)nodedata;
            cloneTextNodeData.setCaptionLabelId(textNodeData.getCaptionLabelId());
            cloneTextNodeData.setTranslationLabelId(textNodeData.getTranslationLabelId());
        }
        return clone;
    }

    public void copy(Node[] selectedNodes) {
        List comps = Arrays.asList(selectedNodes);
        List<EchoTextFieldNodeData> textNodeDataList = new ArrayList<EchoTextFieldNodeData>();
        compList.clear();
        for (int i = 0; i < comps.size(); i++) {
            EchoBaseNode n = (EchoBaseNode) comps.get(i);
            EchoBaseNodeData ndata = (EchoBaseNodeData) n.getNodeData();
            if (!(ndata instanceof EchoFormNodeData)) {
                //Ticket #260
                //if (!ndata.hasLockedField())
                if (EchoUtil.isRunningAsEchoAdmin()) {
                    compList.add(cloneNodeData(ndata));
                } else {
                    if (!ndata.hasLockedPosition()) {
                        compList.add(cloneNodeData(ndata));
                    }
                }
                //store the TextFieldNodeData for later label processing
                if (ndata instanceof EchoTextFieldNodeData) {
                    textNodeDataList.add((EchoTextFieldNodeData) ndata);
                }
            }
        }
        //Ticket #263
        //Loop thru the TextFieldNodeDataList to find the label
        //if the label is not in the list then find it on designerPage complist
        //and add it to the list
        for (EchoTextFieldNodeData txtNodeData : textNodeDataList) {
            EchoLabelNodeData labelNodeData = null;
            EchoLabelNodeData transNodeData = null;
            for (EchoBaseNodeData nd : compList) {
                if (nd instanceof EchoLabelNodeData) {
                    EchoLabelNodeData lNodeData = (EchoLabelNodeData) nd;
                    if (lNodeData.getId().equals(txtNodeData.getTranslationLabelId())) {
                        transNodeData = (EchoLabelNodeData) nd;
                    } else if (lNodeData.getId().equals(txtNodeData.getCaptionLabelId())) {
                        labelNodeData = (EchoLabelNodeData) nd;
                    }
                }
            }
            if (labelNodeData == null) {
                labelNodeData = findLabel(txtNodeData, txtNodeData.getCaptionLabelId());
                if (labelNodeData != null) {
                    compList.add(cloneNodeData(labelNodeData));
                }
            }
            if (transNodeData == null) {
                transNodeData = findLabel(txtNodeData, txtNodeData.getTranslationLabelId());
                if (transNodeData != null) {
                    compList.add(cloneNodeData(transNodeData));
                }
            }
        }
    }

    private EchoLabelNodeData findLabel(EchoBaseNodeData nodeData, String id) {
        for (IEchoComponentNodeData nd : nodeData.getDesignerPage().getCompList()) {
            if (((EchoBaseNodeData) nd).getId().equals(id)) {
                return (EchoLabelNodeData) nd;
            }
        }
        return null;
    }

    public void paste(EchoForm form) {
        if (!compList.isEmpty()) {
            int formY = form.getLastMouseY();
            int formX = form.getLastMouseX();
            int diffY = -1;
            int diffX = -1;
            int posY = 0;
            int posX = 0;
            int taborder = -2;
            boolean setTabOrder = false;
            HashMap<String, EchoLabelNodeData> labelIdMap = new HashMap<String, EchoLabelNodeData>();
            List<EchoTextFieldNodeData> textNodeDataList = new ArrayList<EchoTextFieldNodeData>();
            for (EchoBaseNodeData nodedata : compList) {
                //Ticket #413
                nodedata.clearUncopiableProperties(((EchoFormNodeData)form.getNode().getNodeData()).getTable());
                if (diffY == -1) {
                    diffY = formY - nodedata.getTop();
                    diffX = formX - nodedata.getLeft();
                    posY = formY;
                    posX = formX;
                    taborder = nodedata.getUniqueTabOrder();
                } else {
                    posY = nodedata.getTop() + diffY;
                    posX = nodedata.getLeft() + diffX;
                    if (nodedata.getTabOrder() > -2) {
                        if (setTabOrder)
                            taborder++;
                    }
                }
                nodedata.setTop(posY);
                nodedata.setLeft(posX);
                EchoBaseNodeData nd = form.createEchoComponent(form.getId(),
                        nodedata, true);
                if (nd.getTabOrder() > -2) {
                    //assign unused tab order so it doesn't change the
                    //tab orders of the original components
                    nd.setTabOrder(-3);
                    nd.setTabOrder(taborder);
                    setTabOrder = true;
                }
                //Ticket #263
                //store the new id for the label to be used later for linking
                if (nodedata instanceof EchoLabelNodeData) {
                    labelIdMap.put(nodedata.getId(), (EchoLabelNodeData) nd);
                //store the textFieldNodeData to link it to the correct label
                } else if (nodedata instanceof EchoTextFieldNodeData) {
                    EchoTextFieldNodeData newNodedata = (EchoTextFieldNodeData) nd;
                    EchoTextFieldNodeData srcNodedata = (EchoTextFieldNodeData) nodedata;
                    newNodedata.setCaptionLabelId(srcNodedata.getCaptionLabelId());
                    newNodedata.setTranslationLabelId(srcNodedata.getTranslationLabelId());
                    textNodeDataList.add(newNodedata);
                }
                try {
                    nodedata.getComponent().getNode().refreshProperties();
                } catch (Exception e) {
                }
            }
            //loop thru all the textFieldNodeData and assign the correct
            //label id
            for (EchoTextFieldNodeData txtNodeData : textNodeDataList) {
                EchoTextField txt = (EchoTextField)txtNodeData.getComponent();
                EchoLabelNodeData newLabel = labelIdMap.get(txtNodeData.getCaptionLabelId());
                if (EchoUtil.isNullOrEmpty(newLabel)) {
                    txtNodeData.setCaptionLabelId("");
                    txt.setCaptionLabel(null);
                } else {
                    txtNodeData.setCaptionLabelId(newLabel.getId());
                    txt.setCaptionLabel(newLabel.getComponent());
                }
                EchoLabelNodeData newTrans = labelIdMap.get(txtNodeData.getTranslationLabelId());
                if (EchoUtil.isNullOrEmpty(newTrans)) {
                    txtNodeData.setTranslationLabelId("");
                    txt.setTranslationLabel(null);
                } else {
                    txtNodeData.setTranslationLabelId(newTrans.getId());
                    txt.setTranslationLabel(newTrans.getComponent());
                }
            }

        }
    }

    public int count() {
        return compList.size();
    }
}
