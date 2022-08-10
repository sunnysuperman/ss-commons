package com.sunnysuperman.commons.page;

import com.sunnysuperman.commons.util.StringUtil;

public class PullPageRequest {
	protected String marker;
	protected int limit = Page.DEFAULT_PAGESIZE;

	public PullPageRequest() {
		super();
	}

	public PullPageRequest(String marker, int limit) {
		super();
		setMarker(marker);
		setLimit(limit);
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = StringUtil.emptyToNull(marker);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit");
		}
		this.limit = limit;
	}

	public static PullPageRequest of(String marker, int limit) {
		return new PullPageRequest(marker, limit);
	}
}
