package org.autojs.dynamiclayoutinflater.attrsetter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.autojs.dynamiclayoutinflater.ViewCreator;
import org.autojs.dynamiclayoutinflater.util.Dimensions;
import org.autojs.dynamiclayoutinflater.util.Drawables;
import org.autojs.dynamiclayoutinflater.util.Strings;
import org.autojs.dynamiclayoutinflater.util.ValueMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class SpinnerAttrSetter extends BaseViewAttrSetter<Spinner> {

    protected static final ValueMapper<Integer> SPINNER_MODES = new ValueMapper<Integer>("spinnerMode")
            .map("dialog", Spinner.MODE_DIALOG)
            .map("dropdown", Spinner.MODE_DROPDOWN);

    @Override
    public boolean setAttr(Spinner view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "dropDownHorizontalOffset":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setDropDownHorizontalOffset(Dimensions.parseToIntPixel(value, view));
                }
                break;
            case "dropDownSelector":
                Exceptions.unsupports(view, attr, value);
                break;
            case "dropDownVerticalOffset":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setDropDownVerticalOffset(Dimensions.parseToIntPixel(value, view));
                }
                break;
            case "dropDownWidth":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setDropDownWidth(Dimensions.parseToIntPixel(value, view));
                }
                break;
            case "popupBackground":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setPopupBackgroundDrawable(Drawables.parse(view, value));
                }
                break;
            case "prompt":
                view.setPrompt(Strings.parse(view, value));
                break;
            case "entries":
                view.setAdapter(new ArrayAdapter<>(view.getContext(),
                        android.R.layout.simple_spinner_dropdown_item, value.split("[|]")));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<Spinner> getCreator() {
        return new ViewCreator<Spinner>() {
            @Override
            public Spinner create(Context context, Map<String, String> attrs) {
                String mode = attrs.remove("android:spinnerMode");
                if (mode == null) {
                    return new Spinner(context);
                }
                return new Spinner(context, SPINNER_MODES.get(mode));
            }
        };
    }

    private static class EntryAdapter extends SimpleAdapter {

        public EntryAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }
}
