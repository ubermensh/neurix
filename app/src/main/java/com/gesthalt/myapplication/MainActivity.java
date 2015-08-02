package com.gesthalt.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{

//    private RequestQueue mRequestQueue;
//    private ImageLoader imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new MyAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.print("here!!!!!!!!!!!!" + position);
                Toast.makeText(MainActivity.this, "position = " + position,
                        Toast.LENGTH_SHORT).show();
            }

        });

    }

    private class MyAdapter extends BaseAdapter
    {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);

            items.add(new Item("Image 1", R.drawable.sample_0));
            items.add(new Item("Image 2", R.drawable.sample_1));
            items.add(new Item("Image 3", R.drawable.sample_2));
            items.add(new Item("Image 4", R.drawable.sample_3));

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View v = view;
            ImageView picture;
            TextView name;

            if(v == null)
            {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);

            Item item = (Item)getItem(i);

            picture.setImageResource(item.drawableId);
            name.setText(item.name);

            return v;
        }

        private class Item
        {
            final String name;
            final int drawableId;

            Item(String name, int drawableId)
            {
                this.name = name;
                this.drawableId = drawableId;
            }
        }
    }
}
