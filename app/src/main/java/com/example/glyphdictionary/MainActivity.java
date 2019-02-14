package com.example.glyphdictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Failed to load OpenCV :(");
        } else {
            Log.d(TAG, "Loaded OpenCV :)");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Appendix (Start)
        Button apdixButton = findViewById(R.id.appenButton);
        apdixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MatchingDemo.class);
                startActivity(i);
            }
        });
    }

}
