package org.autojs.dynamiclayoutinflater.attrsetter;

import android.view.ViewGroup;
import android.widget.RadioGroup;

import org.autojs.dynamiclayoutinflater.util.Ids;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class RadioGroupAttrSetter<V extends RadioGroup> extends LinearLayoutAttrSetter<V> {

    private Integer mCheckedButton;

    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        if (attr.equals("checkedButton")) {
            mCheckedButton = Ids.parse(value);
            return true;
        } else {
            return super.setAttr(view, attr, value, parent, attrs);
        }
    }

    @Override
    public void applyPendingAttributesAboutChildren(V view) {
        if (mCheckedButton != null) {
            view.check(mCheckedButton);
            mCheckedButton = null;
        }
    }
}
