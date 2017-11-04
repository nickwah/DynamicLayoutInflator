package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.view.View;

/**
 * Created by Stardust on 2017/11/4.
 */

public class Util {

    public static void unsupports(View v, String name, String value){
        throw new UnsupportedOperationException(String.format("Attr %s:%s=\"%s\" is not supported",
                v.getClass().getSimpleName(), name, value));
    }
}
