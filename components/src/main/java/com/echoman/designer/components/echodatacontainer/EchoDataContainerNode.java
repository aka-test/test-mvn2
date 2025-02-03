/**
 *
 */
package com.echoman.designer.components.echodatacontainer;

import java.awt.Image;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.ColumnsPropertyEditor;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.PropInfo;
import com.echoman.designer.components.echocommon.TablesPropertyEditor;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Dave Athlon
 */
public class EchoDataContainerNode extends EchoBaseNode
{
    /**
     * 
     * @param o
     */
    public EchoDataContainerNode(EchoDataContainerNodeData o) {
        super(o);
        setName("DataContainer" + o.getIndex());
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
            registerProperty(new PropInfo("Index", Integer.class, "getIndex", null), set);
            registerProperty(new PropInfo("Top", Integer.class, "top"), set);
            registerProperty(new PropInfo("Left", Integer.class, "left"), set);
            registerProperty(new PropInfo("Height", Integer.class, "height"), set);
            registerProperty(new PropInfo("Width", Integer.class, "width"), set);
            registerProperty(new PropInfo("Z-Order", Integer.class, "getZOrder", null), set);
            registerProperty(new PropInfo("Table", String.class, "table", null, null, TablesPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Link Field", String.class, "linkField", null, null, ColumnsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Filter SQL", String.class, "filterSql"), set);
            registerProperty(new PropInfo("Sort SQL", String.class, "sortOrder"), set);
            registerProperty(new PropInfo("Master Table", String.class, "getMasterTable", null), set);
            registerProperty(new PropInfo("Master Link Field", String.class, "masterLinkField", null, null, ColumnsPropertyEditor.class, null), set);
            registerProperty(new PropInfo("Prevent Multiple Records", boolean.class, "preventMultipleRecords"), set);
            
        } catch (NoSuchMethodException ex) {
            EchoUtil.showNotification("Properties", "[EchoDataContainerNode] CreateSheet error. " + ex.toString());
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
        return "EchoDataContainer " + nodeData.getIndex();
    }
}
