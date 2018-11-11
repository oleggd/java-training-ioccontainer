package com.study.ioccontainer.service;

import java.util.List;

public interface ApplicationContext<T> {

    public Object getBean( String id);

    public T getBean( Class<T> clazz);

    public T getBean(String id, Class<T> clazz);

    public List<T> getAllBeans();

}
