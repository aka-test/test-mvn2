/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.echoman.designer.components.echocommon;

public class PropInfo {

    private Class valueType;
    private String getter;
    private String setter;
    private String property;
    private String name;
    private Class editorType;
    private Object value;
    private boolean onlyUseCustomEditor = false;

    public PropInfo(String name, Class valueType, String property, String getter, String setter, Class editorType, Object value) {
        this.valueType = valueType;
        this.getter = getter;
        this.setter = setter;
        this.property = property;
        this.name = name;
        this.editorType = editorType;
        this.value = value;
    }

    public PropInfo(String name, Class valueType, String property, String getter,
            String setter, Class editorType, Object value, boolean onlyUseCustomEditor) {
        this(name, valueType, property, getter, setter, editorType, value);
        this.onlyUseCustomEditor = onlyUseCustomEditor;
    }

    public PropInfo(String name, Class valueType, String property) {
        this(name, valueType, property, null, null, null, null);
    }

    public PropInfo(String name, Class valueType, String property, boolean onlyUseCustomEditor) {
        this(name, valueType, property, null, null, null, null);
        this.onlyUseCustomEditor = onlyUseCustomEditor;
    }

    public PropInfo(String name, Class valueType, String getter, String setter) {
        this(name, valueType, null, getter, setter, null, null);
    }

    public Class getEditorType() {
        return editorType;
    }

    public void setEditorType(Class editorType) {
        this.editorType = editorType;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSetter() {
        return setter;
    }

    public void setSetter(String setter) {
        this.setter = setter;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean hasProperty() {
        return ((property != null) && (!property.equals("")));
    }

    public boolean hasEditorType() {
        return (editorType != null);
    }

    public boolean hasValue() {
        return (value != null);
    }

    public boolean isOnlyUseCustomEditor() {
        return onlyUseCustomEditor;
    }

    public void setOnlyUseCustomEditor(boolean onlyUseCustomEditor) {
        this.onlyUseCustomEditor = onlyUseCustomEditor;
    }

}
