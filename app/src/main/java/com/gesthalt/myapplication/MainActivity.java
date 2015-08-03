package com.gesthalt.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // json object response url
    private String urlJsonObj = "http://52.27.129.146/uploads";


    //    private RequestQueue mRequestQueue;
//    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        makeJsonObjectRequest();
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //user clicked on one of filters in grid view
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("AAA", "filter position = " + position);
                Toast.makeText(MainActivity.this, "position = " + position,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), FilterActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }

        });

    }


    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeJsonObjectRequest() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Parsing json object response
                    // response will be a json object
                    JSONArray images = response.getJSONArray("images");
                    setImagesToScrollView(images);
                    Toast.makeText(getApplicationContext(),
                            images.toString(), Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void setImagesToScrollView(JSONArray images) {
        for (int i = 0; i < images.length(); i++) {
            ImageView imgView = new ImageView(this);
            JSONObject img = null;
            try {
                img = (JSONObject) images.get(i);
                imgView.setImageBitmap(getBitmapFromURL((String) img.get("imgThumbUrl")));
                System.out.println("here1111111");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            // positions in comments
            items.add(new Item("Neuralife", R.drawable.neuralife)); //0
            items.add(new Item("NeuraTrip", R.drawable.neuraltrip));//1
            items.add(new Item("NeuralVortex", R.drawable.neuralvortex));//2
            items.add(new Item("NeuralFlow", R.drawable.neuralflow));//3
            items.add(new Item("NeuralWave", R.drawable.neuralwave));//4
            items.add(new Item("Neuralice", R.drawable.neuralice));//5
            items.add(new Item("NeuralAztec", R.drawable.neuralaztec));//6
            items.add(new Item("NeuralLizard", R.drawable.neurallizard));//7

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;

            if (v == null) {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);

            Item item = (Item) getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);

            return v;
        }

        private class Item {
            final String name;
            final int drawableId;

            Item(String name, int drawableId) {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }
}
