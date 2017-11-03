package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.os.Build;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nickandjerry.dynamiclayoutinflator.lib.util.Colors;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Dimensions;
import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflater;
import com.nickandjerry.dynamiclayoutinflator.lib.ViewAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Drawables;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Gravities;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Ids;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class BaseViewAttrSetter<V extends View> implements ViewAttrSetter<V> {

    private DynamicLayoutInflater mLayoutInflater;

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        Integer layoutRule = null;
        boolean layoutTarget = false;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        switch (attr) {
            case "id":
                view.setId(Ids.getIdFromName(value));
                break;
            case "width":
            case "layout_width":
                switch (value) {
                    case "wrap_content":
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case "fill_parent":
                    case "match_parent":
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        break;
                    default:
                        layoutParams.width = Dimensions.parseToPixel(value, view.getResources().getDisplayMetrics(), parent, true);
                        break;
                }
                break;
            case "height":
            case "layout_height":
                switch (value) {
                    case "wrap_content":
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        break;
                    case "fill_parent":
                    case "match_parent":
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        break;
                    default:
                        layoutParams.height = Dimensions.parseToPixel(value, view.getResources().getDisplayMetrics(), parent, false);
                        break;
                }
                break;
            case "layout_gravity":
                if (parent != null && parent instanceof LinearLayout) {
                    ((LinearLayout.LayoutParams) layoutParams).gravity = Gravities.parse(value);
                } else if (parent != null && parent instanceof FrameLayout) {
                    ((FrameLayout.LayoutParams) layoutParams).gravity = Gravities.parse(value);
                }
                break;
            case "layout_weight":
                if (parent != null && parent instanceof LinearLayout) {
                    ((LinearLayout.LayoutParams) layoutParams).weight = Float.parseFloat(value);
                }
                break;
            case "layout_below":
                layoutRule = RelativeLayout.BELOW;
                layoutTarget = true;
                break;
            case "layout_above":
                layoutRule = RelativeLayout.ABOVE;
                layoutTarget = true;
                break;
            case "layout_toLeftOf":
                layoutRule = RelativeLayout.LEFT_OF;
                layoutTarget = true;
                break;
            case "layout_toRightOf":
                layoutRule = RelativeLayout.RIGHT_OF;
                layoutTarget = true;
                break;
            case "layout_alignBottom":
                layoutRule = RelativeLayout.ALIGN_BOTTOM;
                layoutTarget = true;
                break;
            case "layout_alignTop":
                layoutRule = RelativeLayout.ALIGN_TOP;
                layoutTarget = true;
                break;
            case "layout_alignLeft":
            case "layout_alignStart":
                layoutRule = RelativeLayout.ALIGN_LEFT;
                layoutTarget = true;
                break;
            case "layout_alignRight":
            case "layout_alignEnd":
                layoutRule = RelativeLayout.ALIGN_RIGHT;
                layoutTarget = true;
                break;
            case "layout_alignParentBottom":
                layoutRule = RelativeLayout.ALIGN_PARENT_BOTTOM;
                break;
            case "layout_alignParentTop":
                layoutRule = RelativeLayout.ALIGN_PARENT_TOP;
                break;
            case "layout_alignParentLeft":
            case "layout_alignParentStart":
                layoutRule = RelativeLayout.ALIGN_PARENT_LEFT;
                break;
            case "layout_alignParentRight":
            case "layout_alignParentEnd":
                layoutRule = RelativeLayout.ALIGN_PARENT_RIGHT;
                break;
            case "layout_centerHorizontal":
                layoutRule = RelativeLayout.CENTER_HORIZONTAL;
                break;
            case "layout_centerVertical":
                layoutRule = RelativeLayout.CENTER_VERTICAL;
                break;
            case "layout_centerInParent":
                layoutRule = RelativeLayout.CENTER_IN_PARENT;
                break;
            case "layout_margin":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.bottomMargin = params.leftMargin = params.topMargin = params.leftMargin = Dimensions.parseToPixel(value, view);
                }
                break;
            case "layout_marginLeft":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.leftMargin = Dimensions.parseToPixel(value, view);
                }
                break;
            case "layout_marginTop":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.topMargin = Dimensions.parseToPixel(value, view);
                }
                break;
            case "layout_marginRight":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.rightMargin = Dimensions.parseToPixel(value, view);
                }
                break;
            case "layout_marginBottom":
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.bottomMargin = Dimensions.parseToPixel(value, view);
                }
                break;
            case "padding":
                int p = Dimensions.parseToPixel(value, view);
                view.setPadding(p, p, p, p);
                break;
            case "paddingLeft":
                view.setPadding(Dimensions.parseToPixel(value, view), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                break;
            case "paddingTop":
                view.setPadding(view.getLeft(), Dimensions.parseToPixel(value, view), view.getPaddingRight(), view.getPaddingBottom());
                break;
            case "paddingRight":
                view.setPadding(view.getLeft(), view.getPaddingTop(), Dimensions.parseToPixel(value, view), view.getPaddingBottom());
                break;
            case "paddingBottom":
                view.setPadding(view.getLeft(), view.getPaddingTop(), view.getPaddingRight(), Dimensions.parseToPixel(value, view));
                break;
            case "background":
                if (value.startsWith("@drawable/")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(Drawables.parse(view.getContext(), value));
                    } else {
                        view.setBackgroundDrawable(Drawables.parse(view.getContext(), value));
                    }
                } else if (value.startsWith("#") || value.startsWith("@color")) {
                    view.setBackgroundColor(Colors.parse(view.getContext(), value));
                }
                throw new InflateException("Unknown value for background: " + value);
            default:
                return false;

        }
        if (layoutRule != null && parent instanceof RelativeLayout) {
            if (layoutTarget) {
                int anchor = Ids.getIdFromName(value);
                ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule, anchor);
            } else if (value.equals("true")) {
                ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule);
            }
        }
        return true;
    }
}
