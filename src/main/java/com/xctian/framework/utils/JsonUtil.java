package com.xctian.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON和Java Bean的转换工具类，基于Jackson
 *
 * @author xctian
 * @date 2020/1/23
 */
public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Bean转换为Json
     */
    public static <T> String toJson(T obj) {
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error("convert bean to Json failure", e);
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * Json转换为Bean
     */
    public static <T> T toBean(String json, Class<T> type) {
        T bean;
        try {
            bean = OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            LOGGER.error("convert Json to bean failure", e);
            throw new RuntimeException(e);
        }
        return bean;
    }
}
