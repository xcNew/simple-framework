package com.xctian.framework.bean;

/**
 * 返回Data封装类
 *
 * @author xctian
 * @date 2020/1/24
 */
public class Data {

    /**
     * 模型数据
     */
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
