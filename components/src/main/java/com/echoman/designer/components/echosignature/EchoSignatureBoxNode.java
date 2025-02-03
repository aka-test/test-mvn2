/**
 *
 */
package com.echoman.designer.components.echosignature;

import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.EventPropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.SignatureTypePropertyEditor;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoSignatureBoxNode extends EchoBaseNode {
    /**
     * 
     * @param o
     */
    public EchoSignatureBoxNode(EchoSignatureBoxNodeData o) {
        super(o);
        setName("SignatureBox" + o.getIndex());
    }
    
    /**
     * 
     * @return
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("property");
        Sheet.Set set3 = Sheet.createPropertiesSet();
        set3.setDisplayName("Events");
        set3.setName("events");
        set3.setValue("tabName", "Events");
            
        try {
            registerProperty(new PropInfo("Name", String.class, "name"), set);
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Tab Order", Integer.class, "tabOrder"), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            // Possible future use
            //registerProperty(new PropInfo("Table", String.class, "getTable", null), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Signature Type", String.class, "signatureType", null, null, SignatureTypePropertyEditor.class, null), set);
            
            registerProperty(new PropInfo("On Sign Procedure", String.class, "signEvent", null, null, EventPropertyEditor.class, null), set3);
            registerProperty(new PropInfo("On Change Attestation", String.class, "changeEvent", null, null, EventPropertyEditor.class, null), set3);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoSignatureBoxNode] CreateSheet error. " + ex.toString());
        }

        // Show/hide Required properties based on signature type.
        EchoSignatureBoxNodeData nd = (EchoSignatureBoxNodeData) nodeData;
        if (nd.getSignatureType().equalsIgnoreCase("Script")) {
            set3.getProperties()[1].setHidden(true);
        } else {
            set3.getProperties()[1].setHidden(false);
        }
        
        sheet.put(set);
        sheet.put(set3);
        storeSheet(sheet);
        return sheet;
    }
    
    /**
     * 
     * @param type
     * @return
     */
    @Override
    public Image getIcon (int type) {
        return ImageUtilities.loadImage("com/echoman/designer/inspector/icons/icons/indent_radio_button_16.png");
    }
    
    /**
     * 
     * @param i
     * @return
     */
    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public String getNodeDisplayName() {
        return nodeData.getIndex() + " - " + ((EchoSignatureBoxNodeData)nodeData).getName();
    }
}
