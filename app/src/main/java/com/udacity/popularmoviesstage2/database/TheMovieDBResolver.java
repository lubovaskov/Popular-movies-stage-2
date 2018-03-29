package com.udacity.popularmoviesstage2.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;

import java.util.List;

public class TheMovieDBResolver {

    private static final String[] FAVORITE_MOVIES_PROJECTION = {
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_TITLE,
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_POSTER,
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
            TheMovieDBContract.FavoriteMovieEntry.COLUMN_OVERVIEW
    };

    @NonNull
    public static Loader<Cursor> getFavoriteMoviesLoader(@NonNull Context context) {
        Uri uri = TheMovieDBContract.FavoriteMovieEntry.CONTENT_URI;
        String sortOrder = TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " ASC";
        return new CursorLoader(context, uri, FAVORITE_MOVIES_PROJECTION, null, null, sortOrder);
    }

    public static void loadFavoriteMoviesInList(@NonNull Cursor cursor, @NonNull List<MovieInfo> movieInfos) {
        movieInfos.clear();

        if (cursor.getCount() != 0) {
            int columnIndexMovieId = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID);
            int columnIndexTitle = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_TITLE);
            int columnIndexReleaseDate = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE);
            int columnIndexPoster = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_POSTER);
            int columnIndexVoteAverage = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE);
            int columnIndexOverview = cursor.getColumnIndex(TheMovieDBContract.FavoriteMovieEntry.COLUMN_OVERVIEW);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                movieInfos.add(new MovieInfo(cursor.getInt(columnIndexMovieId),
                        cursor.getString(columnIndexTitle),
                        cursor.getString(columnIndexReleaseDate),
                        cursor.getString(columnIndexPoster),
                        cursor.getDouble(columnIndexVoteAverage),
                        cursor.getString(columnIndexOverview),
                        true
                ));
                cursor.moveToNext();
            }
        }
    }

    @NonNull
    public static Boolean isMovieFavorite(@NonNull Context context, int movieId) {
        //check if the movie is marked as favorite
        Boolean res;
        ContentResolver cr = context.getContentResolver();

        Uri uri = TheMovieDBContract.FavoriteMovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(Integer.toString(movieId))
                .build();

        String[] projection = {TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID};

        Cursor cursor = cr.query(uri, projection, null, null, null);
        res = ((cursor != null) && (cursor.getCount() > 0));
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }

    @NonNull
    public static Boolean toggleFavoriteMovie(@NonNull Context context, @NonNull MovieInfo movieInfo) {
        ContentResolver cr = context.getContentResolver();

        Uri uri = TheMovieDBContract.FavoriteMovieEntry.CONTENT_URI
                .buildUpon()
                .appendPath(Integer.toString(movieInfo.id))
                .build();

        if (movieInfo.isFavorite) {
            cr.delete(uri, null, null);
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movieInfo.id);
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_TITLE, movieInfo.title);
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_POSTER, movieInfo.posterURL);
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movieInfo.releaseDate);
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movieInfo.voteAverage);
            values.put(TheMovieDBContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movieInfo.overview);
            cr.insert(uri, values);
            return true;
        }
    }
}
