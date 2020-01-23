package com.xctian.framework;

import com.xctian.framework.bean.Data;
import com.xctian.framework.bean.Handler;
import com.xctian.framework.bean.Param;
import com.xctian.framework.bean.View;
import com.xctian.framework.helper.BeanHelper;
import com.xctian.framework.helper.ControllerHelper;
import com.xctian.framework.helper.PropertiesConfigHelper;
import com.xctian.framework.utils.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求转发器
 *
 * @author xctian
 * @date 2020/1/23
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * init方法在构造器调用之后马上被调用，用来初始化Servlet，init方法在容器装入Servlet 时执行
     * Servlet容器在实例化后只调用一次init方法， init方法必须在servlet接收到任何请求之前完成
     * 只有servlet成功被init()方法初始化后，Service方法才会被调用
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化Helper
        HelperLoader.init();
        // 获取ServletContext对象（用于注册Servlet),该对象全局唯一，而且工程内部的所有servlet都共享这个对象
        ServletContext servletContext = config.getServletContext();
        //注册处理JSP的Servlet，效果等同于web.xml下配置servlet,只不过此处采用编码进行配置
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(PropertiesConfigHelper.getAppJspPath() + "*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(PropertiesConfigHelper.getAppAssetPath() + "*");
    }

    /**
     * service方法是接口中的方法，servlet容器把所有请求发送到该方法，该方法默认行为是转发http请求到doXXX方法中，
     * 如果重载了该方法，默认操作被覆盖，不再进行转发操作
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求方法与路径并进行封装
        String requestMethod = req.getMethod().toLowerCase();
        String requestPath = req.getPathInfo();
        // 获取Action处理器
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if (handler != null) {
            // 通过hanlder获取controller类及实例
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);
            // 创建请求参数对象
            Map<String, Object> paramMap = new HashMap<String, Object>();
            // 请求体类型是application/x- www-form-urlencoded时，对该类型的请求内容提供了request.getParameter()方法来获取请求参数值
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = req.getParameter(paramName);
                paramMap.put(paramName, paramValue);
            }
            // 请求体内容是其它类型时，比如 multipart/form-data或application/json时，
            // 无法通过request.getParameter()获取到请求内容，此时只能通过request.getInputStream()
            // request.getInputStream()返回请求内容字节流，多用于文件上传
            String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
            if (StringUtil.isNotEmpty(body)) {
                String[] params = StringUtil.splitString(body, "&");
                if (ArrayUtil.isNotEmpty(params)) {
                    for (String param : params) {
                        String[] array = StringUtil.splitString(param, "=");
                        if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                            String paramName = array[0];
                            String paramValue = array[1];
                            paramMap.put(paramName, paramValue);
                        }
                    }
                }
            }
            Param param = new Param(paramMap);
            // 通过反射调用Action方法
            Method actionMethod = handler.getActionMethod();
            Object res = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            // 处理Action方法返回值
            if (res instanceof View) {
                //返回JSP页面
                View view = (View) res;
                String path = view.getPath();
                if (StringUtil.isNotEmpty(path)) {
                    // response.sendRedirect(String location)方法中的参数location，如果不以“/”开头，
                    // 表示相对于当前源组件的路径；如果以“/”开头，表示相对于当前服务器根路径的URL
                    if (path.startsWith("/")) {
                        resp.sendRedirect(req.getContextPath() + path);
                    } else {
                        Map<String, Object> model = view.getModel();
                        for (Map.Entry<String, Object> entry : model.entrySet()) {
                            req.setAttribute(entry.getKey(), entry.getValue());
                        }
                        req.getRequestDispatcher(PropertiesConfigHelper.getAppJspPath() + path).forward(req, resp);
                    }
                }
            } else if (res instanceof Data) {
                //若为Data类型，则返回JSON数据
                Data data = (Data) res;
                Object model = data.getModel();
                if (model != null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    // 这个out对象的作用是可以通过当前HttpServletResponse以流的方式响应数据到请求html或者jsp页面，可以在客户端输出
                    PrintWriter writer = resp.getWriter();
                    String json = JsonUtil.toJson(model);
                    writer.write(json);
                    writer.flush();;
                    writer.close();

                }
            }
        }
    }


}