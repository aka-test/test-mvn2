/**
 *
 */
package com.echoman.designer.components.echocommon;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author david.morin
 */
abstract class JoinTransferHandler extends TransferHandler {

  /**
   * 
   * @param c
   * @return
   */
  protected abstract String exportString(JComponent c);

  /**
   * 
   * @param c
   * @param str
   */
  protected abstract void importString(JComponent c, String str);

  /**
   * 
   * @param c
   * @param remove
   */
  protected abstract void cleanup(JComponent c, boolean remove);

  /**
   * 
   * @param c
   * @return
   */
  @Override
  protected Transferable createTransferable(JComponent c) {
    return new StringSelection(exportString(c));
  }

  /**
   * 
   * @param c
   * @return
   */
  @Override
  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  /**
   * 
   * @param c
   * @param t
   * @return
   */
  @Override
  public boolean importData(JComponent c, Transferable t) {
    if (canImport(c, t.getTransferDataFlavors())) {
      try {
        String str = (String) t
            .getTransferData(DataFlavor.stringFlavor);
        importString(c, str);
        return true;
      } catch (UnsupportedFlavorException ufe) {
      } catch (IOException ioe) {
      }
    }

    return false;
  }

  /**
   * 
   * @param c
   * @param data
   * @param action
   */
  @Override
  protected void exportDone(JComponent c, Transferable data, int action) {
    cleanup(c, action == MOVE);
  }

  /**
   * 
   * @param c
   * @param flavors
   * @return
   */
  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    for (int i = 0; i < flavors.length; i++) {
      if (DataFlavor.stringFlavor.equals(flavors[i])) {
        return true;
      }
    }
    return false;
  }
}
