package com.udacity.popularmoviesstage2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    public static final String THEMOVIEDB_URL_SEGMENT_POPULAR = "popular";
    public static final String THEMOVIEDB_URL_SEGMENT_TOP_RATED = "top_rated";
    private static final String THEMOVIEDB_PARAM_API_KEY = "api_key";

    public static URL getURL(String urlSegment, String apiKey) throws MalformedURLException {
        Uri uri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(urlSegment)
                .appendQueryParameter(THEMOVIEDB_PARAM_API_KEY, apiKey)
                .build();

        return new URL(uri.toString());
    }

    public static String getResultFromUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo netInfo = connManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        else {
            return false;
        }
    }
}
