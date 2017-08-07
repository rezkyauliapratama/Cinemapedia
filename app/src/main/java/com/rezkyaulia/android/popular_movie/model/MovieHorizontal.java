package com.rezkyaulia.android.popular_movie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rezky Aulia Pratama on 8/6/2017.
 */

public class MovieHorizontal implements Parcelable {
    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
    }

    public MovieHorizontal() {
    }

    protected MovieHorizontal(Parcel in) {
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<MovieHorizontal> CREATOR = new Parcelable.Creator<MovieHorizontal>() {
        @Override
        public MovieHorizontal createFromParcel(Parcel source) {
            return new MovieHorizontal(source);
        }

        @Override
        public MovieHorizontal[] newArray(int size) {
            return new MovieHorizontal[size];
        }
    };
}
