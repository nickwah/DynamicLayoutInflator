package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Stardust on 2017/11/5.
 */

public class Res {
    public static int parseStyle(View view, String value) {
        return parseStyle(view.getContext(), value);
    }

    public static int parseStyle(Context context, String value) {
        // FIXME: 2017/11/5 Can or should it retrieve android.R.style or styleable?
        return context.getResources().getIdentifier(value, "style", context.getPackageName());
    }
}
