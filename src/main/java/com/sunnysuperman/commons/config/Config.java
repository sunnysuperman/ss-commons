package com.sunnysuperman.commons.config;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置抽象类
 *
 */
public abstract class Config {
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);
    private static Set<String> TYPES;

    /**
     * string类型
     */
    public static final String TYPE_STRING = "string";
    /**
     * int类型
     */
    public static final String TYPE_INT = "int";
    /**
     * bool类型
     */
    public static final String TYPE_BOOLEAN = "bool";
    /**
     * double类型
     */
    public static final String TYPE_DOUBLE = "double";
    /**
     * long类型
     */
    public static final String TYPE_LONG = "long";
    /**
     * object类型
     */
    public static final String TYPE_JSONOBJECT = "object";
    /**
     * date类型
     */
    public static final String TYPE_DATE = "date";
    /**
     * blob类型
     */
    public static final String TYPE_BLOB = "blob";
    /**
     * 未知类型
     */
    public static final String TYPE_UNKNOWN = "u";

    static {
        TYPES = new HashSet<String>(9);
        TYPES.add(TYPE_STRING);
        TYPES.add(TYPE_INT);
        TYPES.add(TYPE_BOOLEAN);
        TYPES.add(TYPE_DOUBLE);
        TYPES.add(TYPE_LONG);
        TYPES.add(TYPE_JSONOBJECT);
        TYPES.add(TYPE_DATE);
        TYPES.add(TYPE_BLOB);
        TYPES.add(TYPE_UNKNOWN);
        TYPES = Collections.unmodifiableSet(TYPES);
    }

    public static interface ConfigValueChangedListener {
        void onChanged(String key, Object value);
    }

    private Map<String, TypeAndValue> dataSource = new ConcurrentHashMap<String, TypeAndValue>(0);
    private ConcurrentLinkedQueue<ConfigValueChangedListener> listeners = new ConcurrentLinkedQueue<ConfigValueChangedListener>();

    public static boolean isValidType(String type) {
        if (type == null) {
            return false;
        }
        return TYPES.contains(type);
    }

    public static boolean isExplicitType(String type) {
        if (type == null) {
            return false;
        }
        return TYPES.contains(type) && !type.equals(TYPE_UNKNOWN);
    }

    public static Set<String> getTypes() {
        return TYPES;
    }

    public static Set<String> getExplicitTypes() {
        Set<String> types = new HashSet<String>(TYPES);
        types.remove(TYPE_UNKNOWN);
        return Collections.unmodifiableSet(types);
    }

    /**
     * 插入数据
     * 
     * @param key
     *            key
     * @param tv
     *            值
     * @param fireChangedEvent
     *            是否触发改变事件
     */
    public void put(String key, TypeAndValue tv, boolean fireChangedEvent) {
        if (tv == null || tv.getValue() == null) {
            dataSource.remove(key);
        } else {
            dataSource.put(key, tv);
        }
        if (fireChangedEvent && !listeners.isEmpty()) {
            for (ConfigValueChangedListener listener : listeners) {
                try {
                    listener.onChanged(key, tv.getValue());
                } catch (Throwable t) {
                    LOG.error("Listener on changed failed", t);
                }
            }
        }
    }

    /**
     * 是否包含key
     * 
     * @param key
     *            key
     * @return 若包含，返回true；否则，返回false
     */
    public final boolean containsKey(String key) {
        return dataSource.containsKey(key);
    }

    public final Set<String> keySet() {
        return dataSource.keySet();
    }

    public final int size() {
        return dataSource.size();
    }

    /**
     * 添加配置属性发生改变的监听器
     * 
     * @param listener
     *            监听器
     */
    public void addListener(ConfigValueChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * 删除配置属性发生改变的监听器
     * 
     * @param listener
     *            监听器
     */
    public void removeListener(ConfigValueChangedListener listener) {
        listeners.remove(listener);
    }

    public TypeAndValue get(String key) {
        TypeAndValue tv = dataSource.get(key);
        if (tv == null) {
            tv = load(key);
            if (tv != null) {
                put(key, tv, true);
            }
        }
        return tv;
    }

    public TypeAndValue ensure(String key) {
        TypeAndValue tv = get(key);
        if (tv == null || tv.value == null) {
            throw new RuntimeException("No value set for key '" + key + "'");
        }
        return tv;
    }

    public boolean remove(String key) {
        return dataSource.remove(key) != null;
    }

    public TypeAndValue getAndRemove(String key) {
        TypeAndValue tv = dataSource.remove(key);
        if (tv == null) {
            tv = load(key);
        }
        return tv;
    }

    public TypeAndValue getAndRemove(String key, Object defaultValue) {
        TypeAndValue tv = getAndRemove(key);
        if (tv == null) {
            tv = new TypeAndValue(TYPE_UNKNOWN, defaultValue);
        }
        return tv;
    }

    public Object getValue(String key) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.getValue() : null;
    }

    public Object ensureValue(String key) {
        return ensure(key).getValue();
    }

    public final String getString(String key) {
        return ensure(key).asString();
    }

    public final String getString(String key, String defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asString() : defaultValue;
    }

    public final int getInt(String key) {
        return ensure(key).asInt();
    }

    public final int getInt(String key, int defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asInt() : defaultValue;
    }

    public final long getLong(String key) {
        return ensure(key).asLong();
    }

    public final long getLong(String key, long defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asLong() : defaultValue;
    }

    public final double getDouble(String key) {
        return ensure(key).asDouble();
    }

    public final double getDouble(String key, double defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asDouble() : defaultValue;
    }

    public final boolean getBoolean(String key) {
        return ensure(key).asBoolean();
    }

    public final boolean getBoolean(String key, boolean defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asBoolean() : defaultValue;
    }

    public final Date getDate(String key) {
        return ensure(key).asDate();
    }

    public final Date getDate(String key, Date defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asDate() : defaultValue;
    }

    public final byte[] getBlob(String key) {
        return ensure(key).asBlob();
    }

    public final byte[] getBlob(String key, byte[] defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asBlob() : defaultValue;
    }

    public final Map<?, ?> getJSONObject(String key) {
        return ensure(key).asJSONObject();
    }

    public final Map<?, ?> getJSONObject(String key, Map<?, ?> defaultValue) {
        TypeAndValue tv = get(key);
        return tv != null ? tv.asJSONObject() : defaultValue;
    }

    public abstract void save(String key, String type, Object value);

    public abstract void purge(String key);

    protected abstract TypeAndValue load(String key);
}
