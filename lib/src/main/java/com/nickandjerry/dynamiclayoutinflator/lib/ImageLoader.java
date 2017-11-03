package com.nickandjerry.dynamiclayoutinflator.lib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by Stardust on 2017/11/3.
 */

public interface ImageLoader {

    void loadImageInto(ImageView view, Uri uri);

    Drawable loadImage(Context context, Uri uri);

}
