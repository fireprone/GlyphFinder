package com.example.glyphdictionary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import static com.example.glyphdictionary.MatchingDemo.decodeSampledBitmapFromResource;
import static com.example.glyphdictionary.MatchingDemo.rotateBitmap;

public class ImageChooser extends AppCompatActivity {

    //Display display;
    Bitmap bMapImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_chooser);

        //display = getWindowManager().getDefaultDisplay();

        GridLayout gl = findViewById(R.id.grid);

        //-- Set OnClick Listeners to return which glyph was selected
        ImageView r1 = (ImageView) gl.getChildAt(0);
        //Set image
        setImageView(r1, R.drawable.card1);
        r1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "card1");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r2 = (ImageView) gl.getChildAt(1);
        setImageView(r2, R.drawable.card2);
        r2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "card2");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        //*
        ImageView r3 = (ImageView) gl.getChildAt(2);
        setImageView(r3, R.drawable.card3);
        r3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "card3");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        ImageView r4 = (ImageView) gl.getChildAt(3);
        setImageView(r4, R.drawable.card4);
        r4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "card4");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        //*/
    }

    private void setImageView(ImageView iv, int resId) {
        bMapImg = decodeSampledBitmapFromResource(getResources(), resId, 300, 300);
        bMapImg = rotateBitmap(bMapImg, 90);
        iv.setImageBitmap(bMapImg);
    }
}
