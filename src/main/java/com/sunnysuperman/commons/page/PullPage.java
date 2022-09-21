package com.sunnysuperman.commons.page;

import java.util.Collections;
import java.util.List;

public class PullPage<T> {
	protected List<T> content;
	protected String marker;
	protected boolean hasMore;

	public static <T> PullPage<T> of(List<T> content, String marker, boolean hasMore) {
		if (content == null) {
			content = Collections.emptyList();
		}
		if (hasMore) {
			if (marker == null) {
				throw new RuntimeException("marker should not be null if hasMore");
			}
		} else {
			marker = null;
		}
		PullPage<T> pr = new PullPage<T>();
		pr.content = content;
		pr.marker = marker;
		pr.hasMore = hasMore;
		return pr;
	}

	public static <T> PullPage<T> empty() {
		return of(null, null, false);
	}

	public static <T> PullPage<T> singleton(T data) {
		return of(Collections.singletonList(data), null, false);
	}

	public static <T> PullPage<T> extend(PullPage<?> page, List<T> data) {
		return of(data, page.getMarker(), page.hasMore);
	}

	public boolean hasContent() {
		return content != null && content.size() > 0;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

}
