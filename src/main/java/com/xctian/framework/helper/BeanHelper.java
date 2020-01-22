package com.xctian.framework.helper;

import com.xctian.framework.utils.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bean容器实现类
 *
 * @author xctian
 * @date 2020/1/22
 */
public class BeanHelper {
    /**
     * Bean 映射Map，key为Class，value为对应的Class对象，可以理解为Bean容器
     */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<Class<?>, Object>();

    // 初始化，将所有实例化的Bean对象缓存至BEAN_MAP，实现创建好对象放入Bean容器，故可保证单例
    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> cls : beanClassSet) {
            Object obj = ReflectionUtil.newInstance(cls);
            BEAN_MAP.put(cls, obj);
        }
    }

    /**
     * 获取BeanMap
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 通过传入Bean类，获取对应的Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> cls) {
        if (!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("can not get bean by class:" + cls);
        }
        return (T) BEAN_MAP.get(cls);
    }
}
