package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.view.View;

/**
 * Created by Stardust on 2017/11/4.
 */

public class Exceptions {

    private static boolean sIgnoresUnsupportedException = false;

    public static void unsupports(View v, String name, String value) {
        if (sIgnoresUnsupportedException)
            return;
        throw new UnsupportedOperationException(String.format("Attr %s:%s=\"%s\" is not supported",
                v.getClass().getSimpleName(), name, value));
    }

    public static boolean ignoresUnsupportedException() {
        return sIgnoresUnsupportedException;
    }

    public static void setIgnoreUnsupportException(boolean ignoresUnsupportedException) {
        sIgnoresUnsupportedException = ignoresUnsupportedException;
    }
}
