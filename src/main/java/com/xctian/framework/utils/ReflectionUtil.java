package com.xctian.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author xctian
 * @date 2020/1/22
 */
public class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 创建实例
     */
    public static Object newInstance(Class<?> clz){
        Object instance;
        try {
            instance = clz.newInstance();
        }catch (Exception e){
            LOGGER.error("new instance failure",e);
            throw new RuntimeException();
        }
        return instance;
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object obj, Method method,Object... args){
        Object res;
        try {
            // setAccessible是启用和禁用访问安全检查的开关，设置为true可以提高反射执行速度
            method.setAccessible(true);
            res = method.invoke(obj,args);
        }catch (Exception e){
            LOGGER.error("invoke method failure",e);
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * 设置成员变量的值
     */
    public static void setField(Object obj, Field field,Object value){
        try {
            field.setAccessible(true);
            field.set(obj,value);
        }catch (Exception e){
            LOGGER.error("set field failure",e);
            throw new RuntimeException(e);
        }
    }
}
