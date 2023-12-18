package com.liuhf.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * 该注解主要影响那些不需要登录的接口
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {
    
}
