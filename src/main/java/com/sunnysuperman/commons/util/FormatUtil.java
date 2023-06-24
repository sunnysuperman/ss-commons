package com.sunnysuperman.commons.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FormatUtil {
	public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");
	public static final String ISO8601DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String ISO8601DATE_WITH_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String ISO8601DATE_WITH_MILLS_TIMEZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String ISO8601DATE_WITH_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	public static final String ISO8601DATE_WITH_ZONE_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final int ISO8601DATE_FORMAT_VALUE_LENGTH = ISO8601DATE_FORMAT.length() - 4;

	protected FormatUtil() {
	}

	public static class FormatException extends RuntimeException {

		public FormatException(String message) {
			super(message);
		}

		public FormatException(String message, Throwable cause) {
			super(message, cause);
		}

	}

	public static Boolean parseBoolean(Object obj, Boolean defaultValue) throws FormatException {
		Boolean bool = parseBoolean(obj);
		if (bool == null) {
			return defaultValue;
		}
		return bool;
	}

	public static Boolean parseBoolean(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj);
		}
		if (obj instanceof Number) {
			obj = obj.toString();
		}
		if (obj instanceof String) {
			String strValue = (String) obj;
			if (strValue.isEmpty()) {
				return null;
			}
			if (strValue.equalsIgnoreCase("true") || strValue.equals("1")) {
				return Boolean.TRUE;
			}
			if (strValue.equalsIgnoreCase("false") || strValue.equals("0")) {
				return Boolean.FALSE;
			}
		}
		throw new FormatException("Failed to parseBoolean: " + obj);
	}

	public static Number parseNumber(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer || obj instanceof Float || obj instanceof Double || obj instanceof Long) {
			return (Number) obj;
		}
		obj = obj.toString();
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			if (theString.indexOf('.') >= 0) {
				return Double.valueOf(theString);
			} else {
				Long longObject = Long.valueOf(theString);
				long longValue = longObject.longValue();
				if (longValue > Integer.MAX_VALUE) {
					return longObject;
				} else {
					return Integer.valueOf((int) longValue);
				}
			}
		}
		throw new FormatException("Failed to parseNumber: " + obj);
	}

	public static Byte parseByte(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Byte) {
			return (Byte) obj;
		}
		if (obj instanceof Number) {
			return Byte.valueOf(((Number) obj).byteValue());
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? (byte) 1 : (byte) 0;
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Byte.valueOf(theString);
		}
		throw new FormatException("Failed to parseByte: " + obj);
	}

	public static byte parseByteValue(Object obj, byte defaultValue) throws FormatException {
		Byte v = parseByte(obj);
		return v == null ? defaultValue : v.byteValue();
	}

	public static Short parseShort(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Short) {
			return (Short) obj;
		}
		if (obj instanceof Number) {
			return Short.valueOf(((Number) obj).shortValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Short.valueOf(theString);
		}
		throw new FormatException("Failed to parseShort: " + obj);
	}

	public static short parseShortValue(Object s, short defaultValue) throws FormatException {
		Short v = parseShort(s);
		return v == null ? defaultValue : v.shortValue();
	}

	public static Integer parseInteger(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Number) {
			return Integer.valueOf(((Number) obj).intValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Integer.valueOf(theString);
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1 : 0;
		}
		throw new FormatException("Failed to parseInteger: " + obj);
	}

	public static Integer parseRoundInteger(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Number) {
			double d = ((Number) obj).doubleValue();
			return (int) Math.round(d);
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			double d = Double.parseDouble(theString);
			return (int) Math.round(d);
		}
		throw new FormatException("Failed to parseRoundInteger: " + obj);
	}

	public static int parseIntValue(Object obj, int defaultValue) throws FormatException {
		Integer v = parseInteger(obj);
		return v == null ? defaultValue : v.intValue();
	}

	public static int parseRoundIntValue(Object obj, int defaultValue) throws FormatException {
		Integer v = parseRoundInteger(obj);
		return v == null ? defaultValue : v.intValue();
	}

	public static Long parseLong(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		if (obj instanceof Number) {
			return Long.valueOf(((Number) obj).longValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Long.valueOf(theString);
		}
		throw new FormatException("Failed to parseLong: " + obj);
	}

	public static long parseLongValue(Object obj, long defaultValue) throws FormatException {
		Long v = parseLong(obj);
		return v == null ? defaultValue : v.longValue();
	}

	public static Double parseDouble(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString()).doubleValue();
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Double.valueOf(theString);
		}
		throw new FormatException("Failed to parseDouble: " + obj);
	}

	public static double parseDoubleValue(Object obj, double defaultValue) throws FormatException {
		Double v = parseDouble(obj);
		return v == null ? defaultValue : v.doubleValue();
	}

	public static Float parseFloat(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Float) {
			return (Float) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString()).floatValue();
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.length() == 0) {
				return null;
			}
			return Float.valueOf(theString);
		}
		throw new FormatException("Failed to parseFloat: " + obj);
	}

	public static float parseFloatValue(Object obj, float defaultValue) throws FormatException {
		Float v = parseFloat(obj);
		return v == null ? defaultValue : v.floatValue();
	}

	public static String parseString(Object obj) throws FormatException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		Class<?> clazz = obj.getClass();
		if (clazz.isArray() && clazz.getComponentType().equals(byte.class)) {
			return new String((byte[]) obj, StringUtil.UTF8_CHARSET);
		}
		return obj.toString();
	}

	public static String parseString(Object obj, String defaultValue) throws FormatException {
		String s = parseString(obj);
		if (s == null) {
			return defaultValue;
		}
		return s;
	}

	public static BigDecimal parseDecimal(Object obj, BigDecimal defaultValue) throws FormatException {
		if (obj == null) {
			return defaultValue;
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}
		if (obj instanceof Long) {
			return new BigDecimal((Long) obj);
		}
		if (obj instanceof Integer) {
			return new BigDecimal((Integer) obj);
		}
		String s = obj.toString();
		if (s.isEmpty()) {
			return defaultValue;
		}
		try {
			return new BigDecimal(s);
		} catch (Exception ex) {
			throw new FormatException("Failed to parseDecimal: " + obj, ex);
		}
	}

	public static BigDecimal parseDecimal(Object value) throws FormatException {
		return parseDecimal(value, null);
	}

	public static Date parseDate(Object d) throws FormatException {
		return parseDate(d, null);
	}

	public static Date parseDate(Object d, TimeZone tz) throws FormatException {
		if (d == null) {
			return null;
		}
		if (d instanceof Date) {
			return (Date) d;
		}
		if (d instanceof Number) {
			return new Date(parseLong(d));
		}
		String s = d.toString();
		if (StringUtil.isNumeric(s)) {
			return new Date(Long.valueOf(s));
		}
		return parseISO8601Date(s, tz);
	}

	public static Date parseISO8601Date(String s, TimeZone tz) throws FormatException {
		if (s == null || s.isEmpty()) {
			return null;
		}
		try {
			Date date;
			if (s.charAt(s.length() - 1) == 'Z') {
				String format = (s.length() == ISO8601DATE_FORMAT_VALUE_LENGTH) ? ISO8601DATE_FORMAT
						: ISO8601DATE_WITH_MILLS_FORMAT;
				DateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(GMT_TIMEZONE);
				date = dateFormat.parse(s);
			} else if (s.length() == DATE_FORMAT.length()) {
				DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				dateFormat.setTimeZone(tz != null ? tz : TimeZone.getDefault());
				date = dateFormat.parse(s);
			} else if (s.indexOf('.') > 0) {
				date = new SimpleDateFormat(ISO8601DATE_WITH_ZONE_MILLS_FORMAT).parse(s);
			} else if (s.indexOf('T') > 0) {
				date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(s);
			} else {
				date = new SimpleDateFormat(ISO8601DATE_WITH_ZONE_FORMAT).parse(s);
			}
			return date;
		} catch (ParseException e) {
			throw new FormatException("Failed to parseISO8601Date", e);
		}
	}

}
