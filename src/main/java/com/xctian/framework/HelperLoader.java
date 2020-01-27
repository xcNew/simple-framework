package com.xctian.framework;

import com.xctian.framework.helper.*;
import com.xctian.framework.utils.ClassUtil;

/**
 * 各Helper初始化工具
 *
 * @author xctian
 * @date 2020/1/23
 */
public class HelperLoader {

    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                // aop要在IOChelper之前加载因为要通过AOP获得代理对象才能通过IOChelper进行依赖注入
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList){
            ClassUtil.loadClass(cls.getName());
        }
    }
}
