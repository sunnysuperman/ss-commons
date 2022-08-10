package com.sunnysuperman.commons.locale;

import java.util.Collection;

import com.sunnysuperman.commons.util.StringUtil;

public class LocaleUtil {
	private static final char LOCALE_SEPERATOR_CHAR = '_';

	public static String formatLocale(String locale) {
		if (StringUtil.isEmpty(locale)) {
			return null;
		}
		int langOffset = locale.lastIndexOf(LOCALE_SEPERATOR_CHAR);
		if (langOffset <= 0) {
			return locale;
		}
		if (langOffset == locale.length() - 1) {
			return locale.substring(0, langOffset);
		}
		return locale.substring(0, langOffset) + locale.substring(langOffset).toUpperCase();
	}

	public static String getParentLocale(String locale) {
		int index = locale.indexOf(LOCALE_SEPERATOR_CHAR);
		if (index > 0 && index < locale.length() - 1) {
			return locale.substring(0, index);
		}
		return null;
	}

	public static String findSupportLocale(String locale, Collection<String> supportedLocales) {
		if (locale == null) {
			return null;
		}
		if (supportedLocales.contains(locale)) {
			return locale;
		}
		String parent = getParentLocale(locale);
		if (parent == null) {
			parent = locale;
		}
		for (String other : supportedLocales) {
			String otherParent = getParentLocale(other);
			if (otherParent == null) {
				otherParent = other;
			}
			if (otherParent.equals(parent)) {
				return other;
			}
		}
		return null;
	}

}
