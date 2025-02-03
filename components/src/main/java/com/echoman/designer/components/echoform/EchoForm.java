/**
 *
 */
package com.echoman.designer.components.echoform;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.UndoableEditEvent;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echoborder.EchoBorderNodeData;
import com.echoman.designer.components.echobutton.EchoButtonNodeData;
import com.echoman.designer.components.echocheckbox.EchoCheckboxNodeData;
import com.echoman.designer.components.echocommon.EchoUndoableEdit;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echocommon.GhostGlassPane;
import com.echoman.designer.components.echocommon.TabOrdersForm;
import com.echoman.designer.components.echodatacontainer.DashedBorder;
import com.echoman.designer.components.echodatacontainer.EchoDataContainerNodeData;
import com.echoman.designer.components.echoimage.EchoImageNodeData;
import com.echoman.designer.components.echolabel.EchoLabelNodeData;
import com.echoman.designer.components.echomemofield.EchoMemoFieldNodeData;
import com.echoman.designer.components.echoradiobutton.EchoRadioButtonNodeData;
import com.echoman.designer.components.echosignature.EchoSignatureBoxNodeData;
import com.echoman.designer.components.echotable.EchoTableNodeData;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import com.echoman.designer.palette.PalItemData;
import com.echoman.designer.palette.PaletteSupport;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Dave Athlon
 */
public class EchoForm extends JPanel implements MouseMotionListener, MouseListener,
        ActionListener, IEchoComponent, Scrollable {
    // The form itself is never persisted in the .form file.  It is created
    // each time the nodedata object is read back in.

    /**
     * Holds the node data instance.
     *
     * @see #setNodeData(EchoBorderNodeData node)
     */
    private transient EchoFormNodeData nodeData;
    private transient EchoFormNode node;
    private transient JPopupMenu popup;
    private transient GhostGlassPane glassPane;
    private transient List<IEchoComponentNodeData> compList;
    private transient Rectangle[][] grid;
    private transient int gridSpace;
    private transient Point mousePoint;
    
    // CDT-561
    private transient Image draggedImage;
    private transient Point draggedLocation;
    
    private String id = "";
    private boolean ignoreMouseEvent;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private TitledBorder tb = new TitledBorder(new DashedBorder(Color.green, 0, false), "", TitledBorder.CENTER, TitledBorder.BELOW_TOP, Font.getFont("Arial"), Color.GRAY);
    private TitledBorder selectedTB = new TitledBorder(new DashedBorder(Color.red, true), "", TitledBorder.CENTER, TitledBorder.BELOW_TOP, Font.getFont("Arial"), Color.red);

    public TitledBorder getSelectedTB() {
        return selectedTB;
    }

    public TitledBorder getTb() {
        return tb;
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        
        // CDT-561
        if (draggedImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2.drawImage(draggedImage, (int)draggedLocation.getX(), (int)draggedLocation.getY(), 
                    draggedImage.getWidth(null), draggedImage.getHeight(null), this);
            g2.dispose();
        }
    }

    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (NbPreferences.forModule(MiscPanel.class).getBoolean("displayGrid", true)) {
            g.setColor(new Color(230, 230, 230));
            // Vertical lines.
            for (int x = 0; x < Math.round(getWidth() / gridSpace); x++) {
                g.drawLine(x * gridSpace, 0, x * gridSpace, getHeight());
            }
            // Horizontal lines.
            for (int y = 0; y < Math.round(getHeight() / gridSpace); y++) {
                g.drawLine(0, y * gridSpace, getWidth(), y * gridSpace);
            }
        }
   }

    /**
     *
     * @return
     */
    public int getGridSpace() {
        return this.gridSpace;
    }

    /**
     *
     * @param gridSpace
     */
    public void setGridSpace(int gridSpace) {
        this.gridSpace = gridSpace;
        calculateGrid();
    }

    //Ticket #235
    private void calculateGrid() {
        int x = Math.round(getWidth() / gridSpace);
        int y = Math.round(getHeight() / gridSpace);
        grid = null;
        grid = new Rectangle[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                grid[i][j] = new Rectangle(i * gridSpace, j * gridSpace, gridSpace, gridSpace);
            }
        }
        nodeData.getDesignerPage().setGrid(null);
        nodeData.getDesignerPage().setGrid(grid);
        invalidate();
        repaint();
    }

    /**
     *
     * @param node
     * @param index
     * @param glassPane
     * @param compList
     * @param fromLoad
     * @param width
     * @param height
     */
    public EchoForm(EchoFormNodeData nodeData, int index, boolean fromLoad, String id,
            int width, int height) {
        this(nodeData, index, fromLoad, id);
        setSize(new Dimension(width, height));

    }

    /**
     *
     * @param node
     * @param index
     * @param glassPane
     * @param compList
     * @param fromLoad
     */
    public EchoForm(EchoFormNodeData nodeData, int index, boolean fromLoad, String id) {
        super();
        gridSpace = Integer.valueOf(NbPreferences.forModule(MiscPanel.class).get("gridSpace", "26"));
        this.nodeData = nodeData;
        this.glassPane = (GhostGlassPane) ((JFrame) WindowManager.getDefault().getMainWindow()).getGlassPane();
        this.compList = nodeData.getDesignerPage().getCompList();
        setOpaque(true);
        setBorder(tb);
        this.id = id;
        nodeData.getDesignerPage().setDropPanel(id, this);

        //setPreferredSize(new Dimension(920, 540));
        setPreferredSize(new Dimension(900, 535));
        setMinimumSize(new Dimension(200, 200));
        if (!fromLoad) {
            //setSize(920, 540);
            setSize(900, 535);
        }
        setLayout(null);

        int x = Math.round(1024 / gridSpace);
        int y = Math.round(768 / gridSpace);

        grid = new Rectangle[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                grid[i][j] = new Rectangle(i * gridSpace, j * gridSpace, gridSpace, gridSpace);
            }
        }

        nodeData.getDesignerPage().setGrid(grid);

        setBackground(Color.WHITE);
        //setName(nodeData.getFormName());
        addMouseListener(this);
        // Ticket #12 Cannot resize with drag/drop with a layout and
        // we must have a layout to have scrollbars show automatically.
        //new Resizeable(this);
        createPopupMenu();

        addMouseMotionListener(this);

        setDropTarget(new DropTarget(this, new DropTargetListener() {
            /**
             *
             */
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                doDragEnter(dtde);
            }

            /**
             *
             */
            @Override
            public void dragExit(DropTargetEvent dte) {
                draggedImage = null;
                repaint(0, 0, getWidth(), getHeight());
            }

            /**
             *
             */
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                doDragUpdate(dtde);
            }

            /**
             *
             */
            @Override
            public void drop(DropTargetDropEvent dtde) {
                doDrop(dtde);
            }

            /**
             *
             */
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }
        }));

    }

    /**
     *
     * @param dtde
     */
    private void doDragEnter(DropTargetDragEvent dtde) {
        PalItemData data = null;
        try {
            data = (PalItemData) dtde.getTransferable().getTransferData(PaletteSupport.MY_DATA_FLAVOR);
        } catch (IOException ex) {
            Logger.getLogger(EchoForm.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (UnsupportedFlavorException ex) {
            dtde.rejectDrag();
            return;
        }
        if ((!(data == null))
                && ((data.getId().equals("id1"))
                || (data.getId().equals("id2"))
                || (data.getId().equals("id3"))
                || (data.getId().equals("id4"))
                || (data.getId().equals("id5"))
                || (data.getId().equals("id6"))
                || (data.getId().equals("id7"))
                || (data.getId().equals("id8"))
                || (data.getId().equals("id9"))
                || (data.getId().equals("id10"))
                || (data.getId().equals("id11")))) {
            draggedImage = data.getDragIcon();
            draggedLocation = new Point((int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
        }
    }

    private void doDragUpdate(DropTargetDragEvent dtde) {
        if (draggedImage != null) {
            int newX = (int)dtde.getLocation().getX();
            int newY = (int)dtde.getLocation().getY();
            int oldX = draggedLocation.x;
            int oldY = draggedLocation.y;
            int useX = newX - oldX < 0 ? newX : oldX;
            int useY = newY - oldY < 0 ? newY : oldY;
            int useWidth = abs(oldX - newX) + draggedImage.getWidth(null);
            int useHeight = abs(oldY - newY) + draggedImage.getHeight(null);
            draggedLocation = new Point((int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            repaint(useX, useY, useWidth, useHeight);
        }
    }

    /**
     *
     * @param dtde
     */
    private void doDrop(DropTargetDropEvent dtde) {
        try {
            if (!dtde.isDataFlavorSupported(PaletteSupport.MY_DATA_FLAVOR)) {
                dtde.rejectDrop();
                return;
            }
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            PalItemData data = null;
            try {
                data = (PalItemData) dtde.getTransferable().getTransferData(PaletteSupport.MY_DATA_FLAVOR);
            } catch (IOException ex) {
                Logger.getLogger(EchoForm.class.getName()).log(Level.SEVERE, ex.getMessage());
            } catch (UnsupportedFlavorException ex) {
                dtde.rejectDrop();
                return;
            }
            dtde.dropComplete(null != data);
            if (null != data) {
                createEchoComponent(dtde.getLocation().x, dtde.getLocation().y, data.getId());
                PaletteSupport.setSelectedPaletteItem(null, null);
            }
        } finally {
            draggedImage = null;
        }
    }

    //Ticket #227
    private void createEchoComponent(int locationX, int locationY, String componentId) {
        String snap = "on";
        if (!NbPreferences.forModule(MiscPanel.class).getBoolean("snapOn", true)) {
            snap = "off";
        }

        int width = 0;
        int height = 0;
        int offset = 0;
        if (componentId.equals("id1")) {        //data field
            width = 110;
            height = 20;
            offset = 20;
        } else if (componentId.equals("id2")) { //label
            width = 72;
            height = 20;
        } else if (componentId.equals("id3")) { //grid
            width = 216;
            height = 72;
        } else if (componentId.equals("id4")) { //button
            width = 48;
            height = 24;
        } else if (componentId.equals("id5")) { //border
            width = 96;
            height = 48;
        } else if (componentId.equals("id6")) { //checkbox
            width = 120;
            height = 20;
        } else if (componentId.equals("id7")) { //memo
            width = 312;
            height = 96;
        } else if (componentId.equals("id8")) { //radio button
            width = 120;
            height = 96;
        } else if (componentId.equals("id9")) { //image
            width = 96;
            height = 96;
        } else if (componentId.equals("id10")) { //data container
            width = 96;
            height = 48;
        } else if (componentId.equals("id11")) { //signature box
            width = 286;
            height = 104;
        }

        Point newLoc = new Point(locationX, locationY + offset);

        if (snap.equals("on")) {
            newLoc = EchoUtil.locateComponentInGrid(grid, newLoc, width, height);
        }

        if (componentId.equals("id1")) {
            EchoTextFieldNodeData textFieldNodeData = new EchoTextFieldNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(textFieldNodeData, textFieldNodeData.getTextField(), newLoc, 0, true, false);
            textFieldNodeData.getTextField().getCaptionLabelNodeData().setParentId(id, false);
        } else if (componentId.equals("id2")) {
            EchoLabelNodeData labelNodeData = new EchoLabelNodeData(nodeData.getDesignerPage(), false, false, this);
            addDroppedComponent(labelNodeData, labelNodeData.getLabel(), newLoc, 0, true, false);
        } else if (componentId.equals("id3")) {
            EchoTableNodeData tableNodeData = new EchoTableNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(tableNodeData, tableNodeData.getTableComp(), newLoc, 0, true, false);
        } else if (componentId.equals("id4")) {
            EchoButtonNodeData buttonNodeData = new EchoButtonNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(buttonNodeData, buttonNodeData.getButton(), newLoc, 0, true, false);
        } else if (componentId.equals("id5")) {
            EchoBorderNodeData borderNodeData = new EchoBorderNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(borderNodeData, borderNodeData.getBorder(), newLoc, -1, true, false);
            borderNodeData.fixZOrder();
        } else if (componentId.equals("id6")) {
            EchoCheckboxNodeData checkboxNodeData = new EchoCheckboxNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(checkboxNodeData, checkboxNodeData.getCheckbox(), newLoc, 0, true, false);
        } else if (componentId.equals("id7")) {
            EchoMemoFieldNodeData memoFieldNodeData = new EchoMemoFieldNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(memoFieldNodeData, memoFieldNodeData.getMemoField(), newLoc, 0, true, false);
        } else if (componentId.equals("id8")) {
            EchoRadioButtonNodeData radioButtonNodeData = new EchoRadioButtonNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(radioButtonNodeData, radioButtonNodeData.getRadioButton(), newLoc, 0, true, false);
        } else if (componentId.equals("id9")) {
            EchoImageNodeData imageNodeData = new EchoImageNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(imageNodeData, imageNodeData.getEchoImage(), newLoc, 0, true, false);
        } else if (componentId.equals("id10")) {
            EchoDataContainerNodeData dataContainerNodeData = new EchoDataContainerNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(dataContainerNodeData, dataContainerNodeData.getDataContainer(), newLoc, -1, true, false);
            dataContainerNodeData.fixZOrder();
        } else if (componentId.equals("id11")) {
            EchoSignatureBoxNodeData signatureBoxNodeData = new EchoSignatureBoxNodeData(nodeData.getDesignerPage(), this);
            addDroppedComponent(signatureBoxNodeData, signatureBoxNodeData.getSignatureBox(), newLoc, 0, true, false);
        }
        //Ticket #188
        //set the top component to be the active component after component
        //is dropped on the form
        ((TopComponent) nodeData.getDesignerPage()).requestActive();
    }
   
    public void addComponent(IEchoComponentNodeData data) {
        if (data instanceof EchoTextFieldNodeData) {
            EchoTextFieldNodeData textFieldNodeData = (EchoTextFieldNodeData) data;
            textFieldNodeData.getTextField().setDropPanel(this);
            this.add(textFieldNodeData.getTextField());
        } else if (data instanceof EchoLabelNodeData) {
            EchoLabelNodeData labelNodeData = (EchoLabelNodeData) data;
            labelNodeData.getLabel().setDropPanel(this);
            this.add(labelNodeData.getLabel());
        } else if (data instanceof EchoTableNodeData) {
            EchoTableNodeData tableNodeData = (EchoTableNodeData) data;
            tableNodeData.getTableComp().setDropPanel(this);
            this.add(tableNodeData.getTableComp());
        } else if (data instanceof EchoButtonNodeData) {
            EchoButtonNodeData buttonNodeData = (EchoButtonNodeData) data;
            buttonNodeData.getButton().setDropPanel(this);
            this.add(buttonNodeData.getButton());
        } else if (data instanceof EchoBorderNodeData) {
            EchoBorderNodeData borderNodeData = (EchoBorderNodeData) data;
            borderNodeData.getBorder().setDropPanel(this);
            this.add(borderNodeData.getBorder());
            //Ticket #416 - Z order should already be set at this point no need 
            //to set again or it will messed up the component parent when
            //swapping tab
            //borderNodeData.fixZOrder();
        } else if (data instanceof EchoCheckboxNodeData) {
            EchoCheckboxNodeData checkboxNodeData = (EchoCheckboxNodeData) data;
            checkboxNodeData.getCheckbox().setDropPanel(this);
            this.add(checkboxNodeData.getCheckbox());
        } else if (data instanceof EchoMemoFieldNodeData) {
            EchoMemoFieldNodeData memoFieldNodeData = (EchoMemoFieldNodeData) data;
            memoFieldNodeData.getMemoField().setDropPanel(this);
            this.add(memoFieldNodeData.getMemoField());
        } else if (data instanceof EchoRadioButtonNodeData) {
            EchoRadioButtonNodeData radioButtonNodeData = (EchoRadioButtonNodeData) data;
            radioButtonNodeData.getRadioButton().setDropPanel(this);
            this.add(radioButtonNodeData.getRadioButton());
        } else if (data instanceof EchoImageNodeData) {
            EchoImageNodeData imageNodeData = (EchoImageNodeData) data;
            imageNodeData.getEchoImage().setDropPanel(this);
            this.add(imageNodeData.getEchoImage());
        } else if (data instanceof EchoDataContainerNodeData) {
            EchoDataContainerNodeData dataContainerNodeData = (EchoDataContainerNodeData) data;
            dataContainerNodeData.getDataContainer().setDropPanel(this);
            this.add(dataContainerNodeData.getDataContainer());
            //Ticket #416 - Z order should already be set at this point no need 
            //to set again or it will messed up the component parent when
            //swapping tab
            //dataContainerNodeData.fixZOrder();
        } else if (data instanceof EchoSignatureBoxNodeData) {
            EchoSignatureBoxNodeData signatureBoxNodeData = (EchoSignatureBoxNodeData) data;
            signatureBoxNodeData.getSignatureBox().setDropPanel(this);
            this.add(signatureBoxNodeData.getSignatureBox());
        }
    }

    //Ticket #150
    private void addUndoableHappened(EchoBaseNodeData data) {
        EchoUndoableEdit edit = new EchoUndoableEdit(data, EchoUndoableEdit.EDIT_TYPE_ADD);
        nodeData.getDesignerPage().getUndoManager().undoableEditHappened(new UndoableEditEvent(data, edit));
    }

    public EchoBaseNodeData createEchoComponent(EchoBaseNodeData data) {
        return createEchoComponent(id, data, false);
    }

    public EchoBaseNodeData createEchoComponent(String parentId, EchoBaseNodeData data, boolean undoable) {
        if (data instanceof EchoTextFieldNodeData) {
            EchoTextFieldNodeData nd = (EchoTextFieldNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoTextFieldNodeData textFieldNodeData = new EchoTextFieldNodeData(nodeData.getDesignerPage(), this, false);
            if (!undoable) {
                textFieldNodeData.setId(nd.getId());
            }
            textFieldNodeData.setParentId(parentId, false);
            addDroppedComponent(textFieldNodeData, textFieldNodeData.getTextField(), newLoc, 0, undoable, false);
            if (undoable) {
                textFieldNodeData.copy(nd, false);
            } else {
                textFieldNodeData.copy(nd);
            }
            textFieldNodeData.setTranslationLabelId(nd.getTranslationLabelId());
            textFieldNodeData.setCaptionLabelId(nd.getCaptionLabelId());
            textFieldNodeData.linkCaptionTranslationLabels();
            return textFieldNodeData;
        } else if (data instanceof EchoLabelNodeData) {
            EchoLabelNodeData nd = (EchoLabelNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoLabelNodeData labelNodeData = new EchoLabelNodeData(nodeData.getDesignerPage(), false, false, this);
            if (!undoable) {
                labelNodeData.setId(nd.getId());
            }
            labelNodeData.setParentId(parentId, false);
            addDroppedComponent(labelNodeData, labelNodeData.getLabel(), newLoc, 0, undoable, false);
            if (undoable) {
                labelNodeData.copy(nd, false);
            } else {
                labelNodeData.copy(nd);
            }
            return labelNodeData;
        } else if (data instanceof EchoTableNodeData) {
            EchoTableNodeData nd = (EchoTableNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoTableNodeData tableNodeData = new EchoTableNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                tableNodeData.setId(nd.getId());
            }
            tableNodeData.setParentId(parentId, false);
            addDroppedComponent(tableNodeData, tableNodeData.getTableComp(), newLoc, 0, undoable, false);
            if (undoable) {
                tableNodeData.copy(nd, false);
            } else {
                tableNodeData.copy(nd);
            }
            return tableNodeData;
        } else if (data instanceof EchoButtonNodeData) {
            EchoButtonNodeData nd = (EchoButtonNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoButtonNodeData buttonNodeData = new EchoButtonNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                buttonNodeData.setId(nd.getId());
            }
            buttonNodeData.setParentId(parentId, false);
            addDroppedComponent(buttonNodeData, buttonNodeData.getButton(), newLoc, 0, undoable, false);
            if (undoable) {
                buttonNodeData.copy(nd, false);
            } else {
                buttonNodeData.copy(nd);
            }
            return buttonNodeData;
        } else if (data instanceof EchoBorderNodeData) {
            EchoBorderNodeData nd = (EchoBorderNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoBorderNodeData borderNodeData = new EchoBorderNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                borderNodeData.setId(nd.getId());
            }
            borderNodeData.setParentId(parentId, false);
            addDroppedComponent(borderNodeData, borderNodeData.getBorder(), newLoc, nd.getZOrder(), undoable, false);
            borderNodeData.fixZOrder();
            if (undoable) {
                borderNodeData.copy(nd, false);
            } else {
                borderNodeData.copy(nd);
            }
            return borderNodeData;
        } else if (data instanceof EchoCheckboxNodeData) {
            EchoCheckboxNodeData nd = (EchoCheckboxNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoCheckboxNodeData checkboxNodeData = new EchoCheckboxNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                checkboxNodeData.setId(nd.getId());
            }
            checkboxNodeData.setParentId(parentId, false);
            addDroppedComponent(checkboxNodeData, checkboxNodeData.getCheckbox(), newLoc, 0, undoable, false);
            if (undoable) {
                checkboxNodeData.copy(nd, false);
            } else {
                checkboxNodeData.copy(nd);
            }
            return checkboxNodeData;
        } else if (data instanceof EchoMemoFieldNodeData) {
            EchoMemoFieldNodeData nd = (EchoMemoFieldNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoMemoFieldNodeData memoFieldNodeData = new EchoMemoFieldNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                memoFieldNodeData.setId(nd.getId());
            }
            memoFieldNodeData.setParentId(parentId, false);
            addDroppedComponent(memoFieldNodeData, memoFieldNodeData.getMemoField(), newLoc, 0, undoable, false);
            if (undoable) {
                memoFieldNodeData.copy(nd, false);
            } else {
                memoFieldNodeData.copy(nd);
            }
            return memoFieldNodeData;
        } else if (data instanceof EchoRadioButtonNodeData) {
            EchoRadioButtonNodeData nd = (EchoRadioButtonNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoRadioButtonNodeData radioButtonNodeData = new EchoRadioButtonNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                radioButtonNodeData.setId(nd.getId());
            }
            radioButtonNodeData.setParentId(parentId, false);
            addDroppedComponent(radioButtonNodeData, radioButtonNodeData.getRadioButton(), newLoc, 0, undoable, false);
            if (undoable) {
                radioButtonNodeData.copy(nd, false);
            } else {
                radioButtonNodeData.copy(nd);
            }
            return radioButtonNodeData;
        } else if (data instanceof EchoImageNodeData) {
            EchoImageNodeData nd = (EchoImageNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoImageNodeData imageNodeData = new EchoImageNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                imageNodeData.setId(nd.getId());
            }
            imageNodeData.setParentId(parentId, false);
            addDroppedComponent(imageNodeData, imageNodeData.getEchoImage(), newLoc, 0, undoable, false);
            if (undoable) {
                imageNodeData.copy(nd, false);
            } else {
                imageNodeData.copy(nd);
            }
            return imageNodeData;
        } else if (data instanceof EchoDataContainerNodeData) {
            EchoDataContainerNodeData nd = (EchoDataContainerNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoDataContainerNodeData dataContainerNodeData = new EchoDataContainerNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                dataContainerNodeData.setId(nd.getId());
            }
            dataContainerNodeData.setParentId(parentId, false);
            addDroppedComponent(dataContainerNodeData, dataContainerNodeData.getDataContainer(), newLoc, nd.getZOrder(), undoable, false);
            dataContainerNodeData.fixZOrder();
            if (undoable) {
                dataContainerNodeData.copy(nd, false);
            } else {
                dataContainerNodeData.copy(nd);
            }
            return dataContainerNodeData;
        } else if (data instanceof EchoSignatureBoxNodeData) {
            EchoSignatureBoxNodeData nd = (EchoSignatureBoxNodeData) data;
            Point newLoc = new Point(nd.getLeft(), nd.getTop());
            EchoSignatureBoxNodeData signatureBoxNodeData = new EchoSignatureBoxNodeData(nodeData.getDesignerPage(), this);
            if (!undoable) {
                signatureBoxNodeData.setId(nd.getId());
            }
            signatureBoxNodeData.setParentId(parentId, false);
            addDroppedComponent(signatureBoxNodeData, signatureBoxNodeData.getSignatureBox(), newLoc, 0, undoable, false);
            if (undoable) {
                signatureBoxNodeData.copy(nd, false);
            } else {
                signatureBoxNodeData.copy(nd);
            }
            return signatureBoxNodeData;
        }
        return null;
    }

    public void addDroppedComponent(IEchoComponentNodeData data, JComponent comp, Point newLoc, int zOrder, boolean undoable, boolean windesiImport) {
        data.setParentId(id, windesiImport);
        compList.add(data);
        comp.setLocation(newLoc);
        add(comp);
        int useZOrder = zOrder;
        if (zOrder == -1) {
            useZOrder = getComponentZOrder(comp);
        }
        try {
            setComponentZOrder(comp, useZOrder);
        } catch (Exception e) {
            // Copy/paste might cause exception with invalid z-order
            // get a new z-order for the copied component
            useZOrder = getComponentZOrder(comp);
            try {
                setComponentZOrder(comp, useZOrder);
            } catch (Exception ex) {
            }
        }
        Rectangle visRect = getVisibleRect();
        paintImmediately(visRect.x, visRect.y, visRect.width, visRect.height);
        // MOVED UP so that component exists in compList before
        // properties are set
        //compList.add(data);
        ((TopComponent) nodeData.getDesignerPage().getInspector()).requestActive();
        nodeData.getDesignerPage().getInspector().refreshList(compList);
        data.initDone();
        if (undoable) {
            addUndoableHappened((EchoBaseNodeData) data);
        }
    }

    /**
     *
     */
    public final void createPopupMenu() {
        popup = new JPopupMenu();
        JMenuItem menuItem;

        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(this);
        popup.add(menuItem);

        menuItem = new JMenuItem("Paste");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        popup.addSeparator();

        menuItem = new JMenuItem("Tab Orders");
        menuItem.addActionListener(this);
        popup.add(menuItem);

        if (EchoUtil.isRunningAsEchoAdmin()) {
            popup.addSeparator();
            menuItem = new JMenuItem("Lock Properties");
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        this.addMouseListener(popupListener);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        //Get the current position.
        int currentPosition;
        int maxUnitIncrement = 10;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition
                    - (currentPosition / maxUnitIncrement)
                    * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int maxUnitIncrement = 10;
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = (EchoFormNode) node;
    }

    @Override
    public void remove() {
        node.setIsDestroying(true);
        ((TopComponent) nodeData.getDesignerPage()).remove(this);
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (ignoreMouseEvent) {
            return;
        }

        if (getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))) {
            removeSelectedForm();
            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, this);
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setDragRectPoint(p, false);
            glassPane.paintImmediately(glassPane.getBounds());
        }

        Point p = (Point) e.getPoint().clone();
        if ((Math.abs(mousePoint.getX() - p.getX()) > 10)
                || (Math.abs(mousePoint.getY() - p.getY()) > 10)) {
            PaletteSupport.setSelectedPaletteItem(null, null);
        }
    }

    private void removeSelectedForm() {
        Node[] ary = nodeData.getDesignerPage().getMgr().getSelectedNodes();
        if (ary != null) {
            ArrayList list = new ArrayList(Arrays.asList(ary));
            if (list.contains(node)) {
                list.remove(node);
            }
            Node[] a = (Node[]) list.toArray(new Node[0]);
            try {
                nodeData.getDesignerPage().getMgr().setSelectedNodes(a);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void setDropPanel(JPanel dropPanel) {
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
     * @param node
     */
    public void setNodeData(EchoFormNodeData node) {
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
     * @param width
     * @param height
     */
    @Override
    public final void setSize(int width, int height) {
        super.setSize(width, height);
        nodeData.setSizeFromEdit(width, height);
        calculateGrid();
    }

    /**
     *
     */
    @Override
    public void removeNotify() {
        // Can't do this here because the parent could be disconnected
        // for some reason other than this component is being destroyed.
        // node.setIsDestroying(true);
        super.removeNotify();
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        ignoreMouseEvent = false;
        if (e.getButton() != MouseEvent.BUTTON1) {
            ignoreMouseEvent = true;
            PaletteSupport.setSelectedPaletteItem(null, null);
            return;
        }

        mousePoint = (Point) e.getPoint().clone();
        if (node != null) {
            glassPane.getDragList().clear();
            // Don't allow multi-select with form.
            addSelectedNode(false);
        }
        if (getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))) {
            glassPane.getDragList().clear();
            Point p = (Point) e.getPoint().clone();
            SwingUtilities.convertPointToScreen(p, this);
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setDragRectPoint(p, true);
            glassPane.setVisible(true);
        }
    }

    public void addSelectedNode(boolean multiSelected) {
        node.addSelectedNode(multiSelected);
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        if (ignoreMouseEvent) {
            ignoreMouseEvent = false;
            return;
        }
        glassPane.setVisible(false);
        multiSelectComponents();
        //Ticket #227
        //Create component on mouse release if there is an item selected
        //on the palette
        Lookup l = PaletteSupport.getSelectedPaletteItem();
        if ((l != null) && (l != Lookup.EMPTY)) {
            Node selNode = (Node) l.lookup(Node.class);
            if (null != selNode) {
                createEchoComponent(e.getX(), e.getY(), selNode.getName());
            }
        }
    }

    //Ticket #471
    public void setLastMouseX(int x) {
        lastMouseX = x;
    }

    //Ticket #471
    public void setLastMouseY(int y) {
        lastMouseY = y;
    }
    
    public int getLastMouseX() {
        return lastMouseX;
    }

    public int getLastMouseY() {
        return lastMouseY;
    }

    private void multiSelectComponents() {
        Point pointStart = new Point(glassPane.getStartPoint().x, glassPane.getStartPoint().y);
        SwingUtilities.convertPointToScreen(pointStart, glassPane);
        SwingUtilities.convertPointFromScreen(pointStart, this);
        Point pointEnd = new Point(glassPane.getCurrPoint().x, glassPane.getCurrPoint().y);
        SwingUtilities.convertPointToScreen(pointEnd, glassPane);
        SwingUtilities.convertPointFromScreen(pointEnd, this);
        Rectangle rect = new Rectangle(pointStart.x, pointStart.y, pointEnd.x - pointStart.x, pointEnd.y - pointStart.y);
        // Multi-selected components were not being checked for the parent
        // so if there were any tabs, the components on multiple tabs could be selected.
        for (IEchoComponentNodeData cnd : compList) {
            if ((cnd.getComponent() != this)
                    && (!cnd.getClass().getName().contains("EchoFormNodeData"))
                    && //Ticket #298
                    (!cnd.getClass().getName().contains("EchoColumnNodeData"))
                    && (cnd.getParentId().equals(id))
                    && (rect.contains(((JComponent) cnd.getComponent()).getLocation()))) {
                if (containComponent((Component) cnd.getComponent())) {
                    cnd.getComponent().getNode().addSelectedNode(true);
                }
            }
        }
    }

    private boolean containComponent(Component comp) {
        for (Component c : this.getComponents()) {
            if (c == comp) {
                return true;
            }
        }
        return false;
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
        String txt = ((JMenuItem) (e.getSource())).getText();
        if (node == null) {
            if (txt.equalsIgnoreCase("Tab Orders")) {
                TabOrdersForm tof = new TabOrdersForm(null, true, nodeData);
                tof.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Not implemented.");
            }
        } else {
            if (!node.handleAction(txt)) {
                if (txt.equalsIgnoreCase("Tab Orders")) {
                    TabOrdersForm tof = new TabOrdersForm(null, true, nodeData);
                    tof.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Not implemented.");
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
