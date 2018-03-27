package com.sunnysuperman.commons.config;

public class AllConfigKeyFilter implements ConfigKeyFilter {

    @Override
    public boolean accept(String key) {
        return true;
    }

}
