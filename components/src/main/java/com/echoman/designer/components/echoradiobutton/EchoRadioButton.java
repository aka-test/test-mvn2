/**
 *
 */
package com.echoman.designer.components.echoradiobutton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import org.jdesktop.swingx.HorizontalLayout;
import org.jdesktop.swingx.VerticalLayout;

/**
 *
 * @author Dave Athlon
 */
public class EchoRadioButton extends JPanel implements MouseListener, ActionListener, IEchoComponent {
    //JRadioButton implements MouseListener, ActionListener, IEchoComponent {

    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoRadioButtonNodeData nodeData;
    private transient Font font = new Font("Open Sans Semibold", Font.BOLD, 14);
    private transient Color fontColor = new Color(1, 85, 149);
    private transient EchoRadioButtonNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;
    private transient JPanel btnPanel = new JPanel();
    // Can't store the radio group because it holds this radio button and
    // will be duplicated on the load (resolve).
    private transient ButtonGroup grp = new ButtonGroup();
    private transient int alignment = SwingConstants.LEFT;
    private transient String buttonLayout = "Vertical";

    /**
     * 
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoRadioButton(EchoRadioButtonNodeData nodeData, int index, JPanel dropPanel) {
        super();
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        setOpaque(false);
        setFont(new Font("Open Sans Semibold", Font.BOLD, 14));
        setForeground(new Color(1, 85, 149));
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);
        //Ticket #438
        //setName("RadioButton" + index);
        setName("RadioButton");
        setSize(120, 96);
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
        setLayout(new GridBagLayout());
        btnPanel.setLayout(new VerticalLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
	c.anchor = GridBagConstraints.WEST;
        c.weightx = 1;
	c.gridx = 0;
        c.gridy = 0;
        add(btnPanel, c);
    }

    public String getChangeEvent() {
        return nodeData.getChangeEvent();
    }

    public String getDeleteEvent() {
        return nodeData.getDeleteEvent();
    }

    public String getInsertEvent() {
        return nodeData.getInsertEvent();
    }

    public String getSaveEvent() {
        return nodeData.getSaveEvent();
    }

    public String getScrollEvent() {
        return nodeData.getScrollEvent();
    }

    /**
     * Must do this to adjust for labels that are larger than the text because
     * html does not include the leading blank space which moves it to the left.
     * @return
     */
    public Point getHtmlLocation() {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        Rectangle2D rect = fm.getStringBounds("", this.getGraphics());
        // Have to add in for the radiobutton icon here.
        Point htmlLoc = new Point((int) ((getLocation().x + getWidth()) - rect.getWidth() - 15), (int) getLocation().getY());
        return htmlLoc;
    }

    /**
     * 
     */
    public final void createPopupMenu() {
        popup = new JPopupMenu();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoRadioButtonNode) node;
    }

    @Override
    public void remove() {
        node.setIsDestroying(true);
        dropPanel.remove(this);
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    /**
     * 
     */
    class PopupListener extends MouseAdapter {

        /**
         * 
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        /**
         * 
         * @param e
         */
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }

    /**
     * 
     * @param dropPanel
     */
    @Override
    public void setDropPanel(JPanel dropPanel) {
        this.dropPanel = dropPanel;
    }

    /**
     * 
     * @param node
     */
    public void setNodeData(EchoRadioButtonNodeData node) {
        nodeData = node;
    }

    /**
     * 
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
    }

    /**
     * 
     * @param x
     * @param y
     */
    @Override
    public void setLocation(int x, int y) {
        int lx = x;
        int ly = y;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isTopLocked()) {
                ly = nodeData.getTop();
            }
            if (nodeData.isLeftLocked()) {
                lx = nodeData.getLeft();
            }
        }
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);

        checkContainers();
    }

    private void checkContainers() {
        if (!nodeData.getLoadingForm()) {
            DataContainerManager.checkContainerComponents(nodeData, nodeData.getDesignerPage().getCompList());
            if (nodeData.getTable().equals("")) {
                nodeData.setTable("Default", JDesiWindowManager.getActiveDesignerPage().getTable());
            }
        }
    }

    /**
     * 
     * @param width
     * @param height
     */
    @Override
    public final void setSize(int width, int height) {
        int w = width;
        int h = height;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isHeightLocked()) {
                h = nodeData.getHeight();
            }
            if (nodeData.isWidthLocked()) {
                w = nodeData.getWidth();
            }
        }
        super.setSize(w, h);
        nodeData.setSizeFromEdit(w, h);
        validate();
    }

    /**
     * 
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        //this.setSelected(false);
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.red, 1));
        node.addSelectedNode(e.isControlDown());
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!node.handleAction(((JMenuItem) (e.getSource())).getText())) {
            JOptionPane.showMessageDialog(null, "Not implemented.");
        }
    }

    public void removeRadioButtons() {
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            grp.remove(b);
            btnPanel.remove(c);
        }
        validate();
    }

    public String getSelectedRadioButtonCaption() {
        String caption = "";
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            if (b.isSelected()) {
                caption = b.getText();
            }
        }
        return caption;
    }

    public void resetButtons() {
        final List<String> buttons = Arrays.stream(btnPanel.getComponents())
                .map(component -> ((JRadioButton) component).getText())
                .collect(Collectors.toList());
        btnPanel.removeAll();
        btnPanel.setLayout(createLayout());
        buttons.stream().forEach(this::addRadioButton);
    }

    public void selectedRadioButton(String caption) {
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            b.setSelected(caption.equals(b.getText()));
        }
    }

    private LayoutManager createLayout() {
        if ("Vertical".equals(buttonLayout)) {
            return new VerticalLayout();
        } else {
            return new HorizontalLayout();
        }
    }

    public void initGroup() {
        if (grp == null) {
            grp = new ButtonGroup();
        }
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            grp.add(b);
        }
    }

    public void setRadioButtonForeground(Color color) {
        this.fontColor = color;
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            b.setForeground(color);
        }
    }

    public void setRadioButtonFont(Font font) {
        this.font = font;
        for (Component c : btnPanel.getComponents()) {
            JRadioButton b = (JRadioButton) c;
            b.setFont(this.font);
        }
    }

    public void addRadioButton(final String caption) {
        if (!"".equals(caption.trim())) {
            final JRadioButton btn = new JRadioButton(caption);
            setButtonAlignment(btn, alignment);
            btn.setFont(font);
            btn.setForeground(fontColor);
            btn.setFocusable(false);
            btn.setOpaque(false);
            //Ticket #472
            btn.addMouseListener(this);
            if (getWidth() < btn.getWidth()) {
                setSize(btn.getWidth() + 5, getHeight());
            }
            grp.add(btn);
            btnPanel.add(btn);
            btn.setEnabled(false);
            this.validate();
        }
    }
   
    private void setButtonAlignment(JRadioButton btn, int alignment) {
        if (alignment == SwingConstants.LEFT) {
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        } else {
            btn.setHorizontalAlignment(SwingConstants.RIGHT);
            btn.setHorizontalTextPosition(SwingConstants.LEFT);
        }
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
        for (Component c : btnPanel.getComponents()) {
            setButtonAlignment((JRadioButton) c, alignment);
        }
    }

    public String getButtonLayout() {
        return buttonLayout;
    }

    public void setButtonLayout(String buttonLayout) {
        if (!this.buttonLayout.equals(buttonLayout)) {
            this.buttonLayout = buttonLayout;
            List<String> btns = new ArrayList<>();
            for (Component c : btnPanel.getComponents()) {
                btns.add(((JRadioButton) c).getText());
            }
            btnPanel.removeAll();
            btnPanel.setLayout(createLayout());
            for (String caption : btns) {
                addRadioButton(caption);
            }
        }
    }
}
