package com.udacity.popularmoviesstage2.database;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.udacity.popularmoviesstage2.R;

public class TheMovieDBProvider extends ContentProvider {

    public static final int CODE_FAVORITE = 10;
    public static final int CODE_FAVORITE_DETAIL = 11;

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private TheMovieDBHelper openHelper;
    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<>();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TheMovieDBContract.CONTENT_AUTHORITY;

        // content://com.udacity.popularmoviesstage2/favorite
        matcher.addURI(authority, TheMovieDBContract.THEMOVIEDB_URL_SEGMENT_FAVORITE, CODE_FAVORITE);
        // content://com.udacity.popularmoviesstage2/favorite/{movie_id}
        matcher.addURI(authority, TheMovieDBContract.THEMOVIEDB_URL_SEGMENT_FAVORITE + "/#", CODE_FAVORITE_DETAIL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new TheMovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db;

        if (uriMatcher.match(uri) == CODE_FAVORITE_DETAIL) {
            db = openHelper.getWritableDatabase();
            long id = db.insertWithOnConflict(TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        } else {
            throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_FAVORITE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_FAVORITE_DETAIL: {
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};

                cursor = openHelper.getReadableDatabase().query(
                        TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_FAVORITE: {
                cursor = openHelper.getReadableDatabase().query(
                        TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch (uriMatcher.match(uri)) {
            case CODE_FAVORITE_DETAIL:
                rowsDeleted = openHelper.getWritableDatabase().delete(
                        TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME,
                        TheMovieDBContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = " + uri.getLastPathSegment(),
                        selectionArgs);
                break;
            case CODE_FAVORITE:
                if (null == selection) selection = "1";
                rowsDeleted = openHelper.getWritableDatabase().delete(
                        TheMovieDBContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri_error_text) + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException(getContext().getString(R.string.gettype_not_implemented_error_text));
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException(getContext().getString(R.string.update_not_implemented_error_text));
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }
        throw new SQLException(getContext().getString(R.string.insert_error_text) + uri);
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        openHelper.close();
        super.shutdown();
    }
}
