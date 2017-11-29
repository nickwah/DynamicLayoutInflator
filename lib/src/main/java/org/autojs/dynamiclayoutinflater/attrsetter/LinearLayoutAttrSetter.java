package org.autojs.dynamiclayoutinflater.attrsetter;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.autojs.dynamiclayoutinflater.util.Drawables;
import org.autojs.dynamiclayoutinflater.util.Gravities;
import org.autojs.dynamiclayoutinflater.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/4.
 */

public class LinearLayoutAttrSetter<V extends LinearLayout> extends ViewGroupAttrSetter<V> {

    private static final ValueMapper<Integer> ORIENTATIONS = new ValueMapper<Integer>("orientation")
            .map("vertical", LinearLayout.VERTICAL)
            .map("horizontal", LinearLayout.HORIZONTAL);

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "baselineAligned":
                view.setBaselineAligned(Boolean.valueOf(value));
                break;
            case "baselineAlignedChildIndex":
                view.setBaselineAlignedChildIndex(Integer.valueOf(value));
                break;
            case "divider":
                view.setDividerDrawable(Drawables.parse(view, value));
                break;
            case "gravity":
                view.setGravity(Gravities.parse(value));
                break;
            case "measureWithLargestChild":
                view.setMeasureWithLargestChildEnabled(Boolean.valueOf(value));
                break;
            case "orientation":
                view.setOrientation(ORIENTATIONS.get(value));
                break;
            case "weightSum":
                view.setWeightSum(Float.valueOf(value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }
}
