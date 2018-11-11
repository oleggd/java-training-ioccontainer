package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.InvalidBeanProcessing;
import com.study.ioccontainer.service.BeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class ClassPathApplicationContextTest {

    ClassPathApplicationContext classPathApplicationContext;

    @Before
    public void Before () throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

    }
    @Test
    public void getBeanByID() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        classPathApplicationContext = new ClassPathApplicationContext("testContextFile.xml");

        Object bean = classPathApplicationContext.getBean("testProductID");
        assertTrue(bean.getClass().getName().equals("com.study.ioccontainer.service.impl.TestProductClass"));

        bean = null;
        bean = classPathApplicationContext.getBean("testRoleID");
        assertTrue(bean.getClass().getName().equals("com.study.ioccontainer.service.impl.TestRoleClass"));

    }

    @Test
    public void getBeanByClass() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        classPathApplicationContext = new ClassPathApplicationContext("testContextFile.xml");

        Object bean = classPathApplicationContext.getBean(TestProductClass.class);
        assertTrue(bean != null);

        bean = null;
        bean = classPathApplicationContext.getBean(TestRoleClass.class);
        assertTrue(bean != null);
    }

    @Test
    public void getBeanByClassAndID() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        classPathApplicationContext = new ClassPathApplicationContext("testContextFile.xml");

        Object bean = classPathApplicationContext.getBean("testProductID", TestProductClass.class);
        assertTrue(bean != null);

        bean = null;
        bean = classPathApplicationContext.getBean("testRoleID", TestRoleClass.class);
        assertTrue(bean != null);
    }

    @Test
    public void constructBeansCorrectTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //
        String testFileName = "textCorrectBeanProcess.xml";
        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext(testFileName);

        BeanDefinitionReader beanDefinitionReader;
        List<BeanDefinition> beanDefinitionList;
        List<Bean>           beanList;

        beanDefinitionReader = new XmlBeanDefinitionReader(testFileName);
        beanDefinitionList   = beanDefinitionReader.readBeanDefinition();

        assertEquals("testBean0",beanDefinitionList.get(0).getId());
        assertEquals("testBean1",beanDefinitionList.get(1).getId());
        assertEquals("testBean2",beanDefinitionList.get(2).getId());

        assertTrue(beanDefinitionList.get(0).getValuesDependencies().containsKey("id"));
        assertTrue(beanDefinitionList.get(1).getValuesDependencies().containsKey("id"));
        assertTrue(beanDefinitionList.get(2).getValuesDependencies().containsKey("id"));

        assertTrue(beanDefinitionList.get(2).getRefDependencies().containsKey("value"));

        beanList = classPathApplicationContext.getAllBeans();
        assertEquals(3,beanList.size());

    }

    @Test
    public void beanFactoryProcessingTest() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String testFileName = "textCorrectBeanProcess.xml";
        List<Bean>           beanList;

        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext(testFileName);
        beanList = classPathApplicationContext.getAllBeans();

        // one system bean, other usual 5 - 1 = 4
        assertEquals(3, beanList.size());

        //check bean definitions id, all should be with 1 at the end
        for (Bean bean : beanList) {
            for (Method method : bean.getValue().getClass().getMethods()) {
                if ("getID".equals(method.getName())) {
                    try {
                        String id = (String) method.invoke(bean.getValue(),null);
                        // get latest symbol
                        id = id.substring(id.length()-1,id.length());
                        assertEquals("1",id);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                    }
                    break;
                }
            }
        }
    }

    @Test
    public void beanPostProcessorTest () throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        String testFileName = "testPostBeforeProcess.xml";
        List<Bean>           beanList;

        ClassPathApplicationContext classPathApplicationContext = new ClassPathApplicationContext(testFileName);
        beanList = classPathApplicationContext.getAllBeans();

        for (Bean bean : beanList) {

            for (Method method : bean.getValue().getClass().getMethods()) {
                if ("getName".equals(method.getName())) {
                    try {
                        String name = (String) method.invoke(bean.getValue(),null);
                        assertEquals("BeforeInit",name);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                    }
                    break;
                }
            }
        }

        testFileName = "testPostAfterProcess.xml";

        classPathApplicationContext = new ClassPathApplicationContext(testFileName);
        beanList = classPathApplicationContext.getAllBeans();

        for (Bean bean : beanList) {

            for (Method method : bean.getValue().getClass().getMethods()) {
                if ("getName".equals(method.getName())) {
                    try {
                        String name = (String) method.invoke(bean.getValue(),null);
                        assertEquals("AfterInit",name);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                    }
                    break;
                }
            }
        }
    }
}

