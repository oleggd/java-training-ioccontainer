package com.study.ioccontainer.service.impl;


import com.study.ioccontainer.annotations.PostConstruct;

public class TestRoleClass {
    String id;
    Object role;

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(TestUserClass role) {
        this.role = role;
    }

    /*@PostConstruct
    public void setRole() {
        this.role = "Test Role";
    }*/

}
