package com.sunnysuperman.commons.util;

public class ByteUtil {

    public static byte[] fromString(String s) {
        if (s == null) {
            return null;
        }
        return s.getBytes(StringUtil.UTF8_CHARSET);
    }

    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StringUtil.UTF8_CHARSET);
    }

    public static String toString(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, offset, length, StringUtil.UTF8_CHARSET);
    }

    public static byte[] fromInt(int num) {
        byte[] buf = new byte[4];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x000000ff);
            num >>= 8;
        }
        return buf;
    }

    public static int toInt(byte[] bytes) {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static int toInt(byte[] bytes, int offset) {
        int num = 0;
        for (int i = offset; i < offset + 4; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] fromLong(long num) {
        byte[] buf = new byte[8];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x00000000000000ff);
            num >>= 8;
        }
        return buf;
    }

    public static long toLong(byte[] bytes) {
        long num = 0;
        for (int i = 0; i < 8; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static long toLong(byte[] bytes, int offset) {
        long num = 0;
        for (int i = offset; i < offset + 8; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] fromShort(short num) {
        byte[] buf = new byte[2];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x00ff);
            num >>= 8;
        }
        return buf;
    }

    public static short toShort(byte[] bytes) {
        short num = 0;
        for (int i = 0; i < 2; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static short toShort(byte[] bytes, int offset) {
        short num = 0;
        for (int i = offset; i < offset + 2; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] fromDouble(double x) {
        long l = Double.doubleToLongBits(x);
        return fromLong(l);
    }

    public static double toDouble(byte[] bytes) {
        long l = toLong(bytes);
        return Double.longBitsToDouble(l);
    }

    public static double toDouble(byte[] bytes, int offset) {
        long l = toLong(bytes, offset);
        return Double.longBitsToDouble(l);
    }

    public static byte getBit(byte b, byte offset) {
        return (byte) ((b >> (7 - offset)) & 0x1);
    }

}
