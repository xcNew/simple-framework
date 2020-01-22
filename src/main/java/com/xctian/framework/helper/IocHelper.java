package com.xctian.framework.helper;

import com.xctian.framework.annotation.Inject;
import com.xctian.framework.utils.ArrayUtil;
import com.xctian.framework.utils.CollectionUtil;
import com.xctian.framework.utils.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入helper,完成IOC容器的初始化工作
 * 在IOC框架中管理的对象都是单例的
 *
 * @author xctian
 * @date 2020/1/22
 */
public class IocHelper {
    // IocHelperer被加载时，就会自动加载static块
    static {
        // 获取Bean Map
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历BeanMap
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                // 获取Bean Class中的所有成员变量,即Bean field
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历bean fields
                    for (Field field : beanFields) {
                        //判断当前field是否有Inject注解
                        if (field.isAnnotationPresent(Inject.class)) {
                            //在BeanMap中获取field对应的实例
                            Class<?> beanFieldClass = field.getType();
                            Object beanFiledInstance = beanMap.get(beanFieldClass);
                            if (beanFiledInstance != null) {
                                //通过反射对Bean field进行初始化
                                ReflectionUtil.setField(beanInstance, field, beanFiledInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
