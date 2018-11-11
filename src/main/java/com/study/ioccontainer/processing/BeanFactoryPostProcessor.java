package com.study.ioccontainer.processing;

import com.study.ioccontainer.entity.BeanDefinition;

import java.util.List;

public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(List<BeanDefinition> definitions);
}