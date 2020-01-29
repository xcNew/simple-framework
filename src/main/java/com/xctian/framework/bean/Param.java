package com.xctian.framework.bean;

import com.xctian.framework.utils.CastUtil;
import com.xctian.framework.utils.CollectionUtil;
import com.xctian.framework.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
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
     * 根据参数名获取boolean参数
     */
    public Boolean getBoolean(String name){
        return CastUtil.castToBoolean(paramMap.get(name));
    }

    /**
     * 根据参数名获取double参数
     */
    public double getDouble(String name){
        return CastUtil.castToDouble(paramMap.get(name));
    }

    /**
     * 根据参数名获取long参数
     */
    public long getLong(String name){
        return CastUtil.castToLong(paramMap.get(name));
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

    /**
     * 判空
     */
    public boolean isEmpty(){
        return CollectionUtil.isEmpty(paramMap);
    }

}
