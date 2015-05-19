package com.sonylawtop.root.imagestorage_metadata;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

import java.io.FileNotFoundException;


public class MainActivity extends Activity {

    final static int CAMERA_RESULT = 0;
    Uri imageFileUri;
    ImageView returnedImageView;
    Button takePictureButton;
    Button saveDataButton;
    TextView titleTextView;
    TextView descriptionTextView;
    EditText titleEditText;
    EditText descriptionEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        returnedImageView = (ImageView) findViewById(R.id.ReturnedImageView);
        takePictureButton = (Button) findViewById(R.id.TakePictureButton);
        saveDataButton = (Button) findViewById(R.id.SaveDataButton);
        titleTextView = (TextView) findViewById(R.id.DescriptionTextView);
        titleEditText = (EditText) findViewById(R.id.TitleEditText);
        descriptionEditText = (EditText) findViewById(R.id.DescriptionEditText);

        returnedImageView.setVisibility(View.GONE);
        saveDataButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        titleEditText.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.GONE);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageFileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(i, CAMERA_RESULT);
            }
        });

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(Media.DISPLAY_NAME, titleEditText.getText().toString());
                contentValues.put(Media.DESCRIPTION, descriptionEditText.getText().toString());
                getContentResolver().update(imageFileUri, contentValues, null, null);

                Toast bread = Toast.makeText(MainActivity.this, "Recod Updated", Toast.LENGTH_SHORT);
                bread.show();

                takePictureButton.setVisibility(View.VISIBLE);

                returnedImageView.setVisibility(View.GONE);
                saveDataButton.setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
                titleEditText.setVisibility(View.GONE);
                descriptionEditText.setVisibility(View.GONE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            // The Camera App has returned
            // Hide the Take Picture Button
            takePictureButton.setVisibility(View.GONE);
            // Show the other UI Elements
            saveDataButton.setVisibility(View.VISIBLE);
            returnedImageView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            titleEditText.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            // Scale the image
            int dw = 200; // Make it at most 200 pixels wide
            int dh = 200; // Make it at most 200 pixels tall
            try {
                // Load up the image's dimensions not the image itself
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
                int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
                int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);
                Log.v("HEIGHTRATIO", "" + heightRatio);
                Log.v("WIDTHRATIO", "" + widthRatio);
                // If both of the ratios are greater than 1,
                // one of the sides of the image is greater than the screen
                if (heightRatio > 1 && widthRatio > 1) {
                    if (heightRatio > widthRatio) {
                        // Height ratio is larger, scale according to it
                        bmpFactoryOptions.inSampleSize = heightRatio;
                    } else {
                        // Width ratio is larger, scale according to it
                        bmpFactoryOptions.inSampleSize = widthRatio;
                    }


                }

                // Decode it for real
                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOptions);
                // Display it
                returnedImageView.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                Log.v("ERROR", e.toString());
            }
        }
    }
}

