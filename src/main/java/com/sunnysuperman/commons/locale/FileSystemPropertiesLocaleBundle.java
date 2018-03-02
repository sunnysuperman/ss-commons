package com.sunnysuperman.commons.locale;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.commons.util.FileUtil;
import com.sunnysuperman.commons.util.StringUtil;

public class FileSystemPropertiesLocaleBundle extends LocaleBundle {

    public static class FileSystemPropertiesLocaleBundleOptions extends LocaleBundleOptions {
        private File dir;
        private String resourcePrefix;
        private String charset;

        public File getDir() {
            return dir;
        }

        public void setDir(File dir) {
            this.dir = dir;
        }

        public String getResourcePrefix() {
            return resourcePrefix;
        }

        public void setResourcePrefix(String resourcePrefix) {
            this.resourcePrefix = resourcePrefix;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

    }

    public FileSystemPropertiesLocaleBundle(FileSystemPropertiesLocaleBundleOptions options) {
        super(options);
        File dir = options.getDir();
        String resourcePrefix = options.getResourcePrefix();
        String defaultLocale = options.getDefaultLocale();

        for (File file : dir.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            if (resourcePrefix != null && !file.getName().startsWith(resourcePrefix)) {
                continue;
            }
            String locale = detectLocaleFromFileName(file.getName(), resourcePrefix);
            if (locale == null) {
                locale = defaultLocale;
                if (locale == null) {
                    throw new RuntimeException("Bad locale file columnName: " + file.getName());
                }
            }
            Map<String, String> props = null;
            try {
                props = FileUtil.readProperties(new FileInputStream(file), options.getCharset(), false);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load: " + file.getAbsolutePath());
            }
            for (Entry<String, String> entry : props.entrySet()) {
                String key = StringUtil.trimToNull(entry.getKey());
                String value = StringUtil.trimToNull(entry.getValue());
                if (key == null || value == null) {
                    continue;
                }
                try {
                    put(key, locale, value);
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        finishPut();
    }

    public static String detectLocaleFromFileName(String fileName, String prefix) {
        int end = fileName.indexOf('.');
        if (end <= 0) {
            throw new RuntimeException("Bad fileName: " + fileName);
        }
        if (prefix == null) {
            return fileName.substring(0, end);
        }
        int start = fileName.indexOf(prefix);
        if (start != 0) {
            throw new RuntimeException("Bad fileName: " + fileName);
        }
        start = prefix.length();
        if (end < start) {
            throw new RuntimeException("Bad fileName: " + fileName);
        }
        if (end == start) {
            return null;
        }
        return fileName.substring(start + 1, end);
    }

}
