/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Font;
import javax.swing.table.TableColumn;
import com.echoman.designer.components.echobasenode.EchoBaseNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDataAwareComponentNodeData;
import com.echoman.designer.components.echointerfaces.IEchoDesignerTopComponent;
import com.echoman.jdesi.PopupFromFieldProperties;
import java.util.ArrayList;
import org.openide.nodes.Node.PropertySet;

import static com.echoman.designer.components.echotable.EchoColumnNode.PROPERTY_INDEX_READONLY;
import static com.echoman.designer.components.echotable.EchoColumnNode.PROPERTY_INDEX_REQUIRED;
import static com.echoman.designer.components.echotable.EchoColumnNode.PROPERTY_INDEX_VISIBLE;

/**
 *
 */
public class EchoColumnNodeData extends EchoBaseNodeData implements IEchoDataAwareComponentNodeData {

    private transient TableColumn tableColumn;
    private transient EchoTableNodeData tableNodeData;

    public EchoColumnNodeData(IEchoDesignerTopComponent designerPage) {
        super(designerPage);
    }

    //Ticket #502
    public EchoColumnNodeData(EchoTableNodeData tableNodeData, TableColumn tableColumn, EchoColumnData c, 
            IEchoDesignerTopComponent designerPage) {
        super(designerPage);
        component = c;
        this.tableColumn = tableColumn;
        this.tableNodeData = tableNodeData;
    }

    public EchoColumnData getCol() {
        return (EchoColumnData) component;
    }

    @Override
    public EchoBaseNodeData cloneData() {
        return this;
    }

    public ArrayList<PopupFromFieldProperties> getPopupFromFieldValue() {
        return getCol().getPopupFromFieldValue();
    }

    public void setPopupFromFieldValue(ArrayList<PopupFromFieldProperties> popupFromFieldValue) {
        getCol().setPopupFromFieldValue(popupFromFieldValue);
        designerPage.setModified(true);
    }

    public void setWidth(Integer width) {
        if (getCol().getWidth() != width) {
            getCol().setWidth(width);
            if (tableColumn != null) {
                tableColumn.setPreferredWidth(width);
            }
            designerPage.setModified(true);
        }
    }

    public int getWidth() {
        return getCol().getWidth();
    }

    public void setHeader(String header) {
        if (!getCol().getHeader().equals(header)) {
            getCol().setHeader(header);
            if (tableColumn != null) {
                tableColumn.setHeaderValue(header);
            }
            designerPage.setModified(true);
        }
    }

    @Override
    public String getName() {
        return getCol().getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        getCol().setName(name);
        designerPage.setModified(true);
    }

    public String getHeader() {
        return getCol().getHeader();
    }

    public void setMask(String mask) {
        getCol().setMask(mask);
        designerPage.setModified(true);
    }

    public String getMask() {
        return getCol().getMask();
    }

    public String getValidationData() {
        return getCol().getValidationData();
    }

    public void setValidationData(String validationData) {
        getCol().setValidationData(validationData);
        designerPage.setModified(true);
    }

    public String getValidationDescriptionSql() {
        return getCol().getValidationDescriptionSql();
    }

    public void setValidationDescriptionSql(String validationDescriptionSql) {
        getCol().setValidationDescriptionSql(validationDescriptionSql);
        designerPage.setModified(true);
    }

    public boolean isVisible() {
        return getCol().isVisible();
    }

    public void setVisible(boolean visible) {
        getCol().setVisible(visible);
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (visible) {
                if (getCol().isEditable()) {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
                } else {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
                }
            } else {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            }
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
    }

    public boolean isReadOnly() {
        return !getCol().isEditable();
    }

    public void setReadOnly(boolean readOnly) {
        getCol().setEditable(!readOnly);
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (readOnly) {
                set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
            } else {
                if (getCol().isVisible()) {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(false);
                } else {
                    set.getProperties()[PROPERTY_INDEX_REQUIRED].setHidden(true);
                }
            }
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
    }

    public boolean isRequired() {
        return getCol().isRequired();
    }

    public void setRequired(boolean required) {
        getCol().setRequired(required);
        if (!((component == null) || (component.getNode() == null))) {
            PropertySet set = component.getNode().getPropertySets()[1];
            if (required) {
                set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(true);
                set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(true);
            } else {
                set.getProperties()[PROPERTY_INDEX_VISIBLE].setHidden(false);
                set.getProperties()[PROPERTY_INDEX_READONLY].setHidden(false);
            }
            getComponent().getNode().restoreSheet();
            designerPage.setModified(true);
        }
    }

    public boolean isTimeZoneOffset() {
        return getCol().isTimeZoneOffset();
    }

    public void setTimeZoneOffset(boolean timeZoneOffset) {
        getCol().setTimeZoneOffset(timeZoneOffset);
        designerPage.setModified(true);
    }

    public void setDefaultValue(String defaultValue) {
        getCol().setDefaultValue(defaultValue);
        designerPage.setModified(true);
    }

    public String getDefaultValue() {
        return getCol().getDefaultValue();
    }

    public void setDisplayMask(String displayMask) {
        getCol().setDisplayMask(displayMask);
        designerPage.setModified(true);
    }

    public String getDisplayMask() {
        return getCol().getDisplayMask();
    }

    @Override
    public void setTop(Integer top) {
    }

    @Override
    public void setLeft(Integer left) {
    }

    @Override
    public int getTop() {
        return -1;
    }

    @Override
    public int getLeft() {
        return -1;
    }

    @Override
    public void updateName(int index) {
        designerPage.setModified(true);
    }

    @Override
    public String[] getExpectedDataType() {
        return new String[]{"char", "varchar", "longvarchar", "uniqueidentifier", "datetime", "date",
            "time", "int", "numeric", "numeric()", "double", "smalldatetime",
            "decimal", "tinyint", "smallint", "bigint", "real", "float", "money"};
    }

    @Override
    public int getExpectedSize() {
        return -1;
    }

    @Override
    public void initCreate() {
    }

    @Override
    public void setBorder() {
    }

    @Override
    public void clearUncopiableProperties(String table) {
        //not implemented
    }

    // Ticket 447
    public String getChangeEvent() {
        return getCol().getChangeEvent();
    }

    // Ticket 447
    public void setChangeEvent(String changeEvent) {
        getCol().setChangeEvent(changeEvent);
        designerPage.setModified(true);
    }

    public Font getHeadingFont() {
        return getCol().getHeadingFont();
    }

    public void setHeadingFont(Font font) {
        getCol().setHeadingFont(font);        
    }

    public Color getHeadingFontColor() {
        return getCol().getHeadingFontColor();
    }

    public void setHeadingFontColor(Color fontColor) {
        getCol().setHeadingFontColor(fontColor);
    }

    @Override
    public String getParentContainer() {
        return tableNodeData.getParentContainer();
    }

    @Override
    public void setParentContainer(String parentContainer) {
        //do nothing. This will be handled by tableNodeData
    }

    @Override
    public String getTable() {
        return tableNodeData.getTable();
    }

    @Override
    public void setTable(String parentContainer, String table) {
        tableNodeData.setTable(parentContainer, table);
    }

    @Override
    public void setTableFromDefault(String table) {
        tableNodeData.setTableFromDefault(table);
    }

    @Override
    public void clearColumn() {
        // Not supported from here for table columns.
    }

    @Override
    public String getColumn() {
        return getCol().getColName();
    }

    @Override
    public boolean getIsKeyCol() {
        return false;
    }

    @Override
    public boolean getisGUIDCol() {
        return false;
    }

}
