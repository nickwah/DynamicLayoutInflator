package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.text.util.Linkify;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.util.ValueMapper;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class TextViewAttrSetter<V extends TextView> extends BaseViewAttrSetter<V> {

    private static final ValueMapper<Integer> AUTO_LINK_MASKS = new ValueMapper<Integer>("autoLink")
            .map("all", Linkify.ALL)
            .map("email", Linkify.EMAIL_ADDRESSES)
            .map("map", Linkify.MAP_ADDRESSES)
            .map("none", 0)
            .map("phone", Linkify.PHONE_NUMBERS)
            .map("web", Linkify.WEB_URLS);

    @Override
    public boolean setAttr(V view, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attrName) {
            case "autoLink":
                view.setAutoLinkMask(AUTO_LINK_MASKS.getOrThrow(value));
                break;
            case "autoText":
                // TODO: 2017/11/3
                throw new UnsupportedOperationException("autoText");
            case "capitalize":
                view.setKeyListener(TextKeyListener.getInstance(false, TextKeyListener.Capitalize.CHARACTERS));
                throw new UnsupportedOperationException("capitalize");
            case "cursorVisible":
                view.setCursorVisible(Boolean.valueOf(value));
                break;
            case "digit":
                if (value.equals("true")) {
                    view.setKeyListener(DigitsKeyListener.getInstance());
                } else if (!value.equals("false")) {
                    view.setKeyListener(DigitsKeyListener.getInstance(value));
                }
                break;
        }
        return super.setAttr(view, attrName, value, parent, attrs);
    }
}
