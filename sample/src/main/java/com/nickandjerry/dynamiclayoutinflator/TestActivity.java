package com.nickandjerry.dynamiclayoutinflator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.nickandjerry.dynamiclayoutinflator.lib.DynamicLayoutInflater;

import java.io.IOException;

/**
 * Created by Stardust on 2017/5/14.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout main = new RelativeLayout(this);
        try {
            View view = DynamicLayoutInflater.inflate(this, getAssets().open("test_single_text.xml"), main);
            setContentView(main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
