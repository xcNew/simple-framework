package com.xctian.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器注解,@Target 用来约束注解可以应用的地方（如方法、类或字段），@Retention用来约束注解的生命周期，
 * 分别有三个值，源码级别（source），类文件级别（class）或者运行时级别（runtime）
 *
 * @author xctian
 * @date 2020/1/22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
