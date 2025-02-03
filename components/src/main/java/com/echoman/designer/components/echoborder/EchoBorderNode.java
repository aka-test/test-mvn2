/**
 *
 */
package com.echoman.designer.components.echoborder;

import java.awt.Color;
import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoDefaultStylePropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.PropInfo;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoBorderNode extends EchoBaseNode
{
    /**
     * 
     * @param o
     */
    public EchoBorderNode(EchoBorderNodeData o) {
        super(o);
        setName("Box" + o.getIndex());
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
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Z-Order", Integer.class, "getZOrder", null), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Echo Default Border", String.class, "echoDefaultStyle", null, null, EchoDefaultStylePropertyEditor.class, null), set2);
            registerProperty(new PropInfo("Border Background Transparent", boolean.class, "borderBackgroundTransparent"), set2);
            registerProperty(new PropInfo("Border Color", Color.class, "borderColor"), set2);
            registerProperty(new PropInfo("Border Background Color", Color.class, "borderBackgroundColor"), set2);
            registerProperty(new PropInfo("Border Left Thickness", Integer.class, "borderLeftThickness"), set2);
            registerProperty(new PropInfo("Border Right Thickness", Integer.class, "borderRightThickness"), set2);
            registerProperty(new PropInfo("Border Top Thickness", Integer.class, "borderTopThickness"), set2);
            registerProperty(new PropInfo("Border Bottom Thickness", Integer.class, "borderBottomThickness"), set2);
            
        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoBorderNode] CreateSheet error. " + ex.toString());
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
        return "EchoBox " + nodeData.getIndex();
    }
}
