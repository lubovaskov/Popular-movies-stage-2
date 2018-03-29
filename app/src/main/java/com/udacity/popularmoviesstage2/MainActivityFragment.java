package com.udacity.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.popularmoviesstage2.adapters.PostersAdapter;
import com.udacity.popularmoviesstage2.database.TheMovieDBContract;
import com.udacity.popularmoviesstage2.database.TheMovieDBResolver;
import com.udacity.popularmoviesstage2.utils.NetworkUtils;
import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;
import com.udacity.popularmoviesstage2.webapi.ListWrapper;
import com.udacity.popularmoviesstage2.webapi.TheMovieDBAPI;
import com.udacity.popularmoviesstage2.webapi.TheMovieDBAPIClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityFragment extends Fragment
        implements
        LoaderManager.LoaderCallbacks<Cursor>,
        Callback<ListWrapper<MovieInfo>>,
        PostersAdapter.PostersAdapterOnClickHandler{

    private static final int FAVORITE_LOADER_ID = 20;
    private static int POSTER_COLUMN_COUNT = 2;
    private static final String MOVIE_INFOS_PARCELABLE_NAME = "movieInfos";

    private Unbinder unbinder;
    private PostersAdapter rvPostersAdapter;

    @BindView(R.id.recyclerview_posters)
    RecyclerView rvPosters;

    private List<MovieInfo> movieInfos;
    private MainFragmentListener mCallback;
    private String currentSortOrder = TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_POPULAR; //default sorting is "popular"

    public MainActivityFragment() {
    }

    @NonNull
    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        View fragmentView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        rvPosters.setLayoutManager(new GridLayoutManager(rvPosters.getContext(), POSTER_COLUMN_COUNT));
        rvPostersAdapter = new PostersAdapter(getActivity(), this);
        rvPosters.setAdapter(rvPostersAdapter);

        if (savedInstanceState == null) {
            getMovies(currentSortOrder);
        } else {
            restoreInstanceState(savedInstanceState);
            rvPostersAdapter.reloadData(movieInfos);
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
        outState.putParcelableArrayList(MOVIE_INFOS_PARCELABLE_NAME, new ArrayList<>(movieInfos));
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        movieInfos = savedInstanceState.getParcelableArrayList(MOVIE_INFOS_PARCELABLE_NAME);
    }

    @Override
    public void onPosterClick(int position) {
        //start the detail activity, passing to it the MovieInfo object at the current position
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        if (movieInfos != null) {
            intent.putExtra(MovieInfo.MOVIE_INFO_PARCELABLE_NAME, movieInfos.get(position));
        }
        startActivity(intent);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_most_popular:
                //load popular movies
                getMovies(TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_POPULAR);
                return true;
            case R.id.action_sort_top_rated:
                //load top-rated movies
                getMovies(TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_TOP_RATED);
                return true;
            case R.id.action_show_favorite:
                //load favorite movies
                getMovies(TheMovieDBContract.THEMOVIEDB_URL_SEGMENT_FAVORITE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMovies(String sortOrder) {
        currentSortOrder = sortOrder;

        switch (sortOrder) {
            case TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_POPULAR:
                if (!checkOnline()) return;
                TheMovieDBAPIClient.getInstance().getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY).enqueue(this);
                break;
            case TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_TOP_RATED:
                if (!checkOnline()) return;
                TheMovieDBAPIClient.getInstance().getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_KEY).enqueue(this);
                break;
            case TheMovieDBContract.THEMOVIEDB_URL_SEGMENT_FAVORITE:
                getActivity().getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
                break;
        }
    }

    @NonNull
    private Boolean checkOnline() {
        //check if network connection is available
        if (!NetworkUtils.isOnline(getActivity())) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.not_online_error_text));
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onResponse(@NonNull Call<ListWrapper<MovieInfo>> call, @NonNull Response<ListWrapper<MovieInfo>> response) {
        if (response.isSuccessful()) {
            ListWrapper<MovieInfo> movieInfosWrapper = response.body();

            if (movieInfosWrapper != null) {
                switch (currentSortOrder) {
                    case TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_POPULAR:
                        getActivity().setTitle(getResources().getString(R.string.title_activity_main_popular));
                        break;
                    case TheMovieDBAPI.THEMOVIEDB_URL_SEGMENT_TOP_RATED:
                        getActivity().setTitle(getResources().getString(R.string.title_activity_main_top_rated));
                        break;
                }

                movieInfos = movieInfosWrapper.results;
                rvPostersAdapter.reloadData(movieInfos);
            }
        } else {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.network_error_text));
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<ListWrapper<MovieInfo>> call, @NonNull Throwable t) {
        if (mCallback != null) {
            mCallback.onError(getResources().getString(R.string.network_error_text));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case FAVORITE_LOADER_ID:
                return TheMovieDBResolver.getFavoriteMoviesLoader(getActivity());
            default:
                throw new RuntimeException(getResources().getString(R.string.loader_not_implemented_error_text) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getActivity().setTitle(getResources().getString(R.string.title_activity_main_favorite));
        TheMovieDBResolver.loadFavoriteMoviesInList(data, movieInfos);
        rvPostersAdapter.reloadData(movieInfos);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieInfos.clear();
        rvPostersAdapter.reloadData(movieInfos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //save a reference to the current activity
        mCallback = (MainFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //clear the reference to the current activity
        mCallback = null;
    }

    public interface MainFragmentListener {
        void onError(String errorText);
    }
}
