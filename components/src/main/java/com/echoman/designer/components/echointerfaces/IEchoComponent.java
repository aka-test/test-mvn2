/**
 *
 */
package com.echoman.designer.components.echointerfaces;

import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNode;

/**
 *
 * @author david.morin
 */
public interface IEchoComponent {

    /**
     *
     * @return
     */
    public EchoBaseNode getNode();

    /**
     * 
     */
    public void clearLinkToEdit();
    
    /**
     *
     * @param node
     */
    public void setNode(EchoBaseNode node);
    
    /**
     *
     * 
     */
    public void remove();

    /**
     *
     * @return
     */
    public String getName();
    
    /**
     *
     * @param dropPanel
     */
    public void setDropPanel(JPanel dropPanel);

}
