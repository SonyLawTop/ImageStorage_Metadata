package com.sonylawtop.root.customcameraapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements Camera.PictureCallback, Camera.AutoFocusCallback,SurfaceHolder.Callback{

    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    private Camera camera;
    Button btCapture, btDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        cameraView = (SurfaceView) this.findViewById(R.id.CameraView);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        btCapture = (Button) findViewById(R.id.btSolarize);
        btCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                camera.setParameters(parameters);
            }
        });

        btDefault =(Button)findViewById(R.id.btDefault);
        btDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                camera.setParameters(parameters);
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        camera = Camera.open();
//        try{
//            camera.setPreviewDisplay(holder);
//        }catch (IOException ex){
//            camera.release();
//        }
//        camera.startPreview();


        camera = Camera.open();

        try {
            Camera.Parameters parameters = camera.getParameters();
            //Camera.Parameters parameters = camera.getParameters();
            //List<String> colorEffects = parameters.getSupportedColorEffects();
            //Iterator<String> cei = colorEffects.iterator();
//            parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
//            camera.setParameters(parameters);

//            Log.v("SNAPSHOT","Using Effect: " + parameters.getColorEffect());
//            camera.setParameters(parameters);
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "portrait");
                // For Android 2.2 and above
                //camera.setDisplayOrientation(90);
                // Uncomment for Android 2.0 and above
                //parameters.setRotation(90);
            } else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                //camera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                //parameters.setRotation(0);
            }
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            camera.release();
            Log.v("", exception.getMessage());
        }
        camera.startPreview();



    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageFileUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try{
            OutputStream imageFIleOS= getContentResolver().openOutputStream(imageFileUri);
            imageFIleOS.write(data);
            imageFIleOS.flush();
            imageFIleOS.close();
        }catch (FileNotFoundException e){

        }catch (IOException e){

        }camera.startPreview();
    }
}
