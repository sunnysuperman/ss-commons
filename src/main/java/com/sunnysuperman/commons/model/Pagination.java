package com.sunnysuperman.commons.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pagination<T> {
    public static final int DEFAULT_PAGESIZE = 10;

    public static <T> Pagination<T> emptyInstance(int limit) {
        Collection<T> list = Collections.emptyList();
        return new Pagination<T>(list, 0, 0, limit);
    }

    public static <T> Pagination<T> singleton(T t, int limit) {
        List<T> list = Collections.singletonList(t);
        return new Pagination<T>(list, 1, 0, limit);
    }

    protected Collection<T> items;
    protected int total;
    protected int offset;
    protected int limit = DEFAULT_PAGESIZE;

    public Pagination() {
        super();
    }

    public Pagination(Collection<T> items, int total, int offset, int limit) {
        this.items = items;
        this.total = total;
        this.offset = offset;
        this.limit = limit <= 0 ? DEFAULT_PAGESIZE : limit;
    }

    public boolean empty() {
        return items == null || items.isEmpty();
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
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
        this.limit = limit;
    }

}
