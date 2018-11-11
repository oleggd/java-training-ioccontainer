package com.study.ioccontainer.exception;

public class InvalidBeanProcessing extends RuntimeException {

    public InvalidBeanProcessing(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBeanProcessing(String s) {
    }
}

