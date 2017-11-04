package com.nickandjerry.dynamiclayoutinflator.lib.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.View;

import com.nickandjerry.dynamiclayoutinflator.lib.ImageLoader;
import com.nickandjerry.dynamiclayoutinflator.lib.R;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Drawables {

    private static ImageLoader sImageLoader;


    public static Drawable parse(Context context, String value) {
        Resources resources = context.getResources();
        if (value.startsWith("@color/") || value.startsWith("@android:color/") || value.startsWith("#")) {
            return new ColorDrawable(Colors.parse(context, value));
        }
        if (value.startsWith("?")) {
            int[] attr = {resources.getIdentifier(value.substring(1), "attr",
                    context.getPackageName())};
            TypedArray ta = context.obtainStyledAttributes(attr);
            Drawable drawable = ta.getDrawable(0 /* index */);
            ta.recycle();
            return drawable;
        }
        if (value.startsWith("file://")) {
            return decodeImage(value);
        }
        if (value.startsWith("http://")) {
            return loadImage(value);
        }

        return resources.getDrawable(resources.getIdentifier(value, "drawable",
                context.getPackageName()));
    }

    private static Drawable decodeImage(String path) {
        return new BitmapDrawable(BitmapFactory.decodeFile(path));
    }

    private static Drawable loadImage(String url) {
        return null;
    }

    public static Drawable parse(View view, String name) {
        return parse(view.getContext(), name);
    }


}
