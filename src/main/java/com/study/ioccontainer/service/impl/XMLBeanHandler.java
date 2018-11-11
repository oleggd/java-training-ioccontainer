package com.study.ioccontainer.service.impl;

import com.study.ioccontainer.entity.BeanDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLBeanHandler  extends DefaultHandler {

    private BeanDefinition beanDefinition = null;

    private List<BeanDefinition> beanDefinitionList;
    private Map<String,String> propertyValues;
    private Map<String,String> propertyRefs;

    boolean bBean = false;
    boolean bPropertyValue = false;
    boolean bPropertyRef = false;

    public List<BeanDefinition> getBeanDefinitionList() {
        return beanDefinitionList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("bean")) {
            bBean = true;
            beanDefinition = new BeanDefinition();
            beanDefinition.setId(attributes.getValue("id"));
            beanDefinition.setClassName(attributes.getValue("class"));

            //initialize list
            if (beanDefinitionList == null) {
                beanDefinitionList = new ArrayList<>();
            }

        } else if (qName.equalsIgnoreCase("property")) {

            if (attributes.getValue("value") != null) {
                if ( propertyValues == null) {
                    propertyValues = new HashMap<>();
                }
                bPropertyValue = true;
                propertyValues.put(attributes.getValue("name"),attributes.getValue("value"));
            } else if (attributes.getValue("ref") != null) {
                if ( propertyRefs == null) {
                    propertyRefs = new HashMap<>();
                }
                bPropertyRef = true;
                propertyRefs.put(attributes.getValue("name"),attributes.getValue("ref"));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("bean") && bBean) {
            //add bean object to list
            beanDefinitionList.add(beanDefinition);
            bBean = false;
            propertyValues = null;
            propertyRefs = null;
        } else if (qName.equalsIgnoreCase("property") && bPropertyValue) {
            beanDefinition.setValuesDependencies(propertyValues);
            bPropertyValue = false;
        } else if (qName.equalsIgnoreCase("property") && bPropertyRef) {
            beanDefinition.setRefDependencies(propertyRefs);
            bPropertyRef = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

    }
}
