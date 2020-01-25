package com.xctian.framework.annotation;

import java.lang.annotation.*;

/**
 * 切面注解
 *
 * @author xctian
 * @date 2020/1/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 注解
     */
    Class<? extends Annotation> value();
}
