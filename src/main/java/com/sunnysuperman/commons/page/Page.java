package com.sunnysuperman.commons.page;

import java.util.Collections;
import java.util.List;

public class Page<T> {
	public static final int DEFAULT_PAGESIZE = 10;

	public static <T> Page<T> of(List<T> content, int total, int offset, int limit) {
		return new Page<T>(content, total, offset, limit);
	}

	public static <T> Page<T> empty(int limit) {
		return new Page<T>(Collections.emptyList(), 0, 0, limit);
	}

	public static <T> Page<T> singleton(T t, int limit) {
		return new Page<T>(Collections.singletonList(t), 1, 0, limit);
	}

	protected List<T> content;
	protected int total;
	protected int offset;
	protected int limit = DEFAULT_PAGESIZE;

	public Page() {
		super();
	}

	public Page(List<T> content, int total, int offset, int limit) {
		this.content = content;
		this.total = total;
		this.offset = offset;
		setLimit(limit);
	}

	public int getPages() {
		return total % limit == 0 ? total / limit : (total / limit + 1);
	}

	public int getPageNo() {
		return offset / limit + 1;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit <= 0 ? DEFAULT_PAGESIZE : limit;
	}

}
