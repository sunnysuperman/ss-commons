package com.sunnysuperman.commons.config;

/**
 * 不过滤任何属性的过滤器
 * 
 * 
 *
 */
public class AllConfigKeyFilter implements ConfigKeyFilter {

    @Override
    public boolean accept(String key) {
        return true;
    }

}
