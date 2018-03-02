package com.sunnysuperman.commons.locale;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.commons.util.FileUtil;
import com.sunnysuperman.commons.util.StringUtil;
import com.sunnysuperman.commons.util.FileUtil.FileListHandler;

public class ClassPathPropertiesLocaleBundle extends LocaleBundle {

    public static class ClassPathPropertiesLocaleBundleOptions extends LocaleBundleOptions {
        private Class<?> clazz;
        private String path;
        private String resourcePrefix;
        private String charset;

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
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

    public ClassPathPropertiesLocaleBundle(final ClassPathPropertiesLocaleBundleOptions options) {
        super(options);
        try {
            FileUtil.listClassPathFiles(
                    options.getClazz() == null ? ClassPathPropertiesLocaleBundle.class : options.getClazz(),
                    options.getPath(), new FileListHandler() {

                        @Override
                        public boolean willOpenStream(String fileName, String fullPath, boolean isDirectory)
                                throws Exception {
                            if (isDirectory) {
                                return true;
                            }
                            if (options.getResourcePrefix() != null
                                    && !fileName.startsWith(options.getResourcePrefix())) {
                                return false;
                            }
                            return true;
                        }

                        @Override
                        public void streamOpened(String fileName, String fullPath, InputStream in) throws Exception {
                            String locale = FileSystemPropertiesLocaleBundle.detectLocaleFromFileName(fileName,
                                    options.getResourcePrefix());
                            if (locale == null) {
                                locale = options.getDefaultLocale();
                                if (locale == null) {
                                    throw new RuntimeException("Bad locale file columnName: " + fileName);
                                }
                            }
                            Map<String, String> props = null;
                            try {
                                props = FileUtil.readProperties(in, options.getCharset(), false);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to load: " + fileName);
                            }
                            for (Entry<String, String> entry : props.entrySet()) {
                                String key = StringUtil.trimToNull(entry.getKey());
                                String value = StringUtil.trimToNull(entry.getValue());
                                if (key == null || value == null) {
                                    continue;
                                }
                                put(key, locale, value);
                            }
                        }

                    });
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        finishPut();
    }

}
