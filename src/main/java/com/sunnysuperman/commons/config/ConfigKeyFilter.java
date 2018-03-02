package com.sunnysuperman.commons.config;

/**
 * 属性key过滤器接口
 * 
 * 
 *
 */
public interface ConfigKeyFilter {

    /**
     * 是否接受该key
     * 
     * @param key
     *            key
     * @return 若接受，则返回true；否则，返回false
     */
    boolean accept(String key);
}
