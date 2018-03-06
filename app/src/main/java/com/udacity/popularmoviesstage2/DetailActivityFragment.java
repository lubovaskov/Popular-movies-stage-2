package com.udacity.popularmoviesstage2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailActivityFragment extends Fragment {

    private static final String MOVIE_INFO_VOTE_AVERAGE_FORMAT = "%1$.1f/10";

    private MovieInfo movieInfo;

    public DetailActivityFragment() {

    }

    public static DetailActivityFragment newInstance(MovieInfo movieInfo) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable(MovieInfo.MOVIE_INFO_PARCELABLE_NAME, movieInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            movieInfo = args.getParcelable(MovieInfo.MOVIE_INFO_PARCELABLE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_detail_activity, container, false);

        if (movieInfo != null) {
            TextView tvTitle = fragmentView.findViewById(R.id.textview_title);
            TextView tvReleaseDate = fragmentView.findViewById(R.id.textview_releaseDate);
            ImageView ivPoster = fragmentView.findViewById(R.id.imageview_poster);
            TextView tvVoteAverage = fragmentView.findViewById(R.id.textview_voteAverage);
            TextView tvOverview = fragmentView.findViewById(R.id.textview_overview);

            tvTitle.setText(movieInfo.title);
            tvReleaseDate.setText(movieInfo.releaseDate);
            Picasso.with(getActivity())
                    .load(movieInfo.posterURL)
                    .into(ivPoster);
            tvVoteAverage.setText(String.format(Locale.getDefault(), MOVIE_INFO_VOTE_AVERAGE_FORMAT, movieInfo.voteAverage));
            tvOverview.setText(movieInfo.overview);
        }

        return fragmentView;
    }
}
