package com.sunnysuperman.commons.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解，bean映射操作中，忽略操作的属性，通过set方法实现
 * 
 * 
 *
 */
@Target({ ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BeanIgnore {
}
