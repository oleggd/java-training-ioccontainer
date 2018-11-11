package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.annotations.PostConstruct;
import com.study.ioccontainer.entity.Bean;
import com.study.ioccontainer.entity.BeanDefinition;
import com.study.ioccontainer.exception.InvalidBeanProcessing;
import com.study.ioccontainer.processing.BeanFactoryPostProcessor;
import com.study.ioccontainer.processing.BeanPostProcessor;
import com.study.ioccontainer.service.ApplicationContext;
import com.study.ioccontainer.service.BeanDefinitionReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private BeanDefinitionReader beanDefinitionReader;
    private List<BeanDefinition> beanDefinitionList;
    private List<Bean> beanList;
    private List<Bean> postProcessorBeanList;
    private String beanFile;

    public ClassPathApplicationContext(String beanFileName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.beanFile = beanFileName;

        // read bean definitions
        beanDefinitionReader = new XmlBeanDefinitionReader(beanFile);
        beanDefinitionList = beanDefinitionReader.readBeanDefinition();
        // bean factory processing
        beanDefinitionList = beanFactoryProcessing(beanDefinitionList);
        // build beans list
        beanList = constructBeans(beanDefinitionList);
        // values dependencies
        injectValuesDependencies(beanDefinitionList, beanList);
        // values depandencies
        injectRefDependencies(beanDefinitionList, beanList);
        // beanPostProcessor - before
        beanList = beanPostProcessRefactor(beanList,"Before");
        // @postConstruct process
        postConstructProcess(beanList);
        // beanPostProcessor - after
        beanList = beanPostProcessRefactor(beanList,"After");
    }

    List<BeanDefinition> beanFactoryProcessing(List<BeanDefinition> beanDefinitionList) throws ClassNotFoundException {

        List<Bean> beanFactoryList = new ArrayList<>();
        List<BeanDefinition> beanNonFactoryDefinitionList = new ArrayList<>();

        for (BeanDefinition beanDefinition : beanDefinitionList) {

            Class beanFactoryClass = Class.forName(beanDefinition.getClassName());

            // find system classes for bean definition procesing
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanFactoryClass))
            {
                try {
                    Object instance = beanFactoryClass.newInstance();
                    Bean factoryBean = new Bean(beanDefinition.getId(), instance);
                    beanFactoryList.add(factoryBean);
                } catch (Exception e) {
                    throw new InvalidBeanProcessing("Error creating bean "+ beanDefinition.getId() + " for className " + beanFactoryClass.getName());
                }
            } else { // save all nonFactory bean definitions
                beanNonFactoryDefinitionList.add(beanDefinition);
            }
        }

        // process all nonFactory beans by factory beans
        for (Bean beanFactory : beanFactoryList) {
            // process
            BeanFactoryPostProcessor beanFactoryObject = (BeanFactoryPostProcessor) beanFactory.getValue();
            beanFactoryObject.postProcessBeanFactory(beanNonFactoryDefinitionList);
        }
        // remove all factory beans and put processed beans only
        return beanNonFactoryDefinitionList;
    }

    private List<Bean> process(Bean beanProcessor, List<Bean> beanToProcessList, String postMethod) {
        List<Bean> postProcessedBeanList = new ArrayList<>();

        try {
            Object beanProcessor1 = beanProcessor.getValue();

            for (Method method : beanProcessor.getValue().getClass().getMethods()) {
                if (postMethod.equals(method.getName())) {
                    for (Bean beanToProcess : beanToProcessList) {
                        Object processedBean = method.invoke(beanProcessor.getValue(),beanToProcess.getValue(),beanToProcess.getId());
                        postProcessedBeanList.add(new Bean(beanToProcess.getId(),processedBean));
                    }
                    break;
                }
            }
        } catch ( IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return postProcessedBeanList;
    }

    List<Bean> beanPostProcessRefactor(List<Bean> beanList, String processingType) {
        // bean post processing - before initialization
        List<Bean> postProcessedBeanList = new ArrayList<>();

        if ("Before".equals(processingType)) {
            postProcessorBeanList = new ArrayList<>();
            // collect all postProcessor and non-system beans
            for (Bean bean : beanList) {
                if ( bean.getValue() instanceof BeanPostProcessor ) {
                    postProcessorBeanList.add(bean);
                } else {
                    postProcessedBeanList.add(bean);
                }
            }
            // pass all non-system beans through Before initialization
            for (Bean bean : postProcessorBeanList) {
                postProcessedBeanList = process(bean, postProcessedBeanList, "postProcessBeforeInitialization");
            }

        }// pass all non-system beans through After initialization
         else if ("After".equals(processingType)) {
             if (postProcessorBeanList.size() != 0 ) {
                 for (Bean postProcessorBean : postProcessorBeanList) {
                     postProcessedBeanList = process(postProcessorBean, beanList, "postProcessAfterInitialization");
                 }
             } else {
                 postProcessedBeanList =  beanList;
             }
        }
        //replace all bean list by new - without system beans
        return postProcessedBeanList;
    }

    void postConstructProcess(List<Bean> beanToProcessList) throws InvalidBeanProcessing {
        // @PostConstruct - initialization
        for (Bean bean : beanToProcessList) {
            Class beanValueClass = bean.getValue().getClass();

            for (Method method : beanValueClass.getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.invoke(beanValueClass);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                    }
                }
            }
        }
    }

    List<Bean> beanPostProcess(List<Bean> beanList, String processingType) {
        // bean post processing - before initialization
        List<Bean> postProcessorBeanList = new ArrayList<>();
        List<Bean> postProcessedBeanList = new ArrayList<>();

        for (Bean bean : beanList) {
            if (bean.getValue().getClass().isInstance("BeanPostProcessor")) {
                postProcessorBeanList.add(bean);
                try {
                    Method method = bean.getValue().getClass().getMethod("postProcessBeforeInitialization");
                    for (Bean nonSysemBean : beanList) {

                        if (!nonSysemBean.getValue().getClass().isInstance("BeanPostProcessor")) {
                            //method.invoke(bean,nonSysemBean,nonSysemBean.getId());
                            BeanPostProcessor postProcessor = (BeanPostProcessor) bean.getValue();
                            Object processedBeanValue = postProcessor.postProcessBeforeInitialization(nonSysemBean, nonSysemBean.getId());
                            bean.setId(nonSysemBean.getId());
                            bean.setValue(processedBeanValue);
                            postProcessedBeanList.add(bean);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    throw new InvalidBeanProcessing("Error getting bean method postProcessBeforeInitialization for className " + bean.getClass().getName());
                }
            }
        }

        // @PostConstruct - initialization
        for (Bean bean : postProcessedBeanList) {
            for (Method method : bean.getValue().getClass().getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.invoke(bean.getValue().getClass());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error invoking bean method "+ method.getName() + " for className " + bean.getClass().getName());
                    }
                }
            }
        }
        // bean post processing - after initialization
        for (Bean beanProcessor : postProcessorBeanList) {
            try {
                Method method = beanProcessor.getValue().getClass().getMethod("postProcessAfterInitialization");
                BeanPostProcessor postProcessor = (BeanPostProcessor) beanProcessor.getValue();

                for (Bean beanProcessed : postProcessedBeanList) {
                    Object processedBeanValue = postProcessor.postProcessAfterInitialization(beanProcessed.getValue(), beanProcessed.getId());
                    beanProcessor.setValue(processedBeanValue);
                }
            } catch (NoSuchMethodException e) {
                throw new InvalidBeanProcessing("Error getting bean method postProcessAfterInitialization for className " + beanProcessor.getClass().getName());
            }
        }
        //replace all bean list by new - without system beans
        return postProcessedBeanList;
    }

    @Override
    public Object getBean(String id) {

        for (Bean bean : beanList) {
            if (bean.getId().equals(id)) {
                return bean.getValue();
            }
        }
        return null;
    }

    @Override
    public Object getBean(Class clazz) {

        for (Bean bean : beanList) {
            if (clazz.isAssignableFrom(bean.getValue().getClass())) {
                return clazz.cast(bean.getValue());
            }
        }
        return null;
    }

    @Override
    public Object getBean(String id, Class clazz) {

        for (Bean bean : beanList) {
            if ((bean.getId().equals(id)) && (bean.getValue().getClass() == clazz)) {
                return bean.getValue();
            }
        }
        return null;

    }

    @Override
    public List<Bean> getAllBeans() {
        return beanList;
    }

    List<Bean> constructBeans(List<BeanDefinition> beanDefinitionList) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        List<Bean> beanList = new ArrayList<>();
        Bean bean = null;

        for (BeanDefinition beanDefinition : beanDefinitionList) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getClassName());
                Object instance = clazz.newInstance();
                bean = new Bean(beanDefinition.getId(), instance);
                beanList.add(bean);

            } catch (Exception e) {
                throw new InvalidBeanProcessing("Error creating bean "+ bean.getId()+" for className  " + beanDefinition.getClassName());
            }
        }
        return beanList;
    }

    void injectValuesDependencies(List<BeanDefinition> beanDefinitionList, List<Bean> beanList) {

        String beanID = "";
        String setterName = "";
        Map<String, String> valuesDependencies = new HashMap<>();

        for (BeanDefinition beanDefinition : beanDefinitionList) {

            beanID = beanDefinition.getId();
            valuesDependencies = beanDefinition.getValuesDependencies();

            // get bean for current beanDefinition
            Object beanObject = getBeanValueByID(beanList, beanID);

            Method setter;
            for (Map.Entry<String, String> entry : valuesDependencies.entrySet()) {
                // get setter by name = set + beanDefinition.name
                Class<?> clazz = beanObject.getClass();
                try {
                    setter = getMethod(clazz,"set", entry.getKey());
                    // call setter for current bean object with beanDefinition.value
                    setter.invoke(beanObject, entry.getValue());
                } catch ( IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidBeanProcessing("Error injecting values dependencies for setter "+ clazz.getName() + "."+ setterName);
                }
            }
        }
    }

    Method getMethod(Class clazz , String prefix, String name) {
        String methodName = prefix + name.substring(0, 1).toUpperCase() + name.substring(1);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    void injectRefDependencies(List<BeanDefinition> beanDefinitionList, List<Bean> beanList) {

        String beanID = "";
        String setterName = "";
        Map<String, String> refDependencies = new HashMap<>();

        for (BeanDefinition beanDefinition : beanDefinitionList) {

            beanID = beanDefinition.getId();
            refDependencies = beanDefinition.getRefDependencies();

            if (refDependencies != null) {
                // get bean for current beanDefinition
                Object bean = getBeanValueByID(beanList, beanID);
                Method setter;

                for (Map.Entry<String, String> entry : refDependencies.entrySet()) {
                    Class<?> clazz = bean.getClass();
                    try {
                        // get setter for current bean by name = set + beanDefinition.name
                        setter = getMethod(bean.getClass(),"set", entry.getKey());//
                        // get referenced bean
                        Object refBeanObject = getBeanValueByID(beanList, entry.getValue());
                        // call setter for current bean object with beanDefinition.value - existing bean object
                        setter.invoke(bean, refBeanObject);

                    } catch ( IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidBeanProcessing("Error inject Ref dependencies for "+ clazz.getName() + "." + setterName);
                    }
                }
            }
        }
    }

    private Object getBeanValueByID(List<Bean> beanList, String beanID) {

        for (Bean bean : beanList) {
            if (bean.getId().equals(beanID)) {
                return bean.getValue();
            }
        }
        return null;
    }


}
