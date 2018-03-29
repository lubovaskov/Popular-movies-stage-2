package com.udacity.popularmoviesstage2.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ReviewInfo implements Parcelable {

    @SerializedName("author")
    public String author;
    @SerializedName("content")
    public String content;
    @SerializedName("url")
    public String url;

    public ReviewInfo(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    private ReviewInfo(Parcel parcel) {
        author = parcel.readString();
        content = parcel.readString();
        url = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<ReviewInfo> CREATOR = new Parcelable.Creator<ReviewInfo>() {
        @Override
        public ReviewInfo createFromParcel(Parcel parcel) {
            return new ReviewInfo(parcel);
        }

        @Override
        public ReviewInfo[] newArray(int i) {
            return new ReviewInfo[i];
        }
    };

}
