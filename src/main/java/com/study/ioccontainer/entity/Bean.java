package com.study.ioccontainer.entity;

public class Bean {

    private String id;
    private Object value;

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Bean(String id, Object value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }


}
