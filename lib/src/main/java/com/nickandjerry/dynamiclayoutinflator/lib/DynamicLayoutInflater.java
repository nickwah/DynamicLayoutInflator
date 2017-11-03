package com.nickandjerry.dynamiclayoutinflator.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;

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
    private static final int NO_LAYOUT_RULE = Integer.MIN_VALUE;
    private static final String[] CORNERS = {"TopLeft", "TopRight", "BottomRight", "BottomLeft"};

    private Map<String, ViewAttrSetter<View>> mViewAttrSetters;
    private int highestIdNumberUsed = 1234567;
    private ImageLoader mImageLoader = null;
    private Context mContext;

    public void setImageLoader(ImageLoader loader) {
        mImageLoader = loader;
    }


    public View inflate(Context context, String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(context, inputStream);
    }

    public View inflate(Context context, String xml, ViewGroup parent) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return inflate(context, inputStream, parent);
    }

    public View inflate(Context context, InputStream inputStream) {
        return inflate(context, inputStream, null);
    }

    public View inflate(Context context, InputStream inputStream, ViewGroup parent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            return inflate(context, document.getDocumentElement(), parent);
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

    private View inflate(Context context, Node node, ViewGroup parent) {
        View mainView = createViewForName(context, node.getNodeName());
        if (parent != null) {
            parent.addView(mainView); // have to add to parent to enable certain layout attrs
        }
        applyAttributes(mainView, getAttributesMap(node), parent);
        if (mainView instanceof ViewGroup && node.hasChildNodes()) {
            inflateChildren(context, node, (ViewGroup) mainView);
        }
        return mainView;
    }

    private void inflateChildren(Context context, Node node, ViewGroup mainView) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE) continue;
            inflate(context, currentNode, mainView); // this recursively can call inflateChildren
        }
    }

    private View createViewForName(Context context, String name) {
        try {
            if (!name.contains(".")) {
                name = "android.widget." + name;
            }
            Class<?> clazz = Class.forName(name);
            Constructor<?> constructor = clazz.getConstructor(Context.class);
            return (View) constructor.newInstance(context);
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
            if (nodeName.startsWith("android:")) nodeName = nodeName.substring(8);
            attributes.put(nodeName, attr.getNodeValue());
        }
        return attributes;
    }

    private void applyAttributes(View view, Map<String, String> attrs, ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int marginLeft = 0, marginRight = 0, marginTop = 0, marginBottom = 0,
                paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            String attr = entry.getKey();
            ViewAttrSetter<View> setter = mViewAttrSetters.get(view.getClass().getName());
            if (setter != null) {
                setter.setAttr(view, attr, entry.getValue(), parent, attrs);
            } else {
                Log.e(LOG_TAG, "cannot set attributes for view: " + view.getClass());
            }
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams)
                    .setMargins(marginLeft, marginTop, marginRight, marginBottom);
        }
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        view.setLayoutParams(layoutParams);
    }


}
