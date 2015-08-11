package com.nickandjerry.dynamiclayoutinflator;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by nick on 8/7/15.
 */
public class DynamicLayoutView extends RelativeLayout {
    public DynamicLayoutView(Context context) {
        this(context, null);
    }

    public DynamicLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //this(context, attrs, defStyleAttr, 0);
    }


}
