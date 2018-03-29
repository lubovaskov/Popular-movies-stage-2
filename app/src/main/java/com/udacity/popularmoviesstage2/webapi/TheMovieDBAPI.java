package com.udacity.popularmoviesstage2.webapi;

import com.udacity.popularmoviesstage2.valueobjects.MovieInfo;
import com.udacity.popularmoviesstage2.valueobjects.ReviewInfo;
import com.udacity.popularmoviesstage2.valueobjects.VideoInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDBAPI {
    String BASE_URL = "http://api.themoviedb.org/3/";
    String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185";

    String THEMOVIEDB_PARAM_API_KEY = "api_key";
    String THEMOVIEDB_URL_SEGMENT_POPULAR = "popular";
    String THEMOVIEDB_URL_SEGMENT_TOP_RATED = "top_rated";

    @GET("movie/popular?language=en-US&page=1")
    Call<ListWrapper<MovieInfo>> getPopularMovies(@Query(THEMOVIEDB_PARAM_API_KEY) String apiKey);

    @GET("movie/top_rated?language=en-US&page=1")
    Call<ListWrapper<MovieInfo>> getTopRatedMovies(@Query(THEMOVIEDB_PARAM_API_KEY) String apiKey);

    @GET("movie/{movie_id}?language=en-US")
    Call<MovieInfo> getMovieDetails(@Path("movie_id") int movieId, @Query(THEMOVIEDB_PARAM_API_KEY) String apiKey);

    @GET("movie/{movie_id}/videos?language=en-US")
    Call<ListWrapper<VideoInfo>> getMovieVideos(@Path("movie_id") int movieId, @Query(THEMOVIEDB_PARAM_API_KEY) String apiKey);

    @GET("movie/{movie_id}/reviews?language=en-US")
    Call<ListWrapper<ReviewInfo>> getMovieReviews(@Path("movie_id") int movieId, @Query(THEMOVIEDB_PARAM_API_KEY) String apiKey);
}
