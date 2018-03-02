package com.sunnysuperman.commons.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONUtil {
    private static Map<Type, ObjectSerializer> DEFAULT_SERIALIZERS = new HashMap<Type, ObjectSerializer>();
    private static SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();
    private static final TimestampSerializer TIMESTAMP_SERIALIZER = new TimestampSerializer();

    static {
        DEFAULT_SERIALIZERS.put(java.util.Date.class, TIMESTAMP_SERIALIZER);
        DEFAULT_SERIALIZERS.put(java.sql.Timestamp.class, TIMESTAMP_SERIALIZER);
        DEFAULT_SERIALIZERS.put(java.sql.Date.class, TIMESTAMP_SERIALIZER);
        for (Entry<Type, ObjectSerializer> entry : DEFAULT_SERIALIZERS.entrySet()) {
            SERIALIZE_CONFIG.put(entry.getKey(), entry.getValue());
        }
    }

    private static class TimestampSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
                throws IOException {
            if (object == null) {
                serializer.getWriter().writeNull();
            } else {
                Date date = (Date) object;
                serializer.write(date.getTime());
            }
        }
    }

    public static String toJSONString(Object object) {
        return toJSONString(object, null, SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
    }

    public static String toJSONString(Object object, Map<Type, ObjectSerializer> serializers) {
        return toJSONString(object, serializers, SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.BrowserCompatible);
    }

    public static String toJSONString(Object object, Map<Type, ObjectSerializer> serializers,
            SerializerFeature... features) {
        if (object == null) {
            return null;
        }
        SerializeConfig sc = null;
        if (serializers == null) {
            sc = SERIALIZE_CONFIG;
        } else {
            sc = new SerializeConfig();
            for (Entry<Type, ObjectSerializer> entry : DEFAULT_SERIALIZERS.entrySet()) {
                sc.put(entry.getKey(), entry.getValue());
            }
            for (Entry<Type, ObjectSerializer> entry : serializers.entrySet()) {
                sc.put(entry.getKey(), entry.getValue());
            }
        }
        return JSON.toJSONString(object, sc, features);
    }

    public static Object parse(String s) {
        if (StringUtil.isEmpty(s)) {
            return null;
        }
        return JSON.parse(s);
    }

    public static Map<String, Object> parseJSONObject(String s) {
        Object o = parse(s);
        return (JSONObject) o;
    }

    public static List<?> parseJSONArray(String s) {
        Object o = parse(s);
        return (JSONArray) o;
    }

    public static boolean isJSONString(String s) {
        if (s == null) {
            return false;
        }
        char endChar = '0';
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '{') {
                endChar = '}';
                break;
            }
            if (c == '[') {
                endChar = ']';
                break;
            }
            return false;
        }
        if (endChar == '0') {
            return false;
        }
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            return c == endChar;
        }
        return false;
    }
}
