package com.echoman.designer.components.echocommon;

import com.echoman.designer.databasemanager.DBConnections;

import java.awt.*;
import java.beans.PropertyEditorSupport;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class SelectUIPropertyEditor extends PropertyEditorSupport {

    private final HashMap<String, String> uiComponents = new HashMap<>();

    @Override
    public Component getCustomEditor() {
        return new SelectListForm("Select UI...", null, true, this, getUiComponents());
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
     * @param uiComponentCode
     */
    @Override
    public void setAsText(String uiComponentCode) {
        final String linkColumn = uiComponents.get(uiComponentCode);
        final String value = EchoUtil.isNullOrEmpty(linkColumn) ? uiComponentCode : uiComponentCode + "|" + linkColumn;
        setValue(value);
    }

    private String[] getUiComponents() {
        final String qry = "SELECT Code, LinkTableName + '.' + LinkColumnName AS LinkColumn FROM dbo.UIComponents WHERE HasUI = 'Y' ORDER BY Code";
        try (final PreparedStatement stmt = DBConnections.getConnection().getJDBCConnection().prepareStatement(qry)) {
            try (final ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    uiComponents.put(rs.getString(1), rs.getString(2));
                }
                return uiComponents.keySet().toArray(new String[uiComponents.size()]);
            }
        } catch (Exception ex) {
            return new String[] {};
        }
    }

}
