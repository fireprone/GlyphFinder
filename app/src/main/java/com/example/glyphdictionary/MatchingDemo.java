package com.example.glyphdictionary;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MatchingDemo extends AppCompatActivity {

    private final String TAG = "MatchingDemo::Activity";
    private Bitmap bMapImg;
    private Bitmap bMapTempl;
    private Uri photoUri;
    private String currentPhotoPath;
    private static Display display;
    static final int REQUEST_IMAGE_CAPTURE = 1,
            REQUEST_TEMPLATE_CHOOSE = 2,
            REQUEST_IMAGE_CHOOSE = 3;

    //Loads OpenCV
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Result from Camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView iv = findViewById(R.id.image_view);
            this.grabImage(iv);
        }
        //Result from TemplChooser
        if (requestCode == REQUEST_TEMPLATE_CHOOSE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");

            //Use result to determine template for matching
            if (result.equals("arrow1")) {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_arrow1, 300, 300);
            } else if (result.equals("circle")) {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_circle, 300, 300);
            } else if (result.equals("curlycross")) {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_curlycross, 300, 300);
            } else if (result.equals("f")) {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_f, 300, 300);
            } else if (result.equals("n")) {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_n, 300, 300);
            } else {
                bMapTempl = decodeSampledBitmapFromResource(getResources(), R.drawable.glyph_twocross, 300, 300);
            }

            ImageView iv = findViewById(R.id.image_view);

            //Finally, match template
            matchCV(iv);
        }
        //Result from ImageChooser
        if(requestCode == REQUEST_IMAGE_CHOOSE && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");

            if(result.equals("card1")) {
                bMapImg = decodeSampledBitmapFromResource(getResources(), R.drawable.card1, 300, 300);
            } else if(result.equals("card2")) {
                bMapImg = decodeSampledBitmapFromResource(getResources(), R.drawable.card2, 300, 300);
            } else if(result.equals("card3")) {
                bMapImg = decodeSampledBitmapFromResource(getResources(), R.drawable.card3, 300, 300);
            } else {
                bMapImg = decodeSampledBitmapFromResource(getResources(), R.drawable.card4, 300, 300);
            }

            bMapImg = rotateBitmap(bMapImg, 90);
            ImageView iv = findViewById(R.id.image_view);
            iv.setImageBitmap(bMapImg);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_demo);

        //-- Load images
        display = getWindowManager().getDefaultDisplay();
        bMapImg = decodeSampledBitmapFromResource(getResources(), R.drawable.card1, 300, 300);
        bMapImg = rotateBitmap(bMapImg, 90);

        //-- Show pre-loaded demo image
        ImageView iv = findViewById(R.id.image_view);
        iv.setImageBitmap(bMapImg);

        //-- Button to get template
        Button matchButton = findViewById(R.id.matchbutton);
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseIntent = new Intent(getApplicationContext(), TemplChooser.class);
                startActivityForResult(chooseIntent, REQUEST_TEMPLATE_CHOOSE);
            }
        });

        //-- Button to get image from camera
        Button imageButton = findViewById(R.id.imagebutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //-- Button to change demo card
        Button cardButton = findViewById(R.id.cardbutton);
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseIntent = new Intent(getApplicationContext(), ImageChooser.class);
                startActivityForResult(chooseIntent, REQUEST_IMAGE_CHOOSE);
            }
        });
    }

    @Override
    public void onResume () {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
    }

    //Calculates SubSampleSize to scale down images
    private static int calculateSubSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth) {
            //Calculate ratios of requested height/width to raw height/width
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            //Choose largest inSampleSpace value that is a power of 2 ratio for inSampleSize
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //Creates and stores Image to store full resolution
    private File createImageFile() throws IOException {
        //Create image name (use date to ensure no collision)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Scales image from res folder and copy into Bitmap
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,R.drawable.card1, options);

        Point size = new Point();
        display.getSize(size);

        options.inSampleSize = calculateSubSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res,resId, options);
    }

    //Scales image from file and copy into Bitmap
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        Point size = new Point();
        display.getSize(size);

        options.inSampleSize = calculateSubSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathName, options);
    }

    //Enters intent to take picture with Android camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //Error creating file
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.glyphdictionary",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //Loads image from file and displays
    public void grabImage(ImageView iv) {
        this.getContentResolver().notifyChange(photoUri, null);
        ContentResolver cr = this.getContentResolver();

        try {
            bMapImg = decodeSampledBitmapFromFile(currentPhotoPath, 900, 900);
            bMapImg = rotateBitmap(bMapImg, 90);
            bMapImg = Bitmap.createScaledBitmap(bMapImg, 1323, 1764, true);

            iv.setImageBitmap(bMapImg);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG ,"Failed to load", e);
        }
    }

    //Uses template matching to find selected glyph in provided image
    public void matchCV (ImageView iv) {
        int match_method = Imgproc.TM_CCOEFF;
        org.opencv.core.Point matchLoc;

        //-- Convert bitmaps to Mats for processing
        Mat img = new Mat();
        Mat templ = new Mat();
        Utils.bitmapToMat(bMapImg, img);
        Utils.bitmapToMat(bMapTempl, templ);

        //-- Create result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        //- Do matching and normalize
        Imgproc.matchTemplate(img, templ, result, match_method);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        //-- Localize best match with minMaxLoc
        while(true) {
            MinMaxLocResult mmr = Core.minMaxLoc(result);
            matchLoc = mmr.maxLoc;
            //- Show
            if (mmr.maxVal >= 0.85) {
                Imgproc.rectangle(img, matchLoc,
                        new org.opencv.core.Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                        new Scalar(0, 255, 0));
                Imgproc.rectangle(result, matchLoc,
                        new org.opencv.core.Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()),
                        new Scalar(0, 255, 0), -1);
            } else {
                break;
            }
        }

        Utils.matToBitmap(img, bMapImg);
        iv.setImageBitmap(bMapImg);
    }

    //Rotates give Bitmap
    public static Bitmap rotateBitmap(Bitmap bm, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

}
