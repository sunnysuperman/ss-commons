package com.sunnysuperman.commons.util;

public final class ObjectUtil {

    private ObjectUtil() {
    }

    /**
     * Checks that the given argument is not null. If it is, throws
     * {@link NullPointerException}. Otherwise, returns the argument.
     */
    public static <T> T checkNotNull(T arg, String text) {
        if (arg == null) {
            if (text != null) {
                throw new NullPointerException(text);
            } else {
                throw new NullPointerException();
            }
        }
        return arg;
    }

    public static <T> T checkNotNull(T arg) {
        return checkNotNull(arg, null);
    }
}