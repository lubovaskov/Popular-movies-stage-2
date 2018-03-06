package com.udacity.popularmoviesstage2.utils;

import com.udacity.popularmoviesstage2.MovieInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class JsonUtils {

    private static final String THEMOVIEDB_BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185";
    private static final String THEMOVIEDB_JSON_FIELD_RESULTS = "results";
    private static final String THEMOVIEDB_JSON_FIELD_TITLE = "title";
    private static final String THEMOVIEDB_JSON_FIELD_RELEASE_DATE = "release_date";
    private static final String THEMOVIEDB_JSON_FIELD_POSTER_PATH = "poster_path";
    private static final String THEMOVIEDB_JSON_FIELD_VOTE_AVERAGE = "vote_average";
    private static final String THEMOVIEDB_JSON_FIELD_OVERVIEW = "overview";

    public static ArrayList<MovieInfo> getMoviesFromJson(String moviesJson) throws JSONException {

        ArrayList<MovieInfo> result = new ArrayList<>();

        JSONObject moviesJSONObect = new JSONObject(moviesJson);
        JSONArray resultsJSONArray = moviesJSONObect.getJSONArray(THEMOVIEDB_JSON_FIELD_RESULTS);
        for (int i = 0; i < resultsJSONArray.length(); i++) {
            JSONObject resultJSON = resultsJSONArray.getJSONObject(i);

            String title = resultJSON.getString(THEMOVIEDB_JSON_FIELD_TITLE);
            String releaseDate = resultJSON.getString(THEMOVIEDB_JSON_FIELD_RELEASE_DATE).substring(0, 4);
            String posterURL = THEMOVIEDB_BASE_URL_POSTER + resultJSON.getString(THEMOVIEDB_JSON_FIELD_POSTER_PATH);
            Double voteAverage = resultJSON.getDouble(THEMOVIEDB_JSON_FIELD_VOTE_AVERAGE);
            String overview = resultJSON.getString(THEMOVIEDB_JSON_FIELD_OVERVIEW);

            result.add(new MovieInfo(title, releaseDate, posterURL, voteAverage, overview));
        }

        return result;
    }
}