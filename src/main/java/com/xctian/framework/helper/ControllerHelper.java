package com.xctian.framework.helper;

import com.xctian.framework.annotation.Action;
import com.xctian.framework.bean.Handler;
import com.xctian.framework.bean.Request;
import com.xctian.framework.utils.ArrayUtil;
import com.xctian.framework.utils.CollectionUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 *
 * @author xctian
 * @date 2020/1/22
 */
public class ControllerHelper {

    /**
     * 存放Request与Handler之间的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有Controller类（通过ClassHelper)
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            // 遍历这些Controller类
            for (Class<?> controllerClass : controllerClassSet) {
                //获取Controller中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    // 遍历Controller类中的所有方法
                    for (Method method : methods) {
                        // 判断当前方法是否带Action注解
                        if (method.isAnnotationPresent(Action.class)) {
                            //从Action注解中获取url映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            // 验证URL规则
                            if (mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    //获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);
                                    Handler handler = new Handler(controllerClass, method);
                                    // 初始化Action Map
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }


}
