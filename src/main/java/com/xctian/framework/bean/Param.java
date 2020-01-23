package com.xctian.framework.bean;

import com.xctian.framework.utils.CastUtil;

import java.util.Map;

/**
 * 请求参数封装类
 *
 * @author xctian
 * @date 2020/1/24
 */
public class Param {

    private Map<String,Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 根据参数名获取int参数
     */
    public int getInt(String name){
        return CastUtil.castToInt(paramMap.get(name));
    }

    /**
     * 获取paramMap
     */
    public Map<String,Object> getParamMap(){
        return paramMap;
    }
}
