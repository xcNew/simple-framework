package com.xctian.framework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet助手类，用于解耦Servlet API
 *
 * @author xctian
 * @date 2020/1/29
 */
public class ServletHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletHelper.class);

    /**
     * 每个线程都有一份ServletHelper实例
     */
    private static final ThreadLocal<ServletHelper> SERVLET_HELPER_HOLDER = new ThreadLocal<ServletHelper>();

    private HttpServletRequest request;

    private HttpServletResponse response;

    private ServletHelper(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 初始化
     */
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        SERVLET_HELPER_HOLDER.set(new ServletHelper(request, response));
    }

    /**
     * 销毁
     */
    public static void destory() {
        SERVLET_HELPER_HOLDER.remove();
    }

    /**
     * 获取request对象
     */
    private static HttpServletRequest getRequest() {
        return SERVLET_HELPER_HOLDER.get().request;
    }

    /**
     * 获取Response对象
     */
    private static HttpServletResponse getResponse() {
        return SERVLET_HELPER_HOLDER.get().response;
    }

    /**
     * 获取session对象
     */
    private static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 获取ServletContext对象
     */
    private static ServletContext getServletContext() {
        return getRequest().getServletContext();
    }

    /*-----封装常用的Servlet API ----*/

    /**
     * Request中获取属性
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(String key) {
        return (T) getRequest().getAttribute(key);
    }

    /**
     * 从Request中移除属性
     */
    public static void removeRequestAttribute(String key) {
        getRequest().removeAttribute(key);
    }

    /**
     * 请求重定向
     */
    public static void sendRedirect(String loc) {
        try {
            getResponse().sendRedirect(getRequest().getContextPath() + loc);
        } catch (IOException e) {
            LOGGER.error("重定向失败", e);
        }
    }

    /**
     * 属性放入Session
     */
    public static void setSessionAttribute(String key, Object val) {
        getSession().setAttribute(key, val);
    }

    /**
     * session中取值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key) {
        return (T) getRequest().getSession().getAttribute(key);
    }

    /**
     * 从session中移除属性
     */
    public static void removeSessionAttribute(String key){
        getRequest().getSession().removeAttribute(key);
    }

    /**
     * 使session失效
     */
    public static void invalidateSession(){
        getRequest().getSession().invalidate();
    }
}
