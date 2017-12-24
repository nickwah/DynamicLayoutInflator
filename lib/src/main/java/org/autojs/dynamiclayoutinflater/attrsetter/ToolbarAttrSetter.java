package org.autojs.dynamiclayoutinflater.attrsetter;

import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import org.autojs.dynamiclayoutinflater.util.Strings;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/5.
 */

public class ToolbarAttrSetter<V extends Toolbar> extends BaseViewAttrSetter<V> {


    @Override
    public boolean setAttr(V view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        return super.setAttr(view, attr, value, parent, attrs);
    }

    @Override
    public boolean setAttr(V view, String ns, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        if (super.setAttr(view, ns, attrName, value, parent, attrs)) {
            return true;
        }
        if (!ns.equals("app")) {
            return false;
        }
        switch (attrName) {
            case "title":
                view.setTitle(Strings.parse(view, value));
                break;
            default:
                return false;
        }
        return true;
    }
}
