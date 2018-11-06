package com.example.zfgg04.getinfofromwikepediawithorcandtranslation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public class TranslateTextFromCamera extends AppCompatActivity {

    Button btnPicture;
    ImageView picturePreview;
    TextView textResult;
    final static int REQUEST_IMAGE_CAPTURE = 1;
    Button btnTranslateSwedish;
    TextView translateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_text_from_camera);

        btnPicture = findViewById(R.id.takePictureButton);
        picturePreview = findViewById(R.id.previewImage);
        textResult = findViewById(R.id.resultsText);
        btnTranslateSwedish = findViewById(R.id.translate_btn);
        translateText = findViewById(R.id.translatedText);
    }

    public void takePicture(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!= null)
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            if(extras != null){
                Bitmap picture = (Bitmap)extras.get("data");
                picturePreview.setImageBitmap(picture);

                //Compress bitmap to Base64 so that Vision API can process it
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                picture.compress(Bitmap.CompressFormat.JPEG,90,byteStream);


                //Get Base64 String
                String base64Data = Base64.encodeToString(byteStream.toByteArray(),
                        Base64.URL_SAFE);

                //Send Request to Vision API
                String requestURL =
                        "https://vision.googleapis.com/v1/images:annotate?key=" +
                                getResources().getString(R.string.myKey);
                try {
                //Create an array containing
                //the LABEL_DETECTION feature
                    JSONArray features = new JSONArray();
                    JSONObject feature = new JSONObject();
                    feature.put("type", "LABEL_DETECTION");
                    features.put(feature);

                    // Create an object containing
                    // the Base64-encoded image data
                    JSONObject imageContent = new JSONObject();
                    imageContent.put("content", base64Data);

                    // Put the array and object into a single request
                    // and then put the request into an array of requests
                    JSONArray requests = new JSONArray();
                    JSONObject request = new JSONObject();
                    request.put("image", imageContent);
                    request.put("features", features);
                    requests.put(request);
                    JSONObject postData = new JSONObject();
                    postData.put("requests", requests);

                    // Convert the JSON into a
                    // string
                    String body = postData.toString();

                    Fuel.post(requestURL)
                            .header(
                                    new Pair<String, Object>("content-length", body.length()),
                                    new Pair<String, Object>("content-type", "application/json")
                            )
                            .body(body.getBytes())
                            .responseString(new Handler<String>() {
                                @Override
                                public void success(@NotNull Request request,
                                                    @NotNull Response response,
                                                    String data) {
                                    // Access the labelAnnotations arrays
                                    try {
                                       JSONArray labels = new JSONObject(data)
                                                .getJSONArray("responses")
                                                .getJSONObject(0)
                                                .getJSONArray("labelAnnotations");
                                        String results = "";

                                        // Loop through the array and extract the
                                        // description key for each item
                                        for(int i=0;i<labels.length();i++) {
                                            results = results +
                                                    labels.getJSONObject(i).getString("description") +
                                                    "\n";
                                        }

                                    // Display the annotations inside the TextView
                                       textResult.setText(results);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(@NotNull Request request,
                                                    @NotNull Response response,
                                                    @NotNull FuelError fuelError) {}
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void translateToSwedish(View view){
        String requestURL =  "https://translation.googleapis.com/language/translate/v2";
        List<Pair<String,String>> params = new ArrayList<>();
// Add API key
        params.add(new Pair<String,String>("key",
                getResources().getString(R.string.myKey)));

// Set source and target languages
        params.add(new Pair<String,String>("source", "en"));
        params.add(new Pair<String,String>("target", "sv"));

        String[] queries = ((TextView)findViewById(R.id.resultsText))
                .getText().toString().split("\n");

        for(String query:queries) {
            params.add(new Pair<String, String>("q", query));
        }

        Fuel.get(requestURL, params).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request,
                                @NotNull Response response,
                                String data) {
                // Access the translations array

                try {
                JSONArray translations = new JSONObject(data)
                            .getJSONObject("data")
                            .getJSONArray("translations");
                    // Loop through the array and extract the translatedText
                    // key for each item
                    String result = "";
                    for(int i=0;i<translations.length();i++) {

                        result += translations.getJSONObject(i)
                                .getString("translatedText") + "\n";

                    }
                    textResult.setText(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request,
                                @NotNull Response response,
                                @NotNull FuelError fuelError) { }
        });
    }
}


