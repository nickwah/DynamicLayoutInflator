package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.view.View;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Ids {

    private static AtomicInteger maxId = new AtomicInteger(20161209);
    private static HashMap<String, Integer> ids = new HashMap<>();

    public static String parseName(String idName) {
        if (idName.startsWith("@+id/")) {
            return idName.substring(5);
        } else if (idName.startsWith("@id/")) {
            return idName.substring(4);
        }
        return idName;
    }

    public static int getIdFromName(String name) {
        name = parseName(name);
        Integer id = ids.get(name);
        if (id == null) {
            id = maxId.incrementAndGet();
            ids.put(name, id);
        }
        return id;
    }
}
