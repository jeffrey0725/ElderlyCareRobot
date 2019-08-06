package com.example.jeffreycheung.elderlycarerobot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class CallActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        imageView = (ImageView) findViewById(R.id.dialog);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
