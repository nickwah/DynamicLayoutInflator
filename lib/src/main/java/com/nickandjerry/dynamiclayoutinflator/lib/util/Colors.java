package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Colors {

    public static int parse(Context context, String color) {
        Resources resources = context.getResources();
        if (color.startsWith("@color/")) {
            return resources.getColor(resources.getIdentifier(color.substring("@color/".length()), "color", context.getPackageName()));
        }
        if (color.length() == 4 && color.startsWith("#")) {
            color = "#" + color.charAt(1) + color.charAt(1) + color.charAt(2) + color.charAt(2) + color.charAt(3) + color.charAt(3);
        }
        return Color.parseColor(color);
    }
}
