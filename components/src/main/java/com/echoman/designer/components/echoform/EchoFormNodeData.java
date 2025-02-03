/**
 *
 */
package com.echoman.designer.components.echoform;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echocommon.DataContainerManager;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echocommon.GhostComponentAdapter;
import com.echoman.designer.components.echocommon.GhostMotionAdapter;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echocommon.JDesiWindowManager;
import com.echoman.designer.components.echocommon.MakeSizeable.Draggable;
import com.echoman.designer.components.echocommon.MakeSizeable.Resizeable;
import com.echoman.designer.components.echointerfaces.IEchoFormNodeData;
import com.echoman.designer.components.echotextfield.EchoTextFieldNodeData;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;

/**
 *
 * @author david.morin
 */
public class EchoFormNodeData extends EchoBaseNodeData implements IEchoFormNodeData {

    private String formName;
    private String caption;
    private ArrayList<String> formLocations = new ArrayList<>();
    private ArrayList<String> formLocationIds = new ArrayList<>();
    private String formLinkField = "";
    private Color color;
    private boolean visible = true;
    private boolean readOnly = false;
    private boolean signable = false;
    private boolean showNavigator = true;
    private boolean closable = true;
    private boolean modal = false;
    private boolean historyForm = false;
    // Ticket 447 - Need the option to hide these buttons on certain forms.
    private boolean hideInsert = false;
    private boolean hideDelete = false;
    private int left;
    private int height;
    private int width;
    private String table = "";
    private String sortOrder = "";
    private String filterSql = "";
    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> linkTables =
            new LinkedHashMap<>();
    private HashMap<String, String> tableRestrictions = new HashMap<>();
    private HashMap<String, String> tableSorts = new HashMap<>();
    private HashMap<String, Boolean> preventMultiples = new HashMap<>();
    
    private String tabs = "";
    private String tabPosition;
    private String formOpenEvent = "";
    private String formCloseEvent = "";
    private String formAfterSaveEvent = "";
    private transient List<EchoForm> forms = null;
    private transient JTabbedPane tab = null;
    private transient JPanel pnltab = null;
    private transient List<IEchoComponentNodeData> syncList = new ArrayList<>();
    private transient HashMap<String, List<IEchoComponentNodeData>> tabMovedComponents =
            new HashMap<>();
    private transient List<String> componentsToMove = new ArrayList<>();
    // Ticket 521 Removed Record Description Property - not used.
    private transient String[] tableList;
    private transient HashMap<String, String> columnList;
    private boolean fullScreen = false;
    // Ticket 553
    private boolean preventMultipleRecords = false;
    private boolean useLegacyStyles = false;

    public EchoFormNodeData() {
    }

    public String getFilterSql() {
        return filterSql;
    }

    public void setFilterSql(String filterSql) {
        String error = EchoUtil.dangerousSqlCheck(filterSql); 
        if (!"".equals(error)) {
            JOptionPane.showMessageDialog(null, error);
            this.filterSql = "";
        } else {
            this.filterSql = filterSql;
        }
    }

    public boolean getPreventMultipleRecords() {
        return preventMultipleRecords;
    }

    public void setPreventMultipleRecords(boolean preventMultipleRecords) {
        this.preventMultipleRecords = preventMultipleRecords;
    }

    public boolean getUseLegacyStyles() {
        return useLegacyStyles;
    }

    public void setUseLegacyStyles(boolean useLegacyStyles) {
        this.useLegacyStyles = useLegacyStyles;
    }

    public String getFormCloseEvent() {
        return formCloseEvent;
    }

    public void setFormCloseEvent(String formCloseEvent) {
        this.formCloseEvent = formCloseEvent;
    }

    public String getFormOpenEvent() {
        return formOpenEvent;
    }

    public void setFormOpenEvent(String formOpenEvent) {
        this.formOpenEvent = formOpenEvent;
    }

    public String getFormAfterSaveEvent() {
        return formAfterSaveEvent;
    }

    public void setFormAfterSaveEvent(String formAfterSaveEvent) {
        this.formAfterSaveEvent = formAfterSaveEvent;
    }

    @Override
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        String error = EchoUtil.dangerousSqlCheck(sortOrder); 
        if (!"".equals(error)) {
            JOptionPane.showMessageDialog(null, error);
            this.sortOrder = "";
        } else {
            this.sortOrder = sortOrder;
        }
    }

    /**
     * This is called by serialization when constructing the object.
     * No constructor is called so you have to tap into it here.
     * @return
     */
    private Object readResolve() {
        designerPage = JDesiWindowManager.getActiveDesignerPage();
        listeners = Collections.synchronizedList(new LinkedList());
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        compList.add(this);
        // The form is transient and created each time because it is the
        // drop panel for the topcomponent designerpage
        forms = new ArrayList<>();
        EchoForm form = new EchoForm(this, index, true, "0");
        forms.add(form);
        form.setBackground(color);
        form.setSize(width, height);
        form.setName(formName);
        // Ticket #12 Adding scrollbars to form panel.
        ((TopComponent) designerPage).removeAll();
        JScrollPane sp = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ((TopComponent) designerPage).add(sp);
        form.addPropertyChangeListener(WeakListeners.propertyChange(this, form));
        designerPage.setTable(table);
        if (tabs != null) {
            setTabs(tabs);
            setTabPosition(tabPosition);
        }
        if (formLocations == null) {
            formLocations = new ArrayList<>();
            formLocationIds = new ArrayList<>();
        }
        // set any new properties that have been added since the form was created.
        defaultNewlyAddedProperties();
        return this;
    }

    private void defaultNewlyAddedProperties() {
        if ((formLinkField == null) || (formLinkField.equals(""))) {
            if (table.equals("")) {
                formLinkField = "";
            } else {
                formLinkField = EchoUtil.getPrimaryKeyForTable(table);
            }
        }
        
        if (filterSql == null) {
            filterSql = "";
        }

        if (preventMultiples == null) {
            preventMultiples = new HashMap<>();
        }
        
    }
    
    public EchoFormNodeData(IEchoDesignerTopComponent designerPage, boolean dataOnly) {
        super(designerPage);
    }

    /**
     * 
     * @param glassPane
     */
    public EchoFormNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        compList.add(this);
        forms = new ArrayList<>();
        EchoForm form = new EchoForm(this, index, false, "0");
        caption = "Echo Form";
        forms.add(form);
        setFormName("Unnamed");
        // Ticket #12 Adding scrollbars to form panel.
        ((TopComponent) designerPage).removeAll();
        JScrollPane sp = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ((TopComponent) designerPage).add(sp);
        form.addPropertyChangeListener(WeakListeners.propertyChange(this, form));
    }

    /**
     *
     * @param EchoFormNodeData
     *
     */
    @Override
    public void copy(EchoBaseNodeData data) {
        super.copy(data);
        EchoFormNodeData nodeData = (EchoFormNodeData) data;
        formName = nodeData.formName;
        caption = nodeData.caption;
        formLocations = nodeData.formLocations;
        formLocationIds = nodeData.formLocationIds;
        formLinkField = nodeData.formLinkField;
        color = nodeData.color;
        visible = nodeData.visible;
        readOnly = nodeData.readOnly;
        signable = nodeData.signable;
        hideInsert = nodeData.hideInsert;
        hideDelete = nodeData.hideDelete;
        showNavigator = nodeData.showNavigator;
        left = nodeData.left;
        height = nodeData.height;
        width = nodeData.width;
        table = nodeData.table;
        tabs = nodeData.tabs;
        tabPosition = nodeData.tabPosition;
        preventMultipleRecords = nodeData.preventMultipleRecords;
        useLegacyStyles = nodeData.useLegacyStyles;
        // Ticket 521 Removed Record Description Property - not used.
    }

    @Override
    public EchoBaseNodeData cloneData() {
        EchoFormNodeData nodeData = new EchoFormNodeData(designerPage, true);
        nodeData.copy(this);
        return nodeData;
    }

    /**
     * 
     * @return
     */
    public EchoForm getForm() {
        return getForm(0);
    }

    public EchoForm getForm(int idx) {
        try {
            return forms.get(idx);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a list of forms on this tab
     * 
     * @return list of EchoForm
     */
    public List<EchoForm> getForms() {
        return forms;
    }
    
    /**
     * 
     * @return
     */
    public String getFormName() {
        return formName;
    }

    /**
     * 
     * @param formName
     */
    public final void setFormName(String formName) {
        this.formName = formName.replaceAll(" ", "_");
        if (getForm() != null) {
            getForm().setName(this.formName);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public String getTable() {
        designerPage.setTable(table);
        return table;
    }

    @Override
    public void setBorder() {
        addTitle();
        if (EchoUtil.isSelected(getDesignerPage(), getForm())) {
            getForm().setBorder(getForm().getSelectedTB());
        } else {
            getForm().setBorder(getForm().getTb());
        }
    }

    private void addTitle() {
        getForm().getSelectedTB().setTitle(table);
        getForm().getTb().setTitle(table);
    }

    private void removeTitle() {
        getForm().getSelectedTB().setTitle("");
        getForm().getTb().setTitle("");
    }

    /**
     * 
     * @param table
     */
    public void setTable(String table) {
        if (table.equals("")) {
            this.table = "";
            designerPage.setPKey("");
            formLinkField = "";
            removeTitle();
        } else {
            if (table.indexOf(";") == -1) {
                this.table = table;
                addTitle();
            } else {
                this.table = table.substring(0, table.indexOf(";"));
                String keyField = table.substring(table.indexOf(";") + 1, table.length());
                designerPage.setPKey(keyField);
            }
        }
        designerPage.setTable(this.table);
        designerPage.setModified(true);
        formLinkField = EchoUtil.getPrimaryKeyForTable(table);
        // Ticket 482
        DataContainerManager.updateFormComponents("Default", table, designerPage.getCompList());
        columnList = null;
    }

    /**
     * 
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 
     * @param caption
     */
    public void setCaption(String caption) {
        fire("nodename", this.caption, caption);
        this.caption = caption;
        designerPage.setModified(true);
    }

    public ArrayList<String> getFormLocations() {
        return formLocations;
    }

    public void setFormLocations(ArrayList<String> formLocations) {
        List values;
        formLocationIds.clear();
        this.formLocations.clear();
        // Forms can be in multiple locations.
        for (String formLoc : formLocations) {
            values = new ArrayList();
            StringTokenizer tokens = new StringTokenizer(formLoc, ";");
            while (tokens.hasMoreTokens()) {
                values.add(tokens.nextToken());
            }
            if (values.size() > 1) {
                this.formLocations.add((String) values.get(1));
                formLocationIds.add((String) values.get(0));
            }
        }
        designerPage.setModified(true);
    }

    public String getFormLinkField() {
        return formLinkField;
    }

    public void setFormLinkField(String formLinkField) {
        this.formLinkField = formLinkField;
        designerPage.setModified(true);
    }

    @Override
    public ArrayList<String> getFormLocationIds() {
        return formLocationIds;
    }

    public void setFormLocationIds(ArrayList<String> formLocationIds) {
        this.formLocationIds = formLocationIds;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getVisible() {
        return visible;
    }

    /**
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getShowNavigator() {
        return showNavigator;
    }

    /**
     *
     * @param showNavigator
     */
    public void setShowNavigator(boolean showNavigator) {
        this.showNavigator = showNavigator;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getReadOnly() {
        return readOnly;
    }

    /**
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public boolean getSignable() {
        return signable;
    }

    /**
     * 
     * @param signable
     */
    public void setSignable(boolean signable) {
        this.signable = signable;
        designerPage.setModified(true);
    }


    /**
     * 
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     * 
     * @param color
     */
    public void setColor(Color color) {
        this.color = new Color(color.getRGB());
        if (getForm() != null) {
            getForm().setBackground(this.color);
        }
        designerPage.setModified(true);

    }

    /**
     * 
     * @return
     */
    @Override
    public int getLeft() {
        return left;
    }

    /**
     * 
     * @param left
     */
    @Override
    public void setLeft(Integer left) {
        this.left = left;
        if (getForm() != null) {
            getForm().setLocation(left, getForm().getLocation().y);
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     *
     * @param width
     * @param height
     */
    private void setFormSize(int width, int height) {
        if (forms.size() == 1) {
            if (getForm() != null) {
                getForm().setSize(width, height);
            }
        } else {
            if (pnltab != null) {
                pnltab.setSize(width, height);
                tab.setSize(width, height);
            }
        }
    }

    /**
     * 
     * @param height
     */
    public void setHeight(Integer height) {
        this.height = height;
        //Ticket #280
        setFormSize(width, height);

        if (pnltab != null) {
            pnltab.setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
        }

        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * @param width
     */
    public void setWidth(Integer width) {
        this.width = width;
        
        //Ticket #280

        setFormSize(width, height);

        if (pnltab != null) {
            pnltab.setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
        }

        designerPage.setModified(true);

    }

    /**
     *
     * @param width
     * @param height
     */
    public void setSize(Point pnt) {
        setWidth(pnt.x);
        setHeight(pnt.y);
    }

    /**
     * 
     * @param width
     * @param height
     */
    public void setSizeFromEdit(Integer width, Integer height) {
        undoableHappened("size", new Point(this.width, this.height), new Point(width, height));
        this.width = width;
        this.height = height;
        fire("refresh", 0, 0);
        if (getForm() != null) {
            getForm().setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
        }
        if (pnltab != null) {
            pnltab.setPreferredSize(new Dimension(width > 1 ? width : 1, height > 1 ? height : 1));
        }
        designerPage.setModified(true);
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void setLocationFromEdit(Integer x, Integer y) {
        //this.top = y;
        this.left = x;
        fire("refresh", 0, 0);
        designerPage.setModified(true);
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return index + " - " + table;
    }

    @Override
    // We want to override this for the form because the form component
    // is transient and not stored with the data.
    public IEchoComponent getComponent() {
        return getForm();
    }

    @Override
    // We want to override this for the form because the form component
    // is transient and not stored with the data.
    public void setComponentNode(EchoBaseNode node) {
        //Ticket #486
        //getForm().setNode(node);
        for (EchoForm f : forms) {
            if (f.getNode() == null) {
                f.setNode(node);
            }
        }
    }

    @Override
    // We want to override this for the form because the form component
    // is transient and not stored with the data.
    public void remove() {
        getForm().remove();
    }

    @Override
    public void updateName(int index) {
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"form"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> getLinkTables() {
        if (linkTables == null) {
            linkTables = new LinkedHashMap<>();
        }
        return linkTables;
    }

    /**
     * return a list of all the tables used in this form
     *
     * @return List of table name
     *
     */
    public List<String> getTables() {
        List<String> tables = new ArrayList<>();
        if (!"".equals(table)) {
            tables.add(table);
        }
        if (linkTables != null) {
            for (Entry e : linkTables.entrySet()) {
                String tbl = e.getKey().toString();
                if (!tables.contains(tbl)) {
                    tables.add(tbl);
                }
                HashMap<String, HashMap<String, String>> map =
                        (HashMap<String, HashMap<String, String>>) e.getValue();
                if (map != null) {
                    for (String ctbl : map.keySet()) {
                        if (!tables.contains(ctbl)) {
                            tables.add(ctbl);
                        }
                    }
                }
            }
        }
        return tables;
    }

    public String getTabs() {
        return tabs;
    }

    private void addMainForm() {
        TopComponent tc = (TopComponent) designerPage;
        tc.removeAll();
        JScrollPane sp = new JScrollPane(getForm(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tc.add(sp);
        tab = null;
        pnltab = null;
        tabs = "";
    }

    //Ticket #217
    private EchoForm createTabForm(String key) {
        EchoForm form = new EchoForm(this, index, false, key, width, height);
        form.setNode(getForm().getNode());
        return form;
    }

    private void addTab(int atPos, String title) {
        if (tab == null) {
            TopComponent tc = (TopComponent) designerPage;
            tc.removeAll();

            pnltab = new JPanel();
            pnltab.setOpaque(true);
            pnltab.setLayout(null);

            tab = new JTabbedPane();
            //Ticket #493
            tab.setSize(width, height + 25);
            tab.addTab(title, getForm());
            pnltab.add(tab);

            JScrollPane sp = new JScrollPane(pnltab, 
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            tc.add(sp);
        } else {
            if (atPos > tab.getTabCount() - 1) {
                String key = Integer.toString(tab.getTabCount());
                EchoForm form = createTabForm(key);
                forms.add(form);
                tab.addTab(title, form);
            } else {
                String tabTitle = tab.getTitleAt(atPos);
                if (!title.equalsIgnoreCase(tabTitle)) {
                    int idx = tab.indexOfTab(title);
                    if (idx == -1) {
                        if (atPos == 0) {
                            EchoForm form = createTabForm("1");
                            if (forms.size() > 1) {
                                forms.set(1, form);
                            } else {
                                forms.add(form);
                            }
                            tab.insertTab(title, null, form, "", 1);
                            swapTab(0, tab.getTitleAt(0), 1, title);
                        } else {
                            EchoForm form = createTabForm(Integer.toString(atPos));
                            forms.set(atPos, form);
                            tab.insertTab(title, null, form, "", atPos);

                        }
                    } else {
                        swapTab(atPos, tabTitle, idx, title);
                    }
                }
            }
        }
    }

    private void swapTab(int idx1, String title1, int idx2, String title2) {
        EchoForm form = getForm(idx1);
        EchoForm form2 = getForm(idx2);
        tab.setTitleAt(idx2, title1);
        tab.setTitleAt(idx1, title2);
        List<Component> comps = new ArrayList<>();
        comps.addAll(Arrays.asList(form.getComponents()));
        form.removeAll();
        List<EchoTextFieldNodeData> textList = new ArrayList<>();
        for (Component c : form2.getComponents()) {
            IEchoComponent ec = (IEchoComponent) c;
            IEchoComponentNodeData node = ec.getNode().getNodeData();
            if (node instanceof EchoTextFieldNodeData) {
                textList.add((EchoTextFieldNodeData) node);
            }
            form.add(c);
            setComponentDragPanelAndListeners(c, form);
        }
        form2.removeAll();
        for (Component c : comps) {
            IEchoComponent ec = (IEchoComponent) c;
            IEchoComponentNodeData node = ec.getNode().getNodeData();
            if (node instanceof EchoTextFieldNodeData) {
                textList.add((EchoTextFieldNodeData) node);
            }
            form2.add(c);
            setComponentDragPanelAndListeners(c, form2);
        }
        for (EchoTextFieldNodeData txt : textList) {
            txt.setLastLabelIds();
        }
    }

    private void setComponentDragPanelAndListeners(Component c, EchoForm form) {
        IEchoComponent ec = (IEchoComponent) c;
        IEchoComponentNodeData node = ec.getNode().getNodeData();
        node.getComponent().setDropPanel(form);
        for (MouseListener l : c.getMouseListeners()) {
            if (l instanceof GhostComponentAdapter) {
                c.removeMouseListener(l);
            }
        }
        for (MouseMotionListener l : c.getMouseMotionListeners()) {
            if (l instanceof GhostMotionAdapter) {
                c.removeMouseMotionListener(l);
            }
        }
        new Draggable(c, form);
        new Resizeable(c);
        form.addComponent(node);
    }

    public void setFormNodes() {
        getForm().getNode();
        for (EchoForm form : forms) {
            if (form != getForm()) {
                form.setNode(getForm().getNode());
            }
        }
    }

    public void syncTabForms() {
        if ((tab != null) && (tab.getTabCount() > 0)) {
            forms.clear();
            for (int i = 0; i < tab.getTabCount(); i++) {
                EchoForm form = (EchoForm) tab.getComponentAt(i);
                form.setId(Integer.toString(i));
                forms.add(form);
            }
        }
        getComponentsToMove().clear();
        getTabMovedComponents().clear();
    }

    public void setTabs(String tabs) {
        this.tabs = tabs;
        if ("".equals(tabs)) {
            List<IEchoComponentNodeData> compList = designerPage.getCompList();
            addMainForm();
            designerPage.getInspector().refreshList(compList);
        } else {
            StringTokenizer st = new StringTokenizer(tabs, "\n");
            int cnt = 0;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                addTab(cnt, s);
                cnt++;
            }
            //Ticket #415 - force the tabs to refresh to avoid missing scrollbar
            if (cnt == 1) {
                addTab(1, "");
                tab.remove(1);
                forms.remove(1);
            }
        }
        if (syncList != null) {
            syncList.clear();
        }
        ((TopComponent) designerPage).validate();
    }

    public void renameTab(String oldCaption, String newCaption) {
        for (int i = 0; i < tab.getTabCount(); i++) {
            if (oldCaption.equalsIgnoreCase(tab.getTitleAt(i))) {
                tab.setTitleAt(i, newCaption);
                break;
            }
        }
    }

    public void syncTabComponents(int oldId, int newId) {
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        for (IEchoComponentNodeData node : compList) {
            if (syncList == null) {
                syncList = new ArrayList<>();
            }
            if (!syncList.contains(node)) {
                if (node.getParentId() != null) {
                    boolean compHasMoved = false;
                    for (String s : getTabMovedComponents().keySet()) {
                        List<IEchoComponentNodeData> moveList = getTabMovedComponents().get(s);
                        if (moveList.contains(node)) {
                            compHasMoved = true;
                            break;
                        }
                    }
                    if (!compHasMoved) {
                        if (node.getParentId().equals(Integer.toString(oldId))) {
                            node.setParentId(Integer.toString(newId), false);
                            syncList.add(node);
                        }
                    }
                }
            }
        }
    }

    private void removeTabComponents(String id) {
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        int idx = 0;
        while (idx < compList.size()) {
            IEchoComponentNodeData node = compList.get(idx);
            if (node.getParentId() == null) {
                idx++;
            } else {
                if (node.getParentId().equals(id)) {
                    compList.remove(node);
                } else {
                    idx++;
                }
            }
        }
        //Ticket #222
        //Also delete all moved components which now has a new parent id after
        //being moved to the first page
        List<IEchoComponentNodeData> lst = getTabMovedComponents().get(id);
        if (lst != null) {
            for (IEchoComponentNodeData node : lst) {
                compList.remove(node);
            }
            getTabMovedComponents().remove(id);
        }
    }

    //Ticket #226
    public void moveComponentsToFirstPage() {
        List<EchoTextFieldNodeData> textList = new ArrayList<>();
        for (String formid : getComponentsToMove()) {
            List<IEchoComponentNodeData> compList = designerPage.getCompList();
            for (IEchoComponentNodeData node : compList) {
                if (node.getParentId() != null) {
                    if (node.getParentId().equals(formid)) {
                        if (node instanceof EchoTextFieldNodeData) {
                            textList.add((EchoTextFieldNodeData) node);
                        }
                        moveComponentToFirstPage(node);
                    }
                }
            }
        }
        for (EchoTextFieldNodeData txt : textList) {
            txt.setLastLabelIds();
        }
    }

    public void deleteTab(String tabCaption, String id, boolean deleteComponents) {
        List<IEchoComponentNodeData> compList = designerPage.getCompList();
        boolean componentDeleted = false;
        for (int i = 0; i < tab.getTabCount(); i++) {
            if (tabCaption.equalsIgnoreCase(tab.getTitleAt(i))) {
                if (i == 0) {
                    if (forms.size() > 1) {
                        String formId = getForm(1).getId();
                        tab.setTitleAt(0, tab.getTitleAt(1));
                        tab.remove(1);
                        forms.remove(1);

                        if (deleteComponents) {
                            removeTabComponents(id);
                            componentDeleted = true;
                        }

                        if (!getComponentsToMove().contains(formId)) {
                            getComponentsToMove().add(formId);
                        }
                    }
                } else {
                    tab.remove(i);
                    forms.remove(i);
                    componentDeleted = false;
                }
                if (deleteComponents) {
                    if (!componentDeleted) {
                        removeTabComponents(id);
                    }
                } else {
                    //Move components to first page
                    if (!getComponentsToMove().contains(id)) {
                        getComponentsToMove().add(id);
                    }
                }
                //Ticket #198
                designerPage.getInspector().refreshList(compList);
                break;
            }
        }
    }

    private HashMap<String, List<IEchoComponentNodeData>> getTabMovedComponents() {
        if (tabMovedComponents == null) {
            tabMovedComponents = new HashMap<>();
        }
        return tabMovedComponents;
    }

    private List<String> getComponentsToMove() {
        if (componentsToMove == null) {
            componentsToMove = new ArrayList<>();
        }
        return componentsToMove;
    }

    //Ticket #219
    private void moveComponentToFirstPage(IEchoComponentNodeData node) {
        //Ticket #222
        //store node of component to be moved so we can delete them later if 
        //needed
        List<IEchoComponentNodeData> lst = getTabMovedComponents().get(node.getParentId());
        if (lst == null) {
            lst = new ArrayList<>();
            getTabMovedComponents().put(node.getParentId(), lst);
        }
        lst.add(node);
        node.setParentId("0", false);
        Component c = (Component) node.getComponent();
        setComponentDragPanelAndListeners(c, getForm());
    }

    public EchoForm getActiveForm() {
        if (tab == null) {
            return getForm();
        } else {
            int idx = tab.getSelectedIndex();
            if (idx == -1) {
                return null;
            } else {
                return getForm(idx);
            }
        }
    }

    public HashMap<String, Boolean> getPreventMultiples() {
        return preventMultiples;
    }

    public HashMap<String, String> getTableRestrictions() {
        return tableRestrictions;
    }

    public HashMap<String, String> getTableSorts() {
        return tableSorts;
    }

    // Ticket 521 Removed Record Description Property - not used.
 
    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
        designerPage.setModified(true);
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
        designerPage.setModified(true);
    }

    public boolean isHistoryForm() {
        return historyForm;
    }

    public void setHistoryForm(boolean historyForm) {
        this.historyForm = historyForm;
        designerPage.setModified(true);
    }

    @Override
    public void setTop(Integer top) {
    }

    @Override
    public int getTop() {
        return 0;
    }

    @Override
    public void initCreate() {
        readResolve();
    }

    public String getTabPosition() {
        return tabPosition;
    }

    public void setTabPosition(String tabPosition) {
        this.tabPosition = tabPosition;
        if (tab != null) {
            if ("bottom".equals(tabPosition)) {
                tab.setTabPlacement(JTabbedPane.BOTTOM);
            } else {
                tab.setTabPlacement(JTabbedPane.TOP);
            }
        }
    }

    @Override
    public String[] getTableList(String propertyName) {
        if (tableList == null) {
            tableList = EchoUtil.getTables(this, propertyName);
        }
        return tableList;
    }

    @Override
    public HashMap<String, String> getColumnList(String propertyName) {
        if (columnList == null) {
            columnList = EchoUtil.getTableColumns(this, propertyName);
        }
        return columnList;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        if (getForm() != null) {
            PropertySet set = getForm().getNode().getPropertySets()[1];
            if (fullScreen) {
                set.getProperties()[5].setHidden(true);
                set.getProperties()[6].setHidden(true);
            } else {
                set.getProperties()[5].setHidden(false);
                set.getProperties()[6].setHidden(false);
            }
            getForm().getNode().restoreSheet();
            designerPage.setModified(true);
        }

    }

    @Override
    public void clearUncopiableProperties(String table) {
        //not implemented
    }

    public boolean isHideDelete() {
        return hideDelete;
    }

    public void setHideDelete(boolean hideDelete) {
        this.hideDelete = hideDelete;
        designerPage.setModified(true);
    }

    public boolean isHideInsert() {
        return hideInsert;
    }

    public void setHideInsert(boolean hideInsert) {
        this.hideInsert = hideInsert;
        designerPage.setModified(true);
    }

}
