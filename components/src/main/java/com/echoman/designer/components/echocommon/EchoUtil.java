/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echocommon;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.security.Provider;
import java.security.Security;
import java.sql.Clob;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Mutex;
import org.openide.windows.WindowManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.designer.components.echoborder.EchoBorderNodeData;
import com.echoman.designer.components.echodatacontainer.EchoDataContainerNodeData;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.designer.components.echointerfaces.IEchoComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataContainerNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.designer.components.echointerfaces.IEchoFormNodeData;
import com.echoman.designer.components.echoradiobutton.EchoRadioButtonNodeData;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.modules.InstalledFileLocator;
import org.openide.nodes.Node;

/**
 *
 * @author david.morin
 */
public class EchoUtil {

    public static final String APPNAME = "JDesiWebApp";
    // Changing to get actual location of JDesiWebApp which is different
    // while debugging in the IDE and at runtime.
    //        String jarPath = ".\\formdesigner\\JDesiWebApp\\WEB-INF\\lib\\";
    public static final File f = InstalledFileLocator.getDefault().locate("JDesiWebApp", "", false);
    public static final String APPSERVERDIR = f.getParent();
    //public static final String APPSERVERDIR = new File("").getAbsolutePath();
    public static final String WEBAPPROOT = APPSERVERDIR + "\\" + APPNAME + "\\";
    
    // Change to use Forms directory instead of webapp now
    // Change to static method instead to prevent multi-user/multi-thread issues.
    //public static final String USERDIR = (System.getenv("USERPROFILE") != null) ? System.getenv("USERPROFILE") : "";
    //public static final String FORMDIR = USERDIR + "\\FormDesignEHR\\Forms\\";

    public static final String FILEDIR = APPSERVERDIR +"\\" + APPNAME + "\\WEB-INF\\Files\\";
    //public static final String FORMDIR = WEBAPPROOT + "WEB-INF\\forms\\";
    public static final int SAVE_TO_FILE = 0;
    public static final int SAVE_TO_DB = 1;
    public static final int ARRAY_VALIDATION_DATA_POS = 0;
    public static final int ARRAY_VALIDATION_STORED_COL_POS = 1;
    public static final int ARRAY_VALIDATION_ID_POS = 2;
    public static final int ARRAY_VALIDATION_CRITERIA_POS = 3;
    public static final int ARRAY_VALIDATION_ORDER_POS = 4;
    public static final int ARRAY_VALIDATION_TYPE_POS = 5;
    private static ArrayList<Notification> notifications = new ArrayList<Notification>();
    private static HashMap<String, String> params = new HashMap<String, String>();

    // Ticket 33273
    private static final String SQL_DANGER_START = "Error in Query : Dangerous Syntax (";
    private static final String SQL_DANGER_END = ") Detected";

    private static final List<String> DANGER_LIST = new ArrayList<String>();
    
    static {
        DANGER_LIST.add("DROP ");
        DANGER_LIST.add("DELETE ");
        DANGER_LIST.add("CREATE ");
        DANGER_LIST.add("TRUNCATE ");
        
        DANGER_LIST.add("UPDATE ");
        DANGER_LIST.add("INSERT ");
        DANGER_LIST.add("EXEC ");
        DANGER_LIST.add("EXECUTE ");
        DANGER_LIST.add("ALTER ");
        DANGER_LIST.add(" SP_");
    }
    
    /**
     * Check the sql statement against a list of SQL nasties
     * 
     * @param sql
     * @throws ResourceException 
     */
    public static String dangerousSqlCheck(String sql) {
        String dangerError = null;
        for (String danger: DANGER_LIST) {
            if (sql.toUpperCase().contains(danger)){
                dangerError = SQL_DANGER_START + danger.trim() + SQL_DANGER_END;
                return dangerError;
            }
        }
        return "";
    }

    public static String getFormDir() {
        String formDir = (System.getenv("USERPROFILE") != null) ? System.getenv("USERPROFILE") : "";
        // CDT-505
        File directory = new File(formDir + "\\FormDesignEHR\\Forms\\");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return formDir + "\\FormDesignEHR\\Forms\\";
    }

    public static Rectangle locateSizeInGrid(Cursor cursor, Rectangle[][] grid, Point compLoc, int width, int height) {
        Point compPoint = (Point) compLoc.clone();
        String rectanglePoint = "";
        String dragCursor = "";

        // Get the component point we're working with based on the cursor
        if (cursor.equals(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))) {
            compPoint.x = compPoint.x + width;
            dragCursor = "E";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))) {
            compPoint.x = compPoint.x + width;
            dragCursor = "NE";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR))) {
            compPoint.x = compPoint.x + width;
            compPoint.y = compPoint.y + height;
            dragCursor = "SE";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR))) {
            dragCursor = "W";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))) {
            dragCursor = "NW";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))) {
            dragCursor = "N";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR))) {
            compPoint.y = compPoint.y + height;
            dragCursor = "SW";
        } else if (cursor.equals(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR))) {
            compPoint.y = compPoint.y + height;
            dragCursor = "S";
        }

        Rectangle gridRectangle = new Rectangle(0, 0, 0, 0);

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y].contains(compPoint)) {
                    gridRectangle = grid[x][y];
                    // No need to continue, we have our grid rectangle
                    // Which component point we used is based on the drage cursor
                    rectanglePoint = getClosestGridRectanglePoint(gridRectangle, compPoint);

                    Rectangle newBounds = getGridSizingBounds(rectanglePoint, dragCursor, gridRectangle, compPoint, compLoc, width, height);
                    int useWidth = width;
                    int useHeight = height;
                    if (newBounds.width < 7) {
                        useWidth = 7;
                    }
                    if (newBounds.height < 7) {
                        useHeight = 7;
                    }
                    if ((newBounds.width < 7) || (newBounds.height < 7)) {
                        return new Rectangle(compLoc.x, compLoc.y, useWidth, useHeight);
                    }
                    return newBounds;
                }
            }
        }
        return new Rectangle(compLoc.x, compLoc.y, width, height);
    }

    private static String getClosestGridRectanglePoint(Rectangle gridRectangle, Point compPoint) {
        HashMap<String, Integer> distances = new HashMap<String, Integer>();

        distances.put("tl", (int) Math.sqrt(Math.pow((gridRectangle.y - compPoint.y), 2) + Math.pow(gridRectangle.x - compPoint.x, 2)));
        distances.put("bl", (int) Math.sqrt(Math.pow((gridRectangle.y + gridRectangle.height - compPoint.y), 2) + Math.pow(gridRectangle.x - compPoint.x, 2)));
        distances.put("tr", (int) Math.sqrt(Math.pow((gridRectangle.y - compPoint.y), 2) + Math.pow(gridRectangle.x + gridRectangle.width - compPoint.x, 2)));
        distances.put("br", (int) Math.sqrt(Math.pow((gridRectangle.y + gridRectangle.height - compPoint.y), 2) + Math.pow(gridRectangle.x + gridRectangle.width - compPoint.x, 2)));

        Entry<String, Integer> min = null;
        for (Entry<String, Integer> entry : distances.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }

        return min.getKey();
    }

    private static Rectangle getGridSizingBounds(String rectanglePoint, String dragCursor, Rectangle gridRectangle, Point compPoint, Point compLoc, int width, int height) {
        int compX = compLoc.x;
        int compY = compLoc.y;
        // Don't adjust height when sizing left or right
        boolean adjHeight = (!(dragCursor.equals("E") || dragCursor.equals("W")));
        // Don't adjust width when sizing up or down
        boolean adjWidth = (!(dragCursor.equals("N") || dragCursor.equals("S")));
        // Don't adjust loc.x when sizing up down or to the right
        boolean adjX = (!(dragCursor.equals("S") || dragCursor.equals("N")
                || dragCursor.equals("SE") || dragCursor.equals("E") || dragCursor.equals("NE")));
        // Don't adjust loc.y when sizing left, right or down
        boolean adjY = (!(dragCursor.equals("S") || dragCursor.equals("SW")
                || dragCursor.equals("SE") || dragCursor.equals("E") || dragCursor.equals("W")));

        if (rectanglePoint.equals("tl")) {
            // If we are adjusting loc.x relative to the top/left grid rectangle
            // then the new loc.x will be moved to the left
            if (adjX) {
                compX = compX + (gridRectangle.x - compPoint.x);
            }
            // If we are adjusting loc.y relative to the top/left grid rectangle
            // then the new loc.y will be moved up
            if (adjY) {
                compY = compY + (gridRectangle.y - compPoint.y);
            }
            // If we are adjusting loc.y and the height relative to the top/left
            // grid rectangle then the new height will increase the same amount
            // that loc.y decreased
            if (adjHeight && adjY) {
                height = height + (compPoint.y - gridRectangle.y);
                // Else if we are just adjusting the height then the new height
                // will be decreasing, moving up to the gridRectangle.y
            } else if (adjHeight) {
                height = height + (gridRectangle.y - compPoint.y);
            }
            // If we are adjusting loc.x and the width relative to the top/left
            // grid rectangle then the new width will increase the same amount
            // that loc.x decreased
            if (adjWidth && adjX) {
                width = width + (compPoint.x - gridRectangle.x);
            } else if (adjWidth) {
                // Else if we are just adjusting the width then the new width
                // will be decreasing, moving left to the gridRectangle.x
                width = width + (gridRectangle.x - compPoint.x);
            }
        } else if (rectanglePoint.equals("bl")) {
            if (adjX) {
                compX = compX + (gridRectangle.x - compPoint.x);
            }
            if (adjY) {
                compY = compY + ((gridRectangle.y + gridRectangle.height) - compPoint.y);
            }
            // We're at the top
            if (adjHeight && adjY) {
                height = height + (compPoint.y - (gridRectangle.y + gridRectangle.height));
            } else if (adjHeight) {
                height = height + ((gridRectangle.y + gridRectangle.height) - compPoint.y);
            }
            if (adjWidth && adjX) {
                width = width + (compPoint.x - gridRectangle.x);
            } else if (adjWidth) {
                width = width + (gridRectangle.x - compPoint.x);
            }
        } else if (rectanglePoint.equals("tr")) {
            if (adjX) {
                compX = compX + ((gridRectangle.x + gridRectangle.width) - compPoint.x);
            }
            if (adjY) {
                compY = compY + (gridRectangle.y - compPoint.y);
            }
            if (adjHeight && adjY) {
                height = height + (compPoint.y - gridRectangle.y);
            } else if (adjHeight) {
                height = height + (gridRectangle.y - compPoint.y);
            }
            if (adjWidth && adjX) {
                width = width + (compPoint.x - (gridRectangle.x + gridRectangle.width));
            } else if (adjWidth) {
                width = width + ((gridRectangle.x + gridRectangle.width) - compPoint.x);
            }
        } else if (rectanglePoint.equals("br")) {
            if (adjX) {
                compX = compX + ((gridRectangle.x + gridRectangle.width) - compPoint.x);
            }
            if (adjY) {
                compY = compY + ((gridRectangle.y + gridRectangle.height) - compPoint.y);
            }
            // We're at the top
            if (adjHeight && adjY) {
                height = height + (compPoint.y - (gridRectangle.y + gridRectangle.height));
            } else if (adjHeight) {
                height = height + ((gridRectangle.y + gridRectangle.height) - compPoint.y);
            }
            if (adjWidth && adjX) {
                width = width + (compPoint.x - (gridRectangle.x + gridRectangle.width));
            } else if (adjWidth) {
                width = width + ((gridRectangle.x + gridRectangle.width) - compPoint.x);
            }
        }

        return new Rectangle(compX, compY, width, height);
    }

    public static Point locateComponentInGrid(Rectangle[][] grid, Point pt, int width, int height) {
        Point p1 = (Point) pt.clone();
        Point p2 = new Point(pt.x, pt.y + height);
        Point p3 = new Point(pt.x + width, pt.y);
        Point p4 = new Point(pt.x + width, pt.y + height);
        Rectangle r1 = new Rectangle(0, 0, 0, 0);
        Rectangle r2 = new Rectangle(0, 0, 0, 0);
        Rectangle r3 = new Rectangle(0, 0, 0, 0);
        Rectangle r4 = new Rectangle(0, 0, 0, 0);

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y].contains(p1)) {
                    r1 = grid[x][y];
                }
                if (grid[x][y].contains(p2)) {
                    r2 = grid[x][y];
                    // break out of the y loop
                    // once we have both left rectangles
                    break;
                }
                if (grid[x][y].contains(p3)) {
                    r3 = grid[x][y];
                }
                if (grid[x][y].contains(p4)) {
                    r4 = grid[x][y];
                    // No need to continue here, we have all four rectangles
                    // the four component points are in.
                    return getClosestPointInGridRectangles(r1, r2, r3, r4, p1, p2, p3, p4);
                }
            }
        }
        return getClosestPointInGridRectangles(r1, r2, r3, r4, p1, p2, p3, p4);
    }

    private static Point adjustComponentToClosestPoint(Rectangle gridRectangle, Point compLoc, Point compPoint, String rectanglePoint) {
        Point pt = (Point) compLoc.clone();

        if (rectanglePoint.equals("tl")) {
            pt.x = pt.x + (gridRectangle.x - compPoint.x);
            pt.y = pt.y + (gridRectangle.y - compPoint.y);
        } else if (rectanglePoint.equals("bl")) {
            pt.x = pt.x + (gridRectangle.x - compPoint.x);
            pt.y = pt.y + (gridRectangle.y + gridRectangle.height - compPoint.y);
        } else if (rectanglePoint.equals("tr")) {
            pt.x = pt.x + (gridRectangle.x + gridRectangle.width - compPoint.x);
            pt.y = pt.y + (gridRectangle.y - compPoint.y);
        } else if (rectanglePoint.equals("br")) {
            pt.x = pt.x + (gridRectangle.x + gridRectangle.width - compPoint.x);
            pt.y = pt.y + (gridRectangle.y + gridRectangle.height - compPoint.y);
        }

        return pt;
    }

    private static Point getClosestPointInGridRectangles(Rectangle r1, Rectangle r2, Rectangle r3,
            Rectangle r4, Point p1, Point p2, Point p3, Point p4) {

        HashMap<String, Integer> distances = new HashMap<String, Integer>();

        distances.put("p1_tl", (int) Math.sqrt(Math.pow((r1.y - p1.y), 2) + Math.pow(r1.x - p1.x, 2)));
        distances.put("p1_bl", (int) Math.sqrt(Math.pow((r1.y + r1.height - p1.y), 2) + Math.pow(r1.x - p1.x, 2)));
        distances.put("p1_tr", (int) Math.sqrt(Math.pow((r1.y - p1.y), 2) + Math.pow(r1.x + r1.width - p1.x, 2)));
        distances.put("p1_br", (int) Math.sqrt(Math.pow((r1.y + r1.height - p1.y), 2) + Math.pow(r1.x + r1.width - p1.x, 2)));
        distances.put("p2_tl", (int) Math.sqrt(Math.pow((r2.y - p2.y), 2) + Math.pow(r2.x - p2.x, 2)));
        distances.put("p2_bl", (int) Math.sqrt(Math.pow((r2.y + r2.height - p2.y), 2) + Math.pow(r2.x - p2.x, 2)));
        distances.put("p2_tr", (int) Math.sqrt(Math.pow((r2.y - p2.y), 2) + Math.pow(r2.x + r2.width - p2.x, 2)));
        distances.put("p2_br", (int) Math.sqrt(Math.pow((r2.y + r2.height - p2.y), 2) + Math.pow(r2.x + r2.width - p2.x, 2)));
        distances.put("p3_tl", (int) Math.sqrt(Math.pow((r3.y - p3.y), 2) + Math.pow(r3.x - p3.x, 2)));
        distances.put("p3_bl", (int) Math.sqrt(Math.pow((r3.y + r3.height - p3.y), 2) + Math.pow(r3.x - p3.x, 2)));
        distances.put("p3_tr", (int) Math.sqrt(Math.pow((r3.y - p3.y), 2) + Math.pow(r3.x + r3.width - p3.x, 2)));
        distances.put("p3_br", (int) Math.sqrt(Math.pow((r3.y + r3.height - p3.y), 2) + Math.pow(r3.x + r3.width - p3.x, 2)));
        distances.put("p4_tl", (int) Math.sqrt(Math.pow((r4.y - p4.y), 2) + Math.pow(r4.x - p4.x, 2)));
        distances.put("p4_bl", (int) Math.sqrt(Math.pow((r4.y + r4.height - p4.y), 2) + Math.pow(r4.x - p4.x, 2)));
        distances.put("p4_tr", (int) Math.sqrt(Math.pow((r4.y - p4.y), 2) + Math.pow(r4.x + r4.width - p4.x, 2)));
        distances.put("p4_br", (int) Math.sqrt(Math.pow((r4.y + r4.height - p4.y), 2) + Math.pow(r4.x + r4.width - p4.x, 2)));

        Entry<String, Integer> min = null;
        for (Entry<String, Integer> entry : distances.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }

        Point pt = (Point) p1.clone();
        if (min != null) {
            // The top/left corner point has the smallest distance to it's
            // grid rectangle point
            String rectanglePoint = min.getKey().substring(min.getKey().indexOf('_') + 1);
            if (min.getKey().contains("p1")) {
                pt = adjustComponentToClosestPoint(r1, pt, p1, rectanglePoint);
            } else if (min.getKey().contains("p2")) {
                pt = adjustComponentToClosestPoint(r2, pt, p2, rectanglePoint);
            } else if (min.getKey().contains("p3")) {
                pt = adjustComponentToClosestPoint(r3, pt, p3, rectanglePoint);
            } else if (min.getKey().contains("p4")) {
                pt = adjustComponentToClosestPoint(r4, pt, p4, rectanglePoint);
            }
        }

        return pt;
    }

    public static boolean isSelected(IEchoDesignerTopComponent designerPage, IEchoComponent component) {
        Node[] selectedNodes = designerPage.getMgr().getSelectedNodes();
        List comps = Arrays.asList(selectedNodes);
        if ((comps.contains(component.getNode()))
                || (component.getClass().getName().equals("EchoForm"))) {
            return true;
        }
        return false;
    }

    public static void fixZOrder(IEchoDesignerTopComponent designerPage, String parentId) {
        JPanel dropPanel = designerPage.getDropPanel(parentId);
        ArrayList<IEchoComponentNodeData> compList = designerPage.getCompList();
        SortedMap<String, IEchoComponentNodeData> containers = new TreeMap<String, IEchoComponentNodeData>();
        for (IEchoComponentNodeData cnd : compList) {
            if (((cnd.getComponent().getClass().getName().contains("EchoDataContainer"))
                    || (cnd.getComponent().getClass().getName().contains("EchoBorder")))
                    && ((cnd.getParentId() != null) && (cnd.getParentId().equals(parentId)))
                    && (!(((Component) cnd.getComponent()).getParent() == null))) {
                int h = ((Component) cnd.getComponent()).getBounds().height;
                int w = ((Component) cnd.getComponent()).getBounds().width;
                int perimeter = 2 * (h + w);
                String perim = Integer.toString(perimeter);
                // Container dimensions should never be smaller than 3 digits
                // or larger than 4 digits, so this will keep the order correct.
                if (perim.length() < 4) {
                    perim = '0' + perim;
                }
                containers.put(perim + cnd.getName(), cnd);
            } 
        }
        int startValue = 0;
        if (dropPanel.getComponentCount() > 1) {
            startValue = dropPanel.getComponentCount() - containers.size();
        }
        for (Map.Entry<String, IEchoComponentNodeData> entry : containers.entrySet()) {
            IEchoComponentNodeData nodeData = entry.getValue();
            if (nodeData.getClass().getName().contains("EchoDataContainer")) {
                dropPanel.setComponentZOrder(((JComponent) ((EchoDataContainerNodeData) nodeData).getComponent()), startValue);
                ((EchoDataContainerNodeData) nodeData).setNewZOrder(startValue);
                startValue++;
            } else if (nodeData.getClass().getName().contains("EchoBorder")) {
                dropPanel.setComponentZOrder(((JComponent) ((EchoBorderNodeData) nodeData).getComponent()), startValue);
                ((EchoBorderNodeData) nodeData).setNewZOrder(startValue);
                startValue++;
            }
        }
        if (!containers.isEmpty()) {
            dropPanel.repaint();
        }
    }

    public static String getDataTypeString(String dataType) {
        String type = "";
        //make sure dataType does not contains the data size eg. varchar(10)
        int pos = dataType.indexOf("(");
        if (pos != -1) {
            dataType = dataType.substring(0, pos);
        }
        if ((dataType.equalsIgnoreCase("char"))
                || (dataType.equalsIgnoreCase("varchar"))
                || (dataType.equalsIgnoreCase("text"))
                || (dataType.equalsIgnoreCase("uniqueidentifier"))
                || (dataType.equalsIgnoreCase("longvarchar"))) {
            type = "String";
        } else if (dataType.equalsIgnoreCase("datetime")
                || (dataType.equalsIgnoreCase("smalldatetime")) 
                || dataType.equalsIgnoreCase("timestamp")) {
            type = "Timestamp";
        } else if (dataType.equalsIgnoreCase("time")) {
            type = "Time";
        } else if (dataType.equalsIgnoreCase("date")) {
            type = "Date";
        } else if ((dataType.equalsIgnoreCase("numeric"))
                || (dataType.equalsIgnoreCase("numeric()"))
                || (dataType.equalsIgnoreCase("double"))
                || (dataType.equalsIgnoreCase("decimal"))
                || (dataType.equalsIgnoreCase("money"))) {
            type = "double";
        } else if ((dataType.equalsIgnoreCase("int"))
                || (dataType.equalsIgnoreCase("smallint"))) {
            type = "int";
        } else if (dataType.equalsIgnoreCase("bit")) {
            type = "boolean";
        } else if ((dataType.equalsIgnoreCase("real")) || (dataType.equalsIgnoreCase("float"))) {
            type = "float";
        } else if (dataType.equalsIgnoreCase("bigint")) {
            type = "long";
        } else if (dataType.equalsIgnoreCase("longvarbinary")) {
            type = "byte[]";
        } else {
            type = dataType;
        }
        return type;
    }

    public static String getClassString(Class clz) {
        if (clz.equals(Double.class)) {
            return "double";
        } else if (clz.equals(Float.class)) {
            return "float";
        } else if (clz.equals(Integer.class)) {
            return "int";
        } else if (clz.equals(Long.class)) {
            return "long";
        } else if (clz.equals(Date.class)) {
            return "Date";
        } else if (clz.equals(Time.class)) {
            return "Time";
        } else if (clz.equals(Timestamp.class)) {
            return "Timestamp";
        } else if (clz.equals(Boolean.class)) {
            return "boolean";
        } else {
            return "String";
        }
    }

    public static void resetSizeNoAlign(JComponent component, String text) {
        /* Dave S thinks this is non-standard to resize when the font
         * changes, the user should control that.
         * Leaving the code in place in case that changes.
        FontMetrics fm = component.getFontMetrics(component.getFont());
        Rectangle2D rect = fm.getStringBounds(text, component.getGraphics());
        Double newHeight = rect.getHeight();
        Double newWidth = rect.getWidth();
        int chgHeight = newHeight.intValue();
        int chgWidth = newWidth.intValue() + 10;
        if (chgHeight <= component.getHeight()) {
        chgHeight = component.getHeight();
        }
        if (chgWidth <= component.getWidth()) {
        chgWidth = component.getWidth();
        }
        component.setSize(chgWidth, chgHeight);
         */
    }

    public static void resetSize(JComponent component, String text) {
        /* Dave S thinks this is non-standard to resize when the font
         * changes, the user should control that
         * Leaving the code in place in case that changes.
        String snap = "on";
        if (!NbPreferences.forModule(MiscPanel.class).getBoolean("snapOn", true)) {
        snap = "off";
        }
        FontMetrics fm = component.getFontMetrics(component.getFont());
        Rectangle2D rect = fm.getStringBounds(text, component.getGraphics());
        Double newHeight = rect.getHeight();
        Double newWidth = rect.getWidth();
        int chgHeight = newHeight.intValue();
        int chgWidth = newWidth.intValue() + 20;
        if (chgHeight <= component.getHeight()) {
        chgHeight = component.getHeight();
        }
        if (chgWidth <= component.getWidth()) {
        chgWidth = component.getWidth();
        } else if (snap.equals("on")) {
        component.setLocation(new Point(component.getX() - (chgWidth - component.getWidth()), component.getY()));
        }
        component.setSize(chgWidth, chgHeight);
         */
    }

    public static void showNotification(String title, String message) {
        Notification n = (Notification) NotificationDisplayer.getDefault().notify(title,
                new ImageIcon(EchoUtil.class.getResource("infoicon.png")), message + " - " + Calendar.getInstance().getTime().toString(), null);
        notifications.add(n);
    }

    public static void clearNotifications() {
        Iterator<Notification> it = notifications.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
        notifications.clear();
    }

    public static String encryptDecrypt(String oldstring, String eORd) {
        Provider sunJce = new com.sun.crypto.provider.SunJCE();
        Security.addProvider(sunJce);
        byte[] salt = {
            (byte) 0xA0, (byte) 0x88, (byte) 0x08, (byte) 0xF1,
            (byte) 0x23, (byte) 0xDD, (byte) 0x96, (byte) 0xBF
        };
        int count = 69;
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
        char[] keypw = {'r', 'u', 'n', 'g', '-', 'g', 'u', 'n', 'g', 'a', '-', 'g', 'u', 'n', 'g'};
        PBEKeySpec pbeKeySpec = new PBEKeySpec(keypw);
        try {
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey skey = keyFac.generateSecret(pbeKeySpec);
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            if (eORd.equalsIgnoreCase("ENCRYPT")) {
                pbeCipher.init(Cipher.ENCRYPT_MODE, skey, pbeParamSpec);
                byte[] cleartext = oldstring.getBytes();
                byte[] ciphertext = pbeCipher.doFinal(cleartext);
                return bytesToHexString(ciphertext);
            } else {
                pbeCipher.init(Cipher.DECRYPT_MODE, skey, pbeParamSpec);
                byte[] ciphertext = hexStringToBytes(oldstring);
                byte[] cleartext = pbeCipher.doFinal(ciphertext);
                return new String(cleartext);
            }
        } catch (Exception e) {
            return "Exception caught: " + e.getMessage();
        }
    }

    private static String bytesToHexString(byte[] bs) {
        char[] hexchars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer sb = new StringBuffer();
        int temp;
        for (int i = 0; i < bs.length; i++) {
            temp = bs[i] + 128;
            sb.append(hexchars[temp / 16]);
            temp -= (temp / 16) * 16;
            sb.append(hexchars[temp]);
        }
        return sb.toString();
    }

    private static byte[] hexStringToBytes(String str) {
        byte[] bs = new byte[str.length() / 2];
        int i = 0;
        while (i < str.length()) {
            bs[i / 2] = new Integer(Integer.parseInt(str.substring(i, i + 2), 16) - 128).byteValue();
            i += 2;
        }
        return bs;
    }

    public static void addParam(String paramName, String paramValue) {
        if (!params.containsKey(paramName)) {
            params.put(paramName, paramValue);
        }
    }

    public static String getParamValue(String paramName) {
        String s = "";
        if (params.containsKey(paramName)) {
            s = params.get(paramName);
        }
        return s;
    }

    public static boolean hasParam(String paramName) {
        return params.containsKey(paramName);
    }

    public static boolean isRunningAsEchoAdmin() {
        return hasParam(EchoOption.PARAM_ECHO);
    }

    public static void changeCursorWaitStatus(final boolean isWaiting) {
        Mutex.EVENT.writeAccess(new Runnable() {

            @Override
            public void run() {
                try {
                    JFrame mainFrame =
                            (JFrame) WindowManager.getDefault().getMainWindow();
                    Component glassPane = mainFrame.getGlassPane();
                    if (isWaiting) {
                        glassPane.setVisible(true);
                        glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    } else {
                        glassPane.setVisible(false);
                        glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public static List<String> getJarClasses(String jarName) throws Exception {
        return getJarClasses(jarName, "");
    }

    public static List<String> getJarClasses(String jarName, String packageName) throws Exception {
        ArrayList classes = new ArrayList();
        JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
        JarEntry jarEntry = null;

        // Ticket 33124
        String agency;
        try {
            agency = DBConnections.getAgencyProperty();
        } catch (SQLException ex) {
            agency = "";
            Logger.getLogger(EchoUtil.class.getName()).log(Level.SEVERE, "Error checking for Agency Code.\n" + ex.getMessage());
        }

        while (true) {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry == null) {
                break;
            }

            // All agency specific packages should be prefixed with "jdesilib" and contain the Agency Code.
            boolean isAgencyClass = (!agency.equals("") && !jarEntry.isDirectory()
                    && jarEntry.getName().toLowerCase().startsWith("jdesilib") 
                    && jarEntry.getName().toLowerCase().contains(agency.toLowerCase()));
                        
            // To support legacy certcode package.
            // All new core custom code packages should be prefixed with jdesilibcore.
            boolean isEchoClass = (!jarEntry.isDirectory() 
                    && (jarEntry.getName().toLowerCase().startsWith("certcode/certcode")
                    || jarEntry.getName().toLowerCase().startsWith("jdesilibcore")));
            
            if (isEchoClass || isAgencyClass) {
                if ("".equals(packageName)) {
                    if (jarEntry.getName().endsWith(".class")) {
                        String cls = jarEntry.getName().replaceAll("/", "\\.");
                        classes.add(cls.substring(0, cls.length() - 6));
                    }
                } else {
                    packageName = packageName.replaceAll("\\.", "/");
                    if ((jarEntry.getName().startsWith(packageName))
                            && (jarEntry.getName().endsWith(".class"))) {
                        String cls = jarEntry.getName().replaceAll("/", "\\.");
                        classes.add(cls.substring(0, cls.length() - 6));
                    }
                }
            }
        }
        Collections.sort(classes);
        return classes;
    }

    public static String[] getJars(String path) throws Exception {
        return getJars(path, null);
    }

    public static String[] getJars(String path, final String[] excludedJars) throws Exception {
        File dir = new File(path);
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (excludedJars == null) {
                    return name.endsWith(".jar");
                } else {
                    boolean isJar = name.endsWith(".jar");
                    if (isJar) {
                        for (String jar : excludedJars) {
                            if (jar.equalsIgnoreCase(name)) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };
        String[] jars = dir.list(filter);
        Arrays.sort(jars);
        return jars;
    }

    public static String[] getJdesiJars(String path, final String agency) throws Exception {
        File dir = new File(path);
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                // Ticket 33124
                boolean isAgency = (!agency.equals("") 
                        && name.toLowerCase().startsWith("jdesilib") 
                        && name.toLowerCase().contains(agency.toLowerCase()));
                        
                boolean isEcho = (name.toLowerCase().startsWith("jdesilibcert")
                        || name.toLowerCase().startsWith("jdesilibcore"));
                
                boolean isJar = name.endsWith(".jar");
                return (isJar && (isAgency || isEcho));
            }
        };
        
        String[] jars = dir.list(filter);
        Arrays.sort(jars);
        return jars;
    }

    public static List<String> getJarClassMethods(String path) throws Exception {
        return getJarClassMethods(path, null);
    }

    public static List<String> getJarClassMethods(String path, String[] excludedJars) throws Exception {
        ArrayList methods = new ArrayList();
        methods.add("");

        // Moved up - no need to do this every time.
        // Need to include the jdesiEchoCoreCustomLibrary package
        // in the class path so the class loader can find the base
        // class that users will extend.

        // JIRA EVO-46
        File dir = new File(path);
        File[] customcodecorefile = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("custom-code-core");
            }
        });
        // There should only be one of these.
        URL echoCoreUrl = null;
        if (customcodecorefile.length > 0) {
            echoCoreUrl = customcodecorefile[0].toURI().toURL();
        }

        String agency;
        try {
            agency = DBConnections.getAgencyProperty();
        } catch (SQLException ex) {
            agency = "";
           Logger.getLogger(EchoUtil.class.getName()).log(Level.SEVERE, "Error checking for Agency Code.\n" + ex.getMessage());
        }
        
        String[] jars = getJdesiJars(path, agency);
        for (String jar : jars) {
            try {
                StringBuilder fullName = new StringBuilder(path);
                fullName.append("/");
                fullName.append(jar);
                File jarFile = new File(fullName.toString());
                URL url = jarFile.toURI().toURL();
                URL[] urls = new URL[]{echoCoreUrl, url};
                List<String> classes = getJarClasses(fullName.toString());
                ClassLoader cl = new URLClassLoader(urls);

                for (String cls : classes) {
                    try {
                        Class clz = cl.loadClass(cls);
                        for (Method m : clz.getDeclaredMethods()) {
                            if (Modifier.isPublic(m.getModifiers())) {
                                try {
                                    StringBuilder mthd = new StringBuilder(cls);
                                    mthd.append(".");
                                    mthd.append(m.getName());
                                    // Do not inclue inner class methods.
                                    if (!mthd.toString().contains("$")) {
                                        // Ticket 32499
                                        if (mthd.toString().equals("certcode.CertCode.runStoredProcedure")) {
                                            methods.add(mthd.toString().concat("(your_schema, your_name)"));
                                        } else {
                                            methods.add(mthd.toString());
                                        }
                                    }
                                } catch (Exception em) {
                                    //ignore invalid methods
                                }
                            }
                        }
                    } catch (Exception ec) {
                        //ignore invalid class
                        // DEBUG ONLY
                        //System.out.println(ec.getMessage());
                    }
                }
            } catch (Exception e) {
                //ignore invalid jars
                // DEBUG ONLY
                //System.out.println(e.getMessage());
            }
        }
        Collections.sort(methods);
        return methods;
    }

    public static boolean isNullOrEmpty(String aStr) {
        return (aStr == null) || (aStr.isEmpty());
    }

    public static boolean isNullOrEmpty(Object aObj) {
        return (aObj == null) || (aObj.toString().isEmpty());
    }

    //Move from EchoTextFieldNodeData so can be reused in EchoColumnNodeData
    public static String[] parseValidationData(String validationData) {
        String id = "";
        String storedColumnName = "";
        String criteria = "";
        String order = "";
        String type = "";
        HashMap<String, String> data = new HashMap<String, String>();
        StringTokenizer tokens = new StringTokenizer(validationData, "~");
        while (tokens.hasMoreTokens()) {
            String key = "";
            String value = "";
            String pair = tokens.nextToken();
            String[] pairArr = pair.split("=");
            for (String s : pairArr) {
                if (isNullOrEmpty(s)) {
                    break;
                } else {
                    if (isNullOrEmpty(key)) {
                        key = s;
                    } else {
                        if (!isNullOrEmpty(value)) {
                            value = value + "=";
                        }
                        value = value + s;
                    }
                }
            }
            data.put(key, value);
        }
        if (!(data.get("id") == null)) {
            id = data.get("id");
        }
        if (!(data.get("storedColumnName") == null)) {
            storedColumnName = data.get("storedColumnName");
        }
        if (!(data.get("criteria") == null)) // Have to get rid of the line feeds when we store as a string.
        {
            criteria = data.get("criteria").replaceAll("\n", "");
        }
        if (!(data.get("order") == null)) {
            order = data.get("order").replaceAll("\n", "");
        }
        if (!(data.get("type") == null)) {
            type = data.get("type");
        }
        return new String[]{validationData, storedColumnName, id, criteria, order, type};
    }

    public static String[] getTables(IEchoComponentNodeData nodeData, String propertyName) {
        List<String> tableList = new ArrayList<String>();
        tableList.add("");
        try {
            tableList.addAll(DBConnections.getTables());
        } catch (SQLException ex) {
            final String msg = ex.getMessage() + "\n Setting the " + propertyName + " property.";
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, msg);
                }
            });
        }
        return tableList.toArray( new String[tableList.size()]);
    }

    public static HashMap<String, String> getTableColumns(IEchoComponentNodeData nodeData, String propertyName) {
        boolean justColumn = false;
        String tableName = "";
        LinkedHashMap<String, String> columnList = new LinkedHashMap<String, String>();
        columnList.put("","");

        // Just get the column name for the data containers.
        if ((propertyName.equals("Master Link Field"))
        || (propertyName.equals("Link Field"))
        || (propertyName.equals("Form Link Field"))) {
            justColumn = true;
        }

        if ((propertyName.equals("Master Link Field"))
            && (nodeData instanceof IEchoDataContainerNodeData)) {
            tableName = ((IEchoDataContainerNodeData)nodeData).getMasterTable();
        }else if ((propertyName.equals("Form Link Field"))
            && (nodeData instanceof IEchoFormNodeData)) {
            tableName = ((IEchoFormNodeData)nodeData).getTable();
        } else if (nodeData instanceof IEchoDataAwareComponentNodeData) {
            tableName = ((IEchoDataAwareComponentNodeData)nodeData).getTable();
        }

        if (!tableName.equals("")) {
            DatabaseConnection con = DBConnections.getConnection();
            if (!(con == null)) {
                String getColTableName = tableName.substring(tableName.indexOf('.') + 1);
                String getColTableSchema = tableName.substring(0, tableName.indexOf('.'));
                Connection conn = con.getJDBCConnection();
                List primaryKeys = new ArrayList();

                if (!(conn == null)) {
                    try {

                        DatabaseMetaData md = conn.getMetaData();

                        ResultSet rspk = md.getPrimaryKeys(null, getColTableSchema, getColTableName);
                        while (rspk.next()) {
                            primaryKeys.add(rspk.getString("COLUMN_NAME"));
                        }
                        rspk.close();


                        ResultSet rsc = md.getColumns(null, getColTableSchema, getColTableName, null);
                        while (rsc.next()) {
                            boolean addCol = false;
                            String colKey = "";
                            String colName = rsc.getString("COLUMN_NAME");
                            String colInfo = "";
                            int colSize = rsc.getInt("COLUMN_SIZE");
                            if (primaryKeys.contains(colName)) {
                                colKey = "pKey";
                            }

                            String colType = rsc.getString("TYPE_NAME");
                            for (String s : nodeData.getExpectedDataType()) {
                                if ((s.equalsIgnoreCase(colType))
                                        || (s.equalsIgnoreCase("form"))) {
                                    if ((nodeData.getExpectedSize() == -1)
                                            || (nodeData.getExpectedSize() == colSize)) {
                                        if (nodeData.getClass() == EchoRadioButtonNodeData.class) {
                                            int len = ((EchoRadioButtonNodeData) nodeData).getItemValueLength();
                                            if (len <= colSize) {
                                                addCol = true;
                                                break;
                                            }
                                        } else {
                                            addCol = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (addCol) {
                                if (!justColumn) {
                                    // Containers and Forms have just a column name
                                    String padColName = colName + ' ';
                                    if (colType.equalsIgnoreCase("varchar")
                                            || colType.equalsIgnoreCase("char")) {
                                        colType = colType + "(" + colSize + ")";
                                    }
                                    String padColType = colType + ' ';
                                    colInfo = padColName + padColType + colKey;
                                }
                            }
                            columnList.put(colName, colInfo);
                        }
                        rsc.close();
                       // This connection should not be closed here...it is controlled through the DatabaseExplorer
                        //conn.close();
                    } catch (SQLException ex) {
                        final String msg = ex.getMessage() + "\n Setting the Field property.";
                        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(null, msg);
                        }
                    });
                    }
                }
            }
        }
        return columnList;
    }
    
    public static String getPrimaryKeyForTable(String tableName) {
        String primaryKeys = "";
        if (!tableName.equals("")) {
            DatabaseConnection con = DBConnections.getConnection();
            if (!(con == null)) {
                String getColTableName = tableName.substring(tableName.indexOf('.') + 1);
                String getColTableSchema = tableName.substring(0, tableName.indexOf('.'));
                Connection conn = con.getJDBCConnection();

                if (!(conn == null)) {
                    try {
                        DatabaseMetaData md = conn.getMetaData();

                        ResultSet rspk = md.getPrimaryKeys(null, getColTableSchema, getColTableName);
                        while (rspk.next()) {
                            if (primaryKeys.equals("")) {
                                primaryKeys = rspk.getString("COLUMN_NAME");
                            } else {
                                primaryKeys = primaryKeys.concat(",").concat(rspk.getString("COLUMN_NAME"));
                            }
                        }
                        rspk.close();

                       // This connection should not be closed here...it is controlled through the DatabaseExplorer
                       //conn.close();
                    } catch (SQLException ex) {
                        final String msg = ex.getMessage() + "\n Setting the Field property.";
                        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(null, msg);
                        }
                    });
                    }
                }
            }
        }
        return primaryKeys;
    }

    // Ticket 31379, 31378
    public static StringBuilder createValidationScript(StringBuilder validationScript, String validationId) throws Throwable {
        DatabaseConnection con = DBConnections.getConnection();
        validationScript = removeExistingValidationValues(con, validationScript, validationId);
        validationScript = removeExistingValidationList(con, validationScript, validationId);
        validationScript = createValidationList(con, validationScript, validationId);
        validationScript = createValidationValues(con, validationScript, validationId);
        return validationScript;
    }
   
    private static StringBuilder removeExistingValidationValues(DatabaseConnection con, StringBuilder validationScript, String validationId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                // Just making sure the validation id is valid before we delete them in another database.
                StringBuilder sqlStatement = new StringBuilder("select top 1 1 from dbo.validation_list_values where validation_list_id = ");
                sqlStatement.append("'").append(validationId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.validation_list_values') IS NOT NULL\n");
                        buildSqlStatement.append("AND EXISTS (SELECT TOP 1 1 FROM dbo.validation_list_values\n");
                        buildSqlStatement.append("WHERE validation_list_id = '").append(validationId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("DELETE FROM dbo.validation_list_values\n");
                        buildSqlStatement.append("WHERE validation_list_id = '").append(validationId).append("'\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        validationScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }

        return validationScript;
    }
    
    private static StringBuilder removeExistingValidationList(DatabaseConnection con, StringBuilder validationScript, String validationId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                // Just making sure the validation id is valid before we delete them in another database.
                StringBuilder sqlStatement = new StringBuilder("select top 1 1 from dbo.validation_lists where validation_list_id = ");
                sqlStatement.append("'").append(validationId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.validation_lists') IS NOT NULL\n");
                        buildSqlStatement.append("AND EXISTS (SELECT TOP 1 1 FROM dbo.validation_lists\n");
                        buildSqlStatement.append("WHERE validation_list_id = '").append(validationId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("DELETE FROM dbo.validation_lists\n");
                        buildSqlStatement.append("WHERE validation_list_id = '").append(validationId).append("'\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        validationScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }

        return validationScript;
    }
    
    private static StringBuilder createValidationList(DatabaseConnection con, StringBuilder validationScript, String validationId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                StringBuilder sqlStatement = new StringBuilder("select * from dbo.validation_lists where validation_list_id = ");
                sqlStatement.append("'").append(validationId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("IF OBJECT_ID('dbo.validation_lists') IS NOT NULL\n");
                        buildSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.validation_lists\n");
                        buildSqlStatement.append("WHERE validation_list_id = '").append(validationId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("INSERT INTO dbo.validation_lists(\n");
                        buildSqlStatement.append("validation_list_id, validation_type, list_name, description\n");
                        buildSqlStatement.append(",sql_statement ");
                        // CDT-342 Missing limiting criteria when export validations script is run.
                        String designTimeColumns = rs.getString("DesignTimeColumns");
                        if (designTimeColumns != null) {
                            buildSqlStatement.append(",DesignTimeColumns ");
                        }
                        buildSqlStatement.append(", stored_column, start_date, end_date, create_dt\n");
                        buildSqlStatement.append(",createuser_c, touch_user, touch_date)\n");
                        buildSqlStatement.append("VALUES (\n");
                        buildSqlStatement.append("'").append(validationId).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("validation_type")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("list_name")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("description")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("sql_statement").replaceAll("'", "''")).append("'\n");
                        if (designTimeColumns != null) {
                            buildSqlStatement.append(",'").append(designTimeColumns.replaceAll("'", "''")).append("'\n");
                        }
                        buildSqlStatement.append(",'").append(rs.getString("stored_column")).append("'\n");

                        if (rs.getString("start_date") == null) {
                            buildSqlStatement.append(",").append(rs.getString("start_date")).append("\n");
                        } else {
                            buildSqlStatement.append(",'").append(rs.getString("start_date")).append("'\n");
                        }
                        if (rs.getString("end_date") == null) {
                            buildSqlStatement.append(",").append(rs.getString("end_date")).append("\n");
                        } else {
                            buildSqlStatement.append(",'").append(rs.getString("end_date")).append("'\n");
                        }
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(")\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        validationScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }
        return validationScript;
    }

    private static StringBuilder createValidationValues(DatabaseConnection con, StringBuilder validationScript, String validationId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                StringBuilder sqlStatement = new StringBuilder("select * from dbo.validation_list_values where validation_list_id = ");
                sqlStatement.append("'").append(validationId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    while (rs.next()) {
                        buildSqlStatement.append("IF OBJECT_ID('dbo.validation_list_values') IS NOT NULL\n");
                        buildSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.validation_list_values\n");
                        buildSqlStatement.append("WHERE validation_list_values_id = '").append(rs.getString("validation_list_values_id")).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("INSERT INTO dbo.validation_list_values(\n");
                        buildSqlStatement.append("validation_list_values_id, validation_list_id, value, translation\n");
                        buildSqlStatement.append(",start_date, end_date, create_dt\n");
                        buildSqlStatement.append(",createuser_c, touch_user, touch_date)\n");
                        buildSqlStatement.append("VALUES (\n");
                        buildSqlStatement.append("'").append(rs.getString("validation_list_values_id")).append("'\n");
                        buildSqlStatement.append(",'").append(validationId).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("value")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("translation")).append("'\n");
                        if (rs.getString("start_date") == null) {
                            buildSqlStatement.append(",").append(rs.getString("start_date")).append("\n");
                        } else {
                            buildSqlStatement.append(",'").append(rs.getString("start_date")).append("'\n");
                        }
                        if (rs.getString("end_date") == null) {
                            buildSqlStatement.append(",").append(rs.getString("end_date")).append("\n");
                        } else {
                            buildSqlStatement.append(",'").append(rs.getString("end_date")).append("'\n");
                        }
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(")\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                    }
                }
                validationScript.append(buildSqlStatement);
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }
        return validationScript;
    }

    private static HashMap<String, String> getTableColumnTypes(String tableNameWithSchema) throws SQLException {
        final LinkedHashMap<String, String> columnTypeList = new LinkedHashMap<>();
        if (!tableNameWithSchema.equals("")) {
            final DatabaseConnection databaseConnection = DBConnections.getConnection();
            if (databaseConnection != null) {
                final String[] schemaAndTableName = tableNameWithSchema.split("\\.");
                final String schemaName = schemaAndTableName[0];
                final String tableName = schemaAndTableName[1];
                final Connection connection = databaseConnection.getJDBCConnection();
                if (connection != null) {
                    try (final ResultSet resultSet = connection.getMetaData().getColumns(null, schemaName, tableName, null)) {
                        while (resultSet.next()) {
                            columnTypeList.put(resultSet.getString("COLUMN_NAME"), resultSet.getString("TYPE_NAME"));
                        }
                    }
                }
            }
        }
        return columnTypeList;
    }

    public static StringBuilder createTableScript(StringBuilder tableScript, String tableName) throws SQLException {
        String primaryKeys = getPrimaryKeyForTable(tableName);
        // Ticket 538
        if (!"".equals(primaryKeys)) {
            List<String> keys = Arrays.asList(primaryKeys.split("\\s*,\\s*"));
            DatabaseConnection con = DBConnections.getConnection();
            String sqlStatement = "select top 1 * from " + tableName;
            Statement stmt = con.getJDBCConnection().createStatement();
            ResultSet rs = stmt.executeQuery( sqlStatement );
            ResultSetMetaData rsmd = rs.getMetaData();
            Integer columnCount = rsmd.getColumnCount();
            StringBuilder tblScript = new StringBuilder();
            if ( columnCount > 0 ) {
                final Map<String, String> columnTypes = getTableColumnTypes(tableName);
                tblScript.append("IF OBJECT_ID('").append(tableName).append("') IS NULL\n");
                tblScript.append("BEGIN\n");
                tblScript.append("BEGIN TRANSACTION\n");
                tblScript.append("CREATE TABLE " ).append(tableName).append(" ( \n");

                for ( int cols = 1; cols <= columnCount; cols ++ ) {
                    if ( cols > 1 ) {
                        tblScript.append( ", " );
                    }
                    final String columnName = rsmd.getColumnLabel(cols);
                    final String columnType = columnTypes.get(columnName);
                    final String columnClass = rsmd.getColumnClassName(cols);
                    final Integer precision = rsmd.getPrecision(cols);
                    final Integer scale = rsmd.getScale(cols);
                    final Integer isNull = rsmd.isNullable(cols);

                    String nullString = " NULL";
                    if (isNull == ResultSetMetaData.columnNoNulls) {
                        nullString = " NOT NULL";
                    }
                    String keyString = "";
                    if (keys.contains(columnName)) {
                        keyString = " PRIMARY KEY";
                    }

                    tblScript.append( columnName ).append( " " ).append( columnType );

                    if ( (columnType.equals("char")) || (columnType.equals("varchar"))
                         || (columnType.equals("longvarchar")) 
                         || (columnType.equals("numeric")) || (columnType.equals("decimal"))) {
                        if (precision == Integer.MAX_VALUE) {
                            tblScript.append("( MAX");
                        } else if (precision != 0) {
                            tblScript.append("( ").append(precision);
                        }
                        if ( scale != 0) {
                            tblScript.append(",").append(scale).append( " )" );
                        } else {
                            tblScript.append(" )");
                        }
                    }            
                    tblScript.append(nullString).append(keyString).append("\n");
                } // for columns
                tblScript.append( " )\n" );
                tblScript.append("COMMIT TRANSACTION\n");
                tblScript.append("END\n");
                tblScript.append("GO\n\n");
            }        
            tableScript.append(tblScript);
        }        
        return tableScript;
    }

    public static StringBuilder createMoveFormScript(String formName) throws SQLException {
        StringBuilder moveFormScript = new StringBuilder();
        DatabaseConnection con = DBConnections.getConnection();
        String formId = getFormId(con, formName);
        moveFormScript = removeExistingFormsWithLocations(con, moveFormScript, formId);
        moveFormScript = createFormsScript(con, moveFormScript, formId);
        moveFormScript = createFormDefinitions(con, moveFormScript, formId);
        moveFormScript = createFormsWithLocationsScript(con, moveFormScript, formId);
        return moveFormScript;
    }

    private static StringBuilder removeExistingFormsWithLocations(DatabaseConnection con, StringBuilder moveFormScript, String formId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                // Just making sure the validation id is valid before we delete them in another database.
                StringBuilder sqlStatement = new StringBuilder("select top 1 1 from dbo.FormsWithLocations where Form = ");
                sqlStatement.append("'").append(formId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.FormsWithLocations') IS NOT NULL\n");
                        buildSqlStatement.append("AND EXISTS (SELECT TOP 1 1 FROM dbo.FormsWithLocations\n");
                        buildSqlStatement.append("WHERE Form = '").append(formId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("DELETE FROM dbo.FormsWithLocations\n");
                        buildSqlStatement.append("WHERE Form = '").append(formId).append("'\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        moveFormScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }

        return moveFormScript;
    }

    private static StringBuilder createFormsScript(DatabaseConnection con, StringBuilder moveFormScript,
                                                   String formId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                StringBuilder sqlStatement = new StringBuilder("select * from dbo.Forms where Form = ");
                sqlStatement.append("'").append(formId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.Forms') IS NOT NULL\n");
                        buildSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.Forms\n");
                        buildSqlStatement.append("WHERE Form = '").append(formId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("INSERT INTO dbo.Forms(\n");
                        buildSqlStatement.append("FormName, Form, LinkTableName, LinkColumnName\n");
                        buildSqlStatement.append(",FormCaption, Signable, FormType, AllowNew, IsModal\n");
                        buildSqlStatement.append(",PreventMultipleRecords, CreateDate, UpdateDate, CreateUser, UpdateUser)\n");
                        buildSqlStatement.append("VALUES (\n");
                        buildSqlStatement.append("'").append(rs.getString("FormName")).append("'\n");
                        buildSqlStatement.append(",'").append(formId).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("LinkTableName")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("LinkColumnName")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("FormCaption")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("Signable")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("FormType")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("AllowNew")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("IsModal")).append("'\n");
                        buildSqlStatement.append(",'").append(rs.getString("PreventMultipleRecords")).append("'\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(")\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("ELSE\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("UPDATE dbo.Forms \n");
                        buildSqlStatement.append("SET FormName=");
                        buildSqlStatement.append("'").append(rs.getString("FormName")).append("',\n");
                        buildSqlStatement.append("LinkTableName=");
                        buildSqlStatement.append("'").append(rs.getString("LinkTableName")).append("',\n");
                        buildSqlStatement.append("LinkColumnName=");
                        buildSqlStatement.append("'").append(rs.getString("LinkColumnName")).append("',\n");
                        buildSqlStatement.append("FormCaption=");
                        buildSqlStatement.append("'").append(rs.getString("FormCaption")).append("',\n");
                        buildSqlStatement.append("Signable=");
                        buildSqlStatement.append("'").append(rs.getString("Signable")).append("',\n");
                        buildSqlStatement.append("FormType=");
                        buildSqlStatement.append("'").append(rs.getString("FormType")).append("',\n");
                        buildSqlStatement.append("AllowNew=");
                        buildSqlStatement.append("'").append(rs.getString("AllowNew")).append("',\n");
                        buildSqlStatement.append("IsModal=");
                        buildSqlStatement.append("'").append(rs.getString("IsModal")).append("',\n");
                        buildSqlStatement.append("PreventMultipleRecords=");
                        buildSqlStatement.append("'").append(rs.getString("PreventMultipleRecords")).append("',\n");
                        buildSqlStatement.append("UpdateDate=getDate() \n");
                        buildSqlStatement.append("WHERE Form=");
                        buildSqlStatement.append("'").append(formId).append("'\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        moveFormScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }
        
        return moveFormScript;
    }
    
    private static StringBuilder createFormDefinitions(DatabaseConnection con, StringBuilder moveFormScript, String formId) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                StringBuilder sqlStatement = new StringBuilder("select * from dbo.FormDefinitions where Form = ");
                sqlStatement.append("'").append(formId).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (!(rs == null)) {
                    if (rs.next()) {
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("DECLARE @FormDefinition VARCHAR(MAX) \n");
                        buildSqlStatement.append("SET @FormDefinition= \n");
                        buildSqlStatement.append("'").append(rs.getString("FormDefinitionData")).append("'\n\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.FormDefinitions') IS NOT NULL\n");
                        buildSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.FormDefinitions\n");
                        buildSqlStatement.append("WHERE Form = '").append(formId).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("INSERT INTO dbo.FormDefinitions(\n");
                        buildSqlStatement.append("FormDefinition, Form, FormDefinitionData\n");
                        buildSqlStatement.append(",CreateDate, UpdateDate, CreateUser, UpdateUser)\n");
                        buildSqlStatement.append("VALUES (\n");
                        buildSqlStatement.append("'").append(rs.getString("FormDefinition")).append("'\n");
                        buildSqlStatement.append(",'").append(formId).append("'\n");
                        buildSqlStatement.append(",@FormDefinition\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(")\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("ELSE\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("UPDATE dbo.FormDefinitions \n");
                        buildSqlStatement.append("SET FormDefinitionData=@FormDefinition, UpdateDate=getDate() \n");
                        buildSqlStatement.append("WHERE Form=");
                        buildSqlStatement.append("'").append(formId).append("'\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                        moveFormScript.append(buildSqlStatement);
                    }
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }
        
        return moveFormScript;
    }

    private static StringBuilder createFormsWithLocationsScript(DatabaseConnection con, StringBuilder moveFormScript, String formId) throws SQLException {
        Statement stmtForm = null;
        ResultSet rsForm = null;
        
        try {
            stmtForm = con.getJDBCConnection().createStatement();
            if (!(stmtForm == null)) {
                StringBuilder buildSqlStatement = new StringBuilder();
                StringBuilder sqlStatement = new StringBuilder("select FormWithLocation, fl.FormLocation, Form, Location ");
                sqlStatement.append("from dbo.FormswithLocations fwl JOIN FormLocations fl ON fwl.FormLocation=fl.FormLocation ");
                sqlStatement.append("where Form = ");
                sqlStatement.append("'").append(formId).append("'");
                rsForm = stmtForm.executeQuery(sqlStatement.toString());
                if (!(rsForm == null)) {
                    while (rsForm.next()) {

                        // Make sure the location exist, if not create it.
                        buildSqlStatement = createParentLocations(con, buildSqlStatement, rsForm.getString("FormLocation"));
                        
                        // Create the FormsWithLocations record.
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("/*** WARNING: THIS SCRIPT WILL OVERWRITE ANY EXISTING DATA ***/\n");
                        buildSqlStatement.append("/*************************************************************/\n");
                        buildSqlStatement.append("IF OBJECT_ID('dbo.FormsWithLocations') IS NOT NULL\n");
                        buildSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.FormsWithLocations fwl \n");
                        buildSqlStatement.append("JOIN FormLocations fl ON fwl.FormLocation = fl.FormLocation \n");
                        buildSqlStatement.append("WHERE Form = '").append(formId).append("' ");
                        buildSqlStatement.append("AND Location = '").append(rsForm.getString("Location")).append("')\n");
                        buildSqlStatement.append("BEGIN\n");
                        buildSqlStatement.append("BEGIN TRANSACTION\n");
                        buildSqlStatement.append("DECLARE @formLocation CHAR(36)\n");
                        buildSqlStatement.append("SELECT @formLocation=FormLocation FROM FormLocations WHERE Location = '");
                        buildSqlStatement.append(rsForm.getString("Location")).append("'\n");
                        buildSqlStatement.append("INSERT INTO dbo.FormsWithLocations(\n");
                        buildSqlStatement.append("FormWithLocation, Form, FormLocation\n");
                        buildSqlStatement.append(",CreateDate, UpdateDate, CreateUser, UpdateUser)\n");
                        buildSqlStatement.append("VALUES (\n");
                        buildSqlStatement.append("'").append(rsForm.getString("FormWithLocation")).append("'\n");
                        buildSqlStatement.append(",'").append(formId).append("'\n");
                        buildSqlStatement.append(",@formLocation\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",getDate()\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(",'dba'\n");
                        buildSqlStatement.append(")\n");
                        buildSqlStatement.append("COMMIT TRANSACTION\n");
                        buildSqlStatement.append("END\n");
                        buildSqlStatement.append("GO\n\n");
                    }
                }

                moveFormScript.append(buildSqlStatement);
            }
        } finally {
            if (!(rsForm == null))
                rsForm.close();
            if (!(stmtForm == null))
                stmtForm.close();
        }
        
        return moveFormScript;
    }

    private static StringBuilder createParentLocations(DatabaseConnection con, StringBuilder buildSqlStatement, String parentLocation) throws SQLException {
        PreparedStatement stmtLocation = null;
        ResultSet rsLocation = null;
        String sqlStatementLocation = "select * from dbo.FormLocations where FormLocation = ?";
        
        try {
            // Get this location, if one exist, check for it to have a parent, then create the script.
            stmtLocation = con.getJDBCConnection().prepareStatement(sqlStatementLocation);
            if (!(stmtLocation == null)) {
                stmtLocation.setString(1, parentLocation);
                rsLocation = stmtLocation.executeQuery();
                if (!(rsLocation == null)) {
                    if (rsLocation.next()) {
                        String parentLoc = rsLocation.getString("ParentLocation");
                        // When the parent location = null, we have reached the root parent.
                        if (parentLoc != null) {
                            buildSqlStatement = createParentLocations(con, buildSqlStatement, parentLoc);
                        }
                        createFormLocationsScript(buildSqlStatement, rsLocation);
                    }
                }
            }
        } finally {
            if (!(rsLocation == null))
                rsLocation.close();
            if (!(stmtLocation == null))
                stmtLocation.close();
        }
        
        return buildSqlStatement;
    }
    
    // This must exist before we try to create the FormsWithLocations entry.
    private static StringBuilder createFormLocationsScript(StringBuilder buildSqlStatement, ResultSet rsLocation) throws SQLException {
        StringBuilder buildLocSqlStatement = new StringBuilder();
        buildLocSqlStatement.append("IF OBJECT_ID('dbo.FormLocations') IS NOT NULL\n");
        buildLocSqlStatement.append("AND NOT EXISTS (SELECT TOP 1 1 FROM dbo.FormLocations\n");
        buildLocSqlStatement.append("WHERE Location = '").append(rsLocation.getString("Location")).append("')\n");
        buildLocSqlStatement.append("BEGIN\n");
        buildLocSqlStatement.append("BEGIN TRANSACTION\n");
        buildLocSqlStatement.append("INSERT INTO dbo.FormLocations(\n");
        buildLocSqlStatement.append("FormLocation, Location, HasChildren\n");
        if (rsLocation.getString("ParentLocation") != null) {
            buildLocSqlStatement.append(",ParentLocation\n");
        }
        buildLocSqlStatement.append(",CreateDate, UpdateDate, CreateUser, UpdateUser)\n");
        buildLocSqlStatement.append("VALUES (\n");
        buildLocSqlStatement.append("'").append(rsLocation.getString("FormLocation")).append("'\n");
        buildLocSqlStatement.append(",'").append(rsLocation.getString("Location")).append("'\n");
        buildLocSqlStatement.append(",'").append(rsLocation.getString("HasChildren")).append("'\n");
        if (rsLocation.getString("ParentLocation") != null) {
            buildLocSqlStatement.append(",'").append(rsLocation.getString("ParentLocation")).append("'\n");
        }
        buildLocSqlStatement.append(",getDate()\n");
        buildLocSqlStatement.append(",getDate()\n");
        buildLocSqlStatement.append(",'dba'\n");
        buildLocSqlStatement.append(",'dba')\n");
        buildLocSqlStatement.append("COMMIT TRANSACTION\n");
        buildLocSqlStatement.append("END\n");
        buildLocSqlStatement.append("GO\n\n");
        buildSqlStatement.append(buildLocSqlStatement);
        return buildSqlStatement;
    }

    private static String getFormId(DatabaseConnection con, String formName) throws SQLException {
        String formId = "";
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.getJDBCConnection().createStatement();
            if (!(stmt == null)) {
                StringBuilder sqlStatement = new StringBuilder("SELECT Form FROM dbo.Forms WHERE FormName = ");
                sqlStatement.append("'").append(formName).append("'");
                rs = stmt.executeQuery(sqlStatement.toString());
                if (rs.next()) {
                    formId = rs.getString("Form");
                }
            }
        } finally {
            if (!(rs == null))
                rs.close();
            if (!(stmt == null))
                stmt.close();
        }
       
        return formId;
    }

    public static String getAlterDefaultColumnQuery(String tableName) {
        String colTableName = tableName.substring(tableName.indexOf('.') + 1);
        return "ALTER TABLE " + tableName + " ADD "
                + "id CHAR(36) CONSTRAINT PK_" + colTableName + " PRIMARY KEY DEFAULT dbo.NEWSMARTGUID(), "
                + "CreateUser CHAR(36) NOT NULL DEFAULT SYSTEM_USER, "
                + "UpdateUser CHAR(36) NOT NULL DEFAULT SYSTEM_USER, "
                + "CreateDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "UpdateDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP";
    }

    public static void updateFormFields(String formName, String allowNew, String preventMultiple) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBConnections.getConnection().getJDBCConnection()
                    .prepareStatement("UPDATE dbo.Forms SET AllowNew = ?, PreventMultipleRecords = ? WHERE FormName = ?");
            if (stmt != null) {
                stmt.setString(1, allowNew);
                stmt.setString(2, preventMultiple);
                stmt.setString(3, formName);
                stmt.executeUpdate();
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

}
