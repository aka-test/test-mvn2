/**
 *
 */
package com.echoman.designer.components.echocommon;

import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListSelectionModel;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author david.morin
 */
public class CheckBoxSelectionModel extends CheckBoxListSelectionModel {

    private HashMap<Integer, Integer> indexes;
    private JTextField indexField;
    private HashMap<Integer, String> captions;
    private JTextField captionField;
    private CheckBoxList checkBoxList;

    /**
     * 
     */
    public class CheckBoxListener implements ListSelectionListener {

        /**
         * 
         * @param e
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // This must be done here and in the SQLTableVisualComponent listener
            // because it is possible for the user to click the checkbox before selecting
            // the item in the list and here it is incorrect, but in the SQLTableVisualComponent
            // the selected index is correct at that point.  Also, it is possible for
            // the user to check and uncheck the checkbox without changing the selected item
            // in which case this event fires but the SQLTableVisualComponent listener does not
            // so it must be handled here.
            String caption = captionExists(checkBoxList.getSelectedIndex());
            int index = indexExists(checkBoxList.getSelectedIndex());
            if (getLeadSelectionIndex() == checkBoxList.getSelectedIndex()) {
                if (checkBoxList.getCheckBoxListSelectionModel().isSelectedIndex(checkBoxList.getSelectedIndex())) {
                    String column = ((String) checkBoxList.getSelectedValue());
                    column = column.substring(column.indexOf(".") + 1);
                    int maxindex = getMaxIndex();

                    if (!(caption.equals(""))) {
                        captionField.setText(caption);
                    } else {
                        addCaption(checkBoxList.getSelectedIndex(), "");
                        captionField.setText(column);
                    }
                    if (!(index == -1)) {
                        indexField.setText(Integer.toString(index));
                    } else {
                        addIndex(checkBoxList.getSelectedIndex(), maxindex);
                        indexField.setText(Integer.toString(maxindex));
                    }
                    captionField.setEnabled(true);
                    indexField.setEnabled(true);
                } else {
                    if (!(caption.equals(""))) {
                        removeCaption(checkBoxList.getSelectedIndex());
                        captionField.setText("");
                    } else {
                        captionField.setText("");
                    }
                    if (!(index == -1)) {
                        removeIndex(checkBoxList.getSelectedIndex());
                        indexField.setText("");
                    } else {
                        indexField.setText("");
                    }
                    captionField.setEnabled(false);
                    indexField.setEnabled(false);
                }
            } else {
                //Ticket #400
                if (!e.getValueIsAdjusting()) {
                    checkBoxList.setSelectedIndex(getLeadSelectionIndex());
                }
            }
        }
    }

    /**
     * 
     * @param row
     * @return
     */
    public String captionExists(Integer row) {
        if (captions.containsKey(row)) {
            return captions.get(row);
        } else {
            return "";
        }
    }

    /**
     * 
     * @return
     */
    public Integer getMaxIndex() {
        if (!(indexes.isEmpty())) {
            return Collections.max(indexes.values()) + 1;
        } else {
            return 1;
        }
    }

    /**
     * 
     * @param row
     * @param text
     */
    public void setCaption(Integer row, String text) {
        captions.put(row, text);
    }

    /**
     * 
     * @param row
     * @param text
     */
    public void addCaption(Integer row, String text) {
        captions.put(row, text);
    }

    /**
     * 
     * @param row
     */
    public void removeCaption(Integer row) {
        captions.remove(row);
    }

    /**
     * 
     * @param row
     * @return
     */
    public Integer indexExists(Integer row) {
        if (indexes.containsKey(row)) {
            return indexes.get(row);
        } else {
            return -1;
        }
    }

    /**
     * 
     * @param row
     * @param index
     */
    public void setIndex(Integer row, Integer index) {
        indexes.put(row, index);
    }

    /**
     * 
     * @param row
     * @param index
     */
    public void addIndex(Integer row, Integer index) {
        indexes.put(row, index);
    }

    /**
     * 
     * @param row
     */
    public void removeIndex(Integer row) {
        indexes.remove(row);
    }

    /**
     * 
     * @param checkBoxList
     * @param captionField
     * @param indexField
     */
    public CheckBoxSelectionModel(CheckBoxList checkBoxList, JTextField captionField, JTextField indexField) {
        captions = new HashMap<Integer, String>();
        this.captionField = captionField;
        indexes = new HashMap<Integer, Integer>();
        this.indexField = indexField;
        this.checkBoxList = checkBoxList;
        addListSelectionListener(new CheckBoxListener());
    }
}
