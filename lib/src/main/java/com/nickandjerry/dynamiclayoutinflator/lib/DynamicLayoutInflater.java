package com.nickandjerry.dynamiclayoutinflator.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.BaseViewAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.DatePickerAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.ImageViewAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.LinearLayoutAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.TextViewAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.ToolbarAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.attrsetter.ViewGroupAttrSetter;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Res;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Copyright Nicholas White 2015.
 * Source: https://github.com/nickwah/DynamicLayoutInflator
 * <p>
 * Licensed under the MIT License:
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class DynamicLayoutInflater {
    private static final String LOG_TAG = "DynamicLayoutInflater";

    private Map<String, ViewAttrSetter<?>> mViewAttrSetters = new HashMap<>();
    private Map<String, ViewCreator<?>> mViewCreators = new HashMap<>();
    private Context mContext;

    public DynamicLayoutInflater(Context context) {
        mContext = context;
        registerViewAttrSetter(TextView.class.getName(), new TextViewAttrSetter<>());
        registerViewAttrSetter(EditText.class.getName(), new TextViewAttrSetter<>());
        registerViewAttrSetter(ImageView.class.getName(), new ImageViewAttrSetter<>());
        registerViewAttrSetter(LinearLayout.class.getName(), new LinearLayoutAttrSetter<>());
        registerViewAttrSetter(View.class.getName(), new BaseViewAttrSetter<>());
        registerViewAttrSetter(Toolbar.class.getName(), new ToolbarAttrSetter<>());
        registerViewAttrSetter(DatePicker.class.getName(), new DatePickerAttrSetter());

    }

    public void registerViewAttrSetter(String fullName, ViewAttrSetter<?> setter) {
        mViewAttrSetters.put(fullName, setter);
        ViewCreator<?> creator = setter.getCreator();
        if (creator != null) {
            mViewCreators.put(fullName, creator);
        }
    }

    public View inflate(String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(inputStream);
    }

    public View inflate(String xml, ViewGroup parent) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(inputStream, parent);
    }

    public View inflate(InputStream inputStream) {
        return inflate(inputStream, null);
    }

    public View inflate(InputStream inputStream, ViewGroup parent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            return inflate(document.getDocumentElement(), parent);
        } catch (Exception e) {
            throw new InflateException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private View inflate(Node node, ViewGroup parent) {
        HashMap<String, String> attrs = getAttributesMap(node);
        View view = createViewForName(node.getNodeName(), attrs);
        if (parent != null) {
            parent.addView(view); // have to add to parent to enable certain layout attrs
        }
        applyAttributes(view, attrs, parent);
        if (view instanceof ViewGroup && node.hasChildNodes()) {
            inflateChildren(node, (ViewGroup) view);
        }
        return view;
    }

    private void inflateChildren(Node node, ViewGroup parent) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE) continue;
            inflate(currentNode, parent); // this recursively can call inflateChildren
        }
    }

    private View createViewForName(String name, HashMap<String, String> attrs) {
        try {
            if (name.equals("View")) {
                return new View(mContext);
            }
            if (!name.contains(".")) {
                name = "android.widget." + name;
            }
            ViewCreator<?> creator = mViewCreators.get(name);
            if (creator != null) {
                return creator.create(mContext, attrs);
            }
            Class<?> clazz = Class.forName(name);
            String style = attrs.get("style");
            if (style == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return (View) clazz.getConstructor(Context.class).newInstance(mContext);
            } else {
                int styleRes = Res.parseStyle(mContext, style);
                return (View) clazz.getConstructor(Context.class, AttributeSet.class, int.class, int.class)
                        .newInstance(mContext, null, 0, styleRes);
            }
        } catch (Exception e) {
            throw new InflateException(e);
        }
    }


    private HashMap<String, String> getAttributesMap(Node currentNode) {
        NamedNodeMap attributeMap = currentNode.getAttributes();
        int attributeCount = attributeMap.getLength();
        HashMap<String, String> attributes = new HashMap<>(attributeCount);
        for (int j = 0; j < attributeCount; j++) {
            Node attr = attributeMap.item(j);
            String nodeName = attr.getNodeName();
            attributes.put(nodeName, attr.getNodeValue());
        }
        return attributes;
    }

    @SuppressWarnings("unchecked")
    private void applyAttributes(View view, Map<String, String> attrs, ViewGroup parent) {
        ViewAttrSetter<View> setter = (ViewAttrSetter<View>) mViewAttrSetters.get(view.getClass().getName());
        Class c = view.getClass();
        while (setter == null && c != View.class) {
            c = c.getSuperclass();
            setter = (ViewAttrSetter<View>) mViewAttrSetters.get(c.getName());
        }
        if (setter != null) {
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String[] attr = entry.getKey().split(":");
                if (attr.length == 1) {
                    setter.setAttr(view, attr[0], entry.getValue(), parent, attrs);
                } else if (attr.length == 2) {
                    setter.setAttr(view, attr[0], attr[1], entry.getValue(), parent, attrs);
                } else {
                    throw new InflateException("illegal attr name: " + entry.getKey());
                }
            }
            setter.applyPendingAttributes(view, parent);
        } else {
            Log.e(LOG_TAG, "cannot set attributes for view: " + view.getClass());
        }

    }


}
