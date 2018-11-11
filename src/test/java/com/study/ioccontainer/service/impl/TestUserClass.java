package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.processing.BeanPostProcessor;

import javax.annotation.PostConstruct;

public class TestUserClass implements BeanPostProcessor {
    String id;
    String name;

    /*@PostConstruct
    public void setName() {
        this.name = "DefaultName";
    }*/

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String id) throws RuntimeException {
        return bean;//"Empty bean";
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String id) throws RuntimeException {
        return bean;//"Full bean";
    }
}
