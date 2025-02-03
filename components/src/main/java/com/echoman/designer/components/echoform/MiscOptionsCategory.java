/**
 *
 */
package com.echoman.designer.components.echoform;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * 
 * @author david.morin
 */
public final class MiscOptionsCategory extends OptionsCategory {

    /**
     * 
     * @return
     */
    @Override
    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.loadImage("com/echoman/designer/components/echoform/table_32.png"));
    }

    /**
     * 
     * @return
     */
    @Override
    public String getCategoryName() {
        return NbBundle.getMessage(MiscOptionsCategory.class, "MiscOptionsCategory_Name_Designer");
    }

    /**
     * 
     * @return
     */
    @Override
    public String getTitle() {
        return NbBundle.getMessage(MiscOptionsCategory.class, "MiscOptionsCategory_Title_Designer");
    }

    /**
     * 
     * @return
     */
    @Override
    public OptionsPanelController create() {
        return new MiscOptionsPanelController();
    }
}
