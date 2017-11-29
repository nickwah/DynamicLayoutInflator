package org.autojs.dynamiclayoutinflater.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import org.autojs.dynamiclayoutinflater.ImageLoader;

import java.net.URL;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Drawables {

    private static ImageLoader sImageLoader = new ImageLoader() {
        @Override
        public void loadInto(final ImageView view, Uri uri) {
            load(view, uri, new DrawableCallback() {
                @Override
                public void onLoaded(Drawable drawable) {
                    view.setImageDrawable(drawable);
                }
            });
        }

        @Override
        public void loadIntoBackground(final View view, Uri uri) {
            load(view, uri, new DrawableCallback() {
                @Override
                public void onLoaded(Drawable drawable) {
                    view.setBackgroundDrawable(drawable);
                }
            });
        }

        @Override
        public Drawable load(View view, Uri uri) {
            try {
                URL url = new URL(uri.toString());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return new BitmapDrawable(bmp);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void load(View view, Uri uri, final DrawableCallback callback) {
            load(view, uri, new BitmapCallback() {
                @Override
                public void onLoaded(Bitmap bitmap) {
                    callback.onLoaded(new BitmapDrawable(bitmap));
                }
            });
        }

        @Override
        public void load(final View view, final Uri uri, final BitmapCallback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(uri.toString());
                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onLoaded(bmp);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    };

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
            return decodeImage(value.substring(7));
        }
        return resources.getDrawable(resources.getIdentifier(value, "drawable",
                context.getPackageName()));
    }

    private static Drawable decodeImage(String path) {
        return new BitmapDrawable(BitmapFactory.decodeFile(path));
    }


    public static Drawable parse(View view, String name) {
        return parse(view.getContext(), name);
    }

    public static void loadInto(ImageView view, Uri uri) {
        sImageLoader.loadInto(view, uri);
    }

    public static void loadIntoBackground(View view, Uri uri) {
        sImageLoader.loadIntoBackground(view, uri);
    }

    public static <V extends ImageView> void setupWithImage(V view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadInto(view, Uri.parse(value));
        } else {
            view.setImageDrawable(Drawables.parse(view, value));
        }
    }

    public static void setupWithViewBackground(View view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadIntoBackground(view, Uri.parse(value));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(Drawables.parse(view, value));
            } else {
                view.setBackgroundDrawable(Drawables.parse(view, value));
            }
        }
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        sImageLoader = imageLoader;
    }

    public static ImageLoader getImageLoader() {
        return sImageLoader;
    }
}
