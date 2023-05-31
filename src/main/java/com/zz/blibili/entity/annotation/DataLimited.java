package com.zz.blibili.entity.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * api接口权限的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface DataLimited {

}



