/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Hendra
 */
public class FilenamePropertyEditor extends PropertyEditorSupport {

    /**
     *
     * @return
     */
    @Override
    public Component getCustomEditor() {
        JFileChooser fc = new JFileChooser() {

            @Override
            public void setSelectedFile(File file) {
                super.setSelectedFile(file);
                if (file != null) {
                    if (file.isFile())
                        FilenamePropertyEditor.this.setValue(file.getAbsolutePath());
                }
            }

        };
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        fc.setControlButtonsAreShown(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "bmp", "gif", "jpg", "png");
        fc.setFileFilter(filter);
        return fc;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAsText() {
        return (String) getValue();
    }

    /**
     *
     * @param s
     */
    @Override
    public void setAsText(String s) {
        setValue(s);
    }
}
