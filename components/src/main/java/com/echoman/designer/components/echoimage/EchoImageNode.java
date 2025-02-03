/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echoimage;

import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.FilenamePropertyEditor;
import com.echoman.designer.components.echocommon.PropInfo;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

public class EchoImageNode extends EchoBaseNode {

    /**
     *
     * @param o
     */
    public EchoImageNode(EchoImageNodeData o) {
        super(o);
        setName("Image" + o.getIndex());
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

        try {
            registerProperty(new PropInfo("Name", String.class, "name"), set);
            registerProperty(new PropInfo("Hint Text", String.class, "hintText"), set);
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Visible", boolean.class, "visible"), set);
            registerProperty(new PropInfo("Filename", String.class, "filename", null, null, FilenamePropertyEditor.class, null), set);
            //registerProperty(new PropInfo("ImageFile", File.class, "imageFile"), set);

        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoImageNode] CreateSheet error. " + ex.toString());
        }
        sheet.put(set);
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
        return "EchoImage" + nodeData.getIndex();
    }

}
