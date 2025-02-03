package com.echoman.designer.components.echocommon;

import com.echoman.designer.databasemanager.DBConnections;
import com.echoman.jdesi.FormData;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class SelectFDNextFormPropertyEditor extends PropertyEditorSupport {

    private List<FormData> availableForms = new ArrayList<>();

    @Override
    public Component getCustomEditor() {
        return new FormSelectionForm("Select FDNext Forms...", null, true, this,
                getAvailableForms());
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public String getAsText() {
        final FormData form = (FormData) getValue();
        return form == null ? "" : form.getFormName();
    }

    @Override
    public void setAsText(String formName) {
        availableForms.stream()
                .filter(formData -> nonNull(formData.getFormName()))
                .filter(formData -> formData.getFormName().equals(formName))
                .findFirst()
                .ifPresent(this::setValue);
    }

    private List<FormData> getAvailableForms() {
        availableForms = DBConnections.getFdNextForms();
        return availableForms;
    }
}
