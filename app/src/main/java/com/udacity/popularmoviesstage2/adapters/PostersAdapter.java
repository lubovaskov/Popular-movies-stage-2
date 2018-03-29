package com.udacity.popularmoviesstage2.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstage2.R;
import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;
import com.udacity.popularmoviesstage2.webapi.TheMovieDBAPI;

import java.util.List;

public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.PostersAdapterViewHolder>  {

    private final Context mContext;
    private List<MovieInfo> posters;
    private int posterWidthPx;
    private int posterHeightPx;

    final private PostersAdapterOnClickHandler mClickHandler;

    public interface PostersAdapterOnClickHandler {
        void onPosterClick(int position);
    }

    public PostersAdapter(@NonNull Context context, PostersAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

        Resources resources = context.getResources();
        posterWidthPx = resources.getDisplayMetrics().widthPixels / 2;
        posterHeightPx = posterWidthPx * resources.getInteger(R.integer.large_poster_height_px) / resources.getInteger(R.integer.large_poster_width_px);
    }

    @Override
    public PostersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_posters, viewGroup, false);
        view.setFocusable(true);

        return new PostersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostersAdapterViewHolder postersAdapterViewHolder, int position) {
        MovieInfo movieInfo = posters.get(position);
        Picasso.with(mContext)
                .load(TheMovieDBAPI.BASE_POSTER_URL + movieInfo.posterURL)
                .into(postersAdapterViewHolder.ivPoster);
    }

    @Override
    public int getItemCount() {
        if (null == posters) return 0;
        return posters.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REVIEWS;
    }*/

    public void reloadData(List<MovieInfo> newData) {
        posters = newData;
        notifyDataSetChanged();
    }

    class PostersAdapterViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        ImageView ivPoster;

        PostersAdapterViewHolder(View view) {
            super(view);
            ivPoster = view.findViewById(R.id.imageview_main_poster);
            view.setLayoutParams(new LinearLayout.LayoutParams(posterWidthPx, posterHeightPx));
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onPosterClick(adapterPosition);
        }
    }
}
