package com.example.pro.watts_hut;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int currentPage;
    private Toast internalToast;
    private MovieRvAdapter internalMovieAdapter;
    private RecyclerView recyclerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        // Hide UI - Go Fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.hide();

        // Setup recycler view
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie_grid) ;

        // Set GridLayoutManager to the recyclerview, with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        // Set the adapter to the recycler view
        internalMovieAdapter = new MovieRvAdapter(this, recyclerView);
        internalMovieAdapter.setOnLoadMoreListener(new MovieRvAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMovies();
            }
        });
        recyclerView.setAdapter(internalMovieAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    private void hideViews() {
//        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
//    }
//
//    private void showViews() {
//        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//    }

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
            Context context = this;
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

        Uri builtURI = Uri.parse(getString(R.string.movie_api_popular)).buildUpon()
                .appendQueryParameter(getString(R.string.movie_api_param_key), apiKey)
                .appendQueryParameter(getString(R.string.movie_api_param_sort), sortingArray[0])
                .appendQueryParameter(getString(R.string.movie_api_param_page), Integer.toString(currentPage))
                .build();

        URL url = null;
        try {
            url = new URL(builtURI.toString());
            Context context = getApplicationContext();
            String message = "URL:"+url;


            Log.d(TAG, String.valueOf(internalToast == null));
            boast(Toast.makeText(context, message, Toast.LENGTH_SHORT));

            Log.d(TAG, "loadMovies: "+url);

            new loadDataTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    class loadDataTask extends AsyncTask<URL, Void, HashSet<MovieObject>> {

        @Override
        protected HashSet<MovieObject> doInBackground(URL... urls) {
            URL url = urls[0];
            String[] imageUrls = new String[0];
            HashSet<MovieObject> movieList = new HashSet<MovieObject>();

            try {

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(true);
                urlConnection.connect();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    JSONObject data = new JSONObject(stringBuilder.toString());

                    try {
                        JSONArray results = data.getJSONArray("results");
                        currentPage = data.getInt("page")+1;

                        imageUrls = new String[results.length()];
                        for(int n = 0; n < results.length(); n++)
                        {
                            JSONObject movie = results.getJSONObject(n);


                            imageUrls[n] = movie.getString(getString(R.string.movie_api_results_image));

                            if (imageUrls[n] == "null")
                                continue;

                            MovieObject movieObject = new MovieObject(movie);
                            movieList.add(movieObject);

                            imageUrls[n] = String.format(getString(R.string.image_api_url), "w185", imageUrls[n]);

                            try {
                                Picasso.with(mContext)
                                        .load(imageUrls[n])
                                        .get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "doInBackground: "+imageUrls[n]);
                        }

                        imageUrls = ArrayUtils.removeAllOccurences(imageUrls, "null");
                        return movieList;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally{
                    urlConnection.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(HashSet<MovieObject> movieList) {
            if((movieList == null)||(movieList.size() == 0)) {
                Log.i("INFO", "THERE WAS AN ERROR");
            } else {
                internalMovieAdapter.addMovies(movieList);
            }
//            boast(Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT));
        }

    }

    public class MovieObject {
        public JSONObject data;
        public ImageView cachedImage;
        public String imageUrl;

        public MovieObject(JSONObject jsonObject) {
            data = jsonObject;

            try {
                imageUrl = jsonObject.getString(getString(R.string.movie_api_results_image));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getThumbnailUrl() {
            String url = "null";

            if (imageUrl != "null")
                url = String.format(getString(R.string.image_api_url), "w185", imageUrl);

            return url;
        }
    }


}
