package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.processing.BeanFactoryPostProcessor;

import javax.annotation.PostConstruct;
import java.util.List;

public class TestPostFactoryProcessorClass1 implements BeanFactoryPostProcessor {
    String id;
    String name;

    @PostConstruct
    public void setName() {
        this.name = "DefaultName";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void postProcessBeanFactory(List<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            definition.setId(definition.getId() + "1");
        }
    }
}
