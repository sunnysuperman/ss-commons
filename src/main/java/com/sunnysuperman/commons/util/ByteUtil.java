package com.sunnysuperman.commons.util;

public class ByteUtil {

    public static byte[] string2bytes(String s) {
        if (s == null) {
            return null;
        }
        return s.getBytes(StringUtil.UTF8_CHARSET);
    }

    public static String bytes2string(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StringUtil.UTF8_CHARSET);
    }

    public static byte[] int2bytes(int num) {
        byte[] buf = new byte[4];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x000000ff);
            num >>= 8;
        }
        return buf;
    }

    public static int bytes2int(byte[] bytes) {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static int bytes2int(byte[] bytes, int offset) {
        int num = 0;
        for (int i = offset; i < offset + 4; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] long2bytes(long num) {
        byte[] buf = new byte[8];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x00000000000000ff);
            num >>= 8;
        }
        return buf;
    }

    public static long bytes2long(byte[] bytes) {
        long num = 0;
        for (int i = 0; i < 8; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static long bytes2long(byte[] bytes, int offset) {
        long num = 0;
        for (int i = offset; i < offset + 8; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] short2bytes(short num) {
        byte[] buf = new byte[2];
        for (int i = buf.length - 1; i >= 0; i--) {
            buf[i] = (byte) (num & 0x00ff);
            num >>= 8;
        }
        return buf;
    }

    public static short bytes2short(byte[] bytes) {
        short num = 0;
        for (int i = 0; i < 2; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static short bytes2short(byte[] bytes, int offset) {
        short num = 0;
        for (int i = offset; i < offset + 2; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }

    public static byte[] double2bytes(double x) {
        long l = Double.doubleToLongBits(x);
        return long2bytes(l);
    }

    public static double bytes2double(byte[] bytes) {
        long l = bytes2long(bytes);
        return Double.longBitsToDouble(l);
    }

    public static double bytes2double(byte[] bytes, int offset) {
        long l = bytes2long(bytes, offset);
        return Double.longBitsToDouble(l);
    }

    public static byte getBit(byte b, byte offset) {
        return (byte) ((b >> (7 - offset)) & 0x1);
    }

}
