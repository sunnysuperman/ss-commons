package com.sunnysuperman.commons.util;

import java.util.List;

public class VersionUtil {

    public static int compare(String version, String compared) {
        String v1 = version.toLowerCase().replaceAll("v", "");
        String v2 = compared.toLowerCase().replaceAll("v", "");
        String[] v1Array = StringUtil.splitAsArray(v1, ".");
        String[] v2Array = StringUtil.splitAsArray(v2, ".");
        for (int i = 0; i < v1Array.length; i++) {
            int d1 = Integer.parseInt(v1Array[i]);
            int d2 = i < v2Array.length ? Integer.parseInt(v2Array[i]) : 0;
            // 2.4 > 2.3
            // 2.3.1 > 2.3
            if (d1 > d2) {
                return 1;
            }
            if (d1 < d2) {
                return -1;
            }
        }
        if (v1Array.length < v2Array.length) {
            for (int j = v1Array.length; j < v2Array.length; j++) {
                int v = Integer.parseInt(v2Array[j]);
                if (v > 0) {
                    // 2.3 < 2.3.1
                    // 2.3 < 2.3.0.5
                    return -1;
                }
            }
            // 2.3 == 2.3.0
            return 0;
        }
        // 2.3 == 2.3 or 2.3.0 == 2.3
        return 0;
    }

    public static boolean isGreaterThan(String version, String compared) {
        int result = compare(version, compared);
        return result > 0;
    }

    public static boolean isGreaterThanOrEqual(String version, String compared) {
        int result = compare(version, compared);
        return result >= 0;
    }

    public static boolean isLessThan(String version, String compared) {
        int result = compare(version, compared);
        return result < 0;
    }

    public static boolean isLessThanOrEqual(String version, String compared) {
        int result = compare(version, compared);
        return result <= 0;
    }

    public static boolean isValidVersion(String s) {
        if (StringUtil.isEmpty(s)) {
            return false;
        }
        List<String> versions = StringUtil.split(s, ".", -1, true);
        for (int i = 0; i < versions.size(); i++) {
            String version = versions.get(i);
            if (version.isEmpty()) {
                return false;
            }
            if (version.length() > 1 && version.charAt(0) == '0') {
                return false;
            }
            if (!StringUtil.isNumeric(version)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidVersion(String s, int maxBlocks, int maxVersionPerBlock) {
        if (!isValidVersion(s)) {
            return false;
        }
        List<String> versions = StringUtil.split(s, ".", -1, false);
        if (versions.size() > maxBlocks) {
            return false;
        }
        if (maxVersionPerBlock > 0) {
            // We do not check first block
            for (int i = 1; i < versions.size(); i++) {
                String version = versions.get(i);
                int versionAsInt = Integer.parseInt(version);
                if (versionAsInt > maxVersionPerBlock) {
                    return false;
                }
            }
        }
        return true;
    }

    public static long parseVersionAsLong(String s, int maxBlocks, int blockLen) {
        List<String> versions = StringUtil.split(s, ".");
        long asLong = 0;
        for (int i = 0; i < versions.size(); i++) {
            String version = versions.get(i);
            int versionAsInt = Integer.parseInt(version);
            long power = (long) Math.pow(10, blockLen * (maxBlocks - 1 - i));
            asLong += power * versionAsInt;
        }
        return asLong;
    }
}
