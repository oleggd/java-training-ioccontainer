package com.study.ioccontainer.service;

import com.study.ioccontainer.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {

    List<BeanDefinition> readBeanDefinition();
}
