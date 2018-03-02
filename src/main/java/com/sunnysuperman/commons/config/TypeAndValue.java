package com.sunnysuperman.commons.config;

import java.util.Date;
import java.util.Map;

import com.sunnysuperman.commons.util.FormatUtil;
import com.sunnysuperman.commons.util.JSONUtil;

public class TypeAndValue {
    protected String type;
    protected Object value;

    public TypeAndValue(String type, Object value) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.type = type;
        this.value = value;
    }

    public TypeAndValue(Object value) {
        this(Config.TYPE_UNKNOWN, value);
    }

    public String asString() {
        return FormatUtil.parseString(value);
    }

    public boolean asBoolean() {
        return FormatUtil.parseBooleanStrictly(value).booleanValue();
    }

    public int asInt() {
        return FormatUtil.parseInteger(value).intValue();
    }

    public long asLong() {
        return FormatUtil.parseLong(value).longValue();
    }

    public double asDouble() {
        return FormatUtil.parseDouble(value).doubleValue();
    }

    public Date asDate() {
        return FormatUtil.parseDate(value);
    }

    public Map<?, ?> asJSONObject() {
        if (value instanceof Map) {
            return (Map<?, ?>) value;
        }
        return JSONUtil.parseJSONObject(FormatUtil.parseString(value));
    }

    public byte[] asBlob() {
        return (byte[]) value;
    }

    // ////////////////////////////////////////

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

}
