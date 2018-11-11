package com.study.ioccontainer.processing;

// track as system beans!
public interface BeanPostProcessor {
    // JdbcUserDao, userDao ->     "hello world" ->   userDao
    Object postProcessBeforeInitialization(Object bean, String id) throws RuntimeException;

    Object postProcessAfterInitialization(Object bean, String id) throws RuntimeException;
}
