package com.xctian.framework;

import com.xctian.framework.helper.BeanHelper;
import com.xctian.framework.helper.ClassHelper;
import com.xctian.framework.helper.ControllerHelper;
import com.xctian.framework.helper.IocHelper;
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
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList){
            ClassUtil.loadClass(cls.getName());
        }
    }
}
