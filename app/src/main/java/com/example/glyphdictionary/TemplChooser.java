package com.example.glyphdictionary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TemplChooser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templ_chooser);

        GridLayout gl = findViewById(R.id.grid);

        //-- Set OnClick Listeners to return which glyph was selected
        ImageView r1 = (ImageView) gl.getChildAt(0);
        r1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "arrow1");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r2 = (ImageView) gl.getChildAt(1);
        r2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "circle");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r3 = (ImageView) gl.getChildAt(2);
        r3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "curlycross");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r4 = (ImageView) gl.getChildAt(3);
        r4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "f");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r5 = (ImageView) gl.getChildAt(4);
        r5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "n");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r6 = (ImageView) gl.getChildAt(5);
        r6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "twocross");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}

