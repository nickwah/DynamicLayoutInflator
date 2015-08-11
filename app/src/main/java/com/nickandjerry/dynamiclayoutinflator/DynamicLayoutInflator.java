package com.nickandjerry.dynamiclayoutinflator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Copyright Nicholas White 2015.
 *
 * Licensed under the MIT License:
 *
 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
public class DynamicLayoutInflator {
    private static final String ns = null;
    public static final int NO_LAYOUT_RULE = -999;
    public static int highestIdNumberUsed = 1234567;

    public interface ImageLoader {
        public void loadImage(ImageView view, String url);
    }

    private static ImageLoader imageLoader = null;
    public static void setImageLoader(ImageLoader il) {
        imageLoader = il;
    }

    public static class DynamicLayoutInfo {
        public DynamicLayoutInfo() {
            nameToIdNumber = new HashMap<>();
        }
        public HashMap<String, Integer> nameToIdNumber;
        public Object delegate;
    }

    public static void setDelegate(View root, Object delegate) {
        DynamicLayoutInfo info;
        if (root.getTag() == null || !(root.getTag() instanceof DynamicLayoutInfo)) {
            info = new DynamicLayoutInfo();
            root.setTag(info);
        } else {
            info = (DynamicLayoutInfo)root.getTag();
        }
        info.delegate = delegate;
    }

    public static View inflate(Context context, File xmlPath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return DynamicLayoutInflator.inflate(context, inputStream);
    }

    public static View inflate(Context context, String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return DynamicLayoutInflator.inflate(context, inputStream);
    }

    public static View inflate(Context context, InputStream inputStream) {
        return inflate(context, inputStream, null);
    }
    public static View inflate(Context context, InputStream inputStream, ViewGroup parent) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputStream);
            try {
                return inflate(context, document.getDocumentElement(), parent);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static View inflate(Context context, Node node) {
        return inflate(context, node, null);
    }
    public static View inflate(Context context, Node node, ViewGroup parent) {
        View mainView = getViewForName(context, node.getNodeName());
        if (mainView instanceof ViewGroup && node.hasChildNodes()) {
            parseChildren(context, node, (ViewGroup) mainView);
        }
        if (parent != null) parent.addView(mainView); // have to add to parent to enable certain layout attrs
        applyAttributes(mainView, getAttributesMap(node), parent);
        return mainView;
    }

    private static void parseChildren(Context context, Node node, ViewGroup mainView) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE) continue;
            inflate(context, currentNode, mainView); // this recursively can call parseChildren
        }
    }

    private static View getViewForName(Context context, String name) {
        try {
            if (!name.contains(".")) {
                name = "android.widget." + name;
            }
            Class<?> clazz = Class.forName(name);
            Constructor<?> constructor = clazz.getConstructor(Context.class);
            return (View)constructor.newInstance(context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, String> getAttributesMap(Node currentNode) {
        NamedNodeMap attributeMap = currentNode.getAttributes();
        int attributeCount = attributeMap.getLength();
        HashMap<String, String> attributes = new HashMap<>(attributeCount);
        for (int j = 0; j < attributeCount; j++) {
            Node attr = attributeMap.item(j);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        return attributes;
    }

    @SuppressLint("NewApi")
    private static void applyAttributes(View view, Map<String, String> attrs, ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int layoutRule;
        int marginLeft = 0, marginRight = 0, marginTop = 0, marginBottom = 0,
        paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;
        for (Map.Entry<String,String> entry : attrs.entrySet()) {
            String attr = entry.getKey();
            if (attr.startsWith("android:")) attr = attr.substring(8);
            layoutRule = NO_LAYOUT_RULE;
            boolean layoutTarget = false;
            switch (attr) {
                case "id":
                    String idValue = parseId(entry.getValue());
                    if (parent != null) {
                        DynamicLayoutInfo info;
                        if (parent.getTag() != null && parent.getTag() instanceof DynamicLayoutInfo) {
                            info = (DynamicLayoutInfo)parent.getTag();
                        } else {
                            info = new DynamicLayoutInfo();
                            parent.setTag(info);
                        }
                        int newId = highestIdNumberUsed++;
                        view.setId(newId);
                        info.nameToIdNumber.put(idValue, newId);
                    }
                    break;
                case "width":
                    switch (entry.getValue()) {
                        case "wrap_content":
                            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            break;
                        case "fill_parent":
                        case "match_parent":
                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            break;
                        default:
                            layoutParams.width = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                            break;
                    }
                    break;
                case "height":
                    switch (entry.getValue()) {
                        case "wrap_content":
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            break;
                        case "fill_parent":
                        case "match_parent":
                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            break;
                        default:
                            layoutParams.height = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                            break;
                    }
                    break;
                case "layout_gravity":
                    if (layoutParams instanceof LinearLayout.LayoutParams) {
                        ((LinearLayout.LayoutParams)layoutParams).gravity = parseGravity(entry.getValue());
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
                    marginLeft = marginRight = marginTop = marginBottom = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "layout_marginLeft":
                    marginLeft = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "layout_marginTop":
                    marginTop = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "layout_marginRight":
                    marginRight = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "layout_marginBottom":
                    marginBottom = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "padding":
                    paddingBottom = paddingLeft = paddingRight = paddingTop = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "paddingLeft":
                    paddingLeft = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "paddingTop":
                    paddingTop = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "paddingRight":
                    paddingRight = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "paddingBottom":
                    paddingBottom = DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics());
                    break;
                case "text":
                    if (view instanceof TextView) {
                        ((TextView)view).setText(entry.getValue());
                    }
                    break;
                case "textColor":
                    if (view instanceof TextView) {
                        ((TextView)view).setTextColor(parseColor(entry.getValue()));
                    }
                    break;
                case "textSize":
                    if (view instanceof TextView) {
                        ((TextView)view).setTextSize(DimensionConverter.stringToDimensionPixelSize(entry.getValue(), view.getResources().getDisplayMetrics()));
                    }
                    break;
                case "textStyle":
                    if (view instanceof TextView) {
                        int typeFace = Typeface.NORMAL;
                        if (entry.getValue().contains("bold")) typeFace |= Typeface.BOLD;
                        else  if (entry.getValue().contains("italic")) typeFace |= Typeface.ITALIC;
                        ((TextView) view).setTypeface(null, typeFace);
                    }
                    break;
                case "textAlignment":
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        int alignment = View.TEXT_ALIGNMENT_TEXT_START;
                        switch (entry.getValue()) {
                            case "center":
                                alignment = View.TEXT_ALIGNMENT_CENTER;
                                break;
                            case "left":
                            case "textStart":
                                break;
                            case "right":
                            case "textEnd":
                                alignment = View.TEXT_ALIGNMENT_TEXT_END;
                                break;
                        }
                        view.setTextAlignment(alignment);
                    } else {
                        int gravity = Gravity.LEFT;
                        switch (entry.getValue()) {
                            case "center":
                                gravity = Gravity.CENTER;
                                break;
                            case "left":
                            case "textStart":
                                break;
                            case "right":
                            case "textEnd":
                                gravity = Gravity.RIGHT;
                                break;
                        }
                        ((TextView)view).setGravity(gravity);
                    }
                    break;
                case "gravity":
                    int gravity = parseGravity(entry.getValue());
                    if (view instanceof TextView) {
                        ((TextView) view).setGravity(gravity);
                    } else if (view instanceof LinearLayout) {
                        ((LinearLayout)view).setGravity(gravity);
                    } else if (view instanceof RelativeLayout) {
                        ((RelativeLayout)view).setGravity(gravity);
                    }
                    break;
                case "background":
                    String bgValue = entry.getValue();
                    if (bgValue.startsWith("@drawable/")) {
                        view.setBackground(getDrawableByName(view, bgValue));
                    } else if (bgValue.startsWith("#")) {
                        view.setBackgroundColor(parseColor(bgValue));
                    }
                    break;
                case "src":
                    if (view instanceof ImageView) {
                        String imageName = entry.getValue();
                        if (imageName.startsWith("//")) imageName = "http:" + imageName;
                        if (imageName.startsWith("http")) {
                            if (imageLoader != null) imageLoader.loadImage((ImageView)view, imageName);
                        } else if (imageName.startsWith("@drawable/")) {
                            imageName = imageName.substring("@drawable/".length());
                            ((ImageView)view).setImageDrawable(getDrawableByName(view, imageName));
                        }
                    }
                    break;
                case "visibility":
                    int visibility = View.VISIBLE;
                    String visValue = entry.getValue().toLowerCase();
                    if (visValue.equals("gone")) visibility = View.GONE;
                    else if (visValue.equals("invisible")) visibility = View.INVISIBLE;
                    view.setVisibility(visibility);
                    break;
                case "onClick":
                    view.setOnClickListener(getClickListener(parent, entry.getValue()));
                    break;

            }
            if (layoutRule != NO_LAYOUT_RULE && parent instanceof RelativeLayout) {
                if (layoutTarget) {
                    int anchor = idNumFromIdString(parent, parseId(entry.getValue()));
                    ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule, anchor);
                } else if (entry.getValue().equals("true")) {
                    ((RelativeLayout.LayoutParams) layoutParams).addRule(layoutRule);
                }
            }
        }
        if (marginLeft > 0 || marginTop > 0 || marginRight > 0 || marginBottom > 0) {
            ((RelativeLayout.LayoutParams) layoutParams).setMargins(marginLeft, marginTop, marginRight, marginBottom);
        }
        if (paddingBottom > 0 || paddingLeft > 0 || paddingRight > 0 || paddingTop > 0) {
            view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        view.setLayoutParams(layoutParams);
    }

    private static View.OnClickListener getClickListener(final ViewGroup myParent, final String methodName) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup root = myParent;
                DynamicLayoutInfo info = null;
                while (root != null && (root.getParent() instanceof ViewGroup)) {
                    if (root.getTag() != null && root.getTag() instanceof DynamicLayoutInfo) {
                        info = (DynamicLayoutInfo)root.getTag();
                        if (info.delegate != null) break;
                    }
                    root = (ViewGroup)root.getParent();
                }
                if (info != null && info.delegate != null) {
                    Object[] args = null;
                    String finalMethod = methodName;
                    if (methodName.endsWith(")")) {
                        String[] parts = methodName.split("[(]", 2);
                        finalMethod = parts[0];
                        try {
                            String argText = parts[1].replace("&quot;", "\"");
                            JSONArray arr = new JSONArray("[" + argText.substring(0, argText.length() - 1) + "]");
                            args = new Object[arr.length()];
                            for (int i = 0; i < arr.length(); i++) {
                                args[i] = arr.get(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    final Object delegate = info.delegate;
                    Class<?> klass = delegate.getClass();
                    try {

                        Class<?>[] argClasses = null;
                        if (args != null && args.length > 0) {
                            argClasses = new Class[args.length];
                            for (int i = 0; i < args.length; i++) {
                                Class<?> argClass = args[i].getClass();
                                if (argClass == Integer.class) argClass = int.class; // Nobody uses Integer...
                                argClasses[i] = argClass;
                            }
                        }
                        Method method = klass.getMethod(finalMethod, argClasses);
                        method.invoke(delegate, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("DynamicLayoutInflator", "Unable to find valid delegate for click named " + methodName);
                }
            }
        };
    }

    private static String parseId(String value) {
        if (value.startsWith("@+id/")) {
            return value.substring(5);
        } else if (value.startsWith("@id/")) {
            return value.substring(4);
        }
        return value;
    }

    private static int parseGravity(String value) {
        int gravity = Gravity.NO_GRAVITY;
        String[] parts = value.toLowerCase().split("[|]");
        for (String part : parts) {
            switch (part) {
                case "center":
                    gravity = gravity | Gravity.CENTER;
                    break;
                case "left":
                case "textStart":
                    gravity = gravity | Gravity.LEFT;
                    break;
                case "right":
                case "textEnd":
                    gravity = gravity | Gravity.RIGHT;
                    break;
                case "top":
                    gravity = gravity | Gravity.TOP;
                    break;
                case "bottom":
                    gravity = gravity | Gravity.BOTTOM;
                    break;
                case "center_horizontal":
                    gravity = gravity | Gravity.CENTER_HORIZONTAL;
                    break;
                case "center_vertical":
                    gravity = gravity | Gravity.CENTER_VERTICAL;
                    break;
            }
        }
        return gravity;
    }

    public static int idNumFromIdString(View view, String id) {
        if (!(view instanceof ViewGroup)) return 0;
        Object tag = view.getTag();
        if (!(tag instanceof DynamicLayoutInfo)) return 0; // not inflated by this class
        DynamicLayoutInfo info = (DynamicLayoutInfo)view.getTag();
        if (!info.nameToIdNumber.containsKey(id)) return 0;
        return info.nameToIdNumber.get(id);
    }

    public static View findViewByIdString(View view, String id) {
        int idNum = idNumFromIdString(view, id);
        if (idNum == 0) return null;
        return view.findViewById(idNum);
    }

    public static int parseColor(String text) {
        return Color.parseColor(text);
    }

    public static Drawable getDrawableByName(View view, String name) {
        Resources resources = view.getResources();
        return resources.getDrawable(resources.getIdentifier(name, "drawable",
                view.getContext().getPackageName()));
    }

}
