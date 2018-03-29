package com.udacity.popularmoviesstage2.webapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheMovieDBAPIClient {

    private static TheMovieDBAPI mInstance;

    private TheMovieDBAPIClient(){}

    public static TheMovieDBAPI getInstance(){
        if (mInstance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TheMovieDBAPI.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mInstance = retrofit.create(TheMovieDBAPI.class);
        }

        return mInstance;
    }
}
