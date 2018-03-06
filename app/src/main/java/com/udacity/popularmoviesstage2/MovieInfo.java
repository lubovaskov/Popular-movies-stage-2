package com.udacity.popularmoviesstage2;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable {

    public static final String MOVIE_INFO_PARCELABLE_NAME = "MovieInfo";

    public String title;
    public String releaseDate;
    public String posterURL;
    public Double voteAverage;
    public String overview;

    public MovieInfo(String title, String releaseDate, String posterURL, Double voteAverage, String overview) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterURL = posterURL;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    private MovieInfo(Parcel parcel){
        title = parcel.readString();
        releaseDate = parcel.readString();
        posterURL = parcel.readString();
        voteAverage = parcel.readDouble();
        overview = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterURL);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel parcel) {
            return new MovieInfo(parcel);
        }

        @Override
        public MovieInfo[] newArray(int i) {
            return new MovieInfo[i];
        }
    };
}
