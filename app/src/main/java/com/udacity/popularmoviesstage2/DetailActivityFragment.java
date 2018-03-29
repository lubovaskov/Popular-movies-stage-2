package com.udacity.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmoviesstage2.adapters.ReviewsAdapter;
import com.udacity.popularmoviesstage2.adapters.VideosAdapter;
import com.udacity.popularmoviesstage2.database.TheMovieDBResolver;
import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;
import com.udacity.popularmoviesstage2.valueobjects.ReviewInfo;
import com.udacity.popularmoviesstage2.valueobjects.VideoInfo;
import com.udacity.popularmoviesstage2.webapi.ListWrapper;
import com.udacity.popularmoviesstage2.webapi.TheMovieDBAPI;
import com.udacity.popularmoviesstage2.webapi.TheMovieDBAPIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityFragment extends Fragment
        implements VideosAdapter.VideosAdapterOnClickHandler {

    private static final String MOVIE_INFO_VOTE_AVERAGE_FORMAT = "%1$.1f/10";

    private static final String CURRENT_MOVIE_INFO_PARCELABLE_NAME = "currentMovieInfo";
    private static final String VIDEO_INFOS_PARCELABLE_NAME = "videoInfos";
    private static final String REVIEW_INFOS_PARCELABLE_NAME = "reviewInfos";

    private Unbinder unbinder;
    private DetailFragmentListener mCallback;

    private MovieInfo currentMovieInfo;
    private List<VideoInfo> videoInfos;
    private List<ReviewInfo> reviewInfos;

    private VideosAdapter rvVideosAdapter;
    private ReviewsAdapter rvReviewsAdapter;

    @BindView(R.id.textview_title)
    TextView tvTitle;
    @BindView(R.id.scollview_detail)
    NestedScrollView svDetail;
    @BindView(R.id.textview_releaseDate)
    TextView tvReleaseDate;
    @BindView(R.id.imageview_poster)
    ImageView ivPoster;
    @BindView(R.id.textview_voteAverage)
    TextView tvVoteAverage;
    @BindView(R.id.textview_overview)
    TextView tvOverview;
    @BindView(R.id.btn_favorite)
    Button btnFavorite;
    @BindView(R.id.recyclerview_videos)
    RecyclerView rvVideos;
    @BindView(R.id.recyclerview_reviews)
    RecyclerView rvReviews;

    public DetailActivityFragment() {
    }

    @NonNull
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        rvVideos.setLayoutManager(new LinearLayoutManager(rvVideos.getContext(), LinearLayoutManager.VERTICAL, false));
        rvVideosAdapter = new VideosAdapter(getActivity(), this);
        rvVideos.setAdapter(rvVideosAdapter);

        rvReviews.setLayoutManager(new LinearLayoutManager(rvReviews.getContext(), LinearLayoutManager.VERTICAL, false));
        rvReviewsAdapter = new ReviewsAdapter(getActivity());
        rvReviews.setAdapter(rvReviewsAdapter);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                currentMovieInfo = args.getParcelable(MovieInfo.MOVIE_INFO_PARCELABLE_NAME);
                if (currentMovieInfo != null) {
                    getDetailsFromAPI(currentMovieInfo.id);
                }
            }
        } else {
            restoreInstanceState(savedInstanceState);
            rvVideosAdapter.reloadData(videoInfos);
            rvReviewsAdapter.reloadData(reviewInfos);
        }

        if (currentMovieInfo != null) {
            displayMovieInfo(currentMovieInfo);
        }

        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_MOVIE_INFO_PARCELABLE_NAME, currentMovieInfo);
        outState.putParcelableArrayList(VIDEO_INFOS_PARCELABLE_NAME, new ArrayList<>(videoInfos));
        outState.putParcelableArrayList(REVIEW_INFOS_PARCELABLE_NAME, new ArrayList<>(reviewInfos));
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        currentMovieInfo = savedInstanceState.getParcelable(CURRENT_MOVIE_INFO_PARCELABLE_NAME);
        videoInfos = savedInstanceState.getParcelableArrayList(VIDEO_INFOS_PARCELABLE_NAME);
        reviewInfos = savedInstanceState.getParcelableArrayList(REVIEW_INFOS_PARCELABLE_NAME);
    }

    private void displayMovieInfo(@NonNull MovieInfo movieInfo) {
        tvTitle.setText(movieInfo.title);
        tvReleaseDate.setText(movieInfo.releaseDate.substring(0, 4));
        Picasso.with(getActivity())
                .load(TheMovieDBAPI.BASE_POSTER_URL + movieInfo.posterURL)
                .into(ivPoster);
        tvVoteAverage.setText(String.format(Locale.getDefault(), MOVIE_INFO_VOTE_AVERAGE_FORMAT, movieInfo.voteAverage));
        tvOverview.setText(movieInfo.overview);
        setCurrentMovieFavorite(TheMovieDBResolver.isMovieFavorite(getActivity(), movieInfo.id));
    }

    private void getDetailsFromAPI(int movieId) {
        TheMovieDBAPIClient.getInstance().getMovieVideos(movieId, BuildConfig.THE_MOVIE_DB_API_KEY).enqueue(videosCallback);
        TheMovieDBAPIClient.getInstance().getMovieReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY).enqueue(reviewsCallback);
    }

    @OnClick(R.id.btn_favorite)
    void onBtnFavoriteClick() {
        if (currentMovieInfo != null) {
            setCurrentMovieFavorite(TheMovieDBResolver.toggleFavoriteMovie(getActivity(), currentMovieInfo));
        }
    }

    private void setCurrentMovieFavorite(Boolean isFavorite) {
        currentMovieInfo.isFavorite = isFavorite;
        if (isFavorite) {
            btnFavorite.setText(getResources().getText(R.string.favorite_button_text_unmark));
        } else {
            btnFavorite.setText(getResources().getText(R.string.favorite_button_text_mark));
        }
    }

    Callback<ListWrapper<VideoInfo>> videosCallback = new Callback<ListWrapper<VideoInfo>>() {
        @Override
        public void onResponse(@NonNull Call<ListWrapper<VideoInfo>> call, @NonNull Response<ListWrapper<VideoInfo>> response) {
            if (response.isSuccessful()) {
                ListWrapper<VideoInfo> videoInfosWrapper = response.body();

                if (videoInfosWrapper != null) {
                    videoInfos = videoInfosWrapper.results;
                    rvVideosAdapter.reloadData(videoInfos);
                }
            } else {
                rvVideosAdapter.reloadData(null);
                if (mCallback != null) {
                    mCallback.onError(getResources().getString(R.string.network_error_text));
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<ListWrapper<VideoInfo>> call, @NonNull Throwable t) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.network_error_text));
            }
        }
    };

    Callback<ListWrapper<ReviewInfo>> reviewsCallback = new Callback<ListWrapper<ReviewInfo>>() {
        @Override
        public void onResponse(@NonNull Call<ListWrapper<ReviewInfo>> call, @NonNull Response<ListWrapper<ReviewInfo>> response) {
            if (response.isSuccessful()) {
                ListWrapper<ReviewInfo> reviewInfosWrapper = response.body();

                if (reviewInfosWrapper != null) {
                    reviewInfos = reviewInfosWrapper.results;
                    rvReviewsAdapter.reloadData(reviewInfos);
                }
            } else {
                rvReviewsAdapter.reloadData(null);
                if (mCallback != null) {
                    mCallback.onError(getResources().getString(R.string.network_error_text));
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<ListWrapper<ReviewInfo>> call, @NonNull Throwable t) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.network_error_text));
            }
        }
    };

    @Override
    public void onVideoClick(String videoKey) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
        videoIntent.putExtra("VIDEO_ID", videoKey);
        startActivity(videoIntent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //save a reference to the current activity
        mCallback = (DetailFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //clear the reference to the current activity
        mCallback = null;
    }

    public interface DetailFragmentListener {
        void onError(String errorText);
    }
}
