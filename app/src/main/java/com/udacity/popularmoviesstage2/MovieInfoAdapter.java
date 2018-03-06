package com.udacity.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieInfoAdapter extends ArrayAdapter<MovieInfo> {

    private Context context;
    private LayoutInflater inflater;
    private int posterWidthPx;
    private int posterHeightPx;

    public MovieInfoAdapter(Activity context, List<MovieInfo> movieInfos) {
        super(context, R.layout.grid_item_poster, movieInfos);
        this.context = context;
        inflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
        posterWidthPx = resources.getDisplayMetrics().widthPixels / 2;
        posterHeightPx = posterWidthPx * resources.getInteger(R.integer.large_poster_height_px) / resources.getInteger(R.integer.large_poster_width_px);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_poster, parent, false);
            convertView.setLayoutParams(new GridView.LayoutParams(posterWidthPx, posterHeightPx));
        }

        MovieInfo movieInfo = getItem(position);
        if (movieInfo != null) {
            ImageView ivPoster = convertView.findViewById(R.id.image_view_poster);
            Picasso.with(context)
                    .load(movieInfo.posterURL)
                    .into(ivPoster);
        }

        return convertView;
    }
}
