/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataContainerNodeData;
import org.openide.nodes.Node;

/**
 *
 * @author david.morin
 */
public class DataContainerManager {

    public static void updateContainerComponents(String parentId, List<IEchoComponentNodeData> compList) {
        // Here we need to loop the components to update all contained with the new table
        for (IEchoComponentNodeData cnd : compList) {
            // Find the data aware components
            if ((((cnd.getParentId() != null) && (cnd.getParentId().equals(parentId))))
            && (cnd instanceof IEchoDataAwareComponentNodeData)) {
                DataContainerManager.checkContainerComponents((IEchoDataAwareComponentNodeData)cnd, compList);
            }
        }
    }

    public static void updateFormComponents(String name, String table, List<IEchoComponentNodeData> compList) {
        // Here we need to loop the components to update all contained with the new table
        for (IEchoComponentNodeData cnd : compList) {
            // Find the data aware components connected to the "Default" form table - doesn't matter which tab.
            if ((cnd instanceof IEchoDataAwareComponentNodeData) &&
               (((IEchoDataAwareComponentNodeData)cnd).getParentContainer().equals(name)) &&
               // Ticket 506 
               ((!(cnd instanceof IEchoDataContainerNodeData)))) {
                    ((IEchoDataAwareComponentNodeData)cnd).setTable("Default", table);
            }
        }
    }

    public static void checkContainerContainers(IEchoDataContainerNodeData thisCnd, List<IEchoComponentNodeData> compList) {
        // Store all the containers on the form in a sorted list
        if (thisCnd.getParentId() == null)
            return;
        
        SortedMap<String, IEchoDataContainerNodeData> containers = new TreeMap<String, IEchoDataContainerNodeData>();
        for (IEchoComponentNodeData cnd : compList) {
            if ((cnd instanceof IEchoDataContainerNodeData)
               && ((cnd.getParentId() != null) && (cnd.getParentId().equals(thisCnd.getParentId())))) {

                int h = ((Component)cnd.getComponent()).getBounds().height;
                int w = ((Component)cnd.getComponent()).getBounds().width;
                int perimeter = 2*(h+w);
                String perim = Integer.toString(perimeter);
                // Container dimensions should never be smaller than 3 digits
                // or larger than 4 digits, so this will keep the order correct.
                if (perim.length() < 4)
                    perim = '0' + perim;
                containers.put(perim + cnd.getName(), (IEchoDataContainerNodeData)cnd);
            }
        }

        // loop through all the container components and find it's parent container
        for (IEchoComponentNodeData cnd : compList) {
            // Find the data aware components
            boolean foundContainer = false;
            if (((cnd instanceof IEchoDataContainerNodeData)
               && (((Component)cnd.getComponent()).isVisible()))
               && ((cnd.getParentId() != null) && (cnd.getParentId().equals(thisCnd.getParentId())))) {
                Rectangle compBounds = ((Component)cnd.getComponent()).getBounds();
                // Loop through the containers starting with the smallest
                for (Map.Entry<String, IEchoDataContainerNodeData> entry : containers.entrySet()) {
                    IEchoDataContainerNodeData nodeData = entry.getValue();
                    Rectangle contBounds = ((Component)nodeData.getComponent()).getBounds();
                    if ((compAndContMultiselected(cnd, nodeData))) {
                        foundContainer = true;
                        break;
                    }
                    if ((!cnd.equals(nodeData))
                            && (contBounds.contains(compBounds))
                            && !(nodeData.getTable() == null)) {
                       if (!((((IEchoDataContainerNodeData)cnd).getMasterTable().equals(nodeData.getTable())) &&
                               (((IEchoDataContainerNodeData)cnd).getParentContainer().equals(nodeData.getComponent().getName())))) {
                            ((IEchoDataContainerNodeData)cnd).setMasterTable(nodeData.getComponent().getName(), nodeData.getTable());
                        }
                        foundContainer = true;
                        break;
                    }
                }
                if (!foundContainer) {
                    ((IEchoDataContainerNodeData)cnd).setMasterTable("Default", "");
                    if (((IEchoDataContainerNodeData)cnd).getTable().equals("")) {
                        ((IEchoDataContainerNodeData)cnd).setTableFromDefault(JDesiWindowManager.getActiveDesignerPage().getTable());
                    }
                }
            }
        }
    }

    private static boolean compAndContMultiselected(IEchoComponentNodeData comp, IEchoComponentNodeData cont) {
        Node[] ary = null;
        ary = JDesiWindowManager.getActiveDesignerPage().getMgr().getSelectedNodes();
        if (ary != null) {
            ArrayList list = new ArrayList(Arrays.asList(ary));
            if ((!comp.equals(cont))
            &&   (list.contains(comp.getComponent().getNode()))
            && (list.contains(cont.getComponent().getNode()))) {
                return true;
            }
        }
        return false;
    }

    // When components are moved or sized they can change containers.
    // This checks for one component to be in any container with the same parent.
    public static void checkContainerComponents(IEchoDataAwareComponentNodeData thisCnd, List<IEchoComponentNodeData> compList) {
        // Store all the containers on the form in a sorted list
        SortedMap<String, IEchoDataContainerNodeData> containers = new TreeMap<String, IEchoDataContainerNodeData>();
        for (IEchoComponentNodeData cnd : compList) {
            if ((cnd instanceof IEchoDataContainerNodeData)
                && (!(cnd.equals(thisCnd)))
                && ((cnd.getParentId() != null) && (cnd.getParentId().equals(thisCnd.getParentId())))) {
                int h = ((Component)cnd.getComponent()).getBounds().height;
                int w = ((Component)cnd.getComponent()).getBounds().width;
                int perimeter = 2*(h+w);
                String perim = Integer.toString(perimeter);
                // Container dimensions should never be smaller than 3 digits
                // or larger than 4 digits, so this will keep the order correct.
                if (perim.length() < 4)
                    perim = '0' + perim;
                containers.put(perim + cnd.getName(), (IEchoDataContainerNodeData)cnd);
            }
        }
        // loop through the containers and find this component
        boolean foundContainer = false;
        //boolean newTable = false;
        if ((thisCnd.getParentId() != null) && (thisCnd.getComponent() != null)) {
            Rectangle compBounds = ((Component)thisCnd.getComponent()).getBounds();
            // Loop through the containers starting with the smallest
            for (Map.Entry<String, IEchoDataContainerNodeData> entry : containers.entrySet()) {
                IEchoDataContainerNodeData nodeData = entry.getValue();
                Rectangle contBounds = ((Component)nodeData.getComponent()).getBounds();
                if ((compAndContMultiselected(thisCnd, nodeData))) {
                    foundContainer = true;
                    break;
                }
                if ((contBounds.contains(compBounds)) && !(nodeData.getTable() == null)) {
                    if (!((((thisCnd.getTable().equals(nodeData.getTable())))) &&
                         ((thisCnd).getParentContainer().equals(nodeData.getComponent().getName())))) {
                        if (thisCnd instanceof IEchoDataContainerNodeData) {
                            ((IEchoDataContainerNodeData)thisCnd).setMasterTable(nodeData.getComponent().getName(), nodeData.getTable());
                            //((IEchoDataContainerNodeData)thisCnd).setMasterLinkField(EchoUtil.getPrimaryKeyForTable(nodeData.getTable()));
                        } else {
                            ((IEchoDataAwareComponentNodeData)thisCnd).setTable(nodeData.getComponent().getName(), nodeData.getTable());
                        }
                    }
                    foundContainer = true;
                    break;
                }
            }
            // If we didn't find a container, then default to the master table.
            if (!foundContainer) {
                if (!((((IEchoDataAwareComponentNodeData)thisCnd).getTable().equals(JDesiWindowManager.getActiveDesignerPage().getTable())) &&
                     (((IEchoDataAwareComponentNodeData)thisCnd).getParentContainer().equals("Default")))) {
                    if (!(thisCnd instanceof IEchoDataContainerNodeData)) {
                        ((IEchoDataAwareComponentNodeData)thisCnd).setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
                    }
                }
            }
        }
    }
}
