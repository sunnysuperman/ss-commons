package com.sunnysuperman.commons.locale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sunnysuperman.commons.exception.UnexpectedException;
import com.sunnysuperman.commons.util.FormatUtil;
import com.sunnysuperman.commons.util.PlaceholderUtil;
import com.sunnysuperman.commons.util.PlaceholderUtil.CompileHandler;
import com.sunnysuperman.commons.util.PlaceholderUtil.CompileOptions;
import com.sunnysuperman.commons.util.StringUtil;

public abstract class LocaleBundle {

	public static class LocaleBundleOptions {
		private boolean strictMode;
		private String defaultLocale;
		private String[] prefLocales;
		private String logKey;
		private boolean escapeSpecialChars = true;
		private String compileStartToken;
		private String compileEndToken;

		public boolean isStrictMode() {
			return strictMode;
		}

		public LocaleBundleOptions setStrictMode(boolean strictMode) {
			this.strictMode = strictMode;
			return this;
		}

		public String getDefaultLocale() {
			return defaultLocale;
		}

		public LocaleBundleOptions setDefaultLocale(String defaultLocale) {
			this.defaultLocale = defaultLocale;
			return this;
		}

		public String[] getPrefLocales() {
			return prefLocales;
		}

		public LocaleBundleOptions setPrefLocales(String[] prefLocales) {
			this.prefLocales = prefLocales;
			return this;
		}

		public String getLogKey() {
			return logKey;
		}

		public LocaleBundleOptions setLogKey(String logKey) {
			this.logKey = logKey;
			return this;
		}

		public boolean isEscapeSpecialChars() {
			return escapeSpecialChars;
		}

		public LocaleBundleOptions setEscapeSpecialChars(boolean escapeSpecialChars) {
			this.escapeSpecialChars = escapeSpecialChars;
			return this;
		}

		public String getCompileStartToken() {
			return compileStartToken;
		}

		public LocaleBundleOptions setCompileStartToken(String compileStartToken) {
			this.compileStartToken = compileStartToken;
			return this;
		}

		public String getCompileEndToken() {
			return compileEndToken;
		}

		public LocaleBundleOptions setCompileEndToken(String compileEndToken) {
			this.compileEndToken = compileEndToken;
			return this;
		}

	}

	private final byte[] writeLock = new byte[0];
	private volatile boolean initialized = false;
	private Map<String, Map<String, String>> bundlesMap = new ConcurrentHashMap<>(0);
	protected final LocaleBundleOptions options;
	private final CompileOptions compileOptions;

	public LocaleBundle(LocaleBundleOptions options) {
		this.options = options;
		this.compileOptions = new CompileOptions().setStartToken(options.getCompileStartToken())
				.setEndToken(options.getCompileEndToken());
		if (options.getDefaultLocale() == null) {
			throw new UnexpectedException(wrapLogMessage("No default locale set", options));
		}
	}

	protected String wrapLogMessage(String msg, LocaleBundleOptions options) {
		if (options.getLogKey() != null) {
			return options.getLogKey() + ": " + msg;
		}
		return msg;
	}

	protected void put(String key, String locale, String value) throws Exception {
		if (StringUtil.isEmpty(key)) {
			throw new IllegalArgumentException("Bad key");
		}
		if (StringUtil.isEmpty(locale)) {
			throw new IllegalArgumentException("Bad locale");
		}
		value = StringUtil.trimToNull(value);
		if (value == null) {
			throw new IllegalArgumentException("Bad value");
		}
		if (options.escapeSpecialChars) {
			value = StringUtil.escapeSpecialChars(value);
		}
		synchronized (writeLock) {
			Map<String, String> table = bundlesMap.get(key);
			if (table == null) {
				table = new ConcurrentHashMap<String, String>();
				bundlesMap.put(key, table);
			}
			table.put(locale, value);
		}
	}

	protected void finishPut() {
		Set<String> locales = null;
		for (Entry<String, Map<String, String>> bundleEntry : bundlesMap.entrySet()) {
			String key = bundleEntry.getKey();
			Map<String, String> table = bundleEntry.getValue();
			if (table.get(options.getDefaultLocale()) == null) {
				throw new UnexpectedException(wrapLogMessage("No default value set for key: " + key, options));
			}
			if (options.strictMode) {
				if (locales == null) {
					locales = table.keySet();
				} else {
					Set<String> theLocales = table.keySet();
					if (theLocales.size() != locales.size() || !theLocales.containsAll(locales)) {
						throw new UnexpectedException(wrapLogMessage("Missing some locales for key: " + key, options));
					}
				}
			}
		}
		initialized = true;
	}

	public boolean containsKey(String key) {
		return bundlesMap.containsKey(key);
	}

	public int size() {
		return bundlesMap.size();
	}

	public String getRaw(String locale, String key) {
		if (!initialized) {
			throw new UnexpectedException("Does not finish init");
		}
		Map<String, String> table = bundlesMap.get(key);
		if (table == null) {
			return null;
		}
		if (locale != null) {
			locale = LocaleUtil.findSupportLocale(locale, table.keySet());
			if (locale != null) {
				return table.get(locale);
			}
		}
		String[] preferencedLocales = options.getPrefLocales();
		if (preferencedLocales != null) {
			for (String prefLocale : preferencedLocales) {
				String value = table.get(prefLocale);
				if (value != null) {
					return value;
				}
			}
		}
		return table.get(options.getDefaultLocale());
	}

	public String toString() {
		return bundlesMap.toString();
	}

	public class LocaleCompileHandler implements CompileHandler {
		private String locale;

		public LocaleCompileHandler(String locale) {
			super();
			this.locale = locale;
		}

		private Number getNumber(String s, Map<String, Object> context) {
			return StringUtil.isNumeric(s.charAt(0)) ? FormatUtil.parseNumber(s)
					: FormatUtil.parseNumber(context.get(s));
		}

		@Override
		public String compile(String key, Map<String, Object> context) {
			Object value = context.get(key);
			if (value != null) {
				return value.toString();
			}
			if (key.indexOf("subtract(") == 0) {
				List<String> numbers = StringUtil.split(key.substring("subtract(".length(), key.indexOf(')')), ",");
				Number n1 = getNumber(numbers.get(0), context);
				Number n2 = getNumber(numbers.get(1), context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() - n2.doubleValue());
				}
				return String.valueOf(n1.longValue() - n2.longValue());
			}
			if (key.indexOf("plus(") == 0) {
				List<String> numbers = StringUtil.split(key.substring("plus(".length(), key.indexOf(')')), ",");
				Number n1 = getNumber(numbers.get(0), context);
				Number n2 = getNumber(numbers.get(1), context);
				if (n1 instanceof Double || n1 instanceof Float || n2 instanceof Double || n2 instanceof Float) {
					return String.valueOf(n1.doubleValue() + n2.doubleValue());
				}
				return String.valueOf(n1.longValue() + n2.longValue());
			}
			int arrayIndex = key.indexOf("[");
			if (arrayIndex > 0) {
				String prefix = key.substring(0, arrayIndex);
				String pluralKey = key.substring(arrayIndex + 1, key.indexOf(']'));
				Number number = FormatUtil.parseNumber(context.get(pluralKey));
				if (number == null) {
					return null;
				}
				String d = number.toString();
				String s = getWithParams(locale, prefix + "[" + d + "]", context);
				if (s != null) {
					return s;
				}
				return getWithParams(locale, prefix + "[other]", context);
			}
			return null;
		}

	}

	public String getWithParams(String locale, String key, Map<String, Object> context) {
		String text = getRaw(locale, key);
		if (context == null) {
			return text;
		}
		return PlaceholderUtil.compile(text, context, compileOptions, new LocaleCompileHandler(locale));
	}

	public String getWithArrayParams(String locale, String key, Object[] params) {
		String text = getRaw(locale, key);
		if (params == null) {
			return text;
		}
		Map<String, Object> context = new HashMap<String, Object>(params.length);
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			context.put(String.valueOf(i), param);
		}
		return PlaceholderUtil.compile(text, context, compileOptions, new LocaleCompileHandler(locale));
	}

}
