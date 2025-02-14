/**
 *
 */
package com.echoman.designer.components.echocommon;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author david.morin
 */
class TableTransferHandler extends StringTransferHandler {
  private int[] rows = null;

  private int addIndex = -1; //Location where items were added

  private int addCount = 0; //Number of items added.

  /**
   * 
   * @param c
   * @return
   */
    @Override
  protected String exportString(JComponent c) {
    JTable table = (JTable) c;
    rows = table.getSelectedRows();
    int colCount = table.getColumnCount();

    StringBuffer buff = new StringBuffer();

    for (int i = 0; i < rows.length; i++) {
      for (int j = 0; j < colCount; j++) {
        Object val = table.getValueAt(rows[i], j);
        buff.append(val == null ? "" : val.toString());
        if (j != colCount - 1) {
          buff.append(",");
        }
      }
      if (i != rows.length - 1) {
        buff.append("\n");
      }
    }

    return buff.toString();
  }

  /**
   * 
   * @param c
   * @param str
   */
    @Override
  protected void importString(JComponent c, String str) {
    JTable target = (JTable) c;
    DefaultTableModel model = (DefaultTableModel) target.getModel();
    int index = target.getSelectedRow();

    //Prevent the user from dropping data back on itself.
    //For example, if the user is moving rows #4,#5,#6 and #7 and
    //attempts to insert the rows after row #5, this would
    //be problematic when removing the original rows.
    //So this is not allowed.
    if (rows != null && index >= rows[0] - 1
        && index <= rows[rows.length - 1]) {
      rows = null;
      return;
    }

    int max = model.getRowCount();
    if (index < 0) {
      index = max;
    } else {
      index++;
      if (index > max) {
        index = max;
      }
    }
    addIndex = index;
    String[] values = str.split("\n");
    addCount = values.length;
    int colCount = target.getColumnCount();
    for (int i = 0; i < values.length && i < colCount; i++) {
      model.insertRow(index++, values[i].split(","));
    }
  }

  /**
   * 
   * @param c
   * @param remove
   */
    @Override
  protected void cleanup(JComponent c, boolean remove) {
    JTable source = (JTable) c;
    if (remove && rows != null) {
      DefaultTableModel model = (DefaultTableModel) source.getModel();

      //If we are moving items around in the same table, we
      //need to adjust the rows accordingly, since those
      //after the insertion point have moved.
      if (addCount > 0) {
        for (int i = 0; i < rows.length; i++) {
          if (rows[i] > addIndex) {
            rows[i] += addCount;
          }
        }
      }
      for (int i = rows.length - 1; i >= 0; i--) {
        model.removeRow(rows[i]);
      }
    }
    rows = null;
    addCount = 0;
    addIndex = -1;
  }
}

