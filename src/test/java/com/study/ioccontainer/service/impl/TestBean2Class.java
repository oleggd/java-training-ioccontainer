package com.study.ioccontainer.service.impl;

import javax.annotation.PostConstruct;

public class TestBean2Class {
    String id;
    Object value;

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    //@PostConstruct
    //public void setvalue() {        this.name = "DefaultProduct";    }

    public Object getValue () {
        return value;
    }
}
