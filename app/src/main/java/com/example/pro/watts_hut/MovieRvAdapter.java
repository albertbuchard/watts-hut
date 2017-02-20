package com.example.pro.watts_hut;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pro.watts_hut.MainActivity.MovieObject;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Pro on 19.02.17.
 */

public class MovieRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MovieRvAdapter.class.getSimpleName();
    public Context appContext;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private GridLayoutManager gridLayoutManager;

    public List<MovieObject> mDataset = new ArrayList<MovieObject>();
    private OnLoadMoreListener onLoadMoreListener;

    private int visibleThreshold = 20;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public MovieRvAdapter (Context c, RecyclerView recyclerView) {
        appContext = c;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    checkVisibleAndLoad();
                }
            });
        } else {
            Log.e(TAG, "MovieRvAdapter: Wrong layout manager", new Exception());
        }
    }

    public void checkVisibleAndLoad() {
        totalItemCount = gridLayoutManager.getItemCount();
        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

            if (onLoadMoreListener != null) {
                onLoadMoreListener.onLoadMore();
            }

            loading = true;
        }
    }

    public void addMovies (Collection<MovieObject> movieObjectList) {
        mDataset.addAll(movieObjectList);
        this.notifyDataSetChanged();
        loading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForListItem = R.layout.movie_list_item;
        int layoutIdForProgressItem = R.layout.support_simple_spinner_dropdown_item;
        boolean shouldAttachToParentImmediately = false;

        RecyclerView.ViewHolder viewHolder;

        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            viewHolder = new MovieViewHolder(view);
        } else {
            View view = inflater.inflate(layoutIdForProgressItem, viewGroup, shouldAttachToParentImmediately);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Log.d(TAG, "#" + position);
        //GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();

        if (holder instanceof MovieViewHolder) {
            MovieViewHolder cast = (MovieViewHolder) holder;

            cast.bind(position);
        } else if (holder instanceof ProgressViewHolder) {
            ProgressViewHolder cast = (ProgressViewHolder) holder;
            cast.bind();
        }

    }


    @Override
    public int getItemCount() {
        return (mDataset.size());
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        onLoadMoreListener.onLoadMore();
        this.checkVisibleAndLoad();
    }

    public List<MovieObject> swapDataset(List<MovieObject> cachedDataset) {
        List<MovieObject> temp = mDataset;
        mDataset = cachedDataset;
        notifyDataSetChanged();
        loading = false;
        return temp;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        ImageView movieThumbnail;
        private int position = -1;

        public MovieViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.rv_movie_thumbnail);
        }

        void bind (int position) {
            this.position = position;
            // Get display size
            DisplayMetrics display = ((Activity) appContext).getResources().getDisplayMetrics();
            int width = display.widthPixels;
            int imageWidth = width / 2;
            int imageHeight = (int) Math.round(display.heightPixels/2.5);

            // Set layout parameter of the thumbnail to make sure it is fullbleed
            Log.i(TAG, "bind: parent is" + movieThumbnail.getParent().toString());

            //movieThumbnail.setLayoutParams();
            movieThumbnail.setAdjustViewBounds(true);
            movieThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            movieThumbnail.setPadding(0, 0, 0, 0);

            MovieObject movie = mDataset.get(position);
            if (movie != null) {
                Picasso.with(appContext)
                        .load(movie.getThumbnailUrl())
                        .resize(imageWidth,imageHeight)
                        .centerCrop()
                        .into(movieThumbnail);
            }
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(appContext, Integer.toString(this.position), Toast.LENGTH_SHORT).show();
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ContentLoadingProgressBar spinner;

        public ProgressViewHolder(View itemView) {
            super(itemView);

            spinner = (ContentLoadingProgressBar) itemView.findViewById(R.id.progressBar);
        }

        void bind () {
            spinner.show();
        }
    }
}
