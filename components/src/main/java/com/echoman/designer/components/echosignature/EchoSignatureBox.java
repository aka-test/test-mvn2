/**
 *
 */
package com.echoman.designer.components.echosignature;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;

/**
 *
 * @author Dave Athlon
 */
public final class EchoSignatureBox extends JLabel implements MouseListener, ActionListener, IEchoComponent {
    private static final String SIGNED_DATE = "Date Signed";
    private static final String TYPE_FULL_NAME = "Type full name";
    /**
     * Holds the node data instance.
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private EchoSignatureBoxNodeData nodeData;
    private transient JPanel btnBox = new JPanel();
    private transient JPanel typedNameBox = new JPanel();
    private transient EchoSignatureBoxNode node;
    private transient JPanel dropPanel;
    private transient JPopupMenu popup;

    private transient static final String script = "Script";
    private transient static final String CHANGE = "Change";
    private transient static final String CLEAR = "Clear";
    private transient static final String VERIFY = "Verify";
    private transient static final String PASSWORD = "Password";
    private transient static final String SIGNATURE_BOX_NAME = "SignatureBox";
    private transient static final String PWD = "Pwd ";
    private transient static final String SIGN = "Sign";
    
    // Possible future use    
    //private transient static final String DEFAULT_CONTAINER = "Default";
    //private transient static final String EMPTY_STRING = "";
    
    // Toggle between the two signature types, script and password.
    public void setSignatureType(String signatureType) {
        if (signatureType.equalsIgnoreCase(script)) {
            for (Component comp : btnBox.getComponents()) {
                if ((comp instanceof JLabel) || (comp instanceof JTextField)) {
                    comp.setVisible(false);
                } else if ((comp instanceof JButton) && ((JButton)comp).getText().equalsIgnoreCase(CHANGE)) {
                    ((JButton)comp).setText(CLEAR);
                }
            }
        } else if (signatureType.equalsIgnoreCase(PASSWORD)) {
            for (Component comp : btnBox.getComponents()) {
                if ((comp instanceof JLabel) || (comp instanceof JTextField)) {
                    comp.setVisible(true);
                } else if ((comp instanceof JButton) && ((JButton)comp).getText().equalsIgnoreCase(CLEAR)) {
                    ((JButton)comp).setText(CHANGE);
                }
            }
        }
    }
    
    /**
     * 
     * @param node
     * @param index
     * @param glassPane
     * @param dropPanel
     */
    public EchoSignatureBox(EchoSignatureBoxNodeData nodeData, int index, JPanel dropPanel) {
        super();
        this.nodeData = nodeData;
        this.dropPanel = dropPanel;
        setBackground(Color.white);
        setBorder(null);
        setBorder(BorderFactory.createLineBorder(Color.lightGray));
        setFocusable(true);
        setForeground(new Color(102, 102, 102));
        setSize(312,104);
        
        setName(SIGNATURE_BOX_NAME);
        addMouseListener(this);
        new Draggable(this, dropPanel);
        new Resizeable(this);
        createPopupMenu();
        
        buildSignatureBox();
    }
    
    /**
     * 
     */
    public void createPopupMenu() {
        popup = new JPopupMenu();
        nodeData.createPopupMenu(popup, this);

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setBorder(BorderFactory.createLineBorder(Color.red, 1));
        node.addSelectedNode(e.isControlDown());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoSignatureBoxNode)node;
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

    private void buildSignatureBox() {
        setLayout(new BorderLayout());
        buildTypedNameBox();
        buildSignBox();
        buildButtonBox();
        buildPassword();
        buildSignButton();
        buildClearButton();
        buildVerifyButton();
        add(btnBox, BorderLayout.PAGE_END);
        validate();
    }

    private void buildTypedNameBox() {
        typedNameBox.setSize(260, 33);
        typedNameBox.setBorder(BorderFactory.createEmptyBorder(4, 0, 5, 0));
        
        JTextField typedName = buildTypedNameField();
        JLabel typedNameLabel = buildTypedNameLabel();
        JLabel dateLabel = new JLabel(SIGNED_DATE);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 13));

        typedNameBox.setLayout(new BoxLayout(typedNameBox, BoxLayout.X_AXIS));
        typedNameBox.setBackground(new Color(204,221,238));
        typedNameBox.add(typedName);
        typedNameBox.add(typedNameLabel);
        typedNameBox.add(dateLabel);
        
        add(typedNameBox, BorderLayout.NORTH);
    }
    
    private void buildSignBox() {
        JPanel signBox = new JPanel();
        signBox.setSize(260, 43);
        // Light Steel Blue
        signBox.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222)));
        signBox.setBackground(Color.white);
        add(signBox, BorderLayout.CENTER);
    }

    private void buildButtonBox() {
        btnBox.setSize(260, 33);
        btnBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 4, 1));
        btnBox.setLayout(new BoxLayout(btnBox, BoxLayout.X_AXIS));
        btnBox.setBackground(new Color(204,221,238));
        // this keeps the buttons over to the right when the 
        // password text field does not exist.
        btnBox.add(Box.createHorizontalGlue());
    }

    private void buildPassword() {
        JTextField pwd = new JTextField();
        pwd.setFocusable(false);
        pwd.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222)));
        pwd.setVisible(false);
        
        JLabel pwdLabel = new JLabel(PWD);
        pwdLabel.setForeground(new Color(3368601));
        pwdLabel.setVisible(false);
        
        btnBox.add(pwdLabel);
        btnBox.add(pwd);
        btnBox.add(Box.createRigidArea(new Dimension(5,0)));
    }

    private void buildSignButton() {
        JButton sign = new JButton(SIGN);
        sign.setVisible(true);
        sign.setContentAreaFilled(false);
        sign.setOpaque(true);
        // Echo Button blue
        sign.setBackground(new Color(3368601));
        sign.setForeground(Color.white);
        sign.setMargin(new Insets(3,0,3,0));
        sign.setPreferredSize(new Dimension(30,23));
        btnBox.add(sign);
    }

    private void buildClearButton() {
        btnBox.add(Box.createRigidArea(new Dimension(5,0)));

        JButton clear = new JButton(CLEAR);
        clear.setVisible(true);
        clear.setContentAreaFilled(false);
        clear.setOpaque(true);
        clear.setBackground(new Color(3368601));
        clear.setForeground(Color.white);
        clear.setMargin(new Insets(3,0,3,0));
        clear.setPreferredSize(new Dimension(45,23));
        btnBox.add(clear);
    }

    private void buildVerifyButton() {
        btnBox.add(Box.createRigidArea(new Dimension(5,0)));

        JButton verify = new JButton(VERIFY);
        verify.setVisible(true);
        verify.setContentAreaFilled(false);
        verify.setOpaque(true);
        verify.setBackground(new Color(3368601));
        verify.setForeground(Color.white);
        verify.setMargin(new Insets(3,0,3,0));
        verify.setPreferredSize(new Dimension(40,23));
        btnBox.add(verify);
    }

    private JTextField buildTypedNameField() {
        JTextField typedName = new JTextField();
        typedName.setFocusable(false);
        typedName.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222)));
        typedName.setForeground(Color.lightGray);
        Font font = typedName.getFont();
        font = font.deriveFont(
            Collections.singletonMap(
                TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
        typedName.setFont(font);
        typedName.setText(TYPE_FULL_NAME);
        return typedName;
    }

    private JLabel buildTypedNameLabel() {
        JLabel typedNameLabel = new JLabel();
        typedNameLabel.setPreferredSize(new Dimension(21, 23));
        typedNameLabel.setForeground(new Color(3368601));
        return typedNameLabel;
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
    public void setNodeData(EchoSignatureBoxNodeData node) {
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
            if (nodeData.isTopLocked() ) {
                ly = nodeData.getTop();
            }
            if (nodeData.isLeftLocked() ) {
                lx = nodeData.getLeft();
            }
        }
        super.setLocation(lx, ly);
        nodeData.setLocationFromEdit(lx, ly);

        // Possible future use
        //checkContainers();
        validate();
    }

// Possible future use    
//    private void checkContainers() {
//        if (!nodeData.getLoadingForm()) {
//            DataContainerManager.checkContainerComponents(nodeData,nodeData.getDesignerPage().getCompList());
//            if (nodeData.getTable().equals(EMPTY_STRING)) {
//                nodeData.setTable(DEFAULT_CONTAINER, JDesiWindowManager.getActiveDesignerPage().getTable());
//            }
//        }
//    }

    /**
     * 
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
        int w = width;
        int h = height;
        //Ticket #182
        if (!EchoUtil.isRunningAsEchoAdmin()) {
            if (nodeData.isHeightLocked() ) {
                h = nodeData.getHeight();
            }
            if (nodeData.isWidthLocked() ) {
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
    public void actionPerformed(ActionEvent e) {
        if (!node.handleAction(((JMenuItem) (e.getSource())).getText()))
            JOptionPane.showMessageDialog(null, "Not implemented.");
    }
}
