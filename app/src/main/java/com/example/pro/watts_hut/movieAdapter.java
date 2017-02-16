package com.example.pro.watts_hut;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.lang.reflect.Array;

/**
 * Created by Pro on 10.02.17.
 */
public class movieAdapter extends BaseAdapter {
    public Context appContext;

    public movieAdapter(Context c) {
        appContext = c;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayMetrics metrics = new DisplayMetrics();
        // get layout from text_item.xml
        //gridView = (LinearLayout)inflater.inflate(R.layout.text_item, null);

        DisplayMetrics display = ((Activity) appContext).getResources().getDisplayMetrics();
        Drawable image = appContext.getDrawable(images[position]);

        int width = display.widthPixels;
        //int height = display.heightPixels;


        int imageWidth = width / 2;
        int imageHeight = imageWidth * image.getIntrinsicHeight() / image.getIntrinsicWidth();

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(appContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width/2, imageHeight));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(images[position]);
        return imageView;
    }


    private Integer[] images = {R.drawable.im2, R.drawable.im01, R.drawable.im3, R.drawable.im4, R.drawable.im5};
}
