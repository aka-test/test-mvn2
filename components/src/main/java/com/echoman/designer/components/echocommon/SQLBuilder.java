/**
 * 
 */
package com.echoman.designer.components.echocommon;

import com.jidesoft.swing.SearchableUtils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.databasemanager.DesignerPanel;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author  david.morin
 */
public class SQLBuilder extends javax.swing.JDialog implements DropTargetListener {

    private int returnStatus = RET_CANCEL;
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    public static final String EMPTY_STRING = "";
    public static final String ONE_EQUALS_ZERO = "1=0";
    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String PERIOD = ".";
    public static final String SINGLE_QUOTE = "'";
    public static final String DOUBLE_QUOTE = "''";
    public static final String SINGLE_QUOTE_COMMA = "',";
    public static final String COMMA_SINGLE_QUOTE = ",'";
    public static final String SINGLE_QUOTE_COMMA_SINGLE_QUOTE = "','";
    public static final String BRACKET_WITH_PERIOD = "].[";
    public static final String ESCAPED_QUOTE = "\"";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
    public static final String NULL = "null";
    public static final String DBUSERNAME = "dbUserName";

    public static final String SELECT_NAME = "SELECT";
    public static final String FROM_NAME = "FROM";
    public static final String WHERE_NAME = "WHERE";
    public static final String ORDER_NAME = "ORDER BY";
    public static final String GROUP_NAME = "GROUP BY";
    public static final String CRITERIA_NAME = "criteria";
    public static final String ORDER_LOWER_NAME = "order";
    public static final String SELECT_PREFIX = "SELECT\n";
    public static final String FROM_PREFIX = "\nFROM\n";
    public static final String WHERE_PREFIX = "\nWHERE\n";
    public static final String ORDER_PREFIX = "\nORDER BY\n";
    public static final String GROUP_PREFIX = "\nGROUP BY\n";
    public static final String SELECT_PREFIX_TAB = "SELECT\n\t";
    public static final String FROM_PREFIX_TAB = "\nFROM\n\t";
    public static final String WHERE_PREFIX_TAB = "\nWHERE\n\t";
    public static final String ORDER_PREFIX_TAB = "\nORDER BY\n\t";
    public static final String GROUP_PREFIX_TAB = "\nGROUP BY\n\t";
    public static final String JOIN_PREFIX_TAB = "\nJOIN\n\t";
    public static final String ASC_PREFIX = " ASC\n";
    public static final String DESC_PREFIX = " DESC\n";
    public static final String DESCENDING = "DESCENDING";
    public static final String SIMPLE_DATE = "MM/dd/yyyy";
    public static final String DATE_TOKEN_LOWER = "{date}";
    public static final String DATE_CAST = "cast(floor(cast(getdate() as float)) as datetime) ";
    public static final String DATE_TOKEN_HUMP = "{Date}";
    public static final String TIME_TOKEN_LOWER = "{time}";
    public static final String GETDATE = "getdate() ";
    public static final String TIME_TOKEN_HUMP = "{Time}";
    public static final String USERID_TOKEN_LOWER = "{userid}";
    public static final String SUSER_NAME = "suser_name() ";
    public static final String USERID_TOKEN_HUMP = "{UserId}";
    public static final String DB_CONN_SETUP_ERROR = "You must have a valid database connection set up.";
    public static final String DB_CONN_ERROR = "You must be connected to the database to set up validations.";
    public static final String FAILED_SQL_ERROR = "Failed to execute SQL: ";
    public static final String ONE_VISIBLE_MESSAGE = "You must have at least one visible column.";

    public static final String VALIDATION_NAME_INSERT_SQL = "insert into validation_lists (validation_list_id, list_name, validation_type, start_date, create_dt, createuser_c, touch_date, touch_user)";
    public static final String VALIDATION_NAME_UPDATE_SQL ="update validation_lists set list_name = '";
    public static final String VALIDATION_DELETE_SQL = "update validation_lists set end_date = getDate() ";

    public static final String INSERT_VALUES_OPEN = " values ('";
    public static final String INSERT_VALUES_CLOSE = "')";
    public static final String UPDATE_WHERE = " where validation_list_id = '";

    public static final String DATE_TOKEN_REGEX = "((?<= )|(?<!\\.))[{]\\w+[}]((?= )|(?!\\.))";
    private static final String DESC_REGEX = "(?i)\\b(?:DESC)\\b";
    private static final String ASC_REGEX = "(?i)\\b(?:ASC)\\b";
    private static final String COLUMNS_REGEX = "(?i)(\\[*[\\w]+(\\])?\\.(\\[)?[\\w]+(\\])?) +(?=[=<>]| *[=<>]|\\b(<>|.*like|.*in)\\b)";
    private static final String SPLIT_OR_REGEX = "(?mi)(^ *or|^or|\\)or)";
    private static final String SPLIT_AND_REGEX = "(?mi)(^ *and|^and|\\)and)";
    private static final String SQL_PARTS_REGEX = "(?i)\\b(select|from|where|group by|order by|join)\\b";
    private static final String SQL_REPLACE_REGEX = "(?m)^[\\s]+|^[\\n]+|[\\s]+$|[\\n]+$";
    private static final String ORDER_REGEX = "(?i)\\b(?:ORDER BY)\\b";
    private static final String FROM_REGEX = "(?i)\\b(?:FROM)\\b";
    private static final String WHERE_REGEX = "(?i)\\b(?:WHERE)\\b";
    private static final String GROUP_REGEX = "(?i)\\b(?:GROUP BY)\\b";

    private static final String SQL_BUILDER = "SQL Builder";
    private static final String RIGHT_ARROW = ">";
    private static final String LOWER_O = "o";
    private static final String DESC_NAME = "DESC";
    private static final String ASC_NAME = "ASC";
    private static final String DISABLED_BACKGROUND = "EditorPane.disabledBackground";
    private static final String OPEN_PAREN = "(";
    private static final String CLOSE_PAREN = ")";
    private static final String SELECT_TABLE_MSG = "Please select a table before generating SQL statement.";
    private static final String SELECT_COLUMN_MSG = "Please select table columns.";
    private static final String NEWLINE = "\n";
    private static final String TAB = "\t";
    private static final String OR_PREFIX = "\nOR\n";
    private static final String AND_PREFIX = " AND";
    public static final String USER_LOWER = "user";
    public static final String TABLE_LOWER = "table";
    private static final String TABLE_HUMP = "Table";
    private static final String TABLE_UPPER = "TABLE";
    private static final String VIEW_UPPER = "VIEW";
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_NAME = "TABLE_NAME";
    
    /** The PropertyEditor instance */
    public PropertyEditor editor;
    private GhostGlassPane glassPane;
    private boolean keyPressed = false;
    private TableModelListener criteriaTableListener = null;
    private TableModelListener displayColumnsTableListener = null;

    /**
     * 
     * @param g The current graphics context.
     */
    public void paintLinkConnectors(Graphics g) {
        g.setColor(Color.red);
        try {
            for (int i = 0; i < tableLayoutPanel.getComponentCount(); i++) {
                if (tableLayoutPanel.getComponent(i) instanceof SQLTableVisualComponent) {
                    SQLTableVisualComponent comp = (SQLTableVisualComponent) tableLayoutPanel.getComponent(i);
                    for (String tableName : comp.getLinksToOtherTables().keySet()) {
                        Point from = comp.getPoint(tableName);
                        Point to = comp.getToPoint(tableName);
                        if (!((from == null) || (to == null))) {
                            g.drawLine(from.x + 8, from.y - 2, to.x - 10, to.y - 2);
                            g.drawString(LOWER_O, from.x, from.y + 3);
                            g.drawString(RIGHT_ARROW, to.x - 10, to.y + 3);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(SQLBuilder.class.getName()).log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        if (!(EMPTY_STRING.equals(src.getText()))) {
            src.setText(src.getText() + COMMA);
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        //Ticket #48 - prevent adding the same string on a textField multiple times
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
        try {
            Transferable tr = dtde.getTransferable();
            String s = tr.getTransferData(DataFlavor.stringFlavor).toString();
            if (src.getText().contains(s)) {
                dtde.rejectDrag();
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * 
     * @param dte
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        JTextField src = (JTextField) ((DropTarget) dte.getSource()).getComponent();
        if ((!(EMPTY_STRING.equals(src.getText()))) && (src.getText().endsWith(COMMA)) && ((DropTarget) (dte.getSource())).isActive()) {
            src.setText(src.getText().substring(0, src.getText().length() - 1));
        }
    }

    /**
     * 
     * @param dtde
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        JTextField src = (JTextField) ((DropTarget) dtde.getSource()).getComponent();
    }

    /**
     * 
     */
    public class TableListPopupListener implements PopupMenuListener {

        /**
         * 
         * @param e
         */
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        /**
         * 
         * @param e
         */
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        /**
         * 
         * @param e
         */
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
    }

    /**
     * 
     */
    public class SelectionListener implements ListSelectionListener {

        JTable table;

        SelectionListener(JTable table) {
            this.table = table;
        }

        /**
         * 
         * @param e
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            //Ticket#28
            EchoUtil.showNotification(SQL_BUILDER, Integer.toString(table.getSelectedRow()));
        }
    }

    /**
     * 
     */
    private void populateFromSQL() {
        int orderEndIndex;
        HashMap<String, String> sqlParts = ((QueryTableModel) validationsListItems.getModel()).getSqlParts();
        if (!(sqlParts == null)) {
            // This needs to be the design time columns, not the visible ones.
            String selectedColumns = ((QueryTableModel) validationsListItems.getModel()).getDesignTimeColumnsAsString();
            //String selectedColumns = sqlParts.get("SELECT");
            String tableName = sqlParts.get(FROM_NAME);

            boolean foundDescMatch = false;
            boolean foundAscMatch = false;
            if (!(sqlParts.get(ORDER_NAME) == null)) {
                try {
                    Pattern regex = Pattern.compile(DESC_REGEX);
                    Matcher regexMatcher = regex.matcher(sqlParts.get(ORDER_NAME));
                    foundDescMatch = regexMatcher.find();
                    if (!foundDescMatch) {
                        regex = Pattern.compile(ASC_REGEX);
                        regexMatcher = regex.matcher(sqlParts.get(ORDER_NAME));
                        foundAscMatch = regexMatcher.find();
                    }
                } catch (PatternSyntaxException ex) {
                    // Syntax error in the regular expression
                    JOptionPane.showMessageDialog(null, ex);
                }
                if (foundAscMatch) {
                    orderEndIndex = sqlParts.get(ORDER_NAME).indexOf(SPACE + ASC_NAME);
                    btnASC.setSelected(true);
                } else if (foundDescMatch) {
                    orderEndIndex = sqlParts.get(ORDER_NAME).indexOf(DESC_NAME);
                    btnDESC.setSelected(true);
                } else {
                    orderEndIndex = sqlParts.get(ORDER_NAME).length();
                }
                orderBy.setText(sqlParts.get(ORDER_NAME).substring(0, orderEndIndex));
            }
            groupBy.setText(sqlParts.get(GROUP_NAME));
            addVisualTable(tableLayoutPanel, tableName.substring(0, tableName.indexOf(SPACE)), 
                    selectedColumns, 150, 150, null);
        }
    }

    /**
     * 
     * @param parent
     * @param modal
     * @param editor
     * @param sql
     */
    public SQLBuilder(java.awt.Frame parent, boolean modal, PropertyEditorSupport editor, String sql) {
        super(parent, modal);
        initComponents();
        criteriaTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = validationsListCriteria.getSelectedRow();
                int col = validationsListCriteria.getSelectedColumn();
                if (!((row == -1) || (col == -1))) {
                    String value = (String) validationsListCriteria.getModel().getValueAt(row, col);
                    value = value.trim();

                    // Ticket 33273
                    String error = EchoUtil.dangerousSqlCheck(value);
                    if (!"".equals(error)) {
                        JOptionPane.showMessageDialog(null, error);
                        value = "";
                        validationsListCriteria.getModel().setValueAt(value, row,col);
                    }
        
                    ((QueryTableModel) validationsListItems.getModel()).setStoredFilter(row, col, value);
                    validationsSQLEdit.setText(rebuildWhereClause(validationsSQLEdit.getText(), (ValidationsCriteriaModel) validationsListCriteria.getModel(), EMPTY_STRING));
                    executeQuery(validationsSQLEdit.getText());
                }
            }
        };

        validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);

        displayColumnsTableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (validationsListDisplayColumns.isFocusOwner()) {
                    QueryTableModel model = (QueryTableModel) validationsListItems.getModel();
                    for (int col=0; col<model.getColumnCount()-1; col++) {
                        Object val = validationsListDisplayColumns.getValueAt(0, col);
                        if ((val != null) && ((Boolean)validationsListDisplayColumns.getValueAt(0, col))) {
                            // Update the runtime SQL when visible columns change
                            model.addVisibleColumn(model.getColumnName(col));
                        } else {
                            model.removeVisibleColumn(model.getColumnName(col));
                        }
                    }
                    // Update the runtime SQL when visible columns change
                    validationsSQLEdit.setText(rebuildSelectClause(validationsSQLEdit.getText(), ((QueryTableModel)model).getVisibleColumnsAsString()));
                    executeQuery(validationsSQLEdit.getText());
                }
            }
        };

        validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);

        setLocationRelativeTo(null);
        glassPane = new GhostGlassPane();
        this.setGlassPane(glassPane);
        try {
            orderBy.getDropTarget().addDropTargetListener(this);
            groupBy.getDropTarget().addDropTargetListener(this);
        } catch (TooManyListenersException ex) {
            Exceptions.printStackTrace(ex);
        }

        // Prepopulate from current sql
        if (!(sql.equals(EMPTY_STRING))) {
            validationsSQLEdit.setText(sql);
            if (executeQuery(sql)) {
                buildCriteriaModel(validationsListItems, validationsListCriteria);
                buildDisplayColumnsModel(validationsListItems, validationsListDisplayColumns);
                validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
                validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
                populateFromSQL();
            }
        }

        JTableHeader itemsHeader = validationsListItems.getTableHeader();
        itemsHeader.setForeground(Color.white);
        itemsHeader.setBackground(new Color(127, 157, 185));
        JTableHeader criteriaHeader = validationsListCriteria.getTableHeader();
        criteriaHeader.setForeground(Color.white);
        criteriaHeader.setBackground(new Color(127, 157, 185));

        if (editor != null) {
            orderBy.setEnabled(false);
            orderBy.setBackground(Color.getColor(DISABLED_BACKGROUND));
            tableList.setEnabled(false);
            tableList.setEnabled(false);
            validationsSQLEdit.setEnabled(false);
            validationsSQLEdit.setBackground(Color.getColor(DISABLED_BACKGROUND));
        }
        if (tableList.isEnabled()) {
            tableList.requestFocus();
        }
    }

    /**
     * 
     * @param retStatus
     */
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * 
     * @return
     */
    public String getSQL() {
        return validationsSQLEdit.getText();
    }

    /**
     *
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pmDataOptionList = new javax.swing.JPopupMenu();
        miDelete = new javax.swing.JMenuItem();
        miListDnDSort = new javax.swing.JCheckBoxMenuItem();
        btnGroup = new javax.swing.ButtonGroup();
        tableLayoutPanel = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintLinkConnectors(g);
            }
        };
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        validationsListItems = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        validationsListDisplayColumns = new javax.swing.JTable();
        displayColumnsCaption = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        validationsListCriteria = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        tableList = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        groupBy = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        orderBy = new javax.swing.JTextField();
        btnASC = new javax.swing.JRadioButton();
        btnDESC = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        validationsSQLEdit = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        miDelete.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.miDelete.text")); // NOI18N
        miDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DataListOptionDelete(evt);
            }
        });
        pmDataOptionList.add(miDelete);

        miListDnDSort.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.miListDnDSort.text")); // NOI18N
        miListDnDSort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miListDnDSortActionPerformed(evt);
            }
        });
        pmDataOptionList.add(miListDnDSort);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.title")); // NOI18N
        setLocationByPlatform(true);
        setModal(true);
        setName("validDataListSetup"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        tableLayoutPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        tableLayoutPanel.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        tableLayoutPanel.setName("tableLayoutPanel"); // NOI18N
        tableLayoutPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                tableLayoutPanelMouseDragged(evt);
            }
        });
        tableLayoutPanel.setLayout(null);

        validationsListItems.setBackground(javax.swing.UIManager.getDefaults().getColor("EditorPane.disabledBackground"));
        validationsListItems.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validationsListItems.setModel(new QueryTableModel(null));
        validationsListItems.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(validationsListItems);

        validationsListDisplayColumns.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validationsListDisplayColumns.setRowSelectionAllowed(false);
        validationsListDisplayColumns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListDisplayColumns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListDisplayColumnsKeyPressed(evt);
            }
        });
        jScrollPane8.setViewportView(validationsListDisplayColumns);

        displayColumnsCaption.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        displayColumnsCaption.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        displayColumnsCaption.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.displayColumnsCaption.text")); // NOI18N
        displayColumnsCaption.setOpaque(true);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel12.text")); // NOI18N
        jLabel12.setOpaque(true);

        validationsListCriteria.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validationsListCriteria.setRowSelectionAllowed(false);
        validationsListCriteria.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        validationsListCriteria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                validationsListCriteriaKeyPressed(evt);
            }
        });
        jScrollPane7.setViewportView(validationsListCriteria);

        btnCancel.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCancel(evt);
            }
        });

        btnOk.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.btnOk.text")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancel)
                .addGap(18, 18, 18)
                .addComponent(btnOk)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(displayColumnsCaption, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(displayColumnsCaption, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        displayColumnsCaption.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.displayColumnsCaption.AccessibleContext.accessibleName")); // NOI18N

        tableList.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.tableList.toolTipText")); // NOI18N
        tableList.setName("tableList"); // NOI18N
        SearchableUtils.installSearchable(tableList);
        tableList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableListActionPerformed(evt);
            }
        });
        tableList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableListKeyPressed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel25.text")); // NOI18N
        jLabel25.setOpaque(true);

        groupBy.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.groupBy.text")); // NOI18N
        groupBy.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.groupBy.toolTipText")); // NOI18N
        groupBy.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        groupBy.setName("groupBy"); // NOI18N
        groupBy.setVerifyInputWhenFocusTarget(false);
        groupBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                groupByKeyTyped(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel24.text")); // NOI18N
        jLabel24.setOpaque(true);

        orderBy.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.orderBy.text")); // NOI18N
        orderBy.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.orderBy.toolTipText")); // NOI18N
        orderBy.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        orderBy.setName("orderBy"); // NOI18N
        orderBy.setVerifyInputWhenFocusTarget(false);
        orderBy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                orderByMouseReleased(evt);
            }
        });
        orderBy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                orderByKeyTyped(evt);
            }
        });

        btnGroup.add(btnASC);
        btnASC.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.btnASC.text")); // NOI18N
        btnASC.setName("btnASC"); // NOI18N

        btnGroup.add(btnDESC);
        btnDESC.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.btnDESC.text")); // NOI18N
        btnDESC.setName("btnDESC"); // NOI18N

        jLabel1.setBackground(new java.awt.Color(189, 207, 231));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel1.text")); // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel1.toolTipText")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        jLabel1.setOpaque(true);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel1MouseEntered(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel3.text")); // NOI18N
        jLabel3.setOpaque(true);

        jLabel7.setBackground(new java.awt.Color(189, 207, 231));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel7.text")); // NOI18N
        jLabel7.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel7.toolTipText")); // NOI18N
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Nb.ScrollPane.Border.color")));
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel7.setOpaque(true);
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel7MouseEntered(evt);
            }
        });

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setAutoscrolls(true);
        jScrollPane3.setHorizontalScrollBar(null);

        validationsSQLEdit.setColumns(1);
        validationsSQLEdit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        validationsSQLEdit.setLineWrap(true);
        validationsSQLEdit.setTabSize(4);
        validationsSQLEdit.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "ValidDataListSetup.sqlStatement.text")); // NOI18N
        validationsSQLEdit.setToolTipText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.validationsSQLEdit.toolTipText")); // NOI18N
        validationsSQLEdit.setWrapStyleWord(true);
        validationsSQLEdit.setName("sqlStatement"); // NOI18N
        validationsSQLEdit.setVerifyInputWhenFocusTarget(false);
        validationsSQLEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DataOptionSQLAdd(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                validationsSQLEditKeyTyped(evt);
            }
        });
        jScrollPane3.setViewportView(validationsSQLEdit);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(org.openide.util.NbBundle.getMessage(SQLBuilder.class, "SQLBuilder.jLabel2.text")); // NOI18N
        jLabel2.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tableLayoutPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tableList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(groupBy)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(orderBy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnASC)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDESC))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(groupBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(orderBy, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDESC)
                    .addComponent(btnASC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableLayoutPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void doCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCancel
    doClose(RET_CANCEL);
}//GEN-LAST:event_doCancel

private void DataListOptionDelete(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DataListOptionDelete
}//GEN-LAST:event_DataListOptionDelete

private void DataOptionSQLAdd(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DataOptionSQLAdd
    if (((evt.getKeyCode() == KeyEvent.VK_ENTER)) && (evt.isShiftDown())) {
        boolean executed;
        if (((JTextArea) evt.getSource()).getSelectedText() == null) {
            executed = executeQuery(((JTextArea) evt.getSource()).getText());
        } else {
            executed = executeQuery(((JTextArea) evt.getSource()).getSelectedText());
        }
        if (executed) {
            buildCriteriaModel(validationsListItems, validationsListCriteria);
            buildDisplayColumnsModel(validationsListItems, validationsListDisplayColumns);
            // New model so attach the listener again - reference to old model is gone and should GC
            validationsListCriteria.getModel().addTableModelListener(criteriaTableListener);
            validationsListDisplayColumns.getModel().addTableModelListener(displayColumnsTableListener);
            evt.consume();
        }
    }
}//GEN-LAST:event_DataOptionSQLAdd

    /**
     *
     * @param sql
     */
    private boolean executeQuery(String sql) {
        return ((QueryTableModel) validationsListItems.getModel()).setQuery(sql);
    }

    /**
     *
     * @param itemsTable
     * @param criteriaTable
     */
    public static void buildCriteriaModel(JTable itemsTable, JTable criteriaTable) {
        QueryTableModel tableModel = (QueryTableModel) itemsTable.getModel();
        HashMap<String, String> sqlParts = tableModel.getSqlParts();
        // Create a new table model when the validation selection changes.
        ValidationsTableCriteriaModel newModel = new ValidationsTableCriteriaModel(tableModel.getDesignSQL(), tableModel.getDesignTimeColumnsAsArray(), 3);
        criteriaTable.setModel(newModel);
        // Populate the criteria in the model from the WHERE clause.
        addCriteriaFromWhere(sqlParts, (ValidationsCriteriaModel)criteriaTable.getModel());
    }

    /**
     *
     * @param itemsTable
     * @param displayColumnsTable
     */
    public static void buildDisplayColumnsModel(JTable itemsTable, JTable displayColumnsTable) {
        QueryTableModel tableModel = (QueryTableModel) itemsTable.getModel();
        // Create a new display columns table model when the validation selection changes.
        ValidationsTableDisplayColumnsModel newModel = new ValidationsTableDisplayColumnsModel(tableModel.getDesignSQL(), tableModel.getDesignTimeColumnsAsArray(), 1);
        displayColumnsTable.setModel(newModel);
    }
    /**
     *
     * @param sqlParts
     * @param criteriaModel
     */
    public static void addCriteriaFromWhere(HashMap<String, String> sqlParts, ValidationsCriteriaModel criteriaModel) {
        String where = sqlParts.get(WHERE_NAME);
        // Clear all previous criteria.
        for (int cr = 0; cr < 3; cr++) {
            for (int ci = 0; ci < criteriaModel.getColumnCount(); ci++) {
                criteriaModel.setValueAt(EMPTY_STRING, cr, ci);
            }
        }

        if (!(where == null)) {
            String restOfWhere;
            String value;
            int startPos;  //HOW ABOUT POSITION OF ANDS?
            //include the format that uses square brackets
            //String columnsPattern = "(?i)( *[\\w]+\\.[\\w]+)(?=[=<>]| *[=<>]|\\b(<>|.*like|.*in)\\b)";
            String columnsPattern = COLUMNS_REGEX;
            Pattern regex = Pattern.compile(columnsPattern);
            // Get the rows that are OR'd
            String[] rows = where.split(SPLIT_OR_REGEX);
            for (int r = 0; r < rows.length; r++) {
                // Get the columns that are AND'd
                String[] cols = rows[r].split(SPLIT_AND_REGEX);
                for (int c = 0; c < cols.length; c++) {
                    Matcher regexMatcher = regex.matcher(cols[c]);
                    while (regexMatcher.find()) {
                        for (int i = 0; i < criteriaModel.getColumnCount(); i++) {
                            try {
                                if (criteriaModel.getPhysicalColumn(i).equalsIgnoreCase(regexMatcher.group().trim())) {
                                    startPos = regexMatcher.end();
                                    restOfWhere = cols[c].substring(startPos);
                                    value = restOfWhere.substring(0, restOfWhere.indexOf(CLOSE_PAREN));
                                    criteriaModel.setValueAt(value, r, i);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param table
     */
    private void stopEditing(JTable table) {
        int column = table.getEditingColumn();
        if (column > -1) {
            TableCellEditor cellEditor = table.getColumnModel().getColumn(column).getCellEditor();
            if (cellEditor == null) {
                cellEditor = table.getDefaultEditor(table.getColumnClass(column));
            }
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
        }
    }

private void miListDnDSortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miListDnDSortActionPerformed
}//GEN-LAST:event_miListDnDSortActionPerformed

private void validationsSQLEditKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsSQLEditKeyTyped
}//GEN-LAST:event_validationsSQLEditKeyTyped

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    AddTables(tableList);
}//GEN-LAST:event_formWindowOpened

private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
    doClose(RET_OK);
}//GEN-LAST:event_btnOkActionPerformed

private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
    // Generate SQL
    if (tableLayoutPanel.getComponentCount() == 0) {
        JOptionPane.showMessageDialog(null, SELECT_TABLE_MSG);
    } else {
        String sql = createSQLStatement(tableLayoutPanel, orderBy.getText(), btnDESC.isSelected(), groupBy.getText());
        if (EMPTY_STRING.equals(sql)) {
            JOptionPane.showMessageDialog(null, SELECT_COLUMN_MSG);
        } else {
            validationsSQLEdit.setText(sql);
        }
    }
}//GEN-LAST:event_jLabel1MouseClicked
   
    public static String createValidationRecord(String uid, String name, String desc, String type, String sqlText, String designTimeColumns, String stored) {
        String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME, SQLBuilder.EMPTY_STRING);
        DatabaseConnection con = DBConnections.getConnection();

        if (!(con == null)) {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    StringBuilder sql = new StringBuilder("insert into validation_lists (validation_list_id, list_name, " +
                            "description, validation_type, sql_statement, DesignTimeColumns, stored_column, start_date, " +
                            "create_dt, createuser_c, touch_date, touch_user)");
                    sql.append(" values (?, ?, ?, ?, ?, ?, ?, getdate(), getdate(), ?, getdate(), ?)");
                    PreparedStatement stmt = conn.prepareStatement(sql.toString());
                    stmt.setString(1, uid);
                    stmt.setString(2, name);
                    stmt.setString(3, desc);
                    stmt.setString(4, type);
                    stmt.setString(5, sqlText);
                    stmt.setString(6, designTimeColumns);
                    stmt.setString(7, stored);
                    stmt.setString(8, dbuser);
                    stmt.setString(9, dbuser);
                    try {
                        stmt.executeUpdate();
                    } finally {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    return ex.getMessage();
                }
            }
        } else {
            return SQLBuilder.DB_CONN_SETUP_ERROR;
        }
        
        return SQLBuilder.EMPTY_STRING;
    }

    public static String updateValidationRecord(String uid, String name, String desc, String designTimeColumns, String sqlText, String stored) {
        String dbuser = NbPreferences.forModule(DesignerPanel.class).get(SQLBuilder.DBUSERNAME, SQLBuilder.EMPTY_STRING);
        DatabaseConnection con = DBConnections.getConnection();

        if (!(con == null)) {
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    StringBuilder sql = new StringBuilder("update validation_lists set list_name = ?, description = ? ");
                    if (!EMPTY_STRING.equals(designTimeColumns)) {
                        sql.append(", DesignTimeColumns = ? ");
                    }
                    if (!EMPTY_STRING.equals(sqlText)) {
                        sql.append(", sql_statement = ? ");
                    }
                    if (!EMPTY_STRING.equals(stored)) {
                        sql.append(", stored_column = ? ");
                    }
                    sql.append(", start_date = getDate(), touch_date = getDate(), touch_user = ? ");
                    sql.append(" where validation_list_id = ? ");
                    PreparedStatement stmt = conn.prepareStatement(sql.toString());
                    int idx = 1;
                    stmt.setString(idx++, name);
                    stmt.setString(idx++, desc);
                    if (!EMPTY_STRING.equals(designTimeColumns)) {
                        stmt.setString(idx++, designTimeColumns);
                    }
                    if (!EMPTY_STRING.equals(sqlText)) {
                        stmt.setString(idx++, sqlText);
                    }
                    if (!EMPTY_STRING.equals(stored)) {
                        stmt.setString(idx++, stored);
                    }
                    stmt.setString(idx++, dbuser);
                    stmt.setString(idx++, uid);
                    stmt.executeUpdate();
                    stmt.close();
                } catch (SQLException ex) {
                    return ex.getMessage();
                }
            }
        } else {
            return SQLBuilder.DB_CONN_SETUP_ERROR;
        }
        
        return SQLBuilder.EMPTY_STRING;
    }
    
    /**
     *
     * @param tableLayout
     * @param orderby
     * @param DESC
     * @param groupby
     * @return
     */
    public static String createSQLStatement(JPanel tableLayout, String orderby, boolean DESC, String groupby) {
        String sqlStatement = EMPTY_STRING;
        String select = SELECT_PREFIX_TAB;
        String from = FROM_PREFIX_TAB;
        String join = JOIN_PREFIX_TAB;

        if (!(EMPTY_STRING.equals(groupby))) {
            groupby = GROUP_PREFIX_TAB + groupby;
        }
        if (!(EMPTY_STRING.equals(orderby))) {
            orderby = ORDER_PREFIX_TAB + orderby;
            if (DESC) {
                orderby = orderby + DESC_PREFIX;
            } else {
                orderby = orderby + ASC_PREFIX;
            }
        }

        try {
            for (int i = 0; i < tableLayout.getComponentCount(); i++) {
                if (tableLayout.getComponent(i) instanceof SQLTableVisualComponent) {
                    SQLTableVisualComponent comp = (SQLTableVisualComponent) tableLayout.getComponent(i);
                    select = select + comp.getSelect();
                    from = from + comp.getFrom();
                    join = join + comp.getJoin();
                }
            }
        } catch (Exception ex) {
            return EMPTY_STRING;
        }
        sqlStatement = sqlStatement + select + from + groupby + orderby;
        return sqlStatement;
    }

private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
    // Clear SQL
    validationsSQLEdit.setText(EMPTY_STRING);
}//GEN-LAST:event_jLabel7MouseClicked

    /**
     *
     * @param sql
     * @param sqlParts
     * @return
     */
    public static boolean breakSqlIntoParts(String sql, HashMap<String, String> sqlParts) {
        boolean syntaxError = false;
        sqlParts.clear();
        try {
            int sqlStart;
            int sqlEnd;
            String name;
            boolean haveAnotherMatch;

            Pattern regex = Pattern.compile(SQL_PARTS_REGEX);
            Matcher regexMatcher = regex.matcher(sql);

            haveAnotherMatch = regexMatcher.find();

            while (haveAnotherMatch) {
                name = regexMatcher.group().toUpperCase();
                sqlStart = regexMatcher.end();
                haveAnotherMatch = regexMatcher.find();
                while (haveAnotherMatch && (regexMatcher.group().equals(name))) {
                    haveAnotherMatch = regexMatcher.find();
                }
                sqlEnd = haveAnotherMatch ? regexMatcher.start() : sql.length();
                // Remove start/end whitespace
                sqlParts.put(name, sql.substring(sqlStart, sqlEnd).replaceAll(SQL_REPLACE_REGEX, EMPTY_STRING));
            }

        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
            JOptionPane.showMessageDialog(null, ex);
            syntaxError = true;
        }
        return syntaxError;
    }

    /**
     *
     * @param sql
     * @param dataOptionList
     * @param dataOptionListCriteria
     * @return
     */
    public static String rebuildOrderBy(String sql, JTable dataOptionList, JTable dataOptionListCriteria) {
        int orderStartPos = 0;
        int orderEndPos = sql.length();
        String orderCol;
        String orderDir;

        Pattern regex = Pattern.compile(ORDER_REGEX);
        Matcher regexMatcher = regex.matcher(sql);
        if (regexMatcher.find()) {
            orderStartPos = regexMatcher.start() - 1;
        }
        if (!(orderStartPos > 0)) {
            orderStartPos = orderEndPos;
        }
        String before = sql.substring(0, orderStartPos);
        String after = sql.substring(orderEndPos, sql.length());
        StringBuffer orderByClause = new StringBuffer(EMPTY_STRING);

        if (!((dataOptionList.getRowSorter() == null) || (dataOptionList.getRowSorter().getSortKeys().isEmpty()))) {
            // Have to subtract one here because the list model has the extra UID column.
            orderCol = ((ValidationsCriteriaModel) dataOptionListCriteria.getModel()).getPhysicalColumn(dataOptionList.getRowSorter().getSortKeys().get(0).getColumn() - 1);
            if (dataOptionList.getRowSorter().getSortKeys().get(0).getSortOrder().toString().equals(DESCENDING)) {
                orderDir = DESC_NAME;
            } else {
                orderDir = ASC_NAME;
            }
            orderByClause.append(ORDER_PREFIX_TAB).append(orderCol).append(SPACE).append(orderDir);
        }

        return before + orderByClause + after;
    }


    public static String rebuildSelectClause(String sql, String columns) {
        int selectEndPos = sql.length();
        Pattern regex = Pattern.compile(FROM_REGEX);
        Matcher regexMatcher = regex.matcher(sql);
        if (regexMatcher.find()) {
            selectEndPos = regexMatcher.start() - 1;
        }

        String after = sql.substring(selectEndPos, sql.length());

        StringBuilder selectClause = new StringBuilder(SELECT_PREFIX);
        selectClause.append(columns);

        return selectClause.toString() + after;
    }

    /**
     *
     * @param sql
     * @param model
     * @param additionalCriteria
     * @return
     */
    public static String rebuildWhereClause(String sql, ValidationsCriteriaModel model, String additionalCriteria) {
        int columnCount = model.getColumnCount();
        int whereStartPos = 0;
        int whereEndPos = sql.length();
        Pattern regex = Pattern.compile(WHERE_REGEX);
        Matcher regexMatcher = regex.matcher(sql);
        if (regexMatcher.find()) {
            whereStartPos = regexMatcher.start() - 1;
        }
        regex = Pattern.compile(GROUP_REGEX);
        regexMatcher = regex.matcher(sql);
        if (regexMatcher.find()) {
            whereEndPos = regexMatcher.start() - 1;
        } else {
            regex = Pattern.compile(ORDER_REGEX);
            regexMatcher = regex.matcher(sql);
            if (regexMatcher.find()) {
                whereEndPos = regexMatcher.start() - 1;
            }
        }
        if (!(whereStartPos > 0)) {
            whereStartPos = whereEndPos;
        }
        String before = sql.substring(0, whereStartPos);
        String after = sql.substring(whereEndPos, sql.length());
        // Store each row of and's
        ArrayList<ArrayList<String>> where = new ArrayList();
        StringBuffer whereClause = new StringBuffer(EMPTY_STRING);

        for (int i = 0; i < 3; i++) {
            ArrayList<String> row = new ArrayList();
            for (int j = 0; j < columnCount; j++) {
                if ((model.getValueAt(i, j) == null)
                        || (model.getValueAt(i, j).equals(EMPTY_STRING))) {
                    continue;
                }
                row.add(OPEN_PAREN + model.getPhysicalColumn(j) + SPACE
                        + model.getValueAt(i, j) + CLOSE_PAREN + NEWLINE);
            }
            where.add(row);
        }

        for (int i = 0; i < where.size(); i++) {
            ArrayList<String> row = where.get(i);
            if (row.size() > 0) {
                if (i == 0) {
                    whereClause.append(WHERE_PREFIX).append(additionalCriteria).append(OPEN_PAREN).append(NEWLINE);
                } else {
                    whereClause.append(OR_PREFIX).append(OPEN_PAREN).append(NEWLINE);
                }
                for (int j = 0; j < row.size(); j++) {
                    if ((j == 0) && (j == row.size() - 1)) {
                        whereClause.append(TAB).append(row.get(j)).append(CLOSE_PAREN).append(SPACE);
                    } else if (j == 0) {
                        whereClause.append(TAB).append(row.get(j));
                    } else if (j == row.size() - 1) {
                        whereClause.append(AND_PREFIX).append(row.get(j)).append(CLOSE_PAREN).append(SPACE);
                    } else {
                        whereClause.append(AND_PREFIX).append(row.get(j));
                    }
                }
            }
        }

        return before + whereClause + after;
    }

    /**
     *
     * @param tableLayoutPanel
     * @param tableName
     * @param selectedColumns
     * @param width
     * @param height
     */
    public static SQLTableVisualComponent addVisualTable(JPanel tableLayoutPanel, String tableName,
            String selectedColumns, int width, int height, 
            SQLTableVisualComponent.SQLTableVisualEvent removeEvent) {
        boolean alreadyHaveOne = false;
        SQLTableVisualComponent comp = null;
        if (tableName.equals(TABLE_HUMP)) {
            alreadyHaveOne = true;
        }
        if (!alreadyHaveOne) {
            for (int i = 0; i < tableLayoutPanel.getComponentCount(); i++) {
                if (tableLayoutPanel.getComponent(i) instanceof SQLTableVisualComponent) {
                    //Ticket #470
                    //Only allow one table at this time. Might need to support
                    //more than one table in the future
                    //comp = (SQLTableVisualComponent) tableLayoutPanel.getComponent(i);
                    //if ((comp.getTableName().equals(tableName)) || (tableName.equals("Table"))) {
                        alreadyHaveOne = true;
                    //}
                }
                if (alreadyHaveOne) {
                    break;
                }
            }
        }
        if (!(alreadyHaveOne)) {
            comp = new SQLTableVisualComponent(tableName, tableLayoutPanel,
                    selectedColumns, width, height, true);
            //Ticket #470
            comp.addRemoveEvent(removeEvent);
        }
        return comp;
    }

private void validationsListCriteriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListCriteriaKeyPressed
    //Ticket #45, #46, #51
    /* Moved to TableModelListener
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
    int row = validationsListCriteria.getSelectedRow();
    int col = validationsListCriteria.getSelectedColumn();
    stopEditing(validationsListCriteria);
    String value = (String) validationsListCriteria.getModel().getValueAt(row, col);

    ((QueryTableModel) validationsListItems.getModel()).setStoredFilter(row, col, value);
    validationsSQLEdit.setText(rebuildWhereClause(validationsSQLEdit.getText(), (ValidationsCriteriaModel) validationsListCriteria.getModel(), ""));
    executeQuery(validationsSQLEdit.getText());

    evt.consume();
    }
     *
     */
}//GEN-LAST:event_validationsListCriteriaKeyPressed

private void tableLayoutPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableLayoutPanelMouseDragged
}//GEN-LAST:event_tableLayoutPanelMouseDragged

private void tableListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableListKeyPressed
    keyPressed = true;
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        addVisualTable(tableLayoutPanel, (String) tableList.getSelectedItem(), "", 150, 150, null);
        keyPressed = false;
    }
}//GEN-LAST:event_tableListKeyPressed

private void tableListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableListActionPerformed
    if (!(keyPressed)) {
        addVisualTable(tableLayoutPanel, (String) tableList.getSelectedItem(), "", 150, 150, null);
    }
    keyPressed = false;
}//GEN-LAST:event_tableListActionPerformed

private void jLabel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseEntered
    jLabel1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_jLabel1MouseEntered

private void jLabel7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseEntered
    jLabel7.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}//GEN-LAST:event_jLabel7MouseEntered

private void orderByMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orderByMouseReleased
}//GEN-LAST:event_orderByMouseReleased

private void groupByKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_groupByKeyTyped
    noKeyTyped(evt);
}//GEN-LAST:event_groupByKeyTyped

private void orderByKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_orderByKeyTyped
    noKeyTyped(evt);
}//GEN-LAST:event_orderByKeyTyped

    private void validationsListDisplayColumnsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_validationsListDisplayColumnsKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_validationsListDisplayColumnsKeyPressed

    public static void noKeyTyped(java.awt.event.KeyEvent evt) {
        evt.consume();
    }

    /**
     *
     * @param list
     */
    public static void AddTables(JComboBox list) {
        DatabaseConnection con = DBConnections.getConnection();

        if (!(con == null)) {
            DefaultComboBoxModel tableListModel = new DefaultComboBoxModel();
            tableListModel.addElement(TABLE_HUMP);
            String[] tableTypes = {TABLE_UPPER, VIEW_UPPER};
            Connection conn = con.getJDBCConnection();
            if (!(conn == null)) {
                try {
                    DatabaseMetaData md = conn.getMetaData();
                    ResultSet rs = md.getTables(null, null, null, tableTypes);
                    while (rs.next()) {
                        tableListModel.addElement(rs.getString(TABLE_SCHEM) + PERIOD + rs.getString(TABLE_NAME));
                    }
                    rs.close();
                    // This connection should not be closed here...it is controlled through the DatabaseExplorer
                    //conn.close();
                } catch (SQLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            list.setModel(tableListModel);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton btnASC;
    private javax.swing.JButton btnCancel;
    private javax.swing.JRadioButton btnDESC;
    private javax.swing.ButtonGroup btnGroup;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel displayColumnsCaption;
    private javax.swing.JTextField groupBy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JMenuItem miDelete;
    private javax.swing.JCheckBoxMenuItem miListDnDSort;
    private javax.swing.JTextField orderBy;
    private javax.swing.JPopupMenu pmDataOptionList;
    private javax.swing.JPanel tableLayoutPanel;
    private javax.swing.JComboBox tableList;
    private javax.swing.JTable validationsListCriteria;
    private javax.swing.JTable validationsListDisplayColumns;
    private javax.swing.JTable validationsListItems;
    private javax.swing.JTextArea validationsSQLEdit;
    // End of variables declaration//GEN-END:variables
}
