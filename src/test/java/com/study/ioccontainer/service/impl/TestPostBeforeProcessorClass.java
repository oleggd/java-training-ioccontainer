package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.exception.InvalidBeanProcessing;
import com.study.ioccontainer.processing.BeanPostProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestPostBeforeProcessorClass implements BeanPostProcessor {
    String id;
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String id) throws RuntimeException {

        //Class beanClass = bean.getClass();

        for (Method method : bean.getClass().getMethods()) {
            if ("setName".equals(method.getName())) {
                try {
                    method.invoke(bean,"BeforeInit");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                }
            }
        }
        return bean;//"Empty bean";
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String id) throws RuntimeException {
        /*for (Method method : bean.getClass().getMethods()) {
            if ("setName".equals(method.getName())) {
                try {
                    method.invoke(bean,"AfterInit");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                }
            }
        }*/
        return bean;//"Full bean";
    }
}
