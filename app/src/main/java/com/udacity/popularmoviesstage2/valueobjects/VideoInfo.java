package com.udacity.popularmoviesstage2.valueobjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VideoInfo implements Parcelable {

    @SerializedName("key")
    public String key;
    @SerializedName("name")
    public String name;

    public VideoInfo(String key, String name) {
        this.key = key;
        this.name = name;
    }

    private VideoInfo(Parcel parcel) {
        key = parcel.readString();
        name = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(name);
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel parcel) {
            return new VideoInfo(parcel);
        }

        @Override
        public VideoInfo[] newArray(int i) {
            return new VideoInfo[i];
        }
    };
}
