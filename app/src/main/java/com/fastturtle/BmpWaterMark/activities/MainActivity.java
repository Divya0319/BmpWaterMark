package com.fastturtle.BmpWaterMark.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fastturtle.BmpWaterMark.R;

import io.fastturtle.BmpWaterMark.WaterMarkProvider;

/**
 * @Author: Divya Gupta
 * @Date: 20-Dec-22
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaterMarkProvider wmp = new WaterMarkProvider(this);

    }
}
