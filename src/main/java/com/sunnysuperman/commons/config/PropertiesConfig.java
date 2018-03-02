package com.sunnysuperman.commons.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.commons.util.FileUtil;

/**
 * 属性文件配置类
 * 
 * 
 *
 */
public class PropertiesConfig extends Config {

    public PropertiesConfig(InputStream in, String charset) {
        try {
            Map<String, String> props = FileUtil.readProperties(in, charset, true);
            for (Entry<String, String> entry : props.entrySet()) {
                put(entry.getKey(), new TypeAndValue(entry.getValue()), true);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PropertiesConfig(InputStream in) {
        this(in, null);
    }

    /**
     * 构造函数
     * 
     * @param map
     *            map对象
     */
    public PropertiesConfig(Map<?, ?> map) {
        for (Entry<?, ?> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            TypeAndValue tv;
            if (value instanceof TypeAndValue) {
                tv = (TypeAndValue) value;
            } else {
                tv = new TypeAndValue(value);
            }
            put(entry.getKey().toString(), tv, true);
        }
    }

    @Override
    public void save(String key, String type, Object value) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public void purge(String key) {
        throw new RuntimeException("Not supported");
    }

    @Override
    protected TypeAndValue load(String key) {
        return null;
    }

}
