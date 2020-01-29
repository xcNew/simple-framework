package com.xctian.framework.helper;

import com.xctian.framework.annotation.Controller;
import com.xctian.framework.annotation.Service;
import com.xctian.framework.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 类操作helper
 *
 * @author xctian
 * @date 2020/1/22
 */
public class ClassHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);
    /**
     * 用于存放所加载的类
     */
    private static final Set<Class<?>> CLASS_SET;

    static {
        String basePackage = PropertiesConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    /**
     * 获取应用包名下的所有类
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用包下的所有Service类
     */
    public static Set<Class<?>> getServiceClassSet() {
        Set<Class<?>> serviceClassSet = new HashSet<Class<?>>();
        for (Class<?> clz : CLASS_SET) {
            if (clz.isAnnotationPresent(Service.class)) {
                serviceClassSet.add(clz);
            }
        }
        return serviceClassSet;
    }

    /**
     * 获取应用包下的所有Controller类
     */
    public static Set<Class<?>> getControllerClassSet() {
        Set<Class<?>> controllerClassSet = new HashSet<Class<?>>();
        for (Class<?> clz : CLASS_SET) {
            if (clz.isAnnotationPresent(Controller.class)) {
                controllerClassSet.add(clz);
            }
        }
        return controllerClassSet;
    }

    /**
     * 获取应用包名下的所有Bean类，含Controller和Service
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        beanClassSet.addAll(getServiceClassSet());
        beanClassSet.addAll(getControllerClassSet());
        return beanClassSet;
    }

    /**
     * 获取应用包命下某父类的所有子类(获接口的实现类)
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用包名下所有带某注解的所有类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }
}
