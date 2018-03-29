package com.udacity.popularmoviesstage2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.popularmoviesstage2.database.TheMovieDBContract.FavoriteMovieEntry;

public class TheMovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritemovies.db";

    private static final int DATABASE_VERSION = 1;

    public TheMovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                        FavoriteMovieEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteMovieEntry.COLUMN_MOVIE_ID     + " INTEGER NOT NULL, " +
                        FavoriteMovieEntry.COLUMN_TITLE        + " TEXT NOT NULL," +
                        FavoriteMovieEntry.COLUMN_POSTER       + " TEXT, " +
                        FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                        FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                        FavoriteMovieEntry.COLUMN_OVERVIEW     + " TEXT, " +
                        " UNIQUE (" + FavoriteMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
