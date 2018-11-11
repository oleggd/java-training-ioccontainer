package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.BeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class XmlBeanDefinitionReaderTest {

    @org.junit.Test
    public void readBeanDefinition() {
        String testContextFile = "testContextFile.xml";
        List<BeanDefinition> expectedBeanDefinitionList = new ArrayList<>();
        List<BeanDefinition> actualBeanDefinitionList = new ArrayList<>();
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setId("1");
        beanDefinition.setClassName("TestClassName");
        beanDefinition.setValuesDependencies(new HashMap<>());
        beanDefinition.setRefDependencies(new HashMap<>());

        expectedBeanDefinitionList.add(beanDefinition);
        expectedBeanDefinitionList.add(beanDefinition);
        expectedBeanDefinitionList.add(beanDefinition);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(testContextFile);

        actualBeanDefinitionList = beanDefinitionReader.readBeanDefinition();

        assertEquals(expectedBeanDefinitionList.size(),actualBeanDefinitionList.size());
    }


}