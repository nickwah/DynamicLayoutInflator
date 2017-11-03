package com.nickandjerry.dynamiclayoutinflator.lib;

import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.util.Dimensions;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Tmp {


    @Nullable
    public static View findViewByIdString(View view, String id) {
        int idNum = idNumFromIdString(view, id);
        if (idNum == 0) return null;
        return view.findViewById(idNum);
    }

    public static int adjustBrightness(int color, float amount) {
        int red = color & 0xFF0000 >> 16;
        int green = color & 0x00FF00 >> 8;
        int blue = color & 0x0000FF;
        int result = (int) (blue * amount);
        result += (int) (green * amount) << 8;
        result += (int) (red * amount) << 16;
        return result;
    }

    public static void createViewRunnables() {

        mViewAttrSetters.put("orientation", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof LinearLayout) {
                    ((LinearLayout) view).setOrientation(value.equals("vertical") ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
                }
            }
        });
        mViewAttrSetters.put("text", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    ((TextView) view).setText(value);
                }
            }
        });
        mViewAttrSetters.put("textSize", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, Dimensions.stringToDimension(value, view.getResources().getDisplayMetrics()));
                }
            }
        });
        mViewAttrSetters.put("textColor", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(parseColor(view, value));
                }
            }
        });
        mViewAttrSetters.put("textStyle", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    int typeFace = Typeface.NORMAL;
                    if (value.contains("bold")) typeFace |= Typeface.BOLD;
                    else if (value.contains("italic")) typeFace |= Typeface.ITALIC;
                    ((TextView) view).setTypeface(null, typeFace);
                }
            }
        });
        mViewAttrSetters.put("textAlignment", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    int alignment = View.TEXT_ALIGNMENT_TEXT_START;
                    switch (value) {
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
                    switch (value) {
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
                    ((TextView) view).setGravity(gravity);
                }
            }
        });
        mViewAttrSetters.put("ellipsize", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    TextUtils.TruncateAt where = TextUtils.TruncateAt.END;
                    switch (value) {
                        case "start":
                            where = TextUtils.TruncateAt.START;
                            break;
                        case "middle":
                            where = TextUtils.TruncateAt.MIDDLE;
                            break;
                        case "marquee":
                            where = TextUtils.TruncateAt.MARQUEE;
                            break;
                        case "end":
                            break;
                    }
                    ((TextView) view).setEllipsize(where);
                }
            }
        });
        mViewAttrSetters.put("singleLine", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    ((TextView) view).setSingleLine();
                }
            }
        });
        mViewAttrSetters.put("hint", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof EditText) {
                    ((EditText) view).setHint(value);
                }
            }
        });
        mViewAttrSetters.put("inputType", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof TextView) {
                    int inputType = 0;
                    switch (value) {
                        case "textEmailAddress":
                            inputType |= InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                            break;
                        case "number":
                            inputType |= InputType.TYPE_CLASS_NUMBER;
                            break;
                        case "phone":
                            inputType |= InputType.TYPE_CLASS_PHONE;
                            break;
                    }
                    if (inputType > 0) ((TextView) view).setInputType(inputType);
                }
            }
        });
        mViewAttrSetters.put("gravity", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                int gravity = parseGravity(value);
                if (view instanceof TextView) {
                    ((TextView) view).setGravity(gravity);
                } else if (view instanceof LinearLayout) {
                    ((LinearLayout) view).setGravity(gravity);
                } else if (view instanceof RelativeLayout) {
                    ((RelativeLayout) view).setGravity(gravity);
                }
            }
        });
        mViewAttrSetters.put("src", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                if (view instanceof ImageView) {
                    String imageName = value;
                    if (imageName.startsWith("//")) imageName = "http:" + imageName;
                    if (imageName.startsWith("http")) {
                        if (mImageLoader != null) {
                            if (attrs.containsKey("cornerRadius")) {
                                int radius = Dimensions.parseToPixel(attrs.get("cornerRadius"), view.getResources().getDisplayMetrics());
                                mImageLoader.loadRoundedImage((ImageView) view, imageName, radius);
                            } else {
                                mImageLoader.loadImage((ImageView) view, imageName);
                            }
                        }
                    } else if (imageName.startsWith("@drawable/")) {
                        imageName = imageName.substring("@drawable/".length());
                        ((ImageView) view).setImageDrawable(getDrawableByName(view, imageName));
                    }
                }
            }
        });
        mViewAttrSetters.put("visibility", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                int visibility = View.VISIBLE;
                String visValue = value.toLowerCase();
                if (visValue.equals("gone")) visibility = View.GONE;
                else if (visValue.equals("invisible")) visibility = View.INVISIBLE;
                view.setVisibility(visibility);
            }
        });
        mViewAttrSetters.put("clickable", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                view.setClickable(value.equals("true"));
            }
        });
        mViewAttrSetters.put("tag", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                // Sigh, this is dangerous because we use tags for other purposes
                if (view.getTag() == null) view.setTag(value);
            }
        });
        mViewAttrSetters.put("onClick", new ViewAttrSetter() {
            @Override
            public void setAttr(View view, String value, ViewGroup parent, Map<String, String> attrs) {
                view.setOnClickListener(getClickListener(parent, value));
            }
        });
    }

}
