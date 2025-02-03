/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echoman.designer.components.echotable;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import com.echoman.designer.components.echobasenode.EchoBaseNode;
import com.echoman.designer.components.echocommon.EchoUtil;
import com.echoman.designer.components.echointerfaces.IEchoComponent;
import com.echoman.jdesi.PopupFromFieldProperties;
import java.util.ArrayList;

public class EchoColumnData implements IEchoComponent {

    private String dataType;
    private int width;
    private String header;
    private String colName;
    private boolean editable = true;
    private transient EchoBaseNode node = null;
    //Ticket #350 - Added property for Validations Data, Visible, Read only, Required,
    //    Default Value, Input Mask, Display Mask
    private boolean visible = true;
    private String validationData = "";
    private String validationDescriptionSql = "";
    private boolean required = false;
    private String defaultValue = "";
    private String mask = "";
    private String displayMask = "";
    private String validationCriteria = "";
    private String validationId = "";
    private String validationOrder = "";
    private String validationStoredColumnName = "";
    private boolean timeZoneOffset = true;
    // Ticket 447
    private String changeEvent = "";
    private String name = "";
    //Ticket #502
    private Font headingFont = new Font("Open Sans Semibold", Font.BOLD, 14);
    private Color headingFontColor = new Color(255, 255, 255);    
    private ArrayList<PopupFromFieldProperties> popupFromFieldValue = new ArrayList<>();

    public ArrayList<PopupFromFieldProperties> getPopupFromFieldValue() {
        return popupFromFieldValue;
    }

    public void setPopupFromFieldValue(ArrayList<PopupFromFieldProperties> popupFromFieldValue) {
        this.popupFromFieldValue = popupFromFieldValue;
    }

    // Ticket 31606
    public String getValidationCriteria() {
        return validationCriteria;
    }

    public void setValidationCriteria(String validationCriteria) {
        this.validationCriteria = validationCriteria;
    }

    public String getValidationId() {
        return validationId;
    }

    public void setValidationId(String validationId) {
        this.validationId = validationId;
    }

    public String getValidationOrder() {
        return validationOrder;
    }

    public void setValidationOrder(String validationOrder) {
        this.validationOrder = validationOrder;
    }

    public String getValidationStoredColumnName() {
        return validationStoredColumnName;
    }

    public void setValidationStoredColumnName(String validationStoredColumnName) {
        this.validationStoredColumnName = validationStoredColumnName;
    }

    public EchoColumnData(String colName) {
        this.colName = colName;
    }

    public EchoColumnData(String colName, int width, String header) {
        this(colName);
        this.width = width;
        this.header = header;
    }

    public EchoColumnData(String colName, String dataType, int width, String header) {
        this(colName, width, header);
        this.dataType = dataType;
    }

    public EchoColumnData(String colName, String dataType, int width, String header,
            boolean editable) {
        this(colName, dataType, width, header);
        this.editable = editable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDisplayMask() {
        return displayMask;
    }

    public void setDisplayMask(String displayMask) {
        this.displayMask = displayMask;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getValidationData() {
        return validationData;
    }

    public void setValidationData(String validationData) {
        if (validationData != null) {
            String[] valDataArray = EchoUtil.parseValidationData(validationData);
            this.validationData = valDataArray[EchoUtil.ARRAY_VALIDATION_DATA_POS];
            if (valDataArray[EchoUtil.ARRAY_VALIDATION_TYPE_POS].equals("user")) {
                this.validationStoredColumnName = "Value";
            } else {
                this.validationStoredColumnName = valDataArray[EchoUtil.ARRAY_VALIDATION_STORED_COL_POS];
            }
            this.validationId = valDataArray[EchoUtil.ARRAY_VALIDATION_ID_POS];
            this.validationCriteria = valDataArray[EchoUtil.ARRAY_VALIDATION_CRITERIA_POS];
            this.validationOrder = valDataArray[EchoUtil.ARRAY_VALIDATION_ORDER_POS];
        }
    }

    public String getValidationDescriptionSql() {
        return validationDescriptionSql;
    }

    public void setValidationDescriptionSql(String validationDescriptionSql) {
        this.validationDescriptionSql = validationDescriptionSql == null ? "" : validationDescriptionSql;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(boolean timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    @Override
    public EchoColumnData clone() {
        EchoColumnData col = new EchoColumnData(colName, dataType, width, header);
        col.setEditable(editable);
        col.setVisible(visible);
        col.setValidationData(validationData);
        col.setValidationDescriptionSql(validationDescriptionSql);
        col.setRequired(required);
        col.setDefaultValue(defaultValue);
        col.setMask(mask);
        col.setDisplayMask(displayMask);
        col.setChangeEvent(changeEvent);
        col.setName(name);        
        col.setHeadingFont(headingFont);
        col.setHeadingFontColor(headingFontColor);
        col.setPopupFromFieldValue(popupFromFieldValue);
        col.setTimeZoneOffset(timeZoneOffset);
        return col;
    }

    public void copy(EchoColumnData coldata) {
        setEditable(coldata.isEditable());
        setVisible(coldata.isVisible());
        setValidationData(coldata.getValidationData());
        setValidationDescriptionSql(coldata.getValidationDescriptionSql());
        setRequired(coldata.isRequired());
        setDefaultValue(coldata.getDefaultValue());
        setDisplayMask(coldata.getDisplayMask());
        setChangeEvent(coldata.getChangeEvent());
        setName(coldata.getName());
        setTimeZoneOffset(coldata.isTimeZoneOffset());
        setHeadingFont(coldata.getHeadingFont());
        setHeadingFontColor(coldata.getHeadingFontColor());
        setPopupFromFieldValue(popupFromFieldValue);
    }

    @Override
    public EchoBaseNode getNode() {
        return node;
    }

    @Override
    public void setNode(EchoBaseNode node) {
        this.node = node;
    }

    @Override
    public void clearLinkToEdit() {
    }

    @Override
    public void remove() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDropPanel(JPanel dropPanel) {
    }

    public String getChangeEvent() {
        return changeEvent;
    }

    public void setChangeEvent(String changeEvent) {
        this.changeEvent = changeEvent;
    }
    
    public Font getHeadingFont() {
        return headingFont;
    }

    public void setHeadingFont(Font font) {
        if ((font == null)
            || (font.getFamily().equalsIgnoreCase("Arial")
               && (font.getSize() == 10)
               && (font.getStyle() == Font.PLAIN))) {
            this.headingFont = new Font("Open Sans Semibold", Font.BOLD, 14);
        } else {
            this.headingFont = font;
        }
    }

    public Color getHeadingFontColor() {
        return headingFontColor;
    }

    public void setHeadingFontColor(Color fontColor) {
        if ((fontColor == null)
            || (fontColor.getRGB() == -16777216)) {
            this.headingFontColor = new Color(255, 255, 255);             
        } else {
            this.headingFontColor = new Color(fontColor.getRGB());
        }
    }     
}
