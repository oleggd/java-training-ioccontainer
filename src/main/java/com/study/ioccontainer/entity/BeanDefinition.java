package com.study.ioccontainer.entity;

import java.util.Map;

public class BeanDefinition {
    private String id ="";
    private String className ="";

    private Map<String,String> valuesDependencies = null;
    private Map<String,String> refDependencies = null;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setValuesDependencies(Map<String, String> valuesDependencies) {
        this.valuesDependencies = valuesDependencies;
    }
    public Map<String, String> getValuesDependencies() {
        return valuesDependencies;
    }

    public Map<String, String> getRefDependencies() {
        return refDependencies;
    }
    public void setRefDependencies(Map<String, String> refDependencies) {
        this.refDependencies = refDependencies;
    }

    public String toString() {
        return "bean definition {" +
                "id =" + id +
                ", name ='" + className +
                ", valuesDependencies = " + (valuesDependencies != null ? valuesDependencies.toString() : "empty")  +
                ", refDependencies = " + ( refDependencies != null ? refDependencies.toString() : "empty") +
                "}";
    }
}
