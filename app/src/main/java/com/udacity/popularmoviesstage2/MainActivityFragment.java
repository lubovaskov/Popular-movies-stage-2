package com.udacity.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.udacity.popularmoviesstage2.utils.JsonUtils;
import com.udacity.popularmoviesstage2.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<String> {

    private static final int MOVIES_LOADER_ID = 50;
    private static final String MOVIES_LOADER_URL_PARAM_NAME = "url";

    private ArrayList<MovieInfo> movieInfos;
    private MainFragmentListener mCallback;
    private GridView posterGridView;
    private String currentSortOrder = NetworkUtils.THEMOVIEDB_URL_SEGMENT_POPULAR; //default sorting is "popular"

    public MainActivityFragment() {

    }

    public static MainActivityFragment newInstance(/* any params here */) {
        MainActivityFragment fragment = new MainActivityFragment();
        //send params to the new fragment
        //Bundle args = new Bundle();
        //args.put...(..., ...);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //do not destroy the main fragment when configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        View fragmentView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        posterGridView = fragmentView.findViewById(R.id.gridview_poster);
        posterGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        launchDetailActivity(position);
                    }
                }
        );

        getMovies(currentSortOrder);

        return fragmentView;
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
                getMovies(NetworkUtils.THEMOVIEDB_URL_SEGMENT_POPULAR);
                return true;
            case R.id.action_sort_top_rated:
                //load top-rated movies
                getMovies(NetworkUtils.THEMOVIEDB_URL_SEGMENT_TOP_RATED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMovies(String sortOrder) {
        //check if network connection is available
        if (!NetworkUtils.isOnline(getActivity())) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.not_online_error_text));
            }
            return;
        }

        //build URL to request data
        URL url;
        try {
            currentSortOrder = sortOrder;
            url = NetworkUtils.getURL(sortOrder, getResources().getString(R.string.themoviedb_api_key));
        } catch (MalformedURLException e) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.url_error_text));
            }
            return;
        }

        //start loader to load data from www.themoviedb.org
        Bundle loaderBundle = new Bundle();
        loaderBundle.putSerializable(MOVIES_LOADER_URL_PARAM_NAME, url);

        LoaderManager loaderManager = getLoaderManager();
        Loader<String> moviesLoader = loaderManager.getLoader(MOVIES_LOADER_ID);
        if (moviesLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER_ID, loaderBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER_ID, loaderBundle, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MoviesInfoLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        //check if any data is returned
        if (data == null) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.network_error_text));
            }
            return;
        }

        //parse JSON data to MovieInfo objects
        try {
            movieInfos = JsonUtils.getMoviesFromJson(data);
        } catch (JSONException e) {
            if (mCallback != null) {
                mCallback.onError(getResources().getString(R.string.json_error_text));
            }
            return;
        }

        if (movieInfos != null) {
            //set activity title to reflect current order
            switch (currentSortOrder) {
                case NetworkUtils.THEMOVIEDB_URL_SEGMENT_POPULAR:
                    getActivity().setTitle(getResources().getString(R.string.title_activity_main_popular));
                    break;
                case NetworkUtils.THEMOVIEDB_URL_SEGMENT_TOP_RATED:
                    getActivity().setTitle(getResources().getString(R.string.title_activity_main_top_rated));
                    break;
            }
            //load movie posters into the gridview
            posterGridView.setAdapter(new MovieInfoAdapter(getActivity(), movieInfos));
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //not used but required
    }

    public static class MoviesInfoLoader extends AsyncTaskLoader<String> {

        Bundle args;

        public MoviesInfoLoader(Context context, Bundle args) {
            super(context);
            this.args = args;
        }

        @Override
        protected void onStartLoading() {
            if (args == null) {
                return;
            }
            forceLoad();
        }

        @Override
        public String loadInBackground() {
            URL url = (URL) args.getSerializable(MOVIES_LOADER_URL_PARAM_NAME);

            if (url == null) {
                return null;
            }

            //execute query to www.themoviesdb.org and return JSON result
            try {
                return NetworkUtils.getResultFromUrl(url);
            } catch (IOException e) {
                return null;
            }
        }
    }

    private void launchDetailActivity(int position) {
        //start the detail activity, passing to it the MovieInfo object at the current position
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(MovieInfo.MOVIE_INFO_PARCELABLE_NAME, movieInfos.get(position));
        startActivity(intent);
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
