package com.example.pro.watts_hut;

import android.content.Context;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int currentPage;
    private Toast internalToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Picasso.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into((ImageView) findViewById(R.id.testView));

        GridView gridview = (GridView) findViewById(R.id.movieGridView);
        gridview.setAdapter(new movieAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void boast(Toast toast) {
        if (internalToast != null) {
            internalToast.cancel();
        }

        internalToast = toast;
        internalToast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_refresh) {
            Context context = MainActivity.this;
            String message = "Refresh!";
            boast(Toast.makeText(context, message, Toast.LENGTH_LONG));
            loadMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadMovies() {
        // Get sorting options and api key to build request url
        String[] sortingArray = getResources().getStringArray(R.array.movie_api_sorting);
        String apiKey = getString(R.string.movie_api_key);

        currentPage += 1;

        Uri builtURI = Uri.parse(getString(R.string.movie_api_url)).buildUpon()
                .appendQueryParameter(getString(R.string.movie_api_param_key), apiKey)
                .appendQueryParameter(getString(R.string.movie_api_param_sort), sortingArray[0])
                .appendQueryParameter(getString(R.string.movie_api_param_page), Integer.toString(currentPage))
                .build();

        URL url = null;
        try {
            url = new URL(builtURI.toString());
            Context context = MainActivity.this;
            String message = "URL:"+url;


            Log.d(TAG, String.valueOf(internalToast == null));
            boast(Toast.makeText(context, message, Toast.LENGTH_SHORT));

            Log.d(TAG, "loadMovies: "+url);

            new loadDataTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    class loadDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);
            boast(Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT));
        }

    }

}
