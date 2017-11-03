package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Drawables {

    public static Drawable parse(Context context, String name){
        Resources resources = context.getResources();
        return resources.getDrawable(resources.getIdentifier(name, "drawable",
                context.getPackageName()));
    }
}
