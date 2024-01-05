package com.sunnysuperman.commons.locale;

public class SimpleLocaleBundle extends LocaleBundle {

	public SimpleLocaleBundle(LocaleBundleOptions options) {
		super(options);
	}

	@Override
	public void put(String key, String locale, String value) throws Exception {
		super.put(key, locale, value);
	}

	@Override
	public void finishPut() {
		super.finishPut();
	}

}