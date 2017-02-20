package com.example.pro.watts_hut;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by Pro on 10.02.17.
 */
public class MovieAdapter extends BaseAdapter {
    public Context appContext;
    private Integer[] images = new Integer[0]; //= {R.drawable.im2, R.drawable.im01, R.drawable.im3, R.drawable.im4, R.drawable.im5};
    public String[] imagesURL = new String[0];

    public MovieAdapter(Context c) {
        appContext = c;
    }

    @Override
    public int getCount() {
        return (images.length+imagesURL.length);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addUrls(String... urls) {
        for (int i = 0; i < urls.length; i++) {
            imagesURL = ArrayUtils.removeAllOccurences(imagesURL, urls[i]);
        }
        imagesURL = ArrayUtils.addAll(imagesURL, urls);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayMetrics display = ((Activity) appContext).getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int imageWidth = width / 2;
        int imageHeight = (int) Math.round(display.heightPixels/2.5);

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(appContext);
            imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageHeight));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        if (position<images.length) {
            Picasso.with(appContext)
                    .load(images[position])
                    .resize(imageWidth,imageHeight)
                    .centerCrop()
                    .into(imageView);
            //int imageHeight = imageWidth * image.getIntrinsicHeight() / image.getIntrinsicWidth();
        } else {
            int index = position-images.length;
            if (index > imagesURL.length) {
                return imageView;
            }
            Picasso.with(appContext)
                    .load(imagesURL[index])
                    .resize(imageWidth,imageHeight)
                    .centerCrop()
                    .into(imageView);
        }

        return imageView;
    }



}
