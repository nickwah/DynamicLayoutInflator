package org.autojs.dynamiclayoutinflater.attrsetter;

import android.view.View;

/**
 * Created by Stardust on 2017/11/4.
 */

public class Exceptions {

    public interface ExceptionHandler {
        boolean handleUnsupportedException(UnsupportedOperationException e, View v, String attrName, String value);
    }

    private static ExceptionHandler sExceptionHandler;

    public static void unsupports(View v, String name, String value) {
        UnsupportedOperationException e = new UnsupportedOperationException(String.format("Attr %s:%s=\"%s\" is not supported",
                v.getClass().getSimpleName(), name, value));
        if (sExceptionHandler == null || !sExceptionHandler.handleUnsupportedException(e, v, name, value)) {
            throw e;
        }

    }

    public static ExceptionHandler getExceptionHandler() {
        return sExceptionHandler;
    }

    public static void setExceptionHandler(ExceptionHandler exceptionHandler) {
        sExceptionHandler = exceptionHandler;
    }
}
