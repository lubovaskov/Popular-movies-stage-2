package com.udacity.popularmoviesstage2.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class TheMovieDBContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.popularmoviesstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String THEMOVIEDB_URL_SEGMENT_FAVORITE = "favorite";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(THEMOVIEDB_URL_SEGMENT_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}
