package com.udacity.popularmoviesstage2.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MovieInfo implements Parcelable {

    public static final String MOVIE_INFO_PARCELABLE_NAME = "MovieInfo";

    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("poster_path")
    public String posterURL;
    @SerializedName("vote_average")
    public Double voteAverage;
    @SerializedName("overview")
    public String overview;
    @SerializedName("isFavorite")
    public Boolean isFavorite;

    public MovieInfo(int id, String title, String releaseDate, String posterURL, Double voteAverage, String overview, Boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterURL = posterURL;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.isFavorite = isFavorite;
    }

    private MovieInfo(Parcel parcel) {
        id = parcel.readInt();
        title = parcel.readString();
        releaseDate = parcel.readString();
        posterURL = parcel.readString();
        voteAverage = parcel.readDouble();
        overview = parcel.readString();
        isFavorite = parcel.readByte() != 0;
    }

    public Boolean getIsFavorite() {
        if (this.isFavorite == null) {
            return false;
        } else {
            return this.isFavorite;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(releaseDate);
        parcel.writeString(posterURL);
        parcel.writeDouble(voteAverage);
        parcel.writeString(overview);
        parcel.writeByte((byte)(getIsFavorite() ? 1 : 0));
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
