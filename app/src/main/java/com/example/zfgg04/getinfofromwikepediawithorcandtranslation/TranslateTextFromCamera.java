package com.example.zfgg04.getinfofromwikepediawithorcandtranslation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TranslateTextFromCamera extends AppCompatActivity {

    Button btnPicture;
    ImageView picturePreview;
    TextView textResult;
    final static int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_text_from_camera);

        btnPicture = findViewById(R.id.takePictureButton);
        picturePreview = findViewById(R.id.previewImage);
        textResult = findViewById(R.id.resultsText);

    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!= null)
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap picture = (Bitmap)extras.get("data");
            picturePreview.setImageBitmap(picture);

        }
    }
}


