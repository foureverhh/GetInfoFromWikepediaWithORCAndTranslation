package com.example.zfgg04.getinfofromwikepediawithorcandtranslation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Config Google Cloud Vision API
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyDVm55Q1b9VB5ZqG-Hfd2WlbTtUmcmkVh8"));
        visionBuilder.build();

    }


}
