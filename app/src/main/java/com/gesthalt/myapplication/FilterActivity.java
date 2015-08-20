package com.gesthalt.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;


public class FilterActivity extends ActionBarActivity {

    private static final String PATH = "http://52.27.129.146:888/";
    private static int RESULT_LOAD_IMG = 1;
    ProgressDialog prgDialog;
    RequestParams requestParams = new RequestParams();
    String imgPath;
    AsyncHttpClient client;
    PersistentCookieStore myCookieStore;
    int filterPosition; //position of filter in filters array
    String[] filters = {
            "neuralife", "neuraltrip", "neuralvortex",
            "neuralflow", "neuralwave", "neuralice",
            "neuralaztec", "neurallizard"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        filterPosition = getIntent().getIntExtra("position", 0);
        Toast.makeText(FilterActivity.this, " filter position = " + filterPosition,
                Toast.LENGTH_SHORT).show();

        client = new AsyncHttpClient();
        myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);

    }

    public void loadImageFromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent

        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }




    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {



                ConvertTask convertTask = new ConvertTask(data);
                convertTask.execute();


            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    class ConvertTask extends AsyncTask<Void, Void, Object>{

        boolean result = false;
        Intent data;
        public ConvertTask(Intent data) {
            super();
            this.data = data;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog.show(FilterActivity.this, "Wait", "uploading...");
        }

        @Override
        protected Object doInBackground(Void... params) {

            // Get the Image from data
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            File imageFile = new File(imgPath);

            try {
                requestParams.put("image", imageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            client.post(PATH + "upload", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    result =true;
                }

                @Override
                public void onFailure(int statusCode, Throwable error, String content) {
                    result = false;
                }
            });

            if (result){
                String path = PATH + "convert?type=" + filters[filterPosition];
                client.get(path, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        getConvertedPicture();
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        result = false;
                    }

                });
            }
            if (result){
                Object picture = getConvertedPicture();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Object image) {
            System.out.println("dfdsdfgsd");
            prgDialog.dismiss();

        }

    }


    //recursive
    private Object getConvertedPicture(){

        client.get(PATH + "status", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.get("status") == "done") {
                        Object image = jsonResponse.get("result");
                        //start activity to show converted picture
                        startShowConvertedActivity(image);

                    } else {
                        pauseThread();
                        getConvertedPicture();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void startShowConvertedActivity(Object image) {
        Intent intent = new Intent(this, ShowConvertedActivity.class);
//        intent.putExtra("convertedImage", image);
        startActivity(intent);

    }



    private void pauseThread() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
