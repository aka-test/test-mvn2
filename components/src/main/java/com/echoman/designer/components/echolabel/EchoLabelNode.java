/**
 *
 */
package com.echoman.designer.components.echolabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.AlignmentPropertyEditor;
import com.echoman.designer.components.echocommon.EchoDefaultStylePropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.PropInfo;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoLabelNode extends EchoBaseNode {

    /**
     * 
     * @param o
     */
    public EchoLabelNode(EchoLabelNodeData o) {
        super(o);
        setName("Label" + o.getIndex());
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
        Sheet.Set set2 = Sheet.createPropertiesSet();
        set2.setDisplayName("Style");
        set2.setName("style");
        set2.setValue("tabName", "Style");
            
        try {
            registerProperty(new PropInfo("Name", String.class, "name"), set);
            registerProperty(new PropInfo("Hint Text", String.class, "hintText"), set);
            registerProperty(new PropInfo("Alignment", String.class, "alignment", null, null, AlignmentPropertyEditor.class, null), set);
            registerProperty(new PropInfo("AutoSize", boolean.class, "autoSize"), set);
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Caption", String.class, "caption"), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Echo Default Style", String.class, "echoDefaultStyle", null, null, EchoDefaultStylePropertyEditor.class, null), set2);
            registerProperty(new PropInfo("Font", Font.class, "font", true), set2);
            registerProperty(new PropInfo("Font Color", Color.class, "fontColor"), set2);
            //Turn this off for now since background does not paint correctly
            //with Vaadin label. Users can still use the Border component if
            //they need a different color background for the label.
            //registerProperty(new PropInfo("Background Color", Color.class, "backgroundColor"), set2);
            //registerProperty(new PropInfo("Background Transparent", boolean.class, "backgroundTransparent"), set2);
        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoLabelNode] CreateSheet error. " + ex.toString());
        }
             
        sheet.put(set);
        sheet.put(set2);
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
        return nodeData.getIndex() + " - " + ((EchoLabelNodeData)nodeData).getCaption();
    }
}
