package com.sunnysuperman.commons.util;

import java.util.Map;

public class PlaceholderUtil {

	public static interface CompileHandler {

		String compile(String key, Map<String, Object> context);

	}

	private static final CompileHandler DEFAULT_COMPILE_HANDLER = new CompileHandler() {

		@Override
		public String compile(String key, Map<String, Object> context) {
			Object value = context.get(key);
			if (value == null) {
				return null;
			}
			return value.toString();
		}

	};

	private static final CompileOptions DEFAULT_OPTIONS = new CompileOptions().setRetainKeyIfNull(false);

	public static class CompileOptions {
		private boolean retainKeyIfNull;
		private String startToken;
		private String endToken;

		public boolean isRetainKeyIfNull() {
			return retainKeyIfNull;
		}

		public CompileOptions setRetainKeyIfNull(boolean retainKeyIfNull) {
			this.retainKeyIfNull = retainKeyIfNull;
			return this;
		}

		public String getStartToken() {
			return startToken;
		}

		public CompileOptions setStartToken(String startToken) {
			this.startToken = startToken;
			return this;
		}

		public String getEndToken() {
			return endToken;
		}

		public CompileOptions setEndToken(String endToken) {
			this.endToken = endToken;
			return this;
		}

	}

	public static String replaceRegexKeywords(String s) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				buf.append("\\\\");
			} else if (c == '$') {
				buf.append("\\$");
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static final String compile(final String s, Map<String, Object> context, CompileOptions options,
			CompileHandler handler) {
		if (s == null) {
			return null;
		}
		if (options == null) {
			options = DEFAULT_OPTIONS;
		}
		if (handler == null) {
			handler = DEFAULT_COMPILE_HANDLER;
		}
		final String tokenStartChars = FormatUtil.parseString(options.getStartToken(), "${");
		final String tokenEndChars = FormatUtil.parseString(options.getEndToken(), "}");
		final boolean retainKeyIfNull = options.retainKeyIfNull;
		final int len = s.length();
		StringBuilder buf = new StringBuilder();
		int fromIndex = 0;
		int bracketStartIndex = 0;
		while ((bracketStartIndex = s.indexOf(tokenStartChars, fromIndex)) >= 0) {
			int keyStartIndex = bracketStartIndex + tokenStartChars.length();
			int bracketEndIndex = s.indexOf(tokenEndChars, keyStartIndex);
			if (bracketEndIndex < 0) {
				throw new RuntimeException("No bracket end: " + s.substring(bracketStartIndex));
			}
			String key = s.substring(keyStartIndex, bracketEndIndex);
			if (key.isEmpty()) {
				throw new RuntimeException("Empty key");
			}
			String value = handler.compile(key, context);
			if (bracketStartIndex > fromIndex) {
				// append head
				buf.append(s.substring(fromIndex, bracketStartIndex));
			}
			if (value == null) {
				if (retainKeyIfNull) {
					buf.append(tokenStartChars).append(key).append(tokenEndChars);
				}
			} else {
				buf.append(value);
			}
			fromIndex = bracketEndIndex + tokenEndChars.length();
		}
		if (fromIndex < len) {
			if (fromIndex == 0) {
				// not found any token
				return s;
			}
			// append tail
			buf.append(s.substring(fromIndex));
		}
		return buf.toString();
	}

	public static final String compile(String content, Map<String, Object> context, CompileOptions options) {
		return compile(content, context, options, null);
	}

	public static final String compile(String content, Map<String, Object> context) {
		return compile(content, context, null, null);
	}

}
