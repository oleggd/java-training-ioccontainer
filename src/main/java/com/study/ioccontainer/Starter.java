package com.study.ioccontainer;

import com.study.ioccontainer.processing.BeanFactoryPostProcessor;
import com.study.ioccontainer.service.ApplicationContext;
import com.study.ioccontainer.service.impl.ClassPathApplicationContext;
import com.study.ioccontainer.service.impl.TestPostFactoryProcessorClass1;

import java.lang.reflect.InvocationTargetException;

public class Starter {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ApplicationContext applicationContext = new ClassPathApplicationContext("testContextFile.xml");
    }
}