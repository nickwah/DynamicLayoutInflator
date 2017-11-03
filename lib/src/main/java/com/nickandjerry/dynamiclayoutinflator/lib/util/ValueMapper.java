package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.view.InflateException;

import java.util.HashMap;

/**
 * Created by Stardust on 2017/11/3.
 */

public class ValueMapper<V> {

    private HashMap<String, V> mHashMap = new HashMap<>();
    private String mAttrName;

    public ValueMapper(String attrName) {
        mAttrName = attrName;
    }

    public ValueMapper<V> map(String key, V value) {
        mHashMap.put(key, value);
        return this;
    }

    public V getOrThrow(String key) {
        V v = mHashMap.get(key);
        if (v == null) {
            throw new InflateException(String.format("unknown value for %s: %s", mAttrName, key));
        }
        return v;
    }

}
