package com.xctian.framework.proxy;

/**
 * @author xctian
 * @date 2020/1/25
 */
public interface Proxy {

    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
