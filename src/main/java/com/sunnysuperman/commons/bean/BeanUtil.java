package com.sunnysuperman.commons.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil {

    /**
     * 获取class类的所有属性，包括父类，父类的父类...
     * 
     * @param clazz
     * @return 属性集合
     */
    public static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fields = new HashMap<>();
        getAllFields(clazz, fields);
        return fields;
    }

    /**
     * 获取class的所有属性，若有父类，则加载父类属性，父类的父类...
     * 
     * @param clazz
     *            class类
     * @param fieldMap
     *            属性map对象
     */
    private static void getAllFields(Class<?> clazz, Map<String, Field> fieldMap) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!fieldMap.containsKey(field.getName())) {
                fieldMap.put(field.getName(), field);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            getAllFields(superClass, fieldMap);
        }
    }
}
