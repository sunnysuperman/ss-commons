package com.sunnysuperman.commons.model;

import java.util.Collections;
import java.util.List;

public class PullPagination<T> {
    protected List<T> list;
    protected String marker;
    protected boolean hasMore;

    protected PullPagination() {

    }

    public static <T> PullPagination<T> newInstance(List<T> list, String marker, boolean hasMore) {
        if (list == null) {
            list = Collections.emptyList();
        }
        if (hasMore) {
            if (marker == null) {
                throw new RuntimeException("marker should not be null if hasMore");
            }
        } else {
            marker = null;
        }
        PullPagination<T> pr = new PullPagination<T>();
        pr.list = list;
        pr.marker = marker;
        pr.hasMore = hasMore;
        return pr;
    }

    public static <T> PullPagination<T> emptyInstance() {
        return newInstance(null, null, false);
    }

    public static <T> PullPagination<T> singleton(T data) {
        return newInstance(Collections.singletonList(data), null, false);
    }

    public static <T> PullPagination<T> extend(List<T> data, PullPagination<?> page) {
        return newInstance(data, page.getMarker(), page.hasMore);
    }

    public boolean empty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public List<T> getList() {
        return list;
    }

    public String getMarker() {
        return marker;
    }

    public boolean isHasMore() {
        return hasMore;
    }

}
