package com.sunnysuperman.commons.bean;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;

public interface ParseBeanInterceptor {

    ParseBeanResult parse(Object value, Class<?> destClass, ParameterizedType pType, LinkedList<String> contextKeys);

}
