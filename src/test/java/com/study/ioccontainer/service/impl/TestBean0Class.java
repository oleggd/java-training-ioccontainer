package com.study.ioccontainer.service.impl;

import javax.annotation.PostConstruct;

public class TestBean0Class {
    String id;
    String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostConstruct
    //public void setName() {this.name = "DefaultProduct";}

    public String getName() {
        return name;
    }
}
