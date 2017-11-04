package com.nickandjerry.dynamiclayoutinflator.lib.attrsetter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.nickandjerry.dynamiclayoutinflator.lib.util.Colors;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Dimensions;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Drawables;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Gravities;
import com.nickandjerry.dynamiclayoutinflator.lib.util.Strings;
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
    private static final ValueMapper<TextUtils.TruncateAt> ELLIPSIZE = new ValueMapper<TextUtils.TruncateAt>("ellipsize")
            .map("end", TextUtils.TruncateAt.END)
            .map("marquee", TextUtils.TruncateAt.MIDDLE)
            .map("none", null)
            .map("start", TextUtils.TruncateAt.START)
            .map("middle", TextUtils.TruncateAt.MIDDLE);
    private static final ValueMapper<Integer> HYPHENATION_FREQUENCY = new ValueMapper<Integer>("hyphenationFrequency")
            .map("full", 2)
            .map("none", 0)
            .map("normal", 1);

    // TODO: 2017/11/4 IME FLAG
    private static final ValueMapper<Integer> IME_OPTIONS = new ValueMapper<Integer>("imeOptions")
            .map("actionDone", EditorInfo.IME_ACTION_DONE)
            .map("actionGo", EditorInfo.IME_ACTION_DONE)
            .map("actionNext", EditorInfo.IME_ACTION_DONE)
            .map("actionNone", EditorInfo.IME_ACTION_DONE)
            .map("actionPrevious", EditorInfo.IME_ACTION_DONE)
            .map("actionSearch", EditorInfo.IME_ACTION_DONE)
            .map("actionSend", EditorInfo.IME_ACTION_DONE)
            .map("actionUnspecified", EditorInfo.IME_ACTION_DONE);

    private static final ValueMapper<Integer> INPUT_TYPES = new ValueMapper<Integer>("inputType")
            .map("date", 0x14)
            .map("datetime", 0x4)
            .map("none", 0x0)
            .map("number", 0x2)
            .map("numberDecimal", 0x2002)
            .map("numberPassword", 0x12)
            .map("numberSigned", 0x1002)
            .map("phone", 0x3)
            .map("text", 0x1)
            .map("textAutoComplete", 0x10001)
            .map("textAutoCorrect", 0x8001)
            .map("textCapCharacters", 0x1001)
            .map("textCapSentences", 0x4001)
            .map("textCapWords", 0x2001)
            .map("textEmailAddress", 0x21)
            .map("textEmailSubject", 0x31)
            .map("textFilter", 0xb1)
            .map("textImeMultiLine", 0x40001)
            .map("textLongMessage", 0x51)
            .map("textMultiLine", 0x20001)
            .map("textNoSuggestions", 0x80001)
            .map("textPassword", 0x81)
            .map("textPersonName", 0x61)
            .map("textPhonetic", 0xc1)
            .map("textPostalAddress", 0x71)
            .map("textShortMessage", 0x41)
            .map("textUri", 0x11)
            .map("textVisiblePassword", 0x91)
            .map("textWebEditText", 0xa1)
            .map("textWebEmailAddress", 0xd1)
            .map("textWebPassword", 0xe1)
            .map("time", 0x24);


    private boolean mAutoText;
    private String mCapitalize;
    private String mFontFamily;
    private Drawable mDrawableBottom;
    private Drawable mDrawableRight;
    private Drawable mDrawableTop;
    private Drawable mDrawableLeft;
    private int mLineSpacingExtra;
    private int mLineSpacingMultiplier;

    @Override
    public boolean setAttr(V view, String attrName, String value, ViewGroup parent, Map<String, String> attrs) {
        if (super.setAttr(view, attrName, value, parent, attrs)) {
            return true;
        }
        switch (attrName) {
            case "autoLink":
                view.setAutoLinkMask(AUTO_LINK_MASKS.get(value));
                break;
            case "autoText":
                mAutoText = Boolean.valueOf(value);
                break;
            case "capitalize":
                mCapitalize = value;
                break;
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
            case "drawableBottom":
                mDrawableBottom = Drawables.parse(view, value);
                break;
            case "drawableTop":
                mDrawableTop = Drawables.parse(view, value);
                break;
            case "drawableLeft":
                mDrawableLeft = Drawables.parse(view, value);
                break;
            case "drawableRight":
                mDrawableRight = Drawables.parse(view, value);
                break;
            case "drawablePadding":
                view.setCompoundDrawablePadding(Dimensions.parseToIntPixel(value, view));
                break;
            case "drawableStart":
            case "drawableEnd":
            case "editable":
            case "editorExtras":
                Util.unsupports(view, attrName, value);
            case "elegantTextHeight":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElegantTextHeight(Boolean.valueOf(value));
                }
                break;
            case "ellipsize":
                TextUtils.TruncateAt e = ELLIPSIZE.get(value);
                if (e != null) {
                    view.setEllipsize(e);
                }
                break;
            case "ems":
                view.setEms(Integer.valueOf(value));
                break;
            case "fontFamily":
                mFontFamily = value;
                break;
            case "fontFeatureSettings":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setFontFeatureSettings(value);
                }
                break;
            case "freezesText":
                view.setFreezesText(Boolean.valueOf(value));
                break;
            case "gravity":
                view.setGravity(Gravities.parse(value));
                break;
            case "height":
                view.setHeight(Dimensions.parseToIntPixel(value, view));
                break;
            case "hint":
                view.setHint(Strings.parse(view, value));
                break;
            case "hyphenationFrequency":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.setHyphenationFrequency(HYPHENATION_FREQUENCY.get(value));
                }
                break;
            case "imeActionId":
                view.setImeActionLabel(view.getImeActionLabel(), Integer.valueOf(value));
                break;
            case "imeActionLabel":
                view.setImeActionLabel(value, view.getImeActionId());
                break;
            case "imeOptions":
                view.setImeOptions(IME_OPTIONS.split(value));
                break;
            case "includeFontPadding":
                view.setIncludeFontPadding(Boolean.valueOf(value));
                break;
            case "inputMethod":
                Util.unsupports(view, attrName, value);
            case "inputType":
                view.setRawInputType(INPUT_TYPES.split(value));
                break;
            case "letterSpacing":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setLetterSpacing(Float.valueOf(value));
                }
                break;
            case "lineSpacingExtra":
                mLineSpacingExtra = Dimensions.parseToIntPixel(value, view);
                break;
            case "lineSpacingMultiplier":
                mLineSpacingMultiplier = Dimensions.parseToIntPixel(value, view);
                break;
            case "lines":
                view.setLines(Integer.valueOf(value));
                break;
            case "linksClickable":
                view.setLinksClickable(Boolean.valueOf(value));
                break;
            case "marqueeRepeatLimit":
                view.setMarqueeRepeatLimit(value.equals("marquee_forever") ? Integer.MAX_VALUE : Integer.valueOf(value));
                break;
            case "text":
                view.setText(Strings.parse(view, value));
                break;
            case "textColor":
                view.setTextColor(Colors.parse(view.getContext(), value));
                break;
            case "textSize":
                view.setTextSize(TypedValue.COMPLEX_UNIT_PX, Dimensions.parseToPixel(value, view));
                break;
            default:
                return false;
        }
        return true;
    }
}
