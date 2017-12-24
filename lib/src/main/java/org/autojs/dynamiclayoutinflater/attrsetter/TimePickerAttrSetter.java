package org.autojs.dynamiclayoutinflater.attrsetter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TimePicker;

import org.autojs.dynamiclayoutinflater.R;
import org.autojs.dynamiclayoutinflater.ViewCreator;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class TimePickerAttrSetter extends BaseViewAttrSetter<TimePicker> {

    @Nullable
    @Override
    public ViewCreator<TimePicker> getCreator() {
        return new ViewCreator<TimePicker>() {
            @Override
            public TimePicker create(Context context, Map<String, String> attrs) {
                String datePickerMode = attrs.remove("android:timePickerMode");
                if (datePickerMode == null || !datePickerMode.equals("spinner")) {
                    return new TimePicker(context);
                }
                return (TimePicker) View.inflate(context, R.layout.time_picker_spinner, null);
            }
        };
    }
}
