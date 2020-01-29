package com.xctian.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理管理类
 *
 * @author xctian
 * @date 2020/1/26
 */
public class ProxyManager {

    /**
     * Cglib动态代理
     *
     * @param targetClass 被代理类
     * @param proxyList 代理对象列表
     * @param <T> 被代理类实例
     * @return 被代理类实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList){
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy) throws Throwable {
                return new ProxyChain(targetClass,targetObject,targetMethod,methodProxy,methodParams,proxyList).doProxyChain();
            }
        });
    }
}
