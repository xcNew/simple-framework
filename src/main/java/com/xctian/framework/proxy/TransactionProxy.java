package com.xctian.framework.proxy;

import com.xctian.framework.annotation.Transaction;
import com.xctian.framework.helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.lang.reflect.Method;

/**
 * 事务代理
 *
 * @author xctian
 * @date 2020/1/28
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);

    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object res;
        boolean flag = FLAG_HOLDER.get();
        Method method = proxyChain.getTargetMethod();
        // 被Transaction注解的方法才被拦截
        if(!flag && method.isAnnotationPresent(Transaction.class)){
            FLAG_HOLDER.set(true);
            try {
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                res = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            }catch (Exception e){
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            }finally {
                FLAG_HOLDER.remove();
            }
        }else {
            res = proxyChain.doProxyChain();
        }
        return res;
    }
}
