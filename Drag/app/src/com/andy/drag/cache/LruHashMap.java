package com.andy.drag.cache;

import java.util.LinkedHashMap;

/**
 * Custom {@link java.util.HashMap} using a LRU policy.
 *
 * @param <K> key
 * @param <V> value
 * @author Shintaro Katafuchi
 */
final class LruHashMap<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public LruHashMap(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Entry entry) {
        return size() > capacity;
    }

}
