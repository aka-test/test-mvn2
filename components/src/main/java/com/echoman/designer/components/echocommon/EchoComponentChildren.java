/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.echoman.designer.components.echotextfield.EchoTextFieldNode;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import java.beans.PropertyVetoException;
import java.util.List;
import com.echoman.designer.components.echoborder.EchoBorderNode;
import com.echoman.designer.components.echoborder.EchoBorderNodeData;
import com.echoman.designer.components.echobutton.EchoButtonNode;
import com.echoman.designer.components.echobutton.EchoButtonNodeData;
import com.echoman.designer.components.echocheckbox.EchoCheckboxNode;
import com.echoman.designer.components.echocheckbox.EchoCheckboxNodeData;
import com.echoman.designer.components.echodatacontainer.EchoDataContainerNode;
import com.echoman.designer.components.echodatacontainer.EchoDataContainerNodeData;
import com.echoman.designer.components.echoform.EchoFormNode;
import com.echoman.designer.components.echoform.EchoFormNodeData;
import com.echoman.designer.components.echoimage.EchoImageNode;
import com.echoman.designer.components.echoimage.EchoImageNodeData;
import com.echoman.designer.components.echolabel.EchoLabelNode;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.designer.components.echomemofield.EchoMemoFieldNode;
import com.echoman.designer.components.echomemofield.EchoMemoFieldNodeData;
import com.echoman.designer.components.echoradiobutton.EchoRadioButtonNode;
import com.echoman.designer.components.echoradiobutton.EchoRadioButtonNodeData;
import com.echoman.designer.components.echosignature.EchoSignatureBoxNode;
import com.echoman.designer.components.echosignature.EchoSignatureBoxNodeData;
import com.echoman.designer.components.echotable.EchoColumnNode;
import com.echoman.designer.components.echotable.EchoColumnNodeData;
import com.echoman.designer.components.echotable.EchoTableNode;
import com.echoman.designer.components.echotable.EchoTableNodeData;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

import javax.swing.*;

/**
 *
 * @author Dave Athlon
 */
public class EchoComponentChildren extends Children.Keys<Object> {
    Node currNode;

    private final static String CONTROL_UNSUPPORTED_MSG = "The Signature control is no longer supported. Please use Palette Manager | Reset Palette to refresh your Palette.";

    /**
     * 
     * @param o
     * @return
     */
    @Override
    protected Node[] createNodes(Object o) {
        if (o instanceof EchoTextFieldNodeData) {
            EchoTextFieldNodeData obj = (EchoTextFieldNodeData) o;
            currNode = new EchoTextFieldNode(obj);
        }
        else if (o instanceof EchoMemoFieldNodeData) {
            EchoMemoFieldNodeData obj = (EchoMemoFieldNodeData) o;
            currNode = new EchoMemoFieldNode(obj);
        }
        else if (o instanceof EchoLabelNodeData) {
            EchoLabelNodeData obj = (EchoLabelNodeData) o;
            currNode = new EchoLabelNode(obj);
        }
        else if (o instanceof EchoButtonNodeData) {
            EchoButtonNodeData obj = (EchoButtonNodeData) o;
            currNode = new EchoButtonNode(obj);
        }
        else if (o instanceof EchoTableNodeData) {
            EchoTableNodeData obj = (EchoTableNodeData) o;
            currNode = new EchoTableNode(obj);
        }
        else if (o instanceof EchoBorderNodeData) {
            EchoBorderNodeData obj = (EchoBorderNodeData) o;
            currNode = new EchoBorderNode(obj);
        }
        else if (o instanceof EchoCheckboxNodeData) {
            EchoCheckboxNodeData obj = (EchoCheckboxNodeData) o;
            currNode = new EchoCheckboxNode(obj);
        }
        else if (o instanceof EchoRadioButtonNodeData) {
            EchoRadioButtonNodeData obj = (EchoRadioButtonNodeData) o;
            currNode = new EchoRadioButtonNode(obj);
        }
        else if (o instanceof EchoFormNodeData) {
            EchoFormNodeData obj = (EchoFormNodeData) o;
            currNode = new EchoFormNode(obj);
        }
        else if (o instanceof EchoImageNodeData) {
            EchoImageNodeData obj = (EchoImageNodeData) o;
            currNode = new EchoImageNode(obj);
        }
        else if (o instanceof EchoSignatureBoxNodeData) {
            JOptionPane.showMessageDialog(null, CONTROL_UNSUPPORTED_MSG);
            return null;
        }
        else if (o instanceof EchoDataContainerNodeData) {
            EchoDataContainerNodeData obj = (EchoDataContainerNodeData) o;
            currNode = new EchoDataContainerNode(obj);
        }
        //Ticket #298
        else if (o instanceof EchoColumnNodeData) {
            EchoColumnNodeData obj = (EchoColumnNodeData) o;
            currNode = new EchoColumnNode(obj);
        }
        return new Node[]{currNode};
    }

    /**
     * 
     */
    @Override
    protected void removeNotify() {
        
        super.removeNotify();
    }

    /**
     * 
     * @param nodes
     */
    @Override
    protected void destroyNodes(Node[] nodes) {
        currNode = null; 
        super.destroyNodes(nodes);
    }

    /**
     * 
     * @param l
     */
    public void refreshList(ExplorerManager mgr, List<IEchoComponentNodeData> l) {
        setKeys(l);
        if (!(currNode == null)) {
            Node[] ary = new Node[1];
            ary[0] = currNode;
            try {
                mgr.setSelectedNodes(ary);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
