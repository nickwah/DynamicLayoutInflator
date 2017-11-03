package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.view.InflateException;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class ImageViewAttrSetter<V extends ImageView> extends BaseViewAttrSetter<V> {

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "scaleType":
                view.setScaleType(parseScaleType(value));
                break;
        }
        return super.setAttr(view, attr, value, parent, attrs);
    }

    private ImageView.ScaleType parseScaleType(String value) {
        switch (value.toLowerCase()) {
            case "center":
                return ImageView.ScaleType.CENTER;
            break;
            case "center_crop":
                return ImageView.ScaleType.CENTER_CROP;
            break;
            case "center_inside":
                return ImageView.ScaleType.CENTER_INSIDE;
            break;
            case "fit_center":
                return ImageView.ScaleType.FIT_CENTER;
            break;
            case "fit_end":
                return ImageView.ScaleType.FIT_END;
            break;
            case "fit_start":
                return ImageView.ScaleType.FIT_START;
            break;
            case "fit_xy":
                return ImageView.ScaleType.FIT_XY;
            break;
            case "matrix":
                return ImageView.ScaleType.MATRIX;
            break;
        }
        throw new InflateException("unknown scale type: " + value);
    }
}
