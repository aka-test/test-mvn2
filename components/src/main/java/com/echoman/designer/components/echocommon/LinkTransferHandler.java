/**
 *
 */
package com.echoman.designer.components.echocommon;

import javax.swing.JComponent;
import javax.swing.JList;

/**
 *
 * @author david.morin
 */
class LinkTransferHandler extends JoinTransferHandler {
    protected static SQLTableVisualComponent dragSource = null;
    protected static int dragSourceIndex = -1;

   /**
   * 
   * @param c
   * @return
   */
    @Override
    protected String exportString(JComponent c) {
    JList list = (JList) c;
    SQLTableVisualComponent source = (SQLTableVisualComponent)c.getRootPane().getParent();
    int sourceIndex = list.getSelectedIndex();
    dragSource = source;
    dragSourceIndex = sourceIndex;
    return (String)list.getSelectedValue();
  }

  /**
   * 
   * @param c
   * @param str
   */
    @Override
    protected void importString(JComponent c, String str) {
    JList target = (JList) c;
    int index = target.getSelectedIndex();
    SQLTableVisualComponent dest = (SQLTableVisualComponent)c.getRootPane().getParent();

    int sourceIndex = dragSourceIndex;
    SQLTableVisualComponent source = dragSource;

    // Using to connect tables by linking columns
    if (!(dest.equals(source))) {
        String name = source.getTableName() + "_" + dest.getTableName();
        dest.addLinkConnector(name, index, "to", null);
        source.addLinkConnector(name, sourceIndex, "from", dest);
    }
  }

  /**
   * If the remove argument is true, the drop has been
   * successful and it's time to remove the selected items
   * from the list. If the remove argument is false, it
   * was a Copy operation and the original list is left
   * intact.
   * 
   * @param c
   * @param remove
   */
    @Override
    protected void cleanup(JComponent c, boolean remove) {
        dragSource = null;
        dragSourceIndex = -1;
    }

}