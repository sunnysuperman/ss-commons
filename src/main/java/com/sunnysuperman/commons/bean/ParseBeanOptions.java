package com.sunnysuperman.commons.bean;

/**
 * 解析bean的配置项
 *
 */
public class ParseBeanOptions {
    private ParseBeanInterceptor interceptor;
    private boolean injectContext;

    public ParseBeanInterceptor getInterceptor() {
        return interceptor;
    }

    public ParseBeanOptions setInterceptor(ParseBeanInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public boolean isInjectContext() {
        return injectContext;
    }

    public ParseBeanOptions setInjectContext(boolean injectContext) {
        this.injectContext = injectContext;
        return this;
    }

}
