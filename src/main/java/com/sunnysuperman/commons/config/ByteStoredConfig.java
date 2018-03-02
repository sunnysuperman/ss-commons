package com.sunnysuperman.commons.config;

import java.util.Date;
import java.util.Map;

import com.sunnysuperman.commons.util.ByteUtil;
import com.sunnysuperman.commons.util.FormatUtil;
import com.sunnysuperman.commons.util.JSONUtil;

public abstract class ByteStoredConfig extends Config {

    public static final Object deserialize(String type, byte[] bytes) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        Object value;
        switch (type) {
        case TYPE_STRING:
            value = ByteUtil.bytes2string(bytes);
            break;
        case TYPE_BOOLEAN:
            value = bytes[0] == 0 ? Boolean.FALSE : Boolean.TRUE;
            break;
        case TYPE_INT:
            value = ByteUtil.bytes2int(bytes);
            break;
        case TYPE_LONG:
            value = ByteUtil.bytes2long(bytes);
            break;
        case TYPE_DOUBLE:
            value = ByteUtil.bytes2double(bytes);
            break;
        case TYPE_DATE:
            value = new Date(ByteUtil.bytes2long(bytes));
            break;
        case TYPE_BLOB:
            value = bytes;
            break;
        case TYPE_JSONOBJECT:
            value = JSONUtil.parseJSONObject(ByteUtil.bytes2string(bytes));
            break;
        default:
            throw new RuntimeException("Unknown config type: " + type);
        }
        return value;
    }

    public static final byte[] serialize(String type, Object value) {
        if (type == null) {
            throw new NullPointerException("type");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        byte[] bytes;
        switch (type) {
        case TYPE_STRING:
            bytes = ByteUtil.string2bytes(FormatUtil.parseString(value));
            break;
        case TYPE_INT:
            bytes = ByteUtil.int2bytes(FormatUtil.parseInteger(value));
            break;
        case TYPE_BOOLEAN:
            bytes = new byte[] { FormatUtil.parseBoolean(value, false) ? (byte) 1 : 0 };
            break;
        case TYPE_DOUBLE:
            bytes = ByteUtil.double2bytes(FormatUtil.parseDouble(value));
            break;
        case TYPE_LONG:
            bytes = ByteUtil.long2bytes(FormatUtil.parseLong(value));
            break;
        case TYPE_DATE:
            bytes = ByteUtil.long2bytes(FormatUtil.parseDate(value).getTime());
            break;
        case TYPE_BLOB:
            bytes = (byte[]) value;
            break;
        case TYPE_JSONOBJECT: {
            Map<?, ?> map;
            if (value instanceof Map) {
                map = (Map<?, ?>) value;
            } else {
                map = JSONUtil.parseJSONObject(FormatUtil.parseString(value));
            }
            String s = (map == null) ? null : JSONUtil.toJSONString(map);
            if (s == null) {
                throw new RuntimeException("Bad object: " + value);
            }
            bytes = ByteUtil.string2bytes(s);
            break;
        }
        default:
            throw new RuntimeException("Unknown config type: " + type);
        }
        return bytes;
    }
}
