package com.sunnysuperman.commons.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    private Map<String, Object> map;

    private MapBuilder() {
    }

    public static MapBuilder create(Map<String, Object> map) {
        MapBuilder builder = new MapBuilder();
        builder.map = map;
        return builder;
    }

    public static MapBuilder create() {
        return create(new HashMap<String, Object>());
    }

    public MapBuilder append(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }

}
